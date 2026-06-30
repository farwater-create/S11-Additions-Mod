package net.spudacious5705.abovethecloudstweaks.WorldTeleport.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import tictim.paraglider.network.ClientPacketHandler;

import static net.spudacious5705.abovethecloudstweaks.Abovethecloudstweaks.MODID;

@EventBusSubscriber(modid = MODID)
public class OverlayNetworking {

    public static void sendPacket(ServerPlayer player, int portalFlavour) {
        PacketDistributor.sendToPlayer(player, new OverlayPacket(portalFlavour));
    }

    public record OverlayPacket(int portalFlavour) implements CustomPacketPayload {

        // Define a unique ID for this network packet channel
        public static final Type<OverlayPacket> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "portal_overlay_update"));

        // Codec to automatically handle reading/writing data over the network
        public static final StreamCodec<FriendlyByteBuf, OverlayPacket> CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, OverlayPacket::portalFlavour,
                OverlayPacket::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0");
        registrar.playToClient(
                OverlayPacket.TYPE,
                OverlayPacket.CODEC,
                ClientHandler::handle
        );
    }

}
