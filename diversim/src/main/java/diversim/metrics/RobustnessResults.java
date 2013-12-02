package diversim.metrics;


import java.util.ArrayList;
import java.util.List;


public class RobustnessResults {

List<Integer> aliveAppsHistory;

double robustness;


public RobustnessResults(List<Integer> aliveAppsHistory, double robustness) {
	this.aliveAppsHistory = aliveAppsHistory;
	this.robustness = robustness;
}


public RobustnessResults() {
	aliveAppsHistory = new ArrayList<Integer>();
	robustness = 0;
}


public List<Integer> getAliveAppsHistory() {
	return aliveAppsHistory;
}


public void setAliveAppsHistory(List<Integer> aliveAppsHistory) {
	this.aliveAppsHistory = aliveAppsHistory;
}


public double getRobustness() {
	return robustness;
}


public void setRobustness(double robustness) {
	this.robustness = robustness;
}

}