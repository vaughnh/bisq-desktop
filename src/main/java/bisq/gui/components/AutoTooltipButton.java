package bisq.gui.components;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;

import com.sun.javafx.scene.control.skin.ButtonSkin;

import static bisq.gui.components.TooltipUtil.showTooltipIfTruncated;

public class AutoTooltipButton extends Button {

    public AutoTooltipButton() {
        super();
    }

    public AutoTooltipButton(String text) {
        super(text);
    }

    public AutoTooltipButton(String text, Node graphic) {
        super(text, graphic);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutoTooltipButtonSkin(this);
    }

    private class AutoTooltipButtonSkin extends ButtonSkin {
        public AutoTooltipButtonSkin(Button button) {
            super(button);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);
            showTooltipIfTruncated(this, getSkinnable());
        }
    }
}
