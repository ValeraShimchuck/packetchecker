package net.lollipopmc.packetchecker.mixin;

import net.minecraft.client.network.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.Optional;

@Mixin(MultiplayerServerListPinger.class)
public class BlackListCheckMixin {


    @Inject(method = "add", at = @At("HEAD"))
    private void onAdd(ServerInfo entry, Runnable runnable, CallbackInfo ci) {
        ServerAddress serverAddress = ServerAddress.parse(entry.address);
        Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(serverAddress)
                .map(Address::getInetSocketAddress);
        //System.out.println(serverAddress.getAddress());
        //System.out.println(AllowedAddressResolver.DEFAULT);
        //System.out.println("is banned: " + optional.isEmpty());
    }

}
