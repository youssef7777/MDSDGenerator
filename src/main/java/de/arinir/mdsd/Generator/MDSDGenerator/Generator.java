package de.arinir.mdsd.Generator.MDSDGenerator;

import java.util.Vector;
import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;


/**
 * 
 * @author Doga Arinir
 *
 * Diese Klasse stellt eine Generator-Infrastruktur zur Verfügung, welche über verschiedene Generierungsschritte in der 
 */
public class Generator {
	private Vector<AbstractGeneratorStep> steps = new Vector<AbstractGeneratorStep>();
	private UMLClassDiagramm classDiagramm;
	private String basePackageName;
	private String projectName;
	
	public Generator(String basePackageName, String projectName, UMLClassDiagramm classDiagramm) {
		this.classDiagramm = classDiagramm;
		this.basePackageName = basePackageName;
		this.projectName = projectName;
		
		//Definition der Standard-Generatorinfrastruktur!
		steps.add(new SpringBootstrapGeneratorStep(this));
		steps.add(new EntitiesGeneratorStep(this));
		steps.add(new RepositoriesGeneratorStep(this));
		steps.add(new ControllersGeneratorStep(this));
	}
	
	public void generate() throws Exception {
		for (AbstractGeneratorStep step : steps) {
			step.run();
		}
	}
		
	public UMLClassDiagramm getClassDiagramm() {
		return classDiagramm;
	}
	
	public String getBasePackageName() {
		return basePackageName;
	}
	
	public String getProjectName() {
		return projectName;
	}
}
