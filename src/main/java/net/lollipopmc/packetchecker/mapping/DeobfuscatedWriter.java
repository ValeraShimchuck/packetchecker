package net.lollipopmc.packetchecker.mapping;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DeobfuscatedWriter extends PrintStream {

    public static PrintStream ERROR = new DeobfuscatedWriter(System.err);

    public DeobfuscatedWriter(@NotNull OutputStream out) {
        super(out);
    }


    @Override
    public void print(@Nullable String s) {
        super.print(deobfuscateMessage(s));
    }

    private String deobfuscateMessage(String msg) {
        if (msg == null) return null;
        AtomicReference<String> newMsg = new AtomicReference<>(msg);
        MinecraftClassMappings.getMappings().forEach((obfuscated, deobfuscated) -> newMsg.updateAndGet(str -> str
                .replace(obfuscated, deobfuscated)));
        MinecraftClassMappings.getSimpleNameMappings().forEach((obfuscated, deobfuscated) -> newMsg
                .updateAndGet(str -> str.replace(obfuscated, deobfuscated)));
        return newMsg.get();
    }

}
