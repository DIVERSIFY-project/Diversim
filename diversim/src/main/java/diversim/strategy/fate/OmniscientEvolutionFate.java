package diversim.strategy.fate;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.util.Log;

import sim.util.Bag;
import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Fate;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.strategy.AbstractStrategy;


public class OmniscientEvolutionFate extends AbstractStrategy<Fate> {

public OmniscientEvolutionFate() {
	super("Omniscient");
}


@SuppressWarnings("unchecked")
@Override
public void evolve(BipartiteGraph graph, Fate agent) {
	// System.out.println("B:" + graph.apps);
	// System.out.println("B:" + graph.platforms);
	String linkingName = "linkingC";
	// cleaning graph
	List<Platform> platformsToDelete = new ArrayList<Platform>(graph.platforms);
	for (Platform platform : platformsToDelete) {
		graph.removeEntity(graph.platforms, platform);
	}
	// creating platforms
	Bag appsBySize = new Bag(graph.apps);
	appsBySize.sort(new SizeComparator());
	Map<List<Service>, Set<App>> appsSetByCommonServices = new LinkedHashMap<List<Service>, Set<App>>();
	Set<App> includedApps;
	App biggestApp;
	while (!appsBySize.isEmpty()) {
		biggestApp = (App)appsBySize.get(0);
		includedApps = new LinkedHashSet<App>();
		for (Object app : appsBySize) {
			// System.out.println(biggestApp + "/" + (App)app + ":"
			// + biggestApp.countCommonServices((App)app, null) + "/"
			// + ((App)app).getSize());
			if (biggestApp.countCommonServices((App)app, null) == ((App)app).getSize()) {
				if (includedApps.size() < graph.getPlatformMaxLoad()) {
					includedApps.add((App)app);
				} else {
					System.err.println("Omniscient: more apps included in " + biggestApp + " than MaxLoad");
					break;
				}
			}
		}
		appsSetByCommonServices.put(biggestApp.getServices(), includedApps);
		appsBySize.removeAll(includedApps);
		appsBySize.sort(new SizeComparator());
	}
	// System.out.println("I:" + appsSetByCommonServices);
	Bag serviceListBySize = new Bag(appsSetByCommonServices.keySet());
	serviceListBySize.sort(new Comparator<List<Service>>() {

		@Override
		public int compare(List<Service> o1, List<Service> o2) {
			return o2.size() - o1.size();
		}

	});
	// System.out.println(Arrays.toString(serviceListBySize.toArray()));
	Platform newPlatform;
	for (Object serviceList : serviceListBySize) {
		if (graph.getNumPlatforms() < graph.getMaxPlatforms()) {
			newPlatform = graph.createPlatform("platform.1");
			newPlatform.getServices().clear();
			newPlatform.getServices().addAll((List<Service>)serviceList);
		}
	}
	Bag platformsBySize = new Bag(graph.platforms);
	platformsBySize.sort(new SizeComparator());
	int index = 0;
	while (graph.getNumPlatforms() < graph.getMaxPlatforms()) {
		index += 1;
		index = index % graph.getNumPlatforms();
		newPlatform = graph.createPlatform("platform.1");
		newPlatform.getServices().clear();
		newPlatform.getServices().addAll(((Platform)platformsBySize.get(index)).getServices());
	}

	// link
	try {
		LinkStrategyFates.class.getDeclaredMethod(linkingName,
		    LinkStrategyFates.getLinkingMethods().get(linkingName)).invoke(null, graph);
	}
	catch (IllegalAccessException e) {
		e.printStackTrace();
	}
	catch (IllegalArgumentException e) {
		e.printStackTrace();
	}
	catch (InvocationTargetException e) {
		e.printStackTrace();
	}
	catch (NoSuchMethodException e) {
		e.printStackTrace();
	}
	catch (SecurityException e) {
		e.printStackTrace();
	}
	// System.out.println("E:" + graph.apps);
	// System.out.println("E:" + graph.platforms);
	for (App app : graph.apps) {
		app.step(graph);
	}
	for (Platform platform : graph.platforms) {
		platform.step(graph);
	}
}


public class SizeComparator implements Comparator<Entity> {

@Override
public int compare(Entity o1, Entity o2) {
	return o2.getSize() - o1.getSize();
}

}

}
