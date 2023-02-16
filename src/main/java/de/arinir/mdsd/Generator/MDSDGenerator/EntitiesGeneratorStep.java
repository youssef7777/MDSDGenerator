package de.arinir.mdsd.Generator.MDSDGenerator;

import de.arinir.mdsd.metamodell.MDSDMetamodell.Assoziation;
import de.arinir.mdsd.metamodell.MDSDMetamodell.Attribute;
import de.arinir.mdsd.metamodell.MDSDMetamodell.Class;
import de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT;
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
             * Die assEndList wird erzeugt, um nur die Inverse Assoziationen zu speichern, um Fehler bei Konstruktor-Generierung zu vermeiden
             */
            ArrayList<Assoziation.AssoziationEnd> assEndList = new ArrayList<>();
            for (Assoziation.AssoziationEnd ass : clazz.getAssoziations()) {
                if (!ass.getReference().getName().equals(clazz.getName())) {
                    assEndList.add(ass);
                }
            }

            Class superClass = null;


            /**
             * Wir benötigen den richtigen Instanz der SuperClass, dies wird mit getSuperClassses() nicht erreicht, da nur ein Objekt vom Typ _StereoType_ ist.
             * Und über diesen Objekt bekommen wir keinen Zugriff auf die AssoziationsListe der SuperClass
             */
            if (clazz.getSuperClasses().size() > 0) {
                String superClassName = clazz.getSuperClasses().get(0).getName();
                for (Class cls: generator.getClassDiagramm().getClasses()) {
                    if (superClassName.equals(cls.getName())) {
                        superClass = cls;
                        superClass.getSubClasses().add(clazz);
                    }
                }
            }

            int derivedAttributeCounter = 0;
            for (Attribute a : clazz.getAttributes()) {
                if (a.getName().contains("derived")) {
                    derivedAttributeCounter++;
                }
            }

            int derivedAttributeCounterSuperClass = 0;

            if (superClass != null) {
                for (Attribute a : superClass.getAttributes()) {
                    if (a.getName().contains("derived"))
                        derivedAttributeCounterSuperClass++;
                }
            }

            int assoziationCounter = 0;
            int index = 0;
            for (Assoziation.AssoziationEnd a : clazz.getAssoziations()) {
                if (a.getMultiplicity().toString() != MultiplicityT.One.toString() && index % 2 != 0) {
                    assoziationCounter++;
                }
                index++;
            }

            System.out.println(clazz.getName() + " " + assoziationCounter);

            VelocityContext context = new VelocityContext();
            context.put("packageName", generator.getBasePackageName());
            context.put("class", clazz);
            System.out.println(clazz.getSubClasses());
            context.put("superClass", superClass);
            context.put("assoziations", assEndList);
            context.put("dCounter", derivedAttributeCounter);
            context.put("dCounterSuperClass", derivedAttributeCounterSuperClass);
            context.put("assoziationCounter", assoziationCounter);
            ve.evaluate(context, writer, "Log", jpaTemplate);

            String workingDirectory = System.getProperty("user.dir");
            //FileOutputStream fos = new FileOutputStream(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + ".java");

            try {
                // Get the file
                File repositoriesDirectory = new File(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/Entities/");
                repositoriesDirectory.mkdirs();
                File f = new File(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/Entities/" + clazz.getName() + ".java");

                // Create new file
                // Check if it does not exist
                if (f.createNewFile()) {
                    FileOutputStream fos = new FileOutputStream(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/Entities/" + clazz.getName() + ".java");
                    fos.write(writer.toString().getBytes());
                    fos.flush();
                    fos.close();
                } else {
                    FileOutputStream fos2 = new FileOutputStream(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/Entities/" + clazz.getName() + "Temp.java");
                    fos2.write(writer.toString().getBytes());
                    fos2.flush();
                    fos2.close();
                    File newFile = new File(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/Entities/" + clazz.getName() + "Temp.java");
                    File oldFile = new File(workingDirectory + "/temp/src/main/java/de/fhdortmund/mbsdprojekt/Entities/" + clazz.getName() + ".java");
                    compareAndUpdateFiles(oldFile, newFile);
                    newFile.delete();
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


