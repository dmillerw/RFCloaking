package dmillerw.cloak.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.cloak.core.CommonProxy;
import dmillerw.cloak.network.PacketClientUpdateMasks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        FMLCommonHandler.instance().bus().register(ClientMaskHandler.INSTANCE);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void handleClientPacket(PacketClientUpdateMasks packet) {
        ClientMaskHandler.INSTANCE.onMaskPacket(packet);
    }

    @Override
    public Block getBlock(Chunk chunk, int x, int y, int z) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            return super.getBlock(chunk, x, y, z);
        } else {
            Block block = chunk.getBlock(x & 15, y, z & 15);
            return ClientMaskHandler.INSTANCE.isCoordinateMasked(x, y, z) ? Blocks.air : block;
        }
    }

    @Override
    public void addCollisionBoxesToList(Block block, World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            super.addCollisionBoxesToList(block, world, x, y, z, mask, list, entity);
        } else {
            if (!ClientMaskHandler.INSTANCE.isCoordinateMasked(x, y, z))
                super.addCollisionBoxesToList(block, world, x, y, z, mask, list, entity);
        }
    }
}
