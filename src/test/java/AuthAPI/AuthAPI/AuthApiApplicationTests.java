package AuthAPI.AuthAPI;

import lombok.RequiredArgsConstructor;
import org.glassfish.jaxb.runtime.v2.runtime.output.Encoded;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

class AuthApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads(){

	}
	@Test
	@WithMockUser(roles = "USER")
	void dadosUSuarioComun_quandoTentarRegistrarAdmin_entaoRetornarForbidden() throws Exception {
		mockMvc.perform(get("/api/usuario/admin/listar"))
				.andExpect(status().isForbidden());
	}
	@Test
	void dadoUsuarioNaoAutenticado_quandoTentarAcessarRotaProtegida_entaoRetornarUnauthorized() throws Exception {
		mockMvc.perform(get("/api/usuario/me"))
				.andExpect(status().isForbidden());
	}
	@Test
	void gerarHashDeSenha() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String senhaCriptografada = encoder.encode("admin123");
		System.out.println("Fmtdm1155");
		System.out.println(senhaCriptografada);
	}
}
