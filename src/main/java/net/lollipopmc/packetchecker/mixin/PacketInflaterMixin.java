package net.lollipopmc.packetchecker.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketInflater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PacketInflater.class)
public class PacketInflaterMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketInflaterMixin.class);

    @Inject(method = "decode", at = @At("HEAD"))
    private void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objects, CallbackInfo ci) {
        ByteBuf copy = buf.copy();
        LOGGER.info("input bytes: " + printByteArray(copy));
    }
    private String printByteArray(ByteBuf buf) {
        if (buf.readableBytes() > 30) buf.writerIndex(30);
        buf.readerIndex(0);
        if (buf.readableBytes() == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        buf.forEachByte(byteData -> {
            sb.append(byteData).append(",");
            return true;
        });
        sb.replace(sb.length() -1, sb.length(), "]");
        return sb.toString();
    }


}
