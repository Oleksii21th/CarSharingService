package carsharing.carsharingservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import carsharing.carsharingservice.dto.user.RoleUpdateRequestDto;
import carsharing.carsharingservice.dto.user.UserRegistrationRequestDto;
import carsharing.carsharingservice.dto.user.UserResponseDto;
import carsharing.carsharingservice.dto.user.UserWithRoleResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends AbstractControllerTest {

    static {
        removeAllSqlFilePaths = List.of(
                "database/user/remove-all-users.sql"
        );

        insertDefaultSqlFilePaths = List.of(
                "database/user/add-default-users.sql"
        );
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"CUSTOMER"})
    void getMyProfile_AuthenticatedUser_ReturnsProfile() throws Exception {
        MvcResult result = mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto profile = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        assertThat(profile.getEmail()).isEqualTo("user@test.com");
        assertThat(profile.getFirstName()).isEqualTo("Test");
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"CUSTOMER"})
    void updateMyProfile_ValidRequestDto_ReturnsUpdatedProfile() throws Exception {
        UserRegistrationRequestDto updateDto = new UserRegistrationRequestDto();
        updateDto.setEmail("updated@test.com");
        updateDto.setFirstName("UpdatedTest");
        updateDto.setLastName("UpdatedTest");
        updateDto.setPassword("Test123");
        updateDto.setRepeatPassword("Test123");

        String json = objectMapper.writeValueAsString(updateDto);

        MvcResult result = mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto updated = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponseDto.class
        );

        assertThat(updated.getEmail()).isEqualTo("updated@test.com");
        assertThat(updated.getFirstName()).isEqualTo("UpdatedTest");
    }

    @Test
    @WithMockUser(username = "manager@test.com", roles = {"MANAGER"})
    void updateUserRole_ByManager_UpdatesRole() throws Exception {
        RoleUpdateRequestDto roleUpdate = new RoleUpdateRequestDto("MANAGER");

        String json = objectMapper.writeValueAsString(roleUpdate);

        MvcResult result = mockMvc.perform(put("/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        UserWithRoleResponseDto updated = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserWithRoleResponseDto.class
        );

        assertThat(updated.getRole()).isEqualTo("MANAGER");
    }
}
