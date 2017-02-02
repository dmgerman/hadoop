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
name|conf
operator|.
name|ReconfigurationException
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
name|FsVolumeSpi
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
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
operator|.
name|FsVolumeImpl
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
name|test
operator|.
name|GenericTestUtils
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
name|Supplier
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|doAnswer
import|;
end_import

begin_comment
comment|/**  * Utility class for accessing package-private DataNode information during tests.  * Must not contain usage of classes that are not explicitly listed as  * dependencies to {@link MiniDFSCluster}.  */
end_comment

begin_class
DECL|class|DataNodeTestUtils
specifier|public
class|class
name|DataNodeTestUtils
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
name|DataNodeTestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
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
comment|/**    * This method is used to mock the data node block pinning API.    *    * @param dn datanode    * @param pinned true if the block is pinned, false otherwise    * @throws IOException    */
DECL|method|mockDatanodeBlkPinning (final DataNode dn, final boolean pinned)
specifier|public
specifier|static
name|void
name|mockDatanodeBlkPinning
parameter_list|(
specifier|final
name|DataNode
name|dn
parameter_list|,
specifier|final
name|boolean
name|pinned
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FsDatasetSpi
argument_list|<
name|?
extends|extends
name|FsVolumeSpi
argument_list|>
name|data
init|=
name|dn
operator|.
name|data
decl_stmt|;
name|dn
operator|.
name|data
operator|=
name|Mockito
operator|.
name|spy
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Bypass the argument to FsDatasetImpl#getPinning to show that
comment|// the block is pinned.
return|return
name|pinned
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|dn
operator|.
name|data
argument_list|)
operator|.
name|getPinning
argument_list|(
name|any
argument_list|(
name|ExtendedBlock
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reconfigure a DataNode by setting a new list of volumes.    *    * @param dn DataNode to reconfigure    * @param newVols new volumes to configure    * @throws Exception if there is any failure    */
DECL|method|reconfigureDataNode (DataNode dn, File... newVols)
specifier|public
specifier|static
name|void
name|reconfigureDataNode
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|File
modifier|...
name|newVols
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|dnNewDataDirs
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|newVol
range|:
name|newVols
control|)
block|{
if|if
condition|(
name|dnNewDataDirs
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|dnNewDataDirs
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|dnNewDataDirs
operator|.
name|append
argument_list|(
name|newVol
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertThat
argument_list|(
name|dn
operator|.
name|reconfigurePropertyImpl
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dnNewDataDirs
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|(
name|dn
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReconfigurationException
name|e
parameter_list|)
block|{
comment|// This can be thrown if reconfiguration tries to use a failed volume.
comment|// We need to swallow the exception, because some of our tests want to
comment|// cover this case.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not reconfigure DataNode."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Get the FsVolume on the given basePath. */
DECL|method|getVolume (DataNode dn, File basePath)
specifier|public
specifier|static
name|FsVolumeImpl
name|getVolume
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|File
name|basePath
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|volumes
init|=
name|dn
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
for|for
control|(
name|FsVolumeSpi
name|vol
range|:
name|volumes
control|)
block|{
if|if
condition|(
name|vol
operator|.
name|getBaseURI
argument_list|()
operator|.
name|equals
argument_list|(
name|basePath
operator|.
name|toURI
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|(
name|FsVolumeImpl
operator|)
name|vol
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Call and wait DataNode to detect disk failure.    *    * @param dn    * @param volume    * @throws Exception    */
DECL|method|waitForDiskError (DataNode dn, FsVolumeSpi volume)
specifier|public
specifier|static
name|void
name|waitForDiskError
parameter_list|(
name|DataNode
name|dn
parameter_list|,
name|FsVolumeSpi
name|volume
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting to wait for datanode to detect disk failure."
argument_list|)
expr_stmt|;
specifier|final
name|long
name|lastDiskErrorCheck
init|=
name|dn
operator|.
name|getLastDiskErrorCheck
argument_list|()
decl_stmt|;
name|dn
operator|.
name|checkDiskErrorAsync
argument_list|(
name|volume
argument_list|)
expr_stmt|;
comment|// Wait 10 seconds for checkDiskError thread to finish and discover volume
comment|// failures.
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
return|return
name|dn
operator|.
name|getLastDiskErrorCheck
argument_list|()
operator|!=
name|lastDiskErrorCheck
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

