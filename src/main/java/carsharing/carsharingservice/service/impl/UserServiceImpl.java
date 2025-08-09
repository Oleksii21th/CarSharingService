package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User changeRole(Long id, String newRole) {
        return null;
    }

    @Override
    public User getProfile() {
        return null;
    }

    @Override
    public User updateProfile(User update) {
        return null;
    }
}
