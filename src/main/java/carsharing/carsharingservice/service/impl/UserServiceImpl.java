package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.exception.RegistrationException;
import carsharing.carsharingservice.mapper.UserMapper;
import carsharing.carsharingservice.model.Role;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Role DEFAULT_ROLE = Role.CUSTOMER;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public User updateUserRole(Long id, Role role) {
        return null;
    }

    @Override
    public User getProfile() {
        return null;
    }

    @Override
    public User updateProfile(User user) {
        return null;
    }

    public UserResponseDto registerUser(UserRegistrationRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException(String.format("The user with email %s already exists",
                    request.getEmail()));
        }
        User user = userMapper.toModel(request);
        user.setRole(DEFAULT_ROLE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
