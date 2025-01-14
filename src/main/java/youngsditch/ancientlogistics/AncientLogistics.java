package youngsditch.ancientlogistics;

import youngsditch.ancientlogistics.gears.*;

import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Item;

import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;
import turniplabs.halplibe.util.ConfigHandler;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.ItemBuilder;

import net.fabricmc.api.ModInitializer;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AncientLogistics implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "ancientlogistics";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConfigHandler config = new ConfigHandler(MOD_ID, new Properties() {{
        put("gearID", "17000");
        put("gearboxID", "905");
        put("geartrommelID", "906");
        put("gearchestID", "907");
        put("reinforcedgearboxID", "908");
        put("gearBoxFuelId", "16480");
        // More keys and values...
    }});

    public static Item gear;
    public static Block gearBlock;
    public static Block reinforcedGearBlock;
    public static Block gearTrommelBlock;
    public static Block gearChestBlock;
    public static int gearBoxFuelId = config.getInt("gearBoxFuelId");

    @Override
    public void beforeGameStart() {
        gear = new ItemBuilder(MOD_ID)
            .setIcon("ancientlogistics:item/gear")
            .build(new Item("gear", config.getInt("gearID")));

        gearBlock = new BlockBuilder(MOD_ID)
            .setTextures("ancientlogistics:block/gear_block")
            //.setTopTexture("ancientlogistics:block/gear_top")
            .setBlockSound(BlockSounds.METAL)
            .build(new GearBox("gearbox", config.getInt("gearboxID")));

        reinforcedGearBlock = new BlockBuilder(MOD_ID)
            .setTextures("ancientlogistics:block/reinf_gear_block")
            //.setTopTexture("ancientlogistics:block/gear_top")
            .setBlockSound(BlockSounds.WOOD)
            .build(new ReinforcedGearBox("reinforcedgearbox", config.getInt("reinforcedgearboxID")));

        gearTrommelBlock = new BlockBuilder(MOD_ID)
            .setTextures("ancientlogistics:block/gear_trommel")
            //.setTopTexture("ancientlogistics:block/gear_top")
            .setBlockSound(BlockSounds.METAL)
            .build(new GearTrommel("geartrommel", config.getInt("geartrommelID")));

        gearChestBlock = new BlockBuilder(MOD_ID)
            .setTextures("ancientlogistics:block/gear_chest")
            //.setTopTexture("ancientlogistics:block/gear_top")
            .setBlockSound(BlockSounds.WOOD)
            .build(new GearChestSorter("gearchest", config.getInt("gearchestID")));

    }
    @Override
    public void afterGameStart() {}
    @Override
    public void onInitialize() {}


    @Override
    public void initNamespaces() {
        RecipeBuilder.initNameSpace(MOD_ID);
    }

    @Override
    public void onRecipesReady() {
        // crafting recipe for gearbox
        RecipeBuilder.Shaped(MOD_ID, "CGC", "GIG", "CGC")
            .addInput('G', gear)
            .addInput('C', Item.clay)
            .addInput('I', Item.ingotIron)
            .create("al/gearblock", new ItemStack(gearBlock));

        // crafting recipe for reinforced gearbox
        RecipeBuilder.Shaped(MOD_ID, "CGC", "GIG", "CGC")
            .addInput('G', gearBlock)
            .addInput('C', Item.nethercoal)
            .addInput('I', Item.ingotIron)
            .create("al/regearblock", new ItemStack(reinforcedGearBlock));

        // crafting recipe for gear trommel
        RecipeBuilder.Shaped(MOD_ID, "CGC", "GTG", "CGC")
            .addInput('G', gearBlock)
            .addInput('C', Item.clay)
            .addInput('T', Block.trommelIdle)
            .create("al/geartrommel", new ItemStack(gearTrommelBlock));

        // crafting recipe for gear chest
        RecipeBuilder.Shaped(MOD_ID, "CGC", "GXG", "CGC")
            .addInput('G', gearBlock)
            .addInput('C', Item.clay)
            .addInput('X', Block.chestPlanksOak)
            .create("al/geartrommel", new ItemStack(gearChestBlock));

        // crafting recipe for gear item
        RecipeBuilder.Shaped(MOD_ID, " S ", "SPS", " S ")
            .addInput('S', Item.stick)
            .addInput('P', Block.planksOak)
            .create("al/gear", new ItemStack(gear));

        LOGGER.info("AncientLogistics initialized.");
    }
}
