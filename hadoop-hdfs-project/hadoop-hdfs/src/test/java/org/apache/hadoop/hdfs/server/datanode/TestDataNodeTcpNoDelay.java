begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fs
operator|.
name|Path
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
name|DFSTestUtil
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
name|DistributedFileSystem
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
name|MiniDFSCluster
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|net
operator|.
name|NetUtils
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
name|net
operator|.
name|StandardSocketFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|SocketChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Checks that used sockets have TCP_NODELAY set when configured.  */
end_comment

begin_class
DECL|class|TestDataNodeTcpNoDelay
specifier|public
class|class
name|TestDataNodeTcpNoDelay
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDataNodeTcpNoDelay
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|baseConf
specifier|private
specifier|static
name|Configuration
name|baseConf
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUpBeforeClass ()
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|baseConf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDownAfterClass ()
specifier|public
specifier|static
name|void
name|tearDownAfterClass
parameter_list|()
throws|throws
name|Exception
block|{    }
annotation|@
name|Test
DECL|method|testTcpNoDelayEnabled ()
specifier|public
name|void
name|testTcpNoDelayEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|testConf
init|=
operator|new
name|Configuration
argument_list|(
name|baseConf
argument_list|)
decl_stmt|;
comment|// here we do not have to config TCP_NDELAY settings, since they should be
comment|// active by default
name|testConf
operator|.
name|set
argument_list|(
name|HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY
argument_list|,
name|SocketFactoryWrapper
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|SocketFactory
name|defaultFactory
init|=
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|testConf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Socket factory is "
operator|+
name|defaultFactory
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|dfsCluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|testConf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|dfsCluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
name|createData
argument_list|(
name|dfs
argument_list|)
expr_stmt|;
name|transferBlock
argument_list|(
name|dfs
argument_list|)
expr_stmt|;
comment|// check that TCP_NODELAY has been set on all sockets
name|assertTrue
argument_list|(
name|SocketFactoryWrapper
operator|.
name|wasTcpNoDelayActive
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|SocketFactoryWrapper
operator|.
name|reset
argument_list|()
expr_stmt|;
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTcpNoDelayDisabled ()
specifier|public
name|void
name|testTcpNoDelayDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|testConf
init|=
operator|new
name|Configuration
argument_list|(
name|baseConf
argument_list|)
decl_stmt|;
comment|// disable TCP_NODELAY in settings
name|setTcpNoDelay
argument_list|(
name|testConf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testConf
operator|.
name|set
argument_list|(
name|HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY
argument_list|,
name|SocketFactoryWrapper
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|SocketFactory
name|defaultFactory
init|=
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|testConf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Socket factory is "
operator|+
name|defaultFactory
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|dfsCluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|testConf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|dfsCluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|dfs
init|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
name|createData
argument_list|(
name|dfs
argument_list|)
expr_stmt|;
name|transferBlock
argument_list|(
name|dfs
argument_list|)
expr_stmt|;
comment|// we can only check that TCP_NODELAY was disabled on some sockets,
comment|// since part of the client write path always enables TCP_NODELAY
comment|// by necessity
name|assertFalse
argument_list|(
name|SocketFactoryWrapper
operator|.
name|wasTcpNoDelayActive
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|SocketFactoryWrapper
operator|.
name|reset
argument_list|()
expr_stmt|;
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createData (DistributedFileSystem dfs)
specifier|private
name|void
name|createData
parameter_list|(
name|DistributedFileSystem
name|dfs
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"test-dir"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"file"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f
argument_list|,
literal|10240
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests the {@code DataNode#transferBlocks()} path by re-replicating an    * existing block.    */
DECL|method|transferBlock (DistributedFileSystem dfs)
specifier|private
name|void
name|transferBlock
parameter_list|(
name|DistributedFileSystem
name|dfs
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"test-block-transfer"
argument_list|)
decl_stmt|;
name|Path
name|f
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"testfile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|f
argument_list|,
literal|10240
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// force a block transfer to another DN
name|dfs
operator|.
name|setReplication
argument_list|(
name|f
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitForReplication
argument_list|(
name|dfs
argument_list|,
name|f
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets known TCP_NODELAY configs to the given value.    */
DECL|method|setTcpNoDelay (Configuration conf, boolean value)
specifier|private
name|void
name|setTcpNoDelay
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_DATA_TRANSFER_CLIENT_TCPNODELAY_KEY
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATA_TRANSFER_SERVER_TCPNODELAY
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_TCPNODELAY_KEY
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_SERVER_TCPNODELAY_KEY
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|class|SocketFactoryWrapper
specifier|public
specifier|static
class|class
name|SocketFactoryWrapper
extends|extends
name|StandardSocketFactory
block|{
DECL|field|sockets
specifier|private
specifier|static
name|List
argument_list|<
name|SocketWrapper
argument_list|>
name|sockets
init|=
operator|new
name|ArrayList
argument_list|<
name|SocketWrapper
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|wasTcpNoDelayActive ()
specifier|public
specifier|static
name|boolean
name|wasTcpNoDelayActive
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking "
operator|+
name|sockets
operator|.
name|size
argument_list|()
operator|+
literal|" sockets for TCP_NODELAY"
argument_list|)
expr_stmt|;
for|for
control|(
name|SocketWrapper
name|sw
range|:
name|sockets
control|)
block|{
if|if
condition|(
operator|!
name|sw
operator|.
name|getLastTcpNoDelay
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|reset ()
specifier|public
specifier|static
name|void
name|reset
parameter_list|()
block|{
name|sockets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSocket ()
specifier|public
name|Socket
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating new socket"
argument_list|)
expr_stmt|;
name|SocketWrapper
name|wrapper
init|=
operator|new
name|SocketWrapper
argument_list|(
name|super
operator|.
name|createSocket
argument_list|()
argument_list|)
decl_stmt|;
name|sockets
operator|.
name|add
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
return|return
name|wrapper
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (String host, int port)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating socket for "
operator|+
name|host
argument_list|)
expr_stmt|;
name|SocketWrapper
name|wrapper
init|=
operator|new
name|SocketWrapper
argument_list|(
name|super
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
argument_list|)
decl_stmt|;
name|sockets
operator|.
name|add
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
return|return
name|wrapper
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (String host, int port, InetAddress localHostAddr, int localPort)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localHostAddr
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating socket for "
operator|+
name|host
argument_list|)
expr_stmt|;
name|SocketWrapper
name|wrapper
init|=
operator|new
name|SocketWrapper
argument_list|(
name|super
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|localHostAddr
argument_list|,
name|localPort
argument_list|)
argument_list|)
decl_stmt|;
name|sockets
operator|.
name|add
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
return|return
name|wrapper
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (InetAddress addr, int port)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|addr
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating socket for "
operator|+
name|addr
argument_list|)
expr_stmt|;
name|SocketWrapper
name|wrapper
init|=
operator|new
name|SocketWrapper
argument_list|(
name|super
operator|.
name|createSocket
argument_list|(
name|addr
argument_list|,
name|port
argument_list|)
argument_list|)
decl_stmt|;
name|sockets
operator|.
name|add
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
return|return
name|wrapper
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (InetAddress addr, int port, InetAddress localHostAddr, int localPort)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|addr
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localHostAddr
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating socket for "
operator|+
name|addr
argument_list|)
expr_stmt|;
name|SocketWrapper
name|wrapper
init|=
operator|new
name|SocketWrapper
argument_list|(
name|super
operator|.
name|createSocket
argument_list|(
name|addr
argument_list|,
name|port
argument_list|,
name|localHostAddr
argument_list|,
name|localPort
argument_list|)
argument_list|)
decl_stmt|;
name|sockets
operator|.
name|add
argument_list|(
name|wrapper
argument_list|)
expr_stmt|;
return|return
name|wrapper
return|;
block|}
block|}
DECL|class|SocketWrapper
specifier|public
specifier|static
class|class
name|SocketWrapper
extends|extends
name|Socket
block|{
DECL|field|wrapped
specifier|private
specifier|final
name|Socket
name|wrapped
decl_stmt|;
DECL|field|tcpNoDelay
specifier|private
name|boolean
name|tcpNoDelay
decl_stmt|;
DECL|method|SocketWrapper (Socket socket)
specifier|public
name|SocketWrapper
parameter_list|(
name|Socket
name|socket
parameter_list|)
block|{
name|this
operator|.
name|wrapped
operator|=
name|socket
expr_stmt|;
block|}
comment|// Override methods, check whether tcpnodelay has been set for each socket
comment|// created. This isn't perfect, as we could still send before tcpnodelay
comment|// is set, but should at least trigger when tcpnodelay is never set at all.
annotation|@
name|Override
DECL|method|connect (SocketAddress endpoint)
specifier|public
name|void
name|connect
parameter_list|(
name|SocketAddress
name|endpoint
parameter_list|)
throws|throws
name|IOException
block|{
name|wrapped
operator|.
name|connect
argument_list|(
name|endpoint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|connect (SocketAddress endpoint, int timeout)
specifier|public
name|void
name|connect
parameter_list|(
name|SocketAddress
name|endpoint
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|wrapped
operator|.
name|connect
argument_list|(
name|endpoint
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bind (SocketAddress bindpoint)
specifier|public
name|void
name|bind
parameter_list|(
name|SocketAddress
name|bindpoint
parameter_list|)
throws|throws
name|IOException
block|{
name|wrapped
operator|.
name|bind
argument_list|(
name|bindpoint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInetAddress ()
specifier|public
name|InetAddress
name|getInetAddress
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|getInetAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalAddress ()
specifier|public
name|InetAddress
name|getLocalAddress
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|getLocalAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|getPort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalPort ()
specifier|public
name|int
name|getLocalPort
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|getLocalPort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteSocketAddress ()
specifier|public
name|SocketAddress
name|getRemoteSocketAddress
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|getRemoteSocketAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalSocketAddress ()
specifier|public
name|SocketAddress
name|getLocalSocketAddress
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|getLocalSocketAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChannel ()
specifier|public
name|SocketChannel
name|getChannel
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|getChannel
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getInputStream ()
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|wrapped
operator|.
name|getInputStream
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOutputStream ()
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|wrapped
operator|.
name|getOutputStream
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setTcpNoDelay (boolean on)
specifier|public
name|void
name|setTcpNoDelay
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
operator|.
name|setTcpNoDelay
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|this
operator|.
name|tcpNoDelay
operator|=
name|on
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTcpNoDelay ()
specifier|public
name|boolean
name|getTcpNoDelay
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getTcpNoDelay
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setSoLinger (boolean on, int linger)
specifier|public
name|void
name|setSoLinger
parameter_list|(
name|boolean
name|on
parameter_list|,
name|int
name|linger
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
operator|.
name|setSoLinger
argument_list|(
name|on
argument_list|,
name|linger
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSoLinger ()
specifier|public
name|int
name|getSoLinger
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getSoLinger
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sendUrgentData (int data)
specifier|public
name|void
name|sendUrgentData
parameter_list|(
name|int
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|wrapped
operator|.
name|sendUrgentData
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setOOBInline (boolean on)
specifier|public
name|void
name|setOOBInline
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
operator|.
name|setOOBInline
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOOBInline ()
specifier|public
name|boolean
name|getOOBInline
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getOOBInline
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setSoTimeout (int timeout)
specifier|public
specifier|synchronized
name|void
name|setSoTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
operator|.
name|setSoTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSoTimeout ()
specifier|public
specifier|synchronized
name|int
name|getSoTimeout
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getSoTimeout
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setSendBufferSize (int size)
specifier|public
specifier|synchronized
name|void
name|setSendBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
operator|.
name|setSendBufferSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSendBufferSize ()
specifier|public
specifier|synchronized
name|int
name|getSendBufferSize
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getSendBufferSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setReceiveBufferSize (int size)
specifier|public
specifier|synchronized
name|void
name|setReceiveBufferSize
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
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
specifier|synchronized
name|int
name|getReceiveBufferSize
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getReceiveBufferSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setKeepAlive (boolean on)
specifier|public
name|void
name|setKeepAlive
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
operator|.
name|setKeepAlive
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKeepAlive ()
specifier|public
name|boolean
name|getKeepAlive
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getKeepAlive
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setTrafficClass (int tc)
specifier|public
name|void
name|setTrafficClass
parameter_list|(
name|int
name|tc
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
operator|.
name|setTrafficClass
argument_list|(
name|tc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTrafficClass ()
specifier|public
name|int
name|getTrafficClass
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getTrafficClass
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setReuseAddress (boolean on)
specifier|public
name|void
name|setReuseAddress
parameter_list|(
name|boolean
name|on
parameter_list|)
throws|throws
name|SocketException
block|{
name|wrapped
operator|.
name|setReuseAddress
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReuseAddress ()
specifier|public
name|boolean
name|getReuseAddress
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|wrapped
operator|.
name|getReuseAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|wrapped
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shutdownInput ()
specifier|public
name|void
name|shutdownInput
parameter_list|()
throws|throws
name|IOException
block|{
name|wrapped
operator|.
name|shutdownInput
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shutdownOutput ()
specifier|public
name|void
name|shutdownOutput
parameter_list|()
throws|throws
name|IOException
block|{
name|wrapped
operator|.
name|shutdownOutput
argument_list|()
expr_stmt|;
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
name|wrapped
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isConnected ()
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|isConnected
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isBound ()
specifier|public
name|boolean
name|isBound
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|isBound
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isClosed ()
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|isClosed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isInputShutdown ()
specifier|public
name|boolean
name|isInputShutdown
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|isInputShutdown
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isOutputShutdown ()
specifier|public
name|boolean
name|isOutputShutdown
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|isOutputShutdown
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setPerformancePreferences (int connectionTime, int latency, int bandwidth)
specifier|public
name|void
name|setPerformancePreferences
parameter_list|(
name|int
name|connectionTime
parameter_list|,
name|int
name|latency
parameter_list|,
name|int
name|bandwidth
parameter_list|)
block|{
name|wrapped
operator|.
name|setPerformancePreferences
argument_list|(
name|connectionTime
argument_list|,
name|latency
argument_list|,
name|bandwidth
argument_list|)
expr_stmt|;
block|}
DECL|method|getLastTcpNoDelay ()
specifier|public
name|boolean
name|getLastTcpNoDelay
parameter_list|()
block|{
return|return
name|tcpNoDelay
return|;
block|}
block|}
block|}
end_class

end_unit

