package diversim.strategy.fate;


import java.util.logging.Level;

import com.sun.istack.internal.logging.Logger;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;


public class CreationFates {

public static void cloningRandom(BipartiteGraph graph, int amount) {
	for (int i = 0; i < Math.min(amount, graph.platforms.size()); i++) {
		graph.platforms.add(new Platform((int)System.currentTimeMillis(), graph.platforms.get(i)
		    .getServices(), graph.platforms.get(i).getStrategy()));
		Logger.getLogger(CreationFates.class).log(Level.INFO,
		    "Platform <" + graph.platforms.get(i) + "> has been cloned");
	}
}
}
