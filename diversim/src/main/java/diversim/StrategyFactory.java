package diversim;

import java.util.ArrayList;
import java.util.List;

import diversim.strategy.extinction.AgingExtinctionStrategy;
import diversim.strategy.extinction.AppExtinctionStrategy;
import diversim.strategy.extinction.AppOrphanExtinctionStrategy;
import diversim.strategy.extinction.PlatformExtinctionStrategy;
import diversim.strategy.matching.AllMatchingService;
import diversim.strategy.matching.MatchingStrategy;
import diversim.strategy.reproduction.AppClonalReproduction;
import diversim.strategy.reproduction.AppReproductionStrategy;
import diversim.strategy.reproduction.AppReproductionWithPossibility;
import diversim.strategy.reproduction.AppSpeciationReproductionByDNA;
import diversim.strategy.reproduction.DNAExtensionSpeciation;
import diversim.strategy.reproduction.DNAReductionSpeciation;
import diversim.strategy.reproduction.PlatformReproductionStrategy;
import diversim.strategy.reproduction.PlatformReproductionWithPossibility;
import diversim.strategy.reproduction.PlatformSpeciationReproductionByDNA;
import ec.util.MersenneTwisterFast;

/**
 * 
 * A StrategyFactory is the nexus between entities and strategies. This is the only
 * where you need to decide what strategies the apps or platforms need to follow.
 * 
 * It is worth noticing that some of strategies are general, such as the current 
 * reproduction ones, whereas some others are specific to entity instances, such as 
 * the {@ AgingExtinctionStrategy} (because it need to record the birthday of each
 * entity}. For the former one, it is encouraged to utilize static fields to record
 * the instances produced before.
 * 
 * @author Hui Song
 *
 */
public class StrategyFactory {
	
	public final static int APP_MAX_LIFE = 100;
	public final static int PLATFORM_MAX_LIFE = 23;
	
	public static StrategyFactory fINSTANCE = new StrategyFactory();
	
	protected StrategyFactory(){
		
	}
	
	//private static AppReproductionStrategy appReproductionStrategy = null;
	/**
	 * Currently, reproduction strategy is general, so only one instance is enough
	 * @param graph
	 * @return
	 */
	public List<AppReproductionStrategy> createAppReproductionStrategy(App app, BipartiteGraph graph){
		
		if(APP_REPRODUCTION_STRATEGIES != null)
			return APP_REPRODUCTION_STRATEGIES;
		
		List<AppReproductionStrategy> result = new ArrayList<AppReproductionStrategy>();
		result.add(new AppReproductionWithPossibility(0.01, new AppClonalReproduction()));
		result.add(new AppReproductionWithPossibility(0.01, 
				new AppSpeciationReproductionByDNA(
					new DNAExtensionSpeciation()	
						)));
		result.add(new AppReproductionWithPossibility(0.01, 
				new AppSpeciationReproductionByDNA(
					new DNAReductionSpeciation()	
						)));
		APP_REPRODUCTION_STRATEGIES = result;
		return result;
	}
	private static List<AppReproductionStrategy> APP_REPRODUCTION_STRATEGIES = null;
	
	/**
	 * Now, the platforms always generate a "degenerated child";
	 * @param allServices
	 * @return
	 */
	public List<PlatformReproductionStrategy> createPlatformReproductionStrategy(Platform platform, BipartiteGraph state){
		if(PLATFORM_REPRODUCTION_STRATEGIES != null)
			return PLATFORM_REPRODUCTION_STRATEGIES;
		
		List<PlatformReproductionStrategy> result = new ArrayList<PlatformReproductionStrategy>();
		
		
		
		PlatformReproductionStrategy reducer =	new PlatformReproductionWithPossibility(0.1,
					new PlatformSpeciationReproductionByDNA(
						new DNAReductionSpeciation()
						//new DNAExtensionSpeciation()
					)
				);
		
		//result.add(reducer);
		
		PLATFORM_REPRODUCTION_STRATEGIES = result;
		return result;
					
	}
	private static List<PlatformReproductionStrategy> PLATFORM_REPRODUCTION_STRATEGIES = null;
	
	public List<AppExtinctionStrategy> createAppExtinctionStrategies(App app, BipartiteGraph graph){
		MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		List<AppExtinctionStrategy> killers = new ArrayList<AppExtinctionStrategy>();
		killers.add(new AgingExtinctionStrategy(app, graph, APP_MAX_LIFE));
		killers.add(new AppOrphanExtinctionStrategy());
		return killers;
		
	}
	
	public List<PlatformExtinctionStrategy> createPlatformExtinctionStrategies(Platform platform, BipartiteGraph graph){
		MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		List<PlatformExtinctionStrategy> killers = new ArrayList<PlatformExtinctionStrategy>();
		
		PlatformExtinctionStrategy lifekiller = new AgingExtinctionStrategy(platform, graph, PLATFORM_MAX_LIFE);
		
		//-killers.add(lifekiller);
		
		return killers;
	}
	
	public MatchingStrategy createMatchingStrategy(){
		return new AllMatchingService();
	}
	

}
