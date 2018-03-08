package leamon.erp.updater;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class UpdaterTool {

	private static final String DIR_FOR_DOWNLOADS = "Enter Download Directory";

	/** Application name. */
	private static final String APPLICATION_NAME =
			"Leamon-ERP Updater Tool";

	/** Global Drive API client. */
	private static Drive service;

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".credentials/UpdateTool");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY =
			JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials
	 * at ~/.credentials/drive-java-quickstart
	 */
	/*private static final Collection<String> SCOPES = DriveScopes.all();*/
	private static final List<String> SCOPES =
			Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in =
				UpdaterTool.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets =
				GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println(
					"Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
							+ "into drive-cmdline-sample/src/main/resources/client_secrets.json");
			System.exit(1);
		}

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow =
				new GoogleAuthorizationCodeFlow.Builder(
						HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				// .setAccessType("offline")
				.build();

		Credential credential = new AuthorizationCodeInstalledApp(
				flow, new LocalServerReceiver()).authorize("user");
		System.out.println(
				"Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	public static Drive getDriveService() throws IOException {
		Credential credential = authorize();
		return new Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}


	public static void main(String[] args) {
		//String fileID = "1qU8304IIcLrmu73fgBtbO_qXCzp1UxN2"; //DriveSample.txt
		//https://drive.google.com/file/d/1xT7joNfqGqzD0NzqH9HFfoyk2YbjCeh3/view?usp=sharing
		String fileID = "1xT7joNfqGqzD0NzqH9HFfoyk2YbjCeh3"; //report.zip
		try {
			service = getDriveService();
			UpdaterTool tool = new UpdaterTool();
			tool.downloadFileByID(fileID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public  void downloadFileByID(String fileId) throws IOException{
		File file=service.files().get(fileId).execute();
		System.out.println(downloadTextFile(file));
	}
	
	/**
	 * Get the content of a file.
	 *
	 * @param File to get the content.
	 * @return String content of the file.
	 */
	public String downloadTextFile(File file) throws IOException{
		GenericUrl url = new GenericUrl(file.getDownloadUrl());
		HttpResponse response = service.getRequestFactory().buildGetRequest(url).execute();
		try {
			downloadZip(response, file.getTitle());
			return new Scanner(response.getContent()).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}
	
	/**
	 * Get the content of a file.
	 *
	 * @param String the file ID.
	 * @return String content of the file.
	 */
	public String downloadTextFile(String fileID) throws IOException{
		File file=service.files().get(fileID).execute();
		return downloadTextFile(file);
	}
	
	
	
	public static void downloadZip(HttpResponse response, String fname) throws IOException{
		InputStream ins = response.getContent();

		java.io.File file = new java.io.File(fname);
		if(!file.exists()){
			file.createNewFile();
		}
		FileOutputStream fout = new FileOutputStream(file);
		int i = 0;
		while( (i=ins.read())!=-1){
			fout.write(i);
		}
		fout.flush();
		fout.close();
		ins.close();
		System.out.println("file has been downloaded.");
	}
}
