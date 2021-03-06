package arc.bloodarsenal.item.inventory;

import WayofTime.bloodmagic.api.iface.ISigil;
import WayofTime.bloodmagic.item.sigil.ItemSigilHolding;
import arc.bloodarsenal.item.sigil.ItemSigilAugmentedHolding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

import static arc.bloodarsenal.registry.Constants.Misc.PLAYER_INVENTORY_COLUMNS;
import static arc.bloodarsenal.registry.Constants.Misc.PLAYER_INVENTORY_ROWS;

public class ContainerAugmentedHolding extends Container
{
    private final EntityPlayer player;
    public final InventoryAugmentedHolding inventoryHolding;

    public ContainerAugmentedHolding(EntityPlayer player, InventoryAugmentedHolding inventoryHolding)
    {
        this.player = player;
        this.inventoryHolding = inventoryHolding;
        int currentSlotHeldIn = player.inventory.currentItem;

        for (int columnIndex = 0; columnIndex < ItemSigilAugmentedHolding.inventorySize; ++columnIndex)
        {
            this.addSlotToContainer(new SlotHolding(this, inventoryHolding, player, columnIndex, 8 + columnIndex * 18, 17));
        }

        for (int rowIndex = 0; rowIndex < PLAYER_INVENTORY_ROWS; ++rowIndex)
        {
            for (int columnIndex = 0; columnIndex < PLAYER_INVENTORY_COLUMNS; ++columnIndex)
            {
                addSlotToContainer(new Slot(player.inventory, columnIndex + rowIndex * 9 + 9, 8 + columnIndex * 18, 41 + rowIndex * 18));
            }
        }

        for (int actionBarIndex = 0; actionBarIndex < PLAYER_INVENTORY_COLUMNS; ++actionBarIndex)
        {
            if (actionBarIndex == currentSlotHeldIn)
            {
                addSlotToContainer(new SlotDisabled(player.inventory, actionBarIndex, 8 + actionBarIndex * 18, 99));
            } else
            {
                addSlotToContainer(new Slot(player.inventory, actionBarIndex, 8 + actionBarIndex * 18, 99));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer)
    {
        super.onContainerClosed(entityPlayer);

        if (!entityPlayer.worldObj.isRemote)
        {
            saveInventory(entityPlayer);
        }
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (!player.worldObj.isRemote)
        {
            saveInventory(player);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
    {
        ItemStack stack = null;
        Slot slotObject = (Slot) inventorySlots.get(slotIndex);
        int slots = inventorySlots.size();

        if (slotObject != null && slotObject.getHasStack())
        {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            if (stack.getItem() instanceof ISigil)
            {
                if (slotIndex < ItemSigilHolding.inventorySize)
                {
                    if (!this.mergeItemStack(stackInSlot, ItemSigilHolding.inventorySize, slots, false))
                    {
                        return null;
                    }
                } else if (!this.mergeItemStack(stackInSlot, 0, ItemSigilHolding.inventorySize, false))
                {
                    return null;
                }
            } else if (stack.getItem() instanceof ItemSigilHolding)
            {
                if (slotIndex < ItemSigilHolding.inventorySize + (PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS))
                {
                    if (!this.mergeItemStack(stackInSlot, ItemSigilHolding.inventorySize + (PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS), inventorySlots.size(), false))
                    {
                        return null;
                    }
                } else if (!this.mergeItemStack(stackInSlot, ItemSigilHolding.inventorySize, ItemSigilHolding.inventorySize + (PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLUMNS), false))
                {
                    return null;
                }
            }

            if (stackInSlot.stackSize == 0)
            {
                slotObject.putStack(null);
            } else
            {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.stackSize == stack.stackSize)
            {
                return null;
            }

            slotObject.onPickupFromSlot(player, stackInSlot);
        }

        return stack;
    }

    public void saveInventory(EntityPlayer entityPlayer)
    {
        inventoryHolding.onGuiSaved(entityPlayer);
    }

    private class SlotHolding extends Slot
    {
        private final EntityPlayer player;
        private ContainerAugmentedHolding containerHolding;

        public SlotHolding(ContainerAugmentedHolding containerHolding, IInventory inventory, EntityPlayer player, int slotIndex, int x, int y)
        {
            super(inventory, slotIndex, x, y);
            this.player = player;
            this.containerHolding = containerHolding;
        }

        @Override
        public void onSlotChanged()
        {
            super.onSlotChanged();

            if (FMLCommonHandler.instance().getEffectiveSide().isServer())
            {
                containerHolding.saveInventory(player);
            }
        }

        @Override
        public boolean isItemValid(ItemStack itemStack)
        {
            return itemStack.getItem() instanceof ISigil && !(itemStack.getItem() instanceof ItemSigilHolding);
        }
    }

    private class SlotDisabled extends Slot
    {
        public SlotDisabled(IInventory inventory, int slotIndex, int x, int y)
        {
            super(inventory, slotIndex, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack itemStack)
        {
            return false;
        }

        @Override
        public boolean canTakeStack(EntityPlayer player)
        {
            return false;
        }
    }
}