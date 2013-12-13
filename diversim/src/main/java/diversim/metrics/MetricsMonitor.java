package diversim.metrics;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import diversim.BipartiteGraphWithUI;
import diversim.model.App;
import diversim.model.BipartiteGraph;
import diversim.model.Platform;
import diversim.util.config.Configuration;

/**
 * MetricsMonitor is a nexus for all the metrics methods. Each monitor stands
 * during a single simulation run, and record the snapshots at the time points
 * when there is a explicit invocation to the {@link recordSnapshot} method.
 * Here a snapshot means a set of values for the specified variables. (A simple
 * way here is to invoke it in the {@link BipartiteGraphWithUI.step}) After each
 * simulation run (or during it, of course) we can retrieve a list of all the
 * values recorded for a specific variable at all the steps. After several runs,
 * we can also calculate the average value at each step between the different
 * runs.
 *
 * The configuration of MetricMonitor is solely dependent to the configuration
 * file. External users can only invoke the static factory method
 * {@link MetricsMonitor.createMetricsInstance} to obtain an instance of
 * monitor, and inside this method, it will refer to the configuration file to
 * decide what metrics methods should be used.
 *
 * The configuration file is like this:
 *
 * metrics true { ShannonPlatform true GiniSimpsonPlatforms true AveDiffPlatform
 * true NumOfPlatform true NumOfPlatformSpecies true RedundancyOfPlatform true
 * RedundancyOfPlatformToApp true WorstCaseOnePlatformFailure false
 * WorstCaseFirstAppDie false }
 *
 * Each sub property correponses to a optional metrics method, with the same
 * string value as difined in the static final fields below. If the root value
 * (metrics) is true, then any sub properties with also a true value will
 * indicate the monitor that this method should be included in the monitoring.
 *
 *
 * @author Hui Song
 */
public class MetricsMonitor {

    public static final String SHANNON_PLATFORM = "ShannonPlatform";
    public static final String GS_PLATFORM = "GiniSimpsonPlatforms";
    public static final String DIFF_PLATFORM = "AveDiffPlatform";
    public static final String NUM_PLATFORM = "NumOfPlatform";
    public static final String NUM_SPECIES_PLATFORM = "NumOfPlatformSpecies";
    public static final String REDUDANCY_PLATFORM = "RedundancyOfPlatform";
    public static final String REDUDANCY_PLATFORM_TO_APP = "RedundancyOfPlatformToApp";
    public static final String WC_ONE_PLATFORM_FAILURE = "WorstCaseOnePlatformFailure";
    public static final String WC_FIRST_APP_DIE = "WorstCaseFirstAppDie";

    public static final String NUM_APP_ALIVE = "NumOfAppAlive";

    public static final String AVE_NUM_APP_ALIVE = "AveNumOfAppAlive";
    public static final String NUM_UNSPORTEDAPP = "NumOfUnsupportedApp";
	public static final String AVG_SERVICE_OF_PLATFORMS = "AverageServiceInPlatforms";
	public static final String TO_SUPPORT_NEW_APP = "AbleToSupportNewApp";
	public static final String FAR_FROM_SUPPORTING_NEW_APP = "FarFromSupportingNewApp";
	public static final String ROBUSTNESS = "Robustness";

    /**
     * A list of all the values declared before. Make sure that it contains all
     * the names.
     */
    public static final String[] ALL_METRICS = new String[]{
        SHANNON_PLATFORM,
        GS_PLATFORM,
        DIFF_PLATFORM,
        NUM_PLATFORM,
        NUM_SPECIES_PLATFORM,
        REDUDANCY_PLATFORM,
        REDUDANCY_PLATFORM_TO_APP,
        WC_ONE_PLATFORM_FAILURE,
        WC_FIRST_APP_DIE, NUM_APP_ALIVE,
        AVE_NUM_APP_ALIVE,
        NUM_UNSPORTEDAPP,
        AVG_SERVICE_OF_PLATFORMS,
        TO_SUPPORT_NEW_APP,
        FAR_FROM_SUPPORTING_NEW_APP,
        ROBUSTNESS
    };

    public List<Long> steps = new ArrayList<Long>();

    List<String> register = null;
    BipartiteGraph graph = null;

