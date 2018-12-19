package tn.meteor.efficaisse.utils;

import java.math.BigDecimal;

/**
 * Created by lilk on 21/01/2018.
 */

public class Mathutils {
    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }
}
