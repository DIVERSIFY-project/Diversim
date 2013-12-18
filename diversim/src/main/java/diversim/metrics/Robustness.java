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
import diversim.model.Entity;
import diversim.strategy.AbstractStrategy;
import diversim.strategy.fate.KillFates;
import diversim.strategy.fate.LinkStrategyFates;
import diversim.util.Log;


public class Robustness {

@SuppressWarnings("unchecked")
public static RobustnessResults calculateRobustness(BipartiteGraph graph, Method linking,
    Method killing) {
	RobustnessResults robustnessResult = new RobustnessResults();
	// shallow cloning
	BipartiteGraph clone = graph.extinctionClone();
	double robustness = 0;
	double maxRobustness = clone.getNumApps() * clone.getNumPlatforms();
	for (int i = clone.getNumPlatforms() - 1; i >= 0; i--) {
		Log.trace("In calculateRobustness, using linking method<" + linking.getName() + ">");
		try {
			linking.invoke(null, clone);
		}
		catch (Exception e) {
			Log.warn("In calculateRobustness, could not load linking method <" + linking.getName() + ">");
			e.printStackTrace();
			return null;
		}
		int aliveAppsCounter = 0;
		for (App app : clone.apps) {
			if (app.isAlive()) {
				aliveAppsCounter++;
			}
		}
		robustnessResult.getAliveAppsHistory().add(aliveAppsCounter);
		robustness += aliveAppsCounter;
		try {
			killing.invoke(null, clone, 1);
		}
		catch (Exception e) {
			Log.warn("In calculateRobustness, could not load killing method <" + killing.getName() + ">");
			e.printStackTrace();
			return null;
		}
	}
	robustnessResult.setRobustness(robustness / maxRobustness);
	return robustnessResult;
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