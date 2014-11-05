package dmillerw.cloak;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.cloak.core.CommonProxy;
import net.minecraft.block.Block;

/**
 * @author dmillerw
 */
@Mod(modid = "RFCloaking", name = "RFCloaking", version = "%MOD_VERSION%")
public class RFCloaking {

    @Mod.Instance("RFCloaking")
    public static RFCloaking instance;

    @SidedProxy(serverSide = "dmillerw.cloak.core.CommonProxy", clientSide = "dmillerw.cloak.client.ClientProxy")
    public static CommonProxy proxy;

    public static Block maskController;
    public static Block maskPylon;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
}
