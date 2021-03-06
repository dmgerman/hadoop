begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|net
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ServerSocketChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSUtilClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|SecureDataNodeStarter
operator|.
name|SecureResources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|Server
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TcpPeerServer
specifier|public
class|class
name|TcpPeerServer
implements|implements
name|PeerServer
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TcpPeerServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serverSocket
specifier|private
specifier|final
name|ServerSocket
name|serverSocket
decl_stmt|;
comment|/**    * Create a non-secure TcpPeerServer.    *    * @param socketWriteTimeout    The Socket write timeout in ms.    * @param bindAddr              The address to bind to.    * @param backlogLength         The length of the tcp accept backlog    * @throws IOException    */
DECL|method|TcpPeerServer (int socketWriteTimeout, InetSocketAddress bindAddr, int backlogLength)
specifier|public
name|TcpPeerServer
parameter_list|(
name|int
name|socketWriteTimeout
parameter_list|,
name|InetSocketAddress
name|bindAddr
parameter_list|,
name|int
name|backlogLength
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|serverSocket
operator|=
operator|(
name|socketWriteTimeout
operator|>
literal|0
operator|)
condition|?
name|ServerSocketChannel
operator|.
name|open
argument_list|()
operator|.
name|socket
argument_list|()
else|:
operator|new
name|ServerSocket
argument_list|()
expr_stmt|;
name|Server
operator|.
name|bind
argument_list|(
name|serverSocket
argument_list|,
name|bindAddr
argument_list|,
name|backlogLength
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a secure TcpPeerServer.    *    * @param secureResources   Security resources.    */
DECL|method|TcpPeerServer (SecureResources secureResources)
specifier|public
name|TcpPeerServer
parameter_list|(
name|SecureResources
name|secureResources
parameter_list|)
block|{
name|this
operator|.
name|serverSocket
operator|=
name|secureResources
operator|.
name|getStreamingSocket
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return     the IP address which this TcpPeerServer is listening on.    */
DECL|method|getStreamingAddr ()
specifier|public
name|InetSocketAddress
name|getStreamingAddr
parameter_list|()
block|{
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|serverSocket
operator|.
name|getInetAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|serverSocket
operator|.
name|getLocalPort
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setReceiveBufferSize (int size)
specifier|public
name|void
name|setReceiveBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|serverSocket
operator|.
name|setReceiveBufferSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReceiveBufferSize ()
specifier|public
name|int
name|getReceiveBufferSize
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|serverSocket
operator|.
name|getReceiveBufferSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|accept ()
specifier|public
name|Peer
name|accept
parameter_list|()
throws|throws
name|IOException
throws|,
name|SocketTimeoutException
block|{
name|Peer
name|peer
init|=
name|DFSUtilClient
operator|.
name|peerFromSocket
argument_list|(
name|serverSocket
operator|.
name|accept
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|peer
return|;
block|}
annotation|@
name|Override
DECL|method|getListeningString ()
specifier|public
name|String
name|getListeningString
parameter_list|()
block|{
return|return
name|serverSocket
operator|.
name|getLocalSocketAddress
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|serverSocket
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error closing TcpPeerServer: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TcpPeerServer("
operator|+
name|getListeningString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

