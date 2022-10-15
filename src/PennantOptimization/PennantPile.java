package PennantOptimization;

import java.util.HashMap;

public class PennantPile extends HashMap<String, Integer> {
    // Attributes
    private int totalAmountOfPennants = 0;

    // Methods
    // - Getters
    public int getTotalAmountOfPennants() {
        return totalAmountOfPennants;
    }
    // - Others
    @Override public Integer put(String pennantColor, Integer amountOfPennants) {
        if (amountOfPennants < 0) {
            throw new IllegalArgumentException("No negative amount of pennants");
        }
        totalAmountOfPennants -= getOrDefault(pennantColor, 0);
        totalAmountOfPennants += amountOfPennants;
        return super.put(pennantColor, amountOfPennants);
    }
    public PennantPile copy() {
        PennantPile copiedPennantPile = new PennantPile();
        copiedPennantPile.putAll(this);
        copiedPennantPile.totalAmountOfPennants = this.totalAmountOfPennants;
        return copiedPennantPile;
    }
}
