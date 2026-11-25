package everythingnunchaku.handlers;

import everythingnunchaku.EverythingNunchaku;
import everythingnunchaku.compat.ModLoadedUtil;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = EverythingNunchaku.MODID)
public class ForgeConfigHandler {

	@Config.Comment("Client-Side Options")
	@Config.Name("Client Options")
	public static final ClientConfig client = new ClientConfig();

	@Config.Name("Mixin Toggles")
	public static final MixinToggleConfig mixin = new MixinToggleConfig();

	@MixinConfig(name = EverythingNunchaku.MODID)
	public static class MixinToggleConfig {

		@Config.Comment({
				"Similar to RLCombat Offhand, don't trigger an attack cooldown reset when clicking a block.",
				"Instant break and block harvesting items will still break blocks and reset attack cooldowns."
		})
		@Config.Name("Cancel Whiffed Block Clicks (Vanilla)")
		@Config.RequiresMcRestart
		@MixinConfig.MixinToggle(defaultValue = true, earlyMixin = "mixins.everythingnunchaku.vanilla.json")
		public boolean vanillaNoBlockClick = true;

		@Config.Comment("Toggles Compatibility for proper attack packets. For RLCombat versions 2.2.0 and beyond.")
		@Config.Name("RLCombat Modern (RLCombat)")
		@Config.RequiresMcRestart
		@MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.everythingnunchaku.rlcombat.json")
		@MixinConfig.CompatHandling(
				modid = ModLoadedUtil.RLCOMBAT_MODID,
				targetVersionRange = ModLoadedUtil.RLCOMBAT_VERSION,
				desired = true,
				warnIngame = false,
				reason = "Requires version " + ModLoadedUtil.RLCOMBAT_VERSION + " to properly function")
		public boolean rlCombatModern = true;

		@Config.Comment("Toggles Compatibility for proper attack packets. For RLCombat versions before 2.2.0")
		@Config.Name("RLCombat Old (RLCombat)")
		@Config.RequiresMcRestart
		@MixinConfig.MixinToggle(defaultValue = true, lateMixin = "mixins.everythingnunchaku.rlcombat.old.json")
		@MixinConfig.CompatHandling(
				modid = ModLoadedUtil.RLCOMBAT_MODID,
				targetVersionRange = ModLoadedUtil.OLD_RLCOMBAT_VERSION,
				desired = true,
				warnIngame = false,
				reason = "Requires versions before 2.2.0 to properly function")
		public boolean rlCombatOld = true;
	}

	public static class ClientConfig {

		@Config.Comment("Ignores whitelists and allows everything")
		@Config.Name("Allow For Everything")
		public boolean allowEverything = false;

		@Config.Comment("Automatically attack while using offhand weapon")
		@Config.Name("RLCombat Offhand")
		public boolean rlCombatOffhand = true;

		@Config.Comment("Use RLCombat's offhand blacklist for targeting entities, forcing manual clicks to attack")
		@Config.Name("RLCombat Entity Blacklist")
		public boolean rlCombatEntityBlacklist = true;

		@Config.Comment("Allows offhand nunchakus to be usable without a spinning mainhand one")
		@Config.Name("RLCombat Offhand Nunchaku")
		public boolean rlCombatOffhandNunchaku = true;

		@Config.Comment("Item classes whitelisted for handling")
		@Config.Name("Item Class Whitelist")
		public String[] itemClassWhitelist = new String[]{
				"net.minecraft.item.ItemSword",
				"net.minecraft.item.ItemAxe",
				"net.minecraft.item.ItemSpade",
				"net.minecraft.item.ItemPickaxe",
				"net.minecraft.item.ItemHoe",
				"com.mujmajnkraft.bettersurvival.items.ItemBattleAxe",
				"com.mujmajnkraft.bettersurvival.items.ItemDagger",
				"com.mujmajnkraft.bettersurvival.items.ItemHammer",
				"com.mujmajnkraft.bettersurvival.items.ItemNunchaku",
				"dev.satyrn.wolfarmor.item.ItemWolfArmor",
				"com.lycanitesmobs.core.item.equipment.ItemEquipment"
		};

		@Config.Comment("Item ids in the format \"domain:itemname\" whitelisted for handling")
		@Config.Name("Item ID Whitelist")
		public String[] itemIDWhitelist = new String[]{

		};

		@Config.Comment("Item ids in the format \"domain:itemname\" blacklisted from handling")
		@Config.Name("Item ID Blacklist")
		public String[] itemIDBlacklist = new String[]{

		};

		@Config.Comment("Blacklisted entity classes from handling, you will not be able to continuously attack any entity that extends these classes")
		@Config.Name("Entity Class Blacklist")
		public String[] entityBlacklist = new String[] {
				"net.minecraft.entity.passive.EntityHorse",
				"net.minecraft.entity.item.EntityArmorStand",
				"net.minecraft.entity.passive.EntityVillager",
				"net.minecraft.entity.item.EntityItemFrame"
		};
	}

	@Mod.EventBusSubscriber(modid = EverythingNunchaku.MODID)
	private static class EventHandler{

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(EverythingNunchaku.MODID)) {
				ConfigManager.sync(EverythingNunchaku.MODID, Config.Type.INSTANCE);

				ForgeConfigProvider.init();
			}
		}
	}
}