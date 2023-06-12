package live.itrip.jvmm.util;

import live.itrip.jvmm.logging.AgentLogFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : ERP --> fengjianfeng4
 * @date : 2021-08-12 19:45
 * description : NetworkUtils
 **/
public class NetworkUtils {
    private static final Logger LOGGER = AgentLogFactory.getLogger(NetworkUtils.class);

    /***
     * 测试主机Host的port端口是否被使用
     * @param host 指定IP
     * @param port 指定端口
     * @return TRUE:端口已经被占用;FALSE:端口尚未被占用
     */
    public static boolean isPortInUsing(String host, int port) {
        Socket socket = null;
        try {
            final InetAddress Address = InetAddress.getByName(host);
            // 建立一个Socket连接
            socket = new Socket(Address, port);
            return socket.isConnected();
        } catch (Exception e) {
            // ignore
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (null != socket) {
                try {
                    socket.close();
                } catch (IOException var2) {
                    LOGGER.log(Level.SEVERE, var2.getMessage(), var2);
                }
            }
        }
        return false;
    }

    /**
     * get local host address
     *
     * @return InetAddress
     */
    public static InetAddress getLocalHostExactAddress() {
        try {
            InetAddress candidateAddress = null;

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了 就是我们要找的
                            // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                            return inetAddr;
                        }

                        // 若不是site-local地址 那就记录下该地址当作候选
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }

            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
            return candidateAddress == null ? InetAddress.getLocalHost() : candidateAddress;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }
}
