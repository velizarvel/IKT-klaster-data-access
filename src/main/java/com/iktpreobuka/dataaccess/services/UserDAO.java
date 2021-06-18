package com.iktpreobuka.dataaccess.services;

import java.io.IOException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;

import com.iktpreobuka.dataaccess.entities.UserEntity;

public interface UserDAO {

	public String uploadFile(MultipartFile file)
			throws IllegalStateException, IOException;

	public UserEntity createUser(UserEntity user);

	public UserEntity updateUser(UserEntity user);

	public String saveFromFile();

	public FileSystemResource downloadFile(String... userAttributes)
			throws IOException;

	void sendMessageWithAttachment(String to, String subject, String text, MultipartFile multipartFile)
			throws Exception;

}
