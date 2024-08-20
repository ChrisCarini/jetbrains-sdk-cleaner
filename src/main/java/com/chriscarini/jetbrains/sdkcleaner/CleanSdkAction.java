package com.chriscarini.jetbrains.sdkcleaner;

import com.chriscarini.jetbrains.sdkcleaner.messages.PluginMessages;
import com.intellij.ide.ui.ProductIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;

import java.util.List;

public class CleanSdkAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(CleanSdkAction.class);

    public CleanSdkAction() {
        super("Clean SDKs", "Clean the SDKs...", ProductIcons.getInstance().getProductIcon());
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();

        LOG.debug("Before SDK cleaning...");
        SdkUtils.printSDKs();

        final List<Sdk> sdKs = SdkUtils.getSDKs();
        SdkUtils.cleanSDKs(sdKs, cleanCount -> Messages.showMessageDialog(
                project,
                PluginMessages.get("sdk.cleaner.settings.clean.now.results", cleanCount.get()),
                PluginMessages.get("sdk.cleaner.settings.clean.now.results", cleanCount.get()),
                Messages.getInformationIcon()
        ));

        LOG.debug("After SDK cleaning...");
        SdkUtils.printSDKs();
    }
}
