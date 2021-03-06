package com.iktpreobuka.dataaccess.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dataaccess.entities.AddressEntity;
import com.iktpreobuka.dataaccess.repositories.AddressRepository;
import com.iktpreobuka.dataaccess.services.AddressDAO;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
	
	@Autowired
	AddressRepository addressRepository;
	
	@Autowired
	AddressDAO addressDAO;

	@PostMapping
	public AddressEntity createAddress(@RequestParam String street, @RequestParam String city,@RequestParam String country) {
		AddressEntity address = new AddressEntity(street, city, country);
		addressRepository.save(address);
		return address;
	}
	
	@GetMapping
	public Iterable<AddressEntity> getAll() {
		return (List<AddressEntity>) addressRepository.findAll();
	}
	
	@GetMapping("/user/{name}")
	public List<AddressEntity> findAddressByUserName(@PathVariable String name) {
		return addressDAO.findAddressByUsername(name);
	}
	
	
}
