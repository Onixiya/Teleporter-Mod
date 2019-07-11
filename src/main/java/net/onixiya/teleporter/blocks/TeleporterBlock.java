package net.onixiya.teleporter.blocks;

import java.util.Random;

import net.onixiya.teleporter.TeleporterMod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TeleporterBlock extends Block {

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
        for (int i = 0; i < 5; i++)
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

        // EnchantingTableBlock
        // world_1.addParticle(ParticleTypes.ENCHANT, (double)blockPos_1.getX() + 0.5D,
        // (double)blockPos_1.getY() + 2.0D, (double)blockPos_1.getZ() + 0.5D,
        // (double)((float)int_1 + random_1.nextFloat()) - 0.5D, (double)((float)int_3 -
        // random_1.nextFloat() - 1.0F), (double)((float)int_2 + random_1.nextFloat()) -
        // 0.5D);
    }

    @Override
    public boolean activate(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1,
            Hand hand_1, BlockHitResult blockHitResult_1) {

        if (world_1.isClient)
            TeleporterMod.openTeleporterGUI(blockPos_1);
        return true;
    }

    @Override
    public void onPlaced(World world_1, BlockPos blockPos_1, BlockState blockState_1, LivingEntity livingEntity_1,
            ItemStack itemStack_1) {
        // super.onPlaced(world_1, blockPos_1, blockState_1, livingEntity_1,
        // itemStack_1);

    }

    @Override
    public boolean isOpaque(BlockState blockState_1) {
        return false;
    }

    @Override
    public boolean isTranslucent(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return true;
    }
}