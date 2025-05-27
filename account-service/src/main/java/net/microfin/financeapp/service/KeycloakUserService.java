package net.microfin.financeapp.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.microfin.financeapp.dto.UserDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final UsersResource usersResource;

    @Value("${keycloak.realm}")
    private String realm;

    public UserRepresentation createUser(UserDTO signupFormDTO) {
        log.info("Ready to create user {}", signupFormDTO);
        UserRepresentation userRepresentation = new UserRepresentation();
        CredentialRepresentation credentialRepresentation = getCredentialResource(signupFormDTO.getPassword());
        userRepresentation.setUsername(signupFormDTO.getUsername());
        userRepresentation.setEnabled(true);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        userRepresentation.setCredentials(List.of(credentialRepresentation));
        userRepresentation.setEnabled(true);
        Response response = usersResource.create(userRepresentation);
        addRealmRoleToUser(signupFormDTO.getUsername(), "zbank.user");
        log.info("User details updated {}", response);
        return usersResource.search(userRepresentation.getUsername()).getFirst();
    }

    private void addRealmRoleToUser(String username, String roleName) {
        RealmResource realmResource = keycloak.realm(realm);
        List<UserRepresentation> users = realmResource.users().search(username);
        UserResource userResource =  realmResource.users().get(users.getFirst().getId());
        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
        RoleMappingResource roleMappingResource = userResource.roles();
        roleMappingResource.realmLevel().add(List.of(role));

    }

    private static CredentialRepresentation getCredentialResource(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }
}
