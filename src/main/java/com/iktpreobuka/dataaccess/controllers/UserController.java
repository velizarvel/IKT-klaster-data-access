package com.iktpreobuka.dataaccess.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.dataaccess.entities.AddressEntity;
import com.iktpreobuka.dataaccess.entities.UserEntity;
import com.iktpreobuka.dataaccess.repositories.AddressRepository;
import com.iktpreobuka.dataaccess.repositories.UserRepository;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	@PostMapping("")
	public UserEntity createUser(@RequestBody UserEntity user) {
		return userRepository.save(user);
	}
	
	@GetMapping("")
	public List<UserEntity> getAll() {
		return (List<UserEntity>) userRepository.findAll();
	}
	
	@PutMapping("/{id}/address")
	public UserEntity addAddress(@PathVariable Integer id, @RequestParam Integer addressId) {
		UserEntity user = userRepository.findById(id).get();
		AddressEntity address = addressRepository.findById(addressId).get();
		user.setAddress(address);
		userRepository.save(user);
		
		return user;
	}
	
	@GetMapping("/by-email")
	public UserEntity findByEmail(@RequestParam String email) {
		return userRepository.findByEmail(email);
	}
	
	@GetMapping("/by-name")
	public UserEntity findByName(@RequestParam String name) {
		return userRepository.findByName(name);
	}
	
	@GetMapping("/by-dob")
	public List<UserEntity> findByDateOfBirth(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
		return userRepository.findByDateOfBirthOrderByNameAsc(date);
	}
	
	@GetMapping("/by-name-first-letter")
	public List<UserEntity> findAllByFirstLetterOfName(@RequestParam String firstLetter) {
		return userRepository.findAllByFirstLetterOfName(firstLetter);
	}
	
	@GetMapping("/by-name-starts-with")
	public List<UserEntity> findByNameStartsWith(@RequestParam String firstLetter) {
		return ((UserRepository) userRepository).findByNameStartsWith(firstLetter);
	}
	
}
