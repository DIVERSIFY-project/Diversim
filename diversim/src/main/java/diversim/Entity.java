package diversim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import diversim.strategy.matching.MatchingStrategy;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 * Superclass of any agent that
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
	int ID;
	
	/**
	 * All services hosted by the entity.
	 */
	public ArrayList<Service> services;
	
	/**
	 * The number of link touching the entity in the bipartite graph.
	 */
	public int degree;
	
	/**
	 * The matching strategy employed by this entity
	 */
	MatchingStrategy matcher;
		

	Entity(int id) {
	  ID = id;
	  services = new ArrayList<Service>();
	  degree = 0;
	}

	public boolean matches(Entity target){
		return this.matcher.matches(this, target);
	}
	
	public void setMatchingStrategy(MatchingStrategy ms){
			this.matcher = ms;
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
	    res += s.ID + "-";
	  }
	  return res;
	}

/**
 * This method is called at any scheduled step by the simulation engine
 * and must contain all the "intelligence" of the agent.
 * By including here some diversification rule in a given order, the agent can
 * affect its state and the network topology.
 *
 */
@Override
abstract public void step(SimState state);


/** 
 * Returns an array with the services of this entity sorted w.r.t. the number
 * of apps that use them.
 * @param edges The edges involving this entity in the current network topology.
 * @return Sorted array of services.
 */
ArrayList<Service> sortServices(Bag edges) {
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
int countCommonServices(Entity e, Integer[] counter) {
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
      + " ; size = " + getSize()
      + " ; composition = " + getComposition();
  return res;
}
}
