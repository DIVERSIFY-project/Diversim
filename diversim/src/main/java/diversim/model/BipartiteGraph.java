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

import ec.util.MersenneTwisterFast;
import diversim.strategy.AbstractStrategy;
import diversim.strategy.NullStrategy;
import diversim.strategy.Strategy;
import diversim.strategy.application.LinkStrategy;
import diversim.strategy.fate.AddApp;
import diversim.strategy.fate.FateStrategy;
import diversim.strategy.fate.KillApp;
import diversim.strategy.platform.CloneMutate;
import diversim.strategy.platform.Split;
import diversim.strategy.platform.SplitOrClone;
import diversim.util.config.Configuration;

import sim.engine.RandomSequence;
import sim.engine.Sequence;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.*;


/**
 * Build a bipartite graph of platforms and apps linked by services provided/used.
 * Create and schedule entities (platforms and apps) as independent agents.
 * Maintain the network topology in a single data structure and takes care of its
 * consistent updating (see comments about the start() method).
 * More info in the comments of the methods.
 *
 * @author Marco Biazzini
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
int initServices;


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

/**
 * Max number of links a platform bears without triggering some diversification rule.
 */
int platformMaxLoad;

/**
 * Min number of services a platform shall host.
 */
int platformMinSize;

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
    protected RandomSequence platformSequence;

/**
 * All the apps currently in the simulation must be in this array.
 */
public ArrayList<App> apps;
    protected RandomSequence appSequence;
/**
 * All the services currently in the simulation must be in this array.
 */
public ArrayList<Service> services;

    /**
     * All the id services currently in the simulation must be in this array.
     */
    public ArrayList<Integer> servicesId;

/**
 * The bipartite graph. An edge links a platform to an app.
 * Edges are weighed according to the number of services in common between the two entities.
 */
public Network bipartiteNetwork;

/**
 * Invisible model that can affect the history of the simulation by injecting external events.
 */
public Fate fate;


private int sCounter;
private int pCounter;
private int aCounter;
protected boolean changed;
protected boolean centralized;
private boolean manualConf;
private boolean supervised;


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


public int getNumPlatforms() {
  return numPlatforms;
}


public int getNumApps() {
  return numApps;
}


public int getNumServices() {
  return numServices;
}


