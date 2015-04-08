begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
name|namenode
operator|.
name|ha
package|;
end_package

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
name|fail
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
name|URISyntaxException
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
name|ha
operator|.
name|ServiceFailedException
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
name|DFSClient
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
name|DFSClientAdapter
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
name|MiniDFSNNTopology
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
name|hdfs
operator|.
name|protocol
operator|.
name|LocatedBlock
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
name|LocatedBlocks
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenSecretManager
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
name|DataNode
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
name|namenode
operator|.
name|FSNamesystem
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
name|token
operator|.
name|Token
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
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
DECL|class|TestFailoverWithBlockTokensEnabled
specifier|public
class|class
name|TestFailoverWithBlockTokensEnabled
block|{
DECL|field|TEST_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/test-path"
argument_list|)
decl_stmt|;
DECL|field|TEST_DATA
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DATA
init|=
literal|"very important text"
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|Before
DECL|method|startCluster ()
specifier|public
name|void
name|startCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Set short retry timeouts so this test runs faster
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutDownCluster ()
specifier|public
name|void
name|shutDownCluster
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|ensureSerialNumbersNeverOverlap ()
specifier|public
name|void
name|ensureSerialNumbersNeverOverlap
parameter_list|()
block|{
name|BlockTokenSecretManager
name|btsm1
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getBlockTokenSecretManager
argument_list|()
decl_stmt|;
name|BlockTokenSecretManager
name|btsm2
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|1
argument_list|)
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getBlockTokenSecretManager
argument_list|()
decl_stmt|;
name|btsm1
operator|.
name|setSerialNo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|btsm2
operator|.
name|setSerialNo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|btsm1
operator|.
name|getSerialNoForTesting
argument_list|()
operator|==
name|btsm2
operator|.
name|getSerialNoForTesting
argument_list|()
argument_list|)
expr_stmt|;
name|btsm1
operator|.
name|setSerialNo
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|btsm2
operator|.
name|setSerialNo
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|btsm1
operator|.
name|getSerialNoForTesting
argument_list|()
operator|==
name|btsm2
operator|.
name|getSerialNoForTesting
argument_list|()
argument_list|)
expr_stmt|;
name|btsm1
operator|.
name|setSerialNo
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|btsm2
operator|.
name|setSerialNo
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|btsm1
operator|.
name|getSerialNoForTesting
argument_list|()
operator|==
name|btsm2
operator|.
name|getSerialNoForTesting
argument_list|()
argument_list|)
expr_stmt|;
name|btsm1
operator|.
name|setSerialNo
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|2
argument_list|)
expr_stmt|;
name|btsm2
operator|.
name|setSerialNo
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|btsm1
operator|.
name|getSerialNoForTesting
argument_list|()
operator|==
name|btsm2
operator|.
name|getSerialNoForTesting
argument_list|()
argument_list|)
expr_stmt|;
name|btsm1
operator|.
name|setSerialNo
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
operator|/
literal|2
argument_list|)
expr_stmt|;
name|btsm2
operator|.
name|setSerialNo
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
operator|/
literal|2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|btsm1
operator|.
name|getSerialNoForTesting
argument_list|()
operator|==
name|btsm2
operator|.
name|getSerialNoForTesting
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|ensureInvalidBlockTokensAreRejected ()
specifier|public
name|void
name|ensureInvalidBlockTokensAreRejected
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
name|TEST_DATA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_DATA
argument_list|,
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|DFSClient
name|dfsClient
init|=
name|DFSClientAdapter
operator|.
name|getDFSClient
argument_list|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
argument_list|)
decl_stmt|;
name|DFSClient
name|spyDfsClient
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|dfsClient
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|LocatedBlocks
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LocatedBlocks
name|answer
parameter_list|(
name|InvocationOnMock
name|arg0
parameter_list|)
throws|throws
name|Throwable
block|{
name|LocatedBlocks
name|locatedBlocks
init|=
operator|(
name|LocatedBlocks
operator|)
name|arg0
operator|.
name|callRealMethod
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
init|=
name|lb
operator|.
name|getBlockToken
argument_list|()
decl_stmt|;
name|BlockTokenIdentifier
name|id
init|=
name|lb
operator|.
name|getBlockToken
argument_list|()
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
comment|// This will make the token invalid, since the password
comment|// won't match anymore
name|id
operator|.
name|setExpiryDate
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|10
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|newToken
init|=
operator|new
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
argument_list|(
name|id
operator|.
name|getBytes
argument_list|()
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
argument_list|,
name|token
operator|.
name|getKind
argument_list|()
argument_list|,
name|token
operator|.
name|getService
argument_list|()
argument_list|)
decl_stmt|;
name|lb
operator|.
name|setBlockToken
argument_list|(
name|newToken
argument_list|)
expr_stmt|;
block|}
return|return
name|locatedBlocks
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|spyDfsClient
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|DFSClientAdapter
operator|.
name|setDFSClient
argument_list|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
argument_list|,
name|spyDfsClient
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|TEST_DATA
argument_list|,
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't have been able to read a file with invalid block tokens"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Could not obtain block"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFailoverAfterRegistration ()
specifier|public
name|void
name|testFailoverAfterRegistration
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|writeUsingBothNameNodes
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFailoverAfterAccessKeyUpdate ()
specifier|public
name|void
name|TestFailoverAfterAccessKeyUpdate
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
name|lowerKeyUpdateIntervalAndClearKeys
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
comment|// Sleep 10s to guarantee DNs heartbeat and get new keys.
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|writeUsingBothNameNodes
argument_list|()
expr_stmt|;
block|}
DECL|method|writeUsingBothNameNodes ()
specifier|private
name|void
name|writeUsingBothNameNodes
parameter_list|()
throws|throws
name|ServiceFailedException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
name|TEST_DATA
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|TEST_PATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
name|TEST_DATA
argument_list|)
expr_stmt|;
block|}
DECL|method|lowerKeyUpdateIntervalAndClearKeys (MiniDFSCluster cluster)
specifier|private
specifier|static
name|void
name|lowerKeyUpdateIntervalAndClearKeys
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|)
block|{
name|lowerKeyUpdateIntervalAndClearKeys
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|lowerKeyUpdateIntervalAndClearKeys
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|dn
operator|.
name|clearAllBlockSecretKeys
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|lowerKeyUpdateIntervalAndClearKeys (FSNamesystem namesystem)
specifier|private
specifier|static
name|void
name|lowerKeyUpdateIntervalAndClearKeys
parameter_list|(
name|FSNamesystem
name|namesystem
parameter_list|)
block|{
name|BlockTokenSecretManager
name|btsm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getBlockTokenSecretManager
argument_list|()
decl_stmt|;
name|btsm
operator|.
name|setKeyUpdateIntervalForTesting
argument_list|(
literal|2
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|btsm
operator|.
name|setTokenLifetime
argument_list|(
literal|2
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|btsm
operator|.
name|clearAllKeysForTesting
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

