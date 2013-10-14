package diversim.metrics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import diversim.model.Entity;
import diversim.model.Service;

/**
 * Calculate how different the DNAs of two individuals are. 
 * For each pair of Entity, the difference is the ratio between the number of 
 * services they don't have in common and the number of all services that appear
 * in either entities. The global difference is the average value between all 
 * the possible pairs.
 * 
 * @author Hui Song
 *
 * @param <E>
 */
public class DiffereceOfDNAs<E extends Entity> {
	private List<E> entities = null;
	
	public List<E> getEntities() {
		return entities;
	}

	public void setEntities(List<E> entities) {
		this.entities = entities;
	}

	public DiffereceOfDNAs(List<E> entities){
		this.entities = entities;
	}
	
	public double getOnePairDifference(E e1, E e2){
		
		HashSet<Service> s2 = new HashSet<Service>(e2.services);
		
		HashSet<Service> intersection = new HashSet<Service>(e1.services);
		intersection.retainAll(s2);
		
		
		HashSet<Service> union = new HashSet<Service>(e1.services);
		union.addAll(s2);
		
		if(union.size() == 0)
			return 0;
		else
			return 1 - ((double)intersection.size()) / union.size();
		
	}
	
	public double getAverageDifference(List<E> entities){
		if(entities.size() <= 1)
			return 0;
		double total = 0;
		for(E e1 : entities){
			for(E e2 : entities){
				total += getOnePairDifference(e1, e2);
			}
		}
		
		return total / (entities.size() * (entities.size()-1));
	}
	
	public double calculateAverageDifference(){
		return getAverageDifference(entities);
	}
}
