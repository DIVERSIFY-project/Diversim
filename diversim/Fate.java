package diversim;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.util.distribution.*;


/** You can't fight fate... So you'd better adapt to it! */
public class Fate extends Entity {

ArrayList<App> apps = new ArrayList<App>();




public Fate(BipartiteGraph graph) {
  for (int i = 0; i < 1000; i++) {
    System.out.print("    " + Distributions.nextLogistic(graph.random));
    if (i % 3 == 0)
      System.out.println();
  }

//  graph.schedule.scheduleRepeating(graph.schedule.getTime() + 1.2, this, 1.0);
}


@Override
public void step(SimState state) {
  // TODO Auto-generated method stub

}

}
