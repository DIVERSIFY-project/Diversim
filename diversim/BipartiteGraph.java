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


public class BipartiteGraph extends SimState {

static final int initPlatforms = 3;
static final int initApps = 10;
static final int initServices = 30;

public int numPlatforms;
public int numApps;
public int numServices;

public Network bipartiteNetwork;
public ArrayList<Platform> platforms;
public ArrayList<App> apps;
public ArrayList<Service> services;
public Fate fate;

private int sCounter;
private int pCounter;
private int aCounter;
public boolean changed = true;


private void init() {
  numPlatforms = 0;
  numApps = 0;
  numServices = 0;

  bipartiteNetwork = new Network(false);
  platforms = new ArrayList<Platform>();
  apps = new ArrayList<App>();
  services = new ArrayList<Service>();
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


public void start() {
  super.start();
  // clear the lists of entities
  platforms.clear();
  apps.clear();
  services.clear();
  bipartiteNetwork.clear();
  sCounter = 0;
  pCounter = 0;
  aCounter = 0;

  for (int i = 0; i < initServices; i++) {
    services.add(new Service(++sCounter));
    numServices++;
  }
  for (int i = 0; i < initPlatforms; i++) {
    createPlatform(services);
  }
  for (int i = 0; i < initApps; i++) {
    createApp(services);
  }

  fate = new Fate(this);

  // define initial network: a link weight is the number of services in common
  // between the app and the platform
  // link every platform to all apps that use at least one of its services
  for (App app : apps) {
    createLinks(app, platforms);
  }

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


public Platform createPlatform(List<Service> servs) {
  Platform platform = new Platform(++pCounter, servs);
  bipartiteNetwork.addNode(platform);
  platforms.add(platform);
  numPlatforms++;
  changed = true;
  schedule.scheduleRepeating(platform);
  return platform;
}


public App createApp(List<Service> servs) {
  App app = new App(++aCounter, servs);
  bipartiteNetwork.addNode(app);
  apps.add(app);
  numApps++;
  changed = true;
  schedule.scheduleRepeating(app);
  return app;
}


// update existing links that have the argument at one end.
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


// it can works with 1 platform vs all apps or viceversa.
// to be used ONLY to associate links to/from a NEWLY created Entity
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


private void printoutNetwork() {
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


}
