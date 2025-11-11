package net.microfin.financeapp.service;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.PasswordDTO;
import net.microfin.financeapp.dto.UserDTO;
import net.microfin.financeapp.util.KeycloakProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final UsersResource usersResource;
    private final KeycloakProperties keycloakProperties;

    public KeycloakUserService(Keycloak keycloak, UsersResource usersResource, KeycloakProperties keycloakProperties) {
        this.keycloak = keycloak;
        this.usersResource = usersResource;
        this.keycloakProperties = keycloakProperties;
    }

    public UserRepresentation createUser(UserDTO signupFormDTO) {
        log.info("Ready to create user {}", signupFormDTO);
        UserRepresentation userRepresentation = new UserRepresentation();
        CredentialRepresentation credentialRepresentation = getCredentialResource(signupFormDTO.getPassword(), null);
        userRepresentation.setUsername(signupFormDTO.getUsername());
        userRepresentation.setEnabled(true);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        userRepresentation.setCredentials(List.of(credentialRepresentation));
        userRepresentation.setEnabled(true);
        Response response = usersResource.create(userRepresentation);
        addRealmRoleToUser(signupFormDTO.getUsername(), "zbank.user");
        log.info("User details updated {}", response);
        return usersResource.search(userRepresentation.getUsername()).stream()
                .findFirst().orElseThrow(() -> new IllegalArgumentException("User not found in keycloak"));
    }

    public void updateUserPassword(PasswordDTO passwordDTO) {
        log.info("Ready to create user {}", passwordDTO);
        CredentialRepresentation credentialRepresentation = getCredentialResource(passwordDTO.getPassword(), String.valueOf(passwordDTO.getKeycloakId()));
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        usersResource.get(String.valueOf(passwordDTO.getKeycloakId())).resetPassword(credentialRepresentation);
        log.info("User password updated");
    }

    private void addRealmRoleToUser(String username, String roleName) {
        RealmResource realmResource = keycloak.realm(keycloakProperties.realm());
        List<UserRepresentation> users = realmResource.users().search(username);
        UserResource userResource =  realmResource.users().get(users.getFirst().getId());
        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
        RoleMappingResource roleMappingResource = userResource.roles();
        roleMappingResource.realmLevel().add(List.of(role));

    }

    private static CredentialRepresentation getCredentialResource(String password, String id) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setId(id);
        String credentialData = """
        {
            "hashIterations": 27500,
            "algorithm": "pbkdf2-sha256"
        }
        """;
        credentialRepresentation.setCredentialData(credentialData);
        return credentialRepresentation;
    }
}
