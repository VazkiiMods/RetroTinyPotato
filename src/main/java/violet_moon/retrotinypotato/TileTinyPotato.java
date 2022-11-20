package violet_moon.retrotinypotato;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

import java.util.*;
import java.util.regex.Pattern;

public class TileTinyPotato extends TileEntity implements ISidedInventory {
	private static final boolean IS_BIRTHDAY = isTinyPotatoBirthday();
	private static final String TAG_NAME = "name";
	public static final int JUMP_EVENT = 0;

	private final InventoryBasic inventory = new InventoryBasic("tinyPotato", ForgeDirection.VALID_DIRECTIONS.length) {
		@Override
		public int getInventoryStackLimit() {
			return 1;
		}
	};
	public int jumpTicks = 0;
	public String name = "";
	private int birthdayTick = 0;

	public void interact(EntityPlayer player, ForgeDirection side) {
		if (!worldObj.isRemote) {
			int index = side.ordinal();
			ItemStack stackAt = this.inventory.getStackInSlot(index);
			ItemStack stack = player.getHeldItem();
			if (stackAt != null && stack == null) {
				player.inventory.mainInventory[player.inventory.currentItem] = stackAt;
				this.inventory.setInventorySlotContents(index, null);
				onInventoryChanged();
			} else if (stack != null) {
				ItemStack copy = stack.splitStack(1);

				if (stack.stackSize == 0) {
					player.inventory.mainInventory[player.inventory.currentItem] = stackAt;
				} else if (stackAt != null) {
					player.inventory.addItemStackToInventory(stackAt);
					// todo voids overflow
				}

				this.inventory.setInventorySlotContents(index, copy);
				onInventoryChanged();
			}

			jump();

			/*
			if (name.toLowerCase(Locale.ROOT).trim().endsWith("shia labeouf") && nextDoIt == 0) {
				nextDoIt = 40;
				level.playSound(null, worldPosition, BotaniaSounds.doit, SoundSource.BLOCKS, 1F, 1F);
			}
			*/

			for (int i = 0; i < getSizeInventory(); i++) {
				ItemStack son = getStackInSlot(i);
				if (son != null && son.itemID == TinyPotatoMod.itemBlockTinyPotato.itemID) {
					player.sendChatToPlayer("Don't talk to me or my son ever again.");
					return;
				}
			}

			// player.awardStat(BotaniaStats.TINY_POTATOES_PETTED);
			// PlayerHelper.grantCriterion((ServerPlayer) player, prefix("main/tiny_potato_pet"), "code_triggered");
		}
	}

