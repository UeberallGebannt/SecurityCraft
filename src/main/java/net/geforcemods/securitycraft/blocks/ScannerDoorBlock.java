package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ScannerDoorBlock extends SpecialDoorBlock
{
	public ScannerDoorBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new ScannerDoorBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return BaseEntityBlock.createTickerHelper(type, SCContent.beTypeScannerDoor, LevelUtils::blockEntityTicker);
	}

	@Override
	public Item getDoorItem()
	{
		return SCContent.SCANNER_DOOR_ITEM.get();
	}
}
