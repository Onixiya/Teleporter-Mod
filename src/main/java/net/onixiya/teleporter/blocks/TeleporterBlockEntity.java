package net.onixiya.teleporter.blocks;

import net.onixiya.teleporter.TeleporterMod;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class TeleporterBlockEntity extends BlockEntity {

    private int LockableInvetory = 0;

    public TeleporterBlockEntity() {
       super(TeleporterMod.TELEPORTER_ENTITY);
    }

    public CompoundTag toTag(CompoundTag tag) {
       super.toTag(tag);
  
       tag.putInt("LockableInvetory", LockableInvetory);

      return tag;
   }

   public void fromTag(CompoundTag tag) {
      super.fromTag(tag);
      LockableInvetory = tag.getInt("LockableInvetory");
     }
}