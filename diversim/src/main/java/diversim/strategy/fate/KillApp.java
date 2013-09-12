package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.App;
import diversim.model.Fate;
import diversim.strategy.AbstractStrategy;

import java.util.ArrayList;
import java.util.Collections;


import sim.util.distribution.Distributions;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:20 PM
 */
public class KillApp extends AbstractStrategy<Fate> {

private ArrayList<Integer> timing;
long counter;

double eta; // expected time unit for 63,2% failures
double beta; // shape of the ditribution (> 1 incresing, < 1 decreasing failure rate)


public KillApp(String s) {
  super(s);
  counter = 0;
  timing = new ArrayList<Integer>(); // zipf-distributed sequence of timesteps.
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {

  if (counter < Math.min(graph.getMaxCycles(), Integer.MAX_VALUE) - 1
      && timing.size() < 1000) {
    double n;
    do {
      n = graph.random.nextInt((int)(graph.getMaxCycles())) * 3;
      //n = Distributions.nextWeibull(eta > 0 ? eta : graph.getMaxCycles() * 2, beta, graph.random);
    }
    while (n <= graph.schedule.getSteps());
    timing.add((int)Math.ceil(n));
    Collections.sort(timing);
    counter++;
  }
  if (graph.schedule.getSteps() >= timing.get(0)) {
    timing.remove(0);
    if (graph.getNumApps() > 0) {
      App a = graph.apps.get(graph.random.nextInt(graph.getNumApps()));
      graph.removeEntity(graph.apps, a);
      System.out.println(graph.getPrintoutHeader() + "Fate : REMOVED " + a);
    }
  }
  System.err.println(graph.getPrintoutHeader()
      + "Fate : INFO : next app failure will occur at cycle " + (int)(timing.get(0) / 3));
}

}
