import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class MBTA {

  // Creates an initially empty simulation
  public MBTA() { }

  //hashmap to store all train lines
  public HashMap<Train, ArrayList<Station>> lines = new HashMap<>();
  //hashmap to store all passenger journeys
  public HashMap<Passenger, ArrayList<Station>> journeys = new HashMap<>();

  //hashmap to store each passenger's current state in MBTA, key = Passenger, Value[0] = current train, Value[1] = current station, Value[2] = next station to deboard
  public HashMap<Passenger, ArrayList<Object>> PassengersState = new HashMap<>();
  //hashmap to store each train's current state in MBTA, key = Train, Value[0] = current station, Value[1] = next station
  public HashMap<Train, ArrayList<Station>> TrainsState = new HashMap<>();
  //hashmap to store each station's current state in MBTA, key = Station, Value = current train at station if there is 1 one
  public HashMap<Station, Train> StationsState = new HashMap<>();

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    ArrayList<Station> newstations = new ArrayList<>();
    //loop through list creating stations
    for(int i = 0; i < stations.size(); i++){

      //make station and add it to arraylist of stations
      newstations.add(Station.make(stations.get(i)));

      //add station to sim via StationsState hashmap if not already there
      if(StationsState.containsKey(Station.make(stations.get(i))) == false){

        //add to sim via StationsState hashmap, with no train to start
        StationsState.put(Station.make(stations.get(i)), null);

      }
    }

    //add to hashmap lines
    lines.put(Train.make(name), newstations);

    ArrayList<Station> trainvalue = new ArrayList<>();
    //Value[0] = current station, start at first station
    trainvalue.add(0,newstations.get(0));
    // Value[1] = next station, start at second station
    trainvalue.add(1,newstations.get(1));

    //add to sim via TrainsState hashmap
    TrainsState.put(Train.make(name), trainvalue);

    //update station in sim to have this train on it to start
    StationsState.put(newstations.get(0), Train.make(name));
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
    ArrayList<Station> newstations = new ArrayList<>();
    //loop through list creating stations
    for(int i = 0; i < stations.size(); i++){
      newstations.add(Station.make(stations.get(i)));
    }
    //add to hashmap lines
    journeys.put(Passenger.make(name), newstations);


    ArrayList<Object> passengervalue = new ArrayList<>();
    //Value[0] = current train, start as null
    passengervalue.add(0,null);
    //Value[1] = current station, start at first station on journey
    passengervalue.add(1,newstations.get(0));
    //Value[2] = next station to deboard at, start at second station on journey
    passengervalue.add(2,newstations.get(1));
    //add to sim via PassengersState hashmap
    PassengersState.put(Passenger.make(name), passengervalue);
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {

    //loop through trains making sure their current station is equal to their first station on line
    // Get an iterator for TrainsState
    Iterator trainIterator = TrainsState.entrySet().iterator();
    // Iterating through Hashmap checking every train begins at first station of journey
    while (trainIterator.hasNext()) {
      //get current element of hashmap
      Map.Entry mapElement = (Map.Entry)trainIterator.next();
      //do check
      if(TrainsState.get(mapElement.getKey()).get(0).equals(lines.get(mapElement.getKey()).get(0)) == false){
        throw new UnsupportedOperationException("train not at correct starting station");
      }
    }

    //loop through passengers making sure their current station is equal to their first station on journey
    // Get an iterator for PassengersState
    Iterator passIterator = PassengersState.entrySet().iterator();
    // Iterating through Hashmap checking every passenger begins at first station of journey
    while (passIterator.hasNext()) {
      //get current element of hashmap
      Map.Entry mapElement = (Map.Entry)passIterator.next();
      //do check
      if(PassengersState.get(mapElement.getKey()).get(1).equals(journeys.get(mapElement.getKey()).get(0)) == false){
        throw new UnsupportedOperationException("passenger not at correct starting station");
      }
    }

  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {

    //loop through passengers making sure their next station is equal to null indicating end of journey
    // Get an iterator for PassengersState
    Iterator passIterator = PassengersState.entrySet().iterator();
    // Iterating through Hashmap checking every passenger has finished their journey
    while (passIterator.hasNext()) {
      //get current element of hashmap
      Map.Entry mapElement = (Map.Entry)passIterator.next();
      //do check
      if(PassengersState.get(mapElement.getKey()).get(2) != null){
        throw new UnsupportedOperationException("passenger not at final station, actually at: " + PassengersState.get(mapElement.getKey()).get(1));
      }
    }
  }

  // reset to an empty simulation
  public void reset() {
    //remove all train lines in lines hahsmap
    lines.clear();
    //remove all passenger journeys in journeys hahsmap
    journeys.clear();
    //remove all trains in sim
    TrainsState.clear();
    //remove all Stations in sim
    StationsState.clear();
    //remove all passenger in sim
    PassengersState.clear();

    //print to test correctness
//      System.out.println("Lines: " + lines);
//      System.out.println("Journeys: " + journeys);
//      System.out.println("TrainsState: " + TrainsState);
//      System.out.println("StationsState: " + StationsState);
//      System.out.println("PassengersState: " + PassengersState);

  }

  // adds simulation configuration from a file
  public void loadConfig(String filename) {

    try {
      //create a new (gson)file
      File file = new File(filename);
      //create a fileReader for the (gson)file
      FileReader fileReader = new FileReader(file);
      //create a gson object
      Gson gson = new Gson();
      //read the (gson)file turning it into an instance of the MBTAGson class
      MBTAGson mbtaGson = gson.fromJson(fileReader, MBTAGson.class);

      // Get an iterator for lines
      Iterator linesIterator = mbtaGson.lines.entrySet().iterator();
      // Iterating through Hashmap copying values to lines hashmap
      while (linesIterator.hasNext()) {
        //get current element of hashmap
        Map.Entry mapElement = (Map.Entry)linesIterator.next();
        //add that element to lines hashamp
        addLine((String)mapElement.getKey(), (List<String>)mapElement.getValue());
      }

      // Get an iterator for trips
      Iterator tripsIterator = mbtaGson.trips.entrySet().iterator();
      // Iterating through Hashmap copying values to journeys hashmap
      while (tripsIterator.hasNext()) {
        //get current element of hashmap
        Map.Entry mapElement = (Map.Entry)tripsIterator.next();
        //add that element to journeys hashmap
        addJourney((String)mapElement.getKey(), (List<String>)mapElement.getValue());
      }

      //print to test correctness
//      System.out.println("Lines: " + lines);
//      System.out.println("Journeys: " + journeys);
//      System.out.println("TrainsState: " + TrainsState);
//      System.out.println("StationsState: " + StationsState);
//      System.out.println("PassengersState: " + PassengersState);

    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

  }

}
