package tn.meteor.efficaisse.model;

/**
 * Created by SKIIN on 27/01/2018.
 */

public enum Unit {
    L("L"), KG("KG"), PIECE("PIECE");

    final String id;

    public String getId() {
        return id;
    }

    Unit(String id) {
        this.id = id;
    }
}


