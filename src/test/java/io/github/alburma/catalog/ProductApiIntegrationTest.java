package io.github.alburma.catalog;

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
class ProductApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void listProducts_isPublic_andReturnsSeedData() throws Exception {
        mvc.perform(get("/api/products"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content").isArray())
           .andExpect(jsonPath("$.content[0].sku").exists())
           .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void searchProducts_filtersByQuery() throws Exception {
        mvc.perform(get("/api/products").param("q", "wine"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content[0].name", org.hamcrest.Matchers.containsStringIgnoringCase("wine")));
    }

    @Test
    void createProduct_withoutAuth_is401or403() throws Exception {
        String body = """
          {"sku":"NEW-SKU","name":"New","priceCents":1000,"currency":"EUR","stock":1}
        """;
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
           .andExpect(status().is4xxClientError());
    }

    @Test
    void login_returnsJwt_andAdminCanCreateProduct() throws Exception {
        String loginBody = """
          {"username":"admin","password":"password123"}
        """;
        MvcResult login = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode node = om.readTree(login.getResponse().getContentAsString());
        String token = node.get("token").asText();

        String productBody = """
          {"sku":"JWT-TEST-1","name":"JWT Test","priceCents":500,"currency":"EUR","stock":3}
        """;
        mvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(productBody))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.sku").value("JWT-TEST-1"));
    }

    @Test
    void regularUser_cannotCreateProduct() throws Exception {
        String loginBody = """
          {"username":"user","password":"password123"}
        """;
        MvcResult login = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn();
        String token = om.readTree(login.getResponse().getContentAsString()).get("token").asText();

        String productBody = """
          {"sku":"USER-FORBIDDEN","name":"x","priceCents":100,"currency":"EUR","stock":1}
        """;
        mvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(productBody))
           .andExpect(status().isForbidden());
    }
}
