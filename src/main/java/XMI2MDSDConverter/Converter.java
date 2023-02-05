package XMI2MDSDConverter;

import de.arinir.mdsd.metamodell.MDSDMetamodell.UMLClassDiagramm;
import de.arinir.xmiparser.xmiparser.*;
import de.arinir.xmiparser.xmiparser.UMLStereotypeReferenceBody.UMLStereotypeReference;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

public class Converter {
	public interface Visitor {
		public void visit(ContentElement element);
	}	
	private Hashtable<Class, Visitor> handlers = new Hashtable<Class, Visitor>();
	private Vector<Association> associations2Convert = new Vector<Association>();
	
	private UMLClassDiagramm diagramm = new UMLClassDiagramm("");
	
	public Converter() {
		handlers.put(Model.class, (ContentElement a) -> {visit((Model)a);});
		handlers.put(XMIPackage.class, (ContentElement a) -> {visit((XMIPackage)a);});
		handlers.put(Clazz.class, (ContentElement a) -> {visit((Clazz)a);});
		handlers.put(Association.class, (ContentElement a) -> {visit((Association)a);});
		handlers.put(Attribute.class, (ContentElement a) -> {visit((Attribute)a);});		
	}
	
	public UMLClassDiagramm convert(InputStream s) {
		
		XMIParser parser = new XMIParser();
		XMI xmi = parser.parse(s);

		if (xmi != null) {
			for (ContentElement element : xmi.getContent()) {
				visit(element);
			}
			
			for (Association asso : associations2Convert) {
				
				if (asso.getConnections().size() == 2) {
					AssociationEnd left = asso.getConnections().get(0);
					AssociationEnd right = asso.getConnections().get(1);
					
					de.arinir.mdsd.metamodell.MDSDMetamodell.Assoziation convertedAssoziation = new de.arinir.mdsd.metamodell.MDSDMetamodell.Assoziation();
														
					convertedAssoziation.getFrom().setRoleName(left.getName());
					convertedAssoziation.getFrom().setMultiplicity(convert(left.getMultiplicity()));
					convertedAssoziation.getFrom().setReference(xmiId2Class.get(left.getTypeReference()));
					convertedAssoziation.getFrom().getReference().addAssoziation(convertedAssoziation.getTo());
					convertedAssoziation.getTo().setRoleName(right.getName());
					convertedAssoziation.getTo().setMultiplicity(convert(right.getMultiplicity()));
					convertedAssoziation.getTo().setReference(xmiId2Class.get(right.getTypeReference()));
					convertedAssoziation.getTo().getReference().addAssoziation(convertedAssoziation.getFrom());
					
					diagramm.addAssoziation(convertedAssoziation);				
				}
			}
			
			return diagramm;			
		}
		return null;
	}
	
	private de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT convert(UMLAssociationMultiplicityET multi) {
		if (multi == UMLAssociationMultiplicityET.Many)
			return de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT.Many;
		else if (multi == UMLAssociationMultiplicityET.One)
			return de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT.One;
		else if (multi == UMLAssociationMultiplicityET.OneToMany)
			return de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT.OneToMany;
		else if (multi == UMLAssociationMultiplicityET.ZeroOrMany)
			return de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT.ZeroOrMany;
		return de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT.ZeroOrOne;		
	}

	private void visit(ContentElement element) {
		Class subjectType = element.getClass();
		if (handlers.containsKey(subjectType)) {
			handlers.get(subjectType).visit(element);
		}
	}

	private void visit(Model element) {	
		for (TaggableElement child : element.getOwnedElements()) {
			visit(child);
		}
	}
	
	private void visit(Association element) {
		associations2Convert.add(element);
	}
	
	
	private Hashtable<String, de.arinir.mdsd.metamodell.MDSDMetamodell.Class> xmiId2Class = new Hashtable<String, de.arinir.mdsd.metamodell.MDSDMetamodell.Class>();
	private Hashtable<String, de.arinir.mdsd.metamodell.MDSDMetamodell.Stereotype> stereotypes = new Hashtable<String, de.arinir.mdsd.metamodell.MDSDMetamodell.Stereotype>();
	private Clazz currentClazz;
	private de.arinir.mdsd.metamodell.MDSDMetamodell.Class currentConvertedClazz;
	private void visit(Clazz element) {
		if (!element.getIsRoot()) {
			currentClazz = element;
			currentConvertedClazz = new de.arinir.mdsd.metamodell.MDSDMetamodell.Class(element.getName());
			xmiId2Class.put(element.getId(), currentConvertedClazz);
			for (ClazzMember member : element.getFeatures()) {
				visit(member);
			}
			
			UMLStereotypeReferenceBody stereotypeBody = element.getStereotype();
			if (stereotypeBody != null) {
				UMLStereotypeReference stereotype = stereotypeBody.getReference();
				if (stereotype != null) {
					String name = stereotype.getName();
					if (name != null) {
						de.arinir.mdsd.metamodell.MDSDMetamodell.Stereotype st = null;
						if (!stereotypes.containsKey(name)) {
							st = new de.arinir.mdsd.metamodell.MDSDMetamodell.Stereotype(name);
							stereotypes.put(name,  st);
						} else {
							st = stereotypes.get(name);
						}
						currentConvertedClazz.addStereotype(st);
					}
				}				
			}
			
			diagramm.addClass(currentConvertedClazz);
		}
		
	}
	private Hashtable<String, de.arinir.mdsd.metamodell.MDSDMetamodell.DataType> datatypes = new Hashtable<String, de.arinir.mdsd.metamodell.MDSDMetamodell.DataType>();
	
	private void visit(Attribute element) {
		de.arinir.mdsd.metamodell.MDSDMetamodell.Attribute convertedAttribute = new de.arinir.mdsd.metamodell.MDSDMetamodell.Attribute(element.getName(), null);
		currentConvertedClazz.addAttribute(convertedAttribute);
		
		element.getTaggedValue().forEach((TaggedValue a) -> {
			if ("type".equals(a.getTag()) ) {
				String value = a.getValue();
				de.arinir.mdsd.metamodell.MDSDMetamodell.DataType type = null;
				if (!datatypes.containsKey(value)) {
					type = new de.arinir.mdsd.metamodell.MDSDMetamodell.DataType(value);
					datatypes.put(value,  type);
				} else {
					type = datatypes.get(value);
				}
				convertedAttribute.setType(type);				
			} });
	}


	
	private void visit(XMIPackage element) {
		for (TaggableElement child : element.getOwnedElements()) {
			visit(child);
		}
	}
}
