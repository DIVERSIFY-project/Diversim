package diversim.strategy.extinction;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.util.config.Configuration;


public class AgingExtinctionStrategy extends ExtinctionStrategy<Entity> {
	
protected int expectedAge;

	
public AgingExtinctionStrategy() {
	super("aging"); // TODO

}


public boolean die(Entity e, BipartiteGraph graph) {
    boolean shouldDie = false;
    long steps = graph.getCurCycle();
	if (steps - e.getBirthCycle() >= expectedAge) {
			shouldDie = true;
		}
		
        if(!shouldDie){
            double population = (double) graph.platforms.size();
            if(graph.random.nextDouble() < (population - graph.getMaxPlatforms()) / population )
                shouldDie = true;
        }
	
		
		
        if(!shouldDie && e.services.isEmpty())
            shouldDie = true;
        
		return shouldDie;
}

@Override
public void evolve(BipartiteGraph graph, Entity agent) {
	agent.dead = die(agent, graph);

}

    public void init(String stratId) {
        expectedAge = Configuration.getInt(stratId+".expected");
    }

}
