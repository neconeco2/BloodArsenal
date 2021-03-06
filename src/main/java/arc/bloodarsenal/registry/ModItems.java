package arc.bloodarsenal.registry;

import WayofTime.bloodmagic.api.Constants;
import arc.bloodarsenal.BloodArsenal;
import arc.bloodarsenal.ConfigHandler;
import arc.bloodarsenal.item.ItemBloodArsenalBase;
import arc.bloodarsenal.item.ItemBloodOrange;
import arc.bloodarsenal.item.ItemGem;
import arc.bloodarsenal.item.block.ItemBlockSpecialBloodArsenal;
import arc.bloodarsenal.item.sigil.*;
import arc.bloodarsenal.item.tool.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.text.WordUtils;

public class ModItems
{
    public static Item glassShard;
    public static Item bloodInfusedStick;
    public static Item bloodBurnedString;
    public static Item bloodOrange;
    public static Item bloodInfusedWoodenPickaxe;
    public static Item bloodInfusedWoodenAxe;
    public static Item bloodInfusedWoodenShovel;
    public static Item bloodInfusedWoodenSword;
    public static Item bloodInfusedGlowstoneDust;
    public static Item inertBloodInfusedIronIngot;
    public static Item bloodInfusedIronIngot;
    public static Item bloodInfusedIronPickaxe;
    public static Item bloodInfusedIronAxe;
    public static Item bloodInfusedIronShovel;
    public static Item bloodInfusedIronSword;
    public static Item glassSacrificialDagger;
    public static Item glassDaggerOfSacrifice;
    public static Item bloodInfusedShield;

    public static Item gemSacrifice;
    public static Item gemSelfSacrifice;
    public static Item gemTartaric;

    public static Item sigilSwimming;
    public static Item sigilEnder;
    public static Item sigilAugmentedHolding;
    public static Item sigilLightning;
    public static Item sigilDivinity;

    public static Item reagentSwimming;
    public static Item reagentEnder;
    public static Item reagentLightning;
    public static Item reagentDivinity;

    public static Item sacrificeAmulet;
    public static Item selfSacrificeAmulet;
    public static Item soulPendant;

    public static Item.ToolMaterial bloodInfusedWoodMaterial = EnumHelper.addToolMaterial("BloodInfusedWoodMaterial", 1, 186, 5.5F, 1.0F, 13);
    public static Item.ToolMaterial bloodInfusedIronMaterial = EnumHelper.addToolMaterial("BloodInfusedIronMaterial", 3, 954, 7.25F, 2.7F, 21);

