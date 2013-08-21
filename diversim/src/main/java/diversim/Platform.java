package diversim;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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
 */
public class Platform extends Entity {

	// how many apps can one service on this platform  support.
	private int APP_PER_SERVICE = 1; 
	
	PlatformReproductionStrategy reproducer;
	// ArrayList<Service> supportedServices;

	public void setReproductionStrategy(PlatformReproductionStrategy rs){
		this.reproducer = rs;
	}
	
	public List<Platform> reproduce(List<Platform> possible_mates){
		return this.reproducer.reproduce(this, possible_mates);
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
	
	  }
	

	@Override
	public String toString() {
	  String res = super.toString();
	  return res;
	}
	
}
