package diversim.strategy.application;

import java.util.Comparator;

import diversim.model.*;
import diversim.strategy.AbstractStrategy;
import sim.util.Bag;


/**
 * For each service, a number of nodes(maxapps/ maxplatforms) are randomly 
 * sampled from the population. The node with the least connections in
 * the sample is selected and is checked to see if it can connect
 * with the app. If it can't, the process is repeated. This happens 
 * a number of times(platform minimum size)
 * 
 * 
 * @author Kwaku Yeboah-Antwi
 */
public class SampleBasedRoundRobin extends AbstractStrategy<App> {

public SampleBasedRoundRobin() {
	super("SampleBasedRoundRobin");
}


public void evolve(BipartiteGraph graph, App e) {
	if (graph.getNumPlatforms() > 1) {
		int threshold = (int)Math.ceil(graph.getMaxApps() / graph.getMaxPlatforms());
		for (Service s: e.getServices()) {

			Platform p = null;
			Platform previous = null;
			
			int transfers = 0;
			do {
				Bag sample = get_sample(new Bag(graph.platforms), graph, threshold);
				previous = ((Platform)sample.top());
				

				if (previous.getDegree() < graph.getPlatformMaxLoad() && previous.getServices().contains(s)) {
					p = previous;
				} else {
					transfers++;
				}
			} while (p == null && transfers < graph.getPlatformMinSize());

			if (p !=null) {
				graph.addEdge(e, p, e.getSize());
			}
		}

	}

}

@Override
public void init(String stratId) {
	this.name = "SampleBasedRoundRobin";
}

/* Return a bag containing size platforms
 * randomly selected from the bag of platforms.
 * The bag is sorted in ascending order by degree
 * 
 */
private Bag get_sample(Bag platforms, BipartiteGraph graph, int size) {
	Bag sample = new Bag(size);
	for (int x = 0; x < size; x = x+1) {
		if (platforms.size() > 0) {
			platforms.shuffle(graph.random);
			sample.add(platforms.remove(graph.random.nextInt(platforms.size())));

		} else {
			break;
		}
	}
	sample.sort(new Comparator<Entity>() {

		@Override
		public int compare(Entity e, Entity e2) {
			return e.getDegree() - e2.getDegree();
		}
	});

	return sample;
}

}