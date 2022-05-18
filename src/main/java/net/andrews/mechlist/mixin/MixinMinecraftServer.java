package net.andrews.mechlist.mixin;

import net.andrews.mechlist.MechList;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Inject(
		method = "shutdown",
		at = @At("HEAD")
	)
	private void onShutdown(CallbackInfo ci) {
		MechList.EXECUTOR.shutdown();
	}
}
