package com.acme.reco.api;

import com.acme.reco.api.dto.AdminUpdateUserRoleRequest;
import com.acme.reco.api.dto.AdminUserSummaryDTO;
import com.acme.reco.persistence.entity.AppUser;
import com.acme.reco.persistence.repo.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsersController {

    private final AppUserRepository users;

    public AdminUsersController(AppUserRepository users) {
        this.users = users;
    }

    /**
     * Lista de usuarios (paginada).
     */
    @GetMapping
    public Page<AdminUserSummaryDTO> list(Pageable pageable) {
        Page<AppUser> page = users.findAll(pageable);
        return page.map(AdminUserSummaryDTO::fromEntity);
    }

    /**
     * Detalle de un usuario.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserSummaryDTO> get(@PathVariable("id") UUID id) {
        return users.findById(id)
                .map(AdminUserSummaryDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cambiar rol de un usuario.
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<AdminUserSummaryDTO> updateRole(@PathVariable("id") UUID id,
                                                          @Valid @RequestBody AdminUpdateUserRoleRequest req) {
        return users.findById(id)
                .map(u -> {
                    u.setRole(req.getRole());
                    users.save(u);
                    return ResponseEntity.ok(AdminUserSummaryDTO.fromEntity(u));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Borrar usuario.
     * (Tus repos/servicios ya se encargan de cascadas o borrado lógico de ratings, etc.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        if (!users.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        users.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
