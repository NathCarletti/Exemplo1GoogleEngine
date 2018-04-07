package br.com.caroltr.gae_exemplo1.Util;

import br.com.caroltr.gae_exemplo1.Exception.UserAlreadyExistsException;
import br.com.caroltr.gae_exemplo1.Exception.UserNotFoundException;
import br.com.caroltr.gae_exemplo1.Repository.UserRepository;
import br.com.caroltr.gae_exemplo1.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;



public class CheckRole {

    private CheckRole(){}

    public static boolean hasRoleAdmin (Authentication authentication) {
        return hasRole(authentication, "ADMIN");
    }

    public static boolean hasRoleUser (Authentication authentication) {
        return hasRole(authentication, "USER");
    }

    private static boolean hasRole (Authentication authentication, String role) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
