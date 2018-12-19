package tn.meteor.efficaisse.model;

public class Prediction {

    private Ingredient ingredient;
    private String criteria;
    double prediction;
    int count;

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "ingredient=" + ingredient.getName() +
                ", criteria='" + criteria + '\'' +
                ", prediction=" + prediction +
                '}';
    }
}
