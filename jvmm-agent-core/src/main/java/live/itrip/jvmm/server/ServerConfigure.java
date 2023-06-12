package live.itrip.jvmm.server;

import com.google.common.base.Strings;
import live.itrip.jvmm.util.NetworkUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author : ERP --> fengjianfeng4
 * @date : 2021-08-12 18:22
 * description : CoreConfigure
 **/
public class ServerConfigure {

    /**
     * local server ip
     */
    private final String serverIp;
    /**
     * local server port
     */
    private final int serverPort;

    private ServerConfigure(Builder builder) {
        this.serverIp = builder.getServerIp();
        this.serverPort = builder.getServerPort();
    }

    /**
     * get server ip
     *
     * @return ip
     */
    public String getServerIp() {
        return this.serverIp;
    }

    /**
     * set server port
     *
     * @return port
     */
    public int getServerPort() {
        return this.serverPort;
    }

    @Override
    public String toString() {
        return String.format(" agent http server ---> %s:%s", this.serverIp, this.serverPort);
    }

    public static class Builder {
        private String serverIp;
        private int serverPort = 8001;

        public ServerConfigure build() throws UnknownHostException {
            if (Strings.isNullOrEmpty(this.serverIp)) {
                InetAddress inetAddress = NetworkUtils.getLocalHostExactAddress();
                if (inetAddress != null) {
                    // local ip
                    this.serverIp = inetAddress.getHostAddress();
                }
            }

//            for (int i = 0; i < 20; i++) {
//                if (NetworkUtils.isPortInUsing(this.serverIp, this.serverPort)) {
//                    // 正常应该8001即可用，如若不可用，端口+1，直到 8020
//                    return new ServerConfigure(this);
//                }
//                this.serverPort++;
//            }
//
//            this.serverPort = 18001;
            return new ServerConfigure(this);
        }

        public Builder setServerIp(String serverIp) {
            this.serverIp = serverIp;
            return this;
        }

        public Builder setServerPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public String getServerIp() {
            return serverIp;
        }


        public int getServerPort() {
            return serverPort;
        }
    }


}
