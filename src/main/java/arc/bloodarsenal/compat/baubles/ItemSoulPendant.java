package arc.bloodarsenal.compat.baubles;

import WayofTime.bloodmagic.api.Constants;
import WayofTime.bloodmagic.api.iface.IMultiWillTool;
import WayofTime.bloodmagic.api.soul.EnumDemonWillType;
import WayofTime.bloodmagic.api.soul.IDemonWill;
import WayofTime.bloodmagic.api.soul.IDemonWillGem;
import WayofTime.bloodmagic.api.soul.PlayerDemonWillHandler;
import WayofTime.bloodmagic.api.util.helper.NBTHelper;
import WayofTime.bloodmagic.client.IMeshProvider;
import WayofTime.bloodmagic.util.helper.TextHelper;
import arc.bloodarsenal.client.mesh.CustomMeshDefinitionSoulPendant;
import baubles.api.BaubleType;
import baubles.common.lib.PlayerHandler;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemSoulPendant extends ItemBauble implements IDemonWillGem, IMeshProvider, IMultiWillTool
{
    public static String[] names = { "petty", "lesser", "common", "greater", "grand" };

    public ItemSoulPendant(String name)
    {
        super(name);

        setHasSubtypes(true);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event)
    {
        ItemStack stack = event.getItem().getEntityItem();
        if (stack != null && stack.getItem() instanceof IDemonWill)
        {
            EntityPlayer player = event.getEntityPlayer();

            ItemStack remainder = addDemonWill(player, stack);

            if (remainder == null || ((IDemonWill) stack.getItem()).getWill(stack) < 0.0001 || isDemonWillFull(EnumDemonWillType.DEFAULT, player))
            {
                stack.stackSize = 0;
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    public static boolean isDemonWillFull(EnumDemonWillType type, EntityPlayer player)
    {
        ItemStack[] inventory = ArrayUtils.addAll(player.inventory.mainInventory, PlayerHandler.getPlayerBaubles(player).stackList);

        boolean hasGem = false;
        for (ItemStack stack : inventory)
        {
            if (stack != null && stack.getItem() instanceof IDemonWillGem)
            {
                hasGem = true;
                if (((IDemonWillGem) stack.getItem()).getWill(type, stack) < ((IDemonWillGem) stack.getItem()).getMaxWill(type, stack))
                    return false;
            }
        }

        return hasGem;
    }

    /**
     * Modified version of {@link PlayerDemonWillHandler}'s addDemonWill
     * We need to override it to special case for baubles
     *
     * @param player
     *        - The player to add will to
     * @param willStack
     *        - ItemStack that contains an IDemonWill to be added
     *
     * @return - The modified willStack
     */
    public static ItemStack addDemonWill(EntityPlayer player, ItemStack willStack)
    {
        if (willStack == null)
            return null;

        ItemStack[] inventory = ArrayUtils.addAll(player.inventory.mainInventory, PlayerHandler.getPlayerBaubles(player).stackList);

        for (ItemStack stack : inventory)
        {
            if (stack != null && stack.getItem() instanceof IDemonWillGem)
            {
                ItemStack newStack = ((IDemonWillGem) stack.getItem()).fillDemonWillGem(stack, willStack);
                if (newStack == null)
                    return null;
            }
        }

        return willStack;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + "." + names[stack.getItemDamage()];
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        EnumDemonWillType type = this.getCurrentType(stack);
        double drain = Math.min(this.getWill(type, stack), this.getMaxWill(type, stack) / 10);

        double filled = PlayerDemonWillHandler.addDemonWill(type, player, drain, stack);
        this.drainWill(type, stack, filled, true);

        return super.onItemRightClick(stack, world, player, hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemMeshDefinition getMeshDefinition()
    {
        return new CustomMeshDefinitionSoulPendant("ItemSoulPendant");
    }

    @Nullable
    @Override
    public ResourceLocation getCustomLocation()
    {
        return null;
    }

    @Override
    public List<String> getVariants()
    {
        List<String> ret = new ArrayList<>();
        for (EnumDemonWillType type : EnumDemonWillType.values())
        {
            ret.add("type=petty_" + type.getName().toLowerCase());
            ret.add("type=lesser_" + type.getName().toLowerCase());
            ret.add("type=common_" + type.getName().toLowerCase());
            ret.add("type=greater_" + type.getName().toLowerCase());
            ret.add("type=grand_" + type.getName().toLowerCase());
        }

        return ret;
    }

    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item id, CreativeTabs creativeTab, List<ItemStack> list)
    {
        for (int i = 0; i < names.length; i++)
        {
            ItemStack emptyStack = new ItemStack(this, 1, i);

            list.add(emptyStack);
        }
        for (EnumDemonWillType type : EnumDemonWillType.values())
        {
            for (int i = 0; i < names.length; i++)
            {
                ItemStack fullStack = new ItemStack(this, 1, i);
                setWill(type, fullStack, getMaxWill(EnumDemonWillType.DEFAULT, fullStack));
                list.add(fullStack);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        EnumDemonWillType type = this.getCurrentType(stack);
        tooltip.add(TextHelper.localize("tooltip.BloodArsenal.soulPendant." + names[stack.getItemDamage()]));
        tooltip.add(TextHelper.localize("tooltip.BloodMagic.will", getWill(type, stack)));
        tooltip.add(TextHelper.localizeEffect("tooltip.BloodMagic.currentType." + getCurrentType(stack).getName().toLowerCase()));

        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        EnumDemonWillType type = this.getCurrentType(stack);
        double maxWill = getMaxWill(type, stack);
        if (maxWill <= 0)
        {
            return 1;
        }
        return 1.0 - (getWill(type, stack) / maxWill);
    }

    @Override
    public ItemStack fillDemonWillGem(ItemStack soulGemStack, ItemStack soulStack)
    {
        if (soulStack != null && soulStack.getItem() instanceof IDemonWill)
        {
            EnumDemonWillType thisType = this.getCurrentType(soulGemStack);
            if (thisType != EnumDemonWillType.DEFAULT)
            {
                return soulStack;
            }
            IDemonWill soul = (IDemonWill) soulStack.getItem();
            double soulsLeft = getWill(thisType, soulGemStack);

            if (soulsLeft < getMaxWill(thisType, soulGemStack))
            {
                double newSoulsLeft = Math.min(soulsLeft + soul.getWill(soulStack), getMaxWill(thisType, soulGemStack));
                soul.drainWill(soulStack, newSoulsLeft - soulsLeft);

                setWill(thisType, soulGemStack, newSoulsLeft);
                if (soul.getWill(soulStack) <= 0)
                {
                    return null;
                }
            }
        }

        return soulStack;
    }

    @Override
    public double getWill(EnumDemonWillType type, ItemStack soulGemStack)
    {
        if (!type.equals(getCurrentType(soulGemStack)))
        {
            return 0;
        }

        NBTTagCompound tag = soulGemStack.getTagCompound();

        return tag.getDouble(Constants.NBT.SOULS);
    }

    @Override
    public void setWill(EnumDemonWillType type, ItemStack soulGemStack, double souls)
    {
        setCurrentType(type, soulGemStack);

        NBTTagCompound tag = soulGemStack.getTagCompound();

        tag.setDouble(Constants.NBT.SOULS, souls);
    }

    @Override
    public double drainWill(EnumDemonWillType type, ItemStack soulGemStack, double drainAmount, boolean doDrain)
    {
        EnumDemonWillType currentType = this.getCurrentType(soulGemStack);
        if (currentType != type)
        {
            return 0;
        }
        double souls = getWill(type, soulGemStack);

        double soulsDrained = Math.min(drainAmount, souls);
        setWill(type, soulGemStack, souls - soulsDrained);

        return soulsDrained;
    }

    @Override
    public int getMaxWill(EnumDemonWillType type, ItemStack soulGemStack)
    {
        EnumDemonWillType currentType = getCurrentType(soulGemStack);
        if (!type.equals(currentType) && currentType != EnumDemonWillType.DEFAULT)
        {
            return 0;
        }

        switch (soulGemStack.getMetadata())
        {
            case 0:
                return 64;
            case 1:
                return 256;
            case 2:
                return 1024;
            case 3:
                return 4096;
            case 4:
                return 16384;
        }
        return 64;
    }

    @Override
    public EnumDemonWillType getCurrentType(ItemStack soulGemStack)
    {
        NBTHelper.checkNBT(soulGemStack);

        NBTTagCompound tag = soulGemStack.getTagCompound();

        if (!tag.hasKey(Constants.NBT.WILL_TYPE))
        {
            return EnumDemonWillType.DEFAULT;
        }

        return EnumDemonWillType.valueOf(tag.getString(Constants.NBT.WILL_TYPE));
    }

    public void setCurrentType(EnumDemonWillType type, ItemStack soulGemStack)
    {
        NBTHelper.checkNBT(soulGemStack);

        NBTTagCompound tag = soulGemStack.getTagCompound();

        if (type == EnumDemonWillType.DEFAULT)
        {
            if (tag.hasKey(Constants.NBT.WILL_TYPE))
            {
                tag.removeTag(Constants.NBT.WILL_TYPE);
            }

            return;
        }

        tag.setString(Constants.NBT.WILL_TYPE, type.toString());
    }

    @Override
    public double fillWill(EnumDemonWillType type, ItemStack stack, double fillAmount, boolean doFill)
    {
        if (!type.equals(getCurrentType(stack)) && this.getWill(getCurrentType(stack), stack) > 0)
        {
            return 0;
        }

        double current = this.getWill(type, stack);
        double maxWill = this.getMaxWill(type, stack);

        double filled = Math.min(fillAmount, maxWill - current);

        if (filled > 0)
        {
            this.setWill(type, stack, filled + current);
            return filled;
        }

        return 0;
    }
}