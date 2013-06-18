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


public class BipartiteGraphWithUI extends GUIState {

public Continuous2D sysSpace = new Continuous2D(1.0,100,100);
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


public static void main(String[] args)
{
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
//  for (Object obj : graph.bipartiteNetwork.allNodes) {
//    sysSpace.setObjectLocation(obj,
//        new Double2D(sysSpace.getWidth() * 0.5 + state.random.nextDouble() - 0.5, sysSpace
//            .getHeight() * ((obj instanceof Platform ? 0.35 : 0.65))));
//  }
  Double2D pos;
  double dist = sysSpace.getWidth() / (graph.apps.size() + 1);
  for (Object obj : graph.apps) {// TODO: consider entity size/2 ?
    pos = new Double2D(dist * i++, sysSpace.getHeight() * (0.35 + state.random.nextInt(2) * 0.10));
    sysSpace.setObjectLocation(obj, pos);
  }
  i = 1;
  dist = sysSpace.getWidth() / (graph.platforms.size() + 1);
  for (Object obj : graph.platforms) {
    pos = new Double2D(dist * i++, sysSpace.getHeight() * (0.65 + state.random.nextInt(2) * 0.10));
    sysSpace.setObjectLocation(obj, pos);
  }
}


public void start() {
  super.start();
  // clear the space
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
          double dist = sysSpace.getWidth() / (((BipartiteGraph)state).apps.size() + 1);
          scale = 2;
          info.draw.width = (app.getSize() * scale) >= (dist - 1) ? dist - 1 : app.getSize();
          super.draw(object, graphics, info);
        }
      });
  entitiesPortrayal.setPortrayalForClass(
      Platform.class, new OvalPortrayal2D() {// inline subclass to override draw()
        public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
          Platform plat = (Platform)object;
          paint = new Color((int)(255 * plat.getPressure()), 0, 0);
          double dist = sysSpace.getWidth() / (((BipartiteGraph)state).apps.size() + 1);
          scale = 2;
          info.draw.width = (plat.getSize() * scale) >= (dist - 1) ? dist - 1 : plat.getSize();
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
  display = new Display2D(600, 600, this);
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




}
