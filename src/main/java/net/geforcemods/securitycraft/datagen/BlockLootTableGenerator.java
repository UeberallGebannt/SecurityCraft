package net.geforcemods.securitycraft.datagen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blocks.mines.IMSBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedSlabBlock;
import net.geforcemods.securitycraft.misc.conditions.TileEntityNBTCondition;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.StandaloneLootEntry;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraft.world.storage.loot.conditions.EntityHasProperty;
import net.minecraft.world.storage.loot.conditions.Inverted;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;
import net.minecraft.world.storage.loot.functions.ExplosionDecay;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.fml.RegistryObject;

public class BlockLootTableGenerator implements IDataProvider
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	protected final Map<Supplier<Block>,LootTable.Builder> lootTables = new HashMap<>();
	private final DataGenerator generator;

	public BlockLootTableGenerator(DataGenerator generator)
	{
		this.generator = generator;
	}

	private void addTables()
	{
		for(RegistryObject<Block> obj : SCContent.BLOCKS.getEntries())
		{
			Block block = obj.get();

			if(block instanceof ReinforcedSlabBlock)
				putSlabLootTable(obj);
			else if(block instanceof IExplosive)
				putMineLootTable(obj);
			else if(block.asItem() != Items.AIR)
				putStandardBlockLootTable(obj);
		}

        lootTables.remove(SCContent.REINFORCED_PISTON_HEAD);
		putSlabLootTable(SCContent.CRYSTAL_QUARTZ_SLAB);

		StandaloneLootEntry.Builder<?> imsLootEntryBuilder = ItemLootEntry.builder(SCContent.BOUNCING_BETTY.get());

		for(int i = 0; i <= 4; i++)
		{
			if(i == 1) //default
				continue;

			imsLootEntryBuilder.acceptFunction(SetCount.builder(ConstantRange.of(i))
					.acceptCondition(BlockStateProperty.builder(SCContent.IMS.get())
							.fromProperties(StatePropertiesPredicate.Builder.newBuilder()
									.withIntProp(IMSBlock.MINES, i))));
		}

		lootTables.put(SCContent.IMS, LootTable.builder()
				.addLootPool(LootPool.builder()
						.rolls(ConstantRange.of(1))
						.addEntry(imsLootEntryBuilder)));
		putStandardBlockLootTable(SCContent.KEY_PANEL_BLOCK, SCContent.KEY_PANEL.get());
		putStandardBlockLootTable(SCContent.KEYPAD_CHEST);
		putDoorLootTable(SCContent.KEYPAD_DOOR, SCContent.KEYPAD_DOOR_ITEM);
		putDoorLootTable(SCContent.REINFORCED_DOOR, SCContent.REINFORCED_DOOR_ITEM);
		lootTables.put(SCContent.REINFORCED_IRON_BARS,
				LootTable.builder()
				.addLootPool(LootPool.builder()
						.rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(SCContent.REINFORCED_IRON_BARS.get())
								.acceptCondition(TileEntityNBTCondition.builder().equals("canDrop", true)))
						.acceptCondition(SurvivesExplosion.builder())));
		putDoorLootTable(SCContent.SCANNER_DOOR, SCContent.SCANNER_DOOR_ITEM);
		putStandardBlockLootTable(SCContent.SECRET_ACACIA_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_ACACIA_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_BIRCH_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_BIRCH_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_DARK_OAK_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_DARK_OAK_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_JUNGLE_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_JUNGLE_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_OAK_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_OAK_WALL_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_SPRUCE_SIGN);
		putStandardBlockLootTable(SCContent.SECRET_SPRUCE_WALL_SIGN);
	}

	protected final LootTable.Builder createStandardBlockLootTable(Supplier<Block> drop)
	{
		return createStandardBlockLootTable(drop.get());
	}

	protected final LootTable.Builder createStandardBlockLootTable(IItemProvider drop)
	{
		return LootTable.builder()
				.addLootPool(LootPool.builder()
						.rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(drop.asItem()))
						.acceptCondition(SurvivesExplosion.builder()));
	}

	protected final void putDoorLootTable(Supplier<Block> door, Supplier<Item> doorItem)
	{
		lootTables.put(door, LootTable.builder()
				.addLootPool(LootPool.builder()
						.rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(doorItem.get())
								.acceptCondition(BlockStateProperty.builder(door.get())
										.fromProperties(StatePropertiesPredicate.Builder.newBuilder()
												.withProp(ReinforcedDoorBlock.HALF, DoubleBlockHalf.LOWER)))
								.acceptCondition(SurvivesExplosion.builder()))));
	}

	protected final void putStandardBlockLootTable(Supplier<Block> block)
	{
		putStandardBlockLootTable(block, block.get());
	}

	protected final void putStandardBlockLootTable(Supplier<Block> block, IItemProvider drop)
	{
		lootTables.put(block, createStandardBlockLootTable(drop));
	}

	protected final void putMineLootTable(Supplier<Block> mine)
	{
		lootTables.put(mine, LootTable.builder()
				.addLootPool(LootPool.builder()
						.rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(mine.get()))
						.acceptCondition(SurvivesExplosion.builder())
						.acceptCondition(Inverted.builder(EntityHasProperty.builder(EntityTarget.THIS)))));
	}

	protected final void putSlabLootTable(Supplier<Block> slab)
	{
		lootTables.put(slab, LootTable.builder()
				.addLootPool(LootPool.builder()
						.rolls(ConstantRange.of(1))
						.addEntry(ItemLootEntry.builder(slab.get())
								.acceptFunction(SetCount.builder(ConstantRange.of(2))
										.acceptCondition(BlockStateProperty.builder(slab.get())
												.fromProperties(StatePropertiesPredicate.Builder.newBuilder()
														.withProp(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE))))
								.acceptFunction(ExplosionDecay.builder()))));
	}

	@Override
	public void act(DirectoryCache cache) throws IOException
	{
		Map<ResourceLocation,LootTable> tables = new HashMap<>();

		addTables();

		for(Map.Entry<Supplier<Block>,LootTable.Builder> entry : lootTables.entrySet())
		{
			tables.put(entry.getKey().get().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.BLOCK).build());
		}

		tables.forEach((key, lootTable) -> {
			try
			{
				IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), generator.getOutputFolder().resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		});
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Block Loot Tables";
	}
}
