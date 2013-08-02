package diversim;

/**
 * Services are comparable, so that they can be kept in sorted array
 * (see the constructor of the entities, e.g. {@code Platform#Platform(int, java.util.List)})
 * to optimize the access and speed up the comparison of the entities.
 *
 * A service is just a label at this point (for the neutral model). It doesn't 
 * have any actual functionality. It only exists as a marker for something a 
 * Platform supports, and an App needs. Perhaps a biological analogy is a 
 * nutrient that is present in a particular plant/food source. Thus, a food 
 * source may have multiple types of nutrients. But, a nutrient in one food 
 * source is equivalent to the same nutrient in another food source.
 *
 * @author Marco Biazzini
 * @author Vivek Nallur
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
