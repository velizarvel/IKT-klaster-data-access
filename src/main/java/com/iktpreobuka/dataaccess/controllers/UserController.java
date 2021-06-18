package com.iktpreobuka.dataaccess.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iktpreobuka.dataaccess.entities.AddressEntity;
import com.iktpreobuka.dataaccess.entities.UserEntity;
import com.iktpreobuka.dataaccess.repositories.AddressRepository;
import com.iktpreobuka.dataaccess.repositories.UserRepository;
import com.iktpreobuka.dataaccess.services.UserDAO;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserDAO userService;

	@Autowired
	AddressRepository addressRepository;

	@PostMapping("")
	public UserEntity createUser(@RequestBody UserEntity user) {
		return userService.createUser(user);
	}

	@PutMapping("/{id}")
	public UserEntity updateUser(@PathVariable Integer id, @RequestBody UserEntity user) {
		UserEntity userDb = userRepository.findById(id).get();
		userDb.setAddress(user.getAddress());
		userDb.setDateOfBirth(user.getDateOfBirth());
		userDb.setEmail(user.getEmail());
		userDb.setIdCard(user.getIdCard());
		userDb.setName(user.getName());
		userDb.setTelephoneNumber(user.getTelephoneNumber());
		return userService.updateUser(userDb);
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

	@PostMapping("/uploadFile")
	public String uploadFile(@RequestParam MultipartFile file) throws IllegalStateException, IOException {
		return userService.uploadFile(file);
	}

	@PostMapping("/downloadFile")
	public FileSystemResource uploadUsers(@RequestParam(required = false) String... userAttributes) throws IOException {
		return userService.downloadFile(userAttributes);
	}

	@PostMapping("/saveUsersFromFile")
	public String saveUsersFromFile() {
		return userService.saveFromFile();
	}

	@PostMapping("/emailWithAttachment")
	public String sendMessageWithAttachment(@RequestParam MultipartFile multipartFile, @RequestParam String to,
			@RequestParam String subject, @RequestParam String text) throws Exception {
		if (to == null || subject == null || text == null || multipartFile == null) {
			return null;
		}
		userService.sendMessageWithAttachment(to, subject, text, multipartFile);
		return "Your mail with atachment has been sent!";
	}
	
	@GetMapping("/fileStatus")
	public String fileStatus() {
			return "fileStatus";
	}
}
