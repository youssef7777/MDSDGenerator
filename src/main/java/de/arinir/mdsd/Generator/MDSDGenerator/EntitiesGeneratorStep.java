package de.arinir.mdsd.Generator.MDSDGenerator;

import de.arinir.mdsd.metamodell.MDSDMetamodell.Assoziation;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.ArrayList;

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

            ArrayList<Assoziation.AssoziationEnd> assEndList = new ArrayList<>();
            for (Assoziation.AssoziationEnd ass: clazz.getAssoziations()) {
                if(!ass.getReference().getName().equals(clazz.getName())) {
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
            FileOutputStream fos = new FileOutputStream(workingDirectory+ "/temp/src/main/java/de/fhdortmund/mbsdprojekt/" + clazz.getName() + ".java");
            fos.write(writer.toString().getBytes());
            fos.flush();
            fos.close();



            System.out.println("generated");
//            try {
//
//                // Get the file
//                File f = new File("C:/Studium/Semester 5/Modellbasiert/Programme/mbsd-projekt/mbsd-projekt/src/main/java/de/fhdortmund/mbsdprojekt/generatedFiles/" + clazz.getName() + ".java");
//
//                // Create new file
//                // Check if it does not exist
//                if (f.createNewFile()) {
//                    System.out.println("File created");
//                    FileOutputStream fos = new FileOutputStream("C:/Studium/Semester 5/Modellbasiert/Programme/mbsd-projekt/mbsd-projekt/src/main/java/de/fhdortmund/mbsdprojekt/generatedFiles/" + clazz.getName() + ".java");
//                    fos.write(writer.toString().getBytes());
//                    fos.flush();
//                    fos.close();
//                }
//				else
//                System.out.println("File already exists");
//            } catch (Exception e) {
//                System.err.println(e);
//            }
        }

    }

    public void compare(File oldFile, File newFile) throws IOException {
        BufferedReader oldr = new BufferedReader(new FileReader(oldFile));
        BufferedReader newr = new BufferedReader(new FileReader(newFile));

        StringBuilder updatedContent = new StringBuilder();
        String olds;
        String news;

        while ((olds = oldr.readLine()) != null && (news = newr.readLine()) != null) {
            if (olds.equals(news)) {
                updatedContent.append(news).append("\n");
            } else {
                updatedContent.append(olds).append("\n");
            }
        }

        oldr.close();
        newr.close();

        FileOutputStream outputStream = new FileOutputStream(oldFile);
        outputStream.write(updatedContent.toString().getBytes());
        outputStream.close();
    }
}
