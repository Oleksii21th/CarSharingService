package carsharing.carsharingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import carsharing.carsharingservice.exception.UserAlreadyExistsRegistrationException;
import carsharing.carsharingservice.exception.notfound.UserNotFoundException;
import carsharing.carsharingservice.mapper.UserMapper;
import carsharing.carsharingservice.model.Role;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRegistrationRequestDto userDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setRole(Role.CUSTOMER);
        user.setPassword("Test123");

        userDto = new UserRegistrationRequestDto();
        userDto.setEmail("updated@test.com");
        userDto.setFirstName("UpdatedTest");
        userDto.setLastName("UpdatedTest");
        userDto.setPassword("Test123");
        userDto.setRepeatPassword("Test123");

        userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("updated2@test.com");
        userResponseDto.setFirstName("UpdatedTest2");
        userResponseDto.setLastName("UpdatedTest2");
    }

    @Test
    @DisplayName("Updates profile with valid data and returns updated profile")
    void updateProfile_ValidDto_ReturnsUpdatedProfile() {
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByEmail(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("Test123");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto updated = userService.updateProfile(authentication, userDto);

        assertThat(updated.getEmail()).isEqualTo(userResponseDto.getEmail());
        assertThat(updated.getFirstName()).isEqualTo(userResponseDto.getFirstName());
        assertThat(updated.getLastName()).isEqualTo(userResponseDto.getLastName());
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(userRepository).findByEmail("user@test.com");
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Throws UserNotFoundException when updating profile of non-existing user")
    void updateProfile_NonExistingUser_ThrowsException() {
        when(authentication.getName()).thenReturn("1@test.com");
        when(userRepository.findByEmail("1@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateProfile(authentication, userDto));

        verify(userRepository).findByEmail("1@test.com");
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("Registers user with valid data and returns saved user DTO")
    void registerUser_ValidDto_ReturnsSavedUser() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toModel(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("Test123");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.registerUser(userDto);

        assertThat(result).isEqualTo(userResponseDto);
        verify(userRepository).findByEmail(userDto.getEmail());
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Throws UserAlreadyExistsRegistrationException when registering with existing email")
    void registerUser_EmailExists_ThrowsException() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsRegistrationException.class,
                () -> userService.registerUser(userDto));

        verify(userRepository).findByEmail(userDto.getEmail());
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("Updates user role with valid role and returns updated role DTO")
    void updateUserRole_ValidRole_ReturnsUpdatedRole() {
        RoleUpdateRequestDto roleUpdate = new RoleUpdateRequestDto("MANAGER");
        UserWithRoleResponseDto roleResponseDto = new UserWithRoleResponseDto();
        roleResponseDto.setRole("MANAGER");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDtoOnlyWithUpdatingRole(user)).thenReturn(roleResponseDto);

        UserWithRoleResponseDto updated = userService.updateUserRole(user.getId(), roleUpdate);

        assertThat(updated.getRole()).isEqualTo("MANAGER");
        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
        verify(userMapper).toDtoOnlyWithUpdatingRole(user);
    }

    @Test
    @DisplayName("Throws UserNotFoundException when updating role of non-existing user")
    void updateUserRole_NonExistingUser_ThrowsException() {
        RoleUpdateRequestDto roleUpdate = new RoleUpdateRequestDto("MANAGER");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserRole(99L, roleUpdate));

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}
