package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {

        public Beer beerToDelete() {
            Random rand = new Random();

            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete Me Beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(String.valueOf(rand.nextInt(99999999)))
                    .build());
        }

        @Test
        void deleteBeerHttpBasic() throws Exception{
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("admin", "pass")))
                    .andExpect(status().is2xxSuccessful());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
        void deleteBeerHttpBasicNotAuth(String user, String pwd) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerNoAuth() throws Exception {
            mockMvc.perform(delete("/api/v1/beer/" + beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("List Beers")
    @Nested
    class ListBeers {
        @Test
        void findBeers() throws Exception {
            mockMvc.perform(get("/api/v1/beer/"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(get("/api/v1/beer/").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Beer By ID")
    @Nested
    class GetBeerById {
        @Test
        void findBeerById() throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerByIdAUTH(String user, String pwd) throws Exception {
            Beer beer = beerRepository.findAll().get(0);

            mockMvc.perform(get("/api/v1/beer/" + beer.getId())
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Find By UPC")
    class FindByUPC {
        @Test
        void findBeerByUpc() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerByUpcAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234200036")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

//    @Test
//    void deleteBeerUrl() throws Exception {
//        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
//                .param("apiKey", "admin").param("apiSecret", "pass"))
//                .andExpect(status().isOk());
//    }

    @Test
    void deleteBeerBadCredentialsUrl() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                .param("apiKey", "admin").header("apiSecret", "password"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeerBadCredentials() throws Exception {
        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
                .header("Api-Key", "admin")
                .header("Api-Secret", "password"))
                .andExpect(status().isUnauthorized());
    }
//    @Test
//    void deleteBeer() throws Exception {
//        mockMvc.perform(delete("/api/v1/beer/97df0c39-90c4-4ae0-b663-453e8e19c311")
//                        .header("Api-Key", "admin")
//                        .header("Api-Secret", "pass"))
//                .andExpect(status().isOk());
//    }

//    @Test
//    void findBeers() throws Exception {
//        mockMvc.perform(get("/api/v1/beer/"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void findBeerById() throws Exception {
//        Beer beer = beerRepository.findAll().get(0);
//        mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void findBeerByUpc() throws Exception {
//        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
//                .andExpect(status().isOk());
//    }

    @Test
    void findBeerFormAdmin() throws Exception {
        mockMvc.perform(get("/beers").param("beerName", "")
                .with(httpBasic("admin", "pass")))
                .andExpect(status().isOk());
    }
}
