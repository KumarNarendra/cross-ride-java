/**
 *
 */
package com.crossover.techtrial.service;

import java.util.List;

import com.crossover.techtrial.model.Person;

/**
 * PersonService interface for Persons.
 *
 * @author cossover
 */
public interface PersonService {
    List<Person> getAll();

    Person save(Person p);

    Person findById(Long personId);

    void deleteById(Long personId);

}
