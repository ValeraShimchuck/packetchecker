package net.lollipopmc.packetchecker.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.lollipopmc.packetchecker.client.accessor.SecretKeyAccessor;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Arrays;


public class DuplexPacketListener extends ChannelDuplexHandler {

    private Channel channel;
    private boolean encryptionEnabled = false;
    private int cipherEncoderRound = 0;
    private int cipherDecoderRound = 0;

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        listenData(ctx, msg);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        listenWriteData(ctx, msg);
        ctx.write(msg, promise);
    }

    private void listenWriteData(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
        if (!(msg instanceof ByteBuf buf)) return;
        checkAndPrintKey();
        if (encryptionEnabled) cipherEncoderRound++;
        System.out.println("c2s encoder round: " + cipherEncoderRound);
        printBytes(buf);
    }

    private void listenData(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof ByteBuf buf)) return;
        checkAndPrintKey();
        if (encryptionEnabled) cipherDecoderRound++;
        System.out.println("s2c decoder round: " + cipherDecoderRound);
        System.out.println(ctx.pipeline().names());
        printBytes(buf);
    }

    private void checkAndPrintKey() {
        if (encryptionEnabled) return;
        encryptionEnabled = existsChannel("encrypt") && existsChannel("decrypt");
        if (!encryptionEnabled) return;
        System.out.println(Arrays.toString(SecretKeyAccessor.getSecretKey().getEncoded()));
        //testCipher(SecretKeyAccessor.getSecretKey());
    }

    private void testCipher(SecretKey secretKey) {
        byte[] bytes = fromIntArray(0, 127, -127);
        try {
            Cipher encoder = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
            Cipher decoder = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
            System.out.println("prepare test: ");
            printBytes(bytes);
            bytes = encoder.update(bytes);
            System.out.println("encoded results: ");
            printBytes(bytes);
            bytes = decoder.update(bytes);
            System.out.println("decoded results: ");
            printBytes(bytes);
        } catch (NetworkEncryptionException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] fromIntArray(int... ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; i++) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }

    private boolean existsChannel(String channel) {
        return this.channel.pipeline().names().contains(channel);
    }

    private void printByteCount(String str, ByteBuf msg) {
        if (msg == null) {
            System.out.println(str + "null");
            return;
        }
        System.out.println(str + msg.readableBytes());
    }

    private void printBytes(ByteBuf buf) {
        if (buf == null) {
            System.out.println("bytes is null");
            return;
        }
        int readerIndex = buf.readerIndex();
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        buf.readerIndex(readerIndex);
        System.out.println(Arrays.toString(arr));
    }

    private void printBytes(byte[] bytes) {
        System.out.println(Arrays.toString(bytes));
    }
}
