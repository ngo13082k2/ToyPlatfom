package com.example.toyplatform_swp_project.services.implement;

import com.example.toyplatform_swp_project.dto.RentalDto;
import com.example.toyplatform_swp_project.model.Rental;
import com.example.toyplatform_swp_project.model.Toy;
import com.example.toyplatform_swp_project.repository.RentalRepository;
import com.example.toyplatform_swp_project.repository.ToyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private ToyRepository toyRepository;

    public RentalDto createRental(RentalDto rentalDto) {
        Rental rental = mapToEntity(rentalDto);
        Rental savedRental = rentalRepository.save(rental);
        return mapToDto(savedRental);
    }

    private Rental mapToEntity(RentalDto dto) {
        Rental rental = new Rental();
        rental.setRentalId(dto.getRentalId());
        rental.setUser(dto.getUser());

        if (dto.getToy() != null && dto.getToy().getToyId() != null) {
            Toy toy = toyRepository.findById(dto.getToy().getToyId())
                    .orElseThrow(() -> new RuntimeException("Toy not found with id: " + dto.getToy().getToyId()));
            rental.setToy(toy);

            Double toyPrice = toy.getPrice();
            Integer duration = dto.getRentalDuration();
            Double rentalPrice = (toyPrice != null && duration != null) ? toyPrice * duration : 0.0;
            rental.setRentalPrice(rentalPrice);
        }

        rental.setRentalDuration(dto.getRentalDuration());
        rental.setRequestDate(dto.getRequestDate());
        return rental;
    }

    private RentalDto mapToDto(Rental rental) {
        RentalDto dto = new RentalDto();
        dto.setRentalId(rental.getRentalId());
        dto.setUser(rental.getUser());
        dto.setToy(rental.getToy());
        dto.setRentalDuration(rental.getRentalDuration());
        dto.setRequestDate(rental.getRequestDate());
        dto.setRentalPrice(rental.getRentalPrice());
        return dto;
    }
}
