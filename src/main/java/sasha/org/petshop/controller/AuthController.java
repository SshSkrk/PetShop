package sasha.org.petshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sasha.org.petshop.dto.CustomerDTO;
import sasha.org.petshop.dto.LoginRequestDTO;
import sasha.org.petshop.service.CustomerService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;

    public AuthController(AuthenticationManager authenticationManager, CustomerService customerService) {
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            //This line makes the session persist the login
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            CustomerDTO customerDTO = customerService.getCustomerByUsername(loginRequestDTO.getUsername());
            return ResponseEntity.ok(customerDTO);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate(); // clear session
        SecurityContextHolder.clearContext(); // clear security context
        return ResponseEntity.ok("Logged out successfully.");
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        CustomerDTO customerDTO = customerService.getCustomerByUsername(authentication.getName());
        if (customerDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Return full customer info
        Map<String, Object> response = new HashMap<>();
        response.put("id", customerDTO.getId());
        response.put("username", customerDTO.getUsername());
        response.put("firstName", customerDTO.getFirstName());
        response.put("lastName", customerDTO.getLastName());
        response.put("role", customerDTO.getRole());

        return ResponseEntity.ok(response);
    }
}

