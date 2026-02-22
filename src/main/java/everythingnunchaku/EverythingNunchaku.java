package everythingnunchaku;

import everythingnunchaku.handlers.ForgeConfigProvider;
import everythingnunchaku.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = EverythingNunchaku.MODID,
        version = EverythingNunchaku.VERSION,
        name = EverythingNunchaku.NAME,
        dependencies =
                "required-after:fermiumbooter;" +
                "required-after:bettercombatmod;" +
                "required-after:mujmajnkraftsbettersurvival;",
        clientSideOnly = true
)
public class EverythingNunchaku {
    public static final String MODID = "everythingnunchaku";
    public static final String VERSION = "1.0.2";
    public static final String NAME = "Everything Nunchaku";
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean completedLoading = false;
	
    @SidedProxy(clientSide = "everythingnunchaku.proxy.ClientProxy", serverSide = "everythingnunchaku.proxy.CommonProxy")
    public static CommonProxy PROXY;
	
	@Instance(MODID)
	public static EverythingNunchaku instance;
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EverythingNunchaku.PROXY.preInit();

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        EverythingNunchaku.PROXY.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ForgeConfigProvider.init();
        completedLoading = true;
    }
}