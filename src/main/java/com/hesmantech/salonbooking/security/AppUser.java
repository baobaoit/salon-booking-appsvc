package com.hesmantech.salonbooking.security;

import com.hesmantech.salonbooking.domain.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.util.Collection;
import java.util.Optional;

public class AppUser extends User {
    @Serial
    private static final long serialVersionUID = -7268336116401171118L;

    public AppUser(UserEntity user, Collection<? extends GrantedAuthority> authorities) {
        super(Optional.ofNullable(user.getUserId()).orElseGet(user::getPhoneNumber), user.getPassword(), true, true, true, true, authorities);
    }
}
