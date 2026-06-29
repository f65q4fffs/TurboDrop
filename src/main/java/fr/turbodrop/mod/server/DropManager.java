package fr.turbodrop.mod.server;

import fr.turbodrop.mod.config.TurboDropConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class DropManager {

    public static void handleDropSlotRequest(ServerPlayer player, int containerId, int slotIndex) {
        if (player == null || !player.isAlive()) {
            return;
        }

        var menu = player.containerMenu;
        if (menu == null || menu.containerId != containerId) {
            return;
        }

        if (slotIndex < 0 || slotIndex >= menu.slots.size()) {
            return;
        }

        Slot slot = menu.getSlot(slotIndex);
        if (slot == null || !slot.hasItem() || !slot.mayPickup(player)) {
            return;
        }

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
            ItemStack dropStack = stack.copy();
            dropStack.setCount(toDrop);

            // Retirer les items de la case
            stack.shrink(toDrop);
            slot.set(stack);
            menu.broadcastChanges();

            // Faire apparaitre l'ItemEntity propulse dans la direction du regard
            ServerLevel level = player.serverLevel();
            double x = player.getX();
            double y = player.getY() + player.getEyeHeight() - 0.3; // Hauteur de drop vanilla exacte
            double z = player.getZ();
            Vec3 look = player.getLookAngle();

            ItemEntity entity = new ItemEntity(level, x, y, z, dropStack);
            entity.setPickUpDelay(10); // Delai de ramassage standard (10 ticks / 0.5s)
            entity.setDeltaMovement(look.x * 0.3, 0.2, look.z * 0.3);
            level.addFreshEntity(entity);
        }
    }

    public static void handleDropRequest(ServerPlayer player, BlockPos pos, boolean fromMenu) {
        if (player == null || !player.isAlive()) {
            return;
        }

        List<ItemStack> extractedItems = new ArrayList<>();
        ServerLevel level = player.serverLevel();

        if (fromMenu) {
            // Drop depuis un GUI ouvert
            var menu = player.containerMenu;
            if (menu == player.inventoryMenu) {
                // Securite : Ne pas autoriser le drop depuis l'inventaire personnel vide de contenant
                return;
            }

            for (Slot slot : menu.slots) {
                // Securite : Ignorer les slots de l'inventaire du joueur
                if (slot.container == player.getInventory()) {
                    continue;
                }
                // Securite : S'assurer que le joueur a le droit de recuperer l'item dans ce slot
                if (!slot.mayPickup(player)) {
                    continue;
                }

                ItemStack stack = slot.getItem();
                if (!stack.isEmpty()) {
                    ItemStack copy = stack.copy();
                    slot.set(ItemStack.EMPTY);
                    slot.setChanged();
                    extractedItems.add(copy);
                }
            }

            if (!extractedItems.isEmpty()) {
                menu.broadcastChanges();
            }
        } else {
            // Drop depuis un bloc dans le monde cible
            if (pos == null) {
                return;
            }

            // Securite : Verification de la distance pour eviter le drop a distance illimitee
            double distanceSq = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distanceSq > 64.0) { // Max 8 blocs de distance
                return;
            }

            // Recuperation du handler d'items du bloc via les capabilities NeoForge
            IItemHandler itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (itemHandler != null) {
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        // Extraire de maniere repetitive jusqu'a ce que la case soit vide
                        // (indispensable car l'itemHandler bride l'extraction a 64 par appel)
                        int toExtract = stack.getCount();
                        while (toExtract > 0) {
                            ItemStack extracted = itemHandler.extractItem(i, toExtract, false);
                            if (extracted.isEmpty() || extracted.getCount() <= 0) {
                                break;
                            }
                            extractedItems.add(extracted);
                            toExtract -= extracted.getCount();
                        }
                    }
                }
            }
        }


        // Si des items ont ete extraits avec succes, on les place dans la file d'attente progressive
        if (!extractedItems.isEmpty()) {
            double dropX, dropY, dropZ;
            if (fromMenu) {
                dropX = player.getX();
                dropY = player.getY() + player.getEyeHeight() - 0.3; // Hauteur de drop vanilla
                dropZ = player.getZ();
            } else {
                dropX = pos.getX() + 0.5;
                dropY = pos.getY() + 1.1; // Au-dessus du coffre (évite le clipping dans le bloc)
                dropZ = pos.getZ() + 0.5;
            }

            TaskQueue.enqueue(new TaskQueue.DropTask(level, dropX, dropY, dropZ, extractedItems, player.getUUID(), fromMenu));
        }
    }
}


