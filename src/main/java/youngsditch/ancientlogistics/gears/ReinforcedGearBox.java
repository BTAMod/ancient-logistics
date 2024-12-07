package youngsditch.ancientlogistics.gears;

public class ReinforcedGearBox extends GearGenerator {
  public ReinforcedGearBox(String key, int id) {
    super(key, id);
    this.canRunMultiple = true;
  }
}
