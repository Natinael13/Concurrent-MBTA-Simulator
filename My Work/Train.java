import java.util.HashMap;

public class Train extends Entity {
  private Train(String name) { super(name); }

  //make hashmap to keep track of trains already made/cacheing method
  public static HashMap<String, Train> AllTrains = new HashMap<>();
  public static Train make(String name) {
    //check if Train has already been made
    if(AllTrains.containsKey(name)){
      //if so return the already made train
      return AllTrains.get(name);
    }
    //else if it has not been made yet, make a new instance and return it
    Train newtrain = new Train(name);
    AllTrains.put(name, newtrain);
    return newtrain;
  }
}
