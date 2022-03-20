package com.chriscarini.jetbrains.sdkcleaner.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * The {@link SettingsManager} for this plugin; settings stored out to and read from {@code jetbrains-sdk-cleaner.xml}.
 */
@State(name = "jetbrains-sdk-cleaner", storages = @Storage(value = "jetbrains-sdk-cleaner.xml"))
public class SettingsManager implements PersistentStateComponent<SettingsManager.SdkCleanerSettingsState> {
    private SdkCleanerSettingsState myState;

    public static SettingsManager getInstance() {
        return ApplicationManager.getApplication().getService(SettingsManager.class);
    }

    @NotNull
    @Override
    public SettingsManager.SdkCleanerSettingsState getState() {
        if (myState == null) {
            myState = new SdkCleanerSettingsState();
        }
        return myState;
    }

    @Override
    public void loadState(@NotNull final SettingsManager.SdkCleanerSettingsState sdkCleanerSettingsState) {
        myState = sdkCleanerSettingsState;
    }

    /**
     * Representation of the Iris Settings {@link State}.
     */
    public static class SdkCleanerSettingsState {
        public boolean cleanOnStartup;
        public boolean cleanOnShutdown;

        @SuppressWarnings("WeakerAccess")
        public SdkCleanerSettingsState() {
            this.cleanOnStartup = false;
            this.cleanOnShutdown = false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SdkCleanerSettingsState that = (SdkCleanerSettingsState) o;
            return cleanOnStartup == that.cleanOnStartup && cleanOnShutdown == that.cleanOnShutdown;
        }

        @Override
        public int hashCode() {
            return Objects.hash(cleanOnStartup, cleanOnShutdown);
        }
    }
}
