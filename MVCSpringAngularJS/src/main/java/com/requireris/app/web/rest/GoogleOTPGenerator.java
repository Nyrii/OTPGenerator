package com.requireris.app.web.rest;

/**
 * Created by noboud_n on 06/11/2016.
 */

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class GoogleOTPGenerator {
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

	@RequestMapping(value = "/getAuthorize", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String generateAuthorizeUrl() throws Exception {
		GoogleAuthorizationCodeFlow flow = GoogleFlow.getFlow();

		String redirect = "http://localhost:8080/api/authorize";

		GoogleAuthorizationCodeRequestUrl authUrl = flow.newAuthorizationUrl()
				.setAccessType("offline")
				.setRedirectUri(redirect)
				.setResponseTypes(Collections.singletonList("code"));

		return authUrl.toString();
	}

	@RequestMapping(value = "/authorize", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String receiveAuthorize(@RequestParam(required = false) String code, @RequestParam(required = false) String error) throws Exception {
		if (error != null || code == null) {
			return "Error : " + (error != null ? error : "missing parameters");
		}

		GoogleAuthorizationCodeFlow flow = GoogleFlow.getFlow();

		String redirect = "http://localhost:8080/api/authorize";
//		String redirect = "http://" + request.getServerName() + request.getContextPath() + "/" + "/public/driveAuthorizeService";
		TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirect).execute();

		System.out.println(response.getRefreshToken());
		System.out.println(response.getAccessToken());
		return "Success";
	}
}
