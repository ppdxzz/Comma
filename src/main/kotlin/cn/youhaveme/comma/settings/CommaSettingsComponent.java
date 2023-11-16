package cn.youhaveme.comma.settings;

import com.intellij.ui.components.JBCheckBox;
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
    private final JBCheckBox trimWhiteSpaceStatus = new JBCheckBox("Do you want to remove spaces? ");

    public CommaSettingsComponent() {
        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Limit merge lines: "), limitMergeLinesText, 1, false)
                .addTooltip("It will be merged into one row of results.")
                .addComponent(trimWhiteSpaceStatus, 1)
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

    public boolean getTrimWhiteSpaceStatus() {
        return trimWhiteSpaceStatus.isSelected();
    }

    public void setTrimWhiteSpaceStatus(boolean newStatus) {
        trimWhiteSpaceStatus.setSelected(newStatus);
    }

}
