package tn.meteor.efficaisse.utils;

public class HomeMenu{

    private int itemDrawble;
    private String itemText;
private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getItemDrawble() {
        return itemDrawble;
    }

    public void setItemDrawble(int itemDrawble) {
        this.itemDrawble = itemDrawble;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }


    public HomeMenu() {
    }

    public HomeMenu(int itemDrawble, String itemText, int color) {
        this.itemDrawble = itemDrawble;
        this.itemText = itemText;
        this.color = color;
    }
}
