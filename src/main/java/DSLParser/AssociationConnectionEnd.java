package DSLParser;

import de.arinir.mdsd.metamodell.MDSDMetamodell.Class;
import de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT;

public class AssociationConnectionEnd {

    private MultiplicityT multiplicityT;
    private Class rollClass;
    private String associationEndName;
    private String associationEndInverseName;

    private String  inverseClassName;

    public AssociationConnectionEnd(MultiplicityT multiplicityT, Class rollClass, String associationEndName, String associationEndInverseName, String inverseClassName) {
        this.multiplicityT = multiplicityT;
        this.rollClass = rollClass;
        this.associationEndName = associationEndName;
        this.associationEndInverseName = associationEndInverseName;
        this.inverseClassName = inverseClassName;
    }

    public MultiplicityT getMultiplicityT() {
        return multiplicityT;
    }

    public Class getRollClass() {
        return rollClass;
    }

    public String getAssociationEndName() {
        return associationEndName;
    }

    public String getAssociationEndInverseName() {
        return associationEndInverseName;
    }

    public String getInverseClassName() {
        return inverseClassName;
    }
}
