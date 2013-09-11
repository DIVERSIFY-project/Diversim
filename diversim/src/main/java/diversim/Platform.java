package diversim;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import diversim.strategy.extinction.AppExtinctionStrategy;
import diversim.strategy.extinction.PlatformExtinctionStrategy;
import diversim.strategy.reproduction.PlatformReproductionStrategy;
import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.util.Bag;
import sim.field.network.*;


/**
 * Platforms are agents pro-actively modifying their state according to
 * some diversification rules to be included in their step() method.
 * By modifying their state they may also affect the network topology,
 * which should be updated accordingly.
 *
 * @author Marco Biazzini
 * @author Vivek Nallur
 * 
 * Platforms reproduce or kill themselves in the same way as Apps, controlled
 * by the corresponding strategies stored in {@ #reproducers} and {@ #killers},
 * respectively.
 *
 */
public class Platform extends Entity {

	// how many apps can one service on this platform  support.
	private int APP_PER_SERVICE = 1; 
	
	public boolean dead = false;
	
	public List<App> app = new ArrayList<App>();
	
	List<PlatformReproductionStrategy> reproducers;
	List<PlatformExtinctionStrategy> killers;
	// ArrayList<Service> supportedServices;

	
	public List<Platform> reproduce(BipartiteGraph state){
		List<Platform> result = new ArrayList<Platform>();
		for(PlatformReproductionStrategy reproducer : reproducers){
			result.addAll(reproducer.reproduce(this, state));
		}
		return result;
	}
	
	public void setLoadingFactor(int load){
		this.APP_PER_SERVICE = load;
	}
	
	public int getLoadingFactor(){
		return this.APP_PER_SERVICE;
	}

	public List<Service> getSupportedServices(){
		return this.services;
	}	

	public void setSupportedServices(List<Service> other_services){
		this.services = new ArrayList<Service>(other_services);
	}

	public void addSupportedServices(List<Service> other_services){
		Set<Service> current_services = new HashSet<Service>(this.services);
		for (Service srv: other_services){
			current_services.add(srv);
		}
		this.services = new ArrayList<Service> (current_services);
	}
	
	public Platform(int id, List<Service> servs) {
	  super(id);
	  this.services = new ArrayList<Service> (servs); 
	}

	public Platform(int id, List<Service> servs, int loading_factor) {
	  super(id);
	  this.services = new ArrayList<Service> (servs); 
	  this.APP_PER_SERVICE = loading_factor;
	}

	
	/*
	 * (non-Javadoc)
	 * @see diversim.Entity#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state) {
		
	  
	  BipartiteGraph graph = (BipartiteGraph)state;
	  
	  if(dieOrNot(graph))
		  return;
	  
	  MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
	  List<Platform> pltfs = reproduce(graph);
	  for(Platform pltf : pltfs){
		  graph.addPlatform(pltf);			  
	  }
	  
	  
	  System.out.println("Step " + state.schedule.getSteps() + " : " + toString());
	}
	

	@Override
	public String toString() {
	  String res = super.toString();
	  return res;
	}
	
	public void initStategies(BipartiteGraph graph){
		this.reproducers = StrategyFactory.fINSTANCE
				.createPlatformReproductionStrategy(this, graph);
		this.killers = StrategyFactory.fINSTANCE
				.createPlatformExtinctionStrategies(this, graph);
	}
	
	public boolean dieOrNot(BipartiteGraph graph){
		if(dead)
			return true;
		for(PlatformExtinctionStrategy killer : killers){
			if( killer.die(this, graph)){
				this.dead = true;
				return true;
			}
		}
		return false;
	}
	
	
}
