package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.model.Role;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/role")
    public User updateUserRole(@PathVariable Long id, @RequestBody Role role) {
        return userService.updateUserRole(id, role);
    }

    @GetMapping("/me")
    public User getMyProfile() {
        return userService.getProfile();
    }

    @PatchMapping("/me")
    public User updateMyProfile(@RequestBody User user) {
        return userService.updateProfile(user);
    }
}
