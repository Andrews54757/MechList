package net.andrews.mechlist.mixin;

import com.mojang.brigadier.CommandDispatcher;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.At;

import net.andrews.mechlist.MechListMod;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

@Mixin(CommandManager.class)
public class MixinCommandManager {
    @Shadow
    @Final
    private CommandDispatcher<ServerCommandSource> dispatcher;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onRegister(CommandManager.RegistrationEnvironment arg, CallbackInfo ci) {
        MechListMod.registerCommands(this.dispatcher);
    }
}