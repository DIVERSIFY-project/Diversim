package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.strategy.util.Metrics;


public class KillFates {

public KillFates() {}


public static Map<String, Class[]> getKillingMethods() {
	Map<String, Class[]> results = new HashMap<String, Class[]>();
	Class[] args = new Class[2];
	args[0] = BipartiteGraph.class;
	args[1] = int.class;
	results.put("randomExact", args);
	args = new Class[2];
	args[0] = BipartiteGraph.class;
	args[1] = int.class;
	results.put("obsolescenceExact", args);
	args = new Class[2];
	args[0] = BipartiteGraph.class;
	args[1] = int.class;
	results.put("unattendedExact", args);
	return results;
}


public static void random(BipartiteGraph graph, double populationSize) {
	for (int i = 0; i < graph.getNumPlatforms() * populationSize; i++) {
		Platform killed = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
		graph.removeEntity(graph.platforms, killed);
		Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
		    "Platform <" + killed + "> has been killed by Random");
	}
}


public static void randomExact(BipartiteGraph graph, int amount) {
	for (int i = 0; i < amount; i++) {
		Platform killed = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
		graph.removeEntity(graph.platforms, killed);
		Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
		    "Platform <" + killed + "> has been killed by RandomExact");
	}
}


public static void backdoor(BipartiteGraph graph, Service backdoor, double populationSize) {
	int counter = (int)(graph.getNumPlatforms() * populationSize) + 1;
	for (int i = graph.getNumPlatforms() - 1; i >= 0; i--) {
		if (graph.platforms.get(i).getServices().contains(backdoor)) {
			Platform killed = graph.platforms.get(i);
			graph.removeEntity(graph.platforms, killed);
			Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
			    "Platform <" + killed + "> has been killed by Backdoor Service <"
			        + backdoor + ">");
			if (--counter <= 0) {
				break;
			}
		}
	}
}


public static void backdoor(BipartiteGraph graph, Service backdoor, int amount) {
	for (int i = graph.getNumPlatforms() - 1; i >= 0; i--) {
		if (graph.platforms.get(i).getServices().contains(backdoor)) {
			Platform killed = graph.platforms.get(i);
			graph.removeEntity(graph.platforms, killed);
			Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
			    "Platform <" + killed + "> has been killed by BackdoorExact Service <" + backdoor + ">");
			if (--amount <= 0) {
				break;
			}
		}
	}
}


public static void obsolescence(BipartiteGraph graph, double populationSize) {
	int counter = (int)(graph.getNumPlatforms() * populationSize);
	Map<Integer, Platform> platformByAgeSorted = new TreeMap<Integer, Platform>();
	for (Platform p : graph.platforms) {
		// sort so the oldest platforms are first
		platformByAgeSorted.put(p.getBirthCycle(), p);
	}
	for (Map.Entry<Integer, Platform> entry : platformByAgeSorted.entrySet()) {
		if (--counter <= 0) {
			break;
		}
		graph.removeEntity(graph.platforms, entry.getValue());
		Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
		    "Platform <" + entry.getValue() + "> has been killed by Obsolescence");
	}
}


public static void obsolescenceExact(BipartiteGraph graph, int amount) {
	Map<Integer, Platform> platformByAgeSorted = new TreeMap<Integer, Platform>();
	for (Platform p : graph.platforms) {
		// sort so the oldest platforms are first
		platformByAgeSorted.put(p.getBirthCycle(), p);
	}
	for (Map.Entry<Integer, Platform> entry : platformByAgeSorted.entrySet()) {
		if (--amount <= 0) {
			break;
		}
		graph.removeEntity(graph.platforms, entry.getValue());
		Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
		    "Platform <" + entry.getValue() + "> has been killed by ObsolescenceExact");
	}
}


public static void unattended(BipartiteGraph graph, double populationSize) {
	Service backdoor = graph.services.get(graph.random.nextInt(graph.getNumServices()));
	int counter = (int)(graph.getNumPlatforms() * populationSize);
	for (int i = graph.platforms.size() - 1; i >= 0; i--) {
		if (graph.platforms.get(i).getServices().contains(backdoor)) {
			Platform killed = graph.platforms.get(i);
			graph.removeEntity(graph.platforms, killed);
			Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
			    "Platform <" + killed + "> has been killed by Unattended Service <"
			        + backdoor + ">");
			if (--counter <= 0) {
				break;
			}
		}
	}
}


public static void unattendedExact(BipartiteGraph graph, int amount) {
	Service backdoor = graph.services.get(graph.random.nextInt(graph.getNumServices()));
	for (int i = graph.platforms.size() - 1; i >= 0; i--) {
		if (graph.platforms.get(i).getServices().contains(backdoor)) {
			Platform killed = graph.platforms.get(i);
			graph.removeEntity(graph.platforms, killed);
			Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
			    "Platform <" + killed + "> has been killed by Unattended Service <" + backdoor + ">");
			if (--amount <= 0) {
				break;
			}
		}
	}
}


public static void concentration(BipartiteGraph graph) {
	if (graph.getNumPlatforms() == 0) {
		Logger.getLogger(KillFates.class.getName()).log(Level.INFO, "No more platforms");
		return;
	}
	Platform condemned = graph.platforms.get(graph.random.nextInt(graph.getNumPlatforms()));
	graph.removeEntity(graph.platforms, condemned);
	Platform augmented = graph.platforms.get(graph.random.nextInt(graph.getNumPlatforms()));
	for (Service s : condemned.getServices()) {
		BipartiteGraph.addUnique(augmented.getServices(), s);
	}
	Logger.getLogger(KillFates.class.getName()).log(
	    Level.INFO,
	    "Platform <" + condemned + "> has been killed by Concentration and its Services <"
	        + condemned.getServices() + "> added to Platform <" + augmented + ">");
}


public static void concentration2(BipartiteGraph graph) {
	Platform condemned = Metrics.getSmallestPlatform(graph);
	graph.removeEntity(graph.platforms, condemned);
	Platform augmented = Metrics.getBiggestPlatform(graph);
	for (Service s : condemned.getServices()) {
		BipartiteGraph.addUnique(augmented.getServices(), s);
	}
	Logger.getLogger(KillFates.class.getName()).log(
	    Level.INFO,
	    "Platform <" + condemned + "> has been killed by Concentration2 and its Services <"
	        + condemned.getServices() + "> added to Platform <" + augmented + ">");
}


public static void concentration3(BipartiteGraph graph) {
	Platform condemned = Metrics.getSmallestPlatform(graph);
	graph.removeEntity(graph.platforms, condemned);
	Platform augmented = Metrics.getSmallestPlatform(graph);
	for (Service s : condemned.getServices()) {
		BipartiteGraph.addUnique(augmented.getServices(), s);
	}
	Logger.getLogger(KillFates.class.getName()).log(
	    Level.INFO,
	    "Platform <" + condemned + "> has been killed by Concentration3 and its Services <"
	        + condemned.getServices() + "> added to Platform <" + augmented + ">");
}


public static void gasFactory(BipartiteGraph graph, int maxServices, double populationSize) {
	int counter = (int)(graph.getNumPlatforms() * populationSize);
	for (int i = graph.platforms.size() - 1; i >= 0; i--) {
		if (graph.platforms.get(i).getSize() >= maxServices) {
			Platform killed = graph.platforms.get(i);
			graph.removeEntity(graph.platforms, killed);
			Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
			    "Platform <" + killed + "> has been killed by GasFactory");
			if (--counter <= 0) {
				break;
			}
		}
	}
}


public static void gasFactoryExact(BipartiteGraph graph, int maxServices, int amount) {
	for (int i = graph.getNumPlatforms() - 1; i >= 0; i--) {
		if (graph.platforms.get(i).getSize() >= maxServices) {
			Platform killed = graph.platforms.get(i);
			graph.removeEntity(graph.platforms, killed);
			Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
			    "Platform <" + killed + "> has been killed by GasFactory");
			if (--amount <= 0) {
				break;
			}
		}
	}
}
}
