package diversim.model;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;
import diversim.strategy.Strategy;
import diversim.util.config.Configuration;
import ec.util.MersenneTwisterFast;


/**
 * Build a bipartite graph of platforms and apps linked by services provided/used.
 * Create and schedule entities (platforms and apps) as independent agents.
 * Maintain the network topology in a single data structure and takes care of its
 * consistent updating (see comments about the start() method).
 * More info in the comments of the methods.
 *
 * In a nutshell:
 * We have a two communities of species: platforms and apps
 * Each specie of platform is description of the set of services it supports.
 * Each individual platform-instance is an individual of a particular specie.
 * There can be many species, but a specie with no individual
 * platform-instances is extinct.
 *
 * Each specie of app is a description of the set of services it needs. Each
 * app-instance is an individual of a particular specie. If an app-instance
 * cannot find a platform-instance that supports at least its required set of
 * services, it dies.
 *
 * Each platform-instance supports one app-instance.
 *
 * For visualization purposes:
 * We need to show specie-level interactions, perhaps(?) instead of individual
 * level interactions.
 *
 * @author Marco Biazzini
 * @author Vivek Nallur
 * @author Hui Song
 *
 */
public class BipartiteGraph extends SimState {

/**
 * Initial number of platforms.
 */
int initPlatforms;

/**
 * Initial number of apps.
 */
int initApps;

/**
 * Initial total number of services
 */
public static int initServices;


/**
 * Maximum number of platforms.
 */
int maxPlatforms;

/**
 * Maximum number of apps.
 */
int maxApps;

/**
 * Maximum total number of services.
 */
int maxServices;

int platformMaxLoad = 4; // FIXME

/**
 * Min number of services a platform shall host.
 */
int platformMinSize = 3;

/**
 * x out of 100 possibility to allow an App to reproduce itself.
 */
int percentAppReproduce = 1;  // 1%
int percentPlatformReproduce = 1; // 1%


/**
 * Maximum number of scheduled events per agent.
 */
double maxCycles;


/**
 * All the platforms' strategies must be in this array.
 */
public ArrayList<Strategy<? extends Steppable>> entityStrategies;


/**
 * All the platforms currently in the simulation must be in this array.
 */
public ArrayList<Platform> platforms;


/**
 * All the apps currently in the simulation must be in this array.
 */
public ArrayList<App> apps;


/**
 * All the services currently in the simulation must be in this array.
 */
public ArrayList<Service> services;


/**
 * The bipartite graph. An edge links a platform to an app.
 * Edges are weighed according to the number of services in common between the two entities.
 */
public Network bipartiteNetwork;

/**
 * Invisible agent that can affect the history of the simulation by injecting external events.
 */
public Fate fate;


protected boolean changed;

public static BipartiteGraph INSTANCE = null;

protected boolean centralized;
private boolean supervised;
private static String configPath;
public int stepsPerCycle;

/**
 * Getters and setters.
 * Any Java Bean getter/setter is auto-magically included in the GUI.
 * If a variable has an associated setter here, it can be modify at runtime via the Model tab of the GUI.
 */

public int getInitPlatforms() {
  return initPlatforms;
}


public void setInitPlatforms(int p) {
  initPlatforms = p;
}


public int getInitApps() {
  return initApps;
}


public void setInitApps(int p) {
  initApps = p;
}


public int getInitServices() {
  return initServices;
}


public void setInitServices(int p) {
  initServices = p;
}


public int getMaxPlatforms() {
  return maxPlatforms;
}


public void setMaxPlatforms(int p) {
  maxPlatforms = p;
}


public int getMaxApps() {
  return maxApps;
}


public void setMaxApps(int p) {
  maxApps = p;
}


public int getMaxServices() {
  return maxServices;
}


public void setMaxServices(int p) {
  maxServices = p;
}


public int getPlatformMaxLoad() {
  return platformMaxLoad;
}


public void setPlatformMaxLoad(int newLoad) {
  platformMaxLoad = newLoad;
}


public int getPlatformMinSize() {
  return platformMinSize;
}


public void setPlatformMinSize(int minsize) {
  platformMinSize = minsize;
}


/**
 * Current number of platforms.
 */
public int getNumPlatforms() {
	return platforms.size();
}


/**
 * Current number of apps.
 */
public int getNumApps() {
	return apps.size();
}


/**
 * Current number of services.
 */
public int getNumServices() {
	return services.size();
}


public double getMaxCycles() {
  return maxCycles;
}


public void setMaxCycles(double d) {
  maxCycles = d;
}


public int getCurCycle() {
	return (int)Math.ceil(((double)schedule.getSteps()) / stepsPerCycle);
}


public double getAvgPlatformDegree() {
  int sum = 0;
  if (schedule.getTime() <= Schedule.BEFORE_SIMULATION
      || getNumPlatforms() == 0)
    return 0.0;
  for (Platform p : platforms) {
    sum += p.getDegree();
  }
  return sum / getNumPlatforms();
}


public double getAvgAppDegree() {
  int sum = 0;
  if (schedule.getTime() <= Schedule.BEFORE_SIMULATION
      || getNumApps() == 0)
    return 0.0;
  for (App a : apps) {
    sum += a.getDegree();
  }
  return sum / getNumApps();
}


public double getAvgPlatformSize() {
  int sum = 0;
  if (schedule.getTime() <= Schedule.BEFORE_SIMULATION
      || getNumPlatforms() == 0)
    return 0.0;
  for (Platform p : platforms) {
    sum += p.getSize();
  }
  return sum / getNumPlatforms();
}


public double getAvgAppSize() {
  int sum = 0;
  if (schedule.getTime() <= Schedule.BEFORE_SIMULATION
      || getNumApps() == 0)
    return 0.0;
  for (App a : apps) {
    sum += a.getSize();
  }
  return sum / getNumApps();
}


/**
 * Dynamic persistent data structures should be created here.
 */
private void init() {
  // create fields (executed only once).
  // After stop or deserialization from checkpoint, only start() is called.
  bipartiteNetwork = new Network(false);
  platforms = new ArrayList<Platform>();
  apps = new ArrayList<App>();
  services = new ArrayList<Service>();
  entityStrategies = new ArrayList<Strategy<? extends Steppable>>();
  try {
//    configPath = System.getenv().get("PWD");
//    if (configPath == null) configPath = "/root"; // XXX ugly but effective to bypass problems with UI...
    configPath = "neutralModel.conf";
    Configuration.setConfig(configPath);
    stepsPerCycle = 0;
    INSTANCE = this;
  } catch (IOException e) {
    System.err.println("ERROR : Configuration file not found.");
    System.exit(1);
  }
}


public BipartiteGraph(long seed) {
  super(seed);
  init();
}


public BipartiteGraph(MersenneTwisterFast random) {
  super(random);
  init();
}


public BipartiteGraph(MersenneTwisterFast random, Schedule schedule) {
  super(random, schedule);
  init();
}


public BipartiteGraph(long seed, Schedule schedule) {
  super(seed, schedule);
  init();
}


private void readConfig() {
  if (!supervised) {
    initApps = Configuration.getInt("init_apps");
    initPlatforms = Configuration.getInt("init_platforms");
    initServices = Configuration.getInt("init_services");
  } else {
    try {
      Configuration.setConfig(configPath);
    } catch (IOException e) {
      System.err.println("WARNING : Configuration file not found. Using previous configuration.");
    }
  }
  int seed = Configuration.getInt("seed", 0);
  if (seed != 0) {
    random.setSeed(seed);
  }
  System.err.println("Config : seed = " + seed);
  supervised = Configuration.getBoolean("supervised");
  maxCycles = Configuration.getDouble("max_cycles", Schedule.MAXIMUM_INTEGER - 1);
  maxApps = Configuration.getInt("max_apps", 0);
  if (maxApps == 0) maxApps = Integer.MAX_VALUE;
  maxPlatforms = Configuration.getInt("max_platforms", 0);
  if (maxPlatforms == 0) maxPlatforms = Integer.MAX_VALUE;
  maxServices = Configuration.getInt("max_services", 0);
  if (maxServices == 0) maxServices = Integer.MAX_VALUE;
  platformMaxLoad = Configuration.getInt("p_max_load");
  platformMinSize = Configuration.getInt("p_min_size");
  centralized = Configuration.getBoolean("centralized");
}


protected void initFate() throws IllegalAccessException, InstantiationException,
    ClassNotFoundException {
	Strategy<?> st = getStrategy(Configuration.getString("fate.strategy", null));
	if (st != null)
    fate = new Fate((Strategy<Fate>)st);
  else
    fate = null;
}


// create initial platforms
protected void initPlatform() throws IllegalAccessException, InstantiationException,
    ClassNotFoundException {
	int c = getNumPlatforms();
	for (String kind : Configuration.getSpecies("platform")) {
		createEntities(kind, initPlatforms, platforms);
		System.err.println("Config : INFO : created " + (getNumPlatforms() - c) + " new platforms of type " + kind);
		c = getNumPlatforms();
	}
}


// create initial apps
protected void initApp() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
	int c = getNumApps();
	for (String s : Configuration.getSpecies("app")) {
		createEntities(s, initApps, apps);
		System.err.println("Config : INFO : created " + (getNumApps() - c) + " new apps of type " + s);
		c = getNumApps();
	}
}


