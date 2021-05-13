package com.bridgelabz.userservice.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.userservice.configuration.ApplicationConfig;
import com.bridgelabz.userservice.dto.LoginDto;
import com.bridgelabz.userservice.dto.UserDTO;
import com.bridgelabz.userservice.entity.User;
import com.bridgelabz.userservice.exceptions.FundooException;
import com.bridgelabz.userservice.repository.UserRepository;
import com.bridgelabz.userservice.utils.EmailService;
import com.bridgelabz.userservice.utils.S3Service;
import com.bridgelabz.userservice.utils.TokenService;

@Service
public class UserServiceImp implements UserService {

	@Autowired
	UserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private S3Service s3Service;
	
	@Value("${verify.url}")
	private String baseUrl;

	@Override
	public void register(UserDTO userDto) {
		Optional<User> userCheck = userRepo.findByEmail(userDto.getEmail());
		if (userCheck.isPresent())
			throw new FundooException(HttpStatus.CONFLICT.value(), "User Already Exist");
		User user = new User();
		BeanUtils.copyProperties(userDto, user);
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		User savedUser = userRepo.save(user);
		String token = tokenService.createToken(savedUser.getId());
		emailService.sendMail(savedUser.getEmail(), "verfiy email", baseUrl+"user/verify/" + token);
	}

	@Override
	public void verifyEmail(String token) {

		Long id = tokenService.decodeToken(token);
		User user = userRepo.findById(id)
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(), "User Not Found"));
		user.setIsVerified(true);
		userRepo.save(user);
	}

	@Override
	public String login(LoginDto loginDto) {
		
		User user = getUser(loginDto.getEmail());
		if (user.getEmail().equals(loginDto.getEmail())
				&& passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
			return tokenService.createToken(user.getId());
		}

		else {
			throw new FundooException(HttpStatus.UNAUTHORIZED.value(),
					ApplicationConfig.getMessageAccessor().getMessage("user_unauthorized"));
		}	}

	@Override
	public void forgotPassword(String email) {
		User user = getUser(email);
		emailService.sendMail(user.getEmail(), "Reset Link",
				baseUrl + "/resetpassword/" + tokenService.createToken(user.getId()));
		
	}

	@Override
	public void resetPassword(String token, String password) {
		User user = userRepo.findById(tokenService.decodeToken(token))
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(),
						ApplicationConfig.getMessageAccessor().getMessage("1")));

		user.setPassword(passwordEncoder.encode(password));
		userRepo.save(user);
		
	}

	@Override
	public String uploadProfileImage(String token, MultipartFile file) {
		User user = userRepo.findById(tokenService.decodeToken(token))
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(),
						ApplicationConfig.getMessageAccessor().getMessage("1")));
		String key = s3Service.fileUpload(file, "profile_Images", user.getId().toString());
		user.setProfileUrl(key);
		userRepo.save(user);
		return key;	}
	
	
	public User getUser(String email) {
		return userRepo.findByEmail(email)
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(),
						ApplicationConfig.getMessageAccessor().getMessage("1")));

	}
}
