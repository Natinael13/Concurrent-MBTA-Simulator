import java.util.*;

public class DeboardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public DeboardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof DeboardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " deboards " + t + " at " + s;
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

    //check if passenger is on train
    if(mbta.PassengersState.get(p).get(0).equals(t) == false){
      throw new UnsupportedOperationException("passenger is not on train t");
    }
    //check if train is at station s
    if(mbta.TrainsState.get(t).get(0).equals(s) == false){
      throw new UnsupportedOperationException("train(and passenger) is not at station s");
    }

    //check if passenger next deboard station on journey is s
    if(mbta.PassengersState.get(p).get(2).equals(s) == false){
      throw new UnsupportedOperationException("Station is not next stop on passengers jounrey");
    }

    //if all checks pass update mbta to reflect changes

    //update Value[0] = current train/take passenger off train
    mbta.PassengersState.get(p).set(0, null);
    // Value[1] = current station
    mbta.PassengersState.get(p).set(1, s);

    //update Value[2] = next station to deboard for passenger
    //current station to deboard
    Station curdeboard = (Station) mbta.PassengersState.get(p).get(2);
    //loop through passengers journey of stations
    for(int i = 0; i < mbta.journeys.get(p).size();i++){
      //check if we are at the index of current station to deboard
      if(mbta.journeys.get(p).get(i).equals(curdeboard)){
        //if we are at last stop set next station to deboard at as null
        if((i+1) == (mbta.journeys.get(p).size())){
          mbta.PassengersState.get(p).set(2, null);
        }
        //if so update next station to deboard to be next one on jounrey
        else{
          mbta.PassengersState.get(p).set(2, mbta.journeys.get(p).get(i+1));
        }
      }
    }

  }
}
