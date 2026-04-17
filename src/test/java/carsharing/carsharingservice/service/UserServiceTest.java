package carsharing.carsharingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserUpdateRequestDto;
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
    private UserUpdateRequestDto updateDto;
    private UserRegistrationRequestDto registrationDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setRole(Role.CUSTOMER);
        user.setPassword("encodedPassword");

        updateDto = new UserUpdateRequestDto(
                "updated@test.com",
                "UpdatedFirst",
                "UpdatedLast"
        );

        registrationDto = new UserRegistrationRequestDto(
                "new@test.com",
                "Test12345",
                "Test12345",
                "New",
                "User"
        );

        userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("updated@test.com");
        userResponseDto.setFirstName("UpdatedFirst");
        userResponseDto.setLastName("UpdatedLast");
    }

    @Test
    @DisplayName("Updates profile with valid data and returns updated profile")
    void updateProfile_ValidDto_ReturnsUpdatedProfile() {
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByEmail(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto updated = userService.updateProfile(authentication, updateDto);

        assertThat(updated.getEmail()).isEqualTo(userResponseDto.getEmail());
        assertThat(updated.getFirstName()).isEqualTo(userResponseDto.getFirstName());
        assertThat(updated.getLastName()).isEqualTo(userResponseDto.getLastName());

        verify(userRepository).findByEmail("user@test.com");
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Throws UserNotFoundException when updating profile of non-existing user")
    void updateProfile_NonExistingUser_ThrowsException() {
        when(authentication.getName()).thenReturn("1@test.com");
        when(userRepository.findByEmail("1@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateProfile(authentication, updateDto));

        verify(userRepository).findByEmail("1@test.com");
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("Updates all user fields and saves entity")
    void updateProfile_UpdatesAllFields() {
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        userService.updateProfile(authentication, updateDto);

        assertThat(user.getUsername()).isEqualTo(updateDto.email());
        assertThat(user.getFirstName()).isEqualTo(updateDto.firstName());
        assertThat(user.getLastName()).isEqualTo(updateDto.lastName());

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Registers user with valid data and returns saved user DTO")
    void registerUser_ValidDto_ReturnsSavedUser() {
        when(userRepository.findByEmail(registrationDto.email())).thenReturn(Optional.empty());
        when(userMapper.toModel(registrationDto)).thenReturn(user);
        when(passwordEncoder.encode(registrationDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.registerUser(registrationDto);

        assertThat(result).isEqualTo(userResponseDto);

        verify(userRepository).findByEmail(registrationDto.email());
        verify(passwordEncoder).encode(registrationDto.password());
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Throws UserAlreadyExistsRegistrationException "
            + "when registering with existing email")
    void registerUser_EmailExists_ThrowsException() {
        when(userRepository.findByEmail(registrationDto.email())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsRegistrationException.class,
                () -> userService.registerUser(registrationDto));

        verify(userRepository).findByEmail(registrationDto.email());
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("Set default role CUSTOMER during registration")
    void registerUser_SetsDefaultRole_OK() {
        when(userRepository.findByEmail(registrationDto.email()))
                .thenReturn(Optional.empty());
        when(userMapper.toModel(registrationDto)).thenReturn(user);
        when(passwordEncoder.encode(registrationDto.password()))
                .thenReturn("test");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        userService.registerUser(registrationDto);

        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    @DisplayName("Encodes password before saving user")
    void registerUser_EncodesPassword() {
        when(userRepository.findByEmail(registrationDto.email()))
                .thenReturn(Optional.empty());
        when(userMapper.toModel(registrationDto)).thenReturn(user);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("testPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        userService.registerUser(registrationDto);

        assertThat(user.getPassword()).isEqualTo("testPassword");

        verify(passwordEncoder).encode(registrationDto.password());
    }

    @Test
    @DisplayName("Does not save user when email already exists")
    void registerUser_EmailExists_NoSave() {
        when(userRepository.findByEmail(registrationDto.email()))
                .thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsRegistrationException.class,
                () -> userService.registerUser(registrationDto));

        verify(userRepository).findByEmail(registrationDto.email());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Updates user role with valid role and returns updated role DTO")
    void updateUserRole_ValidRole_ReturnsUpdatedRole() {
        UserWithRoleResponseDto roleResponseDto = new UserWithRoleResponseDto();
        roleResponseDto.setRole("MANAGER");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDtoOnlyWithUpdatingRole(user)).thenReturn(roleResponseDto);

        RoleUpdateRequestDto roleUpdate = new RoleUpdateRequestDto(Role.MANAGER);
        UserWithRoleResponseDto updated =
                userService.updateUserRole(user.getId(), roleUpdate);

        assertThat(updated.getRole()).isEqualTo("MANAGER");

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
        verify(userMapper).toDtoOnlyWithUpdatingRole(user);
    }

    @Test
    @DisplayName("Throws UserNotFoundException when updating role of non-existing user")
    void updateUserRole_NonExistingUser_ThrowsException() {
        RoleUpdateRequestDto roleUpdate = new RoleUpdateRequestDto(Role.MANAGER);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserRole(99L, roleUpdate));

        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Updates role on user entity before saving")
    void updateUserRole_ChangesRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDtoOnlyWithUpdatingRole(user))
                .thenReturn(new UserWithRoleResponseDto());

        RoleUpdateRequestDto dto = new RoleUpdateRequestDto(Role.MANAGER);

        userService.updateUserRole(1L, dto);

        assertThat(user.getRole()).isEqualTo(Role.MANAGER);
    }

    @Test
    @DisplayName("Saves user once when updating role")
    void updateUserRole_SavesOnce() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDtoOnlyWithUpdatingRole(user))
                .thenReturn(new UserWithRoleResponseDto());

        userService.updateUserRole(1L, new RoleUpdateRequestDto(Role.MANAGER));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Returns current user profile from Authentication")
    void getProfile_ValidUser_ReturnsDto() {
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getProfile(authentication);

        assertThat(result).isEqualTo(userResponseDto);

        verify(userRepository).findByEmail("user@test.com");
        verify(userMapper).toDto(user);
    }
}
