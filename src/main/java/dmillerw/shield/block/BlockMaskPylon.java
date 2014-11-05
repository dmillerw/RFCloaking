package dmillerw.shield.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * @author dmillerw
 */
public class BlockMaskPylon extends Block {

    public BlockMaskPylon() {
        super(Material.iron);

        setHardness(2F);
        setResistance(2F);
        setCreativeTab(CreativeTabs.tabRedstone);
    }
}
