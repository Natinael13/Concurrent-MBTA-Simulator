public class Passenger extends Entity {
  private Passenger(String name) { super(name); }

  public static Passenger make(String name) {
    // Change this method!
    return new Passenger(name);
  }
}
