package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.App;
import diversim.model.Fate;
import diversim.strategy.AbstractStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Simon
 * Date: 7/8/13
 * Time: 2:20 PM
 */
public class KillApp extends AbstractStrategy<Fate> {
    protected KillApp(String n) {
    super(n);
    // TODO Auto-generated constructor stub
  }

    @Override
    public void evolve(BipartiteGraph graph, Fate agent) {
        List<App> appToKill = new ArrayList<App>();
        for(App app: graph.apps) {
            if(app.getDegree() == 0)
                appToKill.add(app);
        }
        for (App app : appToKill) {
            graph.removeEntity(app);
        }
    }
}
