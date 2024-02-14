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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpaceShipParkingControllerIT {

    private static final String BASE_URL = "/api/v1/parkings";
    @LocalServerPort
    private int port;
    private static final MongoDBContainer mongoDBContainer;

    static {
        mongoDBContainer = new MongoDBContainer("mongo:latest").withExposedPorts(27017);
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

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

    @Test
    void givenValidCreateReq_thenCreateNewParkingAndDoGetAndUpdateAfter2Mins() throws InterruptedException {
        final List<Parking> parkings = new ArrayList<>();

        // create
        requestDTOS().forEach(reqDTO -> {
            HttpEntity<String> entity = new HttpEntity<>(reqDTO, headers);
            ResponseEntity<Parking> response = restTemplate.exchange(
                    createURLWithPort(), HttpMethod.POST, entity, Parking.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                parkings.add(response.getBody());
            }
        });

        // get
        assertFalse(parkings.isEmpty());
        parkings.forEach(parking -> {
            var response = restTemplate
                    .getForEntity(createURLWithPort() + "/" + parking.getId(), Parking.class);
            assertTrue(response.getStatusCode().is2xxSuccessful(), "Should be 200");
            assertNotNull(response.getBody(), "Body is not suppose to be null");
            assertEquals(parking, response.getBody());
        });

        // update after 2mins
        TimeUnit.MINUTES.sleep(2);
        assertFalse(parkings.isEmpty());
        ResponseEntity<Parking> getParked = restTemplate.getForEntity(
                createURLWithPort() + "/vacate/" + parkings.getFirst().getId(), Parking.class);
        assertTrue(getParked.getStatusCode().is2xxSuccessful(), "Should be 200");
        assertNotNull(getParked.getBody(), "Body is not suppose to be null");
        assertNotNull(getParked.getBody().getParkingEndedAt());
        assertEquals(1, getParked.getBody().getDurationInHours());
    }

    @Nested
    class InvalidArgumentTests {

        @Test
        void whenInValidCreateReq1_thenBadReqResponse() throws Exception {
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
        void whenInValidCreateReq2_thenBadReqResponse() throws Exception {
            final CreateParkingRequestDTO badRequestDTO = CreateParkingRequestDTO.builder()
                    .parkingPlace(ParkingPlace.builder().floor(2).plot(14).build())
                    .spaceShip(SpaceShip.builder().name(null).registrationNumber("@mine_own").build())
                    .spaceShipUser(SpaceShipUser.builder().user_id("star-lord").build())
                    .build();

            final String st1 = objectMapper.writeValueAsString(badRequestDTO);

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
        void whenValidCreateReq_thenBadReqResponse(Integer floor, Integer plot) throws Exception {
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
            ResponseEntity<Parking> getParked = restTemplate
                    .getForEntity(createURLWithPort() + "/vacate/" + Id, Parking.class);
            assertTrue(getParked.getStatusCode().is4xxClientError(), "Should be 400, bad req");
            // TODO need to fix the return types for exceptions
            assertNotNull(getParked.getBody());
            assertNull(getParked.getBody().getId());
        }
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + BASE_URL;
    }

    private String getReq(int floor, int plot, String regNo) throws JsonProcessingException {
        final CreateParkingRequestDTO requestDTO = CreateParkingRequestDTO.builder()
                .parkingPlace(ParkingPlace.builder().floor(floor).plot(plot).build())
                .spaceShip(SpaceShip.builder().name("RANDOM").registrationNumber(regNo).build())
                .spaceShipUser(SpaceShipUser.builder().user_id("my_own_id1").build())
                .build();

        return objectMapper.writeValueAsString(requestDTO);
    }

    private int getRandomNumberUsingNextInt(int max) {
        Random random = new Random();
        return random.nextInt(max - 1) + 1;
    }

    private List<String> requestDTOS() {
        var createParkingRequestDTOs = new ArrayList<String>();

        IntStream.range(0, 9).forEach(i -> {
            var floor = getRandomNumberUsingNextInt(3);
            var plot = getRandomNumberUsingNextInt(15);
            var regNo = "CSV" + floor + plot + i;
            try {
                createParkingRequestDTOs.add(getReq(floor, plot, regNo));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return createParkingRequestDTOs;
    }
}