package ru.yegorsmirnov.testtask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yegorsmirnov.testtask.model.entity.Commentary;
import ru.yegorsmirnov.testtask.model.entity.Task;
import ru.yegorsmirnov.testtask.model.entity.User;
import ru.yegorsmirnov.testtask.model.dto.JwtAuthenticationResponse;
import ru.yegorsmirnov.testtask.model.dto.SignInRequest;
import ru.yegorsmirnov.testtask.model.dto.SignUpRequest;
import ru.yegorsmirnov.testtask.model.enums.Role;
import ru.yegorsmirnov.testtask.repository.CommentaryRepository;
import ru.yegorsmirnov.testtask.repository.TaskRepository;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TaskRepository taskRepository;
    private final CommentaryRepository commentaryRepository;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .isEnabled(true)
                .build();

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));

            var user = userService
                    .userDetailsService()
                    .loadUserByUsername(request.getUsername());

            var jwt = jwtService.generateToken(user);
            return new JwtAuthenticationResponse(jwt);
        } catch (Exception e) {
            throw new BadCredentialsException("Bad credentials");
        }
    }

}
