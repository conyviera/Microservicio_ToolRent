package com.example.user_service.service;

import com.example.user_service.entity.UserEntity;
import com.example.user_service.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final List<String> ROLES_A_IGNORAR = Arrays.asList(
            "offline_access",
            "uma_authorization",
            "default-roles-sisgr-realm", // Ajusta al nombre de tu realm si cambia
            "account"
    );

    /**
     * RF 7.1: Registrar usuario (Sincronización automática con Keycloak)
     * RF 7.2: Asignar roles (Se extraen del token)
     */
    public UserEntity syncUser(Jwt jwt) {
        // Obtenemos el ID de Keycloak (sub)
        String keycloakId = jwt.getSubject();

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    // Si no existe en BD local, lo creamos
                    UserEntity newUser = new UserEntity();
                    newUser.setKeycloakId(keycloakId);
                    newUser.setUsername(jwt.getClaimAsString("preferred_username"));
                    newUser.setEmail(jwt.getClaimAsString("email"));
                    newUser.setName(jwt.getClaimAsString("name"));

                    // Extracción de roles desde el JWT (realm_access)
                    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                    List<String> rolesCompatibles = List.of();

                    if (realmAccess != null && realmAccess.containsKey("roles")) {
                        List<String> rawRoles = (List<String>) realmAccess.get("roles");

                        rolesCompatibles = rawRoles.stream()
                                .filter(role -> !ROLES_A_IGNORAR.contains(role))
                                .collect(Collectors.toList());
                    }

                    // Guardamos los roles como String separado por comas
                    newUser.setRol(String.join(",", rolesCompatibles));

                    return userRepository.save(newUser);
                });
    }

    // Método helper para obtener el usuario actual desde el contexto de seguridad
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return syncUser(jwt); // Sincroniza y retorna
        }
        return null;
    }
}