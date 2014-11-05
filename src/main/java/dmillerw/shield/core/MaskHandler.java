package dmillerw.shield.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.shield.network.PacketClientUpdateMasks;
import dmillerw.shield.network.PacketHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author dmillerw
 */
public class MaskHandler {

    public static final MaskHandler INSTANCE = new MaskHandler();

    private List<IMaskProvider> maskProviders = Lists.newArrayList();

    public void registerMaskProvider(IMaskProvider maskProvider) {
        maskProviders.add(maskProvider);
        markDirty(maskProviders, maskProvider.getDimensionID());
    }

    public void removeMaskProvider(IMaskProvider maskProvider) {
        maskProviders.remove(maskProvider);
        markDirty(maskProviders, maskProvider.getDimensionID());
    }

    public List<IMaskProvider> getProvidersForDimension(int dimension) {
        if (dimension == Integer.MIN_VALUE) {
            return maskProviders;
        } else {
            List<IMaskProvider> temp = Lists.newArrayList();
            for (IMaskProvider maskProvider : maskProviders) {
                if (maskProvider.getDimensionID() == dimension)
                    temp.add(maskProvider);
            }
            return temp;
        }
    }

    private Map<Integer, List<MaskedArea>> collectAndSort() {
        return collectAndSort(maskProviders);
    }

    private Map<Integer, List<MaskedArea>> collectAndSort(Collection<IMaskProvider> maskProviders) {
        Map<Integer, List<MaskedArea>> dimensionSortedMap = Maps.newHashMap();
        for (IMaskProvider maskProvider : maskProviders) {
            int dimension = maskProvider.getDimensionID();
            if (!dimensionSortedMap.containsKey(dimension)) {
                dimensionSortedMap.put(dimension, new ArrayList<MaskedArea>());
            }
            List<MaskedArea> list = dimensionSortedMap.get(dimension);
            MaskedArea maskedArea = maskProvider.getMaskedArea();
            if (maskedArea != null)
                list.add(maskedArea);

            dimensionSortedMap.put(dimension, list);
        }
        return dimensionSortedMap;
    }

    private void markDirty(Collection<IMaskProvider> collection, int dimension) {
        Map<Integer, List<MaskedArea>> dimensionSortedMap = collectAndSort(collection);
        if (dimension == Integer.MIN_VALUE) {
            for (int d : dimensionSortedMap.keySet()) {
                PacketClientUpdateMasks packetClientUpdateMasks = new PacketClientUpdateMasks();
                packetClientUpdateMasks.maskedAreaList = dimensionSortedMap.get(d);
                PacketHandler.INSTANCE.sendToDimension(packetClientUpdateMasks, d);
            }
        } else {
            PacketClientUpdateMasks packetClientUpdateMasks = new PacketClientUpdateMasks();
            packetClientUpdateMasks.maskedAreaList = dimensionSortedMap.get(dimension);
            PacketHandler.INSTANCE.sendToDimension(packetClientUpdateMasks, dimension);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        List<IMaskProvider> toUpdate = Lists.newArrayList();
        for (IMaskProvider maskProvider : maskProviders) {
            if (maskProvider.hasChanged()) {
                toUpdate.add(maskProvider);
                maskProvider.markAsUpdated();
            }
        }
        if (!toUpdate.isEmpty())
            markDirty(toUpdate, Integer.MIN_VALUE);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        markDirty(maskProviders, event.player.dimension);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        markDirty(maskProviders, event.toDim);
    }
}
