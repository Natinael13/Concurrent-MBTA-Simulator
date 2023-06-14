public class Station extends Entity {
  private Station(String name) { super(name); }

  public static Station make(String name) {
    // Change this method!
    return new Station(name);
  }
}