    Map<String, List<Object>> history = new HashMap<String, List<Object>>();

    public Map<String, List<Object>> getHistory() {
        return history;
    }

    SpeciesAndPopulation<App> snp_a = null;
    SpeciesAndPopulation<Platform> snp_p = null;
    Redundancy redundancy = null;
    PlatformFailures pltfFailures = null;

    AppFailures appFailures = null;

    DiffereceOfDNAs<Platform> diff_p = null;
    Robustness robustness = null;

    /**
     * Not used any more because now we have the configuration file!
     *
     * @param graph
     * @param args
     */
    public MetricsMonitor(BipartiteGraph graph, String... args) {
        this.graph = graph;
        register = Arrays.asList(args);

        _init();

    }

    public MetricsMonitor(BipartiteGraph graph, List<String> paras) {
        this.graph = graph;
        this.register = new ArrayList(paras);
        _init();
    }

    private void _init() {
        for (String s : register) {
            if (SHANNON_PLATFORM.equals(s) && snp_p == null) {
                snp_p = new SpeciesAndPopulation<Platform>(graph.platforms);
            } else if (GS_PLATFORM.equals(s) && snp_p == null) {
                snp_p = new SpeciesAndPopulation<Platform>(graph.platforms);
            } else if (NUM_SPECIES_PLATFORM.equals(s) && snp_p == null) {
                snp_p = new SpeciesAndPopulation<Platform>(graph.platforms);
            } else if (DIFF_PLATFORM.equals(s) && diff_p == null) {
                diff_p = new DiffereceOfDNAs<Platform>(graph.platforms);
            } else if (REDUDANCY_PLATFORM.equals(s) && redundancy == null) {
                redundancy = new Redundancy(graph);
            } else if (REDUDANCY_PLATFORM_TO_APP.equals(s) && redundancy == null) {
                redundancy = new Redundancy(graph);
            } else if (WC_ONE_PLATFORM_FAILURE.equals(s) && pltfFailures == null) {
                pltfFailures = new PlatformFailures(graph);
            } else if (WC_FIRST_APP_DIE.equals(s) && pltfFailures == null) {
                pltfFailures = new PlatformFailures(graph);
            } else if (AVE_NUM_APP_ALIVE.equals(s) && appFailures == null) {
                appFailures = new AppFailures(graph);
            }
            else if(TO_SUPPORT_NEW_APP.equals(s) && pltfFailures == null)
				pltfFailures = new PlatformFailures(graph);
			else if(FAR_FROM_SUPPORTING_NEW_APP.equals(s) && pltfFailures == null)
				pltfFailures = new PlatformFailures(graph);
            else if(NUM_UNSPORTEDAPP.equals(s) && snp_a == null)
				snp_a = new SpeciesAndPopulation<App>(graph.apps);
            else if(AVG_SERVICE_OF_PLATFORMS.equals(s) && snp_p == null){
                snp_p = new SpeciesAndPopulation<Platform>(graph.platforms);
            }
            else if(ROBUSTNESS.equals(s) && robustness == null){
            	robustness = new Robustness("linkingC", "concentrationRandom");
            }

            history.put(s, new ArrayList<Object>());
        }
    }