	private void jump() {
		if (jumpTicks == 0) {
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, blockType.blockID, JUMP_EVENT, 20);
		}
	}


	@Override
	public void receiveClientEvent(int id, int param) {
		if (id == JUMP_EVENT) {
			jumpTicks = param;
			if (worldObj.isRemote) {
				BlockTinyPotato.spawnHearts(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (this.jumpTicks > 0) {
			this.jumpTicks--;
		}

		if (!worldObj.isRemote) {
			if (worldObj.rand.nextInt(100) == 0) {
				this.jump();
			}
			/*
			if (this.nextDoIt > 0) {
				this.nextDoIt--;
			}
			*/
			if (IS_BIRTHDAY) {
				this.tickBirthday();
			}
		}
	}

	private void tickBirthday() {
		ForgeDirection facing = ForgeDirection.getOrientation(getBlockMetadata());
		int facingX = xCoord + facing.offsetX;
		int facingY = yCoord + facing.offsetY;
		int facingZ = zCoord + facing.offsetZ;

		if (worldObj.blockExists(facingX, facingY, facingZ)) {
			int facingId = worldObj.getBlockId(facingX, facingY, facingZ);
			int color = getColor(facingId, worldObj.rand);
			List<EntityPlayer> players = getRealPlayersIn(worldObj, xCoord, yCoord, zCoord);

			if (color != -1 && !players.isEmpty()) {
				birthdayTick++;

				// 3.5s per message, initial delay of 5s
				List<Integer> messageTimes = Arrays.asList(100, 170, 240, 310, 380);
				int messageIndex = messageTimes.indexOf(birthdayTick);
				if (messageIndex != -1) {
					String subMessage;
					if (messageIndex == 1) {
						subMessage = StatCollector.translateToLocalFormatted("tinyPotato.birthday.1", getTinyPotatoAge());
					} else {
						subMessage = StatCollector.translateToLocal("tinyPotato.birthday." + messageIndex);
					}

					String message = String.format("<%s> %s",
						this.name.isEmpty()
							? TinyPotatoMod.blockTinyPotato.translateBlockName()
							: this.name,
						subMessage);

					for (EntityPlayer player : players) {
						player.sendChatToPlayer(message);
					}
					jump();
				}

				if (messageIndex == messageTimes.size() - 1) {
					NBTTagCompound explosion = new NBTTagCompound();
					explosion.setByte("Type", (byte) 1);
					explosion.setBoolean("Flicker", true);
					explosion.setBoolean("Trail", true);
					explosion.setIntArray("Colors", new int[]{
							ItemDye.dyeColors[color],
							0xD260A5, 0xE4AFCD, 0xFEFEFE, 0x57CEF8
					});

					NBTTagList explosions = new NBTTagList();
					explosions.appendTag(explosion);

					ItemStack rocket = new ItemStack(Item.firework);
					NBTTagCompound cmp = new NBTTagCompound();
					NBTTagCompound rocketFireworks = new NBTTagCompound();
					rocketFireworks.setByte("Flight", (byte) 0);
					rocketFireworks.setTag("Explosions", explosions);
					cmp.setCompoundTag("Fireworks", rocketFireworks);
					rocket.setTagCompound(cmp);

					worldObj.spawnEntityInWorld(new EntityFireworkRocket(worldObj, facingX + 0.5, facingY + 0.5, facingZ + 0.5, rocket));
					worldObj.setBlockWithNotify(facingX, facingY, facingZ, 0);
					worldObj.playAuxSFX(2001, facingX, facingY, facingZ, facingId);
					worldObj.playSound(xCoord, yCoord, zCoord, "random.eat", 1F, 0.5F + (float) Math.random() * 0.5F, false);

					/*
					for (EntityPlayer player : players) {
						PlayerHelper.grantCriterion((ServerPlayer) player, BIRTHDAY_ADVANCEMENT, "code_triggered");
					}
					*/
				}
			}
		}
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		if (worldObj != null && !worldObj.isRemote) {
			// todo sync
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound cmp) {
		super.writeToNBT(cmp);
		cmp.setString(TAG_NAME, this.name);
	}

	@Override
	public void readFromNBT(NBTTagCompound cmp) {
		super.readFromNBT(cmp);
		this.name = cmp.getString(TAG_NAME);
	}

	private static int getColor(int blockId, Random rand) {
		if (blockId != Block.cake.blockID) {
			return -1;
		}
		return rand.nextInt(16);
	}

	private static boolean isTinyPotatoBirthday() {
		// Tiny Potato was added in Botania commit c225a134043922724e6ff141ff26f31097d4d9d0,
		// created on July 19, 2014
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.MONTH) == Calendar.JULY && now.get(Calendar.DAY_OF_MONTH) == 19;
	}

	private static int getTinyPotatoAge() {
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.YEAR) - 2014;
	}

	private static final Pattern FAKE_PLAYER_PATTERN = Pattern.compile("^(?:\\[.*]|ComputerCraft)$");
	private static List<EntityPlayer> getRealPlayersIn(World world, int x, int y, int z) {
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
			x + 0.5 - 8, y + 0.5 - 8, z + 0.5 - 8,
			x + 0.5 + 8, y + 0.5 + 8, z + 0.5 + 8
		);
		List<EntityPlayer> ret = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
		Iterator<EntityPlayer> iter = ret.iterator();
		while (iter.hasNext()) {
			EntityPlayer player = iter.next();
			if (FAKE_PLAYER_PATTERN.matcher(player.getEntityName()).matches()) {
				iter.remove();
			}
		}

		return ret;
	}

	@Override
	public int getStartInventorySide(ForgeDirection forgeDirection) {
		return forgeDirection.ordinal();
	}

	@Override
	public int getSizeInventorySide(ForgeDirection forgeDirection) {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return this.inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.inventory.getStackInSlot(i);
	}

	@Override public ItemStack decrStackSize(int i, int amount) {
		return this.inventory.decrStackSize(i, amount);
	}

	@Override public ItemStack getStackInSlotOnClosing(int i) {
		return this.inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		this.inventory.setInventorySlotContents(i, stack);
	}

	@Override
	public String getInvName() {
		return this.name;
	}

	@Override
	public int getInventoryStackLimit() {
		return this.inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.inventory.isUseableByPlayer(player);
	}

	@Override
	public void openChest() {
		this.inventory.openChest();
	}

	@Override
	public void closeChest() {
		this.inventory.closeChest();
	}
}
