/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diversim.strategy.fate;

import diversim.model.BipartiteGraph;
import diversim.model.Entity;
import diversim.model.Fate;
import diversim.model.Platform;
import diversim.strategy.AbstractStrategy;
import diversim.strategy.extinction.AgingExtinctionWithDegreeStrategy;
import diversim.strategy.extinction.ExtinctionStrategy;
import diversim.strategy.extinction.PlatformExtinctionStrategy;
import diversim.strategy.reproduction.DNAExtensionSpeciation;
import diversim.strategy.reproduction.DNAReductionSpeciation;
import diversim.strategy.reproduction.PlatformClonalReproduction;
import diversim.strategy.reproduction.PlatformReproductionWithPossibility;
import diversim.strategy.reproduction.PlatformSpeciationReproductionByDNA;
import diversim.strategy.reproduction.ReproStrategy;
import diversim.util.config.Configuration;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author huis
 */
public class EvolveAndSelectModel extends AbstractStrategy<Fate>{
    
    public EvolveAndSelectModel(){
        super("EvolveAndSelect");
        reproducers = new ArrayList<ReproStrategy<Platform>>();
        
    }
    
    List<ReproStrategy<Platform>> reproducers;
    ExtinctionStrategy<Entity> killer;

    @Override
    public void evolve(BipartiteGraph graph, Fate agent) {
        List<Platform> produced = new ArrayList<Platform>();
        
        
        
        List<Platform> originals = new ArrayList<Platform>();
        originals.addAll(graph.platforms);
        
        List<Platform> toRemoves =  new ArrayList<Platform>();
        for(Platform pltf : originals){
            if(killer.die(pltf, graph))
                toRemoves.add(pltf);
        }
        
        for(Platform pltf : toRemoves){
            graph.removeEntity(graph.platforms, pltf);
        }
        
        originals.clear();
        originals.addAll(graph.platforms);
        
        for(Platform pltf : originals){
            for(ReproStrategy<Platform> reproducer : reproducers){
                produced.addAll(reproducer.reproduce(pltf, graph));
            }
        }
        
        
        
    }
    
    @Override
    public void init(String stratId) {
       
        
        if(Configuration.contains(stratId+".clone")){
            double probability = Configuration.getDouble(stratId+".clone");
            reproducers.add(
                    new PlatformReproductionWithPossibility(probability,
                        new PlatformClonalReproduction("clone")
			));
        }
        
        if(Configuration.contains(stratId+".extend")){
            double probability = Configuration.getDouble(stratId+".extend");
            PlatformSpeciationReproductionByDNA extender =
                    new PlatformSpeciationReproductionByDNA(
                            new DNAExtensionSpeciation()
                        );
            extender.max_size = Configuration.getInt(stratId + ".p_max_size");
            extender.min_size = Configuration.getInt(stratId + ".p_min_size");
            reproducers.add(
                    new PlatformReproductionWithPossibility(probability,
                        extender
                    )			
			);
        }
                
        if(Configuration.contains(stratId+".reduce")){
            double probability = Configuration.getDouble(stratId+".reduce");
            PlatformSpeciationReproductionByDNA reducer = 
                    new PlatformSpeciationReproductionByDNA(
                            new DNAReductionSpeciation()
					);
            reducer.max_size = Configuration.getInt(stratId + ".p_max_size");
            reducer.min_size = Configuration.getInt(stratId + ".p_min_size");
            reproducers.add(
                    new PlatformReproductionWithPossibility(probability,
                        reducer
			));
        }
        
        killer = (ExtinctionStrategy<Entity>) BipartiteGraph.getStrategy(Configuration.getString(stratId + ".kill"));
        
    }
    
}
