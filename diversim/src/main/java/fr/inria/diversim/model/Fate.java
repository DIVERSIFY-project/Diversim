package fr.inria.diversim.model;

import java.util.Arrays;


import fr.inria.diversim.strategy.Strategy;
import fr.inria.diversim.strategy.fate.FateStrategy;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.distribution.*;


/**
 * "You can't fight fate!"
 * ... That's why you'd better adapt!
 * This model can be used to inject in the simulation all the events that are
 * 'external' to the other entities. E.g.: arrival of new apps, failure of apps or services,
 * runtime change of some configuration parameter, etc.
 *
 * @author Marco Biazzini
 */
public class Fate implements Steppable {
    protected Strategy strategy;

    public Fate(FateStrategy fateStrategy) {
        strategy = fateStrategy;
    }


    // add apps timing their injection according to a zipf distribution
    @Override
    public void step(SimState state) {

        strategy.evolve((BipartiteGraph) state, this);
        System.out.flush();

    }

    protected void attackServices(BipartiteGraph graph) {
    }

    protected void killPlatform(BipartiteGraph graph) {
    }

    protected void addApplication(BipartiteGraph graph) {
    }


    // just to quickly try out how values from some distribution look like...
    private void testDistribution() {
        ec.util.MersenneTwisterFast random = new ec.util.MersenneTwisterFast();
        double sum = 0, val[] = new double[1000000];
        for (int i = 0; i < 1000000; i++) {
            val[i] = Distributions.nextZipfInt(1.1, random);
            sum += val[i];
            if (i == 999) {
                Arrays.sort(val, 0, 1000);
                System.out.println("Over 1000 : min=" + val[0] + " ; med=" + val[499] + " ; max=" + val[999]
                        + " ; avg=" + (sum / 1000));
            }
        }
        Arrays.sort(val);
        System.out.println("Over 1000000 : min=" + val[0] + " ; med=" + val[499999] + " ; max="
                + val[999999] + " ; avg=" + (sum / 1000000));
        System.out.flush();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.exit(1);
        }
    }

}
