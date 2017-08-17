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
    public static final String VERSION = "1.1.1";
    
    @EventBusSubscriber
    public static class EventHander {
    	private static Random r = new Random();
    	
    	@SubscribeEvent
    	public static void onWorldTick(WorldTickEvent event) {
    		WorldServer world = (WorldServer) event.world;
    		
    		if (!world.isRaining()) {
    			return;
    		}
    		
    		int baseRate = Config.accumulationRate;

    		if (world.isThundering()) {
    			baseRate /= 2;
    		}
    		

    		for (Iterator<Chunk> iterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator()); iterator.hasNext();) {
    			Chunk chunk = iterator.next();

    			if (!world.provider.canDoRainSnowIce(chunk)) {
    				continue;
    			}
    			
    			if (r.nextInt(baseRate) != 0) {
    				continue;
    			}

    			int j = chunk.x * 16;
    			int k = chunk.z * 16;

    			int posX = r.nextInt(16);
    			int posZ = r.nextInt(16);
    			
    			BlockPos pos = world.getPrecipitationHeight(new BlockPos(j + posX, 0, k + posZ));
    			int layers = snowHeightAt(world, pos);
    			
    			int surroundingAtLayer = 0;
    			for(EnumFacing side : EnumFacing.HORIZONTALS){ 
    				if (snowHeightAt(world, pos.offset(side)) <= layers) {
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
    		pos = world.getPrecipitationHeight(pos);
    		int layers = snowHeightAt(world, pos);

			if(layers == 0 && !world.canSnowAt(pos, true)) {
				return;
			}
			
			if (layers < 8) {
				world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, layers+1));
			}
		}
		private static int snowHeightAt(WorldServer world, BlockPos pos) {
			IBlockState currentBlock = world.getBlockState(pos);
			if (currentBlock.getBlock() == Blocks.SNOW_LAYER) {
				return currentBlock.getValue(BlockSnow.LAYERS);
			}
			return 0;
    	}
    }
}
