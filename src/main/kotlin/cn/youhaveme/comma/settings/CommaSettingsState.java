package cn.youhaveme.comma.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "cn.youhaveme.comma.settings.CommaSettingsState",
    storages = @Storage("SdkSettingsPlugin.xml")
)
public class CommaSettingsState implements PersistentStateComponent<CommaSettingsState.State> {

    public static class State {
        /**
         * 默认最大合并行数
         */
        @NonNls
        public String limitMergeLines = "3";
        /**
         * 默认添加符号
         */
        public boolean symbolCheckBoxSelected = true;
    }

    private State appState = new State();

    public static CommaSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(CommaSettingsState.class);
    }

    @Override
    public @Nullable CommaSettingsState.State getState() {
        return appState;
    }

    @Override
    public void loadState(@NotNull State state) {
        appState = state;
    }
}
