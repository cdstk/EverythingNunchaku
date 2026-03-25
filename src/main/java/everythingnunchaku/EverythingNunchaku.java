package everythingnunchaku;

import everythingnunchaku.compat.ModLoadedUtil;
import everythingnunchaku.client.handlers.MainHandHandler;
import everythingnunchaku.compat.client.OffhandCombatHandler;
import everythingnunchaku.handlers.EverythingNunchakuConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        value = EverythingNunchaku.MODID
)
public class EverythingNunchaku {
    public static final String MODID = "everythingnunchaku";
//    public static final String VERSION = "1.0.0";
//    public static final String NAME = "Everything Nunchaku";
    public static final Logger LOGGER = LogManager.getLogger();

    public EverythingNunchaku() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, EverythingNunchakuConfig.Holder.CLIENT_SPEC);
//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EverythingNunchakuConfig.Holder.COMMON_SPEC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfigEvent);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
        MinecraftForge.EVENT_BUS.register(MainHandHandler.class);
        if(ModLoadedUtil.getOffhandCombatLoaded()) MinecraftForge.EVENT_BUS.register(OffhandCombatHandler.class);
    }

    public void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == EverythingNunchakuConfig.Holder.CLIENT_SPEC) {
            EverythingNunchakuConfig.Setup.client(config);
            LOGGER.debug("Baked client config");
        } else if (config.getSpec() == EverythingNunchakuConfig.Holder.COMMON_SPEC) {
            EverythingNunchakuConfig.Setup.server(config);
            LOGGER.debug("Baked server config");
        }
    }

    public static PlayerEntity getSinglePlayerEntity(){
        return Minecraft.getInstance().player;
    }

    public static boolean iskeyBindAttackKeyDown(){
        return Minecraft.getInstance().options.keyAttack.isDown();
    }

    public static boolean iskeyBindUseItemKeyDown(){
        return Minecraft.getInstance().options.keyUse.isDown();
    }
}