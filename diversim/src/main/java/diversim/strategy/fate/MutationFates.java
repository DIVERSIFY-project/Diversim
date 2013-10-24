package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;


public class MutationFates {

public MutationFates() {}


public static void bugCorrected(BipartiteGraph graph) {
	Service obsolete = graph.services.get(graph.random.nextInt(graph.getNumServices()));
	Service replacement = graph.services.get(graph.random.nextInt(graph.getNumServices()));
	for (Platform platform : graph.platforms) {
		if (platform.getServices().contains(obsolete)) {
			platform.getServices().remove(obsolete);
			BipartiteGraph.addUnique(platform.getServices(), replacement);
			Logger.getLogger(MutationFates.class.getName()).log(
			    Level.INFO,
			    "Platform <" + platform + "> has mutated: Service <" + obsolete + "> replaced with Service <"
			        + replacement + ">");
		}
	}
}


public static void upgrade(BipartiteGraph graph, int upgradedServicesNumber) {
	Platform upgradedPlatform = graph.platforms.get(graph.random.nextInt(graph.getNumPlatforms()));
	List<Service> removedServices = new ArrayList<Service>();
	for (int i = 0; i < Math.min(upgradedServicesNumber, upgradedPlatform.getServices().size()); i++) {
		removedServices.add(upgradedPlatform.getServices().get(
		    graph.random.nextInt(graph.getNumServices())));
	}
	List<Service> upgradedServices = new ArrayList<Service>();
	for (int i = 0; i < Math.min(upgradedServicesNumber, graph.getNumServices()); i++) {
		upgradedServices.add(graph.services.get(graph.random.nextInt(graph.getNumServices())));
	}
	upgradedPlatform.getServices().removeAll(removedServices);
	upgradedPlatform.getServices().addAll(upgradedServices);
	Logger.getLogger(MutationFates.class.getName()).log(
	    Level.INFO,
	    "Platform <" + upgradedPlatform + "> has upgraded Services <" + removedServices + "> into <"
	        + upgradedServices + ">");
}
}
