package com.dantaeusb.immersivemp.locks.capability.canvastracker;

import com.dantaeusb.immersivemp.ImmersiveMp;
import com.dantaeusb.immersivemp.locks.client.gui.CanvasRenderer;
import com.dantaeusb.immersivemp.locks.world.storage.CanvasData;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Map;

public class CanvasClientTracker extends CanvasDefaultTracker  {
    private final World world;
    Map<String, CanvasData> canvases = Maps.newHashMap();

    private final Map<String, Boolean> requestedCanvases = Maps.newHashMap();

    public CanvasClientTracker(World world) {
        this.world = world;
        ImmersiveMp.LOG.info("CanvasClientTracker");
    }

    @Nullable
    public CanvasData getCanvasData(String canvasName) {
        return this.canvases.get(canvasName);
    }


    @Override
    public void registerCanvasData(CanvasData canvasData) {
        this.canvases.put(canvasData.getName(), canvasData);
        CanvasRenderer.getInstance().updateCanvas(canvasData);
    }

    @Override
    public World getWorld() {
        return this.world;
    }
}
