package com.chriscarini.jetbrains.sdkcleaner;

import com.chriscarini.jetbrains.sdkcleaner.messages.PluginMessages;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class SdkUtils {
    @NonNls
    private static final Logger LOG = Logger.getInstance(SdkUtils.class);

    private SdkUtils() {
    }

    public static List<Sdk> getSDKs() {
        final ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        return List.of(jdkTable.getAllJdks());
    }

    public static void cleanSDKs(@NotNull final List<Sdk> sdks) {
        SdkUtils.cleanSDKs(sdks, null, null);
    }

    public static void cleanSDKs(
            @NotNull final List<Sdk> sdks,
            @Nullable final Consumer<AtomicInteger> runDuringCleanupConsumer,
            @Nullable final Consumer<AtomicInteger> runAfterCleanupConsumer
    ) {
        //noinspection UnstableApiUsage
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
            final AtomicInteger cleanCount = new AtomicInteger();
            IntStream.range(0, sdks.size()).forEach(index -> {
                final Sdk sdk = sdks.get(index);
                boolean result = SdkUtils.cleanSdkIfNeeded(sdk);
                if (result) {
                    cleanCount.addAndGet(1);
                }
                if (runDuringCleanupConsumer != null) {
                    runDuringCleanupConsumer.accept(new AtomicInteger(index));
                }
            });

            if (runAfterCleanupConsumer != null) {
                runAfterCleanupConsumer.accept(cleanCount);
            }
        });
    }

    public static boolean cleanSdkIfNeeded(@NotNull final Sdk sdk) {
        if (isSdkDeleted(sdk)) {
            LOG.debug("SDK HOME DIRECTORY DOES NOT EXIST: %s\n\nRemoving...%n", formatSdk(sdk));
            SdkConfigurationUtil.removeSdk(sdk);
            return true;
        }
        return false;
    }

    public static String formatSdk(final Sdk sdk) {
        return PluginMessages.get("sdk.cleaner.format.sdk.string", sdk.getName(), sdk.getHomePath());
    }

    public static boolean isSdkDeleted(final Sdk sdk) {
        final VirtualFile sdkHomeDirectory = sdk.getHomeDirectory();
        return (sdkHomeDirectory == null || !sdkHomeDirectory.exists());
    }

    public static void printSDKs() {
        SdkUtils.getSDKs().forEach(sdk -> LOG.debug(formatSdk(sdk)));
    }
}
