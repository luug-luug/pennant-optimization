package PennantOptimization;

import java.util.HashSet;
import java.util.Set;

/**
 * This class provides a chain of colored {@link Pennant pennants} with a certain <i>quality</i>.
 * <p>
 *     An instance consists of an array of pennants, the actual chain. Initially it is empty and pennants
 *     are added step by step, therefore, the amount of pennants on the chain is saved.
 * </p>
 * <p>
 *     The quality of a chain is measured by
 *     the minimal distance between two same color pennants on the chain and this distance's frequency, meaning,
 *     how many pairs of pennants are there with that minimal distance.
 *     Example: -b-r-b-r-g- results in minimalDistance=1 (one pennant in between) and frequency=2.
 *     For the quality of a chain the distance is prioritized before the frequency, meaning, lower distance
 *     but potentially higher frequency still results in a better chain.
 * </p>
 */
public class PennantChain implements Comparable<PennantChain>{
    // Attributes
    private Pennant[] chain;
    private int minimalDistance;
    private int frequency;
    private int amountOfPennantsOnChain;

    // Constructor
    public PennantChain(int length, boolean dummy) {
        chain = new Pennant[length];
        minimalDistance = dummy? -1 : length;
        frequency = 0;
        amountOfPennantsOnChain = 0;
    }
    // Methods
    // - Getters
    public Pennant[] getChain() {
        return chain;
    }
    public int getAmountOfPennantsOnChain() {
        return amountOfPennantsOnChain;
    }
    // - Others
    // - - Measurements
    public int measureMinDistanceToNextSameColorNeighbourToDirection(int indexPennant, int direction) {
        if (indexPennant < 0 || indexPennant >= amountOfPennantsOnChain) {
            throw new IndexOutOfBoundsException(
                    "Given index for distance measuring out of bounds of chain"
            );
        }
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("Not a direction");
        }

