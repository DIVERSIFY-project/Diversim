package diversim;


import java.util.List;
import java.util.HashSet;
import java.util.Set; // Interface

import sim.engine.SimState;

/**
 * Apps rely on specific services to function, and if an App cannot find a 
 * Platform that provides at least these services, then it dies.
 * @author Vivek Nallur
 *
 */
public class App extends Entity {

double redundancy = 0;


public double getRedundancy() {
  return redundancy;
}

List<Service> dependencies;

public List<Service> getDependencies(){
        return this.dependencies;
}

public void addDependencies(List<Service> new_deps){
        Set all_services = new HashSet(this.dependencies);
        all_services.addAll(new_deps);
        this.dependencies.clear();
        this.dependencies.addAll(all_services);        
}

public void removeDependencies(List<Service> obs_deps){
        // The removeAll method should ideally execute in O(n) time
        // however at least as of java 1.7, it does not. Inserting
        // the obsolete_dependencies into a HashSet ensures that array 
        // compaction happens only once, and thus is reasonably optimal

        this.dependencies.removeAll(new HashSet(obs_deps));
}


public App(int id, List<Service> service_dependencies) {
        super(id);
        this.dependencies = service_dependencies;
}

/**
 * This method should really be called clone, since reproduction in this case 
 * is really just cloning of the object properties. But, we don't want to clash 
 * with java's own clone method inside Object
 *
 */
public App reproduce(){
        App child = new App(this.ID, this.dependencies);
        return child;
}


/*
 * (non-Javadoc)
 * @see diversim.Entity#step(sim.engine.SimState)
 *
 * This is where we want the command pattern to be invoked from the bipartite 
 * graph. 
 */
@Override
public void step(SimState state) {
  BipartiteGraph graph = (BipartiteGraph) state;

// TODO something

  redundancy = ((double)degree) / graph.numPlatforms;
  System.out.println("Step " + state.schedule.getSteps() + " : " + toString());
}


@Override
public String toString() {
  String res = super.toString();
  res += " ; redundancy = " + redundancy;
  return res;
}

}
