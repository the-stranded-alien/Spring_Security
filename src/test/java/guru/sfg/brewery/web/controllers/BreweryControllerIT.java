package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class BreweryControllerIT extends BaseIT {

    @Test
    void listBreweriesCustomer() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweryUser() throws Exception {
        mockMvc.perform(get("/brewery/breweries").with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBreweryAdmin() throws Exception{
        mockMvc.perform(get("/brewery/breweries").with(httpBasic("admin", "pass")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweryNoAuth() throws Exception{
        mockMvc.perform(get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBreweriesJsonCustomer() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getBreweriesJsonUser() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBreweriesJsonAdmin() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("admin", "pass")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getBreweriesJsonNoAuth() throws Exception {
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }
}
