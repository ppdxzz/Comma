package cn.youhaveme.comma.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * Provides controller functionality for application settings.
 */
public class CommaSettingsConfigurable implements Configurable {
    private CommaSettingsComponent commaSettingsComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Comma";
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return commaSettingsComponent.getPreferredFocusedComponent();
    }

    @Override
    public @Nullable JComponent createComponent() {
        commaSettingsComponent = new CommaSettingsComponent();
        return commaSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        CommaSettingsState.State settings = Objects.requireNonNull(CommaSettingsState.getInstance().getState());
        boolean modified = !commaSettingsComponent.getLimitMergeLinesText().equals(settings.limitMergeLines);
        modified |= commaSettingsComponent.getSymbolCheckBoxSelected() != settings.symbolCheckBoxSelected;
        return modified;
    }

    @Override
    public void apply() {
        CommaSettingsState.State settings = Objects.requireNonNull(CommaSettingsState.getInstance().getState());
        settings.limitMergeLines = commaSettingsComponent.getLimitMergeLinesText();
        settings.symbolCheckBoxSelected = commaSettingsComponent.getSymbolCheckBoxSelected();
    }

    @Override
    public void reset() {
        CommaSettingsState.State settings = Objects.requireNonNull(CommaSettingsState.getInstance().getState());
        commaSettingsComponent.setLimitMergeLinesText(settings.limitMergeLines);
        commaSettingsComponent.setSymbolCheckBoxSelected(settings.symbolCheckBoxSelected);
    }

    @Override
    public void disposeUIResources() {
        commaSettingsComponent = null;
    }
}
