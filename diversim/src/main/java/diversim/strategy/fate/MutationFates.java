package diversim.strategy.fate;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.sun.istack.internal.logging.Logger;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;


public class MutationFates {

public static void bugCorrected(BipartiteGraph graph) {
	Service obsolete = graph.services.get((int)(Math.random() * graph.services.size()));
	Service replacement = graph.services.get((int)(Math.random() * graph.services.size()));
	for (Platform p : graph.platforms) {
		if (p.getServices().contains(obsolete)) {
			p.getServices().remove(obsolete);
			p.getServices().add(replacement);
			Logger.getLogger(MutationFates.class).log(
			    Level.INFO,
			    "Platform <" + p + "> has mutated: Service <" + obsolete + "> replaced with Service <"
			        + replacement + ">");
		}
	}
}


public static void upgrade(BipartiteGraph graph, int upgradedServicesNumber) {
	Platform upgradedPlatform = graph.platforms.get((int)(Math.random() * graph.platforms.size()));
	List<Service> removedServices = new ArrayList<Service>();
	for (int i = 0; i < Math.min(upgradedServicesNumber, upgradedPlatform.getServices().size()); i++) {
		removedServices.add(upgradedPlatform.getServices().get(
		    (int)(Math.random() * upgradedPlatform.getServices().size())));
	}
	List<Service> upgradedServices = new ArrayList<Service>();
	for (int i = 0; i < Math.min(upgradedServicesNumber, graph.services.size()); i++) {
		upgradedServices.add(graph.services.get((int)(Math.random() * graph.services.size())));
	}
	upgradedPlatform.getServices().removeAll(removedServices);
	upgradedPlatform.getServices().addAll(upgradedServices);
	Logger.getLogger(MutationFates.class).log(
	    Level.INFO,
	    "Platform <" + upgradedPlatform + "> has upgraded Services <" + removedServices + "> into <"
	        + upgradedServices + ">");
}
}
