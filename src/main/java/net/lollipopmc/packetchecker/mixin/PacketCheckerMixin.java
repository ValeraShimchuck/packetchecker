package net.lollipopmc.packetchecker.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ClientConnection.class)
public class PacketCheckerMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketCheckerMixin.class);

    private static final Map<Class<?>, String> CLIENT_PACKET_MAPPINGS;
    private static final Map<Class<?>, String> SERVER_PACKET_MAPPINGS;

    @Inject(method = "handlePacket",
            at = @At("HEAD")
    )
    private static <T extends PacketListener> void handler(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (packet instanceof EntityS2CPacket) return;
        if (packet instanceof ChunkDataS2CPacket) return;
        LOGGER.info("from server: " + SERVER_PACKET_MAPPINGS.getOrDefault(packet.getClass(), packet.getClass().toString()));
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"))
    private void send(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        if (packet instanceof PlayerMoveC2SPacket) return;
        LOGGER.info("to server: " + CLIENT_PACKET_MAPPINGS.getOrDefault(packet.getClass(), packet.getClass().toString()));
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    private void exception(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        ex.printStackTrace();
    }

    static {
        //noinspection RedundantTypeArguments (explicit type arguments speedup compilation and analysis time)
        CLIENT_PACKET_MAPPINGS = Map.<Class<?>, String>ofEntries(entry(QueryPingC2SPacket.class, "QueryPingC2SPacket"), entry(QueryRequestC2SPacket.class, "QueryRequestC2SPacket"), entry(HandshakeC2SPacket.class, "HandshakeC2SPacket"), entry(LoginHelloC2SPacket.class, "LoginHelloC2SPacket"), entry(LoginKeyC2SPacket.class, "LoginKeyC2SPacket"), entry(LoginQueryResponseC2SPacket.class, "LoginQueryResponseC2SPacket"), entry(AdvancementTabC2SPacket.class, "AdvancementTabC2SPacket"), entry(BoatPaddleStateC2SPacket.class, "BoatPaddleStateC2SPacket"), entry(BookUpdateC2SPacket.class, "BookUpdateC2SPacket"), entry(ButtonClickC2SPacket.class, "ButtonClickC2SPacket"), entry(ChatMessageC2SPacket.class, "ChatMessageC2SPacket"), entry(ClickSlotC2SPacket.class, "ClickSlotC2SPacket"), entry(ClientCommandC2SPacket.class, "ClientCommandC2SPacket"), entry(ClientStatusC2SPacket.class, "ClientStatusC2SPacket"), entry(ClientSettingsC2SPacket.class, "ClientSettingsC2SPacket"), entry(CloseHandledScreenC2SPacket.class, "CloseHandledScreenC2SPacket"), entry(CommandExecutionC2SPacket.class, "CommandExecutionC2SPacket"), entry(CraftRequestC2SPacket.class, "CraftRequestC2SPacket"), entry(CreativeInventoryActionC2SPacket.class, "CreativeInventoryActionC2SPacket"), entry(CustomPayloadC2SPacket.class, "CustomPayloadC2SPacket"), entry(HandSwingC2SPacket.class, "HandSwingC2SPacket"), entry(JigsawGeneratingC2SPacket.class, "JigsawGeneratingC2SPacket"), entry(KeepAliveC2SPacket.class, "KeepAliveC2SPacket"), entry(MessageAcknowledgmentC2SPacket.class, "MessageAcknowledgmentC2SPacket"), entry(PickFromInventoryC2SPacket.class, "PickFromInventoryC2SPacket"), entry(PlayerActionC2SPacket.class, "PlayerActionC2SPacket"), entry(PlayerInputC2SPacket.class, "PlayerInputC2SPacket"), entry(PlayerInteractBlockC2SPacket.class, "PlayerInteractBlockC2SPacket"), entry(PlayerInteractEntityC2SPacket.class, "PlayerInteractEntityC2SPacket"), entry(PlayerMoveC2SPacket.class, "PlayerMoveC2SPacket"), entry(PlayPongC2SPacket.class, "PlayPongC2SPacket"), entry(QueryBlockNbtC2SPacket.class, "QueryBlockNbtC2SPacket"), entry(QueryEntityNbtC2SPacket.class, "QueryEntityNbtC2SPacket"), entry(RecipeBookDataC2SPacket.class, "RecipeBookDataC2SPacket"), entry(RecipeCategoryOptionsC2SPacket.class, "RecipeCategoryOptionsC2SPacket"), entry(RenameItemC2SPacket.class, "RenameItemC2SPacket"), entry(RequestChatPreviewC2SPacket.class, "RequestChatPreviewC2SPacket"), entry(RequestCommandCompletionsC2SPacket.class, "RequestCommandCompletionsC2SPacket"), entry(ResourcePackStatusC2SPacket.class, "ResourcePackStatusC2SPacket"), entry(SelectMerchantTradeC2SPacket.class, "SelectMerchantTradeC2SPacket"), entry(SpectatorTeleportC2SPacket.class, "SpectatorTeleportC2SPacket"), entry(TeleportConfirmC2SPacket.class, "TeleportConfirmC2SPacket"), entry(UpdateBeaconC2SPacket.class, "UpdateBeaconC2SPacket"), entry(UpdateCommandBlockC2SPacket.class, "UpdateCommandBlockC2SPacket"), entry(UpdateCommandBlockMinecartC2SPacket.class, "UpdateCommandBlockMinecartC2SPacket"), entry(UpdateDifficultyC2SPacket.class, "UpdateDifficultyC2SPacket"), entry(UpdateDifficultyLockC2SPacket.class, "UpdateDifficultyLockC2SPacket"), entry(UpdateJigsawC2SPacket.class, "UpdateJigsawC2SPacket"), entry(UpdatePlayerAbilitiesC2SPacket.class, "UpdatePlayerAbilitiesC2SPacket"), entry(UpdateSelectedSlotC2SPacket.class, "UpdateSelectedSlotC2SPacket"), entry(UpdateSignC2SPacket.class, "UpdateSignC2SPacket"), entry(UpdateStructureBlockC2SPacket.class, "UpdateStructureBlockC2SPacket"), entry(VehicleMoveC2SPacket.class, "VehicleMoveC2SPacket"));
        //noinspection RedundantTypeArguments (explicit type arguments speedup compilation and analysis time)
        SERVER_PACKET_MAPPINGS = Map.<Class<?>, String>ofEntries(
                entry(TitleS2CPacket.class, "TitleS2CPacket"),
                entry(QueryPongS2CPacket.class, "QueryPongS2CPacket"),
                entry(QueryResponseS2CPacket.class, "QueryResponseS2CPacket"),
                entry(LoginCompressionS2CPacket.class, "LoginCompressionS2CPacket"),
                entry(LoginDisconnectS2CPacket.class, "LoginDisconnectS2CPacket"),
                entry(LoginHelloS2CPacket.class, "LoginHelloS2CPacket"),
                entry(LoginQueryRequestS2CPacket.class, "LoginQueryRequestS2CPacket"),
                entry(LoginSuccessS2CPacket.class, "LoginSuccessS2CPacket"),
                entry(AdvancementUpdateS2CPacket.class, "AdvancementUpdateS2CPacket"),
                entry(BlockBreakingProgressS2CPacket.class, "BlockBreakingProgressS2CPacket"),
                entry(BlockEntityUpdateS2CPacket.class, "BlockEntityUpdateS2CPacket"),
                entry(BlockEventS2CPacket.class, "BlockEventS2CPacket"),
                entry(BlockUpdateS2CPacket.class, "BlockUpdateS2CPacket"),
                entry(BossBarS2CPacket.class, "BossBarS2CPacket"),
                entry(ChatMessageS2CPacket.class, "ChatMessageS2CPacket"),
                entry(ChatPreviewS2CPacket.class, "ChatPreviewS2CPacket"),
                entry(ChatPreviewStateChangeS2CPacket.class, "ChatPreviewStateChangeS2CPacket"),
                entry(ChatSuggestionsS2CPacket.class, "ChatSuggestionsS2CPacket"),
                entry(ChunkDataS2CPacket.class, "ChunkDataS2CPacket"),
                entry(ChunkDeltaUpdateS2CPacket.class, "ChunkDeltaUpdateS2CPacket"),
                entry(ChunkLoadDistanceS2CPacket.class, "ChunkLoadDistanceS2CPacket"),
                entry(ChunkRenderDistanceCenterS2CPacket.class, "ChunkRenderDistanceCenterS2CPacket"),
                entry(ClearTitleS2CPacket.class, "ClearTitleS2CPacket"),
                entry(CloseScreenS2CPacket.class, "CloseScreenS2CPacket"),
                entry(CommandSuggestionsS2CPacket.class, "CommandSuggestionsS2CPacket"),
                entry(CommandTreeS2CPacket.class, "CommandTreeS2CPacket"),
                entry(CooldownUpdateS2CPacket.class, "CooldownUpdateS2CPacket"),
                entry(CraftFailedResponseS2CPacket.class, "CraftFailedResponseS2CPacket"),
                entry(CustomPayloadS2CPacket.class, "CustomPayloadS2CPacket"),
                entry(DeathMessageS2CPacket.class, "DeathMessageS2CPacket"),
                entry(DifficultyS2CPacket.class, "DifficultyS2CPacket"),
                entry(DisconnectS2CPacket.class, "DisconnectS2CPacket"),
                entry(EndCombatS2CPacket.class, "EndCombatS2CPacket"),
                entry(EnterCombatS2CPacket.class, "EnterCombatS2CPacket"),
                entry(EntitiesDestroyS2CPacket.class, "EntitiesDestroyS2CPacket"),
                entry(EntityAnimationS2CPacket.class, "EntityAnimationS2CPacket"),
                entry(EntityAttachS2CPacket.class, "EntityAttachS2CPacket"),
                entry(EntityAttributesS2CPacket.class, "EntityAttributesS2CPacket"),
                entry(EntityEquipmentUpdateS2CPacket.class, "EntityEquipmentUpdateS2CPacket"),
                entry(EntityPassengersSetS2CPacket.class, "EntityPassengersSetS2CPacket"),
                entry(EntityPositionS2CPacket.class, "EntityPositionS2CPacket"),
                entry(EntityS2CPacket.class, "EntityS2CPacket"),
                entry(EntityS2CPacket.MoveRelative.class, "EntityS2CPacket.MoveRelative"),
                entry(EntityS2CPacket.Rotate.class, "EntityS2CPacket.Rotate"),
                entry(EntityS2CPacket.RotateAndMoveRelative.class, "EntityS2CPacket.RotateAndMoveRelative"),
                entry(EntitySetHeadYawS2CPacket.class, "EntitySetHeadYawS2CPacket"),
                entry(EntitySpawnS2CPacket.class, "EntitySpawnS2CPacket"),
                entry(EntityStatusEffectS2CPacket.class, "EntityStatusEffectS2CPacket"),
                entry(EntityStatusS2CPacket.class, "EntityStatusS2CPacket"),
                entry(EntityTrackerUpdateS2CPacket.class, "EntityTrackerUpdateS2CPacket"),
                entry(EntityVelocityUpdateS2CPacket.class, "EntityVelocityUpdateS2CPacket"),
                entry(ExperienceBarUpdateS2CPacket.class, "ExperienceBarUpdateS2CPacket"),
                entry(ExperienceOrbSpawnS2CPacket.class, "ExperienceOrbSpawnS2CPacket"),
                entry(ExplosionS2CPacket.class, "ExplosionS2CPacket"),
                entry(GameJoinS2CPacket.class, "GameJoinS2CPacket"),
                entry(GameMessageS2CPacket.class, "GameMessageS2CPacket"),
                entry(GameStateChangeS2CPacket.class, "GameStateChangeS2CPacket"),
                entry(HealthUpdateS2CPacket.class, "HealthUpdateS2CPacket"),
                entry(HideMessageS2CPacket.class, "HideMessageS2CPacket"),
                entry(InventoryS2CPacket.class, "InventoryS2CPacket"),
                entry(ItemPickupAnimationS2CPacket.class, "ItemPickupAnimationS2CPacket"),
                entry(KeepAliveS2CPacket.class, "KeepAliveS2CPacket"),
                entry(LightUpdateS2CPacket.class, "LightUpdateS2CPacket"),
                entry(LookAtS2CPacket.class, "LookAtS2CPacket"),
                entry(MapUpdateS2CPacket.class, "MapUpdateS2CPacket"),
                entry(MessageHeaderS2CPacket.class, "MessageHeaderS2CPacket"),
                entry(NbtQueryResponseS2CPacket.class, "NbtQueryResponseS2CPacket"),
                entry(OpenHorseScreenS2CPacket.class, "OpenHorseScreenS2CPacket"),
                entry(OpenScreenS2CPacket.class, "OpenScreenS2CPacket"),
                entry(OpenWrittenBookS2CPacket.class, "OpenWrittenBookS2CPacket"),
                entry(OverlayMessageS2CPacket.class, "OverlayMessageS2CPacket"),
                entry(ParticleS2CPacket.class, "ParticleS2CPacket"),
                entry(PlayerAbilitiesS2CPacket.class, "PlayerAbilitiesS2CPacket"),
                entry(PlayerActionResponseS2CPacket.class, "PlayerActionResponseS2CPacket"),
                entry(PlayerListHeaderS2CPacket.class, "PlayerListHeaderS2CPacket"),
                entry(PlayerListS2CPacket.class, "PlayerListS2CPacket"),
                entry(PlayerPositionLookS2CPacket.class, "PlayerPositionLookS2CPacket"),
                entry(PlayerRespawnS2CPacket.class, "PlayerRespawnS2CPacket"),
                entry(PlayerSpawnPositionS2CPacket.class, "PlayerSpawnPositionS2CPacket"),
                entry(PlayerSpawnS2CPacket.class, "PlayerSpawnS2CPacket"),
                entry(PlayPingS2CPacket.class, "PlayPingS2CPacket"),
                entry(PlaySoundFromEntityS2CPacket.class, "PlaySoundFromEntityS2CPacket"),
                entry(PlaySoundIdS2CPacket.class, "PlaySoundIdS2CPacket"),
                entry(PlaySoundS2CPacket.class, "PlaySoundS2CPacket"),
                entry(RemoveEntityStatusEffectS2CPacket.class, "RemoveEntityStatusEffectS2CPacket"),
                entry(ResourcePackSendS2CPacket.class, "ResourcePackSendS2CPacket"),
                entry(ScoreboardDisplayS2CPacket.class, "ScoreboardDisplayS2CPacket"),
                entry(ScoreboardObjectiveUpdateS2CPacket.class, "ScoreboardObjectiveUpdateS2CPacket"),
                entry(ScoreboardPlayerUpdateS2CPacket.class, "ScoreboardPlayerUpdateS2CPacket"),
                entry(ScreenHandlerPropertyUpdateS2CPacket.class, "ScreenHandlerPropertyUpdateS2CPacket"),
                entry(ScreenHandlerSlotUpdateS2CPacket.class, "ScreenHandlerSlotUpdateS2CPacket"),
                entry(SelectAdvancementTabS2CPacket.class, "SelectAdvancementTabS2CPacket"),
                entry(ServerMetadataS2CPacket.class, "ServerMetadataS2CPacket"),
                entry(SetCameraEntityS2CPacket.class, "SetCameraEntityS2CPacket"),
                entry(SetTradeOffersS2CPacket.class, "SetTradeOffersS2CPacket"),
                entry(SignEditorOpenS2CPacket.class, "SignEditorOpenS2CPacket"),
                entry(SimulationDistanceS2CPacket.class, "SimulationDistanceS2CPacket"),
                entry(StatisticsS2CPacket.class, "StatisticsS2CPacket"),
                entry(StopSoundS2CPacket.class, "StopSoundS2CPacket"),
                entry(SubtitleS2CPacket.class, "SubtitleS2CPacket"),
                entry(SynchronizeRecipesS2CPacket.class, "SynchronizeRecipesS2CPacket"),
                entry(SynchronizeTagsS2CPacket.class, "SynchronizeTagsS2CPacket"),
                entry(TeamS2CPacket.class, "TeamS2CPacket"),
                entry(TitleFadeS2CPacket.class, "TitleFadeS2CPacket"),
                entry(UnloadChunkS2CPacket.class, "UnloadChunkS2CPacket"),
                entry(UnlockRecipesS2CPacket.class, "UnlockRecipesS2CPacket"),
                entry(UpdateSelectedSlotS2CPacket.class, "UpdateSelectedSlotS2CPacket"),
                entry(VehicleMoveS2CPacket.class, "VehicleMoveS2CPacket"),
                entry(WorldBorderCenterChangedS2CPacket.class, "WorldBorderCenterChangedS2CPacket"),
                entry(WorldBorderInitializeS2CPacket.class, "WorldBorderInitializeS2CPacket"),
                entry(WorldBorderInterpolateSizeS2CPacket.class, "WorldBorderInterpolateSizeS2CPacket"),
                entry(WorldBorderSizeChangedS2CPacket.class, "WorldBorderSizeChangedS2CPacket"),
                entry(WorldBorderWarningBlocksChangedS2CPacket.class, "WorldBorderWarningBlocksChangedS2CPacket"),
                entry(WorldBorderWarningTimeChangedS2CPacket.class, "WorldBorderWarningTimeChangedS2CPacket"),
                entry(WorldEventS2CPacket.class, "WorldEventS2CPacket"),
                entry(WorldTimeUpdateS2CPacket.class, "WorldTimeUpdateS2CPacket")
        );
    }

    private static <K, V> Map.Entry<K, V> entry(K k, V v) {
        return Map.entry(k, v);
    }

}
