package diversim;


import diversim.model.BipartiteGraph;


public class ExternalLauncher {

public static int runNum = 10;

public static long seed = 123456789;

public static int cycles = 1000;

public static int deltaCycles = 10;

public static int initPlatforms = 10;

public static int initApps = 1000;

public static int initServices = 100;

public static int maxPlatforms = 12;

public static int maxApps = 1000;

public static int maxServices = 100;

public static int maxLoad = 100;


public static void main(String[] args) {
	BipartiteGraph.doLoop(BipartiteGraph.class, null);
}

}
