package com.puppycrawl.tools.checkstyle;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

public class DefaultLogger
    extends AutomaticBean
    implements AuditListener
{
    /** cushion for avoiding StringBuffer.expandCapacity */
    private static final int BUFFER_CUSHION = 12;

    /** where to write info messages **/
    private final PrintWriter infoWriter;
    /** close info stream after use */
    private final boolean closeInfo;

    /** where to write error messages **/
    private final PrintWriter errorWriter;
    /** close error stream after use */
    private final boolean closeError;

    /**
     * Creates a new <code>DefaultLogger</code> instance.
     * @param os where to log infos and errors
     * @param closeStreamsAfterUse if oS should be closed in auditFinished()
     * @exception UnsupportedEncodingException if there is a problem to use UTF-8 encoding
     */
    public DefaultLogger(OutputStream os, boolean closeStreamsAfterUse)
            throws UnsupportedEncodingException
    {
        // no need to close oS twice
        this(os, closeStreamsAfterUse, os, false);
    }

    /**
     * Creates a new <code>DefaultLogger</code> instance.
     *
     * @param infoStream the <code>OutputStream</code> for info messages
     * @param closeInfoAfterUse auditFinished should close infoStream
     * @param errorStream the <code>OutputStream</code> for error messages
     * @param closeErrorAfterUse auditFinished should close errorStream
     * @exception UnsupportedEncodingException if there is a problem to use UTF-8 encoding
     */
    public DefaultLogger(OutputStream infoStream,
                         boolean closeInfoAfterUse,
                         OutputStream errorStream,
                         boolean closeErrorAfterUse) throws UnsupportedEncodingException
    {
        closeInfo = closeInfoAfterUse;
        closeError = closeErrorAfterUse;
        final Writer infoStreamWriter = new OutputStreamWriter(infoStream, "UTF-8");
        final Writer errorStreamWriter = new OutputStreamWriter(errorStream, "UTF-8");
        infoWriter = new PrintWriter(infoStreamWriter);
        errorWriter = infoStream == errorStream
            ? infoWriter
            : new PrintWriter(errorStreamWriter);
    }

    /**
     * Print an Emacs compliant line on the error stream.
     * If the column number is non zero, then also display it.
     * @param evt {@inheritDoc}
     * @see AuditListener
     **/
    @Override
    public void addError(AuditEvent evt)
    {
        printEvent(evt);
    }

    /** {@inheritDoc} */
    @Override
    public void addException(AuditEvent evt, Throwable throwable)
    {
        synchronized (errorWriter) {
            printEvent(evt);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void auditStarted(AuditEvent evt)
    {
    }

    /** {@inheritDoc} */
    @Override
    public void fileFinished(AuditEvent evt)
    {
    }

    /** {@inheritDoc} */
    @Override
    public void fileStarted(AuditEvent evt)
    {
    }

    /** {@inheritDoc} */
    @Override
    public void auditFinished(AuditEvent evt)
    {
        closeStreams();
    }

    private void printEvent(AuditEvent event) {
        errorWriter.println(event.getFileName() + ":"
                          + event.getLine() + ":"
                          + event.getColumn() + "::"
                          + event.getSourceName() + ":"
                          + event.getSeverityLevel() + ":"
                          + event.getMessage());
    }

    /**
     * Flushes the output streams and closes them if needed.
     */
    protected void closeStreams()
    {
        infoWriter.flush();
        if (closeInfo) {
            infoWriter.close();
        }

        errorWriter.flush();
        if (closeError) {
            errorWriter.close();
        }
    }
}
