package diversim;


import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set; // Interface

import diversim.strategy.extinction.AppExtinctionStrategy;
import diversim.strategy.reproduction.AppReproductionStrategy;
import ec.util.MersenneTwisterFast;
import sim.engine.SimState;

/**
 * Apps rely on specific services to function
 * @author Vivek Nallur
 * 
 * In each step, an App has the authority may choose to reproduce itself, either via clone or 
 * speciation, decided by the {@link #reproducers}, which are all instances of 
 * {@ AppReproductionStrategy}.
 * 
 * An App dies if a instance of {@ AppExtinctionStrategy} in {@link #killers} suggests so. A
 * {@link #dead} app will not reproduce, and will also be removed from the bipartite graph.
 *
 * @author Hui Song
 */
public class App extends Entity {
	
	
	double redundancy = 0;
	
	public boolean dead = false;
	
	public List<Platform> platforms = new ArrayList<Platform>();
	
	
	public double getRedundancy() {
	  return redundancy;
	}
	
	List<AppReproductionStrategy> reproducers;
	List<AppExtinctionStrategy> killers;
	
	
	public List<Service> getDependencies(){
	        return this.services;
	}
	
	public void addDependencies(List<Service> new_deps){
	        Set all_services = new HashSet(this.services);
	        all_services.addAll(new_deps);
	        this.services.clear();
	        this.services.addAll(all_services);        
	}
	
	public void removeDependencies(List<Service> obs_deps){
	        // The removeAll method should ideally execute in O(n) time
	        // however at least as of java 1.7, it does not. Inserting
	        // the obsolete_dependencies into a HashSet ensures that array 
	        // compaction happens only once, and thus is reasonably optimal
	
	        this.services.removeAll(new HashSet(obs_deps));
	}
	
	
	
	
	public List<App> reproduce(BipartiteGraph state){
	  List<App> result = new ArrayList<App>();
	  for(AppReproductionStrategy reproducer : reproducers){
		  result.addAll(reproducer.reproduce(this, state));
	  }
	  return result;
   }
	
	
	public App(int id, List<Service> service_dependencies) {
	        super(id);
	        this.services = new ArrayList<Service> (service_dependencies);
	        
	        // TODO: Now only use AppSpeciationReproduction, may need to include others
	        //Not initialized from 0, so that new Apps will always survice to their first
	        // steps.
	        degree = -1;
	}
	
	public void initStrategies(BipartiteGraph graph){
		this.reproducers = StrategyFactory.fINSTANCE.createAppReproductionStrategy(this, graph);
		this.killers = StrategyFactory.fINSTANCE.createAppExtinctionStrategies(this, graph);
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
	  
	  if(dieOrNot(graph)){
		  return;
	  }
	  
	  MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
	 
	  
	  List<App> newApps = reproduce(graph);
	  for(App app : newApps){
		  graph.addApp(app);
	  }
	  
	  redundancy = ((double)degree) / graph.numPlatforms;
	  if(redundancy < 0) redundancy = 0;
	  System.out.println("Step " + state.schedule.getSteps() + " : " + toString());
	}
	
	
	@Override
	public String toString() {
	  String res = super.toString();
	  res += " ; redundancy = " + redundancy;
	  return res;
	}
	
	/**
	 * Many killers, any of them could kill an App
	 * 
	 * TODO: should introduce a "Die Strategy"
	 */
	public boolean dieOrNot(BipartiteGraph graph){
		if(dead)
			return true;
		for(AppExtinctionStrategy killer : killers){
			if( killer.die(this, graph)){
				this.dead = true;
				return true;
			}
		}
		return false;
	}
	
}
