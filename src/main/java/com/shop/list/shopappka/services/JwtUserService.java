package com.shop.list.shopappka.services;

import com.shop.list.shopappka.exceptions.UserNotFoundException;
import com.shop.list.shopappka.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        return userRepository.findUserByUsernameOrEmail(usernameOrEmail).orElseThrow(() -> {
            log.error("User not found with provided name: {}", usernameOrEmail);
            throw new UserNotFoundException("User not found with provided name: " + usernameOrEmail);
        });
    }
}
