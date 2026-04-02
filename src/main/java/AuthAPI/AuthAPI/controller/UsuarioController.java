package AuthAPI.AuthAPI.controller;

import AuthAPI.AuthAPI.dtos.UsuarioResponseDTO;
import AuthAPI.AuthAPI.dtos.requests.UsuarioRequestDTO;
import AuthAPI.AuthAPI.model.Usuario;
import AuthAPI.AuthAPI.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/api/auth")
public class UsuarioController {

    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarAmin(@RequestBody @Valid UsuarioRequestDTO data, UriComponentsBuilder uriComponentsBuilder){

        UsuarioResponseDTO dto = usuarioService.criarAdmin(data);

        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
}
