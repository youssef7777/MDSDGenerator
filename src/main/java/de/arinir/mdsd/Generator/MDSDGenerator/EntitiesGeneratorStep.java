package de.arinir.mdsd.Generator.MDSDGenerator;

import de.arinir.mdsd.metamodell.MDSDMetamodell.Assoziation;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.ArrayList;

public class EntitiesGeneratorStep extends AbstractGeneratorStep {
    private static final String USER_CODE_START = "// USER CODE START";
    private static final String USER_CODE_END = "// USER CODE END";


    public EntitiesGeneratorStep(Generator generator) {
        super(generator);
    }

    @Override
    public void run() throws Exception {

        VelocityEngine ve = new VelocityEngine();
        ve.init();

        for (de.arinir.mdsd.metamodell.MDSDMetamodell.Class clazz : generator.getClassDiagramm().getClasses()) {

            Reader jpaTemplate = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/JPATemplate.vm")));
            StringWriter writer = new StringWriter();

            /**
             * Die assEndList wird erzeugt um nur die Inverse Assoziationen zu speichern, um Fehler bei Konstruktor-Generierung zu vermeiden
             */
            ArrayList<Assoziation.AssoziationEnd> assEndList = new ArrayList<>();
            for (Assoziation.AssoziationEnd ass : clazz.getAssoziations()) {
                if (!ass.getReference().getName().equals(clazz.getName())) {
                    assEndList.add(ass);
                }
            }

            VelocityContext context = new VelocityContext();
            context.put("packageName", generator.getBasePackageName());
            context.put("class", clazz);
            context.put("assoziations", assEndList);
            context.put("counter", 1);
            ve.evaluate(context, writer, "Log", jpaTemplate);

            String workingDirectory = System.getProperty("user.dir");
            //FileOutputStream fos = new FileOutputStream(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + ".java");

            try {
                // Get the file
                File f = new File(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + ".java");

                // Create new file
                // Check if it does not exist
                if (f.createNewFile()) {
                    FileOutputStream fos = new FileOutputStream(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + ".java");
                    fos.write(writer.toString().getBytes());
                    fos.flush();
                    fos.close();
                } else {
                    FileOutputStream fos2 = new FileOutputStream(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + "Temp.java");
                    fos2.write(writer.toString().getBytes());
                    fos2.flush();
                    fos2.close();
                    File newFile = new File(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + "Temp.java");
                    File oldFile = new File(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + ".java");
                    compareAndUpdateFiles(oldFile, newFile);
                    System.out.println(newFile.delete());

                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }

    }

    public void compareAndUpdateFiles(File oldFile, File newFile) throws IOException {
        BufferedReader oldReader = new BufferedReader(new FileReader(oldFile));
        BufferedReader newReader = new BufferedReader(new FileReader(newFile));

        StringBuilder updatedContent = new StringBuilder();
        String oldLine = "";
        String newLine = "";

        boolean inUserCode = false;

        while ((oldLine = oldReader.readLine()) != null) {
            if (oldLine.equals("null"))
                break;
            // Identifizierung der Anfang vom User Code
            if (oldLine.contains(USER_CODE_START)) {
                inUserCode = true;
            } else if (oldLine.contains(USER_CODE_END)) {
                inUserCode = false;
                updatedContent.append(oldLine).append("\n");
                oldLine = oldReader.readLine();

            }

            if (inUserCode == false) {
                newLine = newReader.readLine();
            }


            //Wenn man in UserAbschnitt ist, da wird an dieser Stelle in die Datei den User Inhalt geschrieben sonst wird der generierte Code einfach übernommen
            if (!inUserCode && !oldLine.equals(newLine)) {
                updatedContent.append(newLine).append("\n");
            } else {
                updatedContent.append(oldLine).append("\n");
            }


        }

        oldReader.close();
        newReader.close();

        FileOutputStream outputStream = new FileOutputStream(oldFile);
        outputStream.write(updatedContent.toString().getBytes());
        outputStream.close();
    }
}


