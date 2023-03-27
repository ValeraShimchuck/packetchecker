package net.lollipopmc.packetchecker.mixin;

import net.lollipopmc.packetchecker.client.accessor.SecretKeyAccessor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.SecretKey;
import java.security.PublicKey;

@Mixin(LoginKeyC2SPacket.class)
public class LoginKeyMixin {

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void write(SecretKey secretKey, PublicKey publicKey, byte[] nonce, CallbackInfo ci) {
        SecretKeyAccessor.setSecretKey(secretKey);
    }

}
