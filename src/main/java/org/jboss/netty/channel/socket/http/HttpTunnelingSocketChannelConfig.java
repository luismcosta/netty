/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.channel.socket.http;

import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.socket.SocketChannelConfig;
import org.jboss.netty.util.internal.ConversionUtil;

/**
 * The {@link ChannelConfig} of a client-side HTTP tunneling
 * {@link SocketChannel}.  A {@link SocketChannel} created by
 * {@link HttpTunnelingClientSocketChannelFactory} will return an instance of
 * this configuration type for {@link SocketChannel#getConfig()}.
 *
 * <h3>Available options</h3>
 *
 * In addition to the options provided by {@link SocketChannelConfig},
 * {@link HttpTunnelingSocketChannelConfig} allows the following options in
 * the option map:
 *
 * <table border="1" cellspacing="0" cellpadding="6">
 * <tr>
 * <th>Name</th><th>Associated setter method</th>
 * </tr><tr>
 * <td>{@code "sslContext"}</td><td>{@link #setSslContext(SSLContext)}</td>
 * </tr><tr>
 * <td>{@code "enabledSslCiperSuites"}</td><td>{@link #setEnabledSslCipherSuites(String[])}</td>
 * </tr><tr>
 * <td>{@code "enabledSslProtocols"}</td><td>{@link #setEnabledSslProtocols(String[])}</td>
 * </tr><tr>
 * <td>{@code "enableSslSessionCreation"}</td><td>{@link #setEnableSslSessionCreation(boolean)}</td>
 * </tr>
 * </table>
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @version $Rev$, $Date$
 *
 * @apiviz.landmark
 */
public final class HttpTunnelingSocketChannelConfig implements SocketChannelConfig {

    private final HttpTunnelingClientSocketChannel channel;
    private volatile String serverName;
    private volatile String serverPath = "/netty-tunnel";
    private volatile SSLContext sslContext;
    private volatile String[] enabledSslCipherSuites;
    private volatile String[] enabledSslProtocols;
    private volatile boolean enableSslSessionCreation = true;

    /**
     * Creates a new instance.
     */
    HttpTunnelingSocketChannelConfig(HttpTunnelingClientSocketChannel channel) {
        this.channel = channel;
    }

    /**
     * Returns the host name of the HTTP server.  If {@code null}, the
     * {@code "Host"} header is not sent by the HTTP tunneling client.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the host name of the HTTP server.  If {@code null}, the
     * {@code "Host"} header is not sent by the HTTP tunneling client.
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Returns the path where the {@link HttpTunnelingServlet} is mapped to.
     * The default value is {@code "/netty-tunnel"}.
     */
    public String getServerPath() {
        return serverPath;
    }

    /**
     * Sets the path where the {@link HttpTunnelingServlet} is mapped to.
     * The default value is {@code "/netty-tunnel"}.
     */
    public void setServerPath(String serverPath) {
        if (serverPath == null) {
            throw new NullPointerException("serverPath");
        }
        this.serverPath = serverPath;
    }

    /**
     * Returns the {@link SSLContext} which is used to establish an HTTPS
     * connection.  If {@code null}, a plain-text HTTP connection is established.
     */
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * Sets the {@link SSLContext} which is used to establish an HTTPS connection.
     * If {@code null}, a plain-text HTTP connection is established.
     */
    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    /**
     * Returns the cipher suites enabled for use on an {@link SSLEngine}.
     * If {@code null}, the default value will be used.
     *
     * @see SSLEngine#getEnabledCipherSuites()
     */
    public String[] getEnabledSslCipherSuites() {
        String[] suites = enabledSslCipherSuites;
        if (suites == null) {
            return null;
        } else {
            return suites.clone();
        }
    }

    /**
     * Sets the cipher suites enabled for use on an {@link SSLEngine}.
     * If {@code null}, the default value will be used.
     *
     * @see SSLEngine#setEnabledCipherSuites(String[])
     */
    public void setEnabledSslCipherSuites(String[] suites) {
        if (suites == null) {
            enabledSslCipherSuites = null;
        } else {
            enabledSslCipherSuites = suites.clone();
        }
    }

    /**
     * Returns the protocol versions enabled for use on an {@link SSLEngine}.
     *
     * @see SSLEngine#getEnabledProtocols()
     */
    public String[] getEnabledSslProtocols() {
        String[] protocols = enabledSslProtocols;
        if (protocols == null) {
            return null;
        } else {
            return protocols.clone();
        }
    }

    /**
     * Sets the protocol versions enabled for use on an {@link SSLEngine}.
     *
     * @see SSLEngine#setEnabledProtocols(String[])
     */
    public void setEnabledSslProtocols(String[] protocols) {
        if (protocols == null) {
            enabledSslProtocols = null;
        } else {
            enabledSslProtocols = protocols.clone();
        }
    }

    /**
     * Returns {@code true} if new {@link SSLSession}s may be established by
     * an {@link SSLEngine}.
     *
     * @see SSLEngine#getEnableSessionCreation()
     */
    public boolean isEnableSslSessionCreation() {
        return enableSslSessionCreation;
    }

    /**
     * Sets whether new {@link SSLSession}s may be established by an
     * {@link SSLEngine}.
     *
     * @see SSLEngine#setEnableSessionCreation(boolean)
     */
    public void setEnableSslSessionCreation(boolean flag) {
        enableSslSessionCreation = flag;
    }

    @Override
    public void setOptions(Map<String, Object> options) {
        for (Entry<String, Object> e: options.entrySet()) {
            setOption(e.getKey(), e.getValue());
        }
    }

    @Override
    public boolean setOption(String key, Object value) {
        if (channel.realChannel.getConfig().setOption(key, value)) {
            return true;
        }

        if (key.equals("serverName")){
            setServerName(String.valueOf(value));
        } else if (key.equals("serverPath")){
            setServerPath(String.valueOf(value));
        } else if (key.equals("sslContext")) {
            setSslContext((SSLContext) value);
        } else if (key.equals("enabledSslCipherSuites")){
            setEnabledSslCipherSuites(ConversionUtil.toStringArray(value));
        } else if (key.equals("enabledSslProtocols")){
            setEnabledSslProtocols(ConversionUtil.toStringArray(value));
        } else if (key.equals("enableSslSessionCreation")){
            setEnableSslSessionCreation(ConversionUtil.toBoolean(value));
        } else {
            return false;
        }

        return true;
    }

    @Override
    public int getReceiveBufferSize() {
        return channel.realChannel.getConfig().getReceiveBufferSize();
    }

    @Override
    public int getSendBufferSize() {
        return channel.realChannel.getConfig().getSendBufferSize();
    }

    @Override
    public int getSoLinger() {
        return channel.realChannel.getConfig().getSoLinger();
    }

    @Override
    public int getTrafficClass() {
        return channel.realChannel.getConfig().getTrafficClass();
    }

    @Override
    public boolean isKeepAlive() {
        return channel.realChannel.getConfig().isKeepAlive();
    }

    @Override
    public boolean isReuseAddress() {
        return channel.realChannel.getConfig().isReuseAddress();
    }

    @Override
    public boolean isTcpNoDelay() {
        return channel.realChannel.getConfig().isTcpNoDelay();
    }

    @Override
    public void setKeepAlive(boolean keepAlive) {
        channel.realChannel.getConfig().setKeepAlive(keepAlive);
    }

    @Override
    public void setPerformancePreferences(
          int connectionTime, int latency, int bandwidth) {
        channel.realChannel.getConfig().setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    @Override
    public void setReceiveBufferSize(int receiveBufferSize) {
        channel.realChannel.getConfig().setReceiveBufferSize(receiveBufferSize);
    }

    @Override
    public void setReuseAddress(boolean reuseAddress) {
        channel.realChannel.getConfig().setReuseAddress(reuseAddress);
    }

    @Override
    public void setSendBufferSize(int sendBufferSize) {
        channel.realChannel.getConfig().setSendBufferSize(sendBufferSize);

    }

    @Override
    public void setSoLinger(int soLinger) {
        channel.realChannel.getConfig().setSoLinger(soLinger);
    }

    @Override
    public void setTcpNoDelay(boolean tcpNoDelay) {
        channel.realChannel.getConfig().setTcpNoDelay(tcpNoDelay);
    }

    @Override
    public void setTrafficClass(int trafficClass) {
        channel.realChannel.getConfig().setTrafficClass(trafficClass);
    }

    @Override
    public ChannelBufferFactory getBufferFactory() {
        return channel.realChannel.getConfig().getBufferFactory();
    }

    @Override
    public int getConnectTimeoutMillis() {
        return channel.realChannel.getConfig().getConnectTimeoutMillis();
    }

    @Override
    public ChannelPipelineFactory getPipelineFactory() {
        return channel.realChannel.getConfig().getPipelineFactory();
    }

    @Override
    public void setBufferFactory(ChannelBufferFactory bufferFactory) {
        channel.realChannel.getConfig().setBufferFactory(bufferFactory);
    }

    @Override
    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        channel.realChannel.getConfig().setConnectTimeoutMillis(connectTimeoutMillis);
    }

    @Override
    public void setPipelineFactory(ChannelPipelineFactory pipelineFactory) {
        channel.realChannel.getConfig().setPipelineFactory(pipelineFactory);
    }
}
