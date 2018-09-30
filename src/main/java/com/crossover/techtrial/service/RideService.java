/**
 * 
 */
package com.crossover.techtrial.service;

import java.time.LocalDateTime;
import java.util.List;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Ride;

/**
 * RideService for rides.
 * @author crossover
 *
 */
public interface RideService {

  Ride save(Ride ride);

  Ride findById(Long rideId);

  void deleteById(Long rideId);

  List<TopDriverDTO> getTopDriverWithLimit(LocalDateTime startTime, LocalDateTime endTime, Long count);
}
