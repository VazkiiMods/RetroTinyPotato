package violet_moon.retrotinypotato.proxy;

import violet_moon.retrotinypotato.TileTinyPotato;
import violet_moon.retrotinypotato.render.RenderTileTinyPotato;
import violet_moon.retrotinypotato.render.RenderTinyPotato;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void clientInit() {
		tinyPotatoRenderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderTinyPotato());
		ClientRegistry.bindTileEntitySpecialRenderer(TileTinyPotato.class, new RenderTileTinyPotato());
	}
}
