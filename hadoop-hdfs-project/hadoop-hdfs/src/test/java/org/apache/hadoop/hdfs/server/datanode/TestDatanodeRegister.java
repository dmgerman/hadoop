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
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|common
operator|.
name|IncorrectVersionException
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
name|protocol
operator|.
name|DatanodeProtocol
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
name|protocol
operator|.
name|NamespaceInfo
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestDatanodeRegister
specifier|public
class|class
name|TestDatanodeRegister
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDatanodeRegister
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Invalid address
DECL|field|INVALID_ADDR
specifier|static
specifier|final
name|InetSocketAddress
name|INVALID_ADDR
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testDataNodeRegister ()
specifier|public
name|void
name|testDataNodeRegister
parameter_list|()
throws|throws
name|Exception
block|{
name|DataNode
name|mockDN
init|=
name|mock
argument_list|(
name|DataNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|mockDN
argument_list|)
operator|.
name|shouldRun
argument_list|()
expr_stmt|;
name|BPOfferService
name|bpos
init|=
operator|new
name|BPOfferService
argument_list|(
name|INVALID_ADDR
argument_list|,
name|mockDN
argument_list|)
decl_stmt|;
name|NamespaceInfo
name|fakeNSInfo
init|=
name|mock
argument_list|(
name|NamespaceInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fakeNSInfo
operator|.
name|getBuildVersion
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"NSBuildVersion"
argument_list|)
expr_stmt|;
name|DatanodeProtocol
name|fakeDNProt
init|=
name|mock
argument_list|(
name|DatanodeProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fakeDNProt
operator|.
name|versionRequest
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fakeNSInfo
argument_list|)
expr_stmt|;
name|bpos
operator|.
name|setNameNode
argument_list|(
name|fakeDNProt
argument_list|)
expr_stmt|;
name|bpos
operator|.
name|bpNSInfo
operator|=
name|fakeNSInfo
expr_stmt|;
try|try
block|{
name|bpos
operator|.
name|retrieveNamespaceInfo
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"register() did not throw exception! "
operator|+
literal|"Expected: IncorrectVersionException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IncorrectVersionException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"register() returned correct Exception: IncorrectVersionException"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

