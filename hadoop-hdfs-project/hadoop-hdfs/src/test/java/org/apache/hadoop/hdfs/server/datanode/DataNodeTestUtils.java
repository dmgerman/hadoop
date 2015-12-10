begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one   * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|protocol
operator|.
name|Block
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
name|DatanodeID
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
name|ExtendedBlock
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
name|datanode
operator|.
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|fsdataset
operator|.
name|impl
operator|.
name|FsDatasetTestUtil
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
name|NameNode
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
name|InterDatanodeProtocol
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
name|junit
operator|.
name|Assert
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
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Utility class for accessing package-private DataNode information during tests.  *  */
end_comment

begin_class
DECL|class|DataNodeTestUtils
specifier|public
class|class
name|DataNodeTestUtils
block|{
DECL|field|DIR_FAILURE_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|DIR_FAILURE_SUFFIX
init|=
literal|".origin"
decl_stmt|;
DECL|field|TEST_CLUSTER_ID
specifier|public
specifier|final
specifier|static
name|String
name|TEST_CLUSTER_ID
init|=
literal|"testClusterID"
decl_stmt|;
DECL|field|TEST_POOL_ID
specifier|public
specifier|final
specifier|static
name|String
name|TEST_POOL_ID
init|=
literal|"BP-TEST"
decl_stmt|;
specifier|public
specifier|static
name|DatanodeRegistration
DECL|method|getDNRegistrationForBP (DataNode dn, String bpid)
name|getDNRegistrationForBP
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dn
operator|.
name|getDNRegistrationForBP
argument_list|(
name|bpid
argument_list|)
return|;
block|}
DECL|method|setHeartbeatsDisabledForTests (DataNode dn, boolean heartbeatsDisabledForTests)
specifier|public
specifier|static
name|void
name|setHeartbeatsDisabledForTests
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|boolean
name|heartbeatsDisabledForTests
parameter_list|)
block|{
name|dn
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|heartbeatsDisabledForTests
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set if cache reports are disabled for all DNs in a mini cluster.    */
DECL|method|setCacheReportsDisabledForTests (MiniDFSCluster cluster, boolean disabled)
specifier|public
specifier|static
name|void
name|setCacheReportsDisabledForTests
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|boolean
name|disabled
parameter_list|)
block|{
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
name|setCacheReportsDisabledForTest
argument_list|(
name|disabled
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|triggerDeletionReport (DataNode dn)
specifier|public
specifier|static
name|void
name|triggerDeletionReport
parameter_list|(
name|DataNode
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
name|bpos
operator|.
name|triggerDeletionReportForTests
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|triggerHeartbeat (DataNode dn)
specifier|public
specifier|static
name|void
name|triggerHeartbeat
parameter_list|(
name|DataNode
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
name|bpos
operator|.
name|triggerHeartbeatForTests
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|triggerBlockReport (DataNode dn)
specifier|public
specifier|static
name|void
name|triggerBlockReport
parameter_list|(
name|DataNode
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
name|bpos
operator|.
name|triggerBlockReportForTests
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Insert a Mockito spy object between the given DataNode and    * the given NameNode. This can be used to delay or wait for    * RPC calls on the datanode->NN path.    */
DECL|method|spyOnBposToNN ( DataNode dn, NameNode nn)
specifier|public
specifier|static
name|DatanodeProtocolClientSideTranslatorPB
name|spyOnBposToNN
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|NameNode
name|nn
parameter_list|)
block|{
name|String
name|bpid
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|BPOfferService
name|bpos
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BPOfferService
name|thisBpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
if|if
condition|(
name|thisBpos
operator|.
name|getBlockPoolId
argument_list|()
operator|.
name|equals
argument_list|(
name|bpid
argument_list|)
condition|)
block|{
name|bpos
operator|=
name|thisBpos
expr_stmt|;
break|break;
block|}
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bpos
operator|!=
literal|null
argument_list|,
literal|"No such bpid: %s"
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|BPServiceActor
name|bpsa
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BPServiceActor
name|thisBpsa
range|:
name|bpos
operator|.
name|getBPServiceActors
argument_list|()
control|)
block|{
if|if
condition|(
name|thisBpsa
operator|.
name|getNNSocketAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|nn
operator|.
name|getServiceRpcAddress
argument_list|()
argument_list|)
condition|)
block|{
name|bpsa
operator|=
name|thisBpsa
expr_stmt|;
break|break;
block|}
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bpsa
operator|!=
literal|null
argument_list|,
literal|"No service actor to NN at %s"
argument_list|,
name|nn
operator|.
name|getServiceRpcAddress
argument_list|()
argument_list|)
expr_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|origNN
init|=
name|bpsa
operator|.
name|getNameNodeProxy
argument_list|()
decl_stmt|;
name|DatanodeProtocolClientSideTranslatorPB
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|origNN
argument_list|)
decl_stmt|;
name|bpsa
operator|.
name|setNameNode
argument_list|(
name|spy
argument_list|)
expr_stmt|;
return|return
name|spy
return|;
block|}
DECL|method|createInterDatanodeProtocolProxy ( DataNode dn, DatanodeID datanodeid, final Configuration conf, boolean connectToDnViaHostname)
specifier|public
specifier|static
name|InterDatanodeProtocol
name|createInterDatanodeProtocolProxy
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|DatanodeID
name|datanodeid
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
name|boolean
name|connectToDnViaHostname
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|connectToDnViaHostname
operator|!=
name|dn
operator|.
name|getDnConf
argument_list|()
operator|.
name|connectToDnViaHostname
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unexpected DN hostname configuration"
argument_list|)
throw|;
block|}
return|return
name|DataNode
operator|.
name|createInterDataNodeProtocolProxy
argument_list|(
name|datanodeid
argument_list|,
name|conf
argument_list|,
name|dn
operator|.
name|getDnConf
argument_list|()
operator|.
name|socketTimeout
argument_list|,
name|dn
operator|.
name|getDnConf
argument_list|()
operator|.
name|connectToDnViaHostname
argument_list|)
return|;
block|}
comment|/**    * This method is used for testing.     * Examples are adding and deleting blocks directly.    * The most common usage will be when the data node's storage is simulated.    *     * @return the fsdataset that stores the blocks    */
DECL|method|getFSDataset (DataNode dn)
specifier|public
specifier|static
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|getFSDataset
parameter_list|(
name|DataNode
name|dn
parameter_list|)
block|{
return|return
name|dn
operator|.
name|getFSDataset
argument_list|()
return|;
block|}
DECL|method|getFile (DataNode dn, String bpid, long bid)
specifier|public
specifier|static
name|File
name|getFile
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|String
name|bpid
parameter_list|,
name|long
name|bid
parameter_list|)
block|{
return|return
name|FsDatasetTestUtil
operator|.
name|getFile
argument_list|(
name|dn
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|bpid
argument_list|,
name|bid
argument_list|)
return|;
block|}
DECL|method|getBlockFile (DataNode dn, String bpid, Block b )
specifier|public
specifier|static
name|File
name|getBlockFile
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FsDatasetTestUtil
operator|.
name|getBlockFile
argument_list|(
name|dn
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|bpid
argument_list|,
name|b
argument_list|)
return|;
block|}
DECL|method|unlinkBlock (DataNode dn, ExtendedBlock bk, int numLinks )
specifier|public
specifier|static
name|boolean
name|unlinkBlock
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|ExtendedBlock
name|bk
parameter_list|,
name|int
name|numLinks
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FsDatasetTestUtil
operator|.
name|unlinkBlock
argument_list|(
name|dn
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|bk
argument_list|,
name|numLinks
argument_list|)
return|;
block|}
comment|/**    * Fetch a copy of ReplicaInfo from a datanode by block id    * @param dn datanode to retrieve a replicainfo object from    * @param bpid Block pool Id    * @param blkId id of the replica's block    * @return copy of ReplicaInfo object @link{FSDataset#fetchReplicaInfo}    */
DECL|method|fetchReplicaInfo (final DataNode dn, final String bpid, final long blkId)
specifier|public
specifier|static
name|ReplicaInfo
name|fetchReplicaInfo
parameter_list|(
specifier|final
name|DataNode
name|dn
parameter_list|,
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blkId
parameter_list|)
block|{
return|return
name|FsDatasetTestUtil
operator|.
name|fetchReplicaInfo
argument_list|(
name|dn
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|bpid
argument_list|,
name|blkId
argument_list|)
return|;
block|}
comment|/**    * It injects disk failures to data dirs by replacing these data dirs with    * regular files.    *    * @param dirs data directories.    * @throws IOException on I/O error.    */
DECL|method|injectDataDirFailure (File... dirs)
specifier|public
specifier|static
name|void
name|injectDataDirFailure
parameter_list|(
name|File
modifier|...
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|File
name|dir
range|:
name|dirs
control|)
block|{
name|File
name|renamedTo
init|=
operator|new
name|File
argument_list|(
name|dir
operator|.
name|getPath
argument_list|()
operator|+
name|DIR_FAILURE_SUFFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|renamedTo
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Can not inject failure to dir: %s because %s exists."
argument_list|,
name|dir
argument_list|,
name|renamedTo
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|dir
operator|.
name|renameTo
argument_list|(
name|renamedTo
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to rename %s to %s."
argument_list|,
name|dir
argument_list|,
name|renamedTo
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|dir
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to create file %s to inject disk failure."
argument_list|,
name|dir
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Restore the injected data dir failures.    *    * @see {@link #injectDataDirFailures}.    * @param dirs data directories.    * @throws IOException    */
DECL|method|restoreDataDirFromFailure (File... dirs)
specifier|public
specifier|static
name|void
name|restoreDataDirFromFailure
parameter_list|(
name|File
modifier|...
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|File
name|dir
range|:
name|dirs
control|)
block|{
name|File
name|renamedDir
init|=
operator|new
name|File
argument_list|(
name|dir
operator|.
name|getPath
argument_list|()
operator|+
name|DIR_FAILURE_SUFFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|renamedDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|isFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Injected failure data dir is supposed to be file: "
operator|+
name|dir
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|dir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete injected failure data dir: "
operator|+
name|dir
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|renamedDir
operator|.
name|renameTo
argument_list|(
name|dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to recover injected failure data dir %s to %s."
argument_list|,
name|renamedDir
argument_list|,
name|dir
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|runDirectoryScanner (DataNode dn)
specifier|public
specifier|static
name|void
name|runDirectoryScanner
parameter_list|(
name|DataNode
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
name|DirectoryScanner
name|directoryScanner
init|=
name|dn
operator|.
name|getDirectoryScanner
argument_list|()
decl_stmt|;
if|if
condition|(
name|directoryScanner
operator|!=
literal|null
condition|)
block|{
name|dn
operator|.
name|getDirectoryScanner
argument_list|()
operator|.
name|reconcile
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Starts an instance of DataNode with NN mocked. Called should ensure to    * shutdown the DN    *    * @throws IOException    */
DECL|method|startDNWithMockNN (Configuration conf, final InetSocketAddress nnSocketAddr, final String dnDataDir)
specifier|public
specifier|static
name|DataNode
name|startDNWithMockNN
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|InetSocketAddress
name|nnSocketAddr
parameter_list|,
specifier|final
name|String
name|dnDataDir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
literal|"hdfs://"
operator|+
name|nnSocketAddr
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|nnSocketAddr
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
argument_list|<
name|StorageLocation
argument_list|>
argument_list|()
decl_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|dnDataDir
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
return|return
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
name|TEST_CLUSTER_ID
argument_list|,
name|TEST_POOL_ID
argument_list|,
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|namenode
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
argument_list|)
operator|.
name|thenReturn
argument_list|(
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
argument_list|)
expr_stmt|;
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
name|nnSocketAddr
argument_list|,
name|nnAddr
argument_list|)
expr_stmt|;
return|return
name|namenode
return|;
block|}
block|}
decl_stmt|;
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
return|return
name|dn
return|;
block|}
block|}
end_class

end_unit

