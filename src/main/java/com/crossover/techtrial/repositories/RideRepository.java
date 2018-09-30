/**
 *
 */
package com.crossover.techtrial.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.crossover.techtrial.model.Ride;

/**
 * @author crossover
 */
@RestResource(exported = false)
public interface RideRepository extends CrudRepository<Ride, Long> {

    @Query("SELECT r FROM Ride r WHERE r.startTime >= :startTime and r.endTime <= :endTime")
    List<Ride> findAllRidesBetweenStartTimeAndEndTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
