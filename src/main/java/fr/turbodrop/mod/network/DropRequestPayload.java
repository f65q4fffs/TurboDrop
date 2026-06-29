package fr.turbodrop.mod.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DropRequestPayload(BlockPos pos, boolean fromMenu) implements CustomPacketPayload {

    public static final Type<DropRequestPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("turbodrop", "drop_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DropRequestPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            DropRequestPayload::pos,
            ByteBufCodecs.BOOL,
            DropRequestPayload::fromMenu,
            DropRequestPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
