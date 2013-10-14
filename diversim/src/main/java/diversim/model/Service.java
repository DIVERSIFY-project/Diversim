package diversim.model;

/**
 * Services are comparable, so that they can be kept in sorted array
 * (see the constructor of the entities, e.g. {@code Platform#Platform(int, java.util.List)})
 * to optimize the access and speed up the comparison of the entities.
 *
 * @author Marco Biazzini
 */
public class Service implements Comparable<Service> {

/**
 * counter for the services
 */
public static int counter;

int id;
    int name;
    int version;
    ServiceState state;
    static int maxLastVersion = 1;

    public Service(int id) {
        this.id = id;
        name = id;
        version = 1;
        state = ServiceState.OK;
    }

    public Service(int id, int name, int version, ServiceState state) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.state = state;
        maxLastVersion = Math.max(maxLastVersion,version);
    }


    @Override
    public int compareTo(Service s) {
        return id - s.id;
    }

    public int getName() {
        return name;
    }
    
    public int getID() {
    	return id;
    }

    public boolean equals(Object o) {
        if (o instanceof Service)
            return compareTo((Service) o) == 0;
        return false;
    }

public Service newVersion(int id) {
  return new Service(id, name, version + 1, ServiceState.OK);
}

    @Override
    public String toString() {
        return name + ":" + version + ":" +state;
    }
}
