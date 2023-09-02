package youngsditch.ancientlogistics;

import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.ItemHelper;
import turniplabs.halplibe.helper.RecipeHelper;
import net.minecraft.client.sound.block.BlockSounds;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;
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
        put("gearID", "17000");
        put("gearboxID", "905");
        put("geartrommelID", "906");
        put("gearchestID", "907");
        put("reinforcedgearboxID", "908");
        // More keys and values...
    }});

    public static final Item gear = ItemHelper.createItem(MOD_ID, new Item(config.getInt("gearID")), "gear", "gear.png");
    public static final Block gearBlock = new BlockBuilder(MOD_ID).setTextures("gear_block.png").setBlockSound(BlockSounds.METAL).build(new GearBox("gearbox", config.getInt("gearboxID")));
    public static final Block reinforcedGearBlock = new BlockBuilder(MOD_ID).setTextures("reinf_gear_block.png").setBlockSound(BlockSounds.WOOD).build(new ReinforcedGearBox("reinforcedgearbox", config.getInt("reinforcedgearboxID")));
    public static final Block gearTrommelBlock = new BlockBuilder(MOD_ID).setTextures("gear_trommel.png").setBlockSound(BlockSounds.METAL).build(new GearTrommel("geartrommel", config.getInt("geartrommelID")));
    public static final Block gearChestBlock = new BlockBuilder(MOD_ID).setTextures("gear_chest.png").setBlockSound(BlockSounds.WOOD).build(new GearChestSorter("gearchest", config.getInt("gearchestID")));
    
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