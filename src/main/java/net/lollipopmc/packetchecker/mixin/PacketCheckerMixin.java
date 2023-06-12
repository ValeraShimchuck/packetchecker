package net.lollipopmc.packetchecker.mixin;

import com.google.gson.Gson;
import com.mojang.serialization.Codec;
import io.netty.channel.ChannelHandlerContext;
import net.lollipopmc.packetchecker.client.netty.DuplexPacketListener;
import net.lollipopmc.packetchecker.mapping.DeobfuscatedWriter;
import net.lollipopmc.packetchecker.mapping.MinecraftClassMappings;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.*;
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
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.PrintStream;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static net.lollipopmc.packetchecker.client.PacketCheckerClient.*;

@Mixin(ClientConnection.class)
public class PacketCheckerMixin {



    @Inject(method = "handlePacket",
            at = @At("HEAD")
    )
    private static <T extends PacketListener> void handler(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (packet instanceof EntityS2CPacket) return;
        if (packet instanceof EntitySetHeadYawS2CPacket) return;
        if (packet instanceof EntityVelocityUpdateS2CPacket) return;
        if (packet instanceof EntityAttributesS2CPacket) return;
        if (packet instanceof EntitiesDestroyS2CPacket) return;
        if (packet instanceof LightUpdateS2CPacket) return;
        if (packet instanceof EntityTrackerUpdateS2CPacket) return;
        if (packet instanceof EntityPositionS2CPacket) return;
        if (packet instanceof ChunkDataS2CPacket) return;
        if (packet instanceof UnloadChunkS2CPacket) return;
        if (packet instanceof WorldTimeUpdateS2CPacket) return;
        if (packet instanceof PlayerListHeaderS2CPacket) return;
        LOGGER.info("from server: " + SERVER_PACKET_MAPPINGS.getOrDefault(packet.getClass(), packet.getClass().toString()));
        if (packet instanceof InventoryS2CPacket inventory) {
            System.out.println("has got inventory update. window id: " + inventory.getSyncId() + " content: " +
                    inventory.getContents().stream().map(item -> item.getItem().toString()).toList());
        }
        if (packet instanceof OpenScreenS2CPacket openScreen) {
            System.out.println("open inventory: " + openScreen.getSyncId());
        }
    }

    @Inject(method = "channelActive", at = @At("HEAD"))
    private void onActive(ChannelHandlerContext context, CallbackInfo ci) {
        //context.pipeline().addFirst(new DuplexPacketListener());
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"))
    private void send(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        if (packet instanceof PlayerMoveC2SPacket) return;
        LOGGER.info("to server: " + CLIENT_PACKET_MAPPINGS.getOrDefault(packet.getClass(), packet.getClass().toString()));
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    private void exception(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        printStackTrace(ex);
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void disconnect(Text disconnectReason, CallbackInfo ci) {
        LOGGER.info("disconnect player: " + disconnectReason.getString());
    }

    private void printStackTrace(Throwable ex) {
        ex.printStackTrace(DeobfuscatedWriter.ERROR);
    }


}
