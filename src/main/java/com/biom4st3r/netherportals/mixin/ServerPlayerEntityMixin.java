package com.biom4st3r.netherportals.mixin;

import com.biom4st3r.netherportals.interfaces.ServerTeleportHelper;
import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerTeleportHelper {

    public ServerPlayerEntityMixin(World world_1, GameProfile gameProfile_1) {
        super(world_1, gameProfile_1);
    }

    @Shadow
    private int field_13978;

    @Shadow
    private void method_18783(ServerWorld serverWorld_1) {

    }

    @Shadow
    private float field_13997;

    @Shadow
    private int field_13979;

    @Shadow
    private boolean inTeleportationState;

    @Override
    public void setIsTeleporting(Boolean value) {
        inTeleportationState = value;
    }

    @Override
    public void setUnnamedFields(int int1, float float1, int int2) {
        field_13978 = int1;
        field_13997 = float1;
        field_13979 = int2;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public void activateMethod_18783(ServerWorld sw) {
        method_18783(sw);
    }


    
}