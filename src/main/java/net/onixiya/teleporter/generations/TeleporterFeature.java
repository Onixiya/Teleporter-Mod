package net.onixiya.teleporter.generations;

import net.onixiya.teleporter.TeleporterMod;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class TeleporterFeature extends AbstractTempleFeature<DefaultFeatureConfig>
{
    public TeleporterFeature()
    {
        super(DefaultFeatureConfig::deserialize);
    }

    @Override
    protected int getSeedModifier() {
        return 0;
    }

    @Override
    public String getName() {
        return "Teleporterpiece";
    }

    @Override
    public int getRadius() {
        return 5;
    }

    @Override
    public StructureStartFactory getStructureStartFactory() {
        return TeleporterStart::new;
    }

    public static class TeleporterStart extends StructureStart
    {
        public TeleporterStart(StructureFeature<?> structureFeature_1, int int_1, int int_2, Biome biome_1,
                MutableIntBoundingBox mutableIntBoundingBox_1, int int_3, long long_1) {
            super(structureFeature_1, int_1, int_2, biome_1, mutableIntBoundingBox_1, int_3, long_1);
            
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structManager, int chunkX, int chunkZ, Biome biome) {
            
            DefaultFeatureConfig defaultFeatureConfig = chunkGenerator.getStructureConfig(biome, TeleporterMod.TeleporterFeature);
            int x = chunkX*16;
            int z = chunkZ*16;
            
            BlockPos startingPos = new BlockPos(x, 90, z);
            System.out.println(String.format("%s %s %s", startingPos.getX(), startingPos.getY(), startingPos.getZ()));
            BlockRotation rotation = BlockRotation.NONE;//BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
            
            TeleporterGenerator.addPart(structManager, startingPos, rotation, this.children, random, defaultFeatureConfig);
            this.setBoundingBoxFromChildren();
        }
        
    }



}