package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.model.Role;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

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
}
