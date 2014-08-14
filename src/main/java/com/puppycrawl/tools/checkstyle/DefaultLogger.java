package com.puppycrawl.tools.checkstyle;

import java.io.OutputStream;
import java.io.PrintWriter;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

public class DefaultLogger extends AutomaticBean implements AuditListener {
    private final PrintWriter info;
    private final PrintWriter error;
    private final boolean closeInfoAfterUse;
    private final boolean closeErrorAfterUse;

    public DefaultLogger(OutputStream os, boolean closeStreamsAfterUse) {
        // no need to close aOS twice
        this(os, closeStreamsAfterUse, os, false);
    }

    public DefaultLogger(OutputStream info,
                         boolean closeInfoAfterUse,
                         OutputStream error,
                         boolean closeErrorAfterUse) {

        this.info = new PrintWriter(info);
        this.closeInfoAfterUse = closeInfoAfterUse;

        this.error = new PrintWriter(error);
        this.closeErrorAfterUse = closeErrorAfterUse;
    }

    public void auditStarted(AuditEvent aEvt) {}

    public void auditFinished(AuditEvent aEvt) {
        info.flush();
        if (closeInfoAfterUse) {
            info.close();
        }

        error.flush();
        if (closeErrorAfterUse) {
            error.close();
        }
    }

    public void fileStarted(AuditEvent event) {}

    public void fileFinished(AuditEvent event) {}

    public void addError(AuditEvent event) {
        printEvent(event);
    }

    public void addException(AuditEvent event, Throwable aThrowable) {
        printEvent(event);
    }

    private void printEvent(AuditEvent event) {
        error.println(event.getFileName() + ":"
            + event.getLine() + ":"
            + event.getColumn() + "::"
            + event.getSourceName() + ":"
            + event.getSeverityLevel() + ":"
            + event.getMessage());
    }
}
