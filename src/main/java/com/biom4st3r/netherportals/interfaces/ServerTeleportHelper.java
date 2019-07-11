package com.biom4st3r.netherportals.interfaces;

import net.minecraft.server.world.ServerWorld;

public interface ServerTeleportHelper
{
    public void setIsTeleporting(Boolean value);
 
    public void setUnnamedFields(int int1,float float1,int int2);

    public void activateMethod_18783(ServerWorld sw);
}