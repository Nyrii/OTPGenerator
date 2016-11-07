package com.requireris.app.web.rest;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Created by wilmot_g on 07/11/16.
 */
public class GoogleFlow {
	public static GoogleAuthorizationCodeFlow getFlow() throws GeneralSecurityException, IOException {
		GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
		GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
		details.setClientId(Constants.clientId);
		details.setClientSecret(Constants.clientSecret);
		clientSecrets.setInstalled(details);

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets,
				Collections.singleton(DriveScopes.DRIVE))
				.setAccessType("offline")
				.build();

		return flow;
	}
}
