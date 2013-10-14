package diversim.strategy.fate;


import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Fate;
import diversim.model.Service;
import diversim.strategy.AbstractStrategy;
import diversim.strategy.application.LinkStrategy;


/**
 * User: Andre Date: 26/9/13 Time: 10:45 AM
 */
public class AndreFateOfDoom extends AbstractStrategy<Fate> {

public AndreFateOfDoom() {
	super("Andre");
}


@Override
public void evolve(BipartiteGraph graph, Fate agent) {
	// kill
	// 25% Backdoor / 25% Obsolescence / 25% UnattendedService / 25% Concentration
	/*
	 * Service backdoor = graph.services.get((int)(Math.random() * graph.services.size())); switch
	 * ((int)(Math.random() * 0)) { case 0: KillFates.backdoor(graph, backdoor, 0.01); break; case 1:
	 * KillFates.obsolescence(graph, 0.01); break; case 2: KillFates.unattended(graph, 0.01); break;
	 * default: KillFates.concentration(graph); }
	 */
	KillFates.unattended(graph, 0.01);
	// creation
	CreationFates.cloningRandom(graph, 0.01);
	// mutation
	MutationFates.bugCorrected(graph);
	// linking
	// LinkStrategyFates.ExclusiveServicesLinkStrategy(graph);
	for (App app : graph.apps) {
		app.step(graph);
	}
}
}
