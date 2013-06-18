package diversim;


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
