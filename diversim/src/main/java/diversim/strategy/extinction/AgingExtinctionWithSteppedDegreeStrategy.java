/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diversim.strategy.extinction;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.util.config.Configuration;

/**
 *
 * @author huis
 */
public class AgingExtinctionWithSteppedDegreeStrategy extends AgingExtinctionStrategy{
    
    @Override
	public boolean die(Entity e, BipartiteGraph graph){
		boolean shouldDie = super.die(e, graph);
        
        //Add population control here (looks a bit ugly though)
        if(!shouldDie){
            double population = (double) graph.platforms.size();
            if(graph.random.nextDouble() < (population - graph.getMaxPlatforms()) / population )
                shouldDie = true;
        }
	
		
		if(shouldDie && e.getDegree() > 0){
			
			if(graph.random.nextDouble() < 1- Math.pow(0.75, e.getDegree()) )				
				shouldDie = false;
			
		}
        

		
        if(!shouldDie && e.services.isEmpty())
            shouldDie = true;
        
		return shouldDie;
	}
    
    public void init(String stratId) {
        expectedAge = Configuration.getInt(stratId+".expected");
    }
    
}
