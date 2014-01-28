package diversim.metrics;


import java.util.ArrayList;
import java.util.List;


public class RobustnessResults {

List<Integer> aliveAppsHistory;

double robustnessAL; // measured against (initApps x numPlatforms) with relinking
double robustnessBL; // measured against (numApps x numPlatforms) with relinking
double robustnessANoL; // measured against (initApps x numPlatforms) with no relinking
double robustnessBNoL; // measured against (numApps x numPlatforms) with no relinking


public RobustnessResults() {
	aliveAppsHistory = new ArrayList<Integer>();
	robustnessAL = 0;
	robustnessBL = 0;
	robustnessANoL = 0;
	robustnessBNoL = 0;
}


public List<Integer> getAliveAppsHistory() {
	return aliveAppsHistory;
}


public void setAliveAppsHistory(List<Integer> aliveAppsHistory) {
	this.aliveAppsHistory = aliveAppsHistory;
}


public double getRobustness() { // FIXME what about the other kinds of robustness?
	return robustnessAL;
}


public double getRobustnessAL() {
	return robustnessAL;
}


public double getRobustnessBL() {
	return robustnessBL;
}


public double getRobustnessANoL() {
	return robustnessANoL;
}


public double getRobustnessBNoL() {
	return robustnessBNoL;
}


public void setRobustnessL(double robA, double robB) {
	this.robustnessAL = robA;
	this.robustnessBL = robB;
}


public void setRobustnessNoL(double robA, double robB) {
	this.robustnessANoL = robA;
	this.robustnessBNoL = robB;
}


@Override
public String toString() {
	return "Rob_AL=" + robustnessAL + " | " + "Rob_BL=" + robustnessBL + " | " +
			"Rob_ANoL=" + robustnessANoL + " | " + "Rob_BNoL=" + robustnessBNoL + " | " +
			"Apps=" + aliveAppsHistory;
}

}