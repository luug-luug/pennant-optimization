# Pennant Optimization
This is a program for generating optimized chains of colored pennants. It aims at solving the pennant chain optimization problem described below. Its main purpose is to practice the implementation of different optimization algorithms.

## Pennant Chain Optimization Problem
The challenge is to generate a chain of colored pennants which is as diverse as possible, meaning, the distribution of the colors should be spread out.
This is measured, firstly, by the minimal distance between two same color pennants and, secondly, by the frequency of that distance.
Hence the optimization consists in generating a chain with a set of colored pennants with the best quality possible.

## Optimization Algorithms
So far the programm uses following techniques:
* Branch and Bound
* Stochastic Optimization Algorithms
  * Adaptive Walk
  * Simulated Annealing (not yet working)
  * Genetic Algorithms
    * Crossover
