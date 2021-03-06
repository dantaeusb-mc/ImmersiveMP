package com.dantaeusb.immersivemp.locks.core;

import com.dantaeusb.immersivemp.ImmersiveMp;
import com.dantaeusb.immersivemp.locks.block.LockableDoorBlock;
import com.dantaeusb.immersivemp.locks.capability.canvastracker.CanvasServerTracker;
import com.dantaeusb.immersivemp.locks.capability.canvastracker.ICanvasTracker;
import com.dantaeusb.immersivemp.locks.client.gui.CanvasRenderer;
import com.dantaeusb.immersivemp.locks.network.packet.CLockDoorOpen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = ImmersiveMp.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModLockGameEvents {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if(!event.getWorld().isRemote || event.getPlayer().isDiscrete() || event.isCanceled() || event.getResult() == Result.DENY || event.getUseBlock() == Result.DENY) {
            return;
        }

        World world = event.getWorld();
        BlockPos pos = event.getPos();

        // Only when we're interacting with our doors
        if(world.getBlockState(pos).getBlock() instanceof LockableDoorBlock) {
            // Cancel both hands interactions, cause Quark utilizes main hand
            event.setCanceled(true);
            event.setResult(Result.DENY);

            // We, however, will consume & ignore event on main hand and proceed with OFF_HAND
            if (event.getHand() == Hand.MAIN_HAND) {
                return;
            }

            CLockDoorOpen doorPacket = new CLockDoorOpen(pos);
            ModLockNetwork.simpleChannel.sendToServer(doorPacket);
        }
    }

    @SubscribeEvent
    public static void onPlayerDisconnected(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerEntity player = event.getPlayer();
        CanvasServerTracker canvasTracker = (CanvasServerTracker) Helper.getWorldCanvasTracker(player.world);

        canvasTracker.stopTrackingAllCanvases(player.getUniqueID());
    }

    @SubscribeEvent
    public static void tickCanvasTracker(TickEvent.ServerTickEvent event) {
        CanvasServerTracker canvasTracker = (CanvasServerTracker) Helper.getWorldCanvasTracker(ServerLifecycleHooks.getCurrentServer().func_241755_D_());
        canvasTracker.tick();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderTickStart(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().world != null) {
            CanvasRenderer.getInstance().update(Util.milliTime());
        }
    }
}
