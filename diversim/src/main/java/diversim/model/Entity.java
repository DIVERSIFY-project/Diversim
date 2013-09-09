package diversim.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import diversim.util.IndexedSortable;
import diversim.strategy.Strategy;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 * Superclass of any model that
 * -- has some services;
 * -- is part of the bipartite graph.
 * Thus, for instance platforms and apps extend this class.
 * Any Java Bean getter/setter enables read/write access to the correspondent field
 * by double clicking the entity portrayal in the GUI.
 *
 * @author Marco Biazzini
 *
 */
abstract public class Entity implements Steppable {

/**
 * See BipartiteGraph.start().
 */
protected int ID;

/**
 * All services hosted by the entity.
 */
protected ArrayList<Service> services;

/**
 * The number of link touching the entity in the bipartite graph.
 */
protected int degree;

    protected Strategy strategy;

Entity(int id, Strategy<? extends Entity> strategy) {
  ID = id;
  services = new ArrayList<Service>();
  degree = 0;
    this.strategy = strategy;
}


public int getDegree() {
  return degree;
}


public int getSize() {
  return services.size();
}


public String getComposition() {
  String res = "";
  for (Service s : services) {
    res += "s" + s.getName() + "-";
  }
  return res;
}

/**
 * This method is called at any scheduled step by the simulation engine
 * and must contain all the "intelligence" of the model.
 * By including here some diversification rule in a given order, the model can
 * affect its state and the network topology.
 *
 */
@Override
abstract public void step(SimState state);


static public <T extends Entity> List<T> findEntityWithAllServices(List<T> ens, List<Service> services) {
  List<T> list = new ArrayList<T>();
  for (T en : ens) {
    if (en.getServices().containsAll(services)) list.add(en);
  }
  Collections.sort(list, new Comparator<Entity>() {

    @Override
    public int compare(Entity e, Entity e2) {
      return e.getDegree() - e2.getDegree();
    }
  });
  return list;
}


static public <T extends Entity> List<T> findEntityWithServices(List<T> ens, List<Service> services) {
  List<T> list = new ArrayList<T>();
  for (T en : ens) {
    for (Service service : services) {
      if (en.getServices().contains(service)) {
        list.add(en);
        break;
      }
    }
  }
  Collections.sort(list, new Comparator<Entity>() {

    @Override
    public int compare(Entity e, Entity e2) {
      return e.getDegree() - e2.getDegree();
    }
  });
  return list;
}


static public void addEdge(BipartiteGraph graph, Entity e, Entity rem, Object info) {
  graph.bipartiteNetwork.addEdge(e, rem, info);
  e.incDegree();
  rem.incDegree();
  graph.changed();
}


/**
 * Returns an array with the services of this entity sorted w.r.t. the number of apps that use them.
 *
 * @param edges
 *          The edges involving this entity in the current network topology.
 * @return Sorted array of services.
 */
public ArrayList<Service> sortServices(Bag edges) {
  ArrayList<Service> res = new ArrayList<Service>(services.size());
  Integer[] counter = new Integer[services.size()];
  Arrays.fill(counter, 0);
  Entity ent;
  for (Edge e : (Edge[])edges.toArray(new Edge[0])) {
    //double weight = ((Number)(e.info)).doubleValue();
    ent = (Entity)e.getOtherNode(this);
    countCommonServices(ent, counter);
  }
  int sorting[] = IndexedSortable.sortedPermutation(counter, true);
  for (int i : sorting) {
    res.add(services.get(i));
  }
  return res;
}


/**
 * Count the service belonging to both this entity and the first argument.
 * The second argument (if not null) is an array of counters (one slot per service)
 * that is updated by this method. It can be used in subsequent calls to
 * sum the occurrences of the services in different entities.
 * @param e
 * @param counter
 * @return The number of services in common.
 */
public int countCommonServices(Entity e, Integer[] counter) {
  int indx, res = 0;
  for (Service s : e.services) {
    indx = Collections.binarySearch(services, s);
    if (indx >= 0) {
      res++;
      if (counter != null) counter[indx]++;
    }
  }
  return res;
}


@Override
public String toString() {
  String res = "";
  res += this.getClass().getSimpleName()
      + " " + ID
      + " : degree = " + degree
      + " ; size = " + getSize()
      + " ; composition = " + getComposition();
  return res;
}

    public ArrayList<Service> getServices() {
        return services;
    }

    public void incDegree() {
        degree++;
    }

    public void decDegree() {
        degree--;
    }
}
