package diversim.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sim.engine.SimState;
import diversim.strategy.Strategy;
import diversim.strategy.extinction.ExtinctionStrategy;
import diversim.strategy.reproduction.ReproStrategy;


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

List<ReproStrategy<Platform>> reproducers;

List<ExtinctionStrategy<Platform>> killers;
	// ArrayList<Service> supportedServices;


	public List<Platform> reproduce(BipartiteGraph state){
		List<Platform> result = new ArrayList<Platform>();
	for (ReproStrategy<Platform> reproducer : reproducers) {
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
	  action = "none";
	}

	public Platform(int id, List<Service> servs, int loading_factor) {
	  super(id);
	  this.services = new ArrayList<Service> (servs);
	  this.APP_PER_SERVICE = loading_factor;
	  action = "none";
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

	printoutCurStep(graph);
	}


	public void initStrategies(BipartiteGraph graph){
		this.reproducers = StrategyFactory.fINSTANCE
				.createPlatformReproductionStrategy(this, graph);
		this.killers = StrategyFactory.fINSTANCE
				.createPlatformExtinctionStrategies(this, graph);
	}
	
	public boolean dieOrNot(BipartiteGraph graph){
		if(dead)
			return true;
	for (ExtinctionStrategy<Platform> killer : killers) {
			if( killer.die(this, graph)){
				this.dead = true;
				return true;
			}
		}
		return false;
	}


    double pressure;
    public String action;


    public double getPressure() {
        return pressure;
    }

    public String getAction() {
        return action;
    }

public Platform() {}

public void init(String entityId, BipartiteGraph graph)  {
	super.init(entityId, graph);
	for (Service s : graph.selectServices(graph.getMaxServices())) {
		BipartiteGraph.addUnique(services, s);
	}
	pressure = 0;
	action = "none";
}

    public Platform(int id, List<Service> servs, Strategy<Platform> strategy) {
        super(id,strategy);
        for (Service s : servs) {
            BipartiteGraph.addUnique(services, s);
        }
        pressure = 0;
        action = "none";
    }


// /*
// * (non-Javadoc)
// * @see diversim.model.Entity#step(sim.engine.SimState)
// */
// @SuppressWarnings("unchecked")
// @Override
// public void step(SimState state) {
// BipartiteGraph graph = (BipartiteGraph) state;
// action = "none";
// if (getDegree() >= graph.getPlatformMaxLoad()
// && getSize() > graph.getPlatformMinSize()) {
// strategy.evolve(graph, this);
// }
// pressure = ((double) degree) / graph.getPlatformMaxLoad();
// if (pressure > 1.0) pressure = 1.0;
//
// printoutCurStep(graph);
// }


    @Override
    public String toString() {
        String res = super.toString();
        res += " ; pressure = " + pressure
                + " ; action = " + action;
        return res;
    }

}
