package fr.turbodrop.mod.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "turbodrop", bus = EventBusSubscriber.Bus.MOD)
public class ModPackets {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Obtenir le registrar reseau avec la version de notre protocole
        final PayloadRegistrar registrar = event.registrar("1");

        // Enregistrer le paquet client-vers-serveur pour vider tout le coffre
        registrar.playToServer(
                DropRequestPayload.TYPE,
                DropRequestPayload.STREAM_CODEC,
                ModPackets::handleDropRequest
        );

        // Enregistrer le paquet client-vers-serveur pour drop un unique slot
        registrar.playToServer(
                DropSlotPayload.TYPE,
                DropSlotPayload.STREAM_CODEC,
                ModPackets::handleDropSlot
        );
    }

    private static void handleDropRequest(final DropRequestPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // Execution de maniere securisee sur le thread principal du serveur
            var player = context.player();
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                fr.turbodrop.mod.server.DropManager.handleDropRequest(serverPlayer, payload.pos(), payload.fromMenu());
            }
        });
    }

    private static void handleDropSlot(final DropSlotPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // Execution de maniere securisee sur le thread principal du serveur
            var player = context.player();
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                fr.turbodrop.mod.server.DropManager.handleDropSlotRequest(serverPlayer, payload.containerId(), payload.slotIndex(), payload.countToDrop());
            }
        });
    }
}

