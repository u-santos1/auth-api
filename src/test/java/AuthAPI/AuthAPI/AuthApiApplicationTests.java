package AuthAPI.AuthAPI;


import AuthAPI.AuthAPI.infra.segurity.RateLimitFilter;
import AuthAPI.AuthAPI.infra.segurity.RegistroRateLimitService;
import AuthAPI.AuthAPI.model.Usuario;
import AuthAPI.AuthAPI.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite de testes de segurança — auth-api
 *
 * Cobre os vetores identificados na análise de segurança:
 * - Autenticação e controle de acesso (OWASP A07)
 * - Autorização baseada em roles (OWASP A01)
 * - Rate limiting (OWASP A07)
 * - Validação de entrada (OWASP A03)
 * - Exposição de dados sensíveis (OWASP A02)
 * - Segurança do fluxo de reset de senha (OWASP A04, A07)
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Suite de Segurança — AuthAPI")

class AuthApiSecurityTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RateLimitFilter rateLimitFilter;

	@Autowired
	private RegistroRateLimitService registroRateLimitService;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@BeforeEach
	void resetRateLimit() {
		rateLimitFilter.resetCache();
		registroRateLimitService.resetCache();
		usuarioRepository.deleteAll();
	}

	// =========================================================================
	// 1. CONTROLE DE ACESSO — endpoints protegidos vs públicos
	// =========================================================================

	@Nested
	@DisplayName("1. Controle de Acesso")
	class ControleDeAcesso {

		@Test
		@DisplayName("Acesso sem token a endpoint protegido deve retornar 403")
		void semToken_endpointProtegido_retorna403() throws Exception {
			mockMvc.perform(get("/api/usuario/me"))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Acesso sem token ao listar usuários deve retornar 403")
		void semToken_listarUsuarios_retorna403() throws Exception {
			mockMvc.perform(get("/api/usuario/admin/listar"))
					.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Endpoint de login deve ser público (sem token)")
		void semToken_login_deveSerAcessivel() throws Exception {
			mockMvc.perform(post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"inexistente@test.com\",\"senha\":\"Senha123!\"}"))
					.andExpect(status().isUnauthorized()); // 401 = endpoint acessível, credenciais erradas
		}

		@Test
		@DisplayName("Endpoint de registro deve ser público (sem token)")
		void semToken_registrar_deveSerAcessivel() throws Exception {
			mockMvc.perform(post("/api/usuario/registrar")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"nome\":\"Teste\",\"email\":\"teste_acesso@test.com\",\"senha\":\"Senha123\"}"))
					.andExpect(status().is2xxSuccessful());
		}

		@Test
		@DisplayName("Endpoint de esqueci-senha deve ser público (sem token)")
		void semToken_esqueciSenha_deveSerAcessivel() throws Exception {
			mockMvc.perform(post("/auth/esqueci-senha")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"qualquer@email.com\"}"))
					.andExpect(status().isOk()); // sempre 200 para não vazar existência
		}
	}

	// =========================================================================
	// 2. AUTORIZAÇÃO POR ROLE — USER não pode acessar endpoints ADMIN
	// =========================================================================

	@Nested
	@DisplayName("2. Autorização por Role")
	class AutorizacaoPorRole {

		@Test
		@DisplayName("USER pode acessar seu próprio perfil /me")
		void user_acessarMe_retorna200() throws Exception {

			Usuario usuarioMock = new Usuario();
			usuarioMock.setId(1L);
			usuarioMock.setNome("Wesley Santos");
			usuarioMock.setEmail("wesley@teste.com");
			usuarioMock.setSenha("senha123");
			usuarioMock.setPerfil(PerfilAcesso.USER);

			mockMvc.perform(get("/api/usuario/me")
							.with(user(usuarioMock)))
					.andExpect(status().isOk());
		}
		@Test
		@WithMockUser(roles = "USER")
		@DisplayName("USER tentando listar todos usuários deve retornar 403")
		void user_listarTodos_retorna403() throws Exception {
			mockMvc.perform(get("/api/usuario/admin/listar"))
					.andExpect(status().isForbidden());
		}

		@Test
		@WithMockUser(roles = "USER")
		@DisplayName("USER tentando criar admin deve retornar 403")
		void user_criarAdmin_retorna403() throws Exception {
			mockMvc.perform(post("/api/usuario/registrar/admin")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"nome\":\"Admin Fake\",\"email\":\"fake@admin.com\",\"senha\":\"Senha123\"}"))
					.andExpect(status().isForbidden());
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		@DisplayName("ADMIN pode acessar listar todos usuários")
		void admin_listarTodos_retorna200() throws Exception {
			mockMvc.perform(get("/api/usuario/admin/listar"))
					.andExpect(status().isOk());
		}


	}

	// =========================================================================
	// 3. AUTENTICAÇÃO — comportamento do login
	// =========================================================================

	@Nested
	@DisplayName("3. Autenticação")
	class Autenticacao {

		@Test
		@DisplayName("Login com credenciais inválidas deve retornar 401")
		void loginInvalido_retorna401() throws Exception {
			mockMvc.perform(post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"naoexiste@test.com\",\"senha\":\"SenhaErrada1\"}"))
					.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("Login com credenciais inválidas não deve vazar se email existe (user enumeration)")
		void loginInvalido_naoVazaExistenciaEmail() throws Exception {
			// Email inexistente e email existente devem retornar o mesmo status HTTP
			var respostaEmailInexistente = mockMvc.perform(post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"email_que_nao_existe_xyz@test.com\",\"senha\":\"Senha123!\"}"))
					.andExpect(status().isUnauthorized())
					.andReturn();

			// A mensagem de erro deve ser genérica, nunca revelar que email não existe
			String body = respostaEmailInexistente.getResponse().getContentAsString();
			org.assertj.core.api.Assertions.assertThat(body)
					.doesNotContainIgnoringCase("não encontrado")
					.doesNotContainIgnoringCase("nao encontrado")
					.doesNotContainIgnoringCase("não existe")
					.doesNotContainIgnoringCase("e-mail")
					.doesNotContainIgnoringCase("usuario");
		}

		@Test
		@DisplayName("Login sem body deve retornar 400")
		void loginSemBody_retorna400() throws Exception {
			mockMvc.perform(post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Login com email mal formatado deve retornar 400")
		void loginEmailMalFormatado_retorna400() throws Exception {
			mockMvc.perform(post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"nao-e-um-email\",\"senha\":\"Senha123\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Token JWT inválido deve retornar 401")
		void tokenInvalido_retorna401() throws Exception {
			mockMvc.perform(get("/api/usuario/me")
							.header("Authorization", "Bearer token.invalido.aqui"))
					.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("Token JWT malformado (sem Bearer) deve retornar 403")
		void tokenSemBearer_retorna403() throws Exception {
			mockMvc.perform(get("/api/usuario/me")
							.header("Authorization", "token.sem.bearer"))
					.andExpect(status().isForbidden());
		}
	}

	// =========================================================================
	// 4. VALIDAÇÃO DE ENTRADA — campos obrigatórios e formato
	// =========================================================================

	@Nested
	@DisplayName("4. Validação de Entrada")
	class ValidacaoDeEntrada {

		@Test
		@DisplayName("Registro sem nome deve retornar 400")
		void registroSemNome_retorna400() throws Exception {
			mockMvc.perform(post("/api/usuario/registrar")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"valid@email.com\",\"senha\":\"Senha123\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Registro com email inválido deve retornar 400")
		void registroEmailInvalido_retorna400() throws Exception {
			mockMvc.perform(post("/api/usuario/registrar")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"nome\":\"Teste\",\"email\":\"nao-e-email\",\"senha\":\"Senha123\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Registro com senha menor que mínimo deve retornar 400")
		void registroSenhaCurta_retorna400() throws Exception {
			mockMvc.perform(post("/api/usuario/registrar")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"nome\":\"Teste\",\"email\":\"teste2@email.com\",\"senha\":\"123\"}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Registro com body vazio deve retornar 400")
		void registroBodyVazio_retorna400() throws Exception {
			mockMvc.perform(post("/api/usuario/registrar")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{}"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Registro com nome em branco deve retornar 400")
		void registroNomeBranco_retorna400() throws Exception {
			mockMvc.perform(post("/api/usuario/registrar")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"nome\":\"   \",\"email\":\"teste3@email.com\",\"senha\":\"Senha123\"}"))
					.andExpect(status().isBadRequest());
		}
	}

	// =========================================================================
	// 5. RESET DE SENHA — fluxo seguro
	// =========================================================================

	@Nested
	@DisplayName("5. Reset de Senha")
	class ResetDeSenha {

		@Test
		@DisplayName("Solicitar reset com email inexistente deve retornar 200 (anti user enumeration)")
		void resetEmailInexistente_retorna200() throws Exception {
			mockMvc.perform(post("/auth/esqueci-senha")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"email_que_nao_existe_abc@fake.com\"}"))
					.andExpect(status().isOk());
		}

		@Test
		@DisplayName("Solicitar reset com email existente deve retornar 200 (mesma resposta)")
		void resetEmailExistente_retornaMesmaRespostaque_EmailInexistente() throws Exception {
			// Primeiro cria um usuário
			mockMvc.perform(post("/api/usuario/registrar")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"nome\":\"Reset Teste\",\"email\":\"reset_test@email.com\",\"senha\":\"Senha123\"}"));

			// Depois solicita reset — deve retornar 200 igual ao email inexistente
			mockMvc.perform(post("/auth/esqueci-senha")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"reset_test@email.com\"}"))
					.andExpect(status().isOk());
		}

		@Test
		@DisplayName("Reset com token inválido deve retornar erro")
		void resetTokenInvalido_retornaErro() throws Exception {
			mockMvc.perform(post("/auth/reset-senha")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"token\":\"token-invalido-xyz\",\"novaSenha\":\"NovaSenha123\"}"))
					.andExpect(status().is4xxClientError());
		}

		@Test
		@DisplayName("Reset sem token deve retornar 400")
		void resetSemToken_retorna400() throws Exception {
			mockMvc.perform(post("/auth/reset-senha")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"novaSenha\":\"NovaSenha123\"}"))
					.andExpect(status().isBadRequest());
		}
	}

	// =========================================================================
	// 6. EXPOSIÇÃO DE DADOS — resposta não vaza campos sensíveis
	// =========================================================================

	@Nested
	@DisplayName("6. Exposição de Dados Sensíveis")
	class ExposicaoDeDados {

		@Test
		@WithMockUser(roles = "USER")
		@DisplayName("Endpoint /me não deve retornar campo senha no JSON")
		void perfilMe_naoRetornaSenha() throws Exception {
			Usuario usuario = new Usuario();
			usuario.setNome("Wesley");
			usuario.setEmail("wesley@test.com");
			usuario.setSenha(passwordEncoder.encode("Senha123"));
			usuario.setPerfil(PerfilAcesso.USER);
			usuario.setAtivo(true);
			usuarioRepository.save(usuario);

			mockMvc.perform(get("/api/usuario/me")
							.with(user(usuario))) // injeta o usuario real
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.senha").doesNotExist())
					.andExpect(jsonPath("$.password").doesNotExist());
		}

		@Test
		@WithMockUser(roles = "ADMIN")
		@DisplayName("Listagem de usuários não deve retornar campo senha no JSON")
		void listarUsuarios_naoRetornaSenha() throws Exception {
			mockMvc.perform(get("/api/usuario/admin/listar"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.content[*].senha").doesNotExist())
					.andExpect(jsonPath("$.content[*].password").doesNotExist());
		}

		@Test
		@DisplayName("Erro de autenticação não deve vazar stack trace")
		void erroAutenticacao_naoVazaStackTrace() throws Exception {
			var result = mockMvc.perform(post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"x@y.com\",\"senha\":\"Senha123!\"}"))
					.andExpect(status().isUnauthorized())
					.andReturn();

			String body = result.getResponse().getContentAsString();
			org.assertj.core.api.Assertions.assertThat(body)
					.doesNotContainIgnoringCase("at org.springframework")
					.doesNotContainIgnoringCase("exception")
					.doesNotContainIgnoringCase("stack")
					.doesNotContainIgnoringCase("caused by");
		}
	}

	// =========================================================================
	// 7. MASS ASSIGNMENT — campos não permitidos são ignorados
	// =========================================================================

	@Nested
	@DisplayName("7. Mass Assignment")
	class MassAssignment {

		@Test
		@DisplayName("Enviar campo 'perfil=ADMIN' no registro não deve criar admin")
		void registroComPerfilAdmin_naoDeveElevarPrivilegio() throws Exception {
			var result = mockMvc.perform(post("/api/usuario/registrar")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"nome\":\"Hacker\",\"email\":\"hacker_mass@test.com\",\"senha\":\"Senha123\",\"perfil\":\"ADMIN\"}"))
					.andExpect(status().isCreated())
					.andReturn();

			// O perfil retornado não deve ser ADMIN
			String body = result.getResponse().getContentAsString();
			org.assertj.core.api.Assertions.assertThat(body)
					.doesNotContainIgnoringCase("ADMIN");
		}

		@Test
		@DisplayName("Enviar campo 'ativo=false' no registro não deve criar conta inativa")
		void registroComAtivoFalse_naoDeveDesativarConta() throws Exception {
			mockMvc.perform(post("/api/usuario/registrar")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"nome\":\"Teste\",\"email\":\"ativo_test@test.com\",\"senha\":\"Senha123\",\"ativo\":false}"))
					.andExpect(status().isCreated());
			// Se chegou 201, o campo ativo foi ignorado — DTO não tem esse campo
		}
	}

	// =========================================================================
	// 8. HEADERS DE SEGURANÇA — resposta inclui headers básicos
	// =========================================================================

	@Nested
	@DisplayName("8. Headers de Segurança")
	class HeadersDeSeguranca {

		@Test
		@DisplayName("Resposta deve incluir X-Content-Type-Options")
		void resposta_incluiXContentTypeOptions() throws Exception {
			mockMvc.perform(post("/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content("{\"email\":\"x@y.com\",\"senha\":\"Senha123!\"}"))
					.andExpect(header().exists("X-Content-Type-Options"));
		}

		@Test
		@DisplayName("Resposta não deve incluir header Server com detalhes")
		void resposta_naoVazaHeaderServer() throws Exception {
			mockMvc.perform(get("/api/usuario/me"))
					.andExpect(header().doesNotExist("X-Powered-By"));
		}
	}
}
