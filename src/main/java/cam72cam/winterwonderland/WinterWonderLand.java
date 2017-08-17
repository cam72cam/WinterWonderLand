package cam72cam.winterwonderland;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@Mod(modid = WinterWonderLand.MODID, version = WinterWonderLand.VERSION, acceptedMinecraftVersions = "[1.12,1.13)")
public class WinterWonderLand
{
    public static final String MODID = "winterwonderland";
    public static final String VERSION = "1.0";
    
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
    			if (layers > 1 && layers < 8) {
    				setSnowHeight(world, pos, layers+1);
    			}
    		}
    	}
    	private static void setSnowHeight(WorldServer world, BlockPos pos, int layers) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockSnow.LAYERS, layers + 1));
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
