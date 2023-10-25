package cn.youhaveme.comma.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        CommaSettingsState settings = CommaSettingsState.getInstance();
        boolean modified = !commaSettingsComponent.getLimitMergeLinesText().equals(settings.limitMergeLines);
        return modified;
    }

    @Override
    public void apply() {
        CommaSettingsState settings = CommaSettingsState.getInstance();
        settings.limitMergeLines = commaSettingsComponent.getLimitMergeLinesText();
    }

    @Override
    public void reset() {
        CommaSettingsState settings = CommaSettingsState.getInstance();
        commaSettingsComponent.setLimitMergeLinesText(settings.limitMergeLines);
    }

    @Override
    public void disposeUIResources() {
        commaSettingsComponent = null;
    }
}
