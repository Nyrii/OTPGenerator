package com.requireris.app.web.rest;

/**
 * Created by noboud_n on 06/11/2016.
 */

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.api.services.plus.Plus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class GoogleOTPGenerator {

	@Autowired
	private HttpServletRequest request;

    @RequestMapping(value = "/{moduleType}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String generateGoogleOTP(@RequestParam("key") String key,
                                            @PathVariable String moduleType) {
        String password = null;
        Authentication auth = new Authentication();

        try {
            if (key == null || key == "") {
                return "Generation of password failed : empty key.";
            }
            password = auth.GoogleAuthenticatorCode(key);
        } catch (Exception e) {
            e.printStackTrace();
            return "Generation of password failed. Please verify your secret key.";
        }
        if (password == null) {
            return "Generation of password failed : invalid password. Please try again later.";
        }
        return password;
    }

	@RequestMapping(value = "/getAuthorize", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String generateAuthorizeUrl() throws Exception {
		GoogleAuthorizationCodeFlow flow = GoogleFlow.getFlow();

		String redirect = "http://" + request.getServerName() + request.getContextPath() + ":" + request.getLocalPort() + "/api/authorize";

		GoogleAuthorizationCodeRequestUrl authUrl = flow.newAuthorizationUrl()
				.setAccessType("offline")
				.setRedirectUri(redirect)
				.setResponseTypes(Collections.singletonList("code"));

		return authUrl.toString();
	}

	@RequestMapping(value = "/authorize", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String receiveAuthorize(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "error", required = false) String error) throws Exception {
		if (error != null || code == null) {
			return "Error : " + (error != null ? error : "missing parameters");
		}

		GoogleAuthorizationCodeFlow flow = GoogleFlow.getFlow();

		String redirect = "http://" + request.getServerName() + request.getContextPath() + ":" + request.getLocalPort() + "/api/authorize";
		TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirect).execute();

		System.out.println(response.getRefreshToken());
		System.out.println(response.getAccessToken());

		GoogleCredential credential = new GoogleCredential().setAccessToken(response.getAccessToken());
		Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("Oauth2").build();
		Userinfoplus userinfo = oauth2.userinfo().get().execute();
//		userinfo.getEmail();
		userinfo.getGivenName();
//		userinfo.getId();

		return "<div style='padding: 20px; background-color: #4CAF50; color: white; margin-bottom: 15px;'><p style='text-align: center;'><strong>Success !</strong> You can now close the tab</p></div>";
	}
}