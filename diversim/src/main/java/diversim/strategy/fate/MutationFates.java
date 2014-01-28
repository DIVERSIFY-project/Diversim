package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sim.util.Bag;
import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.util.Log;
import ec.util.MersenneTwisterFast;


public class MutationFates {

public MutationFates() {}


@SuppressWarnings("rawtypes")
public static <T extends Comparable> List<T> getRandomEntities(List<T> source, int amount,
    MersenneTwisterFast random) {
	List<T> results = new ArrayList<T>();
	if (amount >= source.size()) {
		Log.warn("Requested amount too big in getRandomEntities, returning entire source");
		return source;
	}
	for (int i = 0; i < amount; i++) {
		BipartiteGraph.addUnique(results, source.get(random.nextInt(source.size())));
	}
	return results;
}


public static void increaseOffer(BipartiteGraph graph, double addedServicesRatio,
    double mutatedPlatformsRatio) {
	int addedServicesNumber = (int)Math.ceil(graph.getNumServices() * addedServicesRatio);
	int mutatedPopulationNumber = (int)Math.ceil(graph.getNumPlatforms() * mutatedPlatformsRatio);
	List<Platform> mutatedPlatforms = getRandomEntities(graph.platforms, mutatedPopulationNumber,
	    graph.random());
	int targetSize;
	for (Platform platform : mutatedPlatforms) {
		targetSize = Math.min(platform.getSize() + addedServicesNumber, graph.services.size());
		while (platform.getSize() < targetSize) {
			Service candidateService = getRandomEntities(graph.services, 1, graph.random()).get(0);
			BipartiteGraph.addUnique(platform.getServices(), candidateService);
		}
	}
}


public static void bugCorrected(BipartiteGraph graph) {
	Service obsolete = graph.services.get(graph.random().nextInt(graph.getNumServices()));
	Service replacement = graph.services.get(graph.random().nextInt(graph.getNumServices()));
	for (Platform platform : graph.platforms) {
		int i = Collections.binarySearch(platform.getServices(), obsolete);
		if (i >= 0) {
			platform.getServices().remove(i);
			BipartiteGraph.addUnique(platform.getServices(), replacement);
			Log.debug("Platform <" + platform + "> has mutated: Service <" + obsolete
			    + "> replaced with Service <" + replacement + "> by BugCorrected");
		}
	}
}


public static void upgrade(BipartiteGraph graph, int upgradedServicesNumber) {
	Platform upgradedPlatform = graph.platforms.get(graph.random().nextInt(graph.getNumPlatforms()));
	List<Service> removedServices = new ArrayList<Service>();
	for (int i = 0; i < Math.min(upgradedServicesNumber, upgradedPlatform.getSize()); i++) {
		removedServices.add(upgradedPlatform.getServices().get(
		    graph.random().nextInt(graph.getNumServices())));
	}
	List<Service> upgradedServices = new ArrayList<Service>();
	for (int i = 0; i < Math.min(upgradedServicesNumber, graph.getNumServices()); i++) {
		upgradedServices.add(graph.services.get(graph.random().nextInt(graph.getNumServices())));
	}
	upgradedPlatform.getServices().removeAll(removedServices);
	// upgradedPlatform.getServices().addAll(upgradedServices);
	for (Service service : upgradedServices) {
		BipartiteGraph.addUnique(upgradedPlatform.getServices(), service);
	}
	Log.debug("Platform <" + upgradedPlatform + "> has upgraded Services <" + removedServices
	    + "> into <"
	        + upgradedServices + "> by Upgrade");
}


public static void random(BipartiteGraph graph, double mutatedPopulationRatio, double mutationRatio) {
	int counterPopulation = Math.max((int)(graph.getNumPlatforms() * mutatedPopulationRatio), 1);
	Bag platforms = new Bag(graph.platforms);
	platforms.shuffle(graph.random());
	for (Object platform : platforms) {
		int counterMutation = Math.max((int)(((Platform)platform).getSize() * mutationRatio),
		    1);
		int removedServices = counterMutation;
		Bag services = new Bag(((Platform)platform).getServices());
		services.shuffle(graph.random());
		for (Object service : services) {
			int i = Collections.binarySearch(((Platform)platform).getServices(), (Service)service);
			if (i >= 0) {
				((Platform)platform).getServices().remove(i);
				int initialServiceSize = ((Platform)platform).getSize();
				while (initialServiceSize == ((Platform)platform).getSize()) {
					Service replacement = graph.services.get(graph.random().nextInt(graph.getNumServices()));
					BipartiteGraph.addUnique(((Platform)platform).getServices(), replacement);
				}
				counterMutation--;
			}
			if (counterMutation <= 0) {
				break;
			}
		}
		Log.debug("Platform <" + platform + "> has mutated " + removedServices + " Services by Random");
		if (--counterPopulation <= 0) {
			break;
		}
	}

}
}
