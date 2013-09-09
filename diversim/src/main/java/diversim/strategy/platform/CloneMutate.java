package diversim.strategy.platform;

import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.model.Service;
import diversim.strategy.AbstractStrategy;
import sim.util.Bag;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * User: Simon
 * Date: 7/8/13
 * Time: 10:21 AM
 */
public class CloneMutate extends AbstractStrategy<Platform> {

double factor;


public CloneMutate(String n, double m) {
  super(n);
  factor = m > 0.4 ? 0.4 : m;
}

    @Override
    public void evolve(BipartiteGraph graph, Platform platform) {
        clone_Mutate(graph,platform);
    }

    /**
     * Clone this instance and mutate both this instance and the clone, so that
     * one randomly chosen service in each instance (not the same in both) is removed.
     * Then update the network so that the apps link to the proper instance(s).
     *
     * @param graph
     */
    private void clone_Mutate(BipartiteGraph graph, Platform platform) {
        int csize, i;
        Bag temp = new Bag();
//        Bag out = graph.bipartiteNetwork.getEdges(this, null); // read-only!
//        Edge[] edges = (Edge[]) out.toArray(new Edge[0]);
//        ArrayList<Entity> ents = new ArrayList<Entity>();
//        for (Edge e : edges) {
//            ents.add((Entity) e.getOtherNode(this));
//        }
        for (i = 0; i < platform.getSize(); i++)
          temp.add(i);
        temp.shuffle(graph.random);
        Object[] set = temp.toArray();
//        System.err.println(platform);
//        for (Object o : temp)
//        System.err.println(o);
        csize = (int)Math.round(platform.getSize() * factor);
        csize = csize < 1 ? 1 : csize;

        // generate a clone that has all the services of this platform but size * factor.
        ArrayList<Service> servs = new ArrayList<Service>(platform.getServices());
        Arrays.sort(set, 0, csize);
        for (i = csize - 1; i >= 0; i--)
          servs.remove(((Integer)set[i]).intValue());
        Platform p = graph.createPlatform(servs, this);

        // remove different size * factor services from this platform
        Arrays.sort(set, csize, csize * 2);
        for (i = (csize * 2) - 1; i >= csize; i--)
          platform.getServices().remove(((Integer)set[i]).intValue());
        platform.action = "clone_mutate";
        System.out.println("\tStep " + graph.schedule.getSteps() + " : NEW " + p.toString());
    }
}
