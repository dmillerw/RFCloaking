package dmillerw.shield.asm;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.shield.client.ClientMaskHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * @author dmillerw
 */
public class StaticMethodHandler {

    private static final MovingObjectPosition NULL_MOB = new MovingObjectPosition(0, 0, 0, 0, Vec3.createVectorHelper(0, 0, 0), false);

    private static EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @SideOnly(Side.CLIENT)
    public static boolean shouldRenderBlock(Block block, int pass, World world, int x, int y, int z) {
        return !hidden(getPlayer(), world, x, y, z) && block.canRenderInPass(pass);
    }

    @SideOnly(Side.CLIENT)
    public static boolean shouldRenderTile(TileEntity tileEntity, World world, int x, int y, int z) {
        return !hidden(getPlayer(), world, x, y, z) && TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileEntity);
    }

    @SideOnly(Side.CLIENT)
    public static boolean shouldSideBeRendered(Block block, IBlockAccess world, int x, int y, int z, int side) {
        ForgeDirection forgeDirection = ForgeDirection.getOrientation(side);
        return hidden(getPlayer(), Minecraft.getMinecraft().theWorld, x + forgeDirection.offsetX, y + forgeDirection.offsetY, z + forgeDirection.offsetZ) || block.shouldSideBeRendered(world, x, y, z, side);
    }

    @SideOnly(Side.CLIENT)
    public static MovingObjectPosition raytraceViaVec(World world, Vec3 vec1, Vec3 vec2, boolean holdingBoat, boolean mustCollide, boolean canHitFluid) {
        MovingObjectPosition movingObjectPosition = world.func_147447_a(vec1, vec2, holdingBoat, mustCollide, canHitFluid);
        if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && hidden(getPlayer(), world, movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ))
            return NULL_MOB;
        else
            return movingObjectPosition;
    }

    @SideOnly(Side.CLIENT)
    public static int getLightOpacity(Block block, World world, int x, int y, int z) {
        return hidden(getPlayer(), world, x, y, z) ? 0 : block.getLightOpacity(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public static boolean isBlockNormalCube(World world, int x, int y, int z) {
        return hidden(getPlayer(), world, x, y, z) ? false : world.getBlock(x, y, z).isNormalCube();
    }

    public static void addCollisionBoxesToList(Block block, World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity) {
        if (!(entity instanceof EntityPlayer) || !hidden((EntityPlayer) entity, world, x, y, z)) block.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
    }

    private static boolean hidden(EntityPlayer entityPlayer, World world, int x, int y, int z) {
        return ClientMaskHandler.INSTANCE.isCoordinateMasked(x, y, z);
    }
}
