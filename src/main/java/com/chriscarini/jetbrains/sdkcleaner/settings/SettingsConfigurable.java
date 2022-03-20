package com.chriscarini.jetbrains.sdkcleaner.settings;

import com.chriscarini.jetbrains.sdkcleaner.SdkUtils;
import com.chriscarini.jetbrains.sdkcleaner.messages.PluginMessages;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.FormBuilder;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A {@link Configurable} that provides the user the ability to configure the SDK Cleaner plugin.
 */
public class SettingsConfigurable implements Configurable {
    private final JPanel mainPanel = new JBPanel<>();

    private final JBCheckBox cleanOnStartupField = new JBCheckBox();
    private final JBCheckBox cleanOnShutdownField = new JBCheckBox();

    private final JTextPane sdkCleanerStatsResult = new JTextPane();

    private final JButton cleanSdksNowButton = new JButton(PluginMessages.get("sdk.cleaner.settings.clean.now"));
    private final JBLabel cleanSdksNowResult = new JBLabel();

    public SettingsConfigurable() {
        buildMainPanel();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return PluginMessages.get("sdk.cleaner.settings.display.name.sdk.cleaner.configuration");
    }

    private void buildMainPanel() {
        // Create a simple form to display the user-configurable options
        mainPanel.setLayout(new VerticalFlowLayout(true, false));

        cleanSdksNowButton.setEnabled(true);
        cleanSdksNowButton.addActionListener(new CleanSdkListener());

        sdkCleanerStatsResult.setEnabled(false);
        sdkCleanerStatsResult.setVisible(true);
        sdkCleanerStatsResult.setContentType(ContentType.TEXT_HTML.getMimeType());
        updateSdkCleanerStatsResult();

        mainPanel.add(FormBuilder.createFormBuilder()
                .addLabeledComponent(PluginMessages.get("sdk.cleaner.settings.label.clean.on.startup"), cleanOnStartupField)
                .addSeparator()
                .addLabeledComponent(PluginMessages.get("sdk.cleaner.settings.label.clean.on.shutdown"), cleanOnShutdownField)
                .addSeparator()
                .addLabeledComponent(PluginMessages.get("sdk.cleaner.settings.label.stats"), sdkCleanerStatsResult)
                .addSeparator()
                .addLabeledComponent(cleanSdksNowButton, cleanSdksNowResult)
                .getPanel());
    }

    private void updateSdkCleanerStatsResult() {
        final List<Sdk> sdKs = SdkUtils.getSDKs();

        // compute how many SDKs need cleaning right now.
        final AtomicInteger needsCleaning = new AtomicInteger();
        sdKs.forEach(sdk -> {
            if (SdkUtils.isSdkDeleted(sdk)) {
                needsCleaning.addAndGet(1);
            }
        });

        sdkCleanerStatsResult.setText(PluginMessages.get("sdk.cleaner.settings.stats.result.table", sdKs.size(), needsCleaning.get()));
    }

    /**
     * Modifies the result field and button for SDK Cleaner 'clean now'
     *
     * @param text The result text to display
     */
    private void setSdkCleaningResult(@Nls(capitalization = Nls.Capitalization.Sentence) @NotNull final String text) {
        cleanSdksNowResult.setText(text);
        cleanSdksNowResult.setIcon(AllIcons.General.InspectionsOK);
        cleanSdksNowResult.setVisible(true);
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        // Set the user input field to contain the existing saved settings
        setUserInputFieldsFromSavedSettings();
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return !getSettingsFromUserInput().equals(getSettings());
    }

    /**
     * Apply the settings; saves the current user input list to the {@link SettingsManager}, and updates the table.
     */
    @Override
    public void apply() {
        final SettingsManager.SdkCleanerSettingsState settingsState = getSettingsFromUserInput();
        SettingsManager.getInstance().loadState(settingsState);
        setUserInputFieldsFromSavedSettings();
    }

    @NotNull
    private SettingsManager.SdkCleanerSettingsState getSettingsFromUserInput() {
        final SettingsManager.SdkCleanerSettingsState settingsState = new SettingsManager.SdkCleanerSettingsState();

        settingsState.cleanOnStartup = cleanOnStartupField.isSelected();
        settingsState.cleanOnShutdown = cleanOnShutdownField.isSelected();

        return settingsState;
    }

    /**
     * Get the saved settings and update the user input field.
     */
    private void setUserInputFieldsFromSavedSettings() {
        updateUserInputFields(getSettings());
    }

    /**
     * Update the user input field based on the input value provided by {@code val}
     *
     * @param settings The {@link SettingsManager.SdkCleanerSettingsState} for the plugin.
     */
    private void updateUserInputFields(@Nullable final SettingsManager.SdkCleanerSettingsState settings) {
        if (settings == null) {
            return;
        }

        cleanOnStartupField.setSelected(settings.cleanOnStartup);
        cleanOnShutdownField.setSelected(settings.cleanOnShutdown);
    }

    @NotNull
    private SettingsManager.SdkCleanerSettingsState getSettings() {
        return SettingsManager.getInstance().getState();
    }

    /**
     * An {@link ActionListener} for clean SDKs.
     */
    private final class CleanSdkListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SdkUtils.cleanSDKs(SdkUtils.getSDKs(), null, cleanCount -> {
                setSdkCleaningResult(PluginMessages.get("sdk.cleaner.settings.clean.now.results", cleanCount.get()));
                updateSdkCleanerStatsResult();
            });
        }
    }
}
