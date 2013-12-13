package diversim.metrics;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.strategy.fate.KillFates;
import diversim.strategy.fate.LinkStrategyFates;
import diversim.strategy.extinction.AgingExtinctionWithDegreeStrategy;
import diversim.strategy.application.AllMatchingLinkStrategy;
import diversim.strategy.application.LinkingC;

public class Robustness {
	
	private Method linkingMethod;
	private Method killingMethod;
	//private BipartiteGraph graph;
	
	public Robustness(String linkMethodName, String killMethodName){
		//this.graph = graph;
		try{
			this.linkingMethod = LinkingC.class.getDeclaredMethod(linkMethodName, BipartiteGraph.class);
		}catch(Exception e){
			Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
				    "In calculateAllRobustness, could not load linking method <" + linkMethodName + ">");
				this.linkingMethod = null;
		}
		try{
			this.killingMethod = KillFates.class.getDeclaredMethod(killMethodName, BipartiteGraph.class);
		}catch(Exception e){
			Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
				    "In calculateAllRobustness, could not load killing method <" + killMethodName + ">");
				this.killingMethod = null;
		}
	}
	
	public Method getLinkingMethod(){
		return this.linkingMethod;
	}
	
	public Method getKillingMethod(){
		return this.killingMethod;
	}


public static double calculateRobustness(BipartiteGraph graph, Method linking, Method killing) {
	List<Boolean> deadAppList = new ArrayList<Boolean>();
	for (App app : graph.apps) {
		deadAppList.add(!app.isAlive());
	}
	List<Boolean> deadPlatformList = new ArrayList<Boolean>();
	for (Platform platform : graph.platforms) {
		deadPlatformList.add(!platform.isAlive());
	}
	BipartiteGraph clone = graph.extinctionClone();
	double robustness = 0;
	double maxRobustness = clone.getNumApps() * clone.getNumPlatforms();
	for (int i = clone.getNumPlatforms() - 1; i >= 0; i--) {
		// LinkStrategyFates.linkingA(clone);
		try {
			linking.invoke(null, clone);
		}
		catch (Exception e) {
			Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
			    "In calculateRobustness, could not load linking method <" + linking.getName() + ">");
			return -1;
		}
		int aliveAppsCounter = 0;
		for (App app : clone.apps) {
			if (app.isAlive()) {
				aliveAppsCounter++;
			}
		}
		robustness += aliveAppsCounter;
		// System.out.println("---" + aliveAppsCounter);
		// clone.removeEntity(clone.platforms, clone.platforms.get(i));
		try {
			killing.invoke(null, clone);
		}
		catch (Exception e) {
			Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
			    "In calculateRobustness, could not load killing method <" + killing.getName() + ">");
			e.printStackTrace();
			return -1;
		}
	}
	// System.out.println("+++" + maxRobustness);
	// System.out.println(robustness + "***" + robustness / maxRobustness);
	for (int i = 0; i < deadAppList.size(); i++) {
		graph.apps.get(i).dead = deadAppList.get(i);
	}
	for (int i = 0; i < deadPlatformList.size(); i++) {
		graph.platforms.get(i).dead = deadPlatformList.get(i);
	}
	return robustness / maxRobustness;
}


public static Map<String, Map<String, Double>> calculateAllRobustness(BipartiteGraph graph,
    int trials) {
	Map<String, Map<String, Double>> results = new HashMap<String, Map<String, Double>>();
	Method linkingMethod;
	Method killingMethod;
	DescriptiveStatistics stats = new DescriptiveStatistics();
	Map<String, Double> statResults;
	for (String linkingName : LinkStrategyFates.getLinkingMethods().keySet()) {
		for (String killingName : KillFates.getKillingMethods().keySet()) {
			stats.clear();
			for (int i = 0; i < trials; i++) {
				try {
					linkingMethod = LinkStrategyFates.class.getDeclaredMethod(linkingName, LinkStrategyFates
					    .getLinkingMethods().get(linkingName));
				}
				catch (Exception e) {
					Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
					    "In calculateAllRobustness, could not load linking method <" + linkingName + ">");
					linkingMethod = null;
				}
				try {
					killingMethod = KillFates.class.getDeclaredMethod(killingName, KillFates
					    .getKillingMethods().get(killingName));
				}
				catch (Exception e) {
					Logger.getLogger(Robustness.class.getName()).log(Level.WARNING,
					    "In calculateAllRobustness, could not load killing method <" + killingName + ">");
					e.printStackTrace();
					killingMethod = null;
				}
				double value = 0;
				if (linkingMethod != null && killingMethod != null) {
					// results.put(linkingName + "-" + killingName,
					// calculateRobustness(graph, linkingMethod, killingMethod));
					value = calculateRobustness(graph, linkingMethod, killingMethod);
				}
				stats.addValue(value);
			}
			statResults = new TreeMap<String, Double>();
			statResults.put("Min", stats.getMin());
			statResults.put("P25", stats.getPercentile(25));
			statResults.put("P50", stats.getPercentile(50));
			statResults.put("P75", stats.getPercentile(75));
			statResults.put("Max", stats.getMax());
			statResults.put("Mean", stats.getMean());
			results.put(linkingName + "-" + killingName, statResults);
		}
	}
	return results;
}


public static String displayAllRobustness(BipartiteGraph graph, int trials) {
	Logger.getLogger(KillFates.class.getName()).setLevel(Level.WARNING);
	String result = "";
	final Map<String, Map<String, Double>> robustness = calculateAllRobustness(graph, trials);
	ArrayList<String> names = new ArrayList<String>(robustness.keySet());
	// Collections.sort(names);
	// Collections.sort(names, new Comparator() {
	//
	// @Override
	// public int compare(Object o1, Object o2) {
	// return robustness.get((String)o2) - robustness.get((String)o1) > 0 ? 1 : -1;
	// }
	//
	// });
	for (String name: names) {
		result += "  " + name + ": " + robustness.get(name)
		    + System.getProperty("line.separator");
	}
	return result;
}

}
