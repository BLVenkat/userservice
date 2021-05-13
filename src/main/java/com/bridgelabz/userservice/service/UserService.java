package com.bridgelabz.userservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.userservice.dto.LoginDto;
import com.bridgelabz.userservice.dto.UserDTO;

public interface UserService {

public void register(UserDTO userDto);
	
	public void verifyEmail(String token);
	
	public String login(LoginDto loginDto);
	
	public void forgotPassword(String email);
	
	public void resetPassword(String token,String password);
	
	public String uploadProfileImage(String token,MultipartFile file	);
}
