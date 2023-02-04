package DSLParser;

import de.arinir.mdsd.metamodell.MDSDMetamodell.Class;
import de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT;

public class AssociationConnectionEnd {

    private MultiplicityT multiplicityT;
    private Class myClassName;
    private String myassociationName;
    private String inverseAssociationName;
    private String  inverseClassName;

    private boolean wasAdded;

    public AssociationConnectionEnd(MultiplicityT multiplicityT, Class myClassName, String myassociationName, String inverseAssociationName, String inverseClassName) {
        this.multiplicityT = multiplicityT;
        this.myClassName = myClassName;
        this.myassociationName = myassociationName;
        this.inverseAssociationName = inverseAssociationName;
        this.inverseClassName = inverseClassName;
        this.wasAdded = false;
    }

    public MultiplicityT getMultiplicityT() {
        return multiplicityT;
    }

    public Class getMyClassName() {
        return myClassName;
    }

    public String getMyassociationName() {
        return myassociationName;
    }

    public String getInverseAssociationName() {
        return inverseAssociationName;
    }

    public String getInverseClassName() {
        return inverseClassName;
    }

    public boolean isWasAdded() {
        return wasAdded;
    }

    public void setWasAdded(boolean wasAdded) {
        this.wasAdded = wasAdded;
    }
}
