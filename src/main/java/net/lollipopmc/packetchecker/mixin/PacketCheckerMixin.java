package net.lollipopmc.packetchecker.mixin;

import com.mojang.brigadier.tree.CommandNode;
import io.netty.channel.ChannelHandlerContext;
import net.lollipopmc.packetchecker.client.accessor.CommandDebugAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.*;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.StreamSupport;

import static net.lollipopmc.packetchecker.client.PacketCheckerClient.*;

@Mixin(ClientConnection.class)
public class PacketCheckerMixin {

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "handlePacket",
            at = @At("RETURN")
    )
    private static <T extends PacketListener> void handler(Packet<T> packet, PacketListener listener, CallbackInfo ci) {

        List<? extends Packet<?>> toCheck;
        if (packet instanceof BundleS2CPacket bundleS2CPacket) {
            toCheck = StreamSupport.stream(bundleS2CPacket.getPackets().spliterator(), false)
                    .map(somePacket -> (Packet<?>) somePacket).toList();
        } else toCheck = List.of(packet);
        toCheck.forEach(PacketCheckerMixin::checkPacket);
    }

    private static void checkPacket(Packet<?> packet) {
        if (packet instanceof PlayerSpawnS2CPacket playerSpawnS2CPacket) {
            System.out.println("player has been spawned");
        }
        if (packet instanceof EntityS2CPacket packetOfEntity) {
            if (!isPlayer(packetOfEntity.getEntity(getCurrWorld()))) return;
        }
        if (packet instanceof EntitySetHeadYawS2CPacket packetOfEntity) {
            if (!isPlayer(packetOfEntity.getEntity(getCurrWorld()))) return;
        }
        if (packet instanceof EntityVelocityUpdateS2CPacket packetOfEntity) {
            if (!isPlayer(packetOfEntity.getId()))
                return;
        }
        if (packet instanceof EntityAttributesS2CPacket packetOfEntity) {
            if (!isPlayer(packetOfEntity.getEntityId()))
                return;
        }
        if (packet instanceof EntitiesDestroyS2CPacket packetOfEntity) {
            boolean containsPlayer = packetOfEntity.getEntityIds().intStream().anyMatch(PacketCheckerMixin::isPlayer);
            if (!containsPlayer) return;
        }
        if (packet instanceof EntityTrackerUpdateS2CPacket  packetOfEntity) {
            if (!isPlayer(packetOfEntity.id()))
                return;
        }
        if (packet instanceof EntityPositionS2CPacket packetOfEntity) {
            if (!isPlayer(packetOfEntity.getId()))
                return;
        }
        if (packet instanceof EntitySpawnS2CPacket packetOfEntity) {
            System.out.println("create entity: " + packetOfEntity.getEntityType());
            //if (packetOfEntity.getEntityType() != EntityType.PLAYER) return;
        }
        if (packet instanceof LightUpdateS2CPacket) return;
        if (packet instanceof ChunkDataS2CPacket) return;
        if (packet instanceof PlayPingS2CPacket) return;
        if (packet instanceof WorldTimeUpdateS2CPacket) return;

        if (packet instanceof BundleS2CPacket commandTreeS2CPacket) {
            //CommandRegistryAccess access = CommandRegistryAccess.of(
            //        (RegistryWrapper.WrapperLookup)MinecraftClient.getInstance().getNetworkHandler().getRegistryManager(),
            //        MinecraftClient.getInstance().getNetworkHandler().getEnabledFeatures());
            //LOGGER.info(commandTreeS2CPacket.getCommandTree(access).getChildren()
            //        .stream().map(CommandNode::getName).toList().toString());
            LOGGER.info(MinecraftClient.getInstance().getNetworkHandler().getCommandDispatcher().getRoot().getChildren()
                    .stream().map(CommandNode::getName).toList().toString());
        }
        //if (packet instanceof InventoryS2CPacket inventoryS2CPacket) {
        //    LOGGER.info(inventoryS2CPacket.getContents().stream()
        //            .map(itemStack -> itemStack.getItem().getRegistryEntry().getKey()
        //                    .orElseThrow().getValue().toString())
        //            .toList().toString());
        //}
        //if (packet instanceof ScreenHandlerSlotUpdateS2CPacket slotUpdate) {
        //    LOGGER.info(slotUpdate.getItemStack().getRegistryEntry().getKey().orElseThrow().getValue().toString());
        //}
        LOGGER.info("from server: " + SERVER_PACKET_MAPPINGS.getOrDefault(packet.getClass(), packet.getClass().toString()));
        //if (packet instanceof QueryResponseS2CPacket responseS2CPacket) {
        //    Gson gson = new Gson();
        //    LOGGER.info(gson.toJson(responseS2CPacket.getServerMetadata()));
        //}
        if (packet instanceof ChunkDataS2CPacket chunkDataS2CPacket) {
            NbtCompound nbt = chunkDataS2CPacket.getChunkData().getHeightmap();
            System.out.println(nbt.toString());
        }
        //if (packet instanceof LoginSuccessS2CPacket loginSuccessS2CPacket) {
        //    System.out.println("login: " + loginSuccessS2CPacket.getProfile());
        //}
        //if (packet instanceof GameJoinS2CPacket gameJoinS2CPacket) {
        //    Codec<DynamicRegistryManager> codec = SerializableRegistries.CODEC;
        //    DynamicRegistryManager.Immutable data = gameJoinS2CPacket.registryManager();
        //    NbtElement nbt = Util.getResult(codec.encodeStart(NbtOps.INSTANCE, data), RuntimeException::new);
        //    try (DataOutputStream output = new DataOutputStream(new FileOutputStream("registries.nbt"))) {
        //        nbt.write(output);
        //    } catch (IOException e) {
        //        throw new RuntimeException(e);
        //    }
        //}
    }

    private static boolean isPlayer(int id) {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return false;
        return isPlayer(world.getEntityById(id));
    }

    private static boolean isPlayer(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity.getType() == EntityType.PLAYER;
    }

    private static World getCurrWorld() {
        return MinecraftClient.getInstance().world;
    }

    @Inject(method = "channelActive", at = @At("HEAD"))
    private void onActive(ChannelHandlerContext context, CallbackInfo ci) {
        //context.pipeline().addFirst(new DuplexPacketListener());
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"))
    private void send(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        if (packet instanceof PlayerMoveC2SPacket) return;
        if (packet instanceof PlayPongC2SPacket) return;
        if (packet instanceof PlayerSessionC2SPacket) {

            CommandDebugAccessor.printCommand();
        }
        //if (packet instanceof ClickSlotC2SPacket clickSlot) {
        //    int actionId = clickSlot.getActionType().ordinal();
        //    LOGGER.info("send inventory click " + clickSlot.getActionType() + " " + actionId + " " + clickSlot.getButton());
        //}
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
