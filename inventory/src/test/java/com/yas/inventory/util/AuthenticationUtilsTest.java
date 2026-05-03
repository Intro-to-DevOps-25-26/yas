package com.yas.inventory.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.inventory.utils.AuthenticationUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    @Test
    void extractUserId_WhenAuthenticated_ShouldReturnSubject() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            JwtAuthenticationToken authentication = mock(JwtAuthenticationToken.class);
            Jwt jwt = mock(Jwt.class);
            
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getToken()).thenReturn(jwt);
            when(jwt.getSubject()).thenReturn("user-123");

            String userId = AuthenticationUtils.extractUserId();

            assertThat(userId).isEqualTo("user-123");
        }
    }

    @Test
    void extractUserId_WhenAnonymous_ShouldThrowException() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            AnonymousAuthenticationToken authentication = mock(AnonymousAuthenticationToken.class);
            
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            assertThrows(AccessDeniedException.class, AuthenticationUtils::extractUserId);
        }
    }

    @Test
    void extractJwt_ShouldReturnTokenValue() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            Jwt jwt = mock(Jwt.class);
            
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwt.getTokenValue()).thenReturn("jwt-token");

            String jwtToken = AuthenticationUtils.extractJwt();

            assertThat(jwtToken).isEqualTo("jwt-token");
        }
    }
}
