package dmillerw.shield.block;

import dmillerw.shield.tile.TileMaskController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class BlockMaskController extends BlockContainer {

    public BlockMaskController() {
        super(Material.iron);

        setHardness(2F);
        setResistance(2F);
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            TileMaskController tileMaskController = (TileMaskController) world.getTileEntity(x, y, z);
            if (tileMaskController != null)
                tileMaskController.onNeighborChanged();
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileMaskController();
    }
}
