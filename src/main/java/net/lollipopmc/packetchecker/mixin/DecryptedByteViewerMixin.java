package net.lollipopmc.packetchecker.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.lollipopmc.packetchecker.client.common.BytePrinter;
import net.minecraft.network.encryption.PacketDecryptor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PacketDecryptor.class)
public class DecryptedByteViewerMixin {

    @Inject(method = "decode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V",
            at = @At("RETURN"))
    private void printDecrypted(
            ChannelHandlerContext channelHandlerContext,
            ByteBuf byteBuf,
            List<Object> list,
            CallbackInfo ci
    ) {
        ByteBuf buf = (ByteBuf) list.get(0);
        System.out.println("decrypted packet");
        BytePrinter.printBytes(buf);
    }

}
