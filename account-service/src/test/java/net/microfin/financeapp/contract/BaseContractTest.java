package net.microfin.financeapp.contract;

import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.config.KeyCloakConfig;
import net.microfin.financeapp.service.KeycloakUserService;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

//@Testcontainers
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
public abstract class BaseContractTest extends AbstractTest {

    @MockitoBean
    private KeycloakUserService keycloakUserService;
    @MockitoBean
    private KeyCloakConfig keyCloakConfig;
    @MockitoBean
    private UsersResource usersResource;
    @MockitoBean
    private JwtDecoder jwtDecoder;


    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
//        io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc(
//                MockMvcBuilders.webAppContextSetup(context).build()
//        );
    }
}
