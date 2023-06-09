package live.itrip.jvmm.monitor.convey;

import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import live.itrip.jvmm.logging.DefaultImplLogger;
import live.itrip.jvmm.logging.LoggerLevel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Description: TODO
 * </p>
 * <p>
 * Created in 3:33 下午 2021/12/11
 *
 * @author fengjianfeng
 */
public class DefaultInternalLoggerFactory extends InternalLoggerFactory {

    private static DefaultInternalLoggerFactory INSTANCE = null;
    private static final Map<String, InternalLogger> loggerMap = new ConcurrentHashMap<>();
    static LoggerLevel level = LoggerLevel.INFO;

    private DefaultInternalLoggerFactory(){}

    public static DefaultInternalLoggerFactory newInstance() {
        return newInstance(LoggerLevel.INFO);
    }

    public static DefaultInternalLoggerFactory newInstance(LoggerLevel level) {
        if (INSTANCE == null) {
            synchronized (DefaultInternalLoggerFactory.class) {
                if (INSTANCE != null) {
                    return INSTANCE;
                }
                INSTANCE = new DefaultInternalLoggerFactory();
                setLevel(level);
                return INSTANCE;
            }
        } else {
            return INSTANCE;
        }
    }

    private static void setLevel(LoggerLevel lvl) {
        level = lvl;
    }

    @Override
    protected InternalLogger newInstance(String name) {
        InternalLogger logger = loggerMap.get(name);
        if (logger != null) {
            return logger;
        }
        logger = new DefaultInternalLogger(name);
        loggerMap.put(name, logger);
        return logger;
    }

    static class DefaultInternalLogger extends DefaultImplLogger implements InternalLogger {

        private final String name;

        public DefaultInternalLogger(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void trace(Throwable t) {
            super.trace(t.getMessage(), t);
        }

        @Override
        public void debug(Throwable t) {
            super.debug(t.getMessage(), t);
        }

        @Override
        public void info(Throwable t) {
            super.info(t.getMessage(), t);
        }

        @Override
        public void warn(Throwable t) {
            super.warn(t.getMessage(), t);
        }

        @Override
        public void error(Throwable t) {
            super.error(t.getMessage(), t);
        }

        @Override
        public boolean isEnabled(InternalLogLevel level) {
            return true;
        }

        @Override
        public void log(InternalLogLevel level, String msg) {
            switch (level) {
                case INFO:
                    info(msg);
                    break;
                case WARN:
                    warn(msg);
                    break;
                case DEBUG:
                    debug(msg);
                    break;
                case ERROR:
                    error(msg);
                    break;
                case TRACE:
                    trace(msg);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void log(InternalLogLevel level, String format, Object arg) {
            switch (level) {
                case INFO:
                    info(format, arg);
                    break;
                case WARN:
                    warn(format, arg);
                    break;
                case DEBUG:
                    debug(format, arg);
                    break;
                case ERROR:
                    error(format, arg);
                    break;
                case TRACE:
                    trace(format, arg);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void log(InternalLogLevel level, String format, Object argA, Object argB) {
            switch (level) {
                case INFO:
                    info(format, argA, argB);
                    break;
                case WARN:
                    warn(format, argA, argB);
                    break;
                case DEBUG:
                    debug(format, argA, argB);
                    break;
                case ERROR:
                    error(format, argA, argB);
                    break;
                case TRACE:
                    trace(format, argA, argB);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void log(InternalLogLevel level, String format, Object... arguments) {
            switch (level) {
                case INFO:
                    info(format, arguments);
                    break;
                case WARN:
                    warn(format, arguments);
                    break;
                case DEBUG:
                    debug(format, arguments);
                    break;
                case ERROR:
                    error(format, arguments);
                    break;
                case TRACE:
                    trace(format, arguments);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void log(InternalLogLevel level, String msg, Throwable t) {
            switch (level) {
                case INFO:
                    info(msg, t);
                    break;
                case WARN:
                    warn(msg, t);
                    break;
                case DEBUG:
                    debug(msg, t);
                    break;
                case ERROR:
                    error(msg, t);
                    break;
                case TRACE:
                    trace(msg, t);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void log(InternalLogLevel level, Throwable t) {
            switch (level) {
                case INFO:
                    info(t);
                    break;
                case WARN:
                    warn(t);
                    break;
                case DEBUG:
                    debug(t);
                    break;
                case ERROR:
                    error(t);
                    break;
                case TRACE:
                    trace(t);
                    break;
                default:
                    break;
            }
        }
    }
}
