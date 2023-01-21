package de.arinir.mdsd.Generator.MDSDGenerator;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;

public class RepositoriesGeneratorStep extends AbstractGeneratorStep{

    public RepositoriesGeneratorStep(Generator generator) {
        super(generator);
    }

    @Override
    public void run() throws Exception {

        VelocityEngine ve = new VelocityEngine();
        ve.init();

        for (de.arinir.mdsd.metamodell.MDSDMetamodell.Class clazz : generator.getClassDiagramm().getClasses()) {

            Reader repoTemplate = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/RepositoryTemplate.vm")));
            StringWriter writer = new StringWriter();

            VelocityContext context = new VelocityContext();
            context.put("packageName", generator.getBasePackageName());
            context.put("class", clazz);
            //System.out.println(clazz.getAssoziations());

            ve.evaluate(context, writer, "Log", repoTemplate);
            String workingDirectory = System.getProperty("user.dir");
            FileOutputStream fos = new FileOutputStream(workingDirectory+ "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + "Repo.java");
            fos.write(writer.toString().getBytes());
            fos.flush();
            fos.close();
            System.out.println("generated");
//            try {
//
//                // Get the file
//                File f = new File("C:/Studium/Semester 5/Modellbasiert/Programme/mbsd-projekt/mbsd-projekt/src/main/java/de/fhdortmund/mbsdprojekt/generatedFiles/" + clazz.getName() + "Repo.java");
//
//                // Create new file
//                // Check if it does not exist
//                if (f.createNewFile()) {
//                    System.out.println("File created");
//
//                }
//                else
//                    System.out.println("File already exists");
//            } catch (Exception e) {
//                System.err.println(e);
//            }
        }

    }
}
