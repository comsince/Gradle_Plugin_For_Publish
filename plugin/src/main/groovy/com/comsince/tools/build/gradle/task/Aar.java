package com.comsince.tools.build.gradle.task;

import com.comsince.tools.build.gradle.file.AarCopyAction;

import org.gradle.api.internal.file.copy.CopyAction;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

/**
 * Created by liaojinlong on 17-1-20.
 */

public class Aar extends AbstractArchiveTask {
    public static final String AAR_EXTENSION = "aar";

    public Aar() {
        this.setExtension(AAR_EXTENSION);
    }

    @Override
    protected CopyAction createCopyAction() {
        return new AarCopyAction(getArchivePath());
    }
}
