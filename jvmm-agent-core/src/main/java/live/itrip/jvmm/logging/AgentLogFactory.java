package live.itrip.jvmm.logging;

import com.google.common.base.Strings;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

/**
 * @author fengjianfeng
 * @version v1.0
 * @date 2021/1/29 11:33
 * description： Agent Logger
 */
public class AgentLogFactory {
    /**
     * 50 M = 1024 * 1024 * 50
     */
    private static final int MAX_FILE_SIZE = 52428800;
    private static final int MAX_BACKUP_INDEX = 1;
    private static String LOG_DIR_PATH = "/export/Logs/jdd-mock";

    public static Logger getLogger(Class<?> c) {
        Logger logger = Logger.getLogger(c.getName());
        logger.setUseParentHandlers(false);

        FileHandler fileHandler = null;

        try {
            fileHandler = getFileHandler(LOG_DIR_PATH, null);
        } catch (Exception ex) {
            fileHandler = getFileHandler(null, null);
        }

        ConsoleHandler consoleHandler = getConsoleHandler();
        Formatter formatter = new SingleLineFormatter();
        fileHandler.setFormatter(formatter);
        consoleHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);
        logger.addHandler(consoleHandler);
        return logger;
    }

    /**
     * log to file
     *
     * @param logRoot  log dir root
     * @param filename log file name
     * @return file handler
     */
    private static FileHandler getFileHandler(String logRoot, String filename) {
        String homePath = null;
        String filePath = null;

        if (logRoot != null) {
            File f = new File(logRoot.trim());
            if ((f.isDirectory()) && (f.canWrite())) {
                homePath = logRoot;
            }
        }
        if (Strings.isNullOrEmpty(homePath)) {
            homePath = System.getProperties().get("user.home") + File.separator + "jdd-mock";
        }

        File homeDir = new File(homePath);
        if (!homeDir.exists()) {
            homeDir.mkdir();
        }

        if (Strings.isNullOrEmpty(filename)) {
            //获取时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            String dateStr = sdf.format(now);
            filename = "log_" + dateStr;
        }

        filePath = homePath + File.separator + filename + ".log";

        try {
            FileHandler fileHandler = new FileHandler(filePath, MAX_FILE_SIZE, MAX_BACKUP_INDEX);
//            fileHandler.setFormatter(new SimpleFormatter());
            return fileHandler;
        } catch (Exception e) {
            throw new RuntimeException("Failed to init FileHandler: " + filePath);
        }
    }

    /**
     * log to console
     *
     * @return console handler
     */
    private static ConsoleHandler getConsoleHandler() {
        return new ConsoleHandler();
    }
}
