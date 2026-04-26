package carsharing.carsharingservice.security;

import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AccessManager {

    private final UserRepository userRepository;

    public AccessManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isManager(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_MANAGER"));
    }

    public Long resolveUserId(Authentication authentication, Long resourceUserId) {
        if (resourceUserId != null) {
            return resourceUserId;
        }

        return isManager(authentication) ? null : getCurrentUserId(authentication);
    }

    public void checkOwnerOrManager(Authentication authentication, Long resourceUserId) {
        Long currentUserId = getCurrentUserId(authentication);

        boolean isOwner = resourceUserId != null && resourceUserId.equals(currentUserId);
        boolean isManager = isManager(authentication);

        if (!isOwner && !isManager) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        return user.getId();
    }
}
