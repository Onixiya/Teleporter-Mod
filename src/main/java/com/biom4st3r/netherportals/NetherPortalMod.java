package com.biom4st3r.netherportals;

import java.util.Iterator;
import java.util.Queue;

import com.biom4st3r.netherportals.blocks.BirdBathBlock;
import com.biom4st3r.netherportals.generations.BirdBathFeature;
import com.biom4st3r.netherportals.generations.BirdBathGenerator;
import com.biom4st3r.netherportals.gui.BirdBathGui;
import com.biom4st3r.netherportals.interfaces.ServerTeleportHelper;
import com.biom4st3r.netherportals.registries.ServerPacketRegistries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
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

public class NetherPortalMod implements ModInitializer {

    public static final BirdBathBlock bbb = new BirdBathBlock();
    public static final BirdBathBlock quartz_bbb = new BirdBathBlock();
	public static final String MODID = "biom4st3rportal";
    public static final String MODID_SHORT = "b43rp";
    
    public static final StructurePieceType birdBathPieceType = 
        Registry.register(Registry.STRUCTURE_PIECE, "birdbath_piece", BirdBathGenerator.Piece::new);//BirdBathGenerator.Piece::new);
    public static final StructureFeature<DefaultFeatureConfig> birdBathFeature = 
        Registry.register(Registry.FEATURE, "birdbath_feature", new BirdBathFeature());
    public static final StructureFeature<?> birdbathStruct = 
        Registry.register(Registry.STRUCTURE_FEATURE, "birdbat_structure", birdBathFeature);


	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(MODID, "portal"), bbb);
		Registry.register(Registry.ITEM, new Identifier(MODID, "portal"),
			new BlockItem(bbb, new Item.Settings().itemGroup(ItemGroup.MISC)));
        

        Registry.register(Registry.BLOCK, new Identifier(MODID, "quartz_portal"), quartz_bbb);
        Registry.register(Registry.ITEM, new Identifier(MODID,"quartz_portal"), 
            new BlockItem(quartz_bbb, new Item.Settings().itemGroup(ItemGroup.MISC)));

        ServerPacketRegistries.reg();

        Feature.STRUCTURES.put("Bird Bath Feature",birdBathFeature);
        for(Biome b : Registry.BIOME)
        {
            if(b.hasStructureFeature(Feature.END_CITY))
            {
                b.addStructureFeature(birdBathFeature, new DefaultFeatureConfig());
                b.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES,Biome.configureFeature(birdBathFeature, new DefaultFeatureConfig(), Decorator.CHANCE_PASSTHROUGH, new ChanceDecoratorConfig(1)));
            }
        }

    }

	@Environment(EnvType.CLIENT)
	public static void openBirdBathGUI(BlockPos blockPos_1)
	{
		MinecraftClient.getInstance().getNetworkHandler().sendPacket(NetherPortalClientMod.createRequestPortalList());
		MinecraftClient.getInstance().openScreen(new BirdBathGui(blockPos_1));

	}

	public static ServerPlayerEntity interDimesionalTeleport(ServerPlayerEntity player, DimensionType destDim, BlockPos blockDest) {
        ((ServerTeleportHelper)player).setIsTeleporting(true);
        //PortalBlock
        //PlayerEntity
        //ClientPlayerEntity
        //ServerPlayerEntity
        DimensionType sourceDim = player.dimension;
        ServerWorld sourceWorld = player.server.getWorld(sourceDim);
        player.dimension = destDim;
        ServerWorld destWorld = player.server.getWorld(destDim);
        
        if(destWorld.getBlockState(blockDest).isAir())
        {

        }
        LevelProperties levelProps = player.world.getLevelProperties();
        player.networkHandler.sendPacket(new PlayerRespawnS2CPacket(destDim, levelProps.getGeneratorType(), player.interactionManager.getGameMode()));
        player.networkHandler.sendPacket(new DifficultyS2CPacket(levelProps.getDifficulty(), levelProps.isDifficultyLocked()));
        PlayerManager playerManager = player.server.getPlayerManager();
        playerManager.sendCommandTree(player);
        //player.detach();
;
        sourceWorld.getProfiler().push("moving");
        player.setPositionAndAngles(blockDest.getX(), blockDest.getY(), blockDest.getZ(), player.yaw, player.pitch);
        sourceWorld.getProfiler().pop();
        sourceWorld.getProfiler().push("placing");
        player.setPositionAndAngles(blockDest.getX(), blockDest.getY(), blockDest.getZ(), player.yaw, player.pitch);
        sourceWorld.getProfiler().pop();
        player.setWorld(destWorld);

        destWorld.method_18211(player);
        ((ServerTeleportHelper)player).activateMethod_18783(destWorld);
        player.networkHandler.requestTeleport(player.x, player.y, player.z, player.yaw, player.pitch);
        player.interactionManager.setWorld(destWorld);
        sourceWorld.removePlayer(player);
        player.removed = false;
        player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
        playerManager.sendWorldInfo(player, destWorld);
        playerManager.method_14594(player);
        Iterator<StatusEffectInstance> statusEffectIter = player.getStatusEffects().iterator();

        while(statusEffectIter.hasNext()) {
            StatusEffectInstance statusEffectInstance_1 = (StatusEffectInstance)statusEffectIter.next();
            player.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(player.getEntityId(), statusEffectInstance_1));
        }


        player.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));

        //player.field_13978 = -1;
        //player.field_13997 = -1.0F;
        //player.field_13979 = -1;
        
        ((ServerTeleportHelper)player).setUnnamedFields(-1,-1.0f,-1);
        player.onTeleportationDone();
        return player;
    }


}
