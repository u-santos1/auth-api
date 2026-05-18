package AuthAPI.AuthAPI.controller;

import AuthAPI.AuthAPI.dtos.UsuarioResponseDTO;
import AuthAPI.AuthAPI.dtos.requests.UsuarioRequestDTO;

import AuthAPI.AuthAPI.infra.segurity.RegistroRateLimitService;
import AuthAPI.AuthAPI.infra.segurity.SecurityLogger;
import AuthAPI.AuthAPI.model.Usuario;
import AuthAPI.AuthAPI.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;



@RestController
@RequestMapping("/api/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RegistroRateLimitService rateLimitService;


    @PostMapping("/registrar/admin")
    public ResponseEntity<UsuarioResponseDTO> criarAmin(@RequestBody @Valid UsuarioRequestDTO data, UriComponentsBuilder uriComponentsBuilder){

        UsuarioResponseDTO dto = usuarioService.criarAdmin(data);

        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> criarUser(@Valid @RequestBody UsuarioRequestDTO data,
                                                        UriComponentsBuilder uriComponentsBuilder,
                                                        HttpServletRequest request){
        String ip = extrairIpReal(request);

        rateLimitService.verificarLimiteDeRegistro(ip);

        UsuarioResponseDTO dto = usuarioService.criarUser(data);

        var uri = uriComponentsBuilder.path("/usuario/{id}").buildAndExpand(dto.id()).toUri();

        return ResponseEntity.created(uri).body(dto);
    }
    private String extrairIpReal(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if(ip == null || ip.isBlank()){
            return request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> detalharPerfil(@AuthenticationPrincipal Usuario usuarioLogado){
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioLogado));
    }

    @GetMapping("/admin/listar")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarTodos(Pageable pageable){
        return ResponseEntity.ok(usuarioService.listarTodos(pageable));
    }
}
