package de.arinir.mdsd.Generator.MDSDGenerator;


/**
 * 
 * @author Doga Arinir
 *
 * Kapselt und abstrahiert einen Generierungsschritt. In einem Generierungsschritt kann beispielsweise eine Projektmappe generiert werden, indem
 * aus dem Internet eine Vorlage heruntergeladen wird.
 */
public abstract class AbstractGeneratorStep {
	protected Generator generator;
	
	public AbstractGeneratorStep(Generator generator) {
		this.generator = generator;		
	}
		
	public abstract void run() throws Exception;	
}
