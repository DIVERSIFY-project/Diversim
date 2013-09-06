package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.Service;

public class AbstractSpeciationReproduction {

	public AbstractSpeciationReproduction() {
		super();
	}
	
	ArrayList<SpeciationStrategy> strategies = new ArrayList<SpeciationStrategy>();
	
	List<Service> allServices = null;
	
	public List<Service> getAllServices(){
		return allServices;
	}
	
	public void setAllServices(List<Service> allServices){
		this.allServices = new ArrayList<Service>(allServices);
	}
	
	public List<SpeciationStrategy> getStrategies(){
		return strategies;
	}
	
}