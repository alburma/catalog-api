package works.brm.catalog.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import works.brm.catalog.domain.User;
import works.brm.catalog.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = users.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder()
            .username(u.getUsername())
            .password(u.getPasswordHash())
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())))
            .disabled(!u.getEnabled())
            .build();
    }
}
