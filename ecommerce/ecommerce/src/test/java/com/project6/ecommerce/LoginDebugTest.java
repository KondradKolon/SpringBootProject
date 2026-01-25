package com.project6.ecommerce;

import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
public class LoginDebugTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void debugLoginProcess() throws Exception {
        System.out.println("=== START DEBUGGING LOGIN ===");
        
        // 1. Sprawdź, czy użytkownik istnieje w bazie (zaadowany z data.sql)
        String username = "admin123";
        User user = userRepository.findByName(username).orElse(null);
        
        if (user == null) {
            System.err.println("❌ Critical Error: User '" + username + "' not found in database! Check data.sql.");
            return;
        }
        
        System.out.println("✅ User found in DB: " + user.getName());
        System.out.println("   Email: " + user.getEmail());
        System.out.println("   Stored Hash: " + user.getPassword());
        System.out.println("   Role: " + user.getRole());

        // 2. Sprawdź hasło
        String[] passwordsToTest = {"123", "admin123", "admin", "password"};
        boolean passwordMatched = false;
        String validPassword = null;

        for (String candidate : passwordsToTest) {
            boolean matches = passwordEncoder.matches(candidate, user.getPassword());
            System.out.println("   Checking password '" + candidate + "': " + (matches ? "MATCHED! ✅" : "Invalid ❌"));
            if (matches) {
                passwordMatched = true;
                validPassword = candidate;
            }
        }

        if (!passwordMatched) {
            System.err.println("❌ None of the tested passwords match the stored hash!");
            System.err.println("   Please update data.sql with a known hash.");
        } else {
             System.out.println("✅ Found valid password: '" + validPassword + "'");
             
             // 3. Próba logowania przez MockMvc
             System.out.println("   Attempting MockMvc login with '" + username + "' / '" + validPassword + "'...");
             mockMvc.perform(formLogin().user(username).password(validPassword))
                     .andExpect(status().is3xxRedirection())
                     .andDo(result -> System.out.println("   Login Result URL: " + result.getResponse().getRedirectedUrl()));
             System.out.println("✅ Login via MockMvc succeeded (Redirected)!");
        }

        System.out.println("=== END DEBUGGING LOGIN ===");
    }
}
