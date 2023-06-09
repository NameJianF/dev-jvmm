package live.itrip.jvmm.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author fengjianfeng
 * @version v1.0
 * @date 2021/1/29 11:37
 * description: SingleLineFormatter
 */
public class SingleLineFormatter extends Formatter {

    private Date date = new Date();
    private final static String FORMAT = "{0,date} {0,time}";
    private MessageFormat formatter;
    private Object[] objArgs = new Object[1];
    private String lineSeparator = System.getProperty("line.separator");

    @Override
    public synchronized String format(LogRecord record) {

        StringBuilder sb = new StringBuilder();

        // Level
        sb.append("[");
        sb.append(record.getLevel().getName().toUpperCase());
        sb.append("] ");

        // Minimize memory allocations here.
        date.setTime(record.getMillis());
        objArgs[0] = date;

        // Date and time
        StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(FORMAT);
        }
        formatter.format(objArgs, text, null);
        sb.append(text);
        sb.append(" ");

        // Class name
        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
        } else {
            sb.append(record.getLoggerName());
        }

        // Method name
        if (record.getSourceMethodName() != null) {
            sb.append(".");
            sb.append(record.getSourceMethodName());
        }
        // lineSeparator
        sb.append(" - ");

        String message = formatMessage(record);

        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ignored) {

            }
        }
        return sb.toString();
    }
}