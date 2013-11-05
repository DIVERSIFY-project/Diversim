package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sim.util.Bag;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;


public class CreationFates {

public CreationFates() {}


public static void cloningMutateRandom(BipartiteGraph graph, double populationCreationRatio,
    double servicesSizeVariation) {
	if (graph.getNumPlatforms() * (1 + populationCreationRatio) <= graph.getMaxPlatforms()) {
		for (int i = 0; i < graph.getNumPlatforms() * populationCreationRatio; i++) {
			Platform source = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
			Platform clone = graph.createPlatform(source.getKind());
			clone.getServices().clear();
			int cloneSize = Math.min(
			    graph.random().nextInt((int)(source.getSize() * servicesSizeVariation)),
			    graph.getNumServices());
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
	} else {
		Logger.getLogger(CreationFates.class.getName()).log(Level.FINEST,
		    "CloningMutate: max number of Platforms reached");
	}
}


public static void split(BipartiteGraph graph, double servicesTransmitted, double populationCreationRatio) {
	if (graph.getNumPlatforms() * (1 + populationCreationRatio) < graph.getMaxPlatforms()) {
		for (int i = 0; i < graph.getNumPlatforms() * populationCreationRatio; i++) {
			Platform source = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
			Platform clone1 = graph.createPlatform(source.getKind());
			clone1.getServices().clear();
			Platform clone2 = graph.createPlatform(source.getKind());
			clone2.getServices().clear();
			Bag sourceServices = new Bag(source.getServices());
			sourceServices.shuffle(graph.random());
			for (int j = 0; j < sourceServices.size() * servicesTransmitted; j++) {
				BipartiteGraph.addUnique(clone1.getServices(), (Service)sourceServices.get(j));
			}
			sourceServices.shuffle(graph.random());
			for (int j = 0; j < sourceServices.size() * servicesTransmitted; j++) {
				BipartiteGraph.addUnique(clone2.getServices(), (Service)sourceServices.get(j));
			}
			clone1.setStrategy(source.getStrategy());
			clone2.setStrategy(source.getStrategy());
			graph.removeEntity(graph.platforms, source);
			Logger.getLogger(CreationFates.class.getName()).log(
			    Level.INFO,
			    "Platform <" + source + "> has been split into platforms <" + clone1 + "> & <" + clone2
			        + ">");
		}
	} else {
		Logger.getLogger(CreationFates.class.getName()).log(Level.FINEST,
		    "Split: max number of Platforms reached");
	}
}


public static void splitExact(BipartiteGraph graph, double servicesTransmitted, int amount) {
	if (graph.getNumPlatforms() < graph.getMaxPlatforms()) {
		for (int i = 0; i < amount; i++) {
			Platform source = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
			Platform clone1 = graph.createPlatform(source.getKind());
			clone1.getServices().clear();
			Platform clone2 = graph.createPlatform(source.getKind());
			clone2.getServices().clear();
			Bag sourceServices = new Bag(source.getServices());
			sourceServices.shuffle(graph.random());
			for (int j = 0; j < sourceServices.size() * servicesTransmitted; j++) {
				BipartiteGraph.addUnique(clone1.getServices(), (Service)sourceServices.get(j));
			}
			sourceServices.shuffle(graph.random());
			for (int j = 0; j < sourceServices.size() * servicesTransmitted; j++) {
				BipartiteGraph.addUnique(clone2.getServices(), (Service)sourceServices.get(j));
			}
			clone2.setStrategy(source.getStrategy());
			graph.removeEntity(graph.platforms, source);
			Logger.getLogger(CreationFates.class.getName()).log(
			    Level.INFO,
			    "Platform <" + source + "> has been split into platforms <" + clone1 + "> & <" + clone2
			        + ">");
		}
	} else {
		Logger.getLogger(CreationFates.class.getName()).log(Level.FINEST,
		    "SplitExact: max number of Platforms reached");
	}
}
}
