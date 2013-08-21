package diversim;

import java.util.ArrayList;
import java.util.List;

import sim.engine.SimState;
import sim.util.Bag;
import sim.field.network.*;

// for testing only
import ec.util.MersenneTwisterFast;


/**
 * TestingEntity is used to test any subclass of Entity to check if they support
 * the concrete implementations of all the interfaces/strategies that an Entity 
 * is required to support
 *
 * @author Vivek Nallur
 *
 */
public class TestingEntity extends Entity {

	MatchingStrategy matcher;
	SpeciationStrategy mutator;
	ArrayList<Service> services;

	public TestingEntity(int id, List<Service> servs, MatchingStrategy ms) {
	  	super(id);
		this.matcher = ms;
		this.services = new ArrayList<Service>(servs);
	}

	public void addSpeciationStrategy(SpeciationStrategy ss){
		this.mutator = ss;
	}


	/*
	 * (non-Javadoc)
	 * @see diversim.Entity#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState state) {
	  BipartiteGraph graph = (BipartiteGraph)state;
		// do nothing right now
	  }
	
	@Override
	public String toString() {
	  String res = super.toString();
	  return res;
	}

	public void reproduce(List<Service> all_services){
		MersenneTwisterFast rnd = new MersenneTwisterFast(System.nanoTime());
		if (rnd.nextBoolean()){
			System.out.println(this.getClass().getSimpleName() + "speciating using Reduction strategy");
			this.addSpeciationStrategy(new ReductionSpeciation());
			
		}else{
			System.out.println(this.getClass().getSimpleName() + "speciating using Extension strategy");
			this.addSpeciationStrategy(new ExtensionSpeciation());
		}	
		this.services =  new ArrayList<Service> (this.mutator.speciate(this.services, all_services));
	}

	public static void main(String[] args){
		Service a = new Service(1);	
		Service b = new Service(2);
		Service c = new Service(3);
		Service d = new Service(4);
		Service e = new Service(5);
		MatchingStrategy allMatcher = new AllMatchingService();
		MatchingStrategy anyMatcher = new AnyMatchingService();
		ArrayList allServices = new ArrayList();
		allServices.add(a);
		allServices.add(b);
		allServices.add(c);
		allServices.add(d);
		allServices.add(e);
		ArrayList someServices = new ArrayList();
		someServices.add(a);
		someServices.add(c);
		ArrayList diffServices = new ArrayList();
		diffServices.add(b);
		diffServices.add(d);
      ArrayList moreServices = new ArrayList();
      moreServices.add(e);
      moreServices.add(c);

		AppReproductionStrategy ars = new AppClonalReproduction();
		AppReproductionStrategy asr = new AppSpeciationReproduction();
		PlatformReproductionStrategy prs = new PlatformClonalReproduction();

		Platform platform = new Platform(10, allServices);
		platform.setMatchingStrategy(anyMatcher);
		platform.setReproductionStrategy(prs);

		App app = new App(20, someServices);
		app.setMatchingStrategy(allMatcher);
		app.setReproductionStrategy(ars);

      App app1 = new App(30, diffServices);
		app1.setMatchingStrategy(allMatcher);
		app1.setReproductionStrategy(asr);

      App app2 = new App(40, moreServices);
		app2.setMatchingStrategy(allMatcher);
		app2.setReproductionStrategy(asr);

      List<App> all_apps = new ArrayList<App>(3);
      all_apps.add(app);
      all_apps.add(app1);
      all_apps.add(app2);

		System.out.println("Platform matches app?: " + platform.matches(app));
		System.out.println("App matches Platform?: " + app.matches(platform));
		System.out.println("Platform always matches platform: " + platform.matches(platform));
		System.out.println("App always matches app: " + app.matches(app));

		List<App> child_apps = app.reproduce(all_apps);
		List<Platform> child_platforms = platform.reproduce(null);

		for (App child: child_apps){
			System.out.println(child);
		}
		for (Platform child: child_platforms){
			System.out.println(child);
		}

      List<App> children_app1 = app1.reproduce(all_apps);
      List<App> children_app2 = app2.reproduce(all_apps);

		for (App child: children_app1){
			System.out.println(child);
		}

		for (App child: children_app2){
			System.out.println(child);
		}

	}
	
}
