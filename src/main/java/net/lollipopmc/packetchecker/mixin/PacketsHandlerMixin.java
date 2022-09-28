package net.lollipopmc.packetchecker.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class PacketsHandlerMixin {

    private final static Logger LOGGER = LoggerFactory.getLogger(PacketsHandlerMixin.class);

    @Inject(method = "onInventory", at = @At("HEAD"))
    private void debugInventoryPacket(InventoryS2CPacket packet, CallbackInfo ci) {
        LOGGER.info(
                "{} {} {} {}",
                packet.getSyncId(),
                packet.getRevision(),
                packet.getCursorStack(),
                print(packet.getContents())
        );
    }

    private String print(List<ItemStack> itemStacks) {
        StringBuilder itemStacksString = new StringBuilder("{");
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack itemStack = itemStacks.get(i);
            boolean isLast = i + 1 == itemStacks.size();
            Identifier identifier = Registry.ITEM.getId(itemStack.getItem());
            itemStacksString.append(itemStack.getCount())
                    .append(":")
                    .append(Item.getRawId(itemStack.getItem()))
                    .append(":")
                    .append(identifier.getPath());
            if (!isLast) itemStacksString.append(",");
        }
        return itemStacksString.append("}").toString();
    }

}
