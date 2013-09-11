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

double eta; // expected time unit for 63,2% failures
double beta; // shape of the ditribution (> 1 incresing, < 1 decreasing failure rate)


public KillApp(String s) {
  super(s);
  timing = new ArrayList<Integer>(); // zipf-distributed sequence of timesteps.
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {

    double n;
    do {
      n = Distributions.nextWeibull(eta, beta, graph.random);
    }
    while (n <= graph.schedule.getSteps());
    timing.add((int)Math.round(n));
    Collections.sort(timing);

  if (timing.size() > 0 && graph.schedule.getSteps() >= timing.get(0)) {
    timing.remove(0);
    App a = graph.apps.get(graph.random.nextInt(graph.numApps));
    graph.removeEntity(a);
    System.out.println("Step " + graph.schedule.getSteps() + " " + a + " has been removed.");
  }
  System.err.println(graph.getPrintoutHeader()
      + "Fate : INFO : next app failure will occur at cycle " + (int)(timing.get(0) / 3));
}

}
