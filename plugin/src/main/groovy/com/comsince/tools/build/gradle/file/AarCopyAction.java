package com.comsince.tools.build.gradle.file;

/**
 * Created by liaojinlong on 17-1-20.
 */
import org.gradle.api.internal.file.CopyActionProcessingStreamAction;
import org.gradle.api.internal.file.copy.CopyAction;
import org.gradle.api.internal.file.copy.CopyActionProcessingStream;
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal;
import org.gradle.api.internal.tasks.SimpleWorkResult;
import org.gradle.api.tasks.WorkResult;

import java.io.File;

public class AarCopyAction implements CopyAction {

    private final File archiveFile;

    public AarCopyAction(File archiveFile) {
        this.archiveFile = archiveFile;
    }

    public WorkResult execute(CopyActionProcessingStream stream) {
        FileCopyDetailsInternalAction action = new FileCopyDetailsInternalAction();
        stream.process(action);
        return new SimpleWorkResult(action.didWork);
    }

    private class FileCopyDetailsInternalAction implements CopyActionProcessingStreamAction {
        private boolean didWork;

        private FileCopyDetailsInternalAction() {
        }

        public void processFile(FileCopyDetailsInternal details) {
            boolean copied = details.copyTo(archiveFile);
            if(copied) {
                this.didWork = true;
            }

        }
    }
}
