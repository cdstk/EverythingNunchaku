package everythingnunchaku.proxy;

import everythingnunchaku.compat.ModLoadedUtil;
import everythingnunchaku.compat.client.RLCombatHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {

    }

    @Override
    public void init(){
        if(ModLoadedUtil.getRlCombatLoaded()) MinecraftForge.EVENT_BUS.register(RLCombatHandler.class);
    }
}