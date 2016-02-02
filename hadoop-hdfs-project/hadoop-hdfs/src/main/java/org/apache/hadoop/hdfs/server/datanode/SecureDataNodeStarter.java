begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|daemon
operator|.
name|Daemon
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|daemon
operator|.
name|DaemonContext
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|DFSConfigKeys
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
name|DFSUtil
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
name|HdfsConfiguration
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
name|protocol
operator|.
name|HdfsConstants
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
name|http
operator|.
name|HttpConfig
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
name|security
operator|.
name|SecurityUtil
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
name|security
operator|.
name|UserGroupInformation
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
name|nio
operator|.
name|channels
operator|.
name|ServerSocketChannel
import|;
end_import

begin_comment
comment|/**  * Utility class to start a datanode in a secure cluster, first obtaining   * privileged resources before main startup and handing them to the datanode.  */
end_comment

begin_class
DECL|class|SecureDataNodeStarter
specifier|public
class|class
name|SecureDataNodeStarter
implements|implements
name|Daemon
block|{
comment|/**    * Stash necessary resources needed for datanode operation in a secure env.    */
DECL|class|SecureResources
specifier|public
specifier|static
class|class
name|SecureResources
block|{
DECL|field|streamingSocket
specifier|private
specifier|final
name|ServerSocket
name|streamingSocket
decl_stmt|;
DECL|field|httpServerSocket
specifier|private
specifier|final
name|ServerSocketChannel
name|httpServerSocket
decl_stmt|;
DECL|method|SecureResources (ServerSocket streamingSocket, ServerSocketChannel httpServerSocket)
specifier|public
name|SecureResources
parameter_list|(
name|ServerSocket
name|streamingSocket
parameter_list|,
name|ServerSocketChannel
name|httpServerSocket
parameter_list|)
block|{
name|this
operator|.
name|streamingSocket
operator|=
name|streamingSocket
expr_stmt|;
name|this
operator|.
name|httpServerSocket
operator|=
name|httpServerSocket
expr_stmt|;
block|}
DECL|method|getStreamingSocket ()
specifier|public
name|ServerSocket
name|getStreamingSocket
parameter_list|()
block|{
return|return
name|streamingSocket
return|;
block|}
DECL|method|getHttpServerChannel ()
specifier|public
name|ServerSocketChannel
name|getHttpServerChannel
parameter_list|()
block|{
return|return
name|httpServerSocket
return|;
block|}
block|}
DECL|field|args
specifier|private
name|String
index|[]
name|args
decl_stmt|;
DECL|field|resources
specifier|private
name|SecureResources
name|resources
decl_stmt|;
annotation|@
name|Override
DECL|method|init (DaemonContext context)
specifier|public
name|void
name|init
parameter_list|(
name|DaemonContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Initializing secure datanode resources"
argument_list|)
expr_stmt|;
comment|// Create a new HdfsConfiguration object to ensure that the configuration in
comment|// hdfs-site.xml is picked up.
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Stash command-line arguments for regular datanode
name|args
operator|=
name|context
operator|.
name|getArguments
argument_list|()
expr_stmt|;
name|resources
operator|=
name|getSecureResources
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Starting regular datanode initialization"
argument_list|)
expr_stmt|;
name|DataNode
operator|.
name|secureMain
argument_list|(
name|args
argument_list|,
name|resources
argument_list|)
expr_stmt|;
block|}
DECL|method|destroy ()
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|()
block|{}
DECL|method|stop ()
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* Nothing to do */
block|}
comment|/**    * Acquire privileged resources (i.e., the privileged ports) for the data    * node. The privileged resources consist of the port of the RPC server and    * the port of HTTP (not HTTPS) server.    */
annotation|@
name|VisibleForTesting
DECL|method|getSecureResources (Configuration conf)
specifier|public
specifier|static
name|SecureResources
name|getSecureResources
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpConfig
operator|.
name|Policy
name|policy
init|=
name|DFSUtil
operator|.
name|getHttpPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|boolean
name|isSecure
init|=
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
decl_stmt|;
comment|// Obtain secure port for data streaming to datanode
name|InetSocketAddress
name|streamingAddr
init|=
name|DataNode
operator|.
name|getStreamingAddr
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|socketWriteTimeout
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SOCKET_WRITE_TIMEOUT_KEY
argument_list|,
name|HdfsConstants
operator|.
name|WRITE_TIMEOUT
argument_list|)
decl_stmt|;
name|int
name|backlogLength
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_SERVER_LISTEN_QUEUE_SIZE_KEY
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|IPC_SERVER_LISTEN_QUEUE_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|ServerSocket
name|ss
init|=
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
decl_stmt|;
name|ss
operator|.
name|bind
argument_list|(
name|streamingAddr
argument_list|,
name|backlogLength
argument_list|)
expr_stmt|;
comment|// Check that we got the port we need
if|if
condition|(
name|ss
operator|.
name|getLocalPort
argument_list|()
operator|!=
name|streamingAddr
operator|.
name|getPort
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to bind on specified streaming port in secure "
operator|+
literal|"context. Needed "
operator|+
name|streamingAddr
operator|.
name|getPort
argument_list|()
operator|+
literal|", got "
operator|+
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|SecurityUtil
operator|.
name|isPrivilegedPort
argument_list|(
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
operator|&&
name|isSecure
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot start secure datanode with unprivileged RPC ports"
argument_list|)
throw|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Opened streaming server at "
operator|+
name|streamingAddr
argument_list|)
expr_stmt|;
comment|// Bind a port for the web server. The code intends to bind HTTP server to
comment|// privileged port only, as the client can authenticate the server using
comment|// certificates if they are communicating through SSL.
specifier|final
name|ServerSocketChannel
name|httpChannel
decl_stmt|;
if|if
condition|(
name|policy
operator|.
name|isHttpEnabled
argument_list|()
condition|)
block|{
name|httpChannel
operator|=
name|ServerSocketChannel
operator|.
name|open
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|infoSocAddr
init|=
name|DataNode
operator|.
name|getInfoAddr
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|httpChannel
operator|.
name|socket
argument_list|()
operator|.
name|bind
argument_list|(
name|infoSocAddr
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|localAddr
init|=
operator|(
name|InetSocketAddress
operator|)
name|httpChannel
operator|.
name|socket
argument_list|()
operator|.
name|getLocalSocketAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|localAddr
operator|.
name|getPort
argument_list|()
operator|!=
name|infoSocAddr
operator|.
name|getPort
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to bind on specified info port in secure "
operator|+
literal|"context. Needed "
operator|+
name|streamingAddr
operator|.
name|getPort
argument_list|()
operator|+
literal|", got "
operator|+
name|ss
operator|.
name|getLocalPort
argument_list|()
argument_list|)
throw|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Successfully obtained privileged resources (streaming port = "
operator|+
name|ss
operator|+
literal|" ) (http listener port = "
operator|+
name|localAddr
operator|.
name|getPort
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|localAddr
operator|.
name|getPort
argument_list|()
operator|>
literal|1023
operator|&&
name|isSecure
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot start secure datanode with unprivileged HTTP ports"
argument_list|)
throw|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Opened info server at "
operator|+
name|infoSocAddr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpChannel
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|SecureResources
argument_list|(
name|ss
argument_list|,
name|httpChannel
argument_list|)
return|;
block|}
block|}
end_class

end_unit