protected void initServices() {

	for (String s : Configuration.getSpecies("service")) {
		long size = Math.round(initServices * Configuration.getDouble(s, 1));
		for (int i = 0; i < size && getNumServices() < initServices; i++) {
			services.add(new Service(Service.counter, Service.counter, 1, ServiceState.OK));
			Service.counter++;
		}
		System.err.println("Config : INFO : created " + " new services of type " + s);
	}
}


/**
 * This method is called ONCE at the beginning of every simulation.
 * EVERY field, parameter, structure etc. MUST be initialized here (and not in the constructor).
 */
public void start() {
  super.start();
  // reset all parameters and fields
  platforms.clear();
  apps.clear();
  services.clear();
  bipartiteNetwork.clear();
  entityStrategies.clear();
  changed = true;
  centralized = false;
  stepsPerCycle = 0;

  readConfig();
	initServices();
	try {
		initFate();
		initPlatform();
		initApp();
	} catch (Exception e) {
		e.printStackTrace();
	}
  if (!centralized) stepsPerCycle++;

  schedule.scheduleRepeating(schedule.getTime() + 1.1, new ReConnect(), 1.0);
  stepsPerCycle++;

  if (fate != null) {
    schedule.scheduleRepeating(schedule.getTime() + 1.3, fate, 1.0);
    stepsPerCycle++;
  }

  // An invisible model will printout the state of the graph after all the entities
  // (platform and apps) have done one step, but before the fate might do something.
  // Thus at each epoch the order of the events is: all the entities (randomly shuffled),
  // then the network printout, then fate.
  stepsPerCycle++;
  Steppable print = new Steppable() {
    public void step(SimState state) {
      if (changed)
        printoutNetwork();
      changed = false;
      if ((state.schedule.getSteps() + stepsPerCycle) >= getMaxCycles() * stepsPerCycle)
        state.schedule.seal();
    }
  };
  schedule.scheduleRepeating(schedule.getTime() + 1.2, print, 1.0);

}


