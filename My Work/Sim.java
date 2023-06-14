import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Sim {

  public static void run_sim(MBTA mbta, Log log) {
    //create arraylists to hold all passenger threads and all train threads separately
    ArrayList<Thread> PassengerThreads = new ArrayList<>();
    ArrayList<Thread> TrainThreads = new ArrayList<>();

    //loop through all passengers in mbta and create threads for all of them and store each thread in arraylist
    // Get an iterator for PassengersState
    Iterator passIterator = mbta.PassengersState.entrySet().iterator();
    // Iterating through Hashmap creating thread for every passenger
    while (passIterator.hasNext()) {
      //get current element of hashmap
      Map.Entry mapElement = (Map.Entry)passIterator.next();
      //create thread
      Thread p = new Thread(new PassengerT(mbta, log, (Passenger)mapElement.getKey()));
      //start thread
      p.start();
      //add thread to arraylist
      PassengerThreads.add(p);
    }

    //loop through all trains in mbta and create threads for all of them and store each thread in arraylist
    // Get an iterator for TrainsState
    Iterator trainIterator = mbta.TrainsState.entrySet().iterator();
    // Iterating through Hashmap creating thread for every train
    while (trainIterator.hasNext()) {
      //get current element of hashmap
      Map.Entry mapElement = (Map.Entry)trainIterator.next();
      //create thread
      Thread t = new Thread(new TrainT(mbta, log, (Train)mapElement.getKey()));
      //start thread
      t.start();
      //add thread to arraylist
      TrainThreads.add(t);
    }

    //loop through passenger threads and join all of them
    for(int i = 0; i < PassengerThreads.size(); i++){
      try {
        PassengerThreads.get(i).join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    //loop through trains and interrupt all of them
    for(int i = 0; i < TrainThreads.size(); i++){
      TrainThreads.get(i).interrupt();
    }

    return;
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("usage: ./sim <config file>");
      System.exit(1);
    }

    MBTA mbta = new MBTA();
    mbta.loadConfig(args[0]);

    Log log = new Log();

    run_sim(mbta, log);

    String s = new LogJson(log).toJson();
    PrintWriter out = new PrintWriter("log.json");
    out.print(s);
    out.close();

    mbta.reset();
    mbta.loadConfig(args[0]);
    Verify.verify(mbta, log);
  }
}
