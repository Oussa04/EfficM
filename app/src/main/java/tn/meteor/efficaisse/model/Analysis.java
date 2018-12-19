package tn.meteor.efficaisse.model;

public class Analysis {

    private String ingredientName;

    private double quantity;

    private String indication;

    private String unit;

    public Analysis(String ingredientName, double quantity, String unit, String indication) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.indication = indication;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getIndication() {
        return indication;
    }

    public void setIndication(String indication) {
        this.indication = indication;
    }
}
