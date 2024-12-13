package JavaProgrammerZamanNow.Restfulapi.service;


import JavaProgrammerZamanNow.Restfulapi.entity.User;
import JavaProgrammerZamanNow.Restfulapi.model.RegisterUserRequest;
import JavaProgrammerZamanNow.Restfulapi.model.UpdateUserRequest;
import JavaProgrammerZamanNow.Restfulapi.model.UserResponse;
import JavaProgrammerZamanNow.Restfulapi.repository.UserRepository;
import JavaProgrammerZamanNow.Restfulapi.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ValidationService validationService;

	@Transactional
	public void register(RegisterUserRequest request) {

		validationService.validate(request);

		if (userRepository.existsById(request.getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
		user.setName(request.getName());

		userRepository.save(user);

	}

	public UserResponse get(User user) {
		return UserResponse.builder()
				.name(user.getName())
				.username(user.getUsername())
				.build();
	}

	@Transactional
	public UserResponse update(User user, UpdateUserRequest request) {
		validationService.validate(user);

		if (Objects.nonNull(request.getName())) {
			user.setName(request.getName());
		}

		if (Objects.nonNull(request.getPassword())) {
			user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
		}

		userRepository.save(user);

		return UserResponse.builder()
				.name(user.getName())
				.username(user.getUsername())
				.build();
	}


}
