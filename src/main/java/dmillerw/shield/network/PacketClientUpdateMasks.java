package dmillerw.shield.network;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.shield.client.ClientMaskHandler;
import dmillerw.shield.core.MaskedArea;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * @author dmillerw
 */
public class PacketClientUpdateMasks implements IMessage, IMessageHandler<PacketClientUpdateMasks, IMessage> {

    public List<MaskedArea> maskedAreaList = Lists.newArrayList();

    @Override
    public void toBytes(ByteBuf buf) {
        PacketHandler.writeVarIntToBuffer(buf, maskedAreaList.size());
        for (MaskedArea maskedArea : maskedAreaList) {
            maskedArea.toByteBuffer(buf);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        maskedAreaList = Lists.newArrayList();
        int size = PacketHandler.readVarIntFromBuffer(buf);
        for (int i=0; i<size; i++) {
            maskedAreaList.add(MaskedArea.fromByteBuffer(buf));
        }
    }

    @Override
    public IMessage onMessage(PacketClientUpdateMasks message, MessageContext ctx) {
        ClientMaskHandler.INSTANCE.onMaskPacket(message);
        return null;
    }
}
