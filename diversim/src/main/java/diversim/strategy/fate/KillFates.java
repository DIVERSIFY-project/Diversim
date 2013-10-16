package diversim.strategy.fate;


import java.util.ArrayList;
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


public static void backdoor(BipartiteGraph graph, Service backdoor, double amount) {
	int counter = (int)(graph.getNumPlatforms() * amount);
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


public static void obsolescence(BipartiteGraph graph, double amount) {
	int counter = (int)(graph.getNumPlatforms() * amount);
	@SuppressWarnings("unchecked")
	List<Platform> platforms = (List<Platform>)graph.platforms.clone();
	Map<Integer, Platform> platformByAgeSorted = new TreeMap<Integer, Platform>();
	for (Platform p : graph.platforms) {
		// sort so the oldest platforms are first
		platformByAgeSorted.put(p.getBirthCycle(), p);
	}
	for (Map.Entry<Integer, Platform> entry : platformByAgeSorted.entrySet()) {
		if (--counter <= 0) {
			break;
		}
		platforms.remove(entry.getValue());
		Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
		    "Platform <" + entry.getValue() + "> has been killed by Obsolescence");
	}
	graph.platforms = (ArrayList<Platform>)platforms;
}


public static void unattended(BipartiteGraph graph, double amount) {
	Service backdoor = graph.services.get(graph.random.nextInt(graph.getNumServices()));
	int counter = (int)(graph.getNumPlatforms() * amount);
	@SuppressWarnings("unchecked")
	List<Platform> platforms = (List<Platform>)graph.platforms.clone();
	for (Platform p : graph.platforms) {
		if (p.getServices().contains(backdoor)) {
			platforms.remove(p);
			Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
			    "Platform <" + p + "> has been killed by Unattended Service <" + backdoor + ">");
			if (--counter <= 0) {
				break;
			}
		}
	}
	graph.platforms = (ArrayList<Platform>)platforms;
}


public static void concentration(BipartiteGraph graph) {
	if (graph.getNumPlatforms() == 0) {
		Logger.getLogger(KillFates.class.getName()).log(Level.INFO, "No more platforms");
		return;
	}
	Platform condemned = graph.platforms.get(graph.random.nextInt(graph.getNumPlatforms()));
	graph.platforms.remove(condemned);
	Platform augmented = graph.platforms.get(graph.random.nextInt(graph.getNumPlatforms()));
	for (Service s : condemned.getServices()) {
		augmented.getServices().add(s);
	}
	Logger.getLogger(KillFates.class.getName()).log(
	    Level.INFO,
	    "Platform <" + condemned + "> has been killed by Concentration and its Services <"
	        + condemned.getServices() + "> added to Platform <" + augmented + ">");
}


public static void concentration2(BipartiteGraph graph) {
	Platform condemned = Metrics.getSmallestPlatform(graph);
	graph.platforms.remove(condemned);
	Platform augmented = Metrics.getBiggestPlatform(graph);
	for (Service s : condemned.getServices()) {
		augmented.getServices().add(s);
	}
	Logger.getLogger(KillFates.class.getName()).log(
	    Level.INFO,
	    "Platform <" + condemned + "> has been killed by Concentration2 and its Services <"
	        + condemned.getServices() + "> added to Platform <" + augmented + ">");
}


public static void concentration3(BipartiteGraph graph) {
	Platform condemned = Metrics.getSmallestPlatform(graph);
	graph.platforms.remove(condemned);
	Platform augmented = Metrics.getSmallestPlatform(graph);
	for (Service s : condemned.getServices()) {
		augmented.getServices().add(s);
	}
	Logger.getLogger(KillFates.class.getName()).log(
	    Level.INFO,
	    "Platform <" + condemned + "> has been killed by Concentration3 and its Services <"
	        + condemned.getServices() + "> added to Platform <" + augmented + ">");
}


public static void gasFactory(BipartiteGraph graph, int maxServices, double amount) {
	int counter = (int)(graph.getNumPlatforms() * amount);
	@SuppressWarnings("unchecked")
	List<Platform> platforms = (List<Platform>)graph.platforms.clone();
	for (Platform p : graph.platforms) {
		if (p.getServices().size() >= maxServices) {
			platforms.remove(p);
			Logger.getLogger(KillFates.class.getName()).log(Level.INFO,
			    "Platform <" + p + "> has been killed by GasFactory");
			if (--counter <= 0) {
				break;
			}
		}
	}
	graph.platforms = (ArrayList<Platform>)platforms;
}
}
