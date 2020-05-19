package net.geforcemods.securitycraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.commands.CommandSC;
import net.geforcemods.securitycraft.compat.cyclic.CyclicCompat;
import net.geforcemods.securitycraft.compat.versionchecker.VersionUpdateChecker;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.IProxy;
import net.geforcemods.securitycraft.tabs.CreativeTabSCDecoration;
import net.geforcemods.securitycraft.tabs.CreativeTabSCExplosives;
import net.geforcemods.securitycraft.tabs.CreativeTabSCTechnical;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = SecurityCraft.MODID, name = "SecurityCraft", version = SecurityCraft.VERSION, dependencies = SecurityCraft.DEPENDENCIES, updateJSON = SecurityCraft.UPDATEJSONURL, acceptedMinecraftVersions = "[1.12.2]")
public class SecurityCraft {
	public static final String MODID = "securitycraft";
	private static final String MOTU = "Finally! Cameras!";
	//********************************* This is v1.8.18.2 for MC 1.12.2!
	protected static final String VERSION = "v1.8.18.2";
	protected static final String DEPENDENCIES = "required-after:forge@[14.23.5.2826,)";
	protected static final String UPDATEJSONURL = "https://www.github.com/Geforce132/SecurityCraft/raw/master/Updates/Forge.json";
	@SidedProxy(clientSide = "net.geforcemods.securitycraft.network.ClientProxy", serverSide = "net.geforcemods.securitycraft.network.ServerProxy")
	public static IProxy proxy;
	@Instance(MODID)
	public static SecurityCraft instance = new SecurityCraft();
	public static SimpleNetworkWrapper network;
	public static SCEventHandler eventHandler = new SCEventHandler();
	private GuiHandler guiHandler = new GuiHandler();
	public ArrayList<SCManualPage> manualPages = new ArrayList<>();
	public static CreativeTabs tabSCTechnical = new CreativeTabSCTechnical();
	public static CreativeTabs tabSCMine = new CreativeTabSCExplosives();
	public static CreativeTabs tabSCDecoration = new CreativeTabSCDecoration();

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new CommandSC());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		SecurityCraft.network = NetworkRegistry.INSTANCE.newSimpleChannel(SecurityCraft.MODID);
		RegistrationHandler.registerPackets(SecurityCraft.network);
		SetupHandler.setupBlocks();
		SetupHandler.setupMines();
		SetupHandler.setupItems();

		ModMetadata modMeta = event.getModMetadata();
		modMeta.authorList = Arrays.asList("Geforce", "bl4ckscor3");
		modMeta.autogenerated = false;
		modMeta.credits = "Thanks to all of you guys for your support!";
		modMeta.description = "Adds a load of things to keep your house safe with.\nIf you like this mod, hit the green arrow\nin the corner of the forum thread!\nPlease visit the URL above for help. \n \nMessage of the update: \n" + MOTU;
		modMeta.url = "http://geforcemods.net";
		modMeta.logoFile = "/scLogo.png";

		proxy.registerEntityRenderingHandlers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		FMLInterModComms.sendMessage("waila", "register", "net.geforcemods.securitycraft.compat.waila.WailaDataProvider.callbackRegister");
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "net.geforcemods.securitycraft.compat.top.TOPDataProvider");

		if(ConfigHandler.checkForUpdates) {
			NBTTagCompound vcUpdateTag = VersionUpdateChecker.getNBTTagCompound();
			if(vcUpdateTag != null)
				FMLInterModComms.sendRuntimeMessage(MODID, "VersionChecker", "addUpdate", vcUpdateTag);
		}

		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		EnumModuleType.refresh();
		proxy.registerRenderThings();
		FMLCommonHandler.instance().getDataFixer().init(SecurityCraft.MODID, TileEntityIDDataFixer.VERSION).registerFix(FixTypes.BLOCK_ENTITY, new TileEntityIDDataFixer());
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedCobblestone), new ItemStack(SCContent.reinforcedStone, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedSand, 1, 0), new ItemStack(SCContent.reinforcedGlass, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedSand, 1, 1), new ItemStack(SCContent.reinforcedGlass, 1, 0), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedStoneBrick, 1, 0), new ItemStack(SCContent.reinforcedStoneBrick, 1, 2), 0.1F);
		GameRegistry.addSmelting(new ItemStack(SCContent.reinforcedClay, 1, 0), new ItemStack(SCContent.reinforcedHardenedClay, 1, 0), 0.35F);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(SecurityCraft.eventHandler);

		if(Loader.isModLoaded("cyclicmagic"))
			MinecraftForge.EVENT_BUS.register(new CyclicCompat());

		for(Field field : SCContent.class.getFields())
		{
			try
			{
				if(field.isAnnotationPresent(Reinforced.class))
					IReinforcedBlock.BLOCKS.add((Block)field.get(null));
			}
			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static String getVersion() {
		return VERSION;
	}
}
