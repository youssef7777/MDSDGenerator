package de.arinir.mdsd.Generator.MDSDGenerator;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;

public class EntitiesGeneratorStep extends AbstractGeneratorStep {

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

            VelocityContext context = new VelocityContext();
            context.put("packageName", generator.getBasePackageName());
            context.put("class", clazz);

            ve.evaluate(context, writer, "Log", jpaTemplate);

            try {

                // Get the file
                File f = new File("C:/Studium/Semester 5/Modellbasiert/Programme/mbsd-projekt/mbsd-projekt/src/main/java/de/fhdortmund/mbsdprojekt/generatedFiles/" + clazz.getName() + ".java");

                // Create new file
                // Check if it does not exist
                if (f.createNewFile()) {
                    System.out.println("File created");
                    FileOutputStream fos = new FileOutputStream("C:/Studium/Semester 5/Modellbasiert/Programme/mbsd-projekt/mbsd-projekt/src/main/java/de/fhdortmund/mbsdprojekt/generatedFiles/" + clazz.getName() + ".java");
                    fos.write(writer.toString().getBytes());
                    fos.flush();
                    fos.close();
                }
				else
                System.out.println("File already exists");
            } catch (Exception e) {
                System.err.println(e);
            }
        }

    }
}