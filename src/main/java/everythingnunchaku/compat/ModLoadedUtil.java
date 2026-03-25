package everythingnunchaku.compat;

import net.minecraftforge.fml.ModList;

public abstract class ModLoadedUtil {

    public static final String OFFHAND_COMBAT_MODID = "offhandcombat";

    private static Boolean offhandCombatLoaded = null;

    public static boolean getOffhandCombatLoaded() {
        if(offhandCombatLoaded == null) offhandCombatLoaded = ModList.get().isLoaded(OFFHAND_COMBAT_MODID);
        return offhandCombatLoaded;
    }
}
