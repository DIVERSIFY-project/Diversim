package diversim;

import sim.engine.SimState;
import sim.engine.Steppable;


abstract public class Entity implements Steppable {

public static final double MAX_FORCE = 3.0;
double friendsClose = 0.0; // initially very close to my friends
double enemiesCloser = 10.0; // WAY too close to my enemies
public double getAgitation() { return friendsClose + enemiesCloser; }


public Entity() {
  // TODO Auto-generated constructor stub
}

@Override
abstract public void step(SimState state);

}
