package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;


/**
 * This class implements the ReproductionStrategy with clonal reproduction
 * i.e., the Platform child that is created is exactly the same as the parent Platform. Also, this strategy results in only one child.
 * @author Vivek Nallur
 */
public class PlatformClonalReproduction extends ReproStrategy<Platform> {

protected PlatformClonalReproduction(String n) {
	super(n);
	// TODO Auto-generated constructor stub
}


public List<Platform> reproduce(Platform parent, BipartiteGraph state) {
	Platform child = state.createPlatform(""); // TODO
	child.setServices(parent.getSupportedServices());
			child.setLoadingFactor(parent.getLoadingFactor());
			ArrayList<Platform> children = new ArrayList<Platform>();
			children.add(child);
			return children;
	}

  @Override
  public void evolve(BipartiteGraph graph, Platform agent) {
    reproduce(agent, graph);
  }


@Override
public void init(String stratId) {
	// TODO Auto-generated method stub

}
}
