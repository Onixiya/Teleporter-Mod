package net.onixiya.teleporter.blocks;

import java.util.Random;

import net.onixiya.teleporter.TeleporterMod;
import net.onixiya.teleporter.blocks.TeleporterBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TeleporterBlock extends Block implements BlockEntityProvider {

    public TeleporterBlock() {
        super(FabricBlockSettings.copy(Blocks.QUARTZ_BLOCK).build());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState_1, World world_1, BlockPos blockPos, Random random) {
        for (int i = 0; i < 5; i++) {
            switch (random.nextInt() % 4) {

            case 0:
                world_1.addParticle(ParticleTypes.PORTAL, blockPos.getX() + random.nextFloat(), blockPos.getY() - 0.2,
                        blockPos.getZ(), 0, random.nextFloat() - 0.5D, 0);
                break;
            case 1:
                world_1.addParticle(ParticleTypes.PORTAL, blockPos.getX(), blockPos.getY() - 0.2,
                        blockPos.getZ() + random.nextFloat(), 0, random.nextFloat() - 0.5D, 0);
                break;
            case 2:
                world_1.addParticle(ParticleTypes.PORTAL, blockPos.getX() + 1, blockPos.getY() - 0.2,
                        blockPos.getZ() + random.nextFloat(), 0, random.nextFloat() - 0.5D, 0);
                break;
            case 3:
                world_1.addParticle(ParticleTypes.PORTAL, blockPos.getX() + random.nextFloat(), blockPos.getY() - 0.2,
                        blockPos.getZ() + 1, 0, random.nextFloat() - 0.5D, 0);
                break;
        }
    }
}

    @Override
    public boolean activate(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1,
            Hand hand_1, BlockHitResult blockHitResult_1) {

        if (TeleporterBlockEntity.LockableInvetory == 0) {
            TeleporterMod.openTeleporterGUI(blockPos_1);
        return true;}
        return false;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView BlockView) {
        return new TeleporterBlockEntity();
        }
}