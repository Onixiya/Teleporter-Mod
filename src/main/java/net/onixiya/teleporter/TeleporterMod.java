package net.onixiya.teleporter;

import java.util.Iterator;
import net.onixiya.teleporter.blocks.TeleporterBlock;
import net.onixiya.teleporter.blocks.TeleporterBlockEntity;
import net.onixiya.teleporter.gui.TeleporterGui;
import net.onixiya.teleporter.interfaces.ServerTeleportHelper;
import net.onixiya.teleporter.registries.ServerPacketRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

public class TeleporterMod implements ModInitializer {

	public static final TeleporterBlock bbb = new TeleporterBlock();
	public static final String MODID = "tele";
    public static final String MODID_SHORT = "tele";
    public static final TeleporterBlock TELEPORTER_BLOCK= new TeleporterBlock();

    public static BlockEntityType<TeleporterBlockEntity> TELEPORTER_ENTITY =
        BlockEntityType.Builder.create(TeleporterBlockEntity::new, bbb).build(null);


	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "tele"), bbb);
		Registry.register(Registry.ITEM, new Identifier(MODID, "tele"),
                new BlockItem(bbb, new Item.Settings().itemGroup(ItemGroup.MISC)));
        Registry.register(Registry.BLOCK_ENTITY, MODID, TELEPORTER_ENTITY);
        ServerPacketRegistries.reg();

    }

	@Environment(EnvType.CLIENT)
	public static void openTeleporterGUI(BlockPos blockPos_1)
	{
		MinecraftClient.getInstance().getNetworkHandler().sendPacket(TeleporterClientMod.createRequestPortalList());
		MinecraftClient.getInstance().openScreen(new TeleporterGui(blockPos_1));

	}

	public static ServerPlayerEntity interDimesionalTeleport(ServerPlayerEntity player, DimensionType destDim, BlockPos blockDest) {
        ((ServerTeleportHelper)player).setIsTeleporting(true);
        DimensionType sourceDim = player.dimension;
        ServerWorld sourceWorld = player.server.getWorld(sourceDim);
        player.dimension = destDim;
        ServerWorld destWorld = player.server.getWorld(destDim);
        LevelProperties levelProps = player.world.getLevelProperties();
        player.networkHandler.sendPacket(new PlayerRespawnS2CPacket(destDim, levelProps.getGeneratorType(), player.interactionManager.getGameMode()));
        player.networkHandler.sendPacket(new DifficultyS2CPacket(levelProps.getDifficulty(), levelProps.isDifficultyLocked()));
        PlayerManager playerManager = player.server.getPlayerManager();
        playerManager.sendCommandTree(player);
        sourceWorld.removePlayer(player);
        player.removed = false;
        sourceWorld.getProfiler().push("moving");
 
        player.setPositionAndAngles(blockDest.getX(), blockDest.getY(), blockDest.getZ(), player.yaw, player.pitch);
        sourceWorld.getProfiler().pop();

        player.setWorld(destWorld);
        destWorld.method_18211(player);
        player.networkHandler.requestTeleport(player.x, player.y, player.z, player.yaw, player.pitch);
        player.interactionManager.setWorld(destWorld);
        player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
        playerManager.sendWorldInfo(player, destWorld);
        playerManager.method_14594(player);
        Iterator<StatusEffectInstance> statusEffectIter = player.getStatusEffects().iterator();

        while(statusEffectIter.hasNext()) {
            StatusEffectInstance statusEffectInstance_1 = (StatusEffectInstance)statusEffectIter.next();
            player.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(player.getEntityId(), statusEffectInstance_1));
        }

        player.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
		player.onTeleportationDone();
        return player;
    }
}
