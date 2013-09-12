package diversim.strategy.fate;


import sim.util.Bag;

import diversim.model.*;
import diversim.strategy.Strategy;
import diversim.strategy.AbstractStrategy;


public class FateAlmighty extends AbstractStrategy<Fate> {

Strategy<Fate> strategy;

public FateAlmighty(String n, Strategy<Fate> s) {
  super(n);
  strategy = s;
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {
  randomizedEntityEvolution(graph);
  strategy.evolve(graph, agent);
}


private void randomizedEntityEvolution(BipartiteGraph graph) {
  Bag entities = new Bag(graph.getNumApps() + graph.getNumPlatforms());
  for (App a : graph.apps)
    entities.add(a);
  for (Platform p : graph.platforms)
    entities.add(p);
  entities.shuffle(graph.random);
  for (Object o : entities)
    ((Entity)o).step(graph);
}

}
