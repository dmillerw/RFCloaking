package dmillerw.cloak.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.cloak.RFCloaking;
import dmillerw.cloak.block.BlockMaskController;
import dmillerw.cloak.network.PacketClientUpdateMasks;
import dmillerw.cloak.network.PacketHandler;
import dmillerw.cloak.tile.TileMaskController;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

/**
 * @author dmillerw
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        RFCloaking.maskController = new BlockMaskController().setBlockName("mask_controller");
        GameRegistry.registerBlock(RFCloaking.maskController, "mask_controller");
        GameRegistry.registerTileEntity(TileMaskController.class, "RFShielding:mask_controller");

        PacketHandler.initialize();

        FMLCommonHandler.instance().bus().register(MaskHandler.INSTANCE);
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void handleClientPacket(PacketClientUpdateMasks packet) {
        // NOOP
    }

    public Block getBlock(Chunk chunk, int x, int y, int z) {
        return chunk.getBlock(x & 15, y, z & 15); // DON'T TOUCH!
    }

    public void addCollisionBoxesToList(Block block, World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        if (!(entity instanceof EntityPlayer)) {
            block.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        } else {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            List<IMaskProvider> maskProviders = MaskHandler.INSTANCE.getProvidersForDimension(entity.dimension);
            for (IMaskProvider maskProvider : maskProviders) {
                MaskedArea maskedArea = maskProvider.getMaskedArea();
                if (maskedArea != null && maskedArea.doCoordinatesExistWithin(x, y, z)) {
                    if (maskProvider.whitelist() && (maskProvider.getListedNames().contains(entityPlayer.getCommandSenderName())) ||
                        !maskProvider.whitelist() && !maskProvider.getListedNames().contains(entity.getCommandSenderName())) {
                        block.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
                        return;
                    }
                }
            }
        }
    }
}
