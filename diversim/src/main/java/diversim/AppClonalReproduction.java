package diversim;

import java.util.List;
import java.util.ArrayList;

import ec.util.MersenneTwisterFast;

/**
 * This class implements the clonal reproduction strategy
 * i.e., the App child that is created is exactly the same as the parent App
 * This strategy ignores the second parameter, and merely concerns itself with
 * the parent. Also, it returns only one child, as a clone.
 *
 * @author Vivek Nallur
 */
public class AppClonalReproduction implements AppReproductionStrategy{
        public List<App> reproduce(App parent, List<App> other_apps){
			MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		  	App child = new App(rnd.nextInt(), parent.getDependencies());
			ArrayList<App> children = new ArrayList<App>();
			children.add(child);
			return children;
	}
}
