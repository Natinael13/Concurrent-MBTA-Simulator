import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class TrainT implements Runnable{

    public MBTA mbta;
    public Log log;

    public Train curtrain;
    TrainT(MBTA mbta, Log log, Train curtrain){this.mbta = mbta; this.log = log; this.curtrain = curtrain;}

    public void run() {
        //main while loop keeping train always going
        while(!Thread.interrupted()){
            //get current station lock
            mbta.TrainsState.get(curtrain).get(0).CurrStationLoc.lock();
            //get next station lock
            mbta.TrainsState.get(curtrain).get(1).CurrStationLoc.lock();

            //await until next station has no train in it
            while(mbta.StationsState.get(mbta.TrainsState.get(curtrain).get(1)) != null){
                try {
                    mbta.TrainsState.get(curtrain).get(1).CurrStationCondtion.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            //get next station condition and signal all
            mbta.TrainsState.get(curtrain).get(1).CurrStationCondtion.signalAll();
            //get current station condition and signal all
            mbta.TrainsState.get(curtrain).get(0).CurrStationCondtion.signalAll();

            //get ready to unlock locks before variables change
            Station currstation= mbta.TrainsState.get(curtrain).get(0);
            //get next station lock and unlock it
            Station nextstation= mbta.TrainsState.get(curtrain).get(1);


            //move this train to next station in mbta
            movetrain();

            //get current station lock and unlock it
            currstation.CurrStationLoc.unlock();
            //get next station lock and unlock it
            nextstation.CurrStationLoc.unlock();

            //make this thread sleep for .5Seconds
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }

        }

    }

    public void movetrain() {

        //get current station
        Station s1 = mbta.TrainsState.get(curtrain).get(0);

        //get next station
        Station s2 = mbta.TrainsState.get(curtrain).get(1);

        // update train Value[0] = current station
        mbta.TrainsState.get(curtrain).set(0,s2);

        //update train Value[1] = next station
        //loop through train line's ordered list of stations
        for(int i = 0; i < mbta.lines.get(curtrain).size();i++){
            //check if we are at the index of s2
            if(mbta.lines.get(curtrain).get(i).equals(s2)){
                //if we are at end of list, reverse list in order to move backwards
                if((i+1) == mbta.lines.get(curtrain).size()){
                    //reverse list
                    Collections.reverse(mbta.lines.get(curtrain));
                    //update next station to be second station from reversed list, 1st would be current station now
                    mbta.TrainsState.get(curtrain).set(1,mbta.lines.get(curtrain).get(1));
                    break;
                }
                else {
                    //update next station to be i+1 if still going forward
                    mbta.TrainsState.get(curtrain).set(1, mbta.lines.get(curtrain).get(i + 1));
                    break;
                }
            }
        }

        //update the next station to have train there
        mbta.StationsState.put(s2, curtrain);

        //update last station to not have train there anymore
        mbta.StationsState.put(s1, null);

        //log move
        log.train_moves(curtrain, s1, s2);

    }
}
