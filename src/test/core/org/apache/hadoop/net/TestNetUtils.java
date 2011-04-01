begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
package|;
end_package

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|ConnectException
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
name|InetSocketAddress
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

begin_class
DECL|class|TestNetUtils
specifier|public
class|class
name|TestNetUtils
block|{
comment|/**    * Test that we can't accidentally connect back to the connecting socket due    * to a quirk in the TCP spec.    *    * This is a regression test for HADOOP-6722.    */
annotation|@
name|Test
DECL|method|testAvoidLoopbackTcpSockets ()
specifier|public
name|void
name|testAvoidLoopbackTcpSockets
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Socket
name|socket
init|=
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|createSocket
argument_list|()
decl_stmt|;
name|socket
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"local address: "
operator|+
name|socket
operator|.
name|getLocalAddress
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"local port: "
operator|+
name|socket
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|NetUtils
operator|.
name|connect
argument_list|(
name|socket
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
name|socket
operator|.
name|getLocalAddress
argument_list|()
argument_list|,
name|socket
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have connected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectException
name|ce
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Got exception: "
operator|+
name|ce
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ce
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"resulted in a loopback"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|se
parameter_list|)
block|{
comment|// Some TCP stacks will actually throw their own Invalid argument exception
comment|// here. This is also OK.
name|assertTrue
argument_list|(
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid argument"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test for {    * @throws UnknownHostException @link NetUtils#getLocalInetAddress(String)    * @throws SocketException     */
annotation|@
name|Test
DECL|method|testGetLocalInetAddress ()
specifier|public
name|void
name|testGetLocalInetAddress
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|NetUtils
operator|.
name|getLocalInetAddress
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|NetUtils
operator|.
name|getLocalInetAddress
argument_list|(
literal|"invalid-address-for-test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|NetUtils
operator|.
name|getLocalInetAddress
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

