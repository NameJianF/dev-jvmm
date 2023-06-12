package live.itrip.jvmm.server;

import com.sun.net.httpserver.HttpServer;
import live.itrip.jvmm.logging.AgentLogFactory;
import live.itrip.jvmm.monitor.controller.CollectController;
import live.itrip.jvmm.server.handler.CommandListener;
import live.itrip.jvmm.server.handler.HttpCommandHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author : ERP --> fengjianfeng4
 * @date : 2021-08-16 17:44
 * description : AgentHttpServer
 **/
public class AgentHttpServer {
    private static final Logger LOGGER = AgentLogFactory.getLogger(AgentHttpServer.class);

    static {
        if (!System.getProperties().containsKey("sun.net.httpserver.maxReqTime")) {
            System.setProperty("sun.net.httpserver.maxReqTime", "60");
        }

        if (!System.getProperties().containsKey("sun.net.httpserver.maxRspTime")) {
            System.setProperty("sun.net.httpserver.maxRspTime", "600");
        }
    }

    protected final HttpServer server;
    protected final ExecutorService executorService;

    /**
     * Start a HTTP server
     * The {@code httpServer} is expected to already be bound to an address
     */
    public AgentHttpServer(HttpServer httpServer, boolean daemon, CommandListener commandListener) {
        if (httpServer.getAddress() == null) {
            throw new IllegalArgumentException("HttpServer hasn't been bound to an address");
        }

        this.server = httpServer;
        HttpCommandHandler mHandler = new HttpCommandHandler(commandListener);
        mHandler.registryPath(this.server);

        CollectController collectController = new CollectController();
        collectController.registryPath(this.server);

        this.executorService = Executors.newFixedThreadPool(5, NamedDaemonThreadFactory.defaultThreadFactory(daemon));
        this.server.setExecutor(this.executorService);
        this.start(daemon);
        LOGGER.info(String.format("start agent http server[%s:%s].", this.server.getAddress().getHostString(), this.getPort()));
    }

    /**
     * Start a HTTP server
     */
    public AgentHttpServer(InetSocketAddress addr, boolean daemon, CommandListener commandListener) throws IOException {
        this(HttpServer.create(addr, 3), daemon, commandListener);
    }

    /**
     * Start a HTTP server
     */
    public AgentHttpServer(String host, int port, boolean daemon, CommandListener commandListener) throws IOException {
        this(new InetSocketAddress(host, port), daemon, commandListener);
    }

    /**
     * Start a HTTP server by making sure that its background thread inherit proper daemon flag.
     */
    private void start(boolean daemon) {
        if (daemon == Thread.currentThread().isDaemon()) {
            this.server.start();
        } else {
            FutureTask<Void> startTask = new FutureTask<Void>(new Runnable() {
                @Override
                public void run() {
                    server.start();
                }
            }, null);
            NamedDaemonThreadFactory.defaultThreadFactory(daemon).newThread(startTask).start();
            try {
                startTask.get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Unexpected exception on starting HttpSever", e);
            } catch (InterruptedException e) {
                // This is possible only if the current tread has been interrupted,
                // but in real use cases this should not happen.
                // In any case, there is nothing to do, except to propagate interrupted flag.
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Stop the Http server.
     */
    public void stop() {
        this.server.stop(0);
        // Free any (parked/idle) threads in pool
        this.executorService.shutdown();
    }

    /**
     * Gets the port number.
     */
    public int getPort() {
        return this.server.getAddress().getPort();
    }

    static class NamedDaemonThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        private final int poolNumber = POOL_NUMBER.getAndIncrement();
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadFactory delegate;
        private final boolean daemon;

        NamedDaemonThreadFactory(ThreadFactory delegate, boolean daemon) {
            this.delegate = delegate;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = delegate.newThread(r);
            t.setName(String.format("agent-http-%d-%d", poolNumber, threadNumber.getAndIncrement()));
            t.setDaemon(daemon);
            return t;
        }

        static ThreadFactory defaultThreadFactory(boolean daemon) {
            return new NamedDaemonThreadFactory(Executors.defaultThreadFactory(), daemon);
        }
    }
}
