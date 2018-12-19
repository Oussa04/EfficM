package tn.meteor.efficaisse.data.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by SKIIN on 27/01/2018.
 */

@Database(name = EfficaisseDatabase.NAME, version = EfficaisseDatabase.VERSION)
public class EfficaisseDatabase {

    public static final String NAME = "efficaisse-db";
    public static final int VERSION = 1;
}