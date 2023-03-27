package net.lollipopmc.packetchecker.client.common;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;

public class BytePrinter {

    public static void printBytes(ByteBuf buf) {
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

    public static void printBytes(byte[] bytes) {
        System.out.println(Arrays.toString(bytes));
    }

}
