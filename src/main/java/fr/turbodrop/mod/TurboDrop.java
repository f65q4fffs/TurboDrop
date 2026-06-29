package fr.turbodrop.mod;

import fr.turbodrop.mod.config.TurboDropConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(TurboDrop.MODID)
public class TurboDrop {
    public static final String MODID = "turbodrop";

    public TurboDrop(IEventBus modBus, ModContainer modContainer) {
        // Enregistrer la configuration avec le ModContainer
        modContainer.registerConfig(ModConfig.Type.COMMON, TurboDropConfig.SPEC);
    }
}
