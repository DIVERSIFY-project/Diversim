package diversim.strategy.fate;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.model.Platform;
import diversim.strategy.AbstractStrategy;


public class RandomFate extends AbstractStrategy<Fate> {

public RandomFate() {
	super("Random");
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {
	String linkingName = "linkingC";
	// cleaning graph
	List<Platform> platformsToDelete = new ArrayList<Platform>(graph.platforms);
	for (Platform platform : platformsToDelete) {
		graph.removeEntity(graph.platforms, platform);
	}
	// creating random platforms
	Platform newPlatform;
	int servicesNumber = 0;
	for (int i = 0; i < graph.getMaxPlatforms(); i++) {
		newPlatform = graph.createPlatform("platform.1");
		newPlatform.getServices().clear();
		servicesNumber = graph.random().nextInt(graph.getMaxServices());
		for (int j = 0; j < servicesNumber; j++) {
			newPlatform.getServices().add(
			    graph.services.get(graph.random().nextInt(graph.services.size())));
		}
	}
	// link
	try {
		LinkStrategyFates.class.getDeclaredMethod(linkingName,
		    LinkStrategyFates.getLinkingMethods().get(linkingName)).invoke(null, graph);
	}
	catch (IllegalAccessException e) {
		e.printStackTrace();
	}
	catch (IllegalArgumentException e) {
		e.printStackTrace();
	}
	catch (InvocationTargetException e) {
		e.printStackTrace();
	}
	catch (NoSuchMethodException e) {
		e.printStackTrace();
	}
	catch (SecurityException e) {
		e.printStackTrace();
	}
	// step
	for (App app : graph.apps) {
		app.step(graph);
	}
	for (Platform platform : graph.platforms) {
		platform.step(graph);
	}
}

}
