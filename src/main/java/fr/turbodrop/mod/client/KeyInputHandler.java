package fr.turbodrop.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.turbodrop.mod.config.TurboDropConfig;
import fr.turbodrop.mod.network.DropRequestPayload;
import fr.turbodrop.mod.network.DropSlotPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "turbodrop", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class KeyInputHandler {

    private static KeyMapping emptyStorageKey;
    private static KeyMapping dropSlotKey;

    private static void ensureKeysInitialized() {
        if (emptyStorageKey == null) {
            emptyStorageKey = KeyBindings.EMPTY_STORAGE_KEY.get();
        }
        if (dropSlotKey == null) {
            dropSlotKey = KeyBindings.DROP_SLOT_KEY.get();
        }
    }

    // Capture des raccourcis en jeu (in-world)
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.screen != null) {
            return;
        }

        ensureKeysInitialized();
        while (emptyStorageKey.consumeClick()) {
            HitResult hit = mc.hitResult;
            if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = blockHit.getBlockPos();
                
                if (TurboDropConfig.ENABLE_CONFIRMATION.get()) {
                    Component blockName = mc.level.getBlockState(pos).getBlock().getName();
                    mc.setScreen(new ConfirmScreen(
                            confirmed -> {
                                if (confirmed) {
                                    PacketDistributor.sendToServer(new DropRequestPayload(pos, false));
                                }
                                mc.setScreen(null); // Retour au jeu
                            },
                            Component.translatable("gui.turbodrop.confirmation.title"),
                            Component.translatable("gui.turbodrop.confirmation.message", blockName)
                    ));
                } else {
                    PacketDistributor.sendToServer(new DropRequestPayload(pos, false));
                }
            }
        }
    }

    // Capture du raccourci clavier dans un GUI de stockage
    @SubscribeEvent
    public static void onScreenKeyPressed(final ScreenEvent.KeyPressed.Pre event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> containerScreen) {
            ensureKeysInitialized();
            // Drop de case unique
            if (dropSlotKey.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
                Slot hoveredSlot = getHoveredSlot(containerScreen);
                if (hoveredSlot != null && hoveredSlot.hasItem()) {
                    event.setCanceled(true);
                    triggerDropSlot(containerScreen, hoveredSlot);
                }
            }
            // Vider tout le stockage
            else if (emptyStorageKey.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
                event.setCanceled(true);
                triggerEmptyStorage(containerScreen);
            }
        }
    }

    private static Slot getHoveredSlot(AbstractContainerScreen<?> containerScreen) {

        try {
            java.lang.reflect.Field field = AbstractContainerScreen.class.getDeclaredField("hoveredSlot");
            field.setAccessible(true);
            return (Slot) field.get(containerScreen);
        } catch (Exception e) {
            // Repli : recherche par type
            for (java.lang.reflect.Field field : AbstractContainerScreen.class.getDeclaredFields()) {
                if (field.getType() == Slot.class) {
                    try {
                        field.setAccessible(true);
                        return (Slot) field.get(containerScreen);
                    } catch (Exception ignored) {}
                }
            }
        }
        return null;
    }


    // Capture du clic de souris au sein d'un GUI de stockage
    @SubscribeEvent
    public static void onScreenMouseClicked(final ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> containerScreen) {
            if (TurboDropConfig.ENABLE_MOUSE_SHORTCUT.get() && isMouseModifierPressed()) {
                if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    event.setCanceled(true);
                    triggerEmptyStorage(containerScreen);
                }
            }
        }
    }

    private static boolean isMouseModifierPressed() {
        String modifier = TurboDropConfig.MOUSE_SHORTCUT_MODIFIER.get();
        if ("ALT".equalsIgnoreCase(modifier)) {
            return Screen.hasAltDown();
        } else if ("CONTROL".equalsIgnoreCase(modifier) || "CTRL".equalsIgnoreCase(modifier)) {
            return Screen.hasControlDown();
        } else if ("SHIFT".equalsIgnoreCase(modifier)) {
            return Screen.hasShiftDown();
        } else if ("NONE".equalsIgnoreCase(modifier)) {
            return true; // Pas de modificateur requis
        }
        return false;
    }

    private static void triggerEmptyStorage(AbstractContainerScreen<?> containerScreen) {
        Minecraft mc = Minecraft.getInstance();
        if (TurboDropConfig.ENABLE_CONFIRMATION.get()) {
            Screen currentScreen = mc.screen;
            mc.setScreen(new ConfirmScreen(
                    confirmed -> {
                        if (confirmed) {
                            PacketDistributor.sendToServer(new DropRequestPayload(BlockPos.ZERO, true));
                        }
                        mc.setScreen(currentScreen); // Retour a l'inventaire
                    },
                    Component.translatable("gui.turbodrop.confirmation.title"),
                    Component.translatable("gui.turbodrop.confirmation.message", containerScreen.getTitle())
            ));
        } else {
            PacketDistributor.sendToServer(new DropRequestPayload(BlockPos.ZERO, true));
        }
    }

    private static void triggerDropSlot(AbstractContainerScreen<?> containerScreen, Slot slot) {
        if (slot != null && slot.hasItem()) {
            int containerId = containerScreen.getMenu().containerId;
            ItemStack stack = slot.getItem();
            int toDrop = stack.getCount();

            if (!TurboDropConfig.DROP_ENTIRE_SLOT.get()) {
                if (TurboDropConfig.USE_VANILLA_MAX_STACK.get()) {
                    toDrop = Math.min(toDrop, stack.getMaxStackSize());
                } else {
                    int limit = TurboDropConfig.CUSTOM_DROP_LIMIT.get();
                    toDrop = Math.min(toDrop, limit);
                }
            }

            if (toDrop > 0) {
                PacketDistributor.sendToServer(new DropSlotPayload(containerId, slot.index, toDrop));
            }
        }
    }
}

