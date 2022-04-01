package ncu.cc.activedirectory.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ncu.cc.activedirectory.constants.Constants.LOCAL_API_PATH;

@RestController
@RequestMapping(LOCAL_API_PATH)
public class ApisLocalController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/encrypt/{password}")
    public String encrypt(@PathVariable("password") String password) {
        return passwordEncoder.encode(password);
    }
}
