package everythingnunchaku.proxy;

import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {

    public void preInit() {

    }

    public void init(){

    }

    public EntityPlayer getSinglePlayerEntity(){
        return null;
    }

    public boolean iskeyBindAttackKeyDown(){
        return false;
    }

    public boolean iskeyBindUseItemKeyDown(){
        return false;
    }
}