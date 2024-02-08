package com.twoday.spaceshipparking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twoday.spaceshipparking.dao.Parking;
import com.twoday.spaceshipparking.dao.ParkingPlace;
import com.twoday.spaceshipparking.dao.SpaceShip;
import com.twoday.spaceshipparking.dao.SpaceShipUser;
import com.twoday.spaceshipparking.dto.CreateParkingRequestDTO;
import com.twoday.spaceshipparking.service.ParkingService;
import com.twoday.spaceshipparking.usecases.GetParkedSpaceShipUseCase;
import com.twoday.spaceshipparking.usecases.ParkSpaceShipUseCase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
class SpaceShipParkingControllerIT {

    public static final String BASE_URL = "/api/v1/parkings";
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private GetParkedSpaceShipUseCase getParkedSpaceShipUseCase;

    @Autowired
    private ParkSpaceShipUseCase parkSpaceShipUseCase;

    private static HttpHeaders headers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
    private String createURLWithPort() {
        return "http://localhost:" + port + BASE_URL;
    }

    @Nested
    class ValidArgumentTests {

        //List<CreateParkingRequestDTO> requestDTOS = createValidRequests();

    }

    @Nested
    class InvalidArgumentTests {

        @Test
        void whenInValidCreateReq1_thenBadReqResponse () throws Exception {
            final CreateParkingRequestDTO badRequestDTO = CreateParkingRequestDTO.builder()
                    .parkingPlace(ParkingPlace.builder().floor(-1).plot(25).build())
                    .spaceShip(SpaceShip.builder().name(null).registrationNumber(null).build())
                    .spaceShipUser(SpaceShipUser.builder().user_id(null).build())
                    .build();

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(badRequestDTO), headers);
            ResponseEntity<Parking> response = restTemplate.exchange(
                    createURLWithPort(), HttpMethod.POST, entity, Parking.class);
            assertTrue(response.getStatusCode().is4xxClientError(), "Should be 400, bad req");
            assertNotNull(response.getBody(), "Body is not suppose to be null");
            // TODO same as below.
            assertNull(response.getBody().getId());
        }

        @Test
        void whenInValidCreateReq2_thenBadReqResponse () throws Exception {
            final CreateParkingRequestDTO badRequestDTO = CreateParkingRequestDTO.builder()
                    .parkingPlace(ParkingPlace.builder().floor(2).plot(14).build())
                    .spaceShip(SpaceShip.builder().name(null).registrationNumber("@mine_own").build())
                    .spaceShipUser(SpaceShipUser.builder().user_id("star-lord").build())
                    .build();

            final String st1 = objectMapper.writeValueAsString(badRequestDTO);
            System.out.println(st1);

            HttpEntity<String> entity = new HttpEntity<>(st1, headers);
            ResponseEntity<Parking> response = restTemplate.exchange(
                    createURLWithPort(), HttpMethod.POST, entity, Parking.class);
            assertTrue(response.getStatusCode().is4xxClientError(), "Should be 400, bad req");
            assertNotNull(response.getBody(), "Body is not suppose to be null");
            // TODO same as below.
            assertNull(response.getBody().getId());
        }

        @ParameterizedTest
        @CsvSource({"-2,16", "4,14", "0,0"})
        void whenValidCreateReq_thenBadReqResponse (Integer floor, Integer plot) throws Exception {
            // boundary value testing
            HttpEntity<String> entity = new HttpEntity<>(getReq(floor, plot, "CSX 123"), headers);
            ResponseEntity<Parking> response = restTemplate.exchange(
                    createURLWithPort(), HttpMethod.POST, entity, Parking.class);
            assertTrue(response.getStatusCode().is4xxClientError(), "Should be 400, bad req");
            assertNotNull(response.getBody(), "Body is not suppose to be null");
            // TODO same as below.
            assertNull(response.getBody().getId());
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = "Some_random_id")
        void givenInvalidId_whenGetById_thenReturnBadReq(String Id) {
            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<Parking> getParked = restTemplate
                    .getForEntity(createURLWithPort() + "/" + Id, Parking.class);
            assertTrue(getParked.getStatusCode().is4xxClientError(), "Should be 400, bad req");
            // TODO need to fix the return types for exceptions
            assertNotNull(getParked.getBody());
            assertNull(getParked.getBody().getId());
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = "some-random-id")
        void givenInvalidId_whenPerformUpdateById_thenReturnBadReq(String Id) {
            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<Parking> getParked = restTemplate
                    .getForEntity(createURLWithPort() + "/vacate/" + Id, Parking.class);
            assertTrue(getParked.getStatusCode().is4xxClientError(), "Should be 400, bad req");
            // TODO need to fix the return types for exceptions
            assertNotNull(getParked.getBody());
            assertNull(getParked.getBody().getId());
        }
    }

    @Nested
    class UseCaseTests {
        @Test
        void givenValidCreateReq_thenCreateNewParkingAndDoGet () throws JsonProcessingException {
            HttpEntity<String> entity = new HttpEntity<>(getReq(2,5, "CSX 123"), headers);
            ResponseEntity<Parking> response = restTemplate.exchange(
                    createURLWithPort(), HttpMethod.POST, entity, Parking.class);
            assertTrue(response.getStatusCode().is2xxSuccessful(), "Should be 201");
            var newParking = response.getBody();
            assertNotNull(newParking, "Body is not suppose to be null");
            assertNotNull(newParking.getId(), "Should get an ID");

            entity = new HttpEntity<>(null, headers);
            response = restTemplate
                    .getForEntity(createURLWithPort() + "/" + newParking.getId(), Parking.class);
            assertTrue(response.getStatusCode().is2xxSuccessful(), "Should be 200");
            assertNotNull(response.getBody(), "Body is not suppose to be null");
            assertEquals(newParking, response.getBody());
        }

        @Test
        void givenValidDBId_thenUpdateParking() {
            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<Parking> getParked = restTemplate.getForEntity(
                    createURLWithPort() + "/vacate/" + "65c4d41830e446024202fdbc",
                        Parking.class);
            assertTrue(getParked.getStatusCode().is2xxSuccessful(), "Should be 200");
            assertNotNull(getParked.getBody(), "Body is not suppose to be null");

        }
    }

    private String getReq(int floor, int plot, String regNo) throws JsonProcessingException {
        final CreateParkingRequestDTO requestDTO = CreateParkingRequestDTO.builder()
                .parkingPlace(ParkingPlace.builder().floor(floor).plot(plot).build())
                .spaceShip(SpaceShip.builder().name("RANDOM").registrationNumber(regNo).build())
                .spaceShipUser(SpaceShipUser.builder().user_id("my_own_id1").build())
                .build();

        return objectMapper.writeValueAsString(requestDTO);
    }
}