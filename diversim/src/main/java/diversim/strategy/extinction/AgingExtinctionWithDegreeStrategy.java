/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diversim.strategy.extinction;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Platform;
import diversim.util.config.Configuration;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author huis
 */
public class AgingExtinctionWithDegreeStrategy extends AgingExtinctionStrategy{
    
    double selection = 0.25;
    
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
			
			if(graph.random.nextDouble() < selection )				
				shouldDie = false;
			
//			if(!shouldDie){
//				if(graph.random.nextInt(40) < e.services.size())
//					shouldDie = true;
//			}
		}
        

		
        if(!shouldDie && e.services.isEmpty())
            shouldDie = true;
        
		return shouldDie;
	}
    
    /**
     * This method actually kills the platforms rather than just marking them for the kill.
     * This allows it to be called via the Robustness class
     * @param BipartiteGraph: The graph which is being modified by the killing strategy
     * @param int: This int is only present for compatibility with other kill strategies in KillFates
     */
    public static void ageAndDie(BipartiteGraph graph, int someUselessNumber){
    	 List<Platform> originals = new ArrayList<Platform>();
         originals.addAll(graph.platforms);
         
         List<Platform> toRemoves =  new ArrayList<Platform>();
         for(Platform pltf : originals){
        	 boolean shouldDie = false;
        	 int expectedAge = 5;
        	 long steps = graph.getCurCycle();
        	 if (steps - pltf.getBirthCycle() >= expectedAge) {
        		 	shouldDie = true;
        	 }else{
        		 shouldDie = false;
        	 }
        	 
        	//Add population control here (looks a bit ugly though)
             if(!shouldDie){
                 double population = (double) graph.platforms.size();
                 if(graph.random.nextDouble() < (population - graph.getMaxPlatforms()) / population )
                     shouldDie = true;
             }
     		
     		if(shouldDie && pltf.getDegree() > 0){
     			if(graph.random.nextDouble() < 0.25 ){ 				
     				shouldDie = false;
     			}
     		}
                		
            if(!shouldDie && pltf.services.isEmpty()){
                 shouldDie = true;
            }

        	if(shouldDie){		
        		toRemoves.add(pltf);
        	}
         }
         
         for(Platform pltf : toRemoves){
             graph.removeEntity(graph.platforms, pltf);
         }
    }
    
    public void init(String stratId) {
        expectedAge = Configuration.getInt(stratId+".expected");
        selection = Configuration.getDouble(stratId+".selection");
    }
    
}