public static void main(String[] args) {
  doLoop(BipartiteGraph.class, args);
  System.exit(0);
}


/**
 * Returns a random selection of services. The number of services returned is specified by the given argument.
 * If the argument is <= 0, a gaussian-distributed number of randomly selected services is returned.
 * The number of services returned is always at least 1 and at most {@link #getNumServices()}.
 *
 * @param n
 *          Number of services to return.
 * @return A random selection of services.
 */
public ArrayList<Service> selectServices(int n) {
  ArrayList<Service> servs = new ArrayList<Service>();
  if (n < 1) n = (int)((random.nextGaussian() + 3) / 6 * (getNumServices() - 1) + 1);
  n = n < 1 ? 1 : n > getNumServices() ? getNumServices() : n;
  for (int j = 0; j < n; j++) {
    servs.add(services.get(random.nextInt(getNumServices())));
  }

  return servs;
}


/**
 * This method should always be used by an entity to create a new entity (app, platform or fate). It
 * takes care of adding the entity to the global arrays, to the network and to schedule it with the
 * other entities.
 * 
 * @param entityName
 * @return
 */
public void createEntities(String entityName, long maxSize, List<? extends Entity> all)
    throws IllegalAccessException, InstantiationException, ClassNotFoundException {
	long bound = Math.round(maxSize * Configuration.getDouble(entityName + ".ratio"));
	for (int i = 0; i < bound; i++) {
		Entity entity = createEntity(entityName);

		addUnique(all, entity);
	}
	changed = true;
}


public Entity createEntity(String entityName) throws ClassNotFoundException,
    IllegalAccessException, InstantiationException {
	String className = Configuration.getString(entityName + ".class");
	Class cl = Class.forName(className);
	Entity entity = (Entity)cl.newInstance();
	entity.init(entityName, this);
	bipartiteNetwork.addNode(entity);
	entity.setStoppable(schedule.scheduleRepeating(entity));
	return entity;
}


public App createApp(String entityName)  {
	App app = null;
	try {
		app = (App)createEntity(entityName);
	} catch (Exception e) {
		new Exception(e);
	}
	addUnique(apps, app);
	return app;
}


public Platform createPlatform(String entityName) {
	Platform app = null;
	try {
		app = (Platform)createEntity(entityName);
	} catch (Exception e) {
		new Exception(e);
	}
	addUnique(platforms, app);
	return app;
}


public static Strategy<?> getStrategy(String strategyName)  {
	String id = "";
	for (String strategy : Configuration.getSpecies("strategy")) {
		if (strategyName.equals(Configuration.getString(strategy + ".name"))) {
			id = strategy;
			break;
		}
	}
	String className = Configuration.getString(id + ".class");
	Strategy<?> strategy = null;
	try {
		Class cl = Class.forName(className);
		strategy = (Strategy<?>)cl.newInstance();
		strategy.init(id);
	} catch (Exception e) {
		new Exception(e);
	}


	return strategy;
}

public void addEdge(Entity from, Entity to, Object info) {
  bipartiteNetwork.addEdge(from, to, info);
  from.incDegree();
  to.incDegree();
  changed = true;
}


public void removeEdge(Entity e, Edge edge) {
  Object rem = edge.getOtherNode(e);
  bipartiteNetwork.removeEdge(edge);
  e.decDegree();
  ((Entity)rem).decDegree();
  changed = true;
}


public void removeAllEdges() {
	bipartiteNetwork.removeAllEdges();
	for (Object n : bipartiteNetwork.getAllNodes()) {
		((Entity)n).degree = 0;
	}
	changed = true;
}


/**
 * Update existing links that have the argument at one end.
 * This method should always be used by an entity after triggering
 * some diversification rule that doesn't modify edges directly,
 * but implies (as a side effect) that existing edges to/from the entity
 * had become inconsistent and should be fixed.
 * @param e
 */
public void updateLinks(Entity e) {
  // the graph is undirected, thus EdgesIn = EdgesOut
  Bag edges = bipartiteNetwork.getEdgesIn(e); // read-only!
  int w; Entity rem; Edge edge;
  for (Object o : edges) {
    edge = (Edge)o;
    rem = (Entity)edge.getOtherNode(e);
    w = e.countCommonServices(rem, null);
    if (((Number)edge.info).intValue() != w) {
      bipartiteNetwork.removeEdge(edge);
      if (w > 0) {
        bipartiteNetwork.addEdge(e, rem, new Integer(w));
      } else {
        e.decDegree();
        rem.decDegree();
      }
      changed = true;
    }
  }
}


/**
 * Associate links to/from a NEWLY created entity.
 * To be used only for a new entity, if needed.
 * The second argument is a list of existing entities to consider linking with.
 * @param e New entity to be introduced in the network.
 * @param entities The entities to establish links with.
 */
public void createLinks(Entity e, ArrayList<? extends Entity> entities) {
  int l, r, weight;
  for (Entity remote : entities) {
    weight = l = r = 0; // services are sorted according to their ID...
    while (l < e.services.size() && r < remote.services.size()) {
      if (e.services.get(l).equals(remote.services.get(r))) {
        weight++;
        l++;
        r++;
      } else if (e.services.get(l).compareTo(remote.services.get(r)) > 0) {
        r++;
      } else {
        l++;
      }
    }
    if (weight > 0) {
      addEdge(e, remote, new Integer(weight));
    }
  }

}