    public Map<String, Object> getSnapshot() {
        Map<String, Object> snapshot = new HashMap<String, Object>();
        for (String s : register) {
            if (SHANNON_PLATFORM.equals(s)) {
                snp_p.setEntityList(graph.platforms);
                snapshot.put(s, snp_p.calculateShannon());
                if (register.contains(NUM_SPECIES_PLATFORM)) {
                    snapshot.put(NUM_SPECIES_PLATFORM, snp_p.getNumSpecies());
                }
            }
            else if (GS_PLATFORM.equals(s)) {
                snp_p.setEntityList(graph.platforms);
                snapshot.put(s, snp_p.calculateGiniSimpson());
            }
            else if (DIFF_PLATFORM.equals(s)) {
                diff_p.setEntities(graph.platforms);
                snapshot.put(s, diff_p.calculateAverageDifference());
            }
            else if (NUM_PLATFORM.equals(s)) {
                snapshot.put(s, graph.platforms.size());
            }
            else if (REDUDANCY_PLATFORM.equals(s)) {
                snapshot.put(s, redundancy.calculatePlatformRedundancy());
            }
            else if (REDUDANCY_PLATFORM_TO_APP.equals(s)) {
                snapshot.put(s, redundancy.calculatePlatformRedundancyToApps());
            }
            else if (WC_ONE_PLATFORM_FAILURE.equals(s)) {
                snapshot.put(s, pltfFailures.calculateWorstCaseOnePlatformFailure());
            }
            else if (WC_FIRST_APP_DIE.equals(s)) {
                snapshot.put(s, pltfFailures.calculateWorstCaseFirstAppDie());
            }
            else if (NUM_APP_ALIVE.equals(s)) {
                snapshot.put(s, graph.getAliveAppsNumber());
            }
            else if (AVE_NUM_APP_ALIVE.equals(s)) {
                snapshot.put(s, appFailures.calculateAliveAppsAverage());
            }
            else if(NUM_UNSPORTEDAPP.equals(s)){
				snapshot.put(s, snp_a.getUnusedEnitites());
			}
            else if(AVG_SERVICE_OF_PLATFORMS.equals(s)){
                snapshot.put(s, snp_p.getAvgServices());
            }
            else if(TO_SUPPORT_NEW_APP.equals(s)){
                snapshot.put(s, pltfFailures.toSupportNewApp());
            }
            else if(FAR_FROM_SUPPORTING_NEW_APP.equals(s)){
                snapshot.put(s, pltfFailures.farFromSupportingNewApp());
            }
            else if(ROBUSTNESS.equals(s)){
            	snapshot.put(s, Robustness.calculateRobustness(this.graph,
            													robustness.getLinkingMethod(),
            													robustness.getKillingMethod()));
            }
        }
        return snapshot;
    }

    public Map<String, Object> recordSnapshot() {
        Map<String, Object> snapshot = getSnapshot();
        for (Entry<String, Object> entry : snapshot.entrySet()) {
            history.get(entry.getKey()).add(entry.getValue());
        }
        steps.add(graph.schedule.getSteps());
        return snapshot;
    }

    public String filePath = null;

    public void writeHistoryToFile() {
        writeHistoryToFile(this.filePath);
    }

