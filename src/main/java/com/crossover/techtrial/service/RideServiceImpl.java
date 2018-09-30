/**
 *
 */
package com.crossover.techtrial.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.RideRepository;

/**
 * @author crossover
 */
@Service
public class RideServiceImpl implements RideService {

    @Autowired
    RideRepository rideRepository;

    public Ride save(Ride ride) {
        return rideRepository.save(ride);
    }

    public Ride findById(Long rideId) {
        Optional<Ride> optionalRide = rideRepository.findById(rideId);
        return optionalRide.orElse(null);
    }

    @Override
    public void deleteById(final Long rideId) {
        rideRepository.deleteById(rideId);
    }

    @Override
    public List<TopDriverDTO> getTopDriverWithLimit(final LocalDateTime startTime, final LocalDateTime endTime, final Long count) {
        final List<Ride> rides = rideRepository
                .findAllRidesBetweenStartTimeAndEndTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()), Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        final Map<Long, List<Ride>> driverRides = rides.stream().collect(Collectors.groupingBy(ride -> ride.getDriver().getId()));
        return driverRides.entrySet().parallelStream().map(this::createTopDriverDTO).sorted(Comparator.comparingDouble(TopDriverDTO::getMaxRideDurationInSecods).reversed())
                .collect(Collectors.toList());
    }

    private TopDriverDTO createTopDriverDTO(final Map.Entry<Long, List<Ride>> entry) {
        TopDriverDTO topDriverDTO = new TopDriverDTO();
        List<Ride> rides = entry.getValue();
        final Person driver = rides.get(0).getDriver();
        topDriverDTO.setName(driver.getName());
        topDriverDTO.setEmail(driver.getEmail());
        Double averageDistance = 0.0;
        Long maxDuration = 0L;
        Long totalDuration = 0L;
        for (Ride ride : rides) {
            averageDistance += ride.getDistance();
            final Long duration = ride.getEndTime().toInstant().getEpochSecond() - ride.getStartTime().toInstant().getEpochSecond();
            if (maxDuration < duration) {
                maxDuration = duration;
            }
            totalDuration += duration;
        }
        topDriverDTO.setAverageDistance(averageDistance);
        topDriverDTO.setMaxRideDurationInSecods(maxDuration);
        topDriverDTO.setTotalRideDurationInSeconds(totalDuration);
        return topDriverDTO;
    }

}
