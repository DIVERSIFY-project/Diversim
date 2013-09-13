package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.App;
import diversim.model.Fate;
import ec.util.MersenneTwisterFast;
import diversim.strategy.Strategy;
import sim.util.distribution.Distributions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:22 PM
 */
public class AddApp implements Strategy<Fate> {

    private ArrayList<Integer> timing;
    private int numAdditionalApps  = 20;

    public AddApp(MersenneTwisterFast random) {
        int n;
        timing = new ArrayList<Integer>(); // zipf-distributed sequence of timesteps.
        for (int i = 0; i < numAdditionalApps; i++) {
            n = Distributions.nextZipfInt(1.1, random);
            timing.add(n);
        }
        Collections.sort(timing);
    }

    @Override
    public void evolve(BipartiteGraph graph, Fate agent) {
        if (timing.size() > 0 && graph.schedule.getSteps() > timing.get(0)) {
            timing.remove(0);
            App app = graph.createApp(graph.selectServices());
            System.out.println("Step " + graph.schedule.getSteps() + " : NEW " + app.toString());
        }
    }
}
