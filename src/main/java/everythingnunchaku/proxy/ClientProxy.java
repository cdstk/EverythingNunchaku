package everythingnunchaku.proxy;

import everythingnunchaku.compat.ModLoadedUtil;
import everythingnunchaku.compat.client.RLCombatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {

    }

    @Override
    public void init(){
        if(ModLoadedUtil.versionInRange(ModLoadedUtil.rlCombat, ModLoadedUtil.RLCOMBAT_VERSION)) MinecraftForge.EVENT_BUS.register(RLCombatHandler.class);
    }

    @Override
    public EntityPlayer getSinglePlayerEntity(){
        return Minecraft.getMinecraft().player;
    }

    @Override
    public boolean iskeyBindAttackKeyDown(){
        return Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown();
    }

    @Override
    public boolean iskeyBindUseItemKeyDown(){
        return Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown();
    }
}