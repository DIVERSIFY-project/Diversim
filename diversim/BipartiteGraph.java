package diversim;

import java.util.ArrayList;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.util.*;
import sim.field.continuous.*;
import sim.field.network.*;


public class BipartiteGraph extends SimState {

public Continuous2D sysSpace = new Continuous2D(1.0,100,100);
public int numPlatforms = 3;
public int numApps = 3;
public int numServices = 3;
double getIn = 0.01;
double getOut = 0.1;
public Network bipartiteNetwork = new Network(false);
public ArrayList<Platform> platforms = new ArrayList<Platform>();
public ArrayList<App> apps = new ArrayList<App>();
public ArrayList<Service> services = new ArrayList<Service>();


public BipartiteGraph(long seed) {
  super(seed);
  // TODO Auto-generated constructor stub
}


public BipartiteGraph(MersenneTwisterFast random) {
  super(random);
  // TODO Auto-generated constructor stub
}


public BipartiteGraph(MersenneTwisterFast random, Schedule schedule) {
  super(random, schedule);
  // TODO Auto-generated constructor stub
}


public BipartiteGraph(long seed, Schedule schedule) {
  super(seed, schedule);
  // TODO Auto-generated constructor stub
}


public void start() {
  super.start();
  // clear the space
  sysSpace.clear();
  // clear the lists of entities
  platforms.clear();
  apps.clear();
  services.clear();
  bipartiteNetwork.clear();
  // add some entities to the space
  // TODO services
  Platform platform;
  App app;
  for (int i = 0; i < numPlatforms; i++) {
    platform = new Platform();
    sysSpace.setObjectLocation(platform, new Double2D(sysSpace.getWidth() * 0.5 + random.nextDouble() - 0.5,
        sysSpace.getHeight() * 0.25));
    bipartiteNetwork.addNode(platform);
    platforms.add(platform);
    schedule.scheduleRepeating(platform);
  }
  for (int i = 0; i < numApps; i++) {
    app = new App();
    sysSpace.setObjectLocation(app, new Double2D(sysSpace.getWidth() * 0.5 + random.nextDouble() - 0.5,
        sysSpace.getHeight() * 0.75));
    bipartiteNetwork.addNode(app);
    apps.add(app);
    schedule.scheduleRepeating(app);
  }

  // define like/dislike relationships
  Bag platforms = bipartiteNetwork.getAllNodes(); // READ-ONLY!
  for (int i = 0; i < platforms.size(); i++) {
    Object platformA = platforms.get(i);
    // who does he like?
    Object appB = null;
    do
      appB = apps.get(random.nextInt(apps.size()));
    while (platformA == appB);
    double buddiness = random.nextDouble(); // TODO set the weight of the edge as # of services
    bipartiteNetwork.addEdge(platformA, appB, new Double(buddiness));
    // who does he dislike?
    do
      appB = apps.get(random.nextInt(apps.size()));
    while (platformA == appB);
    buddiness = random.nextDouble();
    bipartiteNetwork.addEdge(platformA, appB, new Double(-buddiness));
  }

}



public static void main(String[] args) {
  doLoop(BipartiteGraph.class, args);
  System.exit(0);
}


}
