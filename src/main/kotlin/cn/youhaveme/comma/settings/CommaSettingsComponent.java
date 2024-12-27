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
    private final JCheckBox symbolCheckBox = new JCheckBox("Default to add symbols to the results");

    public CommaSettingsComponent() {
        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator("Merge"))
                .setFormLeftIndent(20)
                .addLabeledComponent(new JBLabel("Merge lines: "), limitMergeLinesText)
                .addTooltip("It will be merged into one row of results.")
                .setFormLeftIndent(0)
                .addComponent(new TitledSeparator("Other"))
                .setFormLeftIndent(20)
                .addComponent(symbolCheckBox)
                .setFormLeftIndent(0)
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

    public boolean getSymbolCheckBoxSelected() {
        return symbolCheckBox.isSelected();
    }

    public void setSymbolCheckBoxSelected(boolean selected) {
        symbolCheckBox.setSelected(selected);
    }

}
