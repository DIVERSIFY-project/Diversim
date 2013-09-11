package diversim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ec.util.MersenneTwisterFast;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.distribution.*;


/**
 * "You can't fight fate!"
 * ... That's why you'd better adapt!
 * This agent can be used to inject in the simulation all the events that are
 * 'external' to the other entities. E.g.: arrival of new apps, failure of apps or services,
 * runtime change of some configuration parameter, etc.
 * 
 * Now, as apps and platforms evolve themselves, fate is not really useful.
 *
 * @author Marco Biazzini
 * @author Hui Song
 *
 */
public class Fate implements Steppable {

int numAdditionalApps = 20;
ArrayList<Integer> timing;


public Fate(MersenneTwisterFast random) {

  //testDistribution();
  int n;
  timing = new ArrayList<Integer>(); // zipf-distributed sequence of timesteps.
  for (int i = 0; i < numAdditionalApps; i++) {
    n = Distributions.nextZipfInt(1.1, random);
    timing.add(n);
  }
  Collections.sort(timing);
}


// add apps timing their injection according to a zipf distribution
@Override
public void step(SimState state) {
  BipartiteGraph graph = (BipartiteGraph)state;
  if (timing.size() > 0 && state.schedule.getSteps() > timing.get(0)) {
    timing.remove(0);
    App app = graph.createApp(graph.selectServices());
    graph.createLinks(app, graph.platforms);
    System.out.println("Step " + state.schedule.getSteps() + " : NEW " + app.toString());
  }
  System.out.flush();

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
  }
  catch (InterruptedException e) {
    e.printStackTrace();
  }
  finally {
    System.exit(1);
  }
}

}
