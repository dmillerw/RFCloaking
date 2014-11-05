package dmillerw.shield;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.shield.core.CommonProxy;
import net.minecraft.block.Block;

/**
 * @author dmillerw
 */
@Mod(modid = "RFShielding", name = "RFShielding", version = "%MOD_VERSION%")
public class RFShielding {

    @Mod.Instance
    public static RFShielding instance;

    @SidedProxy(serverSide = "dmillerw.shield.core.CommonProxy", clientSide = "dmillerw.shield.client.ClientProxy")
    public static CommonProxy proxy;

    public static Block maskController;
    public static Block maskPylon;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
}
