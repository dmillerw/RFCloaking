package dmillerw.shield.core;

import dmillerw.shield.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.EnumSkyBlock;

/**
 * @author dmillerw
 */
public class MaskedArea {

    public static MaskedArea fromByteBuffer(ByteBuf byteBuf) {
        int x1 = PacketHandler.readVarIntFromBuffer(byteBuf);
        int y1 = PacketHandler.readVarIntFromBuffer(byteBuf);
        int z1 = PacketHandler.readVarIntFromBuffer(byteBuf);
        int x2 = PacketHandler.readVarIntFromBuffer(byteBuf);
        int y2 = PacketHandler.readVarIntFromBuffer(byteBuf);
        int z2 = PacketHandler.readVarIntFromBuffer(byteBuf);
        return new MaskedArea(x1, y1, z1, x2, y2, z2);
    }

    public final int x1;
    public final int y1;
    public final int z1;
    public final int x2;
    public final int y2;
    public final int z2;

    public MaskedArea(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public boolean doCoordinatesExistWithin(int x, int y, int z) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public void updateContainingChunks() {
        Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
        for (int x=x1; x<=x2; x++) {
            for (int y=y1; y<=y2; y++) {
                for (int z=z1; z<=z2; z++) {
                    Minecraft.getMinecraft().theWorld.updateLightByType(EnumSkyBlock.Block, x, y, z);
                    Minecraft.getMinecraft().theWorld.updateLightByType(EnumSkyBlock.Sky, x, y, z);
                }
            }
        }
    }

    public void toByteBuffer(ByteBuf byteBuf) {
        PacketHandler.writeVarIntToBuffer(byteBuf, x1);
        PacketHandler.writeVarIntToBuffer(byteBuf, y1);
        PacketHandler.writeVarIntToBuffer(byteBuf, z1);
        PacketHandler.writeVarIntToBuffer(byteBuf, x2);
        PacketHandler.writeVarIntToBuffer(byteBuf, y2);
        PacketHandler.writeVarIntToBuffer(byteBuf, z2);
    }
}
