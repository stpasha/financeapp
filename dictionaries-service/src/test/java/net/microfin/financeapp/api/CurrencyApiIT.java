package net.microfin.financeapp.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.microfin.financeapp.ServiceApplication;
import net.microfin.financeapp.dto.CurrencyDTO;
import net.microfin.financeapp.service.CurrencyService;
import net.microfin.financeapp.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = ServiceApplication.class)
@AutoConfigureMockMvc
public class CurrencyApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CurrencyService currencyService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldReturnCurrencyList() throws Exception {
        CurrencyDTO dto = new CurrencyDTO("Рубль", "RUB", BigDecimal.ONE);

        when(currencyService.listCurrency()).thenReturn(singletonList(dto));

        String response = mockMvc.perform(get("/api/dictionary/currency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "user").claim("preferred_username", "user"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<CurrencyDTO> result = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Рубль");
        assertThat(result.get(0).code()).isEqualTo(Currency.RUB.name());
        assertThat(result.get(0).value()).isEqualByComparingTo(BigDecimal.ONE);
    }
}
