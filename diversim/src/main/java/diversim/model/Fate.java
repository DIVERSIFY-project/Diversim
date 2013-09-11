package diversim.model;

import java.util.Arrays;

import diversim.strategy.Strategy;
import diversim.strategy.fate.FateStrategy;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.distribution.*;


/**
 * "You can't fight fate!"
 * ... That's why you'd better adapt!
 * This model can be used to inject in the simulation all the events that are
 * 'external' to the other entities. E.g.: arrival of new apps, failure of apps or services,
 * runtime change of some configuration parameter, etc.
 *
 * @author Marco Biazzini
 */
public class Fate implements Steppable {

protected Strategy<Fate> strategy;

    public Fate(FateStrategy fateStrategy) {
        strategy = fateStrategy;
    }


    // add apps timing their injection according to a zipf distribution
    @Override
    public void step(SimState state) {
      BipartiteGraph graph = (BipartiteGraph)state;
        strategy.evolve(graph, this);
    }


// just to quickly try out how values from some distribution look like...
public static void main(String[] args) {
  ec.util.MersenneTwisterFast random = new ec.util.MersenneTwisterFast();
  double sum1 = 0, sum2 = 0, val1[] = new double[1000000],
      val2[] = new double[1000000], xAxis[] = new double[1000];
  for (int i = 0, j = 0; i < 1000000; i++) {
    val1[i] = Distributions.nextWeibull(400, 1, random);
    val2[i] = Distributions.nextZipfInt(1.3, random);
    sum1 += val1[i];
    sum2 += val2[i];
    if (i == 999) {
      Arrays.sort(val1, 0, 1000);
      Arrays.sort(val2, 0, 1000);
      System.out.println("Over 1000 [1] : min=" + val1[0] + " ; med=" + val1[499] + " ; max=" + val1[999]
          + " ; avg=" + (sum1 / 1000));
      System.out.println("Over 1000 [2] : min=" + val2[0] + " ; med=" + val2[499] + " ; max=" + val2[999]
          + " ; avg=" + (sum2 / 1000));
    }
    if (i % 1000 == 0) xAxis[j++] = i + 1;
  }
  Arrays.sort(val1);
  Arrays.sort(val2);
  System.out.println("Over 1000000 [1] : min=" + val1[0] + " ; med=" + val1[499999] + " ; max="
      + val1[999999] + " ; avg=" + (sum1 / 1000000));
  System.out.println("Over 1000000 [2] : min=" + val2[0] + " ; med=" + val2[499999] + " ; max="
      + val2[999999] + " ; avg=" + (sum2 / 1000000));
  System.out.flush();
  try {
    org.jfree.data.xy.XYSeriesCollection series = new org.jfree.data.xy.XYSeriesCollection();
    org.jfree.data.xy.XYSeries data = new org.jfree.data.xy.XYSeries("Weibull", true, true);
    for (int i = 0, j = 0; i < 1000000; i += 1000)
      data.add(xAxis[j++], val1[i]);
    series.addSeries(data);
    data = new org.jfree.data.xy.XYSeries("Zipf", true, true);
    for (int i = 0, j = 0; i < 1000000; i += 1000)
      data.add(xAxis[j++], val2[i]);
    series.addSeries(data);
    org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createXYLineChart("", "step",
        "distr. value", series, org.jfree.chart.plot.PlotOrientation.VERTICAL, true, true, false);
    javax.swing.JFrame plotWindow = new javax.swing.JFrame();
    plotWindow.setTitle(chart.getTitle().getText());
    plotWindow.setSize(640, 480);
    plotWindow.setContentPane(new org.jfree.chart.ChartPanel(chart));
    plotWindow.setVisible(true);
    Thread.sleep(1000000);
  }
  catch (Exception e) {
    System.err.println(e);
  }
  finally {
    System.exit(1);
  }
}

}
