package diversim;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.*;
import sim.field.network.*;


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
int initPlatforms = 3;

/**
 * Initial number of apps.
 */
int initApps = 10;

/**
 * Initial total number of services
 */
public static int initServices = 30;

/**
 * Max number of links a platform bears without triggering some diversification rule.
 */
int platformMaxLoad = 4;

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
 * Current number of platforms.
 */
public int numPlatforms;

/**
 * Current number of apps.
 */
public int numApps;

/**
 * Current number of services.
 */
public int numServices;


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


private int sCounter;
private int pCounter;
private int aCounter;
public boolean changed;

public static BipartiteGraph INSTANCE = null;



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


public int getNumPlatforms() {
  return numPlatforms;
}


public int getNumApps() {
  return numApps;
}


public int getNumServices() {
  return numServices;
}


public double getAvgPlatformDegree() {
  int sum = 0;
  if (schedule.getTime() <= Schedule.BEFORE_SIMULATION)
    return 0.0;
  for (Platform p : platforms) {
    sum += p.getDegree();
  }
  return sum / numPlatforms;
}


public double getAvgAppDegree() {
  int sum = 0;
  if (schedule.getTime() <= Schedule.BEFORE_SIMULATION)
    return 0.0;
  for (App a : apps) {
    sum += a.getDegree();
  }
  return sum / numApps;
}


public double getAvgPlatformSize() {
  int sum = 0;
  if (schedule.getTime() <= Schedule.BEFORE_SIMULATION)
    return 0.0;
  for (Platform p : platforms) {
    sum += p.getSize();
  }
  return sum / numPlatforms;
}


public double getAvgAppSize() {
  int sum = 0;
  if (schedule.getTime() <= Schedule.BEFORE_SIMULATION)
    return 0.0;
  for (App a : apps) {
    sum += a.getSize();
  }
  return sum / numApps;
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
}


public BipartiteGraph(long seed) {
  super(seed);
  init();
  INSTANCE = this;
}


public BipartiteGraph(MersenneTwisterFast random) {
  super(random);
  init();
  INSTANCE = this;
}


public BipartiteGraph(MersenneTwisterFast random, Schedule schedule) {
  super(random, schedule);
  init();
  INSTANCE = this;
}


public BipartiteGraph(long seed, Schedule schedule) {
  super(seed, schedule);
  init();
  INSTANCE = this;
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
  numPlatforms = 0;
  numApps = 0;
  numServices = 0;
  sCounter = 0;
  pCounter = 0;
  aCounter = 0;
  changed = true;

  // create services
  for (int i = 0; i < initServices; i++) {
    services.add(new Service(++sCounter));
    numServices++;
  }
  
 

  // create platforms
  for (int i = 0; i < initPlatforms; i++) { // all services to each platform
    Platform pltf = createPlatform(services);
    pltf.initStategies(this);
  }

  // create apps
  for (int i = 0; i < initApps; i++) {
    App app = createApp(selectServices());
    app.initStrategies(this);
  }


  // create the fate agent
  //fate = new Fate(random);
  //schedule.scheduleRepeating(schedule.getTime() + 1.2, fate, 1.0);

  // define initial network:
  // link every platform to all apps that use at least one of its services
  for (App app : apps) {
    createLinks(app, platforms);
    System.out.println("Step " + schedule.getSteps() + " : NEW " + app.toString());
  }

  // An invisible agent will printout the state of the graph after all the entities
  // (platform and apps) have done one step, but before the fate might do something.
  // Thus at each epoch the order of the events is: all the entities (randomly shuffled),
  // then the network printout, then fate.
  Steppable print = new Steppable() {
    public void step(SimState state) {
    	//System.out.println("Step " + state.schedule.getSteps() + " : " + "what happened?");
      if (changed)
        printoutNetwork();
      changed = false;
    }
  };
  schedule.scheduleRepeating(schedule.getTime() + 1.1, print, 1.0);
  schedule.scheduleRepeating(schedule.getTime()+1, new ReConnect());
  
}


public static void main(String[] args) {
  doLoop(BipartiteGraph.class, args);
  System.exit(0);
}


