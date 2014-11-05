package dmillerw.cloak.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

/**
 * @author dmillerw
 */
public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("RFShielding");

    public static void initialize() {
        INSTANCE.registerMessage(PacketClientUpdateMasks.class, PacketClientUpdateMasks.class, 0, Side.CLIENT);
    }

    public static int readVarIntFromBuffer(ByteBuf byteBuf) {
        int i = 0;
        int j = 0;
        byte b0;

        do {
            b0 = byteBuf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        }
        while ((b0 & 128) == 128);

        return i;
    }

    public static void writeVarIntToBuffer(ByteBuf byteBuf, int value) {
        while ((value & -128) != 0) {
            byteBuf.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        byteBuf.writeByte(value);
    }
}
