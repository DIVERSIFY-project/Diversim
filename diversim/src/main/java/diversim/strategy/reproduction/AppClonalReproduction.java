package diversim.strategy.reproduction;

import java.util.List;
import java.util.ArrayList;

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
public class AppClonalReproduction implements AppReproductionStrategy{

		@Override
		public List<App> reproduce(App parent, BipartiteGraph state) {
			App child = state.createApp(parent.getDependencies());
			child.initStrategies(state);
			ArrayList<App> children = new ArrayList<App>();
			children.add(child);
			return children;
		}
}
