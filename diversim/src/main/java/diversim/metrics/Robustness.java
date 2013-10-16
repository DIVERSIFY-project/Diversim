package diversim.metrics;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sim.util.Bag;
import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Platform;
import diversim.model.Service;
import ec.util.MersenneTwisterFast;


public class Robustness {

private BipartiteGraph graph = null;


public Robustness(BipartiteGraph graph) {
	this.graph = graph;
}


public BipartiteGraph getGraph() {
	return graph;
}


public void setGraph(BipartiteGraph graph) {
	this.graph = graph;
}


public static double calculateRobustness(BipartiteGraph graph) {
	List<App> apps = new ArrayList<App>(graph.apps);
	List<Platform> platforms = new ArrayList<Platform>(graph.platforms);
	double robustness = 0;
	double maxRobustness = graph.getNumApps() * graph.getNumPlatforms();
	for (int i = graph.getNumPlatforms() - 1; i >= 0; i--) {
		for (int j = 0; j < graph.getNumApps(); j++) {
			linkMacroFairShare(graph, graph.apps.get(j));
		}
		int aliveAppsCounter = 0;
		for (App app : graph.apps) {
			if (app.getDegree() == app.getServices().size()) {
				aliveAppsCounter++;
			}
		}
		robustness += aliveAppsCounter;
		System.out.println("---" + robustness);
		graph.removeEntity(graph.platforms, graph.platforms.get(i));
	}
	System.out.println("+++" + maxRobustness);
	System.out.println("***" + robustness / maxRobustness);
	return robustness / maxRobustness;
}


// TODO
public static List<App> linking(List<App> apps, List<Platform> platforms,
    MersenneTwisterFast random, int maxPlatformLoad) {
	List<App> aliveApps = new ArrayList<App>();
	shuffle(platforms, random);
	for (App app : apps) {
		List<Service> requiredServices = app.getServices();
		for (Platform platform : platforms) {
			int formerSize = requiredServices.size();
			if (platform.getDegree() < maxPlatformLoad) {
				for (Service offeredService : platform.getServices()) {
					requiredServices.remove(offeredService);
				}
			}
			if (requiredServices.size() < formerSize) {

			}
		}
	}
	return aliveApps;
}


public static void shuffle(List<? extends Entity> list, MersenneTwisterFast random) {
	List<Entity> shuffledList = new ArrayList<Entity>();
	while (shuffledList.size() < list.size()) {
		Entity toAdd = list.get(random.nextInt(list.size()));
		if (!shuffledList.contains(toAdd)) {
			shuffledList.add(toAdd);
		}
	}
	list = shuffledList;
}


public static void linkMacroFairShare(BipartiteGraph graph, App e) {
	if (graph.getNumPlatforms() > 0) {
		Bag platforms = new Bag(graph.platforms);
		for (Service s : e.getServices()) {
			platforms.sort(new Comparator<Entity>() {
				@Override
				public int compare(Entity e, Entity e2) {
					return e.getSize() - e2.getSize();
				}
			});
			platforms.reverse();
			for (Object p : platforms) {
				if (((Platform)p).getDegree() <= graph.getPlatformMaxLoad()
				    && ((Platform)p).getServices().contains(s)) {
					graph.addEdge(e, ((Platform)p), e.getSize());
					break;
				}
			}
		}
	}

}

}