    public static void init()
    {
        glassShard = registerItemUniquely(new ItemBloodArsenalBase("glassShard"));
        bloodInfusedStick = registerItemUniquely(new ItemBloodArsenalBase("bloodInfusedStick"));
        bloodBurnedString = registerItemUniquely(new ItemBlockSpecialBloodArsenal("bloodBurnedString", ModBlocks.bloodBurnedString));
        bloodOrange = registerItem(new ItemBloodOrange("bloodOrange"));
        bloodInfusedWoodenPickaxe = registerItem(new ItemBloodInfusedWoodenPickaxe());
        bloodInfusedWoodenAxe = registerItem(new ItemBloodInfusedWoodenAxe());
        bloodInfusedWoodenShovel = registerItem(new ItemBloodInfusedWoodenShovel());
        bloodInfusedWoodenSword = registerItem(new ItemBloodInfusedWoodenSword());
        bloodInfusedGlowstoneDust = registerItemUniquely(new ItemBloodArsenalBase("bloodInfusedGlowstoneDust"));
        inertBloodInfusedIronIngot = registerItemUniquely(new ItemBloodArsenalBase("inertBloodInfusedIronIngot"));
        bloodInfusedIronIngot = registerItemUniquely(new ItemBloodArsenalBase("bloodInfusedIronIngot"));
        bloodInfusedIronPickaxe = registerItem(new ItemBloodInfusedIronPickaxe());
        bloodInfusedIronAxe = registerItem(new ItemBloodInfusedIronAxe());
        bloodInfusedIronShovel = registerItem(new ItemBloodInfusedIronShovel());
        bloodInfusedIronSword = registerItem(new ItemBloodInfusedIronSword());
        glassSacrificialDagger = registerItem(new ItemGlassSacrificialDagger("glassSacrificialDagger"));
        glassDaggerOfSacrifice = registerItem(new ItemGlassDaggerOfSacrifice("glassDaggerOfSacrifice"));
//        bloodInfusedShield = registerItem(new ItemBloodInfusedShield("bloodInfusedShield"));

        gemSacrifice = registerItemUniquely(new ItemGem("sacrifice"));
        gemSelfSacrifice = registerItemUniquely(new ItemGem("selfSacrifice"));
        gemTartaric = registerItemUniquely(new ItemGem("tartaric"));

        sigilSwimming = registerItem(new ItemSigilSwimming());
        sigilEnder = registerItem(new ItemSigilEnder());
        sigilAugmentedHolding = registerItem(new ItemSigilAugmentedHolding());
        sigilLightning = registerItem(new ItemSigilLightning());
        sigilDivinity = registerItem(new ItemSigilDivinity());

        reagentSwimming = registerItemUniquely(new ItemBloodArsenalBase("reagentSwimming"));
        reagentEnder = registerItemUniquely(new ItemBloodArsenalBase("reagentEnder"));
        reagentLightning = registerItemUniquely(new ItemBloodArsenalBase("reagentLightning"));
        reagentDivinity = registerItemUniquely(new ItemBloodArsenalBase("reagentDivinity"));

        addOreDictItems();
    }

    public static void addOreDictItems()
    {
        OreDictionary.registerOre("glassShard", glassShard);
    }

    @SideOnly(Side.CLIENT)
    public static void initSpecialRenders()
    {
        final ResourceLocation holdingLoc = new ResourceLocation("bloodarsenal", "item/ItemSigilAugmentedHolding");
        ModelLoader.setCustomMeshDefinition(sigilAugmentedHolding, stack -> stack.hasTagCompound() && stack.getTagCompound().hasKey(Constants.NBT.COLOR) ? new ModelResourceLocation(holdingLoc, "type=color") : new ModelResourceLocation(holdingLoc, "type=normal"));
        ModelLoader.registerItemVariants(sigilAugmentedHolding, new ModelResourceLocation(holdingLoc, "type=normal"));
        ModelLoader.registerItemVariants(sigilAugmentedHolding, new ModelResourceLocation(holdingLoc, "type=color"));
    }

    public static Item registerItem(Item item)
    {
        item.setRegistryName(item.getClass().getSimpleName());
        if (item.getRegistryName() == null)
        {
            BloodArsenal.INSTANCE.getLogger().error("Attempted to register Item {} without setting a registry name. Item will not be registered. Please report this.", item.getClass().getCanonicalName());
            return item;
        }

        String itemName = item.getRegistryName().toString().split(":")[1];
        if (!ConfigHandler.itemBlacklist.contains(itemName))
        {
            GameRegistry.register(item);
            BloodArsenal.PROXY.tryHandleItemModel(item, itemName);
        }

        return item;
    }

    // To be used with ItemBloodArsenalBase and similar classes
    public static Item registerItemUniquely(Item item)
    {
        item.setRegistryName(item.getClass().getSimpleName() + "." + WordUtils.capitalize(item.getUnlocalizedName().substring(18)));
        if (item.getRegistryName() == null)
        {
            BloodArsenal.INSTANCE.getLogger().error("Attempted to register Item {} without setting a registry name. Item will not be registered. Please report this.", item.getClass().getCanonicalName());
            return item;
        }

        String itemName = item.getRegistryName().toString().split(":")[1];
        if (!ConfigHandler.itemBlacklist.contains(itemName))
        {
            GameRegistry.register(item);
            BloodArsenal.PROXY.tryHandleItemModel(item, itemName.split("[.]")[0]);
        }

        return item;
    }
}
