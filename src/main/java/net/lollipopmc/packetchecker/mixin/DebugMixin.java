package net.lollipopmc.packetchecker.mixin;

import net.lollipopmc.packetchecker.client.accessor.CommandDebugAccessor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.world.chunk.SingularPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Mixin(CommandTreeS2CPacket.class)
public abstract class DebugMixin {


    @Inject(method = "readArgumentBuilder", at = @At("HEAD"))
    private static void onRead(PacketByteBuf buf, byte flags, CallbackInfoReturnable<?> cir) {
        int i = flags & 3;
        if (i > 0) {
            buf.markReaderIndex();
            System.out.println();
            CommandDebugAccessor.addCommand(buf.readString());
            buf.resetReaderIndex();
        }

    }



}
