package org.example.apiblitz.service;

import lombok.extern.slf4j.Slf4j;
import org.example.apiblitz.config.SecurityConfig;
import org.example.apiblitz.model.UserResponse;
import org.example.apiblitz.model.UserSignIn;
import org.example.apiblitz.model.UserSignUp;
import org.example.apiblitz.repository.UserRepository;
import org.example.apiblitz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {

	@Autowired
	private SecurityConfig securityConfig;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	public UserResponse signUp(UserSignUp user) {

		boolean userExist = userRepository.findByEmail(user.getEmail());

		UserResponse userResponse = new UserResponse();
		Map<Object, Object> data = new HashMap<>();

		if (userExist) {
			userResponse.setError("Email address is already exist");
			return userResponse;
		}

		String hashPassword = securityConfig.passwordEncode(user.getPassword());
		user.setPassword(hashPassword);

		Integer id = userRepository.addToUserTableWhenSignUp(user);
		user.setId(id);
		user.setPassword(null);

		String token = jwtUtil.userSignUpGenerateToken(user);
		data.put("access_token", token);
		data.put("access_expired", 3600);
		data.put("user", user);
		userResponse.setData(data);
		return userResponse;
	}

	public UserResponse signIn(UserSignIn user) {

		UserResponse userResponse = new UserResponse();
		Map<Object, Object> data = new HashMap<>();

		boolean userExist = userRepository.findByEmail(user.getEmail());

		if (userExist) {
			UserSignIn userInfo = userRepository.getUserInfo(user.getEmail());
			boolean passwordMatches = securityConfig.passwordCheck(user.getPassword(), userInfo.getPassword());
			if (passwordMatches) {
				userInfo.setPassword(null);
				String token = jwtUtil.userSignInGenerateToken(userInfo);
				data.put("access_token", token);
				data.put("access_expired", 3600);
				data.put("user", userInfo);
				userResponse.setData(data);
			} else {
				userResponse.setError("Please confirm whether the entered information is correct");
			}
		} else {
			userResponse.setError("No such user here");
		}
		return userResponse;
	}
}
