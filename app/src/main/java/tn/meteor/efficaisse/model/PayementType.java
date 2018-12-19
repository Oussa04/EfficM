package tn.meteor.efficaisse.model;

/**
 * Created by lilk on 29/01/2018.
 */

public enum PayementType {
    ESPECE("ESPECE"), CHEQUE("CHEQUE"), CONTREBON("CONTREBON"), BON("BON");

    final String id;

    public String getId() {
        return id;
    }

    PayementType(String id) {
        this.id = id;
    }
}

