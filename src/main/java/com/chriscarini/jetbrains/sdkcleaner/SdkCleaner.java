package com.chriscarini.jetbrains.sdkcleaner;

import com.chriscarini.jetbrains.sdkcleaner.messages.PluginMessages;
import com.chriscarini.jetbrains.sdkcleaner.settings.SettingsManager;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.openapi.application.PathManager.*;


public class SdkCleaner extends PreloadingActivity implements AppLifecycleListener {
    private static final Logger LOG = Logger.getInstance(SdkCleaner.class);

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        // triggered. #1
//        SdkUtils.printSDKs();

        String foo = System.getProperty(PROPERTY_SYSTEM_PATH);
        String bar = PathManager.getSystemPath();
        String ba = System.getProperty(PROPERTY_PATHS_SELECTOR);
    }

    @Override
    public void appWillBeClosed(boolean isRestart) {
        if (!SettingsManager.getInstance().getState().cleanOnShutdown) {
            return;
        }
        LOG.debug("Before SDK cleaning...");
        SdkUtils.printSDKs();

        SdkUtils.cleanSDKs(SdkUtils.getSDKs());

        LOG.debug("After SDK cleaning...");
        SdkUtils.printSDKs();
    }

    @Override
    public void preload(@NotNull ProgressIndicator indicator) {
        // triggered. #2
        if (!SettingsManager.getInstance().getState().cleanOnStartup) {
            return;
        }

        final List<Sdk> sdks = SdkUtils.getSDKs();
        indicator.setText(PluginMessages.get("sdk.cleaner.opening.progress.indicator.in.progress", sdks.size()));

        LOG.debug("Before SDK cleaning...");
        SdkUtils.printSDKs();

        SdkUtils.cleanSDKs(sdks, index -> indicator.setFraction((float) index.get() / sdks.size()), null);

        LOG.debug("After SDK cleaning...");
        SdkUtils.printSDKs();

        indicator.setText(PluginMessages.get("sdk.cleaner.opening.progress.indicator.done", sdks.size()));
    }
}