/**
 * A gaussian-distributed number of randomly selected services
 */
public ArrayList<Service> selectServices() {
  ArrayList<Service> servs = new ArrayList<Service>();
  int n = (int)((random.nextGaussian() + 3) / 6 * (numServices - 1) + 1);
  n = n < 1 ? 1 : n > numServices ? numServices : n;
  for (int j = 0; j < n; j++) {
    servs.add(services.get(random.nextInt(numServices)));
  }

  return servs;
}


/**
 * This method should always be used by an entity to create a new platform in the simulation.
 * It takes care of adding the platform to the global arrays, to the network and
 * to schedule it with the other entities.
 * @param servs
 * @return
 */
public Platform createPlatform(List<Service> servs) {
  Platform platform = new Platform(++pCounter, servs);
  bipartiteNetwork.addNode(platform);
  platforms.add(platform);
  numPlatforms++;
  changed = true;
  schedule.scheduleRepeating(platform);
  return platform;
}


/**
 * This method should always be used by an entity to create a new app in the simulation.
 * It takes care of adding the app to the global arrays, to the network and
 * to schedule it with the other entities.
 * @param servs
 * @return
 */
public App createApp(List<Service> servs) {
  App app = new App(++aCounter, servs);
  bipartiteNetwork.addNode(app);
  apps.add(app);
  numApps++;
  changed = true;
  schedule.scheduleRepeating(app);
  return app;
}

/**
 * Same as above, but the App instance is created somewhere else
 * @param app
 * @return
 */
public App addApp(App app){
	bipartiteNetwork.addNode(app);
	apps.add(app);
	numApps++;
	changed = true;
	schedule.scheduleRepeating(app);
	
	return app;
}


/**
 * Update existing links that have the argument at one end.
 * This method should always be used by an entity after triggering
 * some diversification rule that affect the network topology in the
 * portion of the graph that include the entity.
 * @param e
 */
public void updateLinks(Entity e) {
  // the graph is undirected, thus EdgesIn = EdgesOut
  Bag edges = bipartiteNetwork.getEdgesIn(e); // read-only!
  int w; Entity rem;
  for (Edge edge : (Edge[])edges.toArray(new Edge[0])) {
    rem = (Entity)edge.getOtherNode(e);
    w = e.countCommonServices(rem, null);
    if (((Number)edge.info).intValue() != w) {
      bipartiteNetwork.removeEdge(edge);
      if (w > 0) {
        bipartiteNetwork.addEdge(e, rem, new Integer(w));
      } else {
        e.degree--;
        rem.degree--;
      }
      changed = true;
    }
  }
}


/**
 * Associate links to/from a NEWLY created entity.
 * To be used always and only for a new entity.
 * The second argument is a list of existing entities to consider linking with.
 * @param e New entity to be introduced in the network.
 * @param entities The entities to establish links with.
 */
public void createLinks(Entity e, ArrayList<? extends Entity> entities) {
  int l, r, weight, count = 0;
  for (Entity remote : entities) {
    weight = l = r = 0; // services are sorted according to their ID...
    while (l < e.services.size() && r < remote.services.size()) {
      if (e.services.get(l).ID == remote.services.get(r).ID) {
        weight++;
        l++;
        r++;
      } else if (e.services.get(l).ID > remote.services.get(r).ID) {
        r++;
      } else {
        l++;
      }
    }
    if (weight > 0) {
      bipartiteNetwork.addEdge(e, remote, new Integer(weight));
      e.degree++;
      remote.degree++;
      changed = true;
    }
  }

}

public void setLink(App app, Platform pltf){
	bipartiteNetwork.addEdge(app, pltf, 1);
	app.degree ++;
	pltf.degree ++;
	changed = true;
}


/**
 * Textual printout of the network
 */
private void printoutNetwork() { // TODO
  System.out.println("Step " + schedule.getSteps() + " : " + bipartiteNetwork.toString());
  System.out.flush();
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

public Platform addPlatform(Platform platform) {
	  bipartiteNetwork.addNode(platform);
	  platforms.add(platform);
	  numPlatforms++;
	  changed = true;
	  schedule.scheduleRepeating(platform);
	  return platform;
	
}




}
