package diversim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.LocationWrapper;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.network.SpatialNetwork2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Double2D;


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
  
  sysSpace.clear();
  
  int i = 1;
  Double2D pos;
  double dist = sysSpace.getWidth() / (graph.apps.size()+1);
  for (Object obj : graph.apps) {
    pos = new Double2D((dist * i++), sysSpace.getHeight() * 0.35);
    sysSpace.setObjectLocation(obj, pos);
  }
  i = 1;
  dist = sysSpace.getWidth() / (graph.platforms.size() + 1);
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
          //paint = new Color(0,0, (int) (255 * app.services.size() / BipartiteGraph.initServices));
          double dist = sysSpace.getWidth() / (((BipartiteGraph)state).apps.size() + 1);
          info.draw.width = ((double)app.getSize()) / ((BipartiteGraph)state).apps.size() * dist;
          if (info.draw.width >= dist) info.draw.width = dist * 0.9;
          if (info.draw.width < (dist * 0.2)) info.draw.width = dist * 0.25;
          info.draw.height = 50;
          
          
          
          super.draw(object, graphics, info);
          drawServices(graphics, info.draw, app.services, BipartiteGraph.initServices);
        }
        public Inspector getInspector(LocationWrapper wrapper, GUIState state){
        	Inspector insp = super.getInspector(wrapper, state);
        	App app = (App) wrapper.getObject();
        	return insp;
        }
      });
  entitiesPortrayal.setPortrayalForClass(
      Platform.class, new OvalPortrayal2D() {// inline subclass to override draw()
        public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
          Platform plat = (Platform)object;
          paint = new Color((int)(255 * plat.getLoadingFactor()), 0, 0);
          //paint = new Color((int)(255 * plat.services.size() / BipartiteGraph.initServices), 0, 0);
          double dist = sysSpace.getWidth() / (((BipartiteGraph)state).platforms.size() + 1);
          info.draw.width = ((double)plat.getSize()) / ((BipartiteGraph)state).numServices * dist;
          if (info.draw.width >= dist) info.draw.width = dist * 0.9;
          if (info.draw.width < (dist * 0.2)) info.draw.width = dist * 0.25;
          info.draw.height = 50;
          super.draw(object, graphics, info);
          drawServices(graphics, info.draw, plat.services, BipartiteGraph.initServices);
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
  BipartiteGraph graph = (BipartiteGraph) state;
  if(graph.apps.size()==0 || graph.platforms.size()==0)
	  return false;
  else
	  return true;
  //return !state.schedule.scheduleComplete();
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

public void drawServices(Graphics2D graphics, Rectangle2D.Double draw, java.util.List<Service> dna, int numAllServices){
	
	Paint original = graphics.getPaint();
	graphics.setPaint(new Color(255,255,255));
	//graphics.drawLine(0,0,50,50);
	graphics.drawLine((int)(draw.x), (int)(draw.y - draw.height / 2), (int)(draw.x), (int)(draw.y - draw.height/2 + draw.height * dna.size() / BipartiteGraph.initServices));
	graphics.setPaint(original);
	
}


}
