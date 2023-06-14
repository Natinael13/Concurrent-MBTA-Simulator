import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class PassengerT implements Runnable{

    public MBTA mbta;
    public Log log;

    public Passenger curpassenger;
    PassengerT(MBTA mbta, Log log, Passenger curpassenger){this.mbta = mbta; this.log = log; this.curpassenger = curpassenger;}

    public void run() {

        //loop until passenger has reached final stop of journey
        while(mbta.PassengersState.get(curpassenger).get(2) != null){

            //case 1: passenger is waiting at station/not on a train, board
            if(mbta.PassengersState.get(curpassenger).get(0) == null){
                //get lock
                Station currstation = (Station) mbta.PassengersState.get(curpassenger).get(1);
                currstation.CurrStationLoc.lock();
                //await until train has arrived at station
                while(mbta.StationsState.get(currstation) == null){
                    try {
                        currstation.CurrStationCondtion.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //signal all
                currstation.CurrStationCondtion.signalAll();

                //when train arrives at station check if it has passengers next stop, if so board
                if(checktrainstops(mbta.StationsState.get(currstation))){
                    //board train
                    passengerboard(mbta.StationsState.get(currstation), currstation);
                }

                //unlock station
                currstation.CurrStationLoc.unlock();

            }

            //Case 2: passenger is in a train/not waiting at a station, deboard
            else if(mbta.PassengersState.get(curpassenger).get(0) != null){
                //get next stop lock
                Station nextstation = (Station) mbta.PassengersState.get(curpassenger).get(2);
                nextstation.CurrStationLoc.lock();
                //await until arrived at deboarding station
                Train trainholder = (Train) mbta.PassengersState.get(curpassenger).get(0);
                while(mbta.TrainsState.get(trainholder).get(0).equals(mbta.PassengersState.get(curpassenger).get(2)) == false){
                    try {
                        nextstation.CurrStationCondtion.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //signal all
                nextstation.CurrStationCondtion.signalAll();

                //deboard
                passengerdeboard((Station) mbta.PassengersState.get(curpassenger).get(2));

                //unlock station
                nextstation.CurrStationLoc.unlock();
            }

        }

    }

    public boolean checktrainstops(Train t){
        //check if train has passengers next stop in its line
        boolean nextstoppresent = false;
        Station nextstationholder = (Station) mbta.PassengersState.get(curpassenger).get(2);
        for(int i = 0; i < mbta.lines.get(t).size();i++){
            if(mbta.lines.get(t).get(i).equals(nextstationholder)){
                nextstoppresent = true;
            }
        }
        //return result
        return nextstoppresent;
    }

    public void passengerboard(Train t, Station s) {

        //update Value[0] = current train/put passenger on train
        mbta.PassengersState.get(curpassenger).set(0, t);

        //log boarding
        log.passenger_boards(curpassenger, t, s);

    }

    public void passengerdeboard(Station s) {

        //put train deboarding from in holder variable, so we can use it to log the event later
        Train t = (Train) mbta.PassengersState.get(curpassenger).get(0);

        //update Value[0] = current train/take passenger off train
        mbta.PassengersState.get(curpassenger).set(0, null);
        // Value[1] = current station
        mbta.PassengersState.get(curpassenger).set(1, s);

        //update Value[2] = next station to deboard for passenger
        //current station to deboard
        Station curdeboard = (Station) mbta.PassengersState.get(curpassenger).get(2);
        //loop through passengers journey of stations
        for(int i = 0; i < mbta.journeys.get(curpassenger).size();i++){
            //check if we are at the index of current station to deboard
            if(mbta.journeys.get(curpassenger).get(i).equals(curdeboard)){
                //if we are at last stop set next station to deboard at as null
                if((i+1) == (mbta.journeys.get(curpassenger).size())){
                    mbta.PassengersState.get(curpassenger).set(2, null);
                }
                //if so update next station to deboard to be next one on jounrey
                else{
                    mbta.PassengersState.get(curpassenger).set(2, mbta.journeys.get(curpassenger).get(i+1));
                }
            }
        }

        //log deboarding
        log.passenger_deboards(curpassenger, t, s);

    }
}
