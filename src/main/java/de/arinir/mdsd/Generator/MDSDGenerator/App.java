package de.arinir.mdsd.Generator.MDSDGenerator;

import DSLParser.DSLParser;
import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;

import java.io.InputStream;

import static de.arinir.mdsd.metamodell.MDSDMetamodell.App.CreateFlottenmanagement;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        UMLClassDiagramm diagramm;
        try {
            DSLParser parser = new DSLParser("Flottenmanagement");
            try (InputStream inputStream = App.class.getResourceAsStream("/Textdatei.txt"))
            {
               diagramm = parser.parese(inputStream);
                System.out.println(diagramm.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (diagramm != null) {
                System.out.println("diagram Not null");
                UMLClassDiagramm diagramm1 = CreateFlottenmanagement();
                //System.out.println(diagramm1.toString());
                Generator generator = new Generator("de.fhdortmund.mbsdprojekt", "Flottenmanagement", diagramm);
                generator.generate();
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
