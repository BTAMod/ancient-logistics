package youngsditch.ancientlogistics.gears;

public class GearInfo<T extends GearBlock> {
  int[] coordinates;
  int distance = -1;
  T gear;

  public GearInfo(int[] coordinates, T gear) {
    this.coordinates = coordinates;
    this.gear = gear;
  }

  public GearInfo(int[] coordinates, int distance, T gear) {
    this.coordinates = coordinates;
    this.distance = distance;
    this.gear = gear;
  }

  public int[] getCoordinates() {
    return coordinates;
  }
  
  public int getDistance() {
    return distance;
  }

  public T getGear() {
    return gear;
  }
}