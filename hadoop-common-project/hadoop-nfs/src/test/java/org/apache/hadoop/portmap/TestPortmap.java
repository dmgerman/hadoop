begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.portmap
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|portmap
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
name|DatagramPacket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|DatagramSocket
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
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|oncrpc
operator|.
name|RpcCall
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
name|oncrpc
operator|.
name|XDR
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
name|oncrpc
operator|.
name|security
operator|.
name|CredentialsNone
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
name|oncrpc
operator|.
name|security
operator|.
name|VerifierNone
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
name|test
operator|.
name|Whitebox
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

begin_class
DECL|class|TestPortmap
specifier|public
class|class
name|TestPortmap
block|{
DECL|field|pm
specifier|private
specifier|static
name|Portmap
name|pm
init|=
operator|new
name|Portmap
argument_list|()
decl_stmt|;
DECL|field|SHORT_TIMEOUT_MILLISECONDS
specifier|private
specifier|static
specifier|final
name|int
name|SHORT_TIMEOUT_MILLISECONDS
init|=
literal|10
decl_stmt|;
DECL|field|RETRY_TIMES
specifier|private
specifier|static
specifier|final
name|int
name|RETRY_TIMES
init|=
literal|5
decl_stmt|;
DECL|field|xid
specifier|private
name|int
name|xid
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|pm
operator|.
name|start
argument_list|(
name|SHORT_TIMEOUT_MILLISECONDS
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
name|pm
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testIdle ()
specifier|public
name|void
name|testIdle
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|Socket
name|s
init|=
operator|new
name|Socket
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|connect
argument_list|(
name|pm
operator|.
name|getTcpServerLocalAddress
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|s
operator|.
name|isConnected
argument_list|()
operator|&&
name|i
operator|<
name|RETRY_TIMES
condition|)
block|{
operator|++
name|i
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|SHORT_TIMEOUT_MILLISECONDS
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Failed to connect to the server"
argument_list|,
name|s
operator|.
name|isConnected
argument_list|()
operator|&&
name|i
operator|<
name|RETRY_TIMES
argument_list|)
expr_stmt|;
name|int
name|b
init|=
name|s
operator|.
name|getInputStream
argument_list|()
operator|.
name|read
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The server failed to disconnect"
argument_list|,
name|b
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testRegistration ()
specifier|public
name|void
name|testRegistration
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|XDR
name|req
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|RpcCall
operator|.
name|getInstance
argument_list|(
operator|++
name|xid
argument_list|,
name|RpcProgramPortmap
operator|.
name|PROGRAM
argument_list|,
name|RpcProgramPortmap
operator|.
name|VERSION
argument_list|,
name|RpcProgramPortmap
operator|.
name|PMAPPROC_SET
argument_list|,
operator|new
name|CredentialsNone
argument_list|()
argument_list|,
operator|new
name|VerifierNone
argument_list|()
argument_list|)
operator|.
name|write
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|PortmapMapping
name|sent
init|=
operator|new
name|PortmapMapping
argument_list|(
literal|90000
argument_list|,
literal|1
argument_list|,
name|PortmapMapping
operator|.
name|TRANSPORT_TCP
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|sent
operator|.
name|serialize
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|byte
index|[]
name|reqBuf
init|=
name|req
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DatagramSocket
name|s
init|=
operator|new
name|DatagramSocket
argument_list|()
decl_stmt|;
name|DatagramPacket
name|p
init|=
operator|new
name|DatagramPacket
argument_list|(
name|reqBuf
argument_list|,
name|reqBuf
operator|.
name|length
argument_list|,
name|pm
operator|.
name|getUdpServerLoAddress
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|s
operator|.
name|send
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Give the server a chance to process the request
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|PortmapMapping
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|PortmapMapping
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|pm
operator|.
name|getHandler
argument_list|()
argument_list|,
literal|"map"
argument_list|)
decl_stmt|;
for|for
control|(
name|PortmapMapping
name|m
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getPort
argument_list|()
operator|==
name|sent
operator|.
name|getPort
argument_list|()
operator|&&
name|PortmapMapping
operator|.
name|key
argument_list|(
name|m
argument_list|)
operator|.
name|equals
argument_list|(
name|PortmapMapping
operator|.
name|key
argument_list|(
name|sent
argument_list|)
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Registration failed"
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

