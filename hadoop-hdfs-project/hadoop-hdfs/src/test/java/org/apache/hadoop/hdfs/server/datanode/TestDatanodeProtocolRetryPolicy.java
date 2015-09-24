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
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|URISyntaxException
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
name|concurrent
operator|.
name|ThreadLocalRandom
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
name|base
operator|.
name|Supplier
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
name|CommonConfigurationKeys
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
name|FileUtil
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
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|protocol
operator|.
name|BlockReportContext
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
name|DatanodeCommand
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
name|DatanodeRegistration
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
name|HeartbeatResponse
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|NNHAStatusHeartbeat
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
name|RegisterCommand
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
name|StorageBlockReport
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
name|StorageReport
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
name|VolumeFailureSummary
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
name|log4j
operator|.
name|Level
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
name|Assert
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

begin_comment
comment|/**  * This tests DatanodeProtocol retry policy  */
end_comment

begin_class
DECL|class|TestDatanodeProtocolRetryPolicy
specifier|public
class|class
name|TestDatanodeProtocolRetryPolicy
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDatanodeProtocolRetryPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DATA_DIR
specifier|private
specifier|static
specifier|final
name|String
name|DATA_DIR
init|=
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"data"
decl_stmt|;
DECL|field|dn
specifier|private
name|DataNode
name|dn
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|tearDownDone
specifier|private
name|boolean
name|tearDownDone
decl_stmt|;
DECL|field|locations
name|ArrayList
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageLocation
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|CLUSTER_ID
specifier|private
specifier|final
specifier|static
name|String
name|CLUSTER_ID
init|=
literal|"testClusterID"
decl_stmt|;
DECL|field|POOL_ID
specifier|private
specifier|final
specifier|static
name|String
name|POOL_ID
init|=
literal|"BP-TEST"
decl_stmt|;
DECL|field|NN_ADDR
specifier|private
specifier|final
specifier|static
name|InetSocketAddress
name|NN_ADDR
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|5020
argument_list|)
decl_stmt|;
DECL|field|datanodeRegistration
specifier|private
specifier|static
name|DatanodeRegistration
name|datanodeRegistration
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeRegistration
argument_list|()
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
comment|/**    * Starts an instance of DataNode    * @throws IOException    */
annotation|@
name|Before
DECL|method|startUp ()
specifier|public
name|void
name|startUp
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|tearDownDone
operator|=
literal|false
expr_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|DATA_DIR
argument_list|)
expr_stmt|;
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
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|0
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
name|NN_ADDR
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|NN_ADDR
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|DATA_DIR
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|StorageLocation
name|location
init|=
name|StorageLocation
operator|.
name|parse
argument_list|(
name|dataDir
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
comment|/**    * Cleans the resources and closes the instance of datanode    * @throws IOException if an error occurred    */
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|tearDownDone
operator|&&
name|dn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot close: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|DATA_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Cannot delete data-node dirs"
argument_list|,
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tearDownDone
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|waitForBlockReport ( final DatanodeProtocolClientSideTranslatorPB mockNN)
specifier|private
name|void
name|waitForBlockReport
parameter_list|(
specifier|final
name|DatanodeProtocolClientSideTranslatorPB
name|mockNN
parameter_list|)
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|mockNN
argument_list|)
operator|.
name|blockReport
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|datanodeRegistration
argument_list|)
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
name|POOL_ID
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|StorageBlockReport
index|[]
operator|>
name|anyObject
argument_list|()
argument_list|,
name|Mockito
operator|.
expr|<
name|BlockReportContext
operator|>
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting on block report: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|500
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the following scenario.    * 1. The initial DatanodeProtocol.registerDatanode succeeds.    * 2. DN starts heartbeat process.    * 3. In the first heartbeat, NN asks DN to reregister.    * 4. DN calls DatanodeProtocol.registerDatanode.    * 5. DatanodeProtocol.registerDatanode throws EOFException.    * 6. DN retries.    * 7. DatanodeProtocol.registerDatanode succeeds.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testDatanodeRegistrationRetry ()
specifier|public
name|void
name|testDatanodeRegistrationRetry
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DatanodeProtocolClientSideTranslatorPB
name|namenode
init|=
name|mock
argument_list|(
name|DatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|DatanodeRegistration
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|DatanodeRegistration
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|1
operator|&&
name|i
operator|<
literal|5
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"mockito exception "
operator|+
name|i
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"TestDatanodeProtocolRetryPolicy"
argument_list|)
throw|;
block|}
else|else
block|{
name|DatanodeRegistration
name|dr
init|=
operator|(
name|DatanodeRegistration
operator|)
name|invocation
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|datanodeRegistration
operator|=
operator|new
name|DatanodeRegistration
argument_list|(
name|dr
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|dr
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"mockito succeeded "
operator|+
name|datanodeRegistration
argument_list|)
expr_stmt|;
return|return
name|datanodeRegistration
return|;
block|}
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|namenode
argument_list|)
operator|.
name|registerDatanode
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|namenode
operator|.
name|versionRequest
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|NamespaceInfo
argument_list|(
literal|1
argument_list|,
name|CLUSTER_ID
argument_list|,
name|POOL_ID
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|HeartbeatResponse
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|HeartbeatResponse
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|i
operator|++
expr_stmt|;
name|HeartbeatResponse
name|heartbeatResponse
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"mockito heartbeatResponse registration "
operator|+
name|i
argument_list|)
expr_stmt|;
name|heartbeatResponse
operator|=
operator|new
name|HeartbeatResponse
argument_list|(
operator|new
name|DatanodeCommand
index|[]
block|{
name|RegisterCommand
operator|.
name|REGISTER
block|}
argument_list|,
operator|new
name|NNHAStatusHeartbeat
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|,
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextLong
argument_list|()
operator||
literal|1L
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"mockito heartbeatResponse "
operator|+
name|i
argument_list|)
expr_stmt|;
name|heartbeatResponse
operator|=
operator|new
name|HeartbeatResponse
argument_list|(
operator|new
name|DatanodeCommand
index|[
literal|0
index|]
argument_list|,
operator|new
name|NNHAStatusHeartbeat
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|,
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextLong
argument_list|()
operator||
literal|1L
argument_list|)
expr_stmt|;
block|}
return|return
name|heartbeatResponse
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|namenode
argument_list|)
operator|.
name|sendHeartbeat
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|StorageReport
index|[]
operator|.
expr|class
argument_list|)
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
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|VolumeFailureSummary
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|dn
operator|=
operator|new
name|DataNode
argument_list|(
name|conf
argument_list|,
name|locations
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
name|DatanodeProtocolClientSideTranslatorPB
name|connectToNN
parameter_list|(
name|InetSocketAddress
name|nnAddr
parameter_list|)
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NN_ADDR
argument_list|,
name|nnAddr
argument_list|)
expr_stmt|;
return|return
name|namenode
return|;
block|}
block|}
expr_stmt|;
comment|// Trigger a heartbeat so that it acknowledges the NN as active.
name|dn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|triggerHeartbeatForTests
argument_list|()
expr_stmt|;
name|waitForBlockReport
argument_list|(
name|namenode
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

