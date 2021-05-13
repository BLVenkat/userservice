package com.bridgelabz.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginDto {

	 //@ApiModelProperty(notes = "provide email",required = true,example = "admin@gmail.com")	
		private String email;
		
		private String password;
}
