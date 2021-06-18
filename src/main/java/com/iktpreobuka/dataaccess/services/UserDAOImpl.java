package com.iktpreobuka.dataaccess.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanWriter;
import org.supercsv.io.dozer.ICsvDozerBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.iktpreobuka.dataaccess.entities.UserEntity;
import com.iktpreobuka.dataaccess.repositories.AddressRepository;
import com.iktpreobuka.dataaccess.repositories.UserRepository;

@Service
public class UserDAOImpl implements UserDAO {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AddressRepository addressRepository;

	@Autowired
	private JavaMailSender emailSender;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private File uploadedFile = null;

	private static String DOWNLOAD_FOLDER = "F:\\Java Vojvodina Cluster\\Backend\\SpringTemp\\";

	@Override
	public String uploadFile(MultipartFile file) throws IllegalStateException, IOException {
		if (file.equals(null)) {
			throw new MultipartException("File doesn't uploaded");
		}

		uploadedFile = File.createTempFile("temp", ".txt",
				new File("F:\\Java Vojvodina Cluster\\Backend\\SpringTemp\\"));
		file.transferTo(uploadedFile);

		return uploadedFile.getAbsolutePath();
	}

	@Override
	public UserEntity createUser(UserEntity user) {
		if (isEmailExist(user)) {
			return null;
		}
		setTroskovi(user);
		return userRepository.save(user);
	}

	@Override
	public UserEntity updateUser(UserEntity user) {

		if (isEmailExist(user)) {
			return null;
		}
		setTroskovi(user);
		return userRepository.save(user);
	}

	private boolean isEmailExist(UserEntity user) {
		UserEntity userDb = userRepository.findByEmail(user.getEmail());
		if (userDb == null) {
			return false;
		} else {
			logger.warn("The user couldn't saved, because email already exist");
			return true;
		}
	}

	private void setTroskovi(UserEntity user) {

		if (user.getAddress() == null) {
			user.setTroskovi(0.0);
			return;
		}

		switch (user.getAddress().getCity()) {
		case "Novi Sad":
			user.setTroskovi(5000.0);
			break;
		case "Beograd":
			user.setTroskovi(10000.0);
		default:
			user.setTroskovi(0.0);
		}
	}

	@Override
	public String saveFromFile() {

		if (uploadedFile == null) {
			throw new MultipartException("File doesn't uploaded");
		}

		String message = "";
		try (FileInputStream inputStream = new FileInputStream(uploadedFile);
				Scanner input = new Scanner(inputStream).useDelimiter(",\\r\\n")) {

			while (input.hasNext()) {
				String[] userObject = input.next().split(",");

				if (userObject.length > 0) {
					UserEntity user = new UserEntity();
					user.setName(userObject[0].trim());
					user.setEmail(userObject[1].trim());

					if (!isEmailExist(user)) {
						setTroskovi(user);
						userRepository.save(user);
					}
				} else {
					message += "The user couldn't saved, because the file is incorrect\r";
					logger.warn("The user couldn't saved, because the file is incorrect");
				}

				if (message.isEmpty()) {
					message = "List of users successfully saved from file";
				}

			}

		} catch (IOException e) {
			e.getStackTrace();
		}

		return message;
	}

	@Override
	public FileSystemResource downloadFile(String... userAttributes) throws IOException {

		List<UserEntity> users = (List<UserEntity>) userRepository.findAll();
		ICsvDozerBeanWriter beanWriter = null;
		FileSystemResource fileResource = null;
		try {
			beanWriter = new CsvDozerBeanWriter(new FileWriter(DOWNLOAD_FOLDER + "temp.csv"),
					CsvPreference.STANDARD_PREFERENCE);
			fileResource = new FileSystemResource(new File(DOWNLOAD_FOLDER + "temp.csv"));

			final String[] allUserAttributes = new String[] { "id", "name", "email", "telephoneNumber", "idCard",
					"dateOfBirth", "jmbg", "troskovi", "address.id", "address.street", "address.city",
					"address.country" };

			if (userAttributes == null) {
				// configure the mapping from the fields to the CSV columns
				beanWriter.configureBeanMapping(UserEntity.class, allUserAttributes);
				// write the header
				beanWriter.writeHeader(allUserAttributes);
				return fileResource;
			}

			List<String> allAttributes = Arrays.asList(allUserAttributes);

			String[] userHeader = new String[userAttributes.length];

			for (int i = 0; i < userAttributes.length; i++) {
				if (allAttributes.contains(userAttributes[i])) {
					userHeader[i] = userAttributes[i];
				} else {
					throw new IllegalArgumentException("User entity doesn't contain attribute " + userAttributes[i]);
				}
			}

			final CellProcessor[] userProcessors = getUserProcessorsByAttributes(userAttributes.length);

			// configure the mapping from the fields to the CSV columns
			beanWriter.configureBeanMapping(UserEntity.class, userHeader);

			// write the header
			beanWriter.writeHeader(userHeader);

			// write the beans data
			for (UserEntity user : users) {
				beanWriter.write(user, userProcessors);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				beanWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return fileResource;
	}

	private static CellProcessor[] getUserProcessors() {

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(new ParseInt()), // id
				new NotNull(), // name
				new NotNull(), // email
				new Optional(new StrMinMax(0, 15)), // telephoneNumber
				new Optional(new StrMinMax(0, 10)), // idCard
				new Optional(new StrMinMax(0, 13)), // jmbg
				new Optional(new NotNull()), // dateOfBirth
				new Optional(new ParseDouble()), // troskovi
				new NotNull(), // addressId
				new NotNull(), // street
				new NotNull(), // city
				new NotNull(), // country
		};

		return processors;
	}

	private static CellProcessor[] getUserProcessorsByAttributes(int numberOfAttributes) {

		final CellProcessor[] processors = new CellProcessor[numberOfAttributes];
		for (int i = 0; i < numberOfAttributes; i++) {
			processors[i] = new Optional(new NotNull());
		}
		return processors;
	}

	@Override
	public void sendMessageWithAttachment(String to, String subject, String text, MultipartFile multipartFile)
			throws Exception {
		MimeMessage mail = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
		helper.setFrom("email@gmail.com");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(text, false);
		ByteArrayDataSource attachment = new ByteArrayDataSource(multipartFile.getBytes(),
				multipartFile.getContentType());
		helper.addAttachment(multipartFile.getName(), attachment);
		emailSender.send(mail);
	}

}
