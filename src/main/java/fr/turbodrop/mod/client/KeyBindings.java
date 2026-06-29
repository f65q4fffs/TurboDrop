package fr.turbodrop.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "turbodrop", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    public static final String KEY_CATEGORY_TURBODROP = "key.categories.turbodrop";

    public static final Lazy<KeyMapping> EMPTY_STORAGE_KEY = Lazy.of(() -> {
        int defaultKey = GLFW.GLFW_KEY_Q;
        try {
            // Tenter de recuperer la touche de drop configuree par l'utilisateur dans Minecraft
            var options = net.minecraft.client.Minecraft.getInstance().options;
            if (options != null && options.keyDrop != null && options.keyDrop.getKey() != null) {
                defaultKey = options.keyDrop.getKey().getValue();
            }
        } catch (Throwable ignored) {}

        return new KeyMapping(
                "key.turbodrop.empty_storage",
                KeyConflictContext.UNIVERSAL,
                KeyModifier.ALT,
                InputConstants.Type.KEYSYM,
                defaultKey,
                KEY_CATEGORY_TURBODROP
        );
    });

    public static final Lazy<KeyMapping> DROP_SLOT_KEY = Lazy.of(() -> {
        int defaultKey = GLFW.GLFW_KEY_Q;
        try {
            var options = net.minecraft.client.Minecraft.getInstance().options;
            if (options != null && options.keyDrop != null && options.keyDrop.getKey() != null) {
                defaultKey = options.keyDrop.getKey().getValue();
            }
        } catch (Throwable ignored) {}

        return new KeyMapping(
                "key.turbodrop.drop_slot",
                KeyConflictContext.GUI,
                KeyModifier.CONTROL,
                InputConstants.Type.KEYSYM,
                defaultKey,
                KEY_CATEGORY_TURBODROP
        );
    });


    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
        event.register(EMPTY_STORAGE_KEY.get());
        event.register(DROP_SLOT_KEY.get());
    }
}

