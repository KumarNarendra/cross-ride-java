/**
 *
 */
package com.crossover.techtrial.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.utils.RestTestUtil;

/**
 * @author kshah
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PersonControllerTest {

    MockMvc mockMvc;

    @Mock
    private PersonController personController;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
    }

    @Test
    public void testPanelShouldBeRegistered() throws Exception {
        HttpEntity<Object> person = RestTestUtil
                .getHttpEntity("{\"name\": \"test 1\", \"email\": \"test10000000000001@gmail.com\"," + " \"registrationNumber\": \"41DCT\",\"registrationDate\":\"2018-08-08T12:12:12\" }");
        ResponseEntity<Person> response = template.postForEntity("/api/person", person, Person.class);

        Assert.assertEquals(200, response.getStatusCode().value());
        Assert.assertEquals("test 1", response.getBody().getName());

        //Delete this user
        template.delete("/api/person/{person-id}", response.getBody().getId(), Person.class);
    }

    @Test
    public void testGetPersons() throws Exception {
        ResponseEntity<Person[]> responseEntity = template.getForEntity("/api/person", Person[].class);
        Assert.assertEquals(200, responseEntity.getStatusCode().value());
        Assert.assertEquals("test 1", responseEntity.getBody()[0].getName());
    }

    @Test
    public void testGetPersonById() throws Exception {
        ResponseEntity<Person> response = template.getForEntity("/api/person/{person-id}", Person.class, 1);
        Assert.assertEquals(200, response.getStatusCode().value());
        Person person = response.getBody();
        Assert.assertNotNull(person);
        Assert.assertEquals(Long.valueOf(1), person.getId());
        Assert.assertEquals("test 1", person.getName());
        Assert.assertEquals("test10000000000001@gmail.com", person.getEmail());
        Assert.assertEquals("41DCT", person.getRegistrationNumber());
    }

    @Test
    public void testGetPersonById_NotFound() throws Exception {
        ResponseEntity<Person> response = template.getForEntity("/api/person/{person-id}", Person.class, 0);
        Assert.assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        ResponseEntity<Person> response = template.getForEntity("/api/person/{person-id}", Person.class, 1);
        Assert.assertEquals(200, response.getStatusCode().value());
        Person person1 = response.getBody();

        response = template.getForEntity("/api/person/{person-id}", Person.class, 1);
        Assert.assertEquals(200, response.getStatusCode().value());
        Person person2 = response.getBody();

        Assert.assertEquals(person1, person2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadRequest_InsufficientParameter() {
        ResponseEntity<Person> response = template.getForEntity("/api/person/{person-id}", Person.class);
        Assert.assertEquals(400, response.getStatusCode().value());
    }
}
