begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
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
name|Util
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
name|NNStorage
operator|.
name|NameNodeDirType
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

begin_comment
comment|/**  * OfflineEditsViewerHelper is a helper class for TestOfflineEditsViewer,  * it performs NN operations that generate all op codes  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OfflineEditsViewerHelper
specifier|public
class|class
name|OfflineEditsViewerHelper
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
name|OfflineEditsViewerHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|blockSize
name|long
name|blockSize
init|=
literal|512
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|config
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|/**    * Generates edits with all op codes and returns the edits filename    */
DECL|method|generateEdits ()
specifier|public
name|String
name|generateEdits
parameter_list|()
throws|throws
name|IOException
block|{
name|CheckpointSignature
name|signature
init|=
name|runOperations
argument_list|()
decl_stmt|;
return|return
name|getEditsFilename
argument_list|(
name|signature
argument_list|)
return|;
block|}
comment|/**    * Get edits filename    *    * @return edits file name for cluster    */
DECL|method|getEditsFilename (CheckpointSignature sig)
specifier|private
name|String
name|getEditsFilename
parameter_list|(
name|CheckpointSignature
name|sig
parameter_list|)
throws|throws
name|IOException
block|{
name|FSImage
name|image
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
comment|// it was set up to only have ONE StorageDirectory
name|Iterator
argument_list|<
name|StorageDirectory
argument_list|>
name|it
init|=
name|image
operator|.
name|getStorage
argument_list|()
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|File
name|ret
init|=
name|NNStorage
operator|.
name|getFinalizedEditsFile
argument_list|(
name|sd
argument_list|,
literal|1
argument_list|,
name|sig
operator|.
name|curSegmentTxId
operator|-
literal|1
argument_list|)
decl_stmt|;
assert|assert
name|ret
operator|.
name|exists
argument_list|()
operator|:
literal|"expected "
operator|+
name|ret
operator|+
literal|" exists"
assert|;
return|return
name|ret
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
comment|/**    * Sets up a MiniDFSCluster, configures it to create one edits file,    * starts DelegationTokenSecretManager (to get security op codes)    *    * @param dfsDir DFS directory (where to setup MiniDFS cluster)    */
DECL|method|startCluster (String dfsDir)
specifier|public
name|void
name|startCluster
parameter_list|(
name|String
name|dfsDir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// same as manageDfsDirs but only one edits file instead of two
name|config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|Util
operator|.
name|fileAsURI
argument_list|(
operator|new
name|File
argument_list|(
name|dfsDir
argument_list|,
literal|"name"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_DIR_KEY
argument_list|,
name|Util
operator|.
name|fileAsURI
argument_list|(
operator|new
name|File
argument_list|(
name|dfsDir
argument_list|,
literal|"namesecondary1"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// blocksize for concat (file size must be multiple of blocksize)
name|config
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
comment|// for security to work (fake JobTracker user)
name|config
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTH_TO_LOCAL
argument_list|,
literal|"RULE:[2:$1@$0](JobTracker@.*FOO.COM)s/@.*//"
operator|+
literal|"DEFAULT"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|manageNameDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown the cluster    */
DECL|method|shutdownCluster ()
specifier|public
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|IOException
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
comment|/**    * Run file operations to create edits for all op codes    * to be tested.    *    * the following op codes are deprecated and therefore not tested:    *    * OP_DATANODE_ADD    ( 5)    * OP_DATANODE_REMOVE ( 6)    * OP_SET_NS_QUOTA    (11)    * OP_CLEAR_NS_QUOTA  (12)    */
DECL|method|runOperations ()
specifier|private
name|CheckpointSignature
name|runOperations
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating edits by performing fs operations"
argument_list|)
expr_stmt|;
comment|// no check, if it's not it throws an exception which is what we want
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|runOperations
argument_list|(
name|cluster
argument_list|,
name|dfs
argument_list|,
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|,
name|dfs
operator|.
name|getDefaultBlockSize
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getEditLog
argument_list|()
operator|.
name|logStartRollingUpgrade
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
comment|// Force a roll so we get an OP_END_LOG_SEGMENT txn
return|return
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|rollEditLog
argument_list|()
return|;
block|}
block|}
end_class

end_unit

