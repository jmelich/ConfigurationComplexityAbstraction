package cat.udl.eps.switchconfiguration.service;

import cat.udl.eps.switchconfiguration.domain.User;
import cat.udl.eps.switchconfiguration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class BasicUserDetailsService implements UserDetailsService {

    @Autowired private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findOne(username);
        if (user == null)
            throw new UsernameNotFoundException("User " + username + " not found");
        return user;
    }
}
