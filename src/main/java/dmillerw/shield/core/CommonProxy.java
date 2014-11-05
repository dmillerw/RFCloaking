package dmillerw.shield.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.shield.RFShielding;
import dmillerw.shield.block.BlockMaskController;
import dmillerw.shield.network.PacketClientUpdateMasks;
import dmillerw.shield.network.PacketHandler;
import dmillerw.shield.tile.TileMaskController;

/**
 * @author dmillerw
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        RFShielding.maskController = new BlockMaskController().setBlockName("mask_controller");
        GameRegistry.registerBlock(RFShielding.maskController, "mask_controller");
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
}
