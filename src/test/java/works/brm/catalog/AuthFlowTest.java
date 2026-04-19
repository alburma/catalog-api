package works.brm.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void register_returnsToken() throws Exception {
        String body = """
          {"username":"newbie","email":"newbie@example.com","password":"secret12345"}
        """;
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.token").isNotEmpty())
           .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_badPassword_returns401() throws Exception {
        String body = """
          {"username":"admin","password":"wrong"}
        """;
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withValidToken_returnsProfile() throws Exception {
        String loginBody = """
          {"username":"admin","password":"password123"}
        """;
        MvcResult login = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn();
        String token = om.readTree(login.getResponse().getContentAsString()).get("token").asText();

        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username").value("admin"))
           .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
