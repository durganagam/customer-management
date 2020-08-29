package com.ecom.user.management.user.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.user.management.exceptions.EcommerceException;
import com.ecom.user.management.user.entity.Name;
import com.ecom.user.management.user.entity.OtpRequest;
import com.ecom.user.management.user.entity.User;
import com.ecom.user.management.user.entity.UserLogin;
import com.ecom.user.management.user.entity.UserLoginRepository;
import com.ecom.user.management.user.entity.UserRepository;
import com.ecom.user.management.user.entity.model.UserStatus;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserLoginRepository userLoginRepository;

	public User createUser(final Name name, final String mobileNo) {
		if (!isUserExists(mobileNo)) {
			final User user = new User(name);
			user.setMobileNo(mobileNo);
			user.setUserId(UUID.randomUUID().toString());
			
			UserLogin userLogin = new UserLogin();
			userLogin.setLoginId(user.getMobileNo());
			userLogin.setUserId(user.getUserId());
			userLogin.setStatus(UserStatus.INACTIVE);

			userLoginRepository.save(userLogin);
			User persistedUser =  userRepository.save(user);
			
			//Intercommunication with OTP service.
			
		}
		throw new EcommerceException("User already registerd. Please login.");
	}

	private boolean isUserExists(final String mobileNo) {
		return userRepository.findByUserId(mobileNo);
	}
	
	private String generateOTpforRegistration(final User user) {
		
		OtpRequest request = new OtpRequest();
		request.setMobileNo(user.getMobileNo());
		
		WebClient.create("http://localhost:8080/sms").post()
		.body(request, OtpRequest.class)
		.accept(MediaType.APPLICATION_JSON)
		.exchange().doOnSuccess(onSuccess).bodyToMono(String.class)
		.block().;

		
		return null;
		
		
	}
	
}
