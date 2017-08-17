package cam72cam.winterwonderland;

import net.minecraftforge.common.config.Config.Comment;

@net.minecraftforge.common.config.Config(modid=WinterWonderLand.MODID)
public class Config {
	@Comment({"How quickly snow accumulates [200 = slow, 10 = fast]"})
	public static int accumulationRate = 200;
	
	@Comment({"How many sides need to be at the same level before more snow is allowed to accumulate [0 = none, 4 = all]"})
	public static int smoothing = 2;
	
	@Comment({"Number of blocks to increase the snow level at around a snow accumulation event [1 = single block, 5 = a + sign, 9 = all around"})
	public static int snowDriftArea = 5;
	
	@Comment({"Max snow layers, 8 layers per block"})
	public static int maxSnowLayers = 8;
	
	@Comment({"How quickly snow melts [400 = slow, 20 = fast]"})
	public static int snowMeltRate = 400;
	
	@Comment({"Base snow layers to leave when melting"})
	public static int snowMinLayers = 1;
}
