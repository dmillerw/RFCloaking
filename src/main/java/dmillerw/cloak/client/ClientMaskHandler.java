package dmillerw.cloak.client;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import dmillerw.cloak.core.MaskedArea;
import dmillerw.cloak.network.PacketClientUpdateMasks;

import java.util.List;

/**
 * @author dmillerw
 */
public class ClientMaskHandler {

    public static final ClientMaskHandler INSTANCE = new ClientMaskHandler();

    private List<MaskedArea> maskedAreaList = Lists.newArrayList();

    public boolean isCoordinateMasked(int x, int y, int z) {
        for (MaskedArea maskedArea : maskedAreaList) {
            if (maskedArea.doCoordinatesExistWithin(x, y, z))
                return true;
        }
        return false;
    }

    public void onMaskPacket(PacketClientUpdateMasks packet) {
        maskedAreaList.clear();
        maskedAreaList.addAll(packet.maskedAreaList);
        for (MaskedArea maskedArea : maskedAreaList) {
            maskedArea.updateContainingChunks();
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        maskedAreaList.clear();
    }
}
