package diversim.strategy.reproduction;

import java.util.ArrayList;
import java.util.List;

import diversim.Platform;
import diversim.Service;
import ec.util.MersenneTwisterFast;

public class PlatformSpeciationReproduction 
		extends AbstractSpeciationReproduction
		implements PlatformReproductionStrategy {

	
	
	@Override
	public List<Platform> reproduce(Platform parent,
			List<Platform> other_platforms) {
		// TODO Auto-generated method stub
		MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());		
		SpeciationStrategy strategy = getStrategies().get(rnd.nextInt(getStrategies().size()));		
		List<Service> services = strategy.speciate(parent.services, getAllServices());		
		List<Platform> children = new ArrayList<Platform>();		
		children.add(new Platform(rnd.nextInt(), services, parent.getLoadingFactor()));
		return children;
	}

}
