package PennantOptimization;

public class Pennant {
    // Attributes
    private final String color;
    private Pennant leftSameColorNeighbour;
    private int leftSameColorNeighbourDistance;
    private Pennant rightSameColorNeighbour;
    private int rightSameColorNeighbourDistance;

    // Constructor
    public Pennant(String color) {
        this.color = color;
        leftSameColorNeighbour = null;
        leftSameColorNeighbourDistance = -1;
        rightSameColorNeighbour = null;
        rightSameColorNeighbourDistance = -1;
        // -1 is default value, not being in a chain; Set as soon as added to chain
        // no neighbour =>  distance=chainLength-1
    }

    // Methods
    // - Getters
    public String getColor() {
        return color;
    }
    public int getMinDistanceToNextSameColorPennant() {
        if (leftSameColorNeighbourDistance != -1 && rightSameColorNeighbourDistance != -1) {
            return Math.min(leftSameColorNeighbourDistance, rightSameColorNeighbourDistance);
        }
        else if (leftSameColorNeighbourDistance == -1) {
            return rightSameColorNeighbourDistance;
        }
        else {
            return leftSameColorNeighbourDistance;
        }
    }
    public Pennant getLeftSameColorNeighbour() {
        return leftSameColorNeighbour;
    }
    public int getLeftSameColorNeighbourDistance() {
        return leftSameColorNeighbourDistance;
    }
    public Pennant getRightSameColorNeighbour() {
        return rightSameColorNeighbour;
    }
    public int getRightSameColorNeighbourDistance() {
        return rightSameColorNeighbourDistance;
    }
    // - Setters
    public void setLeftSameColorNeighbour(Pennant leftSameColorNeighbour, int pennantDistance) {
        leftSameColorNeighbourDistance = pennantDistance;
        this.leftSameColorNeighbour = leftSameColorNeighbour;
    }
    public void setRightSameColorNeighbour(Pennant rightSameColorNeighbour, int pennantDistance) {
        rightSameColorNeighbourDistance = pennantDistance;
        this.rightSameColorNeighbour = rightSameColorNeighbour;
    }
    // - Others
    @Override public boolean equals(Object o) {
        if (o instanceof Pennant) {
            return this.getColor().equals(((Pennant) o).getColor());
        }
        return false;
    }
    public Pennant rawCopy() {
        return new Pennant(this.getColor());
    }

}