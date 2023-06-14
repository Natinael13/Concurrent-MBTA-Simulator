import java.util.HashMap;

public class Passenger extends Entity {
  private Passenger(String name) { super(name); }

  //make hashmap to keep track of Passengers already made/cacheing method
  public static HashMap<String, Passenger> AllPassengers = new HashMap<>();
  public static Passenger make(String name) {
    //check if Passenger has already been made
    if(AllPassengers.containsKey(name)){
      //if so return the already made Passenger
      return AllPassengers.get(name);
    }
    //else if it has not been made yet, make a new instance and return it
    Passenger newpassenger = new Passenger(name);
    AllPassengers.put(name, newpassenger);
    return newpassenger;
  }
}
