package diversim.metrics;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.strategy.fate.KillFates;
import diversim.strategy.fate.LinkStrategyFates;


public class Robustness {


/*
 * public static Map<String, Double> calculateAllRobustness(BipartiteGraph graph) { Map<String,
 * Double> results = new HashMap<String, Double>(); }
 */

public static double calculateRobustness(BipartiteGraph graph, Method linking, Method killing) {
	BipartiteGraph clone = graph.clone();
	double robustness = 0;
	double maxRobustness = clone.getNumApps() * clone.getNumPlatforms();
	for (int i = clone.getNumPlatforms() - 1; i >= 0; i--) {
		LinkStrategyFates.linkingA(clone);
		try {
			linking.invoke(null, clone);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
			    "In calculateRobustness, could not load linking method <" + linking.getName() + ">");
			return -1;
		}
		int aliveAppsCounter = 0;
		for (App app : clone.apps) {
			if (!app.dead) {
				aliveAppsCounter++;
			}
		}
		robustness += aliveAppsCounter;
		// System.out.println("---" + aliveAppsCounter);
		// clone.removeEntity(clone.platforms, clone.platforms.get(i));
		try {
			killing.invoke(null, clone, 1);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
			    "In calculateRobustness, could not load killing method <" + killing.getName() + ">");
			e.printStackTrace();
			return -1;
		}
	}
	// System.out.println("+++" + maxRobustness);
	// System.out.println(robustness + "***" + robustness / maxRobustness);
	return robustness / maxRobustness;
}


public static Map<String, Double> calculateAllRobustness(BipartiteGraph graph) {
	Map<String, Double> results = new HashMap<String, Double>();
	Method linkingMethod;
	Method killingMethod;
	for (String linkingName : LinkStrategyFates.getLinkingMethods().keySet()) {
		for (String killingName : KillFates.getKillingMethods().keySet()) {
			try {
				linkingMethod = LinkStrategyFates.class.getDeclaredMethod(linkingName, LinkStrategyFates
				    .getLinkingMethods().get(linkingName));
			}
			catch (NoSuchMethodException | SecurityException e) {
				Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
				    "In calculateAllRobustness, could not load linking method <" + linkingName + ">");
				linkingMethod = null;
			}
			try {
				killingMethod = KillFates.class.getDeclaredMethod(killingName, KillFates
				    .getKillingMethods().get(killingName));
			}
			catch (NoSuchMethodException | SecurityException e) {
				Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
				    "In calculateAllRobustness, could not load killing method <" + killingName + ">");
				e.printStackTrace();
				killingMethod = null;
			}
			if (linkingMethod != null && killingMethod != null) {
				results.put(linkingName + "-" + killingName,
				    calculateRobustness(graph, linkingMethod, killingMethod));
			}
		}
	}
	return results;
}


public static String displayAllRobustness(BipartiteGraph graph) {
	Logger.getLogger(KillFates.class.getName()).setLevel(Level.WARNING);
	String result = "";
	final Map<String, Double> robustness = calculateAllRobustness(graph);
	ArrayList<String> names = new ArrayList<String>(robustness.keySet());
	// Collections.sort(names);
	Collections.sort(names, new Comparator() {

		@Override
		public int compare(Object o1, Object o2) {
			return robustness.get((String)o2) - robustness.get((String)o1) > 0 ? 1 : -1;
		}

	});
	for (String name: names) {
		result += "  " + name + ": " + robustness.get(name) + System.getProperty("line.separator");
	}
	return result;
}

}
