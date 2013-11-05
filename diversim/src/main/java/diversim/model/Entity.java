package diversim.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.network.Edge;
import sim.util.Bag;
import diversim.strategy.Strategy;
import diversim.strategy.matching.MatchingStrategy;
import diversim.util.IndexedSortable;
import diversim.util.config.Configuration;


/**
 * Superclass of any agent that -- has some services; -- is part of the bipartite graph. Thus, for
 * instance platforms and apps extend this class. Any Java Bean getter/setter enables read/write
 * access to the correspondent field by double clicking the entity portrayal in the GUI.
 * 
 * @author Marco Biazzini
 */
abstract public class Entity implements Steppable, Comparable<Entity> {

/**
 * See BipartiteGraph.start().
 */
protected int ID;

/**
 * All services hosted by the entity.
 */
public ArrayList<Service> services;

/**
 * The number of link touching the entity in the bipartite graph.
 */
protected int degree;

/**
 * The matching strategy employed by this entity
 */
MatchingStrategy matcher;

/**
 * counter for all entity
 */
protected static int counter;

/**
 * kind of entity (all entities kinds are defined in the conf file)
 */
protected String kind;

/**
 * Internal object to be used to kill this entity (i.e. to delete it from the simulator's schedule)
 */
protected Stoppable stoppable;

protected Strategy strategy;

public boolean dead;

protected int since; // cycle at which the entity is created.


public Entity() {}


public Entity(Entity entity) {
	this.ID = entity.ID;
	this.services = entity.services;
	this.degree = entity.degree;
	this.kind = entity.kind;
	this.stoppable = entity.stoppable;
	this.strategy = entity.strategy;
	this.dead = entity.dead;
	this.since = entity.since;
}

Entity(int id, Strategy<? extends Entity> strategy) {
	ID = id;
	services = new ArrayList<Service>();
	degree = 0;
	this.strategy = strategy;
}


public int getBirthCycle() {
	return since;
}


public void init(String entityId, BipartiteGraph graph) {
	ID = counter;
	counter++;
	kind = entityId;
	services = new ArrayList<Service>();
	strategy = (Strategy<? extends Entity>)BipartiteGraph.getStrategy(Configuration.getString(kind
			+ ".strategy"));
	degree = 0;
	dead = false;
	since = graph.getCurCycle();
}


public Strategy<? extends Entity> getStrategy() {
	return strategy;
}


Entity(int id) {
	ID = id;
	services = new ArrayList<Service>();
	degree = 0;
	strategy = null;
}


public void setStrategy(Strategy<? extends Entity> s) {
	strategy = s;
}


public boolean matches(Entity target) {
	return this.matcher.matches(this, target);
}


public void setMatchingStrategy(MatchingStrategy ms) {
	this.matcher = ms;
}


public int getDegree() {
	return degree;
}


public void setDegree(int d) {
	degree = d;
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


protected void setStoppable(Stoppable s) {
	stoppable = s;
}


protected void stop() {
	stoppable.stop();
}


protected void printoutCurStep(BipartiteGraph g) {
	System.out.println(g.getPrintoutHeader() + toString());
}


/**
 * This method is called at any scheduled step by the simulation engine and must contain all the
 * "intelligence" of the agent. By including here some diversification rule in a given order, the
 * agent can affect its state and the network topology.
 */
@Override
abstract public void step(SimState state);


// XXX START OF CODE FOR LinkStrategy
static public <T extends Entity> List<T> findEntityWithAllServices(List<T> ens,
		List<Service> services) {
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


// XXX END OF CODE FOR LinkStrategy

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
		// double weight = ((Number)(e.info)).doubleValue();
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
 * Count the service belonging to both this entity and the first argument. The second argument (if
 * not null) is an array of counters (one slot per service) that is updated by this method. It can
 * be used in subsequent calls to sum the occurrences of the services in different entities.
 * 
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


public List<Service> getCommonServices(Entity entity) {
	List<Service> result = new ArrayList<Service>();
	int index = 0;
	for (Service s : entity.getServices()) {
		index = Collections.binarySearch(getServices(), s);
		if (index >= 0) {
			result.add(getServices().get(index));
		}
	}
	return result;
}


@Override
public String toString() {
	String res = "";
	res += this.getClass().getSimpleName() + " " + ID + " : degree = " + degree + " ; size = "
			+ getSize();
	// + " ; composition = " + getComposition();
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


public String getKind() {
	return kind;
}


public int compareTo(Entity e) {
	return ID - e.ID;
}


public boolean equals(Object o) {
	if (o instanceof Entity) return compareTo((Entity)o) == 0;
	return false;
}


public void setServices(List<Service> services) {
	for (Service s : services)
		BipartiteGraph.addUnique(this.services, s);
}


public boolean isAlive() {
	return !dead;
}

}
