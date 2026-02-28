package starred.skies.odin.mixin.mixins;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import starred.skies.odin.events.InputEvent;
import starred.skies.odin.features.impl.cheats.FarmKeys;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void onTurnPlayer(CallbackInfo ci) {
        if (FarmKeys.getLock()) ci.cancel();
    }

    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void onButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        if (action == 1) {
            if (new InputEvent.Mouse.Press(buttonInfo).postAndCatch()) ci.cancel();
        } else if (action == 0) {
            new InputEvent.Mouse.Release(buttonInfo).postAndCatch();
        }
    }
}