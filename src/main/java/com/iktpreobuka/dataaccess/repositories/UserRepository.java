package com.iktpreobuka.dataaccess.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iktpreobuka.dataaccess.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer>{
	
	public UserEntity findByEmail(String email);
	
	public UserEntity findByName(String name);
	
	public List<UserEntity> findByDateOfBirthOrderByNameAsc(LocalDate dateOfBirth);
	
	public List<UserEntity> findByNameStartsWith(String firstLetter);
	
	//Drugi nacin kada se napravi sopstveni query
	
	@Query("SELECT u FROM UserEntity u WHERE name LIKE :firstLetter%")
	public List<UserEntity> findAllByFirstLetterOfName(@Param("firstLetter")String firstLetter);

}
