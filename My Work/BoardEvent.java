import java.util.*;

public class BoardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public BoardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof BoardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " boards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  public void replayAndCheck(MBTA mbta) {

    //check if train is in sim
    if(mbta.TrainsState.containsKey(t) == false){
      throw new UnsupportedOperationException("train is not in sim");
    }
    //check if station is in sim
    if(mbta.StationsState.containsKey(s) == false){
      throw new UnsupportedOperationException("station is not in sim");
    }
    //check if passenger is in sim
    if(mbta.PassengersState.containsKey(p) == false){
      throw new UnsupportedOperationException("passenger is not in sim");
    }

    //check if passenger is at station s
    if(mbta.PassengersState.get(p).get(1).equals(s) == false){
      throw new UnsupportedOperationException("passenger is not at station s");
    }
    //check if train is at station s
    if(mbta.TrainsState.get(t).get(0).equals(s) == false){
      throw new UnsupportedOperationException("train is not at station s");
    }

    //check if train has passengers next stop in its line
    boolean nextstoppresent = false;
    Station nextstationholder = (Station) mbta.PassengersState.get(p).get(2);
    for(int i = 0; i < mbta.lines.get(t).size();i++){
      //if we have found the passengers next station in the trains line of stations, set true as then train has stop
      if(mbta.lines.get(t).get(i).equals(nextstationholder)){
        nextstoppresent = true;
      }
    }
    if(nextstoppresent == false){
      throw new UnsupportedOperationException("passenger next stop is not on train");
    }


    //if all checks pass update mbta to reflect changes

    //update Value[0] = current train/put passenger on train
    mbta.PassengersState.get(p).set(0, t);


  }
}
