package diversim.metrics;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.strategy.fate.KillFates;
import diversim.strategy.fate.LinkStrategyFates;
import diversim.util.Log;


public class Robustness {
	
private Method linkingMethod;

private Method killingMethod;
	

public Robustness(Method robustnessLinkingMethod, Method robustnessKillingMethod) {
	linkingMethod = robustnessLinkingMethod;
	killingMethod = robustnessKillingMethod;
}
	

public Method getLinkingMethod() {
	return this.linkingMethod;
}


public Method getKillingMethod() {
	return this.killingMethod;
}

@SuppressWarnings("unchecked")
public static RobustnessResults calculateRobustness(BipartiteGraph graph, Method linking,
    Method killing) {
	RobustnessResults robustnessResult = new RobustnessResults();
	/*
	 * // shallow cloning // BipartiteGraph clone = graph.extinctionClone();
	 */
	boolean[] relink = {true, false};
	int maxNumPlatforms = graph.getNumPlatforms();
	int maxRobustnessWithInitApps = graph.getInitApps() * maxNumPlatforms;
	int maxRobustnessWithAliveApps = graph.getAliveAppsNumber() * maxNumPlatforms;
	double robustness;
	List<Platform> platformsSave = new ArrayList<Platform>(graph.platforms);
	// System.out.println("*real*" + graph.platforms);
	for (int j = 0; j < /* 2 */1; j++) { // j = 0 -> always relink, j = 1 -> no relink
		// saving platforms
		// System.out.println("+save+" + platformsSave);
		robustness = 0;
		for (int i = maxNumPlatforms; i > 0; i--) {
			Log.trace("In calculateRobustness, using linking method<" + linking.getName() + ">");
			if (i == maxNumPlatforms || relink[j]) {
				try {
					linking.invoke(null, graph);
				}
				catch (Exception e) {
					Log.warn("In calculateRobustness, could not load linking method <" + linking.getName()
					    + ">");
					e.printStackTrace();
					return null;
				}
			}
			int aliveAppsCounter = 0;
			for (App app : graph.apps) {
				if (app.isAlive()) {
					aliveAppsCounter++;
				}
			}
			robustnessResult.getAliveAppsHistory().add(aliveAppsCounter);
			robustness += aliveAppsCounter;
			try {
				killing.invoke(null, graph, 1);
			}
			catch (Exception e) {
				Log.warn("In calculateRobustness, could not load killing method <" + killing.getName()
				    + ">");
				e.printStackTrace();
				return null;
			}
		}
		if (relink[j]) {
			robustnessResult.setRobustnessL(robustness / maxRobustnessWithInitApps, robustness
			    / maxRobustnessWithAliveApps);
		} else {
			robustnessResult.setRobustnessNoL(robustness / maxRobustnessWithInitApps, robustness
			    / maxRobustnessWithAliveApps);
		}
		// restoring platforms
		graph.platforms = new ArrayList<Platform>(platformsSave);
		for (int i = 0; i < graph.platforms.size(); i++) {
			graph.bipartiteNetwork.addNode(graph.platforms.get(i));
		}
		// System.out.println("-real-" + graph.platforms);
		// for (Platform platform : platformsSave) {
		// BipartiteGraph.addUnique(graph.platforms, platform);
		// }
	}
	// robustnessResult.setRobustness(robustness / maxRobustness);
	return robustnessResult;
}


public static double calculateResistance(BipartiteGraph graph, Method linking) {
	double resistance = 0;
	double maxResistance = graph.getNumServices() * graph.getNumApps();
	List<Platform> platformsSave = new ArrayList<Platform>(graph.platforms);
	for (Service service : graph.services) {
		KillFates.backdoor(graph, service, 1);
		try {
			linking.invoke(null, graph);
		}
		catch (Exception e) {
			Log.warn("In calculateResistance, could not load linking method <" + linking.getName() + ">");
			e.printStackTrace();
			return -1;
		}
		int aliveAppsCounter = 0;
		for (App app : graph.apps) {
			if (app.isAlive()) {
				aliveAppsCounter++;
			}
		}
		resistance += aliveAppsCounter;
		graph.platforms = new ArrayList<Platform>(platformsSave);
	}
	return resistance / maxResistance;
}


public static Map<String, Map<String, Double>> calculateStatAllRobustness(BipartiteGraph graph,
    int trials) {
	Map<String, Map<String, Double>> results = new HashMap<String, Map<String, Double>>();
	Method linkingMethod;
	Method killingMethod;
	DescriptiveStatistics statResult = new DescriptiveStatistics();
	List<DescriptiveStatistics> statHistoryList = new ArrayList<DescriptiveStatistics>();
	for (int i = 0; i < graph.getNumPlatforms(); i++) {
		statHistoryList.add(new DescriptiveStatistics());
	}
	Map<String, Double> statResults;
	for (String linkingName : LinkStrategyFates.getLinkingMethods().keySet()) {
		for (String killingName : KillFates.getKillingMethods().keySet()) {
			statResult.clear();
			for (DescriptiveStatistics stat : statHistoryList) {
				stat.clear();
			}
			for (int i = 0; i < trials; i++) {
				try {
					linkingMethod = LinkStrategyFates.class.getDeclaredMethod(linkingName, LinkStrategyFates
					    .getLinkingMethods().get(linkingName));
				}
				catch (Exception e) {
					Log.warn("In calculateAllRobustness, could not load linking method <" + linkingName + ">");
					linkingMethod = null;
				}
				try {
					killingMethod = KillFates.class.getDeclaredMethod(killingName, KillFates
					    .getKillingMethods().get(killingName));
				}
				catch (Exception e) {
					Log.warn("In calculateAllRobustness, could not load killing method <" + killingName + ">");
					e.printStackTrace();
					killingMethod = null;
				}
				if (linkingMethod != null && killingMethod != null) {
					RobustnessResults robustnessResult = calculateRobustness(graph, linkingMethod,
							killingMethod);
					statResult.addValue(robustnessResult.getRobustness());
					for (int j = 0; j < robustnessResult.getAliveAppsHistory().size(); j++) {
						statHistoryList.get(j).addValue(robustnessResult.getAliveAppsHistory().get(j));
					}
				}
			}
			statResults = new TreeMap<String, Double>();
			statResults.put("Min", statResult.getMin());
			statResults.put("P25", statResult.getPercentile(25));
			statResults.put("P50", statResult.getPercentile(50));
			statResults.put("P75", statResult.getPercentile(75));
			statResults.put("Max", statResult.getMax());
			statResults.put("Mean", statResult.getMean());
			for (int i = 0; i < statHistoryList.size(); i++) {
				statResults.put("KillingStep" + nameStep(i, statHistoryList.size()), statHistoryList.get(i)
				    .getMean());
			}
			results.put(linkingName + "-" + killingName, statResults);
		}
	}
	return results;
}


public static String nameStep(int step, int stepMax) {
	String name = "";
	if (step == 0) {
		for (int i = 0; i < (int)Math.log10(stepMax) + 1; i++) {
			name += "0";
		}
	} else {
		for (int i = 0; i < (int)Math.log10(stepMax) - (int)Math.log10(step); i++) {
			name += "0";
		}
		name += step;
	}
	return name;
}


public static Map<String, RobustnessResults> calculateAllRobustness(BipartiteGraph graph) {
	Map<String, RobustnessResults> results = new HashMap<String, RobustnessResults>();
	Method linkingMethod;
	Method killingMethod;
	ExecutorService executor = Executors.newCachedThreadPool();
	RobustnessRun robustnessRun;
	Map<String, Future<RobustnessResults>> robustnessFutureList = new LinkedHashMap<String, Future<RobustnessResults>>();
	for (String linkingName : LinkStrategyFates.getLinkingMethods().keySet()) {
		for (String killingName : KillFates.getKillingMethods().keySet()) {
			try {
				linkingMethod = LinkStrategyFates.class.getDeclaredMethod(linkingName, LinkStrategyFates
				    .getLinkingMethods().get(linkingName));
			}
			catch (Exception e) {
				Log.warn("In calculateAllRobustness, could not load linking method <" + linkingName + ">");
				linkingMethod = null;
			}
			try {
				killingMethod = KillFates.class.getDeclaredMethod(killingName, KillFates
				    .getKillingMethods().get(killingName));
			}
			catch (Exception e) {
				Log.warn("In calculateAllRobustness, could not load killing method <" + killingName + ">");
				e.printStackTrace();
				killingMethod = null;
			}
			if (linkingMethod != null && killingMethod != null) {
				robustnessRun = new RobustnessRun(graph, linkingMethod, killingMethod);
				robustnessFutureList.put(linkingName + "-" + killingName, executor.submit(robustnessRun));
			}
		}
	}
	for (String strategyName : robustnessFutureList.keySet()) {
		try {
			results.put(strategyName, robustnessFutureList.get(strategyName).get());
		}
		catch (InterruptedException e) {
			Log.warn("In calculateAllRobustness, interruption in Robustness thread " + strategyName);
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			Log.warn("In calculateAllRobustness, exception in Robustness thread " + strategyName);
			e.printStackTrace();
		}
	}
	return results;
}


public static String displayAllRobustness(BipartiteGraph graph, int trials) {
	String result = "";
	final Map<String, Map<String, Double>> robustness = calculateStatAllRobustness(graph, trials);
	ArrayList<String> names = new ArrayList<String>(robustness.keySet());
	for (String name: names) {
		result += "  " + name + ": " + robustness.get(name)
		    + System.getProperty("line.separator");
	}
	return result;
}

}
