package dmillerw.cloak.tile;

import com.google.common.collect.Lists;
import dmillerw.cloak.core.IMaskProvider;
import dmillerw.cloak.core.MaskHandler;
import dmillerw.cloak.core.MaskedArea;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

/**
 * @author dmillerw
 */
public class TileMaskController extends TileEntity implements IMaskProvider {

    private static final List<String> EMPTY_LIST = Lists.newArrayList();

    private MaskedArea maskedArea;

    private boolean hasChanged = false;
    private boolean initialized;

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote && !initialized) {
            MaskHandler.INSTANCE.registerMaskProvider(this);
            initialized = true;
        }
    }

    public void onNeighborChanged() {
        if (!worldObj.isRemote) {
            if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) && maskedArea == null) {
                maskedArea = new MaskedArea(xCoord - 8, yCoord + 1, zCoord - 8, xCoord + 8, yCoord + 16, zCoord + 8);
                hasChanged = true;
            } else if (maskedArea != null) {
                maskedArea = null;
                hasChanged = true;
            }
        }
    }

    public void onBlockBroken() {
        if (!worldObj.isRemote && initialized) {
            MaskHandler.INSTANCE.removeMaskProvider(this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!worldObj.isRemote && initialized) {
            MaskHandler.INSTANCE.removeMaskProvider(this);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (!worldObj.isRemote && initialized) {
            MaskHandler.INSTANCE.removeMaskProvider(this);
        }
    }

    /* IMASKPROVIDER */
    @Override
    public MaskedArea getMaskedArea() {
        return maskedArea;
    }

    @Override
    public boolean hasChanged() {
        return hasChanged;
    }

    @Override
    public void markAsUpdated() {
        hasChanged = false;
    }

    @Override
    public int getDimensionID() {
        return worldObj.provider.dimensionId;
    }

    @Override
    public boolean whitelist() {
        return false;
    }

    @Override
    public List<String> getListedNames() {
        return EMPTY_LIST;
    }
}
