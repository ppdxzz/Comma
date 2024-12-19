package cn.youhaveme.comma.settings;

import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class CommaSettingsComponent {
    private final JPanel mainPanel;
    private final NumericTextField limitMergeLinesText = new NumericTextField();

    public CommaSettingsComponent() {
        JBLabel limitLabel = new JBLabel("Limit merge lines: ");
        limitLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator("Merge"))
                .addLabeledComponent(limitLabel, limitMergeLinesText)
                .addTooltip("It will be merged into one row of results.")
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return limitMergeLinesText;
    }

    @NotNull
    public String getLimitMergeLinesText() {
        return limitMergeLinesText.getText();
    }

    public void setLimitMergeLinesText(@NotNull String newText) {
        limitMergeLinesText.setText(newText);
    }


}
