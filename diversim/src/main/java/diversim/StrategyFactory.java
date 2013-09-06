package diversim;

import java.util.ArrayList;
import java.util.List;

import diversim.strategy.extinction.AgingExtinctionStrategy;
import diversim.strategy.extinction.ExtinctionStrategy;
import diversim.strategy.extinction.OrphanExtinctionStrategy;
import diversim.strategy.reproduction.AppReproductionStrategy;
import diversim.strategy.reproduction.AppSpeciationReproduction;
import diversim.strategy.reproduction.ExtensionSpeciation;
import diversim.strategy.reproduction.PlatformReproductionStrategy;
import diversim.strategy.reproduction.PlatformSpeciationReproduction;
import diversim.strategy.reproduction.ReductionSpeciation;
import ec.util.MersenneTwisterFast;

public class StrategyFactory {
	
	public final static int APP_MAX_LIFE = 300;
	
	public static StrategyFactory fINSTANCE = new StrategyFactory();
	
	protected StrategyFactory(){
		
	}
	
	private static AppReproductionStrategy appReproductionStrategy = null;
	/**
	 * Currently, reproduction strategy is general, so only one instance is enough
	 * @param graph
	 * @return
	 */
	public AppReproductionStrategy createAppReproductionStrategy(BipartiteGraph graph){
		
		if(appReproductionStrategy != null) 
			return appReproductionStrategy;
		
		List<Service> allServices = graph.services;
		AppSpeciationReproduction asr = new AppSpeciationReproduction();
		
		asr.getStrategies().add(new ReductionSpeciation());
		asr.getStrategies().add(new ExtensionSpeciation());
		asr.setAllServices(allServices);
		
		return asr;
	}
	
	/**
	 * Now, the platforms always generate a "degenerated child";
	 * @param allServices
	 * @return
	 */
	public PlatformReproductionStrategy createPlatformReproductionStrategy(List<Service> allServices){
		PlatformSpeciationReproduction psr = new PlatformSpeciationReproduction();
		psr.getStrategies().add(new ReductionSpeciation());
		psr.setAllServices(allServices);
		
		return psr;
	}
	
	public List<ExtinctionStrategy> createAppExtinctionStrategies(Entity entity, BipartiteGraph graph){
		MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		List<ExtinctionStrategy> killers = new ArrayList<ExtinctionStrategy>();
		killers.add(new AgingExtinctionStrategy(entity, graph.schedule.getSteps(), rnd.nextInt(APP_MAX_LIFE)));
		killers.add(new OrphanExtinctionStrategy(entity));
		return killers;
		
	}
	

}
