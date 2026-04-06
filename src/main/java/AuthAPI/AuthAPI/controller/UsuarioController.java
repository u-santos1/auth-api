package AuthAPI.AuthAPI.controller;

import AuthAPI.AuthAPI.dtos.UsuarioResponseDTO;
import AuthAPI.AuthAPI.dtos.requests.UsuarioRequestDTO;
import AuthAPI.AuthAPI.model.Usuario;
import AuthAPI.AuthAPI.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar/admin")
    public ResponseEntity<UsuarioResponseDTO> criarAmin(@RequestBody @Valid UsuarioRequestDTO data, UriComponentsBuilder uriComponentsBuilder){

        UsuarioResponseDTO dto = usuarioService.criarAdmin(data);

        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> criarUser(@Valid @RequestBody UsuarioRequestDTO data, UriComponentsBuilder uriComponentsBuilder){
        UsuarioResponseDTO dto = usuarioService.criarUser(data);

        var uri = uriComponentsBuilder.path("/usuario/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> detalharPerfil(@AuthenticationPrincipal Usuario usuarioLogado){
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioLogado));
    }

    @GetMapping("/admin/listar")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(){
        return ResponseEntity.ok(usuarioService.listarTodos());
    }
}
