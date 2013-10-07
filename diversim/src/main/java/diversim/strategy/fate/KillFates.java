package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import com.sun.istack.internal.logging.Logger;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.strategy.util.Metrics;


public class KillFates {

public static void backdoor(BipartiteGraph graph, Service backdoor, double amount) {
	int counter = (int)(graph.platforms.size() * amount);
	@SuppressWarnings("unchecked")
	List<Platform> platforms = (List<Platform>)graph.platforms.clone();
	for (Platform p : graph.platforms) {
		if (p.getServices().contains(backdoor)) {
			platforms.remove(p);
			Logger.getLogger(KillFates.class).log(Level.INFO,
			    "Platform <" + p + "> has been killed by Backdoor Service <" + backdoor + ">");
			if (--counter <= 0) {
				break;
			}
		}
	}
	graph.platforms = (ArrayList<Platform>)platforms;
}


public static void obsolescence(BipartiteGraph graph, double amount) {
	int counter = (int)(graph.platforms.size() * amount);
	@SuppressWarnings("unchecked")
	List<Platform> platforms = (List<Platform>)graph.platforms.clone();
	Map<Integer, Platform> platformByAgeSorted = new TreeMap<Integer, Platform>();
	for (Platform p : graph.platforms) {
		// using negative age so the oldest platforms are first
		platformByAgeSorted.put(-p.getAge(), p);
	}
	for (Map.Entry<Integer, Platform> entry : platformByAgeSorted.entrySet()) {
		if (--counter <= 0) {
			break;
		}
		platforms.remove(entry.getValue());
		Logger.getLogger(KillFates.class).log(Level.INFO,
		    "Platform <" + entry.getValue() + "> has been killed by Obsolescence");
	}
	graph.platforms = (ArrayList<Platform>)platforms;
}


public static void unattended(BipartiteGraph graph, double amount) {
	Service backdoor = graph.services.get((int)(Math.random() * graph.services.size()));
	int counter = (int)(graph.platforms.size() * amount);
	@SuppressWarnings("unchecked")
	List<Platform> platforms = (List<Platform>)graph.platforms.clone();
	for (Platform p : graph.platforms) {
		if (p.getServices().contains(backdoor)) {
			platforms.remove(p);
			Logger.getLogger(KillFates.class).log(Level.INFO,
			    "Platform <" + p + "> has been killed by Backdoor Service <" + backdoor + ">");
			if (--counter <= 0) {
				break;
			}
		}
	}
	graph.platforms = (ArrayList<Platform>)platforms;
}


public static void concentration(BipartiteGraph graph) {
	Platform condemned = graph.platforms.get((int)(Math.random() * graph.platforms.size()));
	graph.platforms.remove(condemned);
	Platform augmented = graph.platforms.get((int)(Math.random() * graph.platforms.size()));
	for (Service s : condemned.getServices()) {
		augmented.getServices().add(s);
	}
	Logger.getLogger(KillFates.class).log(
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
	Logger.getLogger(KillFates.class).log(
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
	Logger.getLogger(KillFates.class).log(
	    Level.INFO,
	    "Platform <" + condemned + "> has been killed by Concentration3 and its Services <"
	        + condemned.getServices() + "> added to Platform <" + augmented + ">");
}


public static void gasFactory(BipartiteGraph graph, int maxServices, double amount) {
	int counter = (int)(graph.platforms.size() * amount);
	@SuppressWarnings("unchecked")
	List<Platform> platforms = (List<Platform>)graph.platforms.clone();
	for (Platform p : graph.platforms) {
		if (p.getServices().size() >= maxServices) {
			platforms.remove(p);
			Logger.getLogger(KillFates.class).log(Level.INFO,
			    "Platform <" + p + "> has been killed by GasFactory");
			if (--counter <= 0) {
				break;
			}
		}
	}
	graph.platforms = (ArrayList<Platform>)platforms;
}
}
