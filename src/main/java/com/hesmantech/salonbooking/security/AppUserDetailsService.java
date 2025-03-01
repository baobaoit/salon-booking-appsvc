package com.hesmantech.salonbooking.security;

import com.hesmantech.salonbooking.repository.UserRepository;
import com.hesmantech.salonbooking.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new AppUser(user, SecurityUtils.buildGrantedAuthorities(user)))
                .map(UserDetails.class::cast)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found!"));
    }
}