    public void writeHistoryToFile(String filePath) {
        for (Entry<String, List<Object>> entry : history.entrySet()) {
            String fullFileName = filePath + entry.getKey() + ".data";
            try {
                PrintWriter writer = new PrintWriter(fullFileName, "UTF-8");
                int i = 1;
                for (Object obj : entry.getValue()) {
                    if (obj instanceof Double) {
                        writer.println(String.format("%d\t%.2f", i, ((Double) obj).doubleValue()));
                    } else if (obj instanceof Integer) {
                        writer.println(String.format("%d\t%d", i, ((Integer) obj).intValue()));
                    }
                    i++;
                }
                writer.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        
        PrintWriter writer = null;
		String summaryName = filePath + "summary.data";
		List<Object> sample = history.values().iterator().next();
		try {
			writer = new PrintWriter(summaryName, "UTF-8");
            writer.printf("index,\t");
            for(String key : history.keySet())                
                writer.printf("%s,\t", key);
            writer.println("no-use");
			for(int i = 0; i < sample.size(); i++){
                int max_circle = Configuration.getInt("max_cycles");
				writer.printf("%d,\t", i % max_circle);
				for(List<Object> values : history.values()){
					Object obj = values.get(i);
					if(obj instanceof Double)
						writer.printf("%.2f,\t", ((Double)obj).doubleValue());
					else if(obj instanceof Integer)
						writer.printf("%d,\t", ((Integer)obj).intValue());
				}
				writer.println("0");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(writer != null)
				writer.close();
		}
    }

    public void clear() {
        steps.clear();
        for (List<Object> l : history.values()) {
            l.clear();
        }
    }

    /**
     * It decides what methods to use, based on the configuration file
     *
     * @param graph
     */
    public static MetricsMonitor createMetricsInstance(BipartiteGraph graph) {
//		MetricsMonitor metrics = new MetricsMonitor(graph, 
//				MetricsMonitor.SHANNON_PLATFORM, 
//				MetricsMonitor.GS_PLATFORM,
//				MetricsMonitor.DIFF_PLATFORM,
//				MetricsMonitor.NUM_PLATFORM,
//				MetricsMonitor.NUM_SPECIES_PLATFORM,
//				MetricsMonitor.REDUDANCY_PLATFORM,
//				MetricsMonitor.REDUDANCY_PLATFORM_TO_APP,
//				MetricsMonitor.WC_ONE_PLATFORM_FAILURE,
//				MetricsMonitor.WC_FIRST_APP_DIE
//			);
        String metrics_para_prefix = "metrics";
        List<String> paras = new ArrayList<String>();
        if (Configuration.getBoolean(metrics_para_prefix)) {
            for (int i = 0; i < ALL_METRICS.length; i++) {
                if (Configuration.getBoolean(metrics_para_prefix + "." + ALL_METRICS[i])) {
                    paras.add(ALL_METRICS[i]);
                }
            }
        }
        System.out.println("Metrics : Recording " + paras);
        MetricsMonitor metrics = new MetricsMonitor(graph, paras);
        
        if(Configuration.contains(metrics_para_prefix + ".filepath")){
            metrics.filePath = Configuration.getString(metrics_para_prefix + ".filepath");
        }
        else        
            metrics.filePath = "/home/aelie/Diversify/data";
        allMetrics.add(metrics);
        return metrics;
    }

    public static List<MetricsMonitor> allMetrics = new ArrayList<MetricsMonitor>();

    /**
     * All the monitors we have created before the process is terminated will be
     * preserved in the list {@link allMetrics} declared above. And we use these
     * to calculate the average values on each step.
     *
     * It is worth noting that this is the average of on run between different
     * steps. On the hand, it is the average between several runs.
     */
    public static MetricsMonitor calculateAverage() {
        MetricsMonitor avg = new MetricsMonitor(null);

        List<MetricsMonitor> useful = screenOutIncomplete();

        if (useful.size() == 0) {
            return null;
        }
        MetricsMonitor sample = useful.get(0);
        avg.history = new HashMap<String, List<Object>>();
        for (String key : sample.history.keySet()) {
            ArrayList<Object> avglist = new ArrayList<Object>();
            avg.history.put(key, avglist);
            for (int i = 0; i < sample.history.get(key).size(); i++) {
                double total = 0;
                for (MetricsMonitor mec : useful) {
                    try {
                        Object obj = mec.history.get(key).get(i);
                        total += ((obj instanceof Double)
                                ? ((Double) obj).doubleValue()
                                : ((Integer) obj).doubleValue());
                    } catch (Exception e) {
                        total += 0;
                    }

                }
                avglist.add(total / useful.size());
            }
        }
        return avg;
    }
    
    public static MetricsMonitor combineTotal(){
		MetricsMonitor tot = new MetricsMonitor(null);
        tot.filePath = allMetrics.get(0).filePath + "tot/";
		
		List<MetricsMonitor> useful = screenOutIncomplete();
		
		
		
		if(useful.size() == 0)
			return null;
		MetricsMonitor sample = useful.get(0);
		
		/* Printing takes a long time. Can't afford to print to stdout
		for(String key : sample.history.keySet()){
			System.out.printf("%s, \t", key);
		}
		*/
		//System.out.println("no use");
		
		tot.history = new HashMap<String, List<Object>>();
		for(String key : sample.history.keySet()){
			ArrayList<Object> avglist = new ArrayList<Object>();			
			tot.history.put(key, avglist);			
				
			for(MetricsMonitor mec : useful){
				avglist.addAll(mec.history.get(key));
			}
			
		}
		return tot;
	}

    public static List<MetricsMonitor> screenOutIncomplete() {
        List<MetricsMonitor> useful = new ArrayList<MetricsMonitor>();
        int maxsteps = 0;
        for (MetricsMonitor m : allMetrics) {
            int current = m.history.values().iterator().next().size();
            if (current > maxsteps) {
                maxsteps = current;
            }
        }

        for (MetricsMonitor m : allMetrics) {
            int current = m.history.values().iterator().next().size();
            if (current == maxsteps) {
                useful.add(m);
            }

        }

        return useful;

    }

}
