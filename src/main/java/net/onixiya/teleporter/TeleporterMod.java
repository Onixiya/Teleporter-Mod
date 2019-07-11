package net.onixiya.teleporter;

import java.util.Iterator;

import net.onixiya.teleporter.blocks.TeleporterBlock;
import net.onixiya.teleporter.generations.TeleporterFeature;
import net.onixiya.teleporter.generations.TeleporterGenerator;
import net.onixiya.teleporter.gui.TeleporterGui;
import net.onixiya.teleporter.interfaces.ServerTeleportHelper;
import net.onixiya.teleporter.registries.ServerPacketRegistries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
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
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.LevelProperties;

public class TeleporterMod implements ModInitializer {

	public static final TeleporterBlock bbb = new TeleporterBlock();
	public static final String MODID = "tele";
    public static final String MODID_SHORT = "tele";
    
    public static final StructurePieceType TeleporterPieceType = 
        Registry.register(Registry.STRUCTURE_PIECE, "tele_piece", TeleporterGenerator.Piece::new);//TeleporterGenerator.Piece::new);
    public static final StructureFeature<DefaultFeatureConfig> TeleporterFeature = 
        Registry.register(Registry.FEATURE, "tele_feature", new TeleporterFeature());
    public static final StructureFeature<?> TeleporterStruct = 
        Registry.register(Registry.STRUCTURE_FEATURE, "tele_structure", TeleporterFeature);


	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "tele"), bbb);
		Registry.register(Registry.ITEM, new Identifier(MODID, "tele"),
				new BlockItem(bbb, new Item.Settings().itemGroup(ItemGroup.MISC)));
        ServerPacketRegistries.reg();

        Feature.STRUCTURES.put("Teleporter Feature",TeleporterFeature);
        for(Biome b : Registry.BIOME)
        {
            if(b.hasStructureFeature(Feature.END_CITY))
            {
                b.addStructureFeature(TeleporterFeature, new DefaultFeatureConfig());
                b.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES,Biome.configureFeature(TeleporterFeature, new DefaultFeatureConfig(), Decorator.CHANCE_PASSTHROUGH, new ChanceDecoratorConfig(1)));
            }
        }

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
        //this.method_18783(serverWorld_1);
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
        //this.field_13978 = -1;
        //this.field_13997 = -1.0F;
		//this.field_13979 = -1;
		player.onTeleportationDone();
        return player;
    }
}