        // Search for same color pennant in either left or right direction
        boolean isSameColorFound = false;
        int distance = 0;
        for (int i = indexPennant + direction; i>=0 && i<amountOfPennantsOnChain; i += direction) {
            if (chain[indexPennant].equals(chain[i])) {
                isSameColorFound = true;
                break;
            }
            distance++;
        }
        if (!isSameColorFound) {
            distance = chain.length-1; // hypothetical best distance
        }
        return distance;
    }
    //FIXME -r-r-b-g-w- (8)    r-b-r-b-r-  (15)  find a meaningful way to measure the quality
    public int measureQualityIndex() {
        int sumInvertedDistances = 0;
        int invertPoint = chain.length-1;
        for (int i=0; i<amountOfPennantsOnChain; i++) {
            sumInvertedDistances += invertPoint - chain[i].getMinDistanceToNextSameColorPennant();
        }
        return sumInvertedDistances;
    }
    // - - Adjustments
    private void adjustNeighbourhoodAfterSettingPennant(int i) {
        int leftSameColorNeighbourDistance = measureMinDistanceToNextSameColorNeighbourToDirection(i, -1);
        int rightSameColorNeighbourDistance = measureMinDistanceToNextSameColorNeighbourToDirection(i,1);

        // Adjust pennant at i
        chain[i].setLeftSameColorNeighbour(
                // is there a neighbour? Default: neighbour=null and distance=chainLength-1
                i - (leftSameColorNeighbourDistance+1) >=0 ? chain[i - (leftSameColorNeighbourDistance+1)] : null,
                leftSameColorNeighbourDistance);

        chain[i].setRightSameColorNeighbour(
                i + (rightSameColorNeighbourDistance+1) <= amountOfPennantsOnChain-1? chain[i + (rightSameColorNeighbourDistance+1)] : null,
                rightSameColorNeighbourDistance);
        // Adjust neighbours
        if (chain[i].getLeftSameColorNeighbour() != null) {
            chain[i].getLeftSameColorNeighbour().setRightSameColorNeighbour(chain[i], leftSameColorNeighbourDistance);
        }
        if (chain[i].getRightSameColorNeighbour() != null) {
            chain[i].getRightSameColorNeighbour().setLeftSameColorNeighbour(chain[i], rightSameColorNeighbourDistance);
        }
    }
    private void adjustNeighbourhoodBeforeRemovingPennant(int i, boolean isSwapped) {
        int newDistance;

        // PennantOptimization.Pennant is in between two same color neighbours
        if (chain[i].getLeftSameColorNeighbour() != null && chain[i].getRightSameColorNeighbour() != null) {
            newDistance = chain[i].getLeftSameColorNeighbourDistance() +
                    chain[i].getRightSameColorNeighbourDistance() +
                    (isSwapped? 1 : 0); // gap is filled with swap partner or surrounding pennants move closer together
        }
        // At the most one neighbour to pennant
        else {
            newDistance = chain.length-1; // max distance for possible neighbour because it loses its neighbour
        }
        // Left neighbour of this pennant loses its right neighbour, namely this said one
        if (chain[i].getLeftSameColorNeighbour() != null) {
            chain[i].getLeftSameColorNeighbour().setRightSameColorNeighbour(
                    chain[i].getRightSameColorNeighbour(), newDistance //default: neighbour=null and distance=chainLength-1
            );
        }
        // Right neighbour of this pennant loses its left neighbour, namely this said one
        if (chain[i].getRightSameColorNeighbour() != null) {
            chain[i].getRightSameColorNeighbour().setLeftSameColorNeighbour(
                    chain[i].getLeftSameColorNeighbour(), newDistance
            );
        }
    }
    private void adjustChainQualityAfterAdding(int distanceAddedPennant) {
        if (distanceAddedPennant < minimalDistance) {
            minimalDistance = distanceAddedPennant;
            frequency = 1;
        } else if (distanceAddedPennant == minimalDistance) {
            frequency++;
        }
    }
    private void adjustChainQualityAfterSwapping(int distancePennantABefore, int distancePennantBBefore, int distancePennantAAfter, int distancePennantBAfter) {
        // Much Worse (distance wise)
        if (distancePennantAAfter < minimalDistance || distancePennantBAfter < minimalDistance) {
            if (distancePennantAAfter == distancePennantBAfter) {
                minimalDistance = distancePennantAAfter;
                frequency = 2;
            }
            else if (distancePennantAAfter < distancePennantBAfter) {
                minimalDistance = distancePennantAAfter;
                frequency = 1;
            }
            else {
                minimalDistance = distancePennantBAfter;
                frequency = 1;
            }
        }
        // Better (Same distance, but less frequent) or worse (Same distance, but more frequent)
        else {
            if (distancePennantABefore == minimalDistance && distancePennantAAfter > minimalDistance) {
                frequency--;
            }
            else if (distancePennantABefore > minimalDistance && distancePennantAAfter == minimalDistance ) {
                frequency++;
            }
            if (distancePennantBBefore == minimalDistance && distancePennantBAfter > minimalDistance) {
                frequency--;
            }
            else if (distancePennantBBefore > minimalDistance && distancePennantBAfter == minimalDistance) {
                frequency++;
            }
        }
        // Much better (Better distance): Determine new distance
        if (frequency <= 0) {
            int newMinimalDistance = chain[0].getMinDistanceToNextSameColorPennant();
            int count = 0; // Frequency is less: -r-r- is counted twice, but freq is only 1
            Set<String> overlapCount = new HashSet<>(); // counts colors with min distance
            for (Pennant pennant : chain) {
                if (pennant.getMinDistanceToNextSameColorPennant() < newMinimalDistance) {
                    newMinimalDistance = pennant.getMinDistanceToNextSameColorPennant();
                    count = 1;
                    overlapCount.clear();
                    overlapCount.add(pennant.getColor());
                }
                else if (pennant.getMinDistanceToNextSameColorPennant() == newMinimalDistance) {
                    count++;
                    overlapCount.add(pennant.getColor());
                }
            }
            minimalDistance = newMinimalDistance;
            frequency = count - overlapCount.size();
        }
    }
    // - - Movements
    public PennantChain addPennant(Pennant pennant) {
        if (amountOfPennantsOnChain >= chain.length) {
            throw new IndexOutOfBoundsException(
                    "Too many pennants have been added."
            );
        }

        // Add pennant and measure distance to left neighbour
        chain[amountOfPennantsOnChain] = pennant;
        amountOfPennantsOnChain++;
        int i = amountOfPennantsOnChain -1;
        int distancePennant = measureMinDistanceToNextSameColorNeighbourToDirection(i, -1);
        // Adjustments
        adjustNeighbourhoodAfterSettingPennant(i);
        adjustChainQualityAfterAdding(distancePennant);
        return this;
    }
    public PennantChain swapPennants(int i, int j) {
        if (
                i < 0 || j < 0 ||
                        i >= amountOfPennantsOnChain || j >= amountOfPennantsOnChain) {
            throw new IndexOutOfBoundsException(
                    "At least one given index for swapping pennants out of bounds of chain"
            );
        }
        // Same color case
        if (chain[i].equals(chain[j])) {
            return this;
        }
        // Save old distances
        int distancePennantABefore = chain[i].getMinDistanceToNextSameColorPennant();
        int distancePennantBBefore = chain[j].getMinDistanceToNextSameColorPennant();
        // Adjust old neighbourhoods
        adjustNeighbourhoodBeforeRemovingPennant(i, true);
        adjustNeighbourhoodBeforeRemovingPennant(j, true);
        // Swap pennants
        Pennant tempPennant = chain[i];
        chain[i] = chain[j];
        chain[j] = tempPennant;
        // Adjust new neighbourhoods
        adjustNeighbourhoodAfterSettingPennant(j);
        adjustNeighbourhoodAfterSettingPennant(i);
        // Adjust quality of chain
        adjustChainQualityAfterSwapping(
                distancePennantABefore, distancePennantBBefore,
                chain[j].getMinDistanceToNextSameColorPennant(), chain[i].getMinDistanceToNextSameColorPennant()
        );
        return this;
    }
    // - - General
    @Override public int compareTo(PennantChain o) {
        // 'this' is worse
        if ((this.minimalDistance < o.minimalDistance) ||
                ((this.minimalDistance == o.minimalDistance) && (this.frequency > o.frequency))) {
            return -1;
        }
        // 'o' is worse
        else if ((this.minimalDistance > o.minimalDistance) ||
                ((this.minimalDistance == o.minimalDistance) && (this.frequency < o.frequency))) {
            return 1;
        }
        // Equal
        else {
            return 0;
        }
    }
    @Override public String toString() {
        StringBuilder chainAsString = new StringBuilder("-");
        for (int i = 0; i < amountOfPennantsOnChain; i++) {
            chainAsString.append(chain[i].getColor().charAt(0)).append("-");
        }
        return chainAsString.toString();
    }
    public String getInfos() {
        StringBuilder distances = new StringBuilder();
        StringBuilder indices = new StringBuilder();
        int index = 0;
        for (Pennant pennant : chain) {
            distances.append(" ").append(pennant.getMinDistanceToNextSameColorPennant());
            indices.append(" ").append(index++);
        }

        return "## CHAIN INFOS ##\n" +
                "Distance:  " + minimalDistance + "\n" +
                "Frequency: " + frequency + "\n" +
                "Chain:     " + indices + "\n" +
                "           " + this + "\n" +
                "           " + distances + "\n";
    }
    public PennantChain copy() {
        // Shallow copy of chain
        PennantChain copiedPennantChain = new PennantChain(this.chain.length, false);
        // Copy pennants on chain
        for (int i=0; i<this.amountOfPennantsOnChain; i++) {
            copiedPennantChain.chain[i] = this.chain[i].rawCopy();
            // Replicate links of same color neighbourhoods
            int neighbourIndex = i - (this.chain[i].getLeftSameColorNeighbourDistance()+1);
            if (neighbourIndex >= 0) {
                copiedPennantChain.chain[i].setLeftSameColorNeighbour(
                        copiedPennantChain.chain[neighbourIndex],
                        this.chain[i].getLeftSameColorNeighbourDistance());
                copiedPennantChain.chain[neighbourIndex].setRightSameColorNeighbour(
                        copiedPennantChain.chain[i],
                        copiedPennantChain.chain[i].getLeftSameColorNeighbourDistance());
            } else {
                copiedPennantChain.chain[i].setLeftSameColorNeighbour(null, copiedPennantChain.chain.length-1);
            }
        }
        // Copy quality of chain
        copiedPennantChain.minimalDistance = this.minimalDistance;
        copiedPennantChain.frequency = this.frequency;
        copiedPennantChain.amountOfPennantsOnChain = this.amountOfPennantsOnChain;
        return copiedPennantChain;
    }

}