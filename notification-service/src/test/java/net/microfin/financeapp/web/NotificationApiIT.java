package net.microfin.financeapp.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.AbstractTest;
import net.microfin.financeapp.ServiceApplication;
import net.microfin.financeapp.dto.NotificationDTO;
import net.microfin.financeapp.service.NotificationService;
import net.microfin.financeapp.util.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceApplication.class)
@AutoConfigureMockMvc
@EmbeddedKafka(
        topics = {"input-notification", "user-notification"}
)
public class NotificationApiIT extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldSaveNotification() throws Exception {
        NotificationDTO dto = NotificationDTO.builder()
                .id(null)
                .userId(1001)
                .operationType(OperationType.CASH_DEPOSIT.name())
                .notificationDescription("Test notification")
                .createdAt(LocalDateTime.now())
                .delivered(false)
                .build();

        when(notificationService.saveNotification(dto)).thenReturn(Optional.of(dto));

        String response = mockMvc.perform(post("/api/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        NotificationDTO result = objectMapper.readValue(response, NotificationDTO.class);
        assertThat(result.getNotificationDescription()).isEqualTo(dto.getNotificationDescription());
        assertThat(result.getUserId()).isEqualTo(dto.getUserId());
        assertThat(result.getOperationType()).isEqualTo(dto.getOperationType());
    }

    @Test
    void shouldReturnNotificationListByUserId() throws Exception {
        NotificationDTO n1 = NotificationDTO.builder()
                .id(1)
                .userId(2002)
                .notificationDescription("Message 1")
                .operationType(OperationType.TRANSFER.name())
                .createdAt(LocalDateTime.now())
                .delivered(true)
                .build();

        NotificationDTO n2 = NotificationDTO.builder()
                .id(2)
                .userId(2002)
                .notificationDescription("Message 2")
                .operationType(OperationType.EXCHANGE.name())
                .createdAt(LocalDateTime.now())
                .delivered(true)
                .build();

        when(notificationService.receiveNotification(2002)).thenReturn(List.of(n1, n2));

        String response = mockMvc.perform(get("/api/notification/user/2002")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<NotificationDTO> result = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(result).hasSize(2);
        assertThat(result).extracting(NotificationDTO::getNotificationDescription)
                .containsExactlyInAnyOrder("Message 1", "Message 2");
    }
}
