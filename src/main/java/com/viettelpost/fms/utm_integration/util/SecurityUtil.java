package com.viettelpost.fms.utm_integration.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtil {

    /**
     * Get the current logged in  user
     *
     * @return current username if user has logged. Otherwise return {@link Optional#empty()}
     */
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return Optional.empty();
        }

        // Resolve username depend on type of principal.
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            return Optional.of(principal.toString());
        } else {
            return Optional.empty();
        }
    }
}
