package cam72cam.winterwonderland;

import net.minecraftforge.common.config.Config.Comment;

@net.minecraftforge.common.config.Config(modid=WinterWonderLand.MODID)
public class Config {
	@Comment({"How quickly snow accumulates [200 = slow, 10 = fast]"})
	public static int accumulationRate = 100;
}
