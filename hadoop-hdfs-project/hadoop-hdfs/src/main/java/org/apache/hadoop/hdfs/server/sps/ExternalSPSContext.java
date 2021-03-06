begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.sps
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
name|sps
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|DFSUtilClient
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
name|BlockStoragePolicy
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
name|HdfsConstants
operator|.
name|DatanodeReportType
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
name|HdfsFileStatus
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
name|HdfsLocatedFileStatus
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
name|balancer
operator|.
name|NameNodeConnector
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
name|blockmanagement
operator|.
name|BlockStoragePolicySuite
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
name|namenode
operator|.
name|sps
operator|.
name|BlockMoveTaskHandler
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
name|sps
operator|.
name|BlockMovementListener
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
name|sps
operator|.
name|Context
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
name|sps
operator|.
name|FileCollector
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
name|sps
operator|.
name|SPSService
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
name|sps
operator|.
name|StoragePolicySatisfier
operator|.
name|DatanodeMap
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
name|sps
operator|.
name|StoragePolicySatisfier
operator|.
name|DatanodeWithStorage
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
name|BlockStorageMovementCommand
operator|.
name|BlockMovingInfo
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
name|DatanodeStorageReport
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
name|NetworkTopology
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

begin_comment
comment|/**  * This class used to connect to Namenode and gets the required information to  * SPS from Namenode state.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ExternalSPSContext
specifier|public
class|class
name|ExternalSPSContext
implements|implements
name|Context
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
name|ExternalSPSContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|service
specifier|private
specifier|final
name|SPSService
name|service
decl_stmt|;
DECL|field|nnc
specifier|private
specifier|final
name|NameNodeConnector
name|nnc
decl_stmt|;
DECL|field|createDefaultSuite
specifier|private
specifier|final
name|BlockStoragePolicySuite
name|createDefaultSuite
init|=
name|BlockStoragePolicySuite
operator|.
name|createDefaultSuite
argument_list|()
decl_stmt|;
DECL|field|fileCollector
specifier|private
specifier|final
name|FileCollector
name|fileCollector
decl_stmt|;
DECL|field|externalHandler
specifier|private
specifier|final
name|BlockMoveTaskHandler
name|externalHandler
decl_stmt|;
DECL|field|blkMovementListener
specifier|private
specifier|final
name|BlockMovementListener
name|blkMovementListener
decl_stmt|;
DECL|method|ExternalSPSContext (SPSService service, NameNodeConnector nnc)
specifier|public
name|ExternalSPSContext
parameter_list|(
name|SPSService
name|service
parameter_list|,
name|NameNodeConnector
name|nnc
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|nnc
operator|=
name|nnc
expr_stmt|;
name|this
operator|.
name|fileCollector
operator|=
operator|new
name|ExternalSPSFilePathCollector
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|this
operator|.
name|externalHandler
operator|=
operator|new
name|ExternalSPSBlockMoveTaskHandler
argument_list|(
name|service
operator|.
name|getConf
argument_list|()
argument_list|,
name|nnc
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|this
operator|.
name|blkMovementListener
operator|=
operator|new
name|ExternalBlockMovementListener
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isRunning ()
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|service
operator|.
name|isRunning
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isInSafeMode ()
specifier|public
name|boolean
name|isInSafeMode
parameter_list|()
block|{
try|try
block|{
return|return
name|nnc
operator|!=
literal|null
condition|?
name|nnc
operator|.
name|getDistributedFileSystem
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
else|:
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while creating Namenode Connector.."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNetworkTopology (DatanodeMap datanodeMap)
specifier|public
name|NetworkTopology
name|getNetworkTopology
parameter_list|(
name|DatanodeMap
name|datanodeMap
parameter_list|)
block|{
comment|// create network topology.
name|NetworkTopology
name|cluster
init|=
name|NetworkTopology
operator|.
name|getInstance
argument_list|(
name|service
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeWithStorage
argument_list|>
name|targets
init|=
name|datanodeMap
operator|.
name|getTargets
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeWithStorage
name|node
range|:
name|targets
control|)
block|{
name|cluster
operator|.
name|add
argument_list|(
name|node
operator|.
name|getDatanodeInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|cluster
return|;
block|}
annotation|@
name|Override
DECL|method|isFileExist (long path)
specifier|public
name|boolean
name|isFileExist
parameter_list|(
name|long
name|path
parameter_list|)
block|{
name|Path
name|filePath
init|=
name|DFSUtilClient
operator|.
name|makePathFromFileId
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|nnc
operator|.
name|getDistributedFileSystem
argument_list|()
operator|.
name|exists
argument_list|(
name|filePath
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while getting file is for the given path:{}"
argument_list|,
name|filePath
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getStoragePolicy (byte policyId)
specifier|public
name|BlockStoragePolicy
name|getStoragePolicy
parameter_list|(
name|byte
name|policyId
parameter_list|)
block|{
return|return
name|createDefaultSuite
operator|.
name|getPolicy
argument_list|(
name|policyId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|removeSPSHint (long inodeId)
specifier|public
name|void
name|removeSPSHint
parameter_list|(
name|long
name|inodeId
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|filePath
init|=
name|DFSUtilClient
operator|.
name|makePathFromFileId
argument_list|(
name|inodeId
argument_list|)
decl_stmt|;
try|try
block|{
name|nnc
operator|.
name|getDistributedFileSystem
argument_list|()
operator|.
name|removeXAttr
argument_list|(
name|filePath
argument_list|,
name|HdfsServerConstants
operator|.
name|XATTR_SATISFY_STORAGE_POLICY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|listXAttrs
init|=
name|nnc
operator|.
name|getDistributedFileSystem
argument_list|()
operator|.
name|listXAttrs
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|listXAttrs
operator|.
name|contains
argument_list|(
name|HdfsServerConstants
operator|.
name|XATTR_SATISFY_STORAGE_POLICY
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"SPS hint already removed for the inodeId:{}."
operator|+
literal|" Ignoring exception:{}"
argument_list|,
name|inodeId
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getNumLiveDataNodes ()
specifier|public
name|int
name|getNumLiveDataNodes
parameter_list|()
block|{
try|try
block|{
return|return
name|nnc
operator|.
name|getDistributedFileSystem
argument_list|()
operator|.
name|getDataNodeStats
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
operator|.
name|length
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while getting number of live datanodes."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getFileInfo (long path)
specifier|public
name|HdfsFileStatus
name|getFileInfo
parameter_list|(
name|long
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|HdfsLocatedFileStatus
name|fileInfo
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Path
name|filePath
init|=
name|DFSUtilClient
operator|.
name|makePathFromFileId
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|fileInfo
operator|=
name|nnc
operator|.
name|getDistributedFileSystem
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedFileInfo
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Path:{} doesn't exists!"
argument_list|,
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|fileInfo
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveDatanodeStorageReport ()
specifier|public
name|DatanodeStorageReport
index|[]
name|getLiveDatanodeStorageReport
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|nnc
operator|.
name|getLiveDatanodeStorageReport
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNextSPSPath ()
specifier|public
name|Long
name|getNextSPSPath
parameter_list|()
block|{
try|try
block|{
return|return
name|nnc
operator|.
name|getNNProtocolConnection
argument_list|()
operator|.
name|getNextSPSPath
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while getting next sps path id from Namenode."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|scanAndCollectFiles (long path)
specifier|public
name|void
name|scanAndCollectFiles
parameter_list|(
name|long
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|fileCollector
operator|.
name|scanAndCollectFiles
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|submitMoveTask (BlockMovingInfo blkMovingInfo)
specifier|public
name|void
name|submitMoveTask
parameter_list|(
name|BlockMovingInfo
name|blkMovingInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|externalHandler
operator|.
name|submitMoveTask
argument_list|(
name|blkMovingInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|notifyMovementTriedBlocks (Block[] moveAttemptFinishedBlks)
specifier|public
name|void
name|notifyMovementTriedBlocks
parameter_list|(
name|Block
index|[]
name|moveAttemptFinishedBlks
parameter_list|)
block|{
comment|// External listener if it is plugged-in
if|if
condition|(
name|blkMovementListener
operator|!=
literal|null
condition|)
block|{
name|blkMovementListener
operator|.
name|notifyMovementTriedBlocks
argument_list|(
name|moveAttemptFinishedBlks
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Its an implementation of BlockMovementListener.    */
DECL|class|ExternalBlockMovementListener
specifier|private
specifier|static
class|class
name|ExternalBlockMovementListener
implements|implements
name|BlockMovementListener
block|{
DECL|field|actualBlockMovements
specifier|private
name|List
argument_list|<
name|Block
argument_list|>
name|actualBlockMovements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|notifyMovementTriedBlocks (Block[] moveAttemptFinishedBlks)
specifier|public
name|void
name|notifyMovementTriedBlocks
parameter_list|(
name|Block
index|[]
name|moveAttemptFinishedBlks
parameter_list|)
block|{
for|for
control|(
name|Block
name|block
range|:
name|moveAttemptFinishedBlks
control|)
block|{
name|actualBlockMovements
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Movement attempted blocks"
argument_list|,
name|actualBlockMovements
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

