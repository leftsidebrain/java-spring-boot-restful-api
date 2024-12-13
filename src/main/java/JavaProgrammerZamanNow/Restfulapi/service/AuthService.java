package JavaProgrammerZamanNow.Restfulapi.service;


import JavaProgrammerZamanNow.Restfulapi.entity.User;
import JavaProgrammerZamanNow.Restfulapi.model.LoginUserRequest;
import JavaProgrammerZamanNow.Restfulapi.model.TokenResponse;
import JavaProgrammerZamanNow.Restfulapi.repository.UserRepository;
import JavaProgrammerZamanNow.Restfulapi.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ValidationService validationService;

	public TokenResponse login(LoginUserRequest request) {
		validationService.validate(request);

		User user = userRepository.findById(request.getUsername())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
		if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
			user.setToken(UUID.randomUUID().toString());
			user.setTokenExpiredAt(getExpiredAt());
			userRepository.save(user);
			return TokenResponse.builder()
					.token(user.getToken())
					.expiredAt(user.getTokenExpiredAt())
					.build();
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
		}

	}

	;

	private Long getExpiredAt() {
		return System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7);
	}

	@Transactional
	public void logout(User user) {
		user.setToken(null);
		user.setTokenExpiredAt(null);
		userRepository.save(user);
	}
}
