package violet_moon.retrotinypotato.render;

import violet_moon.retrotinypotato.TileTinyPotato;
import violet_moon.retrotinypotato.TinyPotatoMod;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderTinyPotato implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int i, int j, RenderBlocks renderBlocks) {
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.5F, 0F, -0.5F);
		TileEntityRenderer.instance.renderTileEntityAt(new TileTinyPotato(), 0.0D, 0.0D, 0.0D, 0.0F);
		GL11.glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess iBlockAccess, int i, int j, int k, Block block, int l, RenderBlocks renderBlocks) {
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return TinyPotatoMod.proxy.tinyPotatoRenderId;
	}
}
