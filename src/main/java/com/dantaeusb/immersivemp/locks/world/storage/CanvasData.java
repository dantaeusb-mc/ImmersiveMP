package com.dantaeusb.immersivemp.locks.world.storage;

import com.dantaeusb.immersivemp.ImmersiveMp;
import com.dantaeusb.immersivemp.locks.item.CanvasItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CanvasData extends WorldSavedData {
    private byte[] color;
    private ByteBuffer canvasBuffer;

    private int width;
    private int height;

    public static final String NAME_PREFIX = ImmersiveMp.MOD_ID + "_canvas_";
    private static final String NBT_TAG_NAME_WIDTH = "width";
    private static final String NBT_TAG_NAME_HEIGHT = "height";
    private static final String NBT_TAG_NAME_COLOR = "color";

    public CanvasData(String canvasName) {
        super(canvasName);
    }

    public CanvasData(int canvasId) {
        super(getCanvasName(canvasId));
    }

    public static String getCanvasName(int canvasId) {
        return NAME_PREFIX + canvasId;
    }

    public void initData(int width, int height) {
        byte[] defaultColor = new byte[width * height * 4];
        ByteBuffer defaultColorBuffer = ByteBuffer.wrap(defaultColor);

        for (int x = 0; x < width * height; x++) {
            defaultColorBuffer.putInt(x * 4, 0xFF000000);
        }

        this.initData(width, height, defaultColor);
    }

    public void initData(int width, int height, byte[] color) {
        this.width = width;
        this.height = height;
        this.updateColorData(color);
        this.markDirty();
    }

    /**
     * Mostly to be used on server - update whole canvas and rewrap
     */
    public void updateCanvas(byte[] color) {
        this.updateColorData(color);
        this.markDirty();
    }

    private void updateColorData(byte[] color) {
        this.color = color;
        this.canvasBuffer = ByteBuffer.wrap(this.color);
        this.canvasBuffer.order(ByteOrder.BIG_ENDIAN);
    }

    public void updateCanvasPixel(int pixelX, int pixelY, int color) {
        this.canvasBuffer.putInt(this.getColorAt(pixelX, pixelY) * 4, color);
        this.markDirty();
    }

    public void updateCanvasPixel(int index, int color) {
        this.canvasBuffer.putInt(index * 4, color);
        this.markDirty();
    }

    public int getColorAt(int pixelX, int pixelY) {
        return this.getColorAt(this.getPixelIndex(pixelX, pixelY));
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ByteBuffer getColorDataBuffer() {
        this.canvasBuffer.rewind();
        return this.canvasBuffer.asReadOnlyBuffer();
    }

    /**
     *
     * @param index Integer index, not byte index
     * @return
     */
    public int getColorAt(int index) {
        return this.canvasBuffer.getInt(index * 4);
    }

    /**
     * This is integer index, not byte index!
     * @param pixelX
     * @param pixelY
     * @return
     */
    public int getPixelIndex(int pixelX, int pixelY) {
        pixelX = MathHelper.clamp(pixelX, 0, this.width - 1);
        pixelY = MathHelper.clamp(pixelY, 0, this.height - 1);

        return pixelY * this.width + pixelX;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void read(CompoundNBT nbt) {
        this.width = nbt.getInt(NBT_TAG_NAME_WIDTH);
        this.height = nbt.getInt(NBT_TAG_NAME_HEIGHT);
        this.updateColorData(nbt.getByteArray(NBT_TAG_NAME_COLOR));
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt(NBT_TAG_NAME_WIDTH, this.width);
        compound.putInt(NBT_TAG_NAME_HEIGHT, this.height);
        compound.putByteArray(NBT_TAG_NAME_COLOR, this.color);

        return compound;
    }
}

