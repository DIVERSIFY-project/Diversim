package diversim.strategy;

import sim.engine.Steppable;


/**
 * @author mbiazzin
 * @param <T>
 *
 */
public abstract class AbstractStrategy<T extends Steppable> implements Strategy<T>, Comparable<Object> {

protected String name;

protected AbstractStrategy(String n) {
  name = n;
}


/* (non-Javadoc)
 * @see java.lang.Comparable#compareTo(java.lang.Object)
 */
@Override
public int compareTo(Object o) {
  if (o instanceof AbstractStrategy<?>)
    return name.compareTo(((AbstractStrategy<?>)o).name);
  else if (o instanceof String)
    return name.compareTo((String)o);
  return 0;
}


public boolean equals(Object o) {
  return compareTo(o) == 0;
}


public String toString() {
  return name + " ( " + this.getClass().getSimpleName() + "#" + this.hashCode() + " )";
}


public void init(String stratId) {}
}
