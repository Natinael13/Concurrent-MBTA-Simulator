import java.util.*;
import java.util.Collections;

public class MoveEvent implements Event {
  public final Train t; public final Station s1, s2;
  public MoveEvent(Train t, Station s1, Station s2) {
    this.t = t; this.s1 = s1; this.s2 = s2;
  }
  public boolean equals(Object o) {
    if (o instanceof MoveEvent e) {
      return t.equals(e.t) && s1.equals(e.s1) && s2.equals(e.s2);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(t, s1, s2);
  }
  public String toString() {
    return "Train " + t + " moves from " + s1 + " to " + s2;
  }
  public List<String> toStringList() {
    return List.of(t.toString(), s1.toString(), s2.toString());
  }
  public void replayAndCheck(MBTA mbta) {

    //check if train is in sim
    if(mbta.TrainsState.containsKey(t) == false){
      throw new UnsupportedOperationException("train is not in sim");
    }
    //check if s1 is in sim
    if(mbta.StationsState.containsKey(s1) == false){
      throw new UnsupportedOperationException("s1 is not in sim");
    }
    //check if s2 is in sim
    if(mbta.StationsState.containsKey(s2) == false){
      throw new UnsupportedOperationException("s2 is not in sim");
    }

    //check if train is at station s1
    if(mbta.TrainsState.get(t).get(0).equals(s1) == false){
      throw new UnsupportedOperationException("train is not at station s1");
    }


    //check if train next station on line is s2
    if(mbta.TrainsState.get(t).get(1).equals(s2) == false){
      throw new UnsupportedOperationException("train next station is not s2");
    }

    //check if there is another train at s2
    if(mbta.StationsState.get(s2) != null){
      throw new UnsupportedOperationException("another train is at s2 already");
    }

    //if all checks pass update mbta to reflect changes

    // update train Value[0] = current station
    mbta.TrainsState.get(t).set(0,s2);

    //update train Value[1] = next station
    //loop through train line's ordered list of stations
    for(int i = 0; i < mbta.lines.get(t).size();i++){
      //check if we are at the index of s2
      if(mbta.lines.get(t).get(i).equals(s2)){
        //if we are at end of list, reverse list in order to move backwards
        if((i+1) == mbta.lines.get(t).size()){
          //reverse list
          Collections.reverse(mbta.lines.get(t));
          //update next station to be second station from reversed list, 1st would be current station now
          mbta.TrainsState.get(t).set(1,mbta.lines.get(t).get(1));
          break;
        }
        else {
          //update next station to be i+1 if still going forward
          mbta.TrainsState.get(t).set(1, mbta.lines.get(t).get(i + 1));
          break;
        }
      }
    }

    //update the next station to have train there
    mbta.StationsState.put(s2, t);

    //update last station to not have train there anymore
    mbta.StationsState.put(s1, null);

  }
}
