public class Train extends Entity {
  private Train(String name) { super(name); }

  public static Train make(String name) {
    // Change this method!
    return new Train(name);
  }
}
