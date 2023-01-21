package de.arinir.mdsd.Generator.MDSDGenerator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            Generator generator = new Generator("de.fhdortmund.mbsdprojekt", "Flottenmanagement", de.arinir.mdsd.metamodell.MDSDMetamodell.App.CreateFlottenmanagement());
			generator.generate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
