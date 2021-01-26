package com.dantaeusb.immersivemp.locks.client.renderer.tileentity;

import com.dantaeusb.immersivemp.ImmersiveMp;
import com.dantaeusb.immersivemp.locks.block.EaselBlock;
import com.dantaeusb.immersivemp.locks.capability.canvastracker.ICanvasTracker;
import com.dantaeusb.immersivemp.locks.client.gui.CanvasRenderer;
import com.dantaeusb.immersivemp.locks.core.Helper;
import com.dantaeusb.immersivemp.locks.core.ModLockBlocks;
import com.dantaeusb.immersivemp.locks.item.CanvasItem;
import com.dantaeusb.immersivemp.locks.tileentity.EaselTileEntity;
import com.dantaeusb.immersivemp.locks.world.storage.CanvasData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EaselTileEntityRenderer extends TileEntityRenderer<EaselTileEntity> {
    private final ModelRenderer rack;
    private final ModelRenderer canvas;
    private final ModelRenderer topPlank;
    private final ModelRenderer backLeg;
    private final ModelRenderer frontLegs;

    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft:textures/block/spruce_planks.png");

    //private final DynamicTexture canvasTexture;
    /**
     * @see {@link net.minecraft.client.gui.MapItemRenderer}
     */
    //private final TextureManager textureManager;
    //private final RenderType canvasRenderType;

    public EaselTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);

        this.rack = new ModelRenderer(64, 64, 0, 0);
        this.rack.setRotationPoint(0.0F, 0, 0.0F);
        this.rack.setTextureOffset(0, 0).addBox(1.0F, 11.5F, 3.5F, 14.0F, 1.0F, 4.0F, 0.0F, false);

        this.canvas = new ModelRenderer(64, 64, 0, 0);
        this.canvas.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(this.canvas, 0.1745F, 0.0F, 0.0F);
        this.canvas.setTextureOffset(0, 0).addBox(0.0F, 12.0F, 3.0F, 16.0F, 18.0F, 1.0F, 0.0F, false);

        this.topPlank = new ModelRenderer(64, 64, 0, 0);
        this.topPlank.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(topPlank, 0.1745F, 0.0F, 0.0F);
        this.topPlank.setTextureOffset(0, 0).addBox(1.0F, 26.0F, 5.0F, 14.0F, 2.0F, 1.0F, 0.0F, false);

        this.backLeg = new ModelRenderer(64, 64, 0, 0);
        this.backLeg.setRotationPoint(0.0F, 0.0F, 15.0F);
        setRotationAngle(backLeg, -0.2182F, 0.0F, 0.0F);
        this.backLeg.setTextureOffset(0, 0).addBox(7.0F, 0.0F, 0.0F, 2.0F, 30.0F, 1.0F, 0.0F, false);

        this.frontLegs = new ModelRenderer(64, 64, 0, 0);
        this.frontLegs.setRotationPoint(0.0F, 0.0F, -3.0F);
        setRotationAngle(frontLegs, 0.1745F, 0.0F, 0.0F);
        this.frontLegs.setTextureOffset(0, 0).addBox(12.0F, 0.0F, 7.0F, 2.0F, 30.0F, 1.0F, 0.0F, false);
        this.frontLegs.setTextureOffset(0, 0).addBox(2.0F, 0.0F, 7.0F, 2.0F, 30.0F, 1.0F, 0.0F, false);

        //this.canvasTexture = new DynamicTexture(CanvasItem.CANVAS_SIZE, CanvasItem.CANVAS_SIZE, true);
        //this.textureManager = Minecraft.getInstance().getTextureManager();
        //this.canvasRenderType = this.textureManager.getDynamicTextureLocation("canvas/" + mapdataIn.getName(), this.canvasTexture);
    }

    /**
     * @todo: replace with TE packet
     */
    private boolean stoopidUpdate = false;

    public void render(EaselTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        World world = tileEntity.getWorld();
        boolean flag = world != null;
        BlockState blockState = flag ? tileEntity.getBlockState() : ModLockBlocks.EASEL.getDefaultState().with(EaselBlock.FACING, Direction.SOUTH);

        IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(RenderType.getEntityCutout(TEXTURE));

        matrixStack.push();

        float facingAngle = blockState.get(ChestBlock.FACING).getHorizontalAngle();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-facingAngle));
        matrixStack.translate(-0.5D, -0.5D, -0.5D);

        rack.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay);
        topPlank.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay);
        backLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay);
        frontLegs.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay);
        canvas.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay);

        if (tileEntity.hasCanvas()) {
            CanvasData canvasData = getCanvasData(world, tileEntity.getCanvasName());

            if (canvasData != null) {
                /**
                 * Copied from {@link net.minecraft.client.renderer.entity.ItemFrameRenderer#render}
                 */

                final float scaleFactor = 1.0F / 16.0F;

                // Scale and prepare
                matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
                matrixStack.translate(0.0D, 12.5D, 5.0D);
                matrixStack.rotate(Vector3f.XP.rotation(0.1745F));

                matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                matrixStack.translate(-16.0D, -16.0D, 0.0D);
                matrixStack.translate(0.0D, 0.0D, 0.1D);

                CanvasRenderer.getInstance().renderCanvas(matrixStack, renderTypeBuffer, canvasData, combinedLight);
            } else {
                CanvasRenderer.getInstance().requestCanvasTexture(tileEntity.getCanvasName());
            }
        }

        matrixStack.pop();
    }

    public static void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Nullable
    public static CanvasData getCanvasData(World world, String canvasName) {
        ICanvasTracker canvasTracker = Helper.getWorldCanvasTracker(world);

        if (canvasTracker == null) {
            return null;
        }

        return canvasTracker.getCanvasData(canvasName);
    }
}
