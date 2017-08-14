package cat.udl.eps.switchconfiguration.config;

import cat.udl.eps.switchconfiguration.domain.DataUser;
import cat.udl.eps.switchconfiguration.domain.User;
import cat.udl.eps.switchconfiguration.repository.UserRepository;
import cat.udl.eps.switchconfiguration.service.BasicUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@Profile("!Test")
public class AuthenticationConfig extends GlobalAuthenticationConfigurerAdapter {

    @Autowired Environment environment;
    @Autowired BasicUserDetailsService userDetailsService;
    @Autowired UserRepository userRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
        auth.inMemoryAuthentication()
                .passwordEncoder(new BCryptPasswordEncoder())
                .withUser("admin")
                .password("$2a$10$B1dcscvS/lgiBnGdkhhupew8AhbjqUL7TjdA2ggvxQhs5jN7KVSMC")
                .roles("ADMIN");

        // Use encrypted secret password when deploying publicly in Heroku
        if(environment.acceptsProfiles("heroku")) {
            User owner = new DataUser();
            owner.setUsername("owner");
            owner.setPassword("$2a$10$B1dcscvS/lgiBnGdkhhupew8AhbjqUL7TjdA2ggvxQhs5jN7KVSMC");
            userRepository.save(owner);

            User user = new DataUser();
            user.setUsername("user");
            user.setPassword("$2a$10$B1dcscvS/lgiBnGdkhhupew8AhbjqUL7TjdA2ggvxQhs5jN7KVSMC");
            userRepository.save(user);
        } else {
            User owner = new DataUser();
            owner.setUsername("owner");
            owner.setPassword(new BCryptPasswordEncoder().encode("password"));
            userRepository.save(owner);

            User user = new DataUser();
            user.setUsername("user");
            user.setPassword(new BCryptPasswordEncoder().encode("password"));
            userRepository.save(user);
        }
    }
}