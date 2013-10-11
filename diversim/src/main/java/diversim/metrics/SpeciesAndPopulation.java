package diversim.metrics;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import diversim.model.Entity;
import diversim.model.Service;

/**
 * 
 * Given a bipartite graph, identify all the *species* and count the *population* of each
 * species. Here a species means a group of entities (either App or Platform) that have the
 * same services.
 * 
 * @author Hui Song
 *
 */
public class SpeciesAndPopulation<E extends Entity> {
	
	private List<E> entities = null;
	private Map<BigInteger, Integer> species = null;
	
	
	public SpeciesAndPopulation(List<E> entities){
		this.entities = entities;		
	}

	
	
	public BigInteger encode(List<Service> dna){
		BigInteger code = BigInteger.valueOf(0);
		for(Service service : dna){
			int id = service.getID();
			code = code.or(BigInteger.ONE.shiftLeft(id));
		}		
		return code;
	}
	
	public Map<E, BigInteger> encodeEntityList(List<E> entities){
		Map<E, BigInteger> code = new HashMap<E, BigInteger>();
		for(E e : entities)
			code.put(e,encode(e.services));
		return code;
	}
	
	public Map<BigInteger,Integer> countSpecies(){
		Map<E, BigInteger> entityCode = encodeEntityList(entities);		
		Map<BigInteger, Integer> species= new HashMap<BigInteger, Integer>();
		this.species = species;
		for(BigInteger code : entityCode.values()){
			Integer count = species.get(code);
			if(count == null)
				count = new Integer(0);
			count = count + 1;
			species.put(code, count);
		}
		return species;
	}
	
	public List<Double> getSpeciesProportion(Map<BigInteger,Integer> species){
		List<Double> proportion = new ArrayList<Double>();
		List<Integer> dist = new ArrayList<Integer>( species.values());
		Double total = Double.valueOf(0);
		for(Integer i : dist){
			total += i.doubleValue();
		}
		
		for(Integer i : dist){
			proportion.add(i.doubleValue() / total);
		}
		
		return proportion;
	}
	
	public double calculateShannon(){
		Map<BigInteger, Integer> species = countSpecies();
		List<Double> proportion = getSpeciesProportion(species);
		double h = 0;
		for(Double p : proportion)
			h -= p * Math.log10(p);
		return h;
		
	}
	
	public double calculateSimpson(){
		
		Map<BigInteger, Integer> species = countSpecies();
		if(species.size() == 0)
			return 1;
		List<Double> proportion = getSpeciesProportion(species);
		double simpson = 0;
		for(Double p : proportion)
			simpson += p*p;
		return simpson;
	}
	
	public double calculateGiniSimpson(){
		return 1-calculateSimpson();
	}
	
	public double calculateEvennessShannon(){
		if(species.size() <= 1)
			return 0.0;
		return calculateShannon()/Math.log10(species.size());
	}
	
	public void setEntityList(List<E> entities){
		this.entities = entities;
	}
	
	/**
	 * only invoked after calculateSimpson or calculateShannon
	 */
	public int getNumSpecies(){
		return species.size();
	}
	
	public int getNumIndividual(){
		return entities.size();
	}
	
}
