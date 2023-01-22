package DSLParser;

import de.arinir.mdsd.metamodell.MDSDMetamodell.Class;
import de.arinir.mdsd.metamodell.MDSDMetamodell.*;

import javax.swing.text.html.HTMLDocument;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class DSLParser {
    UMLClassDiagramm diagramm;

    public DSLParser(String projektName) {
        this.diagramm = new UMLClassDiagramm(projektName);
    }

    public UMLClassDiagramm parese(InputStream stream, InputStream stream2) throws Exception {
        String line;
        Class currentClass;
        ArrayList<AssociationConnectionEnd> connectionEndList = new ArrayList<>();

        /**
         *
         */
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("entity")) {
                    String className = line.replaceAll("(entity|\\{)", " ").strip();
                    currentClass = new Class(className);
//                    classes.add(currentClass);

                    while (!(line = reader.readLine()).equals("}")) {
                        String attributeName = "";
                        DataType attributeDataType = null;
                        MultiplicityT myMultiplicityT = null;
                        MultiplicityT inverseMultiplicityT = null;
                        String myRollName = "";
                        String inverseRollName = "";
                        String inverseClassName = "";

                        if (line.startsWith("attribute")) {
                            int startIndex = 10;
                            int endIndex = line.indexOf(":");
                            attributeName = line.substring(startIndex, endIndex).strip();

                            if (line.endsWith("String")) {
                                attributeDataType = DataType.String;
                            } else if (line.endsWith("Boolean")) {
                                attributeDataType = DataType.Boolean;
                            } else if (line.endsWith("Integer")) {
                                attributeDataType = DataType.Integer;
                            } else if (line.endsWith("Float")) {
                                attributeDataType = DataType.Float;
                            }
                            currentClass.addAttribute(new Attribute(attributeName, attributeDataType));

                        } else if (line.startsWith("association")) {
                            int startIndex = 12;
                            int endIndex = line.indexOf(">");
                            String multiTemp = line.substring(startIndex, endIndex).strip();

                            if (multiTemp.contains("One")) {
                                myMultiplicityT = MultiplicityT.One;
                                startIndex = 16;
                                endIndex = line.indexOf(":");
                                myRollName = line.substring(startIndex, endIndex).strip();
                            } else if (multiTemp.contains("Many")) {
                                startIndex = 17;
                                endIndex = line.indexOf(":");
                                myRollName = line.substring(startIndex, endIndex).strip();
                                myMultiplicityT = MultiplicityT.Many;
                            }

                            startIndex = line.indexOf("(") + 1;
                            endIndex = line.indexOf(")");
                            inverseRollName = line.substring(startIndex, endIndex).strip();

                            startIndex = line.indexOf(":") + 1;
                            endIndex = line.indexOf("(");
                            inverseClassName = line.substring(startIndex, endIndex).strip();

                            connectionEndList.add(new AssociationConnectionEnd(myMultiplicityT, currentClass, myRollName, inverseRollName, inverseClassName));

                        }
                    }
                    diagramm.addClass(currentClass);
                }
            }
            //System.out.println(classes.toString());
        } catch (Exception e) {
            System.out.println("Inputstream dirty read!");
        }

        /**
         * Der untere Try-Block wird die DSL-Datei nochmal durchlaufen, um die SubClasses zu erzeugen
         */

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream2))) {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("entity") && line.contains("extends")) {
                    String superClassName = line.substring(line.indexOf("extends")).replaceAll("(\\{|extends)", " ").strip();
                    String className = line.replaceAll("(entity|\\{|extends|"+superClassName+")", " ").strip();
                    Iterator<Class> iterator = diagramm.getClasses().iterator();
                    Iterator<Class> iterator2 = diagramm.getClasses().iterator();
                    Class superClass;

                    while (iterator.hasNext()) {
                        currentClass = iterator.next();
                        if (currentClass.getName().equals(className)) {
                            while (iterator2.hasNext()) {
                                superClass = iterator2.next();
                                if (superClass.getName().equals(superClassName)) {
                                    currentClass.addStereotype(new Stereotype(superClassName));
                                }
                            }
                        }
                    }
                }
            }
            Assoziation assoziation;
            for (AssociationConnectionEnd end1 : connectionEndList) {
                for (AssociationConnectionEnd end2 : connectionEndList) {
                    if (end1.getAssociationEndName().equals(end2.getAssociationEndInverseName()) && end1.getAssociationEndInverseName().equals(end2.getAssociationEndName())) {
                        for (Class cls : diagramm.getClasses()) {
                            if (cls.getName().equals(end2.getInverseClassName())) {
                                assoziation = new Assoziation(end1.getMultiplicityT(), end1.getAssociationEndName(), end1.getRollClass(), end2.getMultiplicityT(), end2.getAssociationEndName(), cls);
                                diagramm.addAssoziation(assoziation);
                            }
                        }
                    }
                }
            }

            return diagramm;
        } catch (Exception e) {
            System.out.println("Inputstream dirty read!");
            e.printStackTrace();
        }
        return null;
    }
}
