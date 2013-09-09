package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.App;
import diversim.model.Fate;
import diversim.strategy.AbstractStrategy;
import sim.engine.Schedule;
import sim.util.distribution.Distributions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:22 PM
 */
public class AddApp extends AbstractStrategy<Fate> {

    long counter;

    private ArrayList<Integer> timing;

    public AddApp(String s) {
      super(s);
      timing = new ArrayList<Integer>(); // zipf-distributed sequence of timesteps.
      counter = 0; // how many times a new value has been added to timing
    }

    @Override
    public void evolve(BipartiteGraph graph, Fate agent) {
  if (counter < Math.min((graph.getMaxApps() - graph.getInitApps()), Schedule.MAXIMUM_INTEGER - 1)) {
    int n;
    do {
      n = Distributions.nextZipfInt(1.1, graph.random);
    } while (n <= graph.schedule.getSteps());
    timing.add(n);
    Collections.sort(timing);
    counter++;
  }

        if (timing.size() > 0 && graph.schedule.getSteps() > timing.get(0)) {
            timing.remove(0);
            App app = graph.createApp(graph.selectServices(0));
            app.step(graph); // FIXME : this should not be necessary, but it is, due to the fact that Fate also kills app at each step...
            System.out.println("Step " + graph.schedule.getSteps() + " : NEW " + app.toString());
        }
    }
}
