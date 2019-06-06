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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
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
name|assertEquals
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
name|doReturn
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
name|mock
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
name|when
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
name|net
operator|.
name|InetSocketAddress
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
name|Collections
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|HadoopIllegalArgumentException
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
name|FileSystem
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
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|HdfsServerConstants
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
name|NamespaceInfo
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
name|GenericTestUtils
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
name|util
operator|.
name|VersionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|TestDatanodeRegister
specifier|public
class|class
name|TestDatanodeRegister
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDatanodeRegister
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Invalid address
DECL|field|INVALID_ADDR
specifier|private
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
DECL|field|actor
specifier|private
name|BPServiceActor
name|actor
decl_stmt|;
DECL|field|fakeNsInfo
name|NamespaceInfo
name|fakeNsInfo
decl_stmt|;
DECL|field|mockDnConf
name|DNConf
name|mockDnConf
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|mockDnConf
operator|=
name|mock
argument_list|(
name|DNConf
operator|.
name|class
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnConf
argument_list|)
operator|.
name|getMinimumNameNodeVersion
argument_list|()
expr_stmt|;
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
name|doReturn
argument_list|(
name|mockDnConf
argument_list|)
operator|.
name|when
argument_list|(
name|mockDN
argument_list|)
operator|.
name|getDnConf
argument_list|()
expr_stmt|;
name|BPOfferService
name|mockBPOS
init|=
name|mock
argument_list|(
name|BPOfferService
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|mockDN
argument_list|)
operator|.
name|when
argument_list|(
name|mockBPOS
argument_list|)
operator|.
name|getDataNode
argument_list|()
expr_stmt|;
name|actor
operator|=
operator|new
name|BPServiceActor
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
name|INVALID_ADDR
argument_list|,
literal|null
argument_list|,
name|mockBPOS
argument_list|)
expr_stmt|;
name|fakeNsInfo
operator|=
name|mock
argument_list|(
name|NamespaceInfo
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Return a a good software version.
name|doReturn
argument_list|(
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|fakeNsInfo
argument_list|)
operator|.
name|getSoftwareVersion
argument_list|()
expr_stmt|;
comment|// Return a good layout version for now.
name|doReturn
argument_list|(
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
argument_list|)
operator|.
name|when
argument_list|(
name|fakeNsInfo
argument_list|)
operator|.
name|getLayoutVersion
argument_list|()
expr_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|fakeDnProt
init|=
name|mock
argument_list|(
name|DatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fakeDnProt
operator|.
name|versionRequest
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fakeNsInfo
argument_list|)
expr_stmt|;
name|actor
operator|.
name|setNameNode
argument_list|(
name|fakeDnProt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSoftwareVersionDifferences ()
specifier|public
name|void
name|testSoftwareVersionDifferences
parameter_list|()
throws|throws
name|Exception
block|{
comment|// We expect no exception to be thrown when the software versions match.
name|assertEquals
argument_list|(
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|,
name|actor
operator|.
name|retrieveNamespaceInfo
argument_list|()
operator|.
name|getSoftwareVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// We expect no exception to be thrown when the min NN version is below the
comment|// reported NN version.
name|doReturn
argument_list|(
literal|"4.0.0"
argument_list|)
operator|.
name|when
argument_list|(
name|fakeNsInfo
argument_list|)
operator|.
name|getSoftwareVersion
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|"3.0.0"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnConf
argument_list|)
operator|.
name|getMinimumNameNodeVersion
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4.0.0"
argument_list|,
name|actor
operator|.
name|retrieveNamespaceInfo
argument_list|()
operator|.
name|getSoftwareVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// When the NN reports a version that's too low, throw an exception.
name|doReturn
argument_list|(
literal|"3.0.0"
argument_list|)
operator|.
name|when
argument_list|(
name|fakeNsInfo
argument_list|)
operator|.
name|getSoftwareVersion
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|"4.0.0"
argument_list|)
operator|.
name|when
argument_list|(
name|mockDnConf
argument_list|)
operator|.
name|getMinimumNameNodeVersion
argument_list|()
expr_stmt|;
try|try
block|{
name|actor
operator|.
name|retrieveNamespaceInfo
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an exception for NN with too-low version"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IncorrectVersionException
name|ive
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"The reported NameNode version is too low"
argument_list|,
name|ive
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|ive
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDifferentLayoutVersions ()
specifier|public
name|void
name|testDifferentLayoutVersions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// We expect no exceptions to be thrown when the layout versions match.
name|assertEquals
argument_list|(
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
argument_list|,
name|actor
operator|.
name|retrieveNamespaceInfo
argument_list|()
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// We expect an exception to be thrown when the NN reports a layout version
comment|// different from that of the DN.
name|doReturn
argument_list|(
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
operator|*
literal|1000
argument_list|)
operator|.
name|when
argument_list|(
name|fakeNsInfo
argument_list|)
operator|.
name|getLayoutVersion
argument_list|()
expr_stmt|;
try|try
block|{
name|actor
operator|.
name|retrieveNamespaceInfo
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Should not fail to retrieve NS info from DN with different layout version"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDNShutdwonBeforeRegister ()
specifier|public
name|void
name|testDNShutdwonBeforeRegister
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|InetSocketAddress
name|nnADDR
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|5020
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTP_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_IPC_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
literal|"hdfs://"
operator|+
name|nnADDR
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|nnADDR
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|DataNode
name|dn
init|=
operator|new
name|DataNode
argument_list|(
name|conf
argument_list|,
name|locations
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|BPOfferService
name|bpos
init|=
operator|new
name|BPOfferService
argument_list|(
literal|"test_ns"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"nn0"
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|nnADDR
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|InetSocketAddress
operator|>
name|nCopies
argument_list|(
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|,
name|dn
argument_list|)
decl_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|fakeDnProt
init|=
name|mock
argument_list|(
name|DatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fakeDnProt
operator|.
name|versionRequest
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fakeNsInfo
argument_list|)
expr_stmt|;
name|BPServiceActor
name|localActor
init|=
operator|new
name|BPServiceActor
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
name|INVALID_ADDR
argument_list|,
literal|null
argument_list|,
name|bpos
argument_list|)
decl_stmt|;
name|localActor
operator|.
name|setNameNode
argument_list|(
name|fakeDnProt
argument_list|)
expr_stmt|;
try|try
block|{
name|NamespaceInfo
name|nsInfo
init|=
name|localActor
operator|.
name|retrieveNamespaceInfo
argument_list|()
decl_stmt|;
name|bpos
operator|.
name|setNamespaceInfo
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
name|localActor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|localActor
operator|.
name|register
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"DN shut down before block pool registered"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidConfigurationValue ()
specifier|public
name|void
name|testInvalidConfigurationValue
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|HadoopIllegalArgumentException
operator|.
name|class
argument_list|,
literal|"Invalid value configured for dfs.datanode.failed.volumes.tolerated"
operator|+
literal|" - -2 should be greater than or equal to -1"
argument_list|,
parameter_list|()
lambda|->
operator|new
name|DataNode
argument_list|(
name|conf
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

