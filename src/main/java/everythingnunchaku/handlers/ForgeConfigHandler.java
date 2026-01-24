package everythingnunchaku.handlers;

import everythingnunchaku.EverythingNunchaku;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = EverythingNunchaku.MODID)
public class ForgeConfigHandler {
	
//	@Config.Comment("Server-Side Options")
//	@Config.Name("Server Options")
//	public static final ServerConfig server = new ServerConfig();

	@Config.Comment("Client-Side Options")
	@Config.Name("Client Options")
	public static final ClientConfig client = new ClientConfig();

//	@MixinConfig(name = EverythingNunchaku.MODID) //Needed on config classes that contain MixinToggles for those mixins to be added
//	public static class ServerConfig {
//
//	}

	public static class ClientConfig {

		@Config.Comment("Ignores whitelists and allows everything")
		@Config.Name("Allow For Everything")
		public boolean allowEverything = false;

		@Config.Comment("Automatically attack while using offhand weapon")
		@Config.Name("RLCombat Offhand")
		public boolean rlCombatOffhand = true;

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