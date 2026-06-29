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
        int defaultKey = parseVanillaDropKey();
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
        int defaultKey = parseVanillaDropKey();
        return new KeyMapping(
                "key.turbodrop.drop_slot",
                KeyConflictContext.GUI,
                KeyModifier.CONTROL,
                InputConstants.Type.KEYSYM,
                defaultKey,
                KEY_CATEGORY_TURBODROP
        );
    });

    private static int parseVanillaDropKey() {
        java.io.File optionsFile = new java.io.File("options.txt");
        if (optionsFile.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(optionsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("key_key.drop:")) {
                        String value = line.substring("key_key.drop:".length()).trim();
                        return InputConstants.getKey(value).getValue();
                    }
                }
            } catch (Exception ignored) {}
        }
        return GLFW.GLFW_KEY_Q; // Par defaut Q (A sur AZERTY physique)
    }

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
        event.register(EMPTY_STORAGE_KEY.get());
        event.register(DROP_SLOT_KEY.get());
    }
}

