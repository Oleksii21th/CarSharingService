package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import carsharing.carsharingservice.exception.UserAlreadyExistsRegistrationException;
import carsharing.carsharingservice.exception.UserNotFoundException;
import carsharing.carsharingservice.mapper.UserMapper;
import carsharing.carsharingservice.model.Role;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Role DEFAULT_ROLE = Role.CUSTOMER;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(UserMapper userMapper,
                           BCryptPasswordEncoder passwordEncoder,
                           UserRepository userRepository) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserWithRoleResponseDto updateUserRole(Long userId,
                                                  RoleUpdateRequestDto updatedRole) {
        User user = getUserById(userId);
        user.setRole(Role.valueOf(updatedRole.role()));
        userRepository.save(user);
        return userMapper.toDtoOnlyWithUpdatingRole(user);
    }

    @Override
    public UserResponseDto getProfile(Authentication authentication) {
        return userMapper.toDto(getCurrentUser(authentication));
    }

    @Override
    public UserResponseDto updateProfile(Authentication authentication,
                                         UserRegistrationRequestDto userDto) {
        User user = getCurrentUser(authentication);
        user.setEmail(userDto.getEmail());
        user.setLastName(userDto.getLastName());
        user.setFirstName(userDto.getFirstName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsRegistrationException(userDto.getEmail());
        }

        User user = userMapper.toModel(userDto);
        user.setRole(DEFAULT_ROLE);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    private User getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return getUserById(user.getId());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
