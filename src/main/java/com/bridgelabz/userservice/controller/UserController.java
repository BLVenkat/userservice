package com.bridgelabz.userservice.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.userservice.configuration.ApplicationConfig;
import com.bridgelabz.userservice.dto.LoginDto;
import com.bridgelabz.userservice.dto.UserDTO;
import com.bridgelabz.userservice.response.Response;
import com.bridgelabz.userservice.service.UserService;

@RestController
@RequestMapping(value="/user",produces = "application/json")
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/register",consumes ="application/json")
//	@ApiOperation(value = "register a user for fundonotes",response = Response.class)
//	@ApiResponses(value = {
//			@ApiResponse(code = 201,message = "User registered Successfully"),
//			@ApiResponse(code = 401,message = "User not found")

//	})
	public ResponseEntity<Response> register(@Valid @RequestBody UserDTO userDto,BindingResult result){
		
		if(result.hasErrors()) {
			return new ResponseEntity<Response>(new Response(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getAllErrors().get(0).getDefaultMessage(), ""), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		userService.register(userDto);
		return new ResponseEntity<Response>(new Response(HttpStatus.CREATED.value(), ApplicationConfig.getMessageAccessor().getMessage("100"), ""), HttpStatus.CREATED);
	}
	
	@PutMapping(value = "/verify/{token}")
	//@ApiOperation(value = "api to verify user email",response = Response.class)
	public ResponseEntity<Response> verifyEmail(@PathVariable String token){
		
		userService.verifyEmail(token);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(),  ApplicationConfig.getMessageAccessor().getMessage("101"), ""), HttpStatus.OK);
	}
	
	@PostMapping(value = {"/login"})
	public ResponseEntity<Response> login(@RequestBody LoginDto loginDto){
		String token = userService.login(loginDto);
		
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), ApplicationConfig.getMessageAccessor().getMessage("102"), token), HttpStatus.OK);
	}
	
	@GetMapping(value = "/forgotpassword")
	public ResponseEntity<Response> forgotPassword(@RequestParam String email){
		userService.forgotPassword(email);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), ApplicationConfig.getMessageAccessor().getMessage("103"), ""),HttpStatus.OK);
	}
	
	@PutMapping(value = "/restpassword")
	//@ApiOperation(value = "api to reset password of user",response = Response.class)
	public ResponseEntity<Response> resetPassword(@RequestHeader String token,@ModelAttribute("password") String password){
		userService.resetPassword(token, password);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(),  ApplicationConfig.getMessageAccessor().getMessage("104"), ""), HttpStatus.OK);
	}
	
	@PutMapping(value = "/profileimage")
	public ResponseEntity<Response> uploadProfile(@RequestHeader String token,@RequestParam MultipartFile file){
		
		String key = userService.uploadProfileImage(token, file);
		
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), ApplicationConfig.getMessageAccessor().getMessage("105"), key),HttpStatus.OK); 
	}
}
