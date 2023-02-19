package de.arinir.mdsd.Generator.MDSDGenerator;

import de.arinir.mdsd.metamodell.MDSDMetamodell.Class;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;

public class ControllersGeneratorStep extends AbstractGeneratorStep {
    private static final String USER_CODE_START = "// USER CODE START";
    private static final String USER_CODE_END = "// USER CODE END";

    int userEingabe;
    public ControllersGeneratorStep(Generator generator, int userEingabe) {
        super(generator);
        this.userEingabe = userEingabe;
    }

    @Override
    public void run() throws Exception {
        VelocityEngine ve = new VelocityEngine();
        ve.init();

        for (de.arinir.mdsd.metamodell.MDSDMetamodell.Class clazz : generator.getClassDiagramm().getClasses()) {

            Reader controllerTemplate = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/ControllerTemplate.vm")));
            StringWriter writer = new StringWriter();

            VelocityContext context = new VelocityContext();
            context.put("packageName", generator.getBasePackageName());
            context.put("class", clazz);

            ve.evaluate(context, writer, "Log", controllerTemplate);

            if (userEingabe == 1 || userEingabe == 3) {
                generateDirectory("DSLTemp", writer, clazz);
            } else {
                generateDirectory("XMLTemp", writer, clazz);
            }

        }
    }

    public static void generateDirectory(String dirName, StringWriter writer, Class clazz) {
        String workingDirectory = System.getProperty("user.dir");

        try {
            // Get the file
            File repositoriesDirectory = new File(workingDirectory + "/" + dirName + "/src/main/java/de/fhdortmund/mbsdprojekt/Controller/");
            repositoriesDirectory.mkdirs();
            File f = new File(workingDirectory + "/" + dirName + "/src/main/java/de/fhdortmund/mbsdprojekt/Controller/" + clazz.getName() + "Controller.java");

            // Create new file
            // Check if it does not exist
            if (f.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(workingDirectory + "/" + dirName + "/src/main/java/de/fhdortmund/mbsdprojekt/Controller/" + clazz.getName() + "Controller.java");
                fos.write(writer.toString().getBytes());
                fos.flush();
                fos.close();
            } else {
                FileOutputStream fos2 = new FileOutputStream(workingDirectory + "/" + dirName + "/src/main/java/de/fhdortmund/mbsdprojekt/Controller/" + clazz.getName() + "ControllerTemp.java");
                fos2.write(writer.toString().getBytes());
                fos2.flush();
                fos2.close();
                File newFile = new File(workingDirectory + "/" + dirName + "/src/main/java/de/fhdortmund/mbsdprojekt/Controller/" + clazz.getName() + "ControllerTemp.java");
                File oldFile = new File(workingDirectory + "/" + dirName + "/src/main/java/de/fhdortmund/mbsdprojekt/Controller/" + clazz.getName() + "Controller.java");
                compareAndUpdateFiles(oldFile, newFile);
                newFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compareAndUpdateFiles(File oldFile, File newFile) throws IOException {
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
                while (!newLine.contains(USER_CODE_END)) {
                    newLine = newReader.readLine();
                }

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
