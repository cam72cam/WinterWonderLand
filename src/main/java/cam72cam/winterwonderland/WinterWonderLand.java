package cam72cam.winterwonderland;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@Mod(modid = WinterWonderLand.MODID, version = WinterWonderLand.VERSION, acceptedMinecraftVersions = "[1.12,1.13)", acceptableRemoteVersions = "*")
public class WinterWonderLand
{
    public static final String MODID = "winterwonderland";
    public static final String VERSION = "1.2.1";
    
    @EventBusSubscriber
    public static class EventHander {
    	private static Random r = new Random();
    	
    	@SubscribeEvent
    	public static void onWorldTick(WorldTickEvent event) {
    		WorldServer world = (WorldServer) event.world;
    		
    		
    		if (world.isRaining()) {
    			onTickSnowIncrease(world);
    		} else if (world.provider.isDaytime()) {
    			onTickSnowDecrease(world);
    		}
    	}
    	
    	private static BlockPos getRandomPosInChunk(Chunk chunk) {
			int j = chunk.x * 16;
			int k = chunk.z * 16;

			int posX = r.nextInt(16);
			int posZ = r.nextInt(16);
			return new BlockPos(j + posX, 0, k + posZ);
    	}
		
		private static BlockPos getSnowTopPosition(WorldServer world, BlockPos pos) {
			pos = world.getPrecipitationHeight(pos);
    		
    		// Precipitation height ignores snow blocks, need to loop to the top of the stack
    		while (world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER) {
    			pos = pos.up();
    		}
    		
    		return pos;
		}
		
		private static boolean isSnowyArea(WorldServer world, BlockPos pos) {
			return world.getBiome(pos).getFloatTemperature(pos) < 0.15F;
		}
    	
    	private static void onTickSnowDecrease(WorldServer world) {
    		for (Iterator<Chunk> iterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator()); iterator.hasNext();) {
    			Chunk chunk = iterator.next();
    			
    			if (r.nextInt(Config.snowMeltRate) != 0) {
    				continue;
    			}
    			
				BlockPos pos = getRandomPosInChunk(chunk);
    			
    			pos = getSnowTopPosition(world, pos);
    			
    			int layers = snowHeightAt(world, pos);
    			
    			if (layers <= Config.snowMinLayers) {
    				continue;
    			}
    			
    			if (layers % 8 != 1) {
    				// decrement layer
    				world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, (layers-1)%8));
    			} else {
    				//remove last layer
    				world.setBlockToAir(pos);
    			}
    		}
    	}
    	
		private static void onTickSnowIncrease(WorldServer world) {
    		int baseRate = Config.accumulationRate;

    		if (world.isThundering()) {
    			baseRate /= 2;
    		}
    		

    		for (Iterator<Chunk> iterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator()); iterator.hasNext();) {
    			Chunk chunk = iterator.next();
    			
    			if (r.nextInt(baseRate) != 0) {
    				continue;
    			}

    			if (!world.provider.canDoRainSnowIce(chunk)) {
    				continue;
    			}
    			
    			BlockPos pos = getRandomPosInChunk(chunk);
    			
    			pos = getSnowTopPosition(world, pos);
    			int layers = snowHeightAt(world, pos);
    			
    			int surroundingAtLayer = 0;
    			for(EnumFacing side : EnumFacing.HORIZONTALS){ 
    				if (snowHeightAt(world, getSnowTopPosition(world, pos.offset(side))) >= layers) {
    					surroundingAtLayer++;
    				}
    			}
    			
    			if (surroundingAtLayer < Config.smoothing) {
    				continue;
    			}
    			
				switch(Config.snowDriftArea) {
				case 9:
					incrementSnowHeight(world, pos.north().east());
				case 8:
					incrementSnowHeight(world, pos.south().west());
				case 7:
					incrementSnowHeight(world, pos.north().west());
				case 6:
					incrementSnowHeight(world, pos.south().east());
				case 5:
					incrementSnowHeight(world, pos.west());
				case 4:
					incrementSnowHeight(world, pos.east());
				case 3:
					incrementSnowHeight(world, pos.north());
				case 2:
					incrementSnowHeight(world, pos.south());
				case 1:
					incrementSnowHeight(world, pos);
				}
    		}
    	}
		
    	private static void incrementSnowHeight(WorldServer world, BlockPos pos) {
    		pos = getSnowTopPosition(world, pos);
    		
    		int layers = snowHeightAt(world, pos);

    		// Check if we can snow here if this is the first snow layer
			if(layers == 0 && !world.canSnowAt(pos, true)) {
				return;
			} else if (!isSnowyArea(world, pos)) {
				return;
			}
			
			if (layers >= Config.maxSnowLayers ) {
				return;
			}
			
			if (layers == 0 || layers % 8 != 0) {
				// Continue stacking on current stack
				world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, layers%8+1));
			} else {
				// Add onto stack on block above, this one is full
				world.setBlockState(pos.up(), Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, layers%8+1));
			}
		}
		private static int snowHeightAt(WorldServer world, BlockPos pos) {
			IBlockState currentBlock = world.getBlockState(pos);
			if (currentBlock.getBlock() == Blocks.SNOW_LAYER) {
				return snowHeightAt(world, pos.down()) + currentBlock.getValue(BlockSnow.LAYERS);
			}
			if (currentBlock.getBlock() == Blocks.AIR && world.getBlockState(pos.down()).getBlock() != Blocks.AIR) {
				return snowHeightAt(world, pos.down());
			}
			return 0;
    	}
    }
}
