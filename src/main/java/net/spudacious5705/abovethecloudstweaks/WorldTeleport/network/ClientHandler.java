package net.spudacious5705.abovethecloudstweaks.WorldTeleport.network;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.CameraOverlay;

public class ClientHandler {
    public static void handle(final OverlayNetworking.OverlayPacket payload, final IPayloadContext context) {
        // Enqueue the work on the client game thread safely
        context.enqueueWork(() -> {
            CameraOverlay.update(payload.portalFlavour());
        });
    }
}