public double getMaxCycles() {
  return maxCycles;
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
  entityStrategies = new ArrayList<Strategy<? extends Steppable>>();
  try {
    String path = System.getenv().get("PWD");
    if (path == null) path = "/root"; // XXX ugly but effective to bypass problems with UI...
    Configuration.setConfig(path + "/diversim.conf");
      manualConf = false;
  } catch (IOException e) {
    System.err.println("WARNING : Configuration file not found. Please proceed with manual configuration.");
    manualConf = true;
    supervised = true;
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


@SuppressWarnings("unchecked")
<T extends Steppable> Strategy<T> setStrategy(String s) {
  s = s.toLowerCase();
  @SuppressWarnings("rawtypes")
  AbstractStrategy res = getElement(entityStrategies, s);
  if (res == null) {
    if (s.startsWith("fate")) {
      AbstractStrategy<T> add = (AbstractStrategy<T>)setStrategy(
          Configuration.getString(s + ".add_app", "add"));
      AbstractStrategy<T> kill = (AbstractStrategy<T>)setStrategy(
          Configuration.getString(s + ".kill_app", "kill"));
      res = new FateStrategy(Configuration.getString(s), (Strategy<Fate>)add, (Strategy<Fate>)kill);
    } else if (s.startsWith("combo")) {
      AbstractStrategy<T> sp = (AbstractStrategy<T>)setStrategy(
          Configuration.getString(s + ".split"));
      AbstractStrategy<T> cl = (AbstractStrategy<T>)setStrategy(
          Configuration.getString(s + ".clone"));
      res = new SplitOrClone(Configuration.getString(s),
          (Split)sp, Configuration.getDouble(s + ".split_factor", 1.0),
          (CloneMutate)cl, Configuration.getDouble(s + ".clone_factor", 2.0));
    } else if (s.startsWith("add")) {
      AbstractStrategy<App> st = getElement(entityStrategies,
          Configuration.getString(s + ".new_app_strategy", "Link"));
      res = new AddApp(Configuration.getString(s, "Add"), st);
    } else if (s.startsWith("kill")) {
      res = new KillApp(Configuration.getString(s, "Kill"));
    }
    else if (s.startsWith("clone")) {
      res = new CloneMutate(Configuration.getString(s), Configuration.getDouble(s + ".mutation", 0.1));
    } else if (s.startsWith("split")) {
      res = new Split(Configuration.getString(s), Configuration.getDouble(s + ".keep", 2.0));
    } else if (s.equals("link")) {
      res = new LinkStrategy("Link");
    } else
      res = new NullStrategy<Entity>();
    addUnique(entityStrategies, res);
  }
  return res;
}


private void readConfig() {
  String[] species;
  long size;
  int i, nSer;
  Strategy<? extends Steppable> st;
  int seed = Configuration.getInt("seed", 0);
  if (seed != 0) {
    random.setSeed(seed);
  }
  System.err.println("Config : seed = " + seed);
  supervised = Configuration.getBoolean("supervised");
  maxCycles = Configuration.getDouble("max_cycles", Schedule.MAXIMUM_INTEGER - 1);
  if (!supervised) {
    initApps = Configuration.getInt("init_apps");
    initPlatforms = Configuration.getInt("init_platforms");
    initServices = Configuration.getInt("init_services");
  }
  maxApps = Configuration.getInt("max_apps", 0);
  if (maxApps == 0) maxApps = Integer.MAX_VALUE;
  maxPlatforms = Configuration.getInt("max_platforms", 0);
  if (maxPlatforms == 0) maxPlatforms = Integer.MAX_VALUE;
  maxServices = Configuration.getInt("max_services", 0);
  if (maxServices == 0) maxServices = Integer.MAX_VALUE;
  platformMaxLoad = Configuration.getInt("p_max_load");
  platformMinSize = Configuration.getInt("p_min_size");
  centralized = Configuration.getBoolean("centralized");

  // create services
  species = Configuration.getSpecies("service");
  ServiceState ser;
  int ver;
  for (String s : species) {
    size = Math.round(initServices * Configuration.getDouble(s, 1));
    ser = ServiceState.valueOf(Configuration.getString(s + ".state", "OK"));
    ver = Configuration.getInt(s + ".version", 1);
    for (i = 0; i < size && numServices < initServices; i++) {
      ++sCounter;
      services.add(new Service(sCounter, sCounter, ver, ser));
      numServices++;
    }
    System.err.println("Config : INFO : created " + i + " new services of type " + s);
    //printAny(services.subList((numServices - i), numServices), "\n", System.err);
  }

  // create platforms
  species = Configuration.getSpecies("platform");
  for (String s : species) {
    size = Math.round(initPlatforms * Configuration.getDouble(s, 1));
    st = setStrategy(Configuration.getString(s + ".strategy", "null"));
    for (i = 0; i < size && numPlatforms < initPlatforms; i++) {
      //System.err.println("Config : INFO : NEW platform : " +
      createPlatform(services, st);//);
    }
    System.err.println("Config : INFO : created " + i + " new platforms of type " + s);
  }

  // create apps
  species = Configuration.getSpecies("app");
  for (String s : species) {
    size = Math.round(initApps * Configuration.getDouble(s, 1));
    nSer = Configuration.getInt(s + ".services", 0);
    st = setStrategy(Configuration.getString(s + ".strategy", "Link"));
    for (i = 0; i < size && numApps < initApps; i++) {
      //System.err.println("Config : INFO : NEW app : " +
      createApp(selectServices(nSer), st);//);
    }
    System.err.println("Config : INFO : created " + i + " new apps of type " + s);
  }

  // create the fate model
  st = setStrategy(Configuration.getString("fate"));
  fate = new Fate((FateStrategy)st);

  System.err.println("Config : INFO : the following strategies have been initialized :");
  printAny(entityStrategies, "\n", System.err);

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
  numPlatforms = 0;
  numApps = 0;
  numServices = 0;
  sCounter = 0;
  pCounter = 0;
  aCounter = 0;
  changed = true;
  centralized = false;

    //init the scheduler for apps and platforms
    platformSequence = new RandomSequence(new ArrayList());
    appSequence = new RandomSequence(new ArrayList());
    schedule.scheduleRepeating(new Sequence(new RandomSequence[]{platformSequence,appSequence}));

  if (manualConf) {
    // create services
    for (int i = 0; i < initServices; i++) {
      services.add(new Service(++sCounter));
      numServices++;
    }

    Strategy<App> sApp;

    // create platforms
    for (int i = 0; i < initPlatforms; i++) { // all services to each platform
      createPlatform(services, new SplitOrClone("Combo", new Split("Split", 0.5), 2.0,
          new CloneMutate("Clone", 0.1), 1.1));
    }

    // create apps
    for (int i = 0; i < initApps; i++) {
      createApp(selectServices(0), new LinkStrategy("Link"));
    }

    // create the fate model
    sApp = new LinkStrategy("Link");
    fate = new Fate(new FateStrategy("Fate", new AddApp("Add", sApp), new KillApp("Kill")));
  } else
    readConfig();

  schedule.scheduleRepeating(schedule.getTime() + 1.2, fate, 1.0);

  // An invisible model will printout the state of the graph after all the entities
  // (platform and apps) have done one step, but before the fate might do something.
  // Thus at each epoch the order of the events is: all the entities (randomly shuffled),
  // then the network printout, then fate.
  Steppable print = new Steppable() {
    public void step(SimState state) {
      if (changed)
        printoutNetwork();
      changed = false;
    }
  };
  schedule.scheduleRepeating(schedule.getTime() + 1.1, print, 1.0);
}


public static void main(String[] args) {
  doLoop(BipartiteGraph.class, args);
  System.exit(0);
}


/**
 * Returns a random selection of services. The number of services returned is specified by the given argument.
 * If the argument is <= 0, a gaussian-distributed number of randomly selected services is returned.
 * The number of services returned is always at least 1 and at most {@link #numServices}.
 *
 * @param n
 *          Number of services to return.
 * @return A random selection of services.
 */
public ArrayList<Service> selectServices(int n) {
  ArrayList<Service> servs = new ArrayList<Service>();
  if (n < 1) n = (int)((random.nextGaussian() + 3) / 6 * (numServices - 1) + 1);
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
public Platform createPlatform(List<Service> servs, Strategy strategy) {
  Platform platform = new Platform(++pCounter, servs, strategy);
  bipartiteNetwork.addNode(platform);
  platforms.add(platform);
  numPlatforms++;
  changed = true;
//  schedule.scheduleRepeating(platform);
    platformSequence.addSteppable(platform);
  return platform;
}


/**
 * This method should always be used by an entity to create a new app in the simulation.
 * It takes care of adding the app to the global arrays, to the network and
 * to schedule it with the other entities.
 * @param servs
 * @return
 */
public App createApp(List<Service> servs, Strategy s) {
  App app = new App(++aCounter, servs, s);
  bipartiteNetwork.addNode(app);
  apps.add(app);
  numApps++;
  changed = true;
//  schedule.scheduleRepeating(app);
    appSequence.addSteppable(app);
  return app;
}


/**
 * Textual printout of the network
 */
private void printoutNetwork() { // TODO
  System.out.println("Step " + schedule.getSteps() + " : " + bipartiteNetwork.toString());
  System.out.flush();
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


    public void changed() {
        changed = true;
    }

    public void removeEntity(Entity entity) {
        System.out.println("remove entity: "+entity);
       apps.remove(entity);
       appSequence.removeSteppable(entity);
       bipartiteNetwork.removeNode(entity);
    }
}
