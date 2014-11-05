package dmillerw.shield.core;

import java.util.List;

/**
 * @author dmillerw
 */
public interface IMaskProvider {

    public MaskedArea getMaskedArea();

    public boolean hasChanged();

    public void markAsUpdated();

    public int getDimensionID();

    /**
     *
     * @return Whether this should act on a whitelist, based on listed names. Returning false assumes blacklist
     */
    public boolean whitelist();

    public List<String> getListedNames();
}
