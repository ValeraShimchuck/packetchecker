package net.lollipopmc.packetchecker.mixin;

import net.minecraft.network.*;
import org.spongepowered.asm.mixin.Mixin;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.lollipopmc.packetchecker.client.PacketCheckerClient.*;

@Mixin(ClientConnection.class)
public class PacketCheckerMixin {

    @Inject(method = "handlePacket",
            at = @At("HEAD")
    )
    private static <T extends PacketListener> void handler(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (packet instanceof EntityS2CPacket) return;
        if (packet instanceof EntityPositionS2CPacket) return;
        if (packet instanceof ChunkDataS2CPacket) return;
        if (packet instanceof EntitySetHeadYawS2CPacket) return;
        if (packet instanceof EntityVelocityUpdateS2CPacket) return;
        if (packet instanceof EntityAttributesS2CPacket) return;
        if (packet instanceof EntitiesDestroyS2CPacket) return;
        if (packet instanceof LightUpdateS2CPacket) return;
        if (packet instanceof EntityTrackerUpdateS2CPacket) return;
        LOGGER.info("from server: " + SERVER_PACKET_MAPPINGS.getOrDefault(packet.getClass(), packet.getClass().toString()));

    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"))
    private void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
        if (packet instanceof PlayerMoveC2SPacket) return;
        LOGGER.info("to server: " + CLIENT_PACKET_MAPPINGS.getOrDefault(packet.getClass(), packet.getClass().toString()));
    }
    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    private void exception(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        ex.printStackTrace();
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void disconnect(Text disconnectReason, CallbackInfo ci) {
        LOGGER.info("disconnect player: " + disconnectReason.getString());
    }



}
