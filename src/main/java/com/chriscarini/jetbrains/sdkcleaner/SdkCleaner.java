package com.chriscarini.jetbrains.sdkcleaner;

import com.chriscarini.jetbrains.sdkcleaner.settings.SettingsManager;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.Sdk;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.openapi.application.PathManager.PROPERTY_PATHS_SELECTOR;
import static com.intellij.openapi.application.PathManager.PROPERTY_SYSTEM_PATH;


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

    @Nullable
    @Override
    public Object execute(@NotNull Continuation<? super Unit> $completion) {
        // triggered. #2
        if (!SettingsManager.getInstance().getState().cleanOnStartup) {
            return null;
        }

        final List<Sdk> sdks = SdkUtils.getSDKs();

        LOG.debug("Before SDK cleaning...");
        SdkUtils.printSDKs();

        SdkUtils.cleanSDKs(sdks, null);

        LOG.debug("After SDK cleaning...");
        SdkUtils.printSDKs();
        return null;
    }
}
