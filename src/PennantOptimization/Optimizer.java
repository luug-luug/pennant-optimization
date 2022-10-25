package PennantOptimization;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;

public class Optimizer {
    // Attributes
    private final UserInterface UI = new UserInterface();
    private List<PennantChain> tempBestPennantChains = new ArrayList<>();

    // Methods
    // - PennantOptimization.Main Process
    public void start() {
        // Welcome
        System.out.print("""
                \n#######################
                PENNANT CHAIN OPTIMIZER
                #######################\n
                """);
        // Start interface
        while (UI.decisionInterface("\nWould you like to calculate an optimized pennant chain? (Y)/(N)\n"
        )) {
            PennantPile pennantPile = new PennantPile();
            if (!UI.decisionInterface("\nTest Mode? (Y)/(N)\n")) {
                // Creation of pennants
                String pennantColor = UI.textInterface("\nType in a pennant color!\n").toLowerCase();
                int amountOfPennants = UI.quantityInterface(
                        String.format("\nHow many %s pennants would you like to have?\n", pennantColor), 1, 50);
                pennantPile.put(pennantColor, amountOfPennants);
                while (UI.decisionInterface("\nWould you like to have additional colors? (Y)/(N)\n"
                )) {
                    pennantColor = UI.textInterface("\nType in a pennant color!\n").toLowerCase();
                    if (pennantPile.containsKey(pennantColor)) {
                        System.out.print("\nThat color is already existing.\n");
                        continue;
                    }
                    amountOfPennants = UI.quantityInterface(
                            String.format("\nHow many %s pennants would you like to have?\n", pennantColor), 1, 50);
                    pennantPile.put(pennantColor, amountOfPennants);
                }
                // Test mode: default pile of pennants
            } else {
                pennantPile.put("rot", 4);
                pennantPile.put("grün", 3);
                pennantPile.put("weiß", 2);
                pennantPile.put("schwarz", 1);
            }
            // Optimization
            UI.actionInterface("\nStart the machine! (X)\n");
            System.out.print("""
                    \n##############
                    Branch And Bound
                    ##############
                    """);
            generateBestChainsOfPennants(pennantPile.copy(), "BranchAndBound");
            for (PennantChain pennantChain : tempBestPennantChains) {
                System.out.print("\n" + pennantChain.getInfos());
            }
            System.out.print("""
                    \n############
                    Adaptive Walk
                    ###########
                    """);
            generateBestChainsOfPennants(pennantPile.copy(), "AdaptiveWalk");
            for (PennantChain pennantChain : tempBestPennantChains) {
                System.out.print("\n" + pennantChain.getInfos());
            }
            System.out.print("""
                    \n###########
                    Simulated Annealing
                    ###########
                    """);
            generateBestChainsOfPennants(pennantPile.copy(), "Simulated Annealing");
            for (PennantOptimization.PennantChain pennantChain : tempBestPennantChains) {
                System.out.print("\n" + pennantChain.getInfos());
            }
        }
        System.out.print("\nThe program is terminated.\n");
    }
    // - Optimization Procedures
    // - - General
    private void generateBestChainsOfPennants(PennantPile pennantPile, String mode) {
        tempBestPennantChains.clear();

        if (pennantPile.getTotalAmountOfPennants() == 1) {
            PennantChain pennantChain = new PennantChain(1,false);
            for (String color : pennantPile.keySet()) {
                pennantChain.addPennant(new Pennant(color)); // there is only one color
            }
            tempBestPennantChains.add(pennantChain);
            return;
        }

        switch (mode) {
            case "BranchAndBound" -> {
                tempBestPennantChains.add(new PennantChain(pennantPile.getTotalAmountOfPennants(), true));
                recAddPennant(new PennantChain(pennantPile.getTotalAmountOfPennants(), false), pennantPile);}
            case "AdaptiveWalk" ->
                    adaptiveWalk(pennantPile, 100);
            case "Simulated Annealing" ->
                    simulatedAnnealing(pennantPile, 100, 100, 0.001);
            default -> throw new IllegalArgumentException("No mode selected");
        }
    }
    // - - Branch and Bound
    // - - - Adder: Recursively adding pennants to a chain, whilst branching into different chains
    private void recAddPennant(PennantChain pennantChain, PennantPile pennantPile) {
        // Cut branch: The (incomplete) pennant chain is already worse than the best completed chain(s) so far
        if (pennantChain.compareTo(tempBestPennantChains.get(0)) < 0) {
            return;
        }
        // Tree leaf: No more pennants on the pile
        if (pennantPile.getTotalAmountOfPennants() == 0) {
            if (pennantChain.compareTo(tempBestPennantChains.get(0)) > 0) {
                tempBestPennantChains = new ArrayList<>() {{add(pennantChain);}};
            } else {
                tempBestPennantChains.add(pennantChain);
            }
        }
        // Recursive call:
        // For all pennant colors still present on the pile, the chain is copied and a pennant with that
        // color is added to that copy. The new chains are then put into a PriorityQueue and recursively called according to
        // their quality. Additionally, for each recursive call the pennant pile is copied and a pennant with that last added
        // color is removed. If it was the last pennant of that color, the color is removed.
        PriorityQueue<PennantChain> queueOfRecPennantChains = new PriorityQueue<>();
        for (String pennantColor : pennantPile.keySet()) {
            PennantChain recPennantChain = pennantChain.copy().addPennant(new Pennant(pennantColor));
            queueOfRecPennantChains.add(recPennantChain);
        }
        for (PennantChain recPennantChain : queueOfRecPennantChains) {
            String lastPennantColor = recPennantChain.getChain()[recPennantChain.getAmountOfPennantsOnChain()-1].getColor();
            PennantPile recPennantPile = pennantPile.copy();
            recPennantPile.put(lastPennantColor, recPennantPile.get(lastPennantColor) - 1);
            if (recPennantPile.get(lastPennantColor) <= 0) {
                recPennantPile.remove(lastPennantColor);
            }
            recAddPennant(recPennantChain, recPennantPile);
        }
    }
    // - - Random
    // - - - General
    private PennantChain generateRandomChain(PennantPile pennantPile) {
        List<Pennant> listOfPennants = new ArrayList<>();
        for (String pennantColor : pennantPile.keySet()) {
            for (int i=0; i<pennantPile.get(pennantColor);i++) {
                listOfPennants.add(new Pennant(pennantColor));
            }
        }
        PennantChain pennantChain = new PennantChain(pennantPile.getTotalAmountOfPennants(), false);
        int i;
        while (!listOfPennants.isEmpty()) {
            i = (int) Math.floor(Math.random() * listOfPennants.size());
            pennantChain.addPennant(listOfPennants.get(i));
            listOfPennants.remove(i);
        }
        return pennantChain;
    }
    // - - - Adaptive Walk
    private void adaptiveWalk(PennantPile pennantPile, int limitForPotentialImprovementIterations) {
        if (limitForPotentialImprovementIterations <= 0) {
            throw new IllegalArgumentException("Only positive limits for potential improvement iterations");
        }
        PennantChain pennantChain = generateRandomChain(pennantPile.copy());
        PennantChain newPennantChain;
        int count = 0;
        while (count < limitForPotentialImprovementIterations) {
            newPennantChain = generateRandomChain(pennantPile.copy());
            if (newPennantChain.compareTo(pennantChain) > 0) {
                pennantChain = newPennantChain;
                count = 0;
            } else {
                count++;
            }
        }
        tempBestPennantChains.add(pennantChain);
    }
    // - - - Simulated Annealing
    // FIXME metropolis calculation produces not-terminating-loop
    private void simulatedAnnealing(PennantPile pennantPile, int limitForPotentialImprovementIterations, double temperature, double coolingStep) {
        if (coolingStep <= 0) {
            throw new IllegalArgumentException("Inadequate cooling steps");
        }
        if (limitForPotentialImprovementIterations <= 0) {
            throw new IllegalArgumentException("Only positive limits for potential improvement iterations");
        }

        PennantChain pennantChain = generateRandomChain(pennantPile);
        System.out.println(pennantChain);
        int count = 0;
        int i, j;
        double metropolis;
        while (count < limitForPotentialImprovementIterations) {
            // Create similar chain
            PennantChain newPennantChain = pennantChain.copy();
            i = (int) Math.floor(Math.random() * pennantPile.getTotalAmountOfPennants());
            j = (int) Math.floor(Math.random() * pennantPile.getTotalAmountOfPennants());
            newPennantChain.swapPennants(i,j);
            // Compare chains
            metropolis = Math.exp(-(newPennantChain.measureQualityIndex()-pennantChain.measureQualityIndex())/temperature);
            System.out.println(
                    newPennantChain.measureQualityIndex() + " - " + pennantChain.measureQualityIndex() + " / " + temperature);
            System.out.println("metr:" + metropolis);
            if (
                    newPennantChain.compareTo(pennantChain) > 0 ||
                            Math.random() <= metropolis
            ) {
                pennantChain = newPennantChain;
                count = 0;
            } else {
                count++;
            }
            temperature -= coolingStep;
            if (temperature < 0) {
                break;
            }
        }
        tempBestPennantChains.add(pennantChain);
    }
}