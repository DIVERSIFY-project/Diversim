package diversim.strategy.fate;


import java.util.*;

import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.strategy.AbstractStrategy;


/**
 * User: Simon Date: 7/8/13 Time: 2:33 PM
 */
public class AddPlatform extends AbstractStrategy<Fate> {

public AddPlatform() {
	super("AddPlatform");
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {
	Map<List<Service>, Integer> species = getSpecies(graph);
	List<Service> p = selectRandomSpecies(species, graph.getNumPlatforms());

	if (species.get(p).equals(1)) {
		childrenPlatform(graph, p);
	} else {
		clonePlatform(graph, p);
	}
}


protected void clonePlatform(BipartiteGraph graph, List<Service> services) {
	String kind = graph.platforms.get(0).getKind();
	Platform platform = graph.createPlatform(kind);
	platform.setServices(services);
	// graph.createLinks(platform, graph);
}


protected void childrenPlatform(BipartiteGraph graph, List<Service> p) {
	String kind = graph.platforms.get(0).getKind();
	Platform platform = graph.createPlatform(kind);

	Random r = new Random();
	p.remove(r.nextInt(p.size()));
	p.remove(r.nextInt(p.size()));

	p.addAll(graph.selectServices(2));
}


@Override
public void init(String stratId) {}


protected List<Service> selectRandomSpecies(Map<List<Service>, Integer> distribution, int nbPlatform) {
	List<List<Service>> list = new ArrayList<List<Service>>();

	for (List<Service> p : distribution.keySet()) {
		long bound = Math.round((double)nbPlatform / (double)distribution.get(p));
		for (int i = 0; i < bound; i++) {
			list.add(p);
		}
	}
	Random r = new Random();
	return list.get(r.nextInt(list.size()));
}


protected Map<List<Service>, Integer> getSpecies(BipartiteGraph graph) {
	Map<List<Service>, Integer> map = new HashMap<List<Service>, Integer>();

	for (Platform p : graph.platforms) {
		List<Service> pServices = p.getServices();
		Integer value = map.get(pServices);
		if (value != null)
			map.put(pServices, value++);
		else
			map.put(pServices, 1);
	}
	return map;
}
}
