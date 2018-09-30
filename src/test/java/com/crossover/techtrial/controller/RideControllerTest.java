package com.crossover.techtrial.controller;

import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.utils.RestTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author narendrakumar
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideControllerTest {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    MockMvc mockMvc;

    @Mock
    private RideController rideController;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    ObjectMapper objectMapper;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createNewRide() throws Exception {
        ResponseEntity<Person> personResponse = template.getForEntity("/api/person/{person-id}", Person.class, 2);
        Assert.assertEquals(200, personResponse.getStatusCode().value());
        Person driver = personResponse.getBody();

        personResponse = template.getForEntity("/api/person/{person-id}", Person.class, 3);
        Assert.assertEquals(200, personResponse.getStatusCode().value());
        Person rider = personResponse.getBody();

        Ride ride = new Ride();
        ride.setStartTime(format.parse("2018-08-08T12:00:00"));
        ride.setEndTime(format.parse("2018-08-08T12:30:00"));
        ride.setDistance(40L);
        ride.setDriver(driver);
        ride.setRider(rider);

        ResponseEntity<Ride> response = template.postForEntity("/api/ride", RestTestUtil.getHttpEntity(objectMapper.writeValueAsString(ride)), Ride.class);

        Assert.assertEquals(200, response.getStatusCode().value());
        Ride actualRide = response.getBody();
        Assert.assertNotNull(actualRide);
        Assert.assertEquals("test driver", actualRide.getDriver().getName());
        Assert.assertEquals("test rider", actualRide.getRider().getName());

        //Delete this user
        template.delete("/api/ride/{ride-id}", response.getBody().getId(), Ride.class);
    }

    @Test
    public void getRideById() throws Exception {
        ResponseEntity<Ride> response = template.getForEntity("/api/ride/{ride-id}", Ride.class, 1);
        Assert.assertEquals(200, response.getStatusCode().value());
        Ride actualRide = response.getBody();
        Assert.assertNotNull(actualRide);
        Assert.assertEquals(Long.valueOf(1), actualRide.getId());
        Assert.assertEquals(format.parse("2018-08-08T12:00:00"), actualRide.getStartTime());
        Assert.assertEquals(format.parse("2018-08-08T12:30:00"), actualRide.getEndTime());
        Assert.assertEquals(Long.valueOf(40), actualRide.getDistance());
        Assert.assertEquals(Long.valueOf(2), actualRide.getDriver().getId());
        Assert.assertEquals(Long.valueOf(3), actualRide.getRider().getId());
    }

    @Test
    public void getRideById_NotFound() {
        ResponseEntity<Ride> response = template.getForEntity("/api/ride/{ride-id}", Ride.class, 0);
        Assert.assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void getTopDriver() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/api/top-rides").queryParam("startTime", "2018-08-08T00:00:01").queryParam("endTime", "2018-08-08T23:59:59")
                .queryParam("max", 5);
        ResponseEntity<TopDriverDTO[]> response = template.getForEntity(builder.build().toUri(), TopDriverDTO[].class);
        Assert.assertEquals(200, response.getStatusCode().value());
        TopDriverDTO[] topDriverDTOS = response.getBody();
        Assert.assertNotNull(topDriverDTOS);
        Assert.assertEquals(5, topDriverDTOS.length);
        Assert.assertEquals("test 1", topDriverDTOS[0].getName());
        Assert.assertEquals("test10000000000001@gmail.com", topDriverDTOS[0].getEmail());
        Assert.assertEquals(Long.valueOf(5100), topDriverDTOS[0].getMaxRideDurationInSecods());
        Assert.assertEquals(Long.valueOf(5100), topDriverDTOS[0].getTotalRideDurationInSeconds());
        Assert.assertEquals(Double.valueOf(100.0), topDriverDTOS[0].getAverageDistance());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        ResponseEntity<Ride> response = template.getForEntity("/api/ride/{ride-id}", Ride.class, 1);
        Assert.assertEquals(200, response.getStatusCode().value());
        Ride ride1 = response.getBody();

        response = template.getForEntity("/api/ride/{ride-id}", Ride.class, 1);
        Assert.assertEquals(200, response.getStatusCode().value());
        Ride ride2 = response.getBody();

        Assert.assertEquals(ride1, ride2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadRequest_InsufficientParameter() {
        ResponseEntity<Ride> response = template.getForEntity("/api/person/{ride-id}", Ride.class);
        Assert.assertEquals(400, response.getStatusCode().value());
    }
}