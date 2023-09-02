package youngsditch.ancientlogistics.gears;

public class ReinforcedGearBox extends GearBox {

  public ReinforcedGearBox(String key, int id) {
    super(key, id);
    this.canRunMultiple = true;
  }
}
