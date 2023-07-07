package youngsditch.ancientlogistics;

import turniplabs.halplibe.helper.*;
import net.minecraft.src.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
public class AncientLogistics implements ModInitializer {
    public static final String MOD_ID = "ancientlogistics";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Item sethGravy = ItemHelper.createItem(MOD_ID, new Item(140), "sethgravy", "seth_gravy.png");

    @Override
    public void onInitialize() {
        LOGGER.info("AncientLogistics initialized.");
    }
}