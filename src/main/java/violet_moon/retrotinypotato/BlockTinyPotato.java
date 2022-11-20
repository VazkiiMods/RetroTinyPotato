package violet_moon.retrotinypotato;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;

public class BlockTinyPotato extends BlockContainer {
	public BlockTinyPotato(int id) {
		super(id, Material.cloth);
		setBlockName("tinyPotato");
		setHardness(0.25F);
		float f = 1F / 16F * 6F;
		setBlockBounds(f, 0, f, 1F - f, f, 1F - f);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileTinyPotato) {
			TileTinyPotato tile = (TileTinyPotato) te;
			for (int i = 0; i < tile.getSizeInventory(); i++) {
				ItemStack stack = tile.getStackInSlot(i);
				if (stack != null) {
					world.spawnEntityInWorld(new EntityItem(world, x, y, z, stack.copy()));
				}
			}
		}
		super.breakBlock(world, x, y, z, id, meta);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileTinyPotato) {
			((TileTinyPotato) te).interact(player, ForgeDirection.getOrientation(side));
			spawnHearts(world, x, y, z);
		}
		return true;
	}

	public static void spawnHearts(World world, int x, int y, int z) {
		double minX = TinyPotatoMod.blockTinyPotato.minX;
		double minZ = TinyPotatoMod.blockTinyPotato.minZ;
		double maxX = TinyPotatoMod.blockTinyPotato.maxX;
		double maxY = TinyPotatoMod.blockTinyPotato.maxY;
		double maxZ = TinyPotatoMod.blockTinyPotato.maxZ;

		world.spawnParticle("heart", x + minX + Math.random() * (maxX - minX), y + maxY, z + minZ + Math.random() * (maxZ - minZ), 0, 0 ,0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving living) {
		super.onBlockPlacedBy(world, x, y, z, living);
		int l1 = MathHelper.floor_double(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		world.setBlockMetadataWithNotify(x, y, z, l1);
		/* todo
		if (par6ItemStack.hasDisplayName())
			((TileTinyPotato) par1World.getTileEntity(par2, par3, par4)).name = par6ItemStack.getDisplayName();
		*/
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileTinyPotato) {
			ItemStack stack = new ItemStack(this);
			String name = ((TileTinyPotato) tile).name;
			if (!name.isEmpty()) {
				stack.setItemName(name);
			}
			list.add(stack);
		}

		return list;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return TinyPotatoMod.proxy.tinyPotatoRenderId;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileTinyPotato();
	}
}
