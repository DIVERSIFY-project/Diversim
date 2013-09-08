package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.App;
import diversim.model.Fate;
import ec.util.MersenneTwisterFast;
import diversim.strategy.AbstractStrategy;
import diversim.strategy.Strategy;
import sim.util.distribution.Distributions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:22 PM
 */
public class AddApp extends AbstractStrategy<Fate> {

    private ArrayList<Integer> timing;
    private int numAdditionalApps  = 20;

    public AddApp(String s) {
      super(s);
    }

    @Override
    public void evolve(BipartiteGraph graph, Fate agent) {
  if (timing == null) {
    int n;
    timing = new ArrayList<Integer>(); // zipf-distributed sequence of timesteps.
    for (int i = 0; i < numAdditionalApps; i++) {
      n = Distributions.nextZipfInt(1.1, graph.random);
      timing.add(n);
    }
    Collections.sort(timing);
  }

        if (timing.size() > 0 && graph.schedule.getSteps() > timing.get(0)) {
            timing.remove(0);
            App app = graph.createApp(graph.selectServices(0));
            System.out.println("Step " + graph.schedule.getSteps() + " : NEW " + app.toString());
        }
    }
}
