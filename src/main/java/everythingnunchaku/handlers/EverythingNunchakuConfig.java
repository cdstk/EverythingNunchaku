package everythingnunchaku.handlers;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EverythingNunchakuConfig {

    public static class Common {

        public Common(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class Client {

        public final ForgeConfigSpec.BooleanValue allowEverything;
        public final ForgeConfigSpec.BooleanValue offhandCombatMod;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> itemClassWhitelist;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> itemIDWhitelist;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> itemIDBlacklist;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> entityBlacklist;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client-Side Options")
                    .push("Client Options");
            allowEverything = builder
                    .comment("Ignores whitelists and allows everything")
                    .define("Allow For Everything", false);
            offhandCombatMod = builder
                    .comment("Automatically attack while using offhand weapon")
                    .define("Offhand Combat Mod", true);
            itemClassWhitelist = builder
                    .comment("Item classes whitelisted for handling")
                    .defineList(
                            "Item Class Whitelist",
                            Arrays.asList(
                                    "net.minecraft.item.SwordItem",
                                    "net.minecraft.item.AxeItem"
                            ),
                            String.class::isInstance
                    );
            itemIDWhitelist = builder
                    .comment("Item ids in the format \"domain:itemname\" whitelisted for handling")
                    .defineList(
                    "Item ID Whitelist",
                            Collections.emptyList(),
                            String.class::isInstance
            );
            itemIDBlacklist = builder
                    .comment("Item ids in the format \"domain:itemname\" whitelisted for handling")
                    .defineList(
                            "Item ID Blacklist",
                            Collections.emptyList(),
                            String.class::isInstance
                    );
            entityBlacklist = builder
                    .comment("Blacklisted entity classes from handling, you will not be able to continuously attack any entity that extends these classes")
                    .defineList(
                            "Entity Class Blacklist",
                            Arrays.asList(
                                    "net.minecraft.entity.passive.horse.HorseEntity",
                                    "net.minecraft.entity.item.ArmorStandEntity",
                                    "net.minecraft.entity.merchant.villager.AbstractVillagerEntity",
                                    "net.minecraft.entity.item.ItemFrameEntity"
                            ),
                            String.class::isInstance
                    );
            builder.pop();
        }
    }

    public static final class Holder {

        public static final EverythingNunchakuConfig.Common COMMON;
        public static final ForgeConfigSpec COMMON_SPEC;

        public static final EverythingNunchakuConfig.Client CLIENT;
        public static final ForgeConfigSpec CLIENT_SPEC;

        static {
            final Pair<EverythingNunchakuConfig.Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(EverythingNunchakuConfig.Common::new);
            COMMON_SPEC = specPair.getRight();
            COMMON = specPair.getLeft();
        }

        static {
            final Pair<EverythingNunchakuConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(EverythingNunchakuConfig.Client::new);
            CLIENT_SPEC = specPair.getRight();
            CLIENT = specPair.getLeft();
        }
    }

    public static final class Setup {

        public static void client(final ModConfig config) {
            Runtime.allowEverything = Holder.CLIENT.allowEverything.get();
            Runtime.offhandCombatMod = Holder.CLIENT.offhandCombatMod.get();
            ForgeConfigProvider.init();
        }

        public static void server(final ModConfig config) {

        }
    }

    public static final class Runtime {

        public static boolean allowEverything;
        public static boolean offhandCombatMod;
    }
}
