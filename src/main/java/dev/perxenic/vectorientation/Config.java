package dev.perxenic.vectorientation;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue SQUETCH = BUILDER.comment("Whether Squash & Stretch should be enabled").define("squetch", true);

    private static final ModConfigSpec.DoubleValue MIN_WARP = BUILDER.comment("Defines the vertical squish at 0 velocity").defineInRange("minWarp", 0.75, 0, Double.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue WARP_FACTOR = BUILDER.comment("Defines the amount squish increases with velocity").defineInRange("warpFactor", 1.0, 0, Double.MAX_VALUE);

    //TODO: Rename config entry to blacklist on major version update
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST = BUILDER.comment("A list of blocks that should not be squished.").defineListAllowEmpty("blocks", List.of("minecraft:anvil", "minecraft:chipped_anvil", "minecraft:damaged_anvil", "minecraft:pointed_dripstone"), () -> "", Config::validateBlockName);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean squetch;
    public static double minWarp;
    public static double warpFactor;
    public static Set<Block> blacklist;

    private static boolean validateBlockName(final Object obj) {
        try {
            return obj instanceof String blockName && BuiltInRegistries.BLOCK.containsKey(ResourceLocation.parse(blockName));
        }
        catch(ResourceLocationException exception) {
            return false;
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        squetch = SQUETCH.get();
        minWarp = MIN_WARP.get();
        warpFactor = WARP_FACTOR.get();

        // convert the list of strings into a set of items
        blacklist = BLACKLIST.get().stream().map(blockName -> BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockName))).collect(Collectors.toSet());
    }
}
