package fr.turbodrop.mod.server;

import fr.turbodrop.mod.config.TurboDropConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;

@EventBusSubscriber(modid = "turbodrop", bus = EventBusSubscriber.Bus.GAME)
public class TaskQueue {

    public static class DropTask {
        public final ServerLevel level;
        public final double x, y, z;
        public final List<ItemStack> itemsToDrop;
        public final UUID playerUuid;
        public final boolean fromMenu;

        public DropTask(ServerLevel level, double x, double y, double z, List<ItemStack> itemsToDrop, UUID playerUuid, boolean fromMenu) {
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;
            this.itemsToDrop = new ArrayList<>(itemsToDrop);
            this.playerUuid = playerUuid;
            this.fromMenu = fromMenu;
        }
    }

    private static final List<DropTask> QUEUE = Collections.synchronizedList(new ArrayList<>());

    public static void enqueue(DropTask task) {
        QUEUE.add(task);
    }

    @SubscribeEvent
    public static void onServerTick(final ServerTickEvent.Post event) {
        processQueue();
    }

    private static void processQueue() {
        if (QUEUE.isEmpty()) return;

        int maxDrops = TurboDropConfig.MAX_STACKS_PER_TICK.get();
        int dropsThisTick = 0;

        synchronized (QUEUE) {
            Iterator<DropTask> iterator = QUEUE.iterator();
            while (iterator.hasNext() && dropsThisTick < maxDrops) {
                DropTask currentTask = iterator.next();
                
                if (currentTask.itemsToDrop.isEmpty()) {
                    iterator.remove();
                    continue;
                }

                int remainingLimit = maxDrops - dropsThisTick;
                int dropCount = Math.min(currentTask.itemsToDrop.size(), remainingLimit);

                for (int i = 0; i < dropCount; i++) {
                    ItemStack stack = currentTask.itemsToDrop.remove(0);
                    if (!stack.isEmpty()) {
                        // Dispersion aleatoire legere des coordonnees pour eviter le cramming/clipping
                        double rx = currentTask.x + (currentTask.level.random.nextFloat() - 0.5) * 0.3;
                        double ry = currentTask.y + (currentTask.level.random.nextFloat() - 0.5) * 0.1;
                        double rz = currentTask.z + (currentTask.level.random.nextFloat() - 0.5) * 0.3;

                        ItemEntity entity = new ItemEntity(currentTask.level, rx, ry, rz, stack.copy());
                        entity.setPickUpDelay(10); // Delai de ramassage standard (10 ticks / 0.5s)

                        if (currentTask.fromMenu) {
                            // Si c'est depuis le menu, on jette l'item en avant (comme un drop de joueur)
                            net.minecraft.world.entity.player.Player player = currentTask.level.getPlayerByUUID(currentTask.playerUuid);
                            if (player != null) {
                                net.minecraft.world.phys.Vec3 look = player.getLookAngle();
                                double vx = look.x * 0.3 + (currentTask.level.random.nextFloat() - 0.5) * 0.05;
                                double vy = 0.2 + (currentTask.level.random.nextFloat() - 0.5) * 0.05;
                                double vz = look.z * 0.3 + (currentTask.level.random.nextFloat() - 0.5) * 0.05;
                                entity.setDeltaMovement(vx, vy, vz);
                            } else {
                                entity.setDeltaMovement(0, 0.1, 0);
                            }
                        } else {
                            // Si c'est depuis le bloc dans le monde, on le fait popper vers le haut (ejection standard)
                            double dx = currentTask.level.random.nextGaussian() * 0.05;
                            double dy = currentTask.level.random.nextGaussian() * 0.05 + 0.2;
                            double dz = currentTask.level.random.nextGaussian() * 0.05;
                            entity.setDeltaMovement(dx, dy, dz);
                        }

                        currentTask.level.addFreshEntity(entity);
                    }
                }

                dropsThisTick += dropCount;

                if (currentTask.itemsToDrop.isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerUuid = event.getEntity().getUUID();
        synchronized (QUEUE) {
            Iterator<DropTask> iterator = QUEUE.iterator();
            while (iterator.hasNext()) {
                DropTask task = iterator.next();
                if (task.playerUuid.equals(playerUuid)) {
                    // Ejecter tous les items restants d'un coup au sol pour eviter les pertes
                    for (ItemStack stack : task.itemsToDrop) {
                        if (!stack.isEmpty()) {
                            ItemEntity entity = new ItemEntity(task.level, task.x, task.y, task.z, stack.copy());
                            entity.setPickUpDelay(10);
                            entity.setDeltaMovement(0, 0.1, 0);
                            task.level.addFreshEntity(entity);
                        }
                    }
                    iterator.remove();
                }
            }
        }
    }
}


