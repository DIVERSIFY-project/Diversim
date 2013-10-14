package diversim.strategy.fate;


import java.util.logging.Level;
import java.util.logging.Logger;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;


public class CreationFates {

public CreationFates() {}


public static void cloningRandom(BipartiteGraph graph, double amount) {
	for (int i = 0; i < graph.getNumPlatforms() * amount; i++) {
		Platform source = graph.platforms.get(graph.random.nextInt(graph.getNumPlatforms()));
		Platform clone = graph.createPlatform("platform.1");
		clone.setServices(source.getServices());
		clone.setStrategy(source.getStrategy());
		Logger.getLogger(CreationFates.class.getName()).log(Level.INFO,
		    "Platform <" + source + "> has been cloned");
	}
}
}
