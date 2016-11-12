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
import com.google.api.client.util.Base64;
import com.google.api.client.util.ByteStreams;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class GoogleOTPGenerator {

	@Autowired
	private HttpServletRequest request;

	private DatabaseReference usersRef;

	public GoogleOTPGenerator() {
		try {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setServiceAccount(new FileInputStream(System.getProperty("user.dir") + "/google-services.json"))
					.setDatabaseUrl("https://requireris-6348a.firebaseio.com")
					.build();
			FirebaseApp.initializeApp(options);
			FirebaseDatabase database = FirebaseDatabase.getInstance();
			DatabaseReference ref = database.getReference("/");
			usersRef = ref.child("users");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private String getId(HttpServletRequest request) {
		String id = null;
		for (Cookie c : request.getCookies())
			if (c.getName().equals("Requireris"))
				id = new String(Base64.decodeBase64 (c.getValue()));
		return id;
	}

    @RequestMapping(value = "/generate/{moduleType}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String generateGoogleOTP(@RequestParam(name = "key", required = false) String key, @PathVariable String moduleType) throws InterruptedException {
		String password = null;
		String id = getId(request);
        Authentication auth = new Authentication();

		if ((key == null || key.equals("")) && id != null) {
			final Semaphore semaphore = new Semaphore(0);
			final String infos[] = new String[1];
			infos[0] = null;
			usersRef.orderByChild("id").equalTo(id).addChildEventListener(new ChildEventListener() {
				@Override
				public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
					try {
						HashMap<String, String> map = (HashMap) dataSnapshot.getValue();
						String key = map.get(moduleType + "Key");
						if (key != null) {
							infos[0] = new String(Base64.decodeBase64(key));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					semaphore.release();
				}
				@Override
				public void onChildChanged(DataSnapshot dataSnapshot, String s) {semaphore.release();}
				@Override
				public void onChildRemoved(DataSnapshot dataSnapshot) 			{semaphore.release();}
				@Override
				public void onChildMoved(DataSnapshot dataSnapshot, String s) 	{semaphore.release();}
				@Override
				public void onCancelled(DatabaseError databaseError) 			{semaphore.release();}
			});
			Runnable run = () -> {try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();} semaphore.release();}; new Thread(run).start();
			semaphore.acquire();
			key = infos[0];
		}

        try {
            if (key == null || key.equals("")) {
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

		Map<String, Object> update = new HashMap<>();
		update.put(String.valueOf(userinfo.getId()) + "/id", String.valueOf(userinfo.getId()));
		update.put(String.valueOf(userinfo.getId()) + "/name", userinfo.getGivenName() + " " + userinfo.getFamilyName());
		update.put(String.valueOf(userinfo.getId()) + "/key", state);

		//synchronize upload
		final Semaphore semaphore = new Semaphore(0);
		final String message[] = new String[1];
		usersRef.updateChildren(update, (databaseError, databaseReference) -> {
			if (databaseError == null) {
				message[0] = "<div style='padding: 20px; background-color: #4CAF50; color: white; margin-bottom: 15px;'><p style='text-align: center;'><strong>Success !</strong> You can close the tab.</div>";
			} else {
				message[0] = "<div style='padding: 20px; background-color: red; color: white; margin-bottom: 15px;'><p style='text-align: center;'><strong>Error with firebase.</strong> You can close the tab</div>";
			}
			semaphore.release();
		});
		semaphore.acquire();

		return message[0];
	}

	@RequestMapping(value = "/getAuthorizeData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAuthorize(@RequestParam(value = "key") String key) throws JSONException, InterruptedException {
		final Semaphore semaphore = new Semaphore(0);
		final String infos[] = new String[2];
		infos[0] = null;
		infos[1] = null;
		usersRef.orderByChild("key").equalTo(key).addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
				HashMap<String, String> map = (HashMap) dataSnapshot.getValue();
				try {
					infos[0] = map.get("id");
					infos[1] = map.get("name");
				} catch (Exception e) {
					infos[0] = null;
					infos[1] = null;
 				}
				semaphore.release();
			}
			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {semaphore.release();}
			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) 			{semaphore.release();}
			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) 	{semaphore.release();}
			@Override
			public void onCancelled(DatabaseError databaseError) 			{semaphore.release();}
		});
		Runnable run = () -> {try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();} semaphore.release();}; new Thread(run).start();
		semaphore.acquire();

		if (infos[0] != null && infos[1] != null) {
			Map<String, Object> update = new HashMap<>();
			update.put(String.valueOf(infos[0]) + "/key", null);
			usersRef.updateChildren(update, (databaseError, databaseReference) -> semaphore.release());
			semaphore.acquire();

			JSONObject info = new JSONObject();
			info.put("id", Base64.encodeBase64String(infos[0].getBytes()));
			info.put("name", infos[1]);
			return info.toString();
		}
		JSONObject err = new JSONObject();
		err.put("error", "Error.");
		return err.toString();
	}

	@RequestMapping(value = "/updateKey/{moduleType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String updateKey(@RequestParam(value = "key") String key, @PathVariable String moduleType) throws JSONException, InterruptedException {
		String id = getId(request);
		if (id == null)
			return "Unable to update. Please log in.";

		final String message[] = new String[1];
		final Semaphore semaphore = new Semaphore(0);
		Map<String, Object> update = new HashMap<>();
		update.put(String.valueOf(id) + "/" + moduleType + "Key", Base64.encodeBase64String(key.getBytes()));
		usersRef.updateChildren(update, (databaseError, databaseReference) -> {
			message[0] = (databaseError == null ? "Key updated." : "Error with firebase. Key not updated.");
			semaphore.release();
		});
		semaphore.acquire();

		return message[0];
	}
}