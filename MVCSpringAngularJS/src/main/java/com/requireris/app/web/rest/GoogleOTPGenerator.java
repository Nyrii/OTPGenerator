package com.requireris.app.web.rest;

/**
 * Created by noboud_n on 06/11/2016.
 */

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class GoogleOTPGenerator {

	Map<String, JSONObject> infos = new ArrayMap<>();

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

    @RequestMapping(value = "/generate/{moduleType}",
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

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256);
		SecretKey secretKey = keyGen.generateKey();
		byte[] encoded = secretKey.getEncoded();

		GoogleAuthorizationCodeRequestUrl authUrl = flow.newAuthorizationUrl()
				.setAccessType("offline")
				.setRedirectUri(redirect)
				.setState(new String(encoded))
				.setResponseTypes(Collections.singletonList("code"));

		JSONObject obj = new JSONObject();
		obj.put("url", authUrl.toString());
		obj.put("key", new String(encoded));

		return obj.toString();
	}

	@RequestMapping(value = "/authorize", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String receiveAuthorize(@RequestParam(value = "state", required = false) String state, @RequestParam(value = "code", required = false) String code, @RequestParam(value = "error", required = false) String error) throws Exception {
		if (error != null || code == null || state == null) {
			return "<div style='padding: 20px; background-color: red; color: white; margin-bottom: 15px;'><p style='text-align: center;'><strong>Error.</strong> You can now close the tab</p></div>";
		}

		GoogleAuthorizationCodeFlow flow = GoogleFlow.getFlow();

		String redirect = "http://" + request.getServerName() + request.getContextPath() + ":" + request.getLocalPort() + "/api/authorize";
		TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirect).execute();

		GoogleCredential credential = new GoogleCredential().setAccessToken(tokenResponse.getAccessToken());
		Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("Oauth2").build();
		Userinfoplus userinfo = oauth2.userinfo().get().execute();

		JSONObject user = new JSONObject();
		user.put("name", userinfo.getGivenName() + " " + userinfo.getFamilyName());
		user.put("id", userinfo.getId());
		user.put("refresh", tokenResponse.getRefreshToken());
		user.put("access", tokenResponse.getAccessToken());
		infos.put(state, user);

		return "<div style='padding: 20px; background-color: #4CAF50; color: white; margin-bottom: 15px;'><p style='text-align: center;'><strong>Success !</strong> You can now close the tab</p></div>";
	}

	@RequestMapping(value = "/getAuthorizeData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAuthorize(@RequestParam(value = "key") String key) throws JSONException {
		if (infos.containsKey(key)) {
			JSONObject info = infos.get(key);
			info.remove("access");
			info.remove("refresh");
			return info.toString();
		}
		JSONObject err = new JSONObject();
		err.put("error", "No such key");
		return err.toString();
	}
}