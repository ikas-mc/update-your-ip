package ikas.java.project.updateip;

import java.net.Inet6Address;
import java.net.NetworkInterface;

public class IpUtil {

    @Nullable
    public static String FindBestIp() {
        try {
            for (var interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                for (var inetAddresses = interfaces.nextElement().getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    if (inetAddresses.nextElement() instanceof Inet6Address inet6Address) {
                        if (!inet6Address.isLoopbackAddress()
                                && !inet6Address.isLinkLocalAddress()
                                && !inet6Address.isSiteLocalAddress()
                                //TODO config
                                && inet6Address.getHostAddress().startsWith("240e:")
                        ) {
                            return numericToTextFormat(inet6Address.getAddress());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    //TODO
    // copy form jdk
    private static final int INADDRSZ = 16;
    private static final int INT16SZ = 2;

    static String numericToTextFormat(byte[] src) {
        StringBuilder sb = new StringBuilder(39);
        for (int i = 0; i < (INADDRSZ / INT16SZ); i++) {
            sb.append(Integer.toHexString(((src[i << 1] << 8) & 0xff00)
                    | (src[(i << 1) + 1] & 0xff)));
            if (i < (INADDRSZ / INT16SZ) - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
}
