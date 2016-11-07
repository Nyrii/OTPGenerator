package com.requireris.app.web.rest;

/**
 * Created by noboud_n on 06/11/2016.
 */

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class googleOTPGenerator {
    @RequestMapping(value = "/{moduleType}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String generateGoogleOTP(@RequestParam("key") String key,
                                            @PathVariable String moduleType) {
        String password = null;
        Authentication auth = new Authentication();

        try {
            if (key == null || key == "") {
                return new String("Generation of password failed : empty key.");
            }
            password = auth.GoogleAuthenticatorCode(key);
        } catch (Exception e) {
            e.printStackTrace();
            return new String("Generation of password failed. Please verify your secret key.");
        }
        if (password == null) {
            return new String("Generation of password failed : invalid password. Please try again later.");
        }
        return new String(password);
    }

}