/**
 * Textual printout of the network
 */
private void printoutNetwork() { // TODO
  System.out.println(getPrintoutHeader() + bipartiteNetwork.toString());
  System.out.flush();
}


/**
 * Standard header for all printout lines.
 * @return The standard printout header.
 */
public String getPrintoutHeader() {
	return "Cycle " + getCurCycle() + " [" + schedule.getSteps() + "] : ";
}


/**
 * It gets a object from a given list, that matches the given target. The object is cast to the
 * runtime class of the variable to which is assigned to, with NO type check. The list must be
 * ordered according to the natural ordering of the items. If the list contains duplicates, the
 * element returned is the one that would be found by {@link java.util.Collections#binarySearch}.<br>
 * No type checking on the argument to search for is performed. Thus the caller must be sure that
 * the arguments are mutually comparable.
 *
 * @param list
 *          The list that hosts the items
 * @param target
 *          An object comparable with the elements in the list
 * @return The object in the list that matches target, or null.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public static <T> T getElement(List list, Comparable target) {
  int i = Collections.binarySearch(list, target);
  if (i >= 0) {
    return (T)list.get(i);
  } else
    return null;
}


/**
 * It adds {@link java.lang.Comparable} objects (of any type) to the given list. The list will be
 * always ordered according to the natural ordering of the items. No duplicates are allowed in the
 * list, thus no addition occurs if an item is already in the list.<br>
 * No type checking on the objects being added is performed. Thus the caller must be sure that the
 * items being added are consistent with respect to their mutual comparison.
 *
 * @param set
 *          The list that hosts the items
 * @param item
 *          The object to be added
 * @return The [0, set.size()) index of the item in the List.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public static int addUnique(List set, Comparable item) {
  int i = Collections.binarySearch(set, item);
  if (i < 0) {
    i = -i - 1;
    set.add(i, item);
  }
  return i;
}


/**
 * It provides the printout of the given data in the given output stream.
 * If the argument is an array, print one element per line, each line starting
 * with the array index of the element.
 * It does not handle Interfaces and Enums.
 *
 * @param data
 *          Data to be printed
 * @param trailer
 *          A string that will always be printed after the data
 * @param out
 *          Stream in which the data printout must be written
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
static public void printAny(Object data, String trailer, PrintStream out) {
  int size, i = 0;
  if (data == null) {
    out.print("NULL");
  } else if (data instanceof Map) {
    Entry ec = null;
    Iterator ecit = ((Map)data).entrySet().iterator();
    while (ecit.hasNext()) { // && i++ < 3
      ec = (Map.Entry)ecit.next();
      printAny(ec.getKey(), " :\n", out);
      printAny(ec.getValue(), "\n------------------------------", out);
    }
  } else if (data instanceof List) {
    List<Object> a = (List<Object>)data;
    size = a.size();
    for (i = 0; i < size; i++) { // && i < 5
      out.print(" entry # " + i + " : ");
      printAny(a.get(i), "\n", out);
    }
  } else if (data.getClass().isArray()) {
    Object e;
    size = Array.getLength(data);
    for (i = 0; i < size; i++) { // && i < 5
      e = Array.get(data, i);
      out.print(" [" + i + "] ");
      printAny(e, "\n", out);
    }
  } else if (data.getClass().isPrimitive()) {
    out.print(data);
  } else if (!(data.getClass().isEnum() || data.getClass().isInterface())) {
    out.print((data.getClass().cast(data)).toString());
  } else {
    out.println("\nERROR : cannot print " + data.getClass().toString() + " !");
  }
  out.print(trailer);
  out.flush();
}


    public <T extends Entity> void removeEntity(ArrayList<T> eList, T entity) {
       eList.remove(Collections.binarySearch(eList, entity));
       if (!centralized) entity.stop();
       Bag edges = bipartiteNetwork.getEdgesIn(entity); // edgesIn = edgesOut
       for (Object o : edges) {
         ((Entity)((Edge)o).getOtherNode(entity)).decDegree();
       }
       bipartiteNetwork.removeNode(entity);
       changed = true;
    }

}
