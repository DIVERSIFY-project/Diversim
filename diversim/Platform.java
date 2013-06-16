package diversim;

import sim.engine.SimState;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.MutableDouble2D;
import sim.field.network.*;


public class Platform extends Entity {




public Platform() {
  // TODO Auto-generated constructor stub
}

@Override
public void step(SimState state) {
  BipartiteGraph graph = (BipartiteGraph) state;
  Double2D me = graph.sysSpace.getObjectLocation(this);
  MutableDouble2D sumForces = new MutableDouble2D();
  friendsClose = enemiesCloser = 0.0;

  // Go through my partners and determine how much I want to be near them
  MutableDouble2D forceVector = new MutableDouble2D();
  Bag out = graph.bipartiteNetwork.getEdges(this, null);
  int len = out.size();
  for (int buddy = 0; buddy < len; buddy++) {
    Edge e = (Edge)(out.get(buddy));
    double buddiness = ((Double)(e.info)).doubleValue();
    // I could be in the to() end or the from() end. getOtherNode is a cute function
    // getOtherNode is a cute function which grabs the guy at the opposite end from me.
    Double2D him = graph.sysSpace.getObjectLocation(e.getOtherNode(this));
    if (buddiness >= 0) { // the further I am from him the more I want to go to him
      forceVector.setTo((him.x - me.x) * buddiness, (him.y - me.y) * buddiness);
      if (forceVector.length() > MAX_FORCE) // I’m far enough away
        forceVector.resize(MAX_FORCE);
      friendsClose += forceVector.length();
    } else {// the nearer I am to him the more I want to get away from him, up to a limit
      forceVector.setTo((him.x - me.x) * buddiness, (him.y - me.y) * buddiness);
      if (forceVector.length() > MAX_FORCE) // I’m far enough away
        forceVector.resize(0.0);
      else if (forceVector.length() > 0)
        forceVector.resize(MAX_FORCE - forceVector.length()); // invert the distance
      enemiesCloser += forceVector.length();
    }
    sumForces.addIn(forceVector);
  }

  
  // add in a vector to the center of the space, so we don’t go too far away
  sumForces.addIn(new Double2D((graph.sysSpace.width * 0.5 - me.x) * graph.getIn,
  (graph.sysSpace.height * 0.5 - me.y) * graph.getIn));
  // add a bit of randomness
  sumForces.addIn(new Double2D(graph.getOut * (graph.random.nextDouble() * 1.0 - 0.5),
      graph.getOut * (graph.random.nextDouble() * 1.0 - 0.5)));
  sumForces.addIn(me);
  graph.sysSpace.setObjectLocation(this, new Double2D(sumForces));

}

}
