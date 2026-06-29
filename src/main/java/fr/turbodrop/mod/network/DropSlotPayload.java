package fr.turbodrop.mod.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DropSlotPayload(int containerId, int slotIndex, int countToDrop) implements CustomPacketPayload {

    public static final Type<DropSlotPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("turbodrop", "drop_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DropSlotPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            DropSlotPayload::containerId,
            ByteBufCodecs.VAR_INT,
            DropSlotPayload::slotIndex,
            ByteBufCodecs.VAR_INT,
            DropSlotPayload::countToDrop,
            DropSlotPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
