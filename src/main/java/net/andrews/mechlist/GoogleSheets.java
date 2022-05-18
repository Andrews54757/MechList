package net.andrews.mechlist;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GoogleSheets {

	public static LocalServerReceiver localServerReceiver;
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	/**
	 * Global instance of the scopes required by this quickstart.
	 * If modifying these scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

	private static boolean isValidCredential(Credential credential) {
		return credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() == null || credential.getExpiresInSeconds() > 60);
	}

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, Consumer<String> authMessageCallback) throws IOException {

		BufferedReader credentialsStream = Files.newBufferedReader(ConfigFiles.CREDENTIALS.getPath());
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, credentialsStream);

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
			HTTP_TRANSPORT,
			JSON_FACTORY,
			clientSecrets,
			SCOPES
		).setDataStoreFactory(new FileDataStoreFactory(ConfigFiles.TOKENS.getPath().toFile()))
		 .setAccessType("offline")
		 .build();

		if (localServerReceiver != null) {
			localServerReceiver.stop();
		}

		localServerReceiver = new LocalServerReceiver.Builder().setPort(8888).build();

		try {
			Credential credential = flow.loadCredential(MechList.USER_ID);
			if (isValidCredential(credential)) {
				return credential;
			}

			String redirectUri = localServerReceiver.getRedirectUri();
			AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
			String url = authorizationUrl.build();
			authMessageCallback.accept(url);

			String code = localServerReceiver.waitForCode();

			if (code == null) {
				throw new IOException("Failed to authenticate");
			}

			TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

			return flow.createAndStoreCredential(response, MechList.USER_ID);
		} finally {
			localServerReceiver.stop();
		}
	}

	public static Set<String> getUsernames(Consumer<String> authMessageCallback) throws IOException, GeneralSecurityException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		final String spreadsheetId = MechList.getConfig().spreadsheetId();
		final String range = MechList.getConfig().sheetRange();

		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, authMessageCallback))
			.setApplicationName(MechList.USER_ID)
			.build();

		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();

		List<List<Object>> values = response.getValues();

		if (values == null || values.isEmpty()) {
			return null;
		} else {
			return values.stream()
				.filter(e -> !e.isEmpty())
				.map(row -> (String) row.get(0))
				.map(String::toLowerCase)
				.collect(Collectors.toSet());
		}
	}

	public static void reset() {
		try {
			if (localServerReceiver != null) {
				localServerReceiver.stop();
				localServerReceiver = null;
			}
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}
}
