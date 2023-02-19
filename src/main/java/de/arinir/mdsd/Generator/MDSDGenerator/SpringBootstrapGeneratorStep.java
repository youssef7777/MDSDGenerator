package de.arinir.mdsd.Generator.MDSDGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SpringBootstrapGeneratorStep extends AbstractGeneratorStep {
    private static final String Spring_Initializr_URL = "https://start.spring.io";
    private static final String User_Agent = "Mozilla/5.0";

    int userEingabe;

    public SpringBootstrapGeneratorStep(Generator generator, int userEingabe) {
        super(generator);
        this.userEingabe = userEingabe;
    }

    @Override
    public void run() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append(SpringBootstrapGeneratorStep.Spring_Initializr_URL);
        sb.append("/starter.zip?type=maven-project&language=java&platformVersion=3.0.2");
        sb.append("&packaging=jar&jvmVersion=17");
        sb.append("&groupId=").append(generator.getBasePackageName());
        sb.append("&artefactId=").append(generator.getProjectName());
        sb.append("&name=").append(generator.getProjectName());
        sb.append("&description=").append("Generated%20Spring%20Boot%20Project");
        sb.append("&packageName=").append(generator.getBasePackageName());
        sb.append("&dependencies=devtools,web,data-jpa,h2");


        URL url = new URL(sb.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", SpringBootstrapGeneratorStep.User_Agent);

        int response = connection.getResponseCode();
        if (response == HttpURLConnection.HTTP_OK) {

            String workingDirectory = System.getProperty("user.dir");
            Path outputDirectory;
            if (userEingabe == 1 || userEingabe == 3) {
                outputDirectory = Paths.get(workingDirectory, "DSLTemp");
            } else {
                outputDirectory = Paths.get(workingDirectory, "XMLTemp");
            }


            File fileOutputDirectory = outputDirectory.toFile();
            if (!fileOutputDirectory.exists()) {
                fileOutputDirectory.mkdirs();
            } else {
                //Eventuell Dateien löschen. Aber das ist auch gefährlich!!!
//                deleteDir(fileOutputDirectory);
//                fileOutputDirectory.mkdirs();
//                System.out.println("Temp Dirk. deleted and new created successfully");
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

    public static void deleteDir(File dir) {

        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDir(files[i]);
                } else {
                    files[i].delete();
                }
            }
            dir.delete();
        }
    }
}
