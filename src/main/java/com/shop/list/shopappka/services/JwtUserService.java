package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserNotFoundException;
import com.shop.list.shopappka.models.domain.UserEntity;
import com.shop.list.shopappka.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@Slf4j
public class JwtUserService implements UserDetailsService {

    private final UserRepository userRepository;

    public JwtUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        UserEntity userDetails = userRepository.findUserByUsernameOrEmail(usernameOrEmail).orElseThrow(() -> {
            log.error("User not found with provided name: {}", usernameOrEmail);
            throw new UserNotFoundException("User not found with provided name: " + usernameOrEmail);
        });
        return new User(userDetails.getUsername(), userDetails.getPassword(), mapRolesToAuthorities(userDetails.getRole()));
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(String role) {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }
}
