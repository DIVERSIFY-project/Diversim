package fr.inria.diversim.model;

/**
 * Services are comparable, so that they can be kept in sorted array
 * (see the constructor of the entities, e.g. {@code Platform#Platform(int, java.util.List)})
 * to optimize the access and speed up the comparison of the entities.
 *
 * @author Marco Biazzini
 */
public class Service implements Comparable<Service> {

    int ID;
    int version;
    ServiceState state;
    static int maxLastVersion = 1;

    public Service(int id) {
        ID = id;
        version = 1;
        state = ServiceState.OK;
    }

    public Service(int id, int version, ServiceState state) {
        ID = id;
        this.version = version;
        this.state = state;
        maxLastVersion = Math.max(maxLastVersion,version);
    }


    @Override
    public int compareTo(Service s) {
        if (ID - s.ID == 0)
            if (version - s.version == 0)
                return state.stateToInt() - s.state.stateToInt();
            else
                return (version - s.version) * 3;
        else

            return (ID - s.ID) * 3 * maxLastVersion;
    }

    public int getID() {
        return ID;
    }

    public boolean equals(Object o) {
        if (o instanceof Service)
            return compareTo((Service) o) == 0;
        return false;
    }

    public Service newVersion() {
        return new Service(ID, version+1, ServiceState.OK);
    }

    @Override
    public String toString() {
        return ID + ":" + version + ":" +state;
    }
}
