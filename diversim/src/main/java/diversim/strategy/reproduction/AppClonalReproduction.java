package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.model.App;
import diversim.model.BipartiteGraph;


/**
 * This class implements the clonal reproduction strategy
 * i.e., the App child that is created is exactly the same as the parent App
 * This strategy ignores the second parameter, and merely concerns itself with
 * the parent. Also, it returns only one child, as a clone.
 *
 * @author Vivek Nallur
 */
public class AppClonalReproduction extends ReproStrategy<App> {

public AppClonalReproduction(String n) {
	super(n);
	// TODO Auto-generated constructor stub
}


		@Override
		public List<App> reproduce(App parent, BipartiteGraph state) {
	App child = state.createApp(""); // TODO
	child.setServices(parent.getDependencies());
			child.initStrategies(state);
			ArrayList<App> children = new ArrayList<App>();
			children.add(child);
			return children;
		}

    @Override
    public void evolve(BipartiteGraph graph, App agent) {
      reproduce(agent, graph);
    }


@Override
public void init(String stratId) {
	// TODO Auto-generated method stub

}
}
