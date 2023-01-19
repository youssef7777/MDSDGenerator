package de.arinir.mdsd.Generator.MDSDGenerator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SpringBootstrapGeneratorStep extends AbstractGeneratorStep {
	private static final String Spring_Initializr_URL = "https://start.spring.io";
	private static final String User_Agent = "Mozilla/5.0";
	
	public SpringBootstrapGeneratorStep(Generator generator) {
		super(generator);
	}

	@Override
	public void run() throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append(SpringBootstrapGeneratorStep.Spring_Initializr_URL);
		sb.append("/starter.zip?type=maven-project&language=java&bootVersion=2.2.2.RELEASE&baseDir=");
		sb.append(generator.getProjectName());
		sb.append("&groupId=").append(generator.getBasePackageName());
		sb.append("&artefactId=").append(generator.getProjectName());
		sb.append("&name=").append(generator.getProjectName());
		sb.append("&description=").append("Eine%20Beschreibung%20des%20Projektes");
		sb.append("&packageName=").append(generator.getBasePackageName());
		sb.append("&packaging=jar&javaVersion=1.8&style=data-jpa&style=web&style=devtools");

				
		URL url = new URL(sb.toString());
		
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", SpringBootstrapGeneratorStep.User_Agent);
		
		int response = connection.getResponseCode();	
		if (response == HttpURLConnection.HTTP_OK) {
		
			String workingDirectory = System.getProperty("user.dir");		
			Path outputDirectory = Paths.get(workingDirectory, "temp");			
			File fileOutputDirectory = outputDirectory.toFile();
			if (!fileOutputDirectory.exists()) {
				fileOutputDirectory.mkdirs();
			} else {
				//Eventuell Dateien löschen. Aber das ist auch gefährlich!!!
				//TODO
			}
			
			ZipInputStream zip = new ZipInputStream(connection.getInputStream());			
			ZipEntry zipEntry = zip.getNextEntry();
			byte[] buffer = new byte[4096];
			
			while (zipEntry != null) {			
				File newFile = new File(fileOutputDirectory.getCanonicalPath(), zipEntry.getName());
				if (zipEntry.isDirectory()) {
					newFile.mkdirs();
				} else {
					FileOutputStream fos = new FileOutputStream(newFile);
					int length = 0;
					while ((length = zip.read(buffer)) > 0) {
						fos.write(buffer, 0, length);
					}
					fos.close();
				}
				zipEntry = zip.getNextEntry();
			}			
			zip.close();			
		}		
	}
}
