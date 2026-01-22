package everythingnunchaku.compat;

import net.minecraftforge.fml.common.Loader;

public abstract class ModLoadedUtil {

    public static final String RLCOMBAT_MODID = "bettercombatmod";

    private static Boolean rlCombatLoaded = null;

    public static boolean getRlCombatLoaded() {
        if(rlCombatLoaded == null) rlCombatLoaded = Loader.isModLoaded(RLCOMBAT_MODID);
        return rlCombatLoaded;
    }
}
