package diversim;

import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;
import sim.util.Bag;
import sim.field.network.*;


/**
 * Platforms are agents pro-actively modifying their state according to
 * some diversification rules to be included in their step() method.
 * By modifying their state they may also affect the network topology,
 * which should be updated accordingly.
 *
 * @author Marco Biazzini
 *
 */
public class Platform extends Entity {


double pressure = 0;
String action;


public double getPressure() {
  return pressure;
}

public String getAction() {
  return action;
}


public Platform(int id, List<Service> servs) {
  super(id);
  for (Service s : servs) {
    BipartiteGraph.addUnique(services, s);
  }
  action = "none";
}


/*
 * (non-Javadoc)
 * @see diversim.Entity#step(sim.engine.SimState)
 */
@Override
public void step(SimState state) {
  BipartiteGraph graph = (BipartiteGraph)state;

  action = "none";
  if (degree > graph.platformMaxLoad)
    if (getSize() >= 2 * graph.platformMinSize) {
      split_Part(graph);
      action = "split_part";
    }
    else if (getSize() > graph.platformMinSize) {
      clone_Mutate(graph);
      action = "clone_mutate";
    }
  pressure = ((double)degree) / graph.platformMaxLoad;
  if (pressure > 1.0) pressure = 1.0;

  System.out.println("Step " + state.schedule.getSteps() + " : " + toString());
}


/**
 * Split this platform in two and partition the services, so that
 * the most common services among the linked application are kept in this
 * instance and the other half of the services are removed and assigned
 * to the newly created platform.
 *
 * @param graph
 */
private void split_Part(BipartiteGraph graph) {
  Bag out = graph.bipartiteNetwork.getEdges(this, null); // read-only!
  Edge[] edges = (Edge[])out.toArray(new Edge[0]);
  // get the services used by the apps, sorted from the most to the least common
  ArrayList<Service> sortedServices = sortServices(out);

  // split the platform and keep here only the most shared half of the services
  Platform p = graph.createPlatform(
      sortedServices.subList(sortedServices.size() / 2, sortedServices.size()));
  ArrayList<Entity> ents = new ArrayList<Entity>();
  for (Edge e : edges) {
    ents.add((Entity)e.getOtherNode(this));
  }
  graph.createLinks(p, ents);
  System.out.println("Step " + graph.schedule.getSteps() + " : NEW " + p.toString());
  for (int i = sortedServices.size() / 2; i < sortedServices.size(); i++) {
    services.remove(sortedServices.get(i));
  }
  graph.updateLinks(this);
}


/**
 * Clone this instance and mutate both this instance and the clone, so that
 * one randomly chosen service in each instance (not the same in both) is removed.
 * Then update the network so that the apps link to the proper instance(s).
 *
 * @param graph
 */
private void clone_Mutate(BipartiteGraph graph) {
  int r1, r2;
  Bag out = graph.bipartiteNetwork.getEdges(this, null); // read-only!
  Edge[] edges = (Edge[])out.toArray(new Edge[0]);
  ArrayList<Entity> ents = new ArrayList<Entity>();
  for (Edge e : edges) {
    ents.add((Entity)e.getOtherNode(this));
  }

  r1 = graph.random.nextInt(services.size());
  do
    r2 = graph.random.nextInt(services.size());
  while (r1 == r2);

  // generate a clone that has all the services of this platform but one.
  ArrayList<Service> servs = new ArrayList<Service>(services);
  servs.remove(r2);
  Platform p = graph.createPlatform(servs);
  graph.createLinks(p, ents);

  // remove a service from this platform
  services.remove(r1);
  graph.updateLinks(p);
}


@Override
public String toString() {
  String res = super.toString();
  res += " ; pressure = " + pressure
      + " ; action = " + action;
  return res;
}

}
