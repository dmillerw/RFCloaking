package dmillerw.shield.tile;

import dmillerw.shield.core.IMaskProvider;
import dmillerw.shield.core.MaskHandler;
import dmillerw.shield.core.MaskedArea;
import net.minecraft.tileentity.TileEntity;

/**
 * @author dmillerw
 */
public class TileMaskController extends TileEntity implements IMaskProvider {

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
    public String[] getListedNames() {
        return new String[0];
    }
}
