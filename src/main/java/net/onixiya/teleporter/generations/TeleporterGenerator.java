package net.onixiya.teleporter.generations;

import java.util.List;
import java.util.Random;

import net.onixiya.teleporter.TeleporterMod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;

public class TeleporterGenerator {
    public static Identifier TeleporterFeature = new Identifier(TeleporterMod.MODID,"telepiece");

    public static final String TEMPLATE_TAG = "Template"; 
    public static final String ROTATION_TAG = "Rot"; 

    public static void addPart(StructureManager sm, BlockPos blockpos, BlockRotation rotation,
            List<StructurePiece> listPieces, Random random, DefaultFeatureConfig featureconfig) {
        listPieces.add(new TeleporterGenerator.Piece(sm, TeleporterFeature, blockpos, BlockRotation.NONE, 0));
        //EndCityGenerator
    }

    public static class Piece extends SimpleStructurePiece {
        private final Identifier template;
        private final BlockRotation rotation;

        public Piece(StructureManager structureManager, Identifier identifier, BlockPos blockPos,BlockRotation rotation, int offset) 
        {
            super(TeleporterMod.TeleporterPieceType, 0);

            this.pos = blockPos;
            this.rotation = rotation;
            this.template = identifier;

            this.setStructureData(structureManager);
        }

        public Piece(StructureManager sm, CompoundTag tag)
        {
            super(TeleporterMod.TeleporterPieceType,tag);
            this.template = new Identifier(tag.getString(TEMPLATE_TAG));
            this.rotation = BlockRotation.valueOf(tag.getString(ROTATION_TAG));
            this.setStructureData(sm);
        }

        @Override
        protected void toNbt(CompoundTag compoundTag_1) {
            super.toNbt(compoundTag_1);
            compoundTag_1.putString(TEMPLATE_TAG, this.template.toString());
            compoundTag_1.putString(ROTATION_TAG, BlockRotation.NONE.name());
        }

        public void setStructureData(StructureManager sm) {
            Structure struct = sm.getStructure(this.template);
            System.out.println("Structure: " + struct == null);

            StructurePlacementData spd = (new StructurePlacementData()).setRotation(rotation)
                    .setMirrored(BlockMirror.NONE).setPosition(this.pos)
                    .addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
            System.out.println("StructurePLacementData: " + spd == null);
            this.setStructureData(struct, this.pos, spd);
        }

        @Override
        protected void handleMetadata(String arg0, BlockPos arg1, IWorld arg2, Random arg3,
                MutableIntBoundingBox arg4) {

        }

        @Override
        public boolean generate(IWorld iWorld_1, Random random_1, MutableIntBoundingBox mutableIntBoundingBox_1,
                ChunkPos chunkPos_1) {
            //int yHeight = iWorld_1.getTop(Type.WORLD_SURFACE_WG, this.pos.getX()+8, this.pos.getZ() + 8);
            int yHeight = iWorld_1.getTop(Type.WORLD_SURFACE_WG, this.pos.getX(), this.pos.getZ())-89;
            this.pos = this.pos.add(0,yHeight-1,0);
            System.out.println(String.format("%s %s %s", pos.getX(), pos.getY(), pos.getZ()));
            
            return super.generate(iWorld_1, random_1, mutableIntBoundingBox_1, chunkPos_1);
        }
    }
}

