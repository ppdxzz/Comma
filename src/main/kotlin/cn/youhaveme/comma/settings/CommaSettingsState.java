package cn.youhaveme.comma.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
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
public class CommaSettingsState implements PersistentStateComponent<CommaSettingsState> {

    /**
     * 默认最大合并行数
     */
    public String limitMergeLines = "3";
    /**
     * 是否去除空格
     */
    public boolean trimWhiteSpaceStatus = false;

    public static CommaSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(CommaSettingsState.class);
    }

    @Override
    public @Nullable CommaSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CommaSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
