package youngsditch.ancientlogistics;

import turniplabs.halplibe.helper.*;
import net.minecraft.src.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

import youngsditch.ancientlogistics.utility.Config;
import youngsditch.ancientlogistics.gears.*;
 
public class AncientLogistics implements ModInitializer {
    public static final String MOD_ID = "ancientlogistics";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Config config = new Config(MOD_ID, new Properties() {{
        put("gearID", "140");
        put("gearboxID", "900");
        put("geartrommelID", "901");
        put("gearchestID", "902");
        put("reinforcedgearboxID", "903");
        // More keys and values...
    }});

    public static final Item gear = ItemHelper.createItem(MOD_ID, new Item(config.getInt("gearID")), "gear", "gear.png");
    public static final Block gearBlock = BlockHelper.createBlock(MOD_ID, new GearBox(config.getInt("gearboxID")), "gearbox", "gear_top.png", "gear_block.png", Block.soundMetalFootstep, 0.1f, 0.1f, 0.0f);
    public static final Block reinforcedGearBlock = BlockHelper.createBlock(MOD_ID, new ReinforcedGearBox(config.getInt("reinforcedgearboxID")), "reinforcedgearbox", "gear_top.png", "reinf_gear_block.png", Block.soundMetalFootstep, 0.1f, 0.1f, 0.0f);
    public static final Block gearTrommelBlock = BlockHelper.createBlock(MOD_ID, new GearTrommel(config.getInt("geartrommelID")), "geartrommel", "gear_top.png", "gear_trommel.png", Block.soundMetalFootstep, 0.1f, 0.1f, 0.0f);
    public static final Block gearChestBlock = BlockHelper.createBlock(MOD_ID, new GearChestSorter(config.getInt("gearchestID")), "gearchest", "gear_top.png", "gear_chest.png", Block.soundWoodFootstep, 0.1f, 0.1f, 0.0f);
    
    @Override
    public void onInitialize() {
        // crafting recipe for gearbox
        RecipeHelper.Crafting.createRecipe(gearBlock, 1, new Object[]{
            "CGC",
            "GIG",
            "CGC",
            'G', gear,
            'C', Item.clay,
            'I', Item.ingotIron
        });

        // crafting recipe for reinforced gearbox
        RecipeHelper.Crafting.createRecipe(reinforcedGearBlock, 1, new Object[]{
            "CGC",
            "GIG",
            "CGC",
            'G', gearBlock,
            'C', Item.nethercoal,
            'I', Item.ingotIron
        });

        // crafting recipe for gear trommel
        RecipeHelper.Crafting.createRecipe(gearTrommelBlock, 1, new Object[]{
            "CGC",
            "GTG",
            "CGC",
            'G', gearBlock,
            'C', Item.clay,
            'T', Block.trommelIdle
        });

        // crafting recipe for gear chest
        RecipeHelper.Crafting.createRecipe(gearChestBlock, 1, new Object[]{
            "CGC",
            "GXG",
            "CGC",
            'G', gearBlock,
            'C', Item.clay,
            'X', Block.chestPlanksOak
        });

        // crafting recipe for gear item
        RecipeHelper.Crafting.createRecipe(gear, 1, new Object[]{
            " S ",
            "SPS",
            " S ",
            'S', Item.stick,
            'P', Block.planksOak
        });

        LOGGER.info("AncientLogistics initialized.");
    }
}