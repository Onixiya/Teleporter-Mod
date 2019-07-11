package net.onixiya.teleporter.mixin;

import net.onixiya.teleporter.interfaces.ServerTeleportHelper;
import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerTeleportHelper {

    public ServerPlayerEntityMixin(World world_1, GameProfile gameProfile_1) {
        super(world_1, gameProfile_1);
    }

    @Shadow
    private boolean inTeleportationState;

    @Override
    public void setIsTeleporting(Boolean value) {
        inTeleportationState = value;
    }


    
}