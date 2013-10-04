package diversim.model;

import java.util.ArrayList;
import java.util.List;

import diversim.strategy.extinction.AgingExtinctionStrategy;
import diversim.strategy.extinction.AppOrphanExtinctionStrategy;
import diversim.strategy.extinction.ExtinctionStrategy;
import diversim.strategy.matching.AllMatchingService;
import diversim.strategy.matching.MatchingStrategy;
import diversim.strategy.reproduction.AppClonalReproduction;
import diversim.strategy.reproduction.AppReproductionWithPossibility;
import diversim.strategy.reproduction.AppSpeciationReproductionByDNA;
import diversim.strategy.reproduction.DNAExtensionSpeciation;
import diversim.strategy.reproduction.DNAReductionSpeciation;
import diversim.strategy.reproduction.PlatformReproductionWithPossibility;
import diversim.strategy.reproduction.PlatformSpeciationReproductionByDNA;
import diversim.strategy.reproduction.ReproStrategy;


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
	public List<ReproStrategy<App>> createAppReproductionStrategy(App app, BipartiteGraph graph){
		
		if(APP_REPRODUCTION_STRATEGIES != null)
			return APP_REPRODUCTION_STRATEGIES;
		
	List<ReproStrategy<App>> result = new ArrayList<ReproStrategy<App>>();
	result.add(new AppReproductionWithPossibility(0.01, new AppClonalReproduction(""))); // TODO
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

private static List<ReproStrategy<App>> APP_REPRODUCTION_STRATEGIES = null;
	
	/**
	 * Now, the platforms always generate a "degenerated child";
	 * @param allServices
	 * @return
	 */
public List<ReproStrategy<Platform>> createPlatformReproductionStrategy(Platform platform,
		BipartiteGraph state) {
		if(PLATFORM_REPRODUCTION_STRATEGIES != null)
			return PLATFORM_REPRODUCTION_STRATEGIES;
		
	List<ReproStrategy<Platform>> result = new ArrayList<ReproStrategy<Platform>>();
		
		
		
	ReproStrategy<Platform> reducer = new PlatformReproductionWithPossibility(0.1,
					new PlatformSpeciationReproductionByDNA(
						new DNAReductionSpeciation()
						//new DNAExtensionSpeciation()
					)
				);
		
		//result.add(reducer);
		
		PLATFORM_REPRODUCTION_STRATEGIES = result;
		return result;
					
	}

private static List<ReproStrategy<Platform>> PLATFORM_REPRODUCTION_STRATEGIES = null;
	

public List<ExtinctionStrategy<App>> createAppExtinctionStrategies(App app, BipartiteGraph graph) {
	List<ExtinctionStrategy<App>> killers = new ArrayList<ExtinctionStrategy<App>>();
	killers.add((ExtinctionStrategy)new AgingExtinctionStrategy(app, graph, APP_MAX_LIFE));
	killers.add(new AppOrphanExtinctionStrategy("")); // TODO
		return killers;
		
	}
	

public List<ExtinctionStrategy<Platform>> createPlatformExtinctionStrategies(Platform platform,
		BipartiteGraph graph) {
	List<ExtinctionStrategy<Platform>> killers = new ArrayList<ExtinctionStrategy<Platform>>();
		
	ExtinctionStrategy<Entity> lifekiller = new AgingExtinctionStrategy(platform, graph,
			PLATFORM_MAX_LIFE);
		
	killers.add((ExtinctionStrategy)lifekiller);
		
		return killers;
	}
	
	public MatchingStrategy createMatchingStrategy(){
		return new AllMatchingService();
	}
	

}
