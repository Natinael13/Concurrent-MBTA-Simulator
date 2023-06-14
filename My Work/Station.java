import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Station extends Entity {
  private Station(String name) { super(name); }

  public Lock CurrStationLoc = new ReentrantLock();
  public Condition CurrStationCondtion = CurrStationLoc.newCondition();


  //make hashmap to keep track of stations already made/cacheing method
  public static HashMap<String, Station> AllStations = new HashMap<>();

  public static Station make(String name) {
    //check if Station has already been made
    if(AllStations.containsKey(name)){
      //if so return the already made Station
      return AllStations.get(name);
    }
    //else if it has not been made yet, make a new instance and return it
    Station newstation = new Station(name);
    AllStations.put(name, newstation);
    return newstation;
  }
}
