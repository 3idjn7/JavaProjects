import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Configure users and roles (Authentication)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("USER")
                .and()
                .withUser("admin").password("{noop}admin").roles("USER", "ADMIN");
    }

    // Configure URL-based access control (Authorization)
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/public/**").permitAll() // Publicly accessible URLs
                .antMatchers("/admin/**").hasRole("ADMIN") // URLs restricted to ADMIN role
                .antMatchers("/user/**").hasRole("USER") // URLs restricted to USER role
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login") // Custom login page URL
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .httpBasic(); // Enable basic authentication
    }
}
