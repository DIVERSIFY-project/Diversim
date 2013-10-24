package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;


public class CreationFates {

public CreationFates() {}


public static void cloningRandom(BipartiteGraph graph, double populationSize) {
	for (int i = 0; i < graph.getNumPlatforms() * populationSize; i++) {
		Platform source = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
		Platform clone = graph.createPlatform(source.getKind());
		for (Service s : source.getServices()) {
			BipartiteGraph.addUnique(clone.getServices(), s);
		}
		clone.setStrategy(source.getStrategy());
		Logger.getLogger(CreationFates.class.getName()).log(Level.INFO,
		    "Platform <" + source + "> has been cloned");
	}
}


public static void cloningMutateRandom(BipartiteGraph graph, double populationSize,
    double servicesSizeVariation) {
	for (int i = 0; i < graph.getNumPlatforms() * populationSize; i++) {
		Platform source = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
		Platform clone = graph.createPlatform(source.getKind());
		int cloneSize = Math.min(graph.random()
		    .nextInt((int)(source.getSize() * servicesSizeVariation)), graph.getNumServices());
		List<Service> cloneServices = new ArrayList<Service>();
		for (int j = 0; j < cloneSize; j++) {
			cloneServices.add(graph.services.get(graph.random().nextInt(graph.getNumServices())));
		}
		for (Service s : cloneServices) {
			BipartiteGraph.addUnique(clone.getServices(), s);
		}
		clone.setStrategy(source.getStrategy());
		Logger.getLogger(CreationFates.class.getName()).log(Level.INFO,
		    "Platform <" + source + "> has been cloned");
	}
}


public static void split(BipartiteGraph graph, double populationSize) {
	for (int i = 0; i < graph.getNumPlatforms() * populationSize; i++) {
		Platform source = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
		Platform clone1 = graph.createPlatform(source.getKind());
		Platform clone2 = graph.createPlatform(source.getKind());
		List<Service> clone1Services = source.getServices().subList(0, source.getServices().size() / 2);
		for (Service s : clone1Services) {
			BipartiteGraph.addUnique(clone1.getServices(), s);
		}
		clone1.setStrategy(source.getStrategy());
		List<Service> clone2Services = source.getServices().subList(source.getServices().size() / 2,
		    source.getServices().size());
		for (Service s : clone2Services) {
			BipartiteGraph.addUnique(clone2.getServices(), s);
		}
		clone2.setStrategy(source.getStrategy());
		graph.removeEntity(graph.platforms, source);
		Logger.getLogger(CreationFates.class.getName()).log(
		    Level.INFO,
		    "Platform <" + source + "> has been split into platforms <" + clone1 + "> & <" + clone2
		        + ">");
	}
}


public static void splitExact(BipartiteGraph graph, int amount) {
	for (int i = 0; i < amount; i++) {
		Platform source = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
		Platform clone1 = graph.createPlatform(source.getKind());
		Platform clone2 = graph.createPlatform(source.getKind());
		List<Service> clone1Services = source.getServices().subList(0, source.getServices().size() / 2);
		for (Service s : clone1Services) {
			BipartiteGraph.addUnique(clone1.getServices(), s);
		}
		clone1.setStrategy(source.getStrategy());
		List<Service> clone2Services = source.getServices().subList(source.getServices().size() / 2,
		    source.getServices().size());
		for (Service s : clone2Services) {
			BipartiteGraph.addUnique(clone2.getServices(), s);
		}
		clone2.setStrategy(source.getStrategy());
		graph.removeEntity(graph.platforms, source);
		Logger.getLogger(CreationFates.class.getName()).log(
		    Level.INFO,
		    "Platform <" + source + "> has been split into platforms <" + clone1 + "> & <" + clone2
		        + ">");
	}
}
}
