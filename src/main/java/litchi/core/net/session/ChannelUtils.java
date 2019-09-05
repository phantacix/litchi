//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.session;

import io.netty.channel.Channel;
import litchi.core.common.utils.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ChannelUtils {

    public static String getRemoteIp(Channel channel) {
        String remoteIp = "";
        SocketAddress add = channel.remoteAddress();
        if (add != null) {
            remoteIp = ((InetSocketAddress) add).getAddress().getHostAddress();
        }
        if (StringUtils.isBlank(remoteIp)) {
            remoteIp = ((InetSocketAddress) channel.localAddress()).getAddress().getHostAddress();
        }
        return remoteIp;
    }
}
