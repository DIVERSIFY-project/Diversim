package diversim;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.portrayal.continuous.*;
import sim.portrayal.simple.*;
import javax.swing.*;
import java.awt.Color;
import sim.portrayal.network.*;
import sim.portrayal.*;
import sim.util.Double2D;

import java.awt.*;


/**
 * GUI of the BipartiteGraph simulation model.
 *
 * @author Marco Biazzini
 *
 */
public class BipartiteGraphWithUI extends GUIState {

public Continuous2D sysSpace = new Continuous2D(1.0,1000,300);
public Display2D display;
public JFrame displayFrame;
ContinuousPortrayal2D entitiesPortrayal = new ContinuousPortrayal2D();
NetworkPortrayal2D linksPortrayal = new NetworkPortrayal2D();


public BipartiteGraphWithUI(SimState state) {
  super(state);
  // TODO Auto-generated constructor stub
}


public BipartiteGraphWithUI() {
  super(new BipartiteGraph(System.currentTimeMillis())); 
}


public static void main(String[] args) {
  BipartiteGraphWithUI vid = new BipartiteGraphWithUI();
  Console c = new Console(vid);
  c.setVisible(true);
}


public static String getName() {
  return "Platforms/Apps bipartite graph.";
}


private void setPositions() {
  BipartiteGraph graph = (BipartiteGraph)state;
  int i = 1;
  Double2D pos;
  double dist = sysSpace.getWidth() / (graph.numApps + 1);
  for (Object obj : graph.apps) {
    pos = new Double2D((dist * i++), sysSpace.getHeight() * 0.35);
    sysSpace.setObjectLocation(obj, pos);
  }
  i = 1;
  dist = sysSpace.getWidth() / (graph.numPlatforms + 1);
  for (Object obj : graph.platforms) {
    pos = new Double2D((dist * i++), sysSpace.getHeight() * 0.65);
    sysSpace.setObjectLocation(obj, pos);
  }
}


public void start() {
  super.start();
  sysSpace.clear();

  setPositions();
  setupPortrayals();
  this.scheduleRepeatingImmediatelyBefore(display);
}


public void load(SimState state) {
  super.load(state);
  setPositions();
  setupPortrayals();
}


private void setupPortrayals() {
  BipartiteGraph graph = (BipartiteGraph)state;
  // tell the portrayals what to portray and how to portray them
  entitiesPortrayal.setField(sysSpace);
  entitiesPortrayal.setPortrayalForClass(
      App.class, new RectanglePortrayal2D() {// inline subclass to override draw()
        public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
          App app = (App)object;
          paint = new Color(0, 0, (int)(255 * app.getRedundancy()));
          double dist = sysSpace.getWidth() / (((BipartiteGraph)state).numApps + 1);
          info.draw.width = ((double)app.getSize()) / ((BipartiteGraph)state).numServices * dist;
          if (info.draw.width >= dist) info.draw.width = dist * 0.9;
          if (info.draw.width < (dist * 0.2)) info.draw.width = dist * 0.25;
          info.draw.height = 50;
          super.draw(object, graphics, info);
        }
      });
  entitiesPortrayal.setPortrayalForClass(
      Platform.class, new OvalPortrayal2D() {// inline subclass to override draw()
        public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
          Platform plat = (Platform)object;
          paint = new Color((int)(255 * plat.getPressure()), 0, 0);
          double dist = sysSpace.getWidth() / (((BipartiteGraph)state).numPlatforms + 1);
          info.draw.width = ((double)plat.getSize()) / ((BipartiteGraph)state).numServices * dist;
          if (info.draw.width >= dist) info.draw.width = dist * 0.9;
          if (info.draw.width < (dist * 0.2)) info.draw.width = dist * 0.25;
          info.draw.height = 50;
          super.draw(object, graphics, info);
        }
      });
  linksPortrayal.setField(new SpatialNetwork2D(sysSpace, graph.bipartiteNetwork));
  linksPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
  // reschedule the displayer
  display.reset();
  display.setBackdrop(Color.white);
  // redraw the display
  display.repaint();
}


public void init(Controller c) {
  super.init(c);
  display = new Display2D(1200, 400, this);
  display.setClipping(false);
  displayFrame = display.createFrame();
  displayFrame.setTitle("Bipartite graph Display");
  c.registerFrame(displayFrame);
  // so the frame appears in the "Display" list
  displayFrame.setVisible(true);
  display.attach(linksPortrayal, "Links");
  display.attach(entitiesPortrayal, "Graph");
}


public boolean step() {
  super.step();
  setPositions();
  return !state.schedule.scheduleComplete();
}


public void quit() {
  super.quit();
  if (displayFrame != null) displayFrame.dispose();
  displayFrame = null;
  display = null;
}


@Override
public Object getSimulationInspectedObject() {
  return state;
}


}
