package de.arinir.mdsd.Generator.MDSDGenerator;

import DSLParser.DSLParser;
import XMI2MDSDConverter.Converter;
import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        int convertedInput = 0;
        do {
            try {
                System.out.println("Bitte waehlen Sie Ihren Eingabedatei:");
                System.out.println("1. Flottenmanagement aus DSL-Datei laden");
                System.out.println("2. Flottenmanagement aus XML-Datei laden");
                System.out.println("3. Eine beliebige DSL-Datei laden");
                System.out.println("4. Eine beliebige XML-Datei laden");
                System.out.println();
                System.out.println("0. Programm beenden");
                convertedInput = scanner.nextInt();
//                if (input.length() < 1) {
//                    continue;
//                }
//                convertedInput = Integer.parseInt(input);
                if (convertedInput < 0 || convertedInput > 4) {
                    continue;
                }

                switch (convertedInput) {
                    case 1:
                        generateFromDSL("/Textdatei.txt");
                        break;
                    case 2:
                        generateFromXML("/Flottenmanagement.xml");
                        break;
                    case 3:
                        System.out.println("Bitte geben Sie den absoluten Pfad ein:");
                        input = scanner.nextLine();
                        generateFromDSL( input);
                        break;
                    case 4:
                        System.out.println("Bitte geben Sie den absoluten Pfad ein:");
                        input = scanner.nextLine();
                        generateFromXML(input);
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (convertedInput != 0);

    }

    public static void generateFromDSL(String absolutePathName) {
        DSLParser parser = new DSLParser("Flottenmanagement");
        try (InputStream inputStream1 = App.class.getResourceAsStream(absolutePathName); InputStream inputStream2 = App.class.getResourceAsStream(absolutePathName)) {
           UMLClassDiagramm dslDiagramm = parser.parese(inputStream1, inputStream2);

            if (dslDiagramm != null) {
                Generator generator = new Generator("de.fhdortmund.mbsdprojekt", "Flottenmanagement", dslDiagramm);
                generator.generate();
                System.out.println("--------------------------------------");
                System.out.println("Projekt aus DSL erfolgreich generiert!");
                System.out.println("--------------------------------------\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void generateFromXML(String absolutePathName) {
        try (InputStream s = App.class.getResourceAsStream(absolutePathName)) {
            Converter converter = new Converter();
            UMLClassDiagramm xmiDiagramm = converter.convert(s);

            if (xmiDiagramm != null) {
                Generator generator = new Generator("de.fhdortmund.mbsdprojekt", "Flottenmanagement", xmiDiagramm);
                generator.generate();
                System.out.println("--------------------------------------");
                System.out.println("Projekt aus XML erfolgreich generiert!");
                System.out.println("--------------------------------------\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
