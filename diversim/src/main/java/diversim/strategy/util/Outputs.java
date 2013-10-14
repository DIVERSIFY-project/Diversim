package diversim.strategy.util;


import diversim.model.BipartiteGraph;


public class Outputs {

private static String separator = System.getProperty("line.separator") + "  ";

public static void consoleOutput(BipartiteGraph graph) {
	System.out.println(graph.getPrintoutHeader() + separator
			+ "Platforms: " + graph.platforms.size() + separator
			+ "Apps: " + graph.apps.size() + separator
			+ "Services: " + graph.services.size() + separator
			);
}
}
