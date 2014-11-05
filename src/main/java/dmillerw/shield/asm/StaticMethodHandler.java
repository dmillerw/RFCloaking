package dmillerw.shield.asm;

import dmillerw.shield.RFShielding;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

/**
 * @author dmillerw
 */
public class StaticMethodHandler {

    public static Block getBlock(Chunk chunk, int x, int y, int z) {
        return RFShielding.proxy.getBlock(chunk, x, y, z);
    }

    public static void addCollisionBoxesToList(Block block, World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        RFShielding.proxy.addCollisionBoxesToList(block, world, x, y, z, mask, list, entity);
    }
}
