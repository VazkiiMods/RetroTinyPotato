package violet_moon.retrotinypotato;

import violet_moon.retrotinypotato.proxy.CommonProxy;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import java.io.File;
import java.util.logging.Logger;

@Mod(modid = TinyPotatoMod.MODID, useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class TinyPotatoMod {
	public static final String NAME = "Tiny Potato";
	public static final String MODID = "retrotinypotato";
	
	public static final Logger LOGGER = Logger.getLogger(NAME);
	
	@SidedProxy(clientSide = "violet_moon.retrotinypotato.proxy.ClientProxy", serverSide = "violet_moon.retrotinypotato.proxy.CommonProxy")
	public static CommonProxy proxy;
	@Mod.Instance
	public static TinyPotatoMod instance;
	public static Configuration config;
	
	public static BlockTinyPotato blockTinyPotato;

	public static ItemBlock itemBlockTinyPotato;

	@Mod.PreInit
	public void preinit(FMLPreInitializationEvent pre) {
		LOGGER.setParent(FMLLog.getLogger());

		config = new Configuration(new File(pre.getModConfigurationDirectory(), "retrotinypotato.conf"));
		try {
			config.load();
			blockTinyPotato = new BlockTinyPotato(config.getBlock("tinyPotato.id", 730).getInt());
		} finally {
			config.save();
		}
		
		itemBlockTinyPotato = new ItemBlock(blockTinyPotato.blockID - 256);
		GameRegistry.registerTileEntity(TileTinyPotato.class, "tinyPotato");
		LanguageRegistry.instance().loadLocalization("/lang/tinypotato/en_US.properties", "en_US", false);
	}
	
	@Mod.Init
	public void init(FMLInitializationEvent e) {
		proxy.clientInit();
		GameRegistry.addShapedRecipe(new ItemStack(itemBlockTinyPotato, 6),
			"pp ",
			"pp ",
			"pp ",
			'p', Item.potato);
	}
}
