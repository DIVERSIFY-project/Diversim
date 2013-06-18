package diversim;


import java.util.List;

import sim.engine.SimState;


public class App extends Entity {

double redundancy = 0;


public double getRedundancy() {
  return redundancy;
}


public App(int id, List<Service> servs) {
  super(id);
  for (Service s : servs) {
    BipartiteGraph.addUnique(services, s);
  }
}


@Override
public void step(SimState state) {
  BipartiteGraph graph = (BipartiteGraph) state;

// TODO something

  degree = graph.bipartiteNetwork.getEdgesIn(this).size();
  redundancy = ((double)degree) / graph.numPlatforms;
  System.out.println("Step " + state.schedule.getSteps() + " : " + toString());
}


@Override
public String toString() {
  String res = super.toString();
  res += " ; redundancy = " + redundancy;
  return res;
}

}
