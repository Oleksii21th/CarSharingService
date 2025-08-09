package carsharing.carsharingservice.service;

import carsharing.carsharingservice.model.User;

public interface UserService {
    User changeRole(Long id, String newRole);

    User getProfile();

    User updateProfile(User update);
}
