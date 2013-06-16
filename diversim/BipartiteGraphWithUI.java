package diversim;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.*;
import sim.portrayal.simple.*;
import javax.swing.*;
import java.awt.Color;
import sim.portrayal.network.*;
import sim.portrayal.*;
import java.awt.*;


public class BipartiteGraphWithUI extends GUIState {

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


public void start() {
  super.start();
  setupPortrayals();
}


public void load(SimState state) {
  super.load(state);
  setupPortrayals();
}


public void setupPortrayals() {
  BipartiteGraph graph = (BipartiteGraph)state;
  // tell the portrayals what to portray and how to portray them
  entitiesPortrayal.setField(graph.sysSpace);
  entitiesPortrayal.setPortrayalForClass(App.class, new RectanglePortrayal2D() {// inline subclass
                                                                         // to override draw()
        public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
          Entity entity = (Entity)object;
          int agitationShade = (int)(entity.getAgitation() * 255 / 10.0);
          if (agitationShade > 255) agitationShade = 255;
          paint = new Color(agitationShade, 0, 255 - agitationShade);
          super.draw(object, graphics, info);
        }
      });
  entitiesPortrayal.setPortrayalForClass(Platform.class, new OvalPortrayal2D() {// inline subclass
                                                                              // to override draw()
        public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
          Entity entity = (Entity)object;
          int agitationShade = (int)(entity.getAgitation() * 255 / 10.0);
          if (agitationShade > 255) agitationShade = 255;
          paint = new Color(agitationShade, 0, 255 - agitationShade);
          super.draw(object, graphics, info);
        }
      });
  linksPortrayal.setField(new SpatialNetwork2D(graph.sysSpace, graph.bipartiteNetwork));
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
  display.attach(linksPortrayal, "Partners");
  display.attach(entitiesPortrayal, "Graph");
}


public void quit() {
  super.quit();
  if (displayFrame != null) displayFrame.dispose();
  displayFrame = null;
  display = null;
}




}
