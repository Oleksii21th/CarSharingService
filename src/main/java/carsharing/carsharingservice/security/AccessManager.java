package carsharing.carsharingservice.security;

import carsharing.carsharingservice.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AccessManager {
    private boolean isManager(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_MANAGER"));
    }

    public Long resolveUserId(Authentication authentication, Long resourceUserId) {
        User currentUser = (User) authentication.getPrincipal();
        return resourceUserId != null ? resourceUserId : currentUser.getId();
    }

    public void checkOwnerOrManager(Authentication authentication, Long resourceUserId) {
        User currentUser = (User) authentication.getPrincipal();

        boolean isOwner = resourceUserId.equals(currentUser.getId());
        boolean isManager = isManager(authentication);

        if (!isOwner && !isManager) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
