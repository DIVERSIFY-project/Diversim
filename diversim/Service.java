package diversim;

/**
 * Services are comparable, so that they can be kept in sorted array
 * (see the constructor of the entities, e.g. {@code Platform#Platform(int, java.util.List)})
 * to optimize the access and speed up the comparison of the entities.
 *
 * @author Marco Biazzini
 *
 */
public class Service implements Comparable<Service> {

int ID;


public Service(int id) {
  ID = id;
}


@Override
public int compareTo(Service s) {
  return ID - s.ID;
}


public boolean equals(Object o) {
  if (o instanceof Service) 
    return compareTo((Service)o) == 0;
  return false;
}

}
