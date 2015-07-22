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
name|IOException
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
name|fs
operator|.
name|UnresolvedLinkException
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
name|permission
operator|.
name|FsAction
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
name|QuotaExceededException
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
name|SnapshotAccessControlException
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
name|BlockInfo
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
name|BlockInfoUnderConstruction
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
name|BlockInfoUnderConstructionContiguous
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
name|BlockManager
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
operator|.
name|BlockUCState
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
operator|.
name|RecoverLeaseOp
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
name|INode
operator|.
name|BlocksMapUpdateInfo
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * Helper class to perform truncate operation.  */
end_comment

begin_class
DECL|class|FSDirTruncateOp
specifier|final
class|class
name|FSDirTruncateOp
block|{
comment|/**    * Private constructor for preventing FSDirTruncateOp object creation.    * Static-only class.    */
DECL|method|FSDirTruncateOp ()
specifier|private
name|FSDirTruncateOp
parameter_list|()
block|{}
comment|/**    * Truncate a file to a given size.    *    * @param fsn namespace    * @param srcArg path name    * @param newLength the target file size    * @param clientName client name    * @param clientMachine client machine info    * @param mtime modified time    * @param toRemoveBlocks to be removed blocks    * @param pc permission checker to check fs permission    * @return tuncate result    * @throws IOException    */
DECL|method|truncate (final FSNamesystem fsn, final String srcArg, final long newLength, final String clientName, final String clientMachine, final long mtime, final BlocksMapUpdateInfo toRemoveBlocks, final FSPermissionChecker pc)
specifier|static
name|TruncateResult
name|truncate
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|,
specifier|final
name|long
name|newLength
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|String
name|clientMachine
parameter_list|,
specifier|final
name|long
name|mtime
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|toRemoveBlocks
parameter_list|,
specifier|final
name|FSPermissionChecker
name|pc
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
assert|assert
name|fsn
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|srcArg
argument_list|)
decl_stmt|;
specifier|final
name|String
name|src
decl_stmt|;
specifier|final
name|INodesInPath
name|iip
decl_stmt|;
specifier|final
name|boolean
name|onBlockBoundary
decl_stmt|;
name|Block
name|truncateBlock
init|=
literal|null
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|srcArg
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
name|iip
operator|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|src
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsd
operator|.
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|,
name|FsAction
operator|.
name|WRITE
argument_list|)
expr_stmt|;
block|}
name|INodeFile
name|file
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|iip
operator|.
name|getLastINode
argument_list|()
argument_list|,
name|src
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|lpPolicy
init|=
name|fsd
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getStoragePolicy
argument_list|(
literal|"LAZY_PERSIST"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lpPolicy
operator|!=
literal|null
operator|&&
name|lpPolicy
operator|.
name|getId
argument_list|()
operator|==
name|file
operator|.
name|getStoragePolicyID
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot truncate lazy persist file "
operator|+
name|src
argument_list|)
throw|;
block|}
comment|// Check if the file is already being truncated with the same length
specifier|final
name|BlockInfo
name|last
init|=
name|file
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
operator|&&
name|last
operator|.
name|getBlockUCState
argument_list|()
operator|==
name|BlockUCState
operator|.
name|UNDER_RECOVERY
condition|)
block|{
specifier|final
name|Block
name|truncatedBlock
init|=
operator|(
operator|(
name|BlockInfoUnderConstruction
operator|)
name|last
operator|)
operator|.
name|getTruncateBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|truncatedBlock
operator|!=
literal|null
condition|)
block|{
specifier|final
name|long
name|truncateLength
init|=
name|file
operator|.
name|computeFileSize
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
operator|+
name|truncatedBlock
operator|.
name|getNumBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|newLength
operator|==
name|truncateLength
condition|)
block|{
return|return
operator|new
name|TruncateResult
argument_list|(
literal|false
argument_list|,
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|iip
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|// Opening an existing file for truncate. May need lease recovery.
name|fsn
operator|.
name|recoverLeaseInternal
argument_list|(
name|RecoverLeaseOp
operator|.
name|TRUNCATE_FILE
argument_list|,
name|iip
argument_list|,
name|src
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Truncate length check.
name|long
name|oldLength
init|=
name|file
operator|.
name|computeFileSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldLength
operator|==
name|newLength
condition|)
block|{
return|return
operator|new
name|TruncateResult
argument_list|(
literal|true
argument_list|,
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|iip
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|oldLength
operator|<
name|newLength
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Cannot truncate to a larger file size. Current size: "
operator|+
name|oldLength
operator|+
literal|", truncate size: "
operator|+
name|newLength
operator|+
literal|"."
argument_list|)
throw|;
block|}
comment|// Perform INodeFile truncation.
specifier|final
name|QuotaCounts
name|delta
init|=
operator|new
name|QuotaCounts
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|onBlockBoundary
operator|=
name|unprotectedTruncate
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|,
name|newLength
argument_list|,
name|toRemoveBlocks
argument_list|,
name|mtime
argument_list|,
name|delta
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|onBlockBoundary
condition|)
block|{
comment|// Open file for write, but don't log into edits
name|long
name|lastBlockDelta
init|=
name|file
operator|.
name|computeFileSize
argument_list|()
operator|-
name|newLength
decl_stmt|;
assert|assert
name|lastBlockDelta
operator|>
literal|0
operator|:
literal|"delta is 0 only if on block bounday"
assert|;
name|truncateBlock
operator|=
name|prepareFileForTruncate
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
name|lastBlockDelta
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// update the quota: use the preferred block size for UC block
name|fsd
operator|.
name|updateCountNoQuotaCheck
argument_list|(
name|iip
argument_list|,
name|iip
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logTruncate
argument_list|(
name|src
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
name|newLength
argument_list|,
name|mtime
argument_list|,
name|truncateBlock
argument_list|)
expr_stmt|;
return|return
operator|new
name|TruncateResult
argument_list|(
name|onBlockBoundary
argument_list|,
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|iip
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Unprotected truncate implementation. Unlike    * {@link FSDirTruncateOp#truncate}, this will not schedule block recovery.    *    * @param fsn namespace    * @param src path name    * @param clientName client name    * @param clientMachine client machine info    * @param newLength the target file size    * @param mtime modified time    * @param truncateBlock truncate block    * @throws IOException    */
DECL|method|unprotectedTruncate (final FSNamesystem fsn, final String src, final String clientName, final String clientMachine, final long newLength, final long mtime, final Block truncateBlock)
specifier|static
name|void
name|unprotectedTruncate
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|String
name|clientMachine
parameter_list|,
specifier|final
name|long
name|newLength
parameter_list|,
specifier|final
name|long
name|mtime
parameter_list|,
specifier|final
name|Block
name|truncateBlock
parameter_list|)
throws|throws
name|UnresolvedLinkException
throws|,
name|QuotaExceededException
throws|,
name|SnapshotAccessControlException
throws|,
name|IOException
block|{
assert|assert
name|fsn
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath
argument_list|(
name|src
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|INodeFile
name|file
init|=
name|iip
operator|.
name|getLastINode
argument_list|()
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|BlocksMapUpdateInfo
name|collectedBlocks
init|=
operator|new
name|BlocksMapUpdateInfo
argument_list|()
decl_stmt|;
name|boolean
name|onBlockBoundary
init|=
name|unprotectedTruncate
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|,
name|newLength
argument_list|,
name|collectedBlocks
argument_list|,
name|mtime
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|onBlockBoundary
condition|)
block|{
name|BlockInfo
name|oldBlock
init|=
name|file
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
name|Block
name|tBlk
init|=
name|prepareFileForTruncate
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
name|file
operator|.
name|computeFileSize
argument_list|()
operator|-
name|newLength
argument_list|,
name|truncateBlock
argument_list|)
decl_stmt|;
assert|assert
name|Block
operator|.
name|matchingIdAndGenStamp
argument_list|(
name|tBlk
argument_list|,
name|truncateBlock
argument_list|)
operator|&&
name|tBlk
operator|.
name|getNumBytes
argument_list|()
operator|==
name|truncateBlock
operator|.
name|getNumBytes
argument_list|()
operator|:
literal|"Should be the same block."
assert|;
if|if
condition|(
name|oldBlock
operator|.
name|getBlockId
argument_list|()
operator|!=
name|tBlk
operator|.
name|getBlockId
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|isBlockInLatestSnapshot
argument_list|(
name|oldBlock
argument_list|)
condition|)
block|{
name|fsd
operator|.
name|getBlockManager
argument_list|()
operator|.
name|removeBlockFromMap
argument_list|(
name|oldBlock
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|onBlockBoundary
operator|==
operator|(
name|truncateBlock
operator|==
literal|null
operator|)
operator|:
literal|"truncateBlock is null iff on block boundary: "
operator|+
name|truncateBlock
assert|;
name|fsn
operator|.
name|removeBlocksAndUpdateSafemodeTotal
argument_list|(
name|collectedBlocks
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert current INode to UnderConstruction. Recreate lease. Create new    * block for the truncated copy. Schedule truncation of the replicas.    *    * @param fsn namespace    * @param iip inodes in the path containing the file    * @param leaseHolder lease holder    * @param clientMachine client machine info    * @param lastBlockDelta last block delta size    * @param newBlock new block    * @return the returned block will be written to editLog and passed back    *         into this method upon loading.    * @throws IOException    */
annotation|@
name|VisibleForTesting
DECL|method|prepareFileForTruncate (FSNamesystem fsn, INodesInPath iip, String leaseHolder, String clientMachine, long lastBlockDelta, Block newBlock)
specifier|static
name|Block
name|prepareFileForTruncate
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|INodesInPath
name|iip
parameter_list|,
name|String
name|leaseHolder
parameter_list|,
name|String
name|clientMachine
parameter_list|,
name|long
name|lastBlockDelta
parameter_list|,
name|Block
name|newBlock
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsn
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|INodeFile
name|file
init|=
name|iip
operator|.
name|getLastINode
argument_list|()
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|file
operator|.
name|recordModification
argument_list|(
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|toUnderConstruction
argument_list|(
name|leaseHolder
argument_list|,
name|clientMachine
argument_list|)
expr_stmt|;
assert|assert
name|file
operator|.
name|isUnderConstruction
argument_list|()
operator|:
literal|"inode should be under construction."
assert|;
name|fsn
operator|.
name|getLeaseManager
argument_list|()
operator|.
name|addLease
argument_list|(
name|file
operator|.
name|getFileUnderConstructionFeature
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|file
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|shouldRecoverNow
init|=
operator|(
name|newBlock
operator|==
literal|null
operator|)
decl_stmt|;
name|BlockInfo
name|oldBlock
init|=
name|file
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
name|boolean
name|shouldCopyOnTruncate
init|=
name|shouldCopyOnTruncate
argument_list|(
name|fsn
argument_list|,
name|file
argument_list|,
name|oldBlock
argument_list|)
decl_stmt|;
if|if
condition|(
name|newBlock
operator|==
literal|null
condition|)
block|{
name|newBlock
operator|=
operator|(
name|shouldCopyOnTruncate
operator|)
condition|?
name|fsn
operator|.
name|createNewBlock
argument_list|()
else|:
operator|new
name|Block
argument_list|(
name|oldBlock
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|oldBlock
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|fsn
operator|.
name|nextGenerationStamp
argument_list|(
name|fsn
operator|.
name|getBlockIdManager
argument_list|()
operator|.
name|isLegacyBlock
argument_list|(
name|oldBlock
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BlockInfoUnderConstruction
name|truncatedBlockUC
decl_stmt|;
name|BlockManager
name|blockManager
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|shouldCopyOnTruncate
condition|)
block|{
comment|// Add new truncateBlock into blocksMap and
comment|// use oldBlock as a source for copy-on-truncate recovery
name|truncatedBlockUC
operator|=
operator|new
name|BlockInfoUnderConstructionContiguous
argument_list|(
name|newBlock
argument_list|,
name|file
operator|.
name|getPreferredBlockReplication
argument_list|()
argument_list|)
expr_stmt|;
name|truncatedBlockUC
operator|.
name|setNumBytes
argument_list|(
name|oldBlock
operator|.
name|getNumBytes
argument_list|()
operator|-
name|lastBlockDelta
argument_list|)
expr_stmt|;
name|truncatedBlockUC
operator|.
name|setTruncateBlock
argument_list|(
name|oldBlock
argument_list|)
expr_stmt|;
name|file
operator|.
name|setLastBlock
argument_list|(
name|truncatedBlockUC
argument_list|,
name|blockManager
operator|.
name|getStorages
argument_list|(
name|oldBlock
argument_list|)
argument_list|)
expr_stmt|;
name|blockManager
operator|.
name|addBlockCollection
argument_list|(
name|truncatedBlockUC
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"BLOCK* prepareFileForTruncate: Scheduling copy-on-truncate to new"
operator|+
literal|" size {}  new block {} old block {}"
argument_list|,
name|truncatedBlockUC
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|newBlock
argument_list|,
name|truncatedBlockUC
operator|.
name|getTruncateBlock
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Use new generation stamp for in-place truncate recovery
name|blockManager
operator|.
name|convertLastBlockToUnderConstruction
argument_list|(
name|file
argument_list|,
name|lastBlockDelta
argument_list|)
expr_stmt|;
name|oldBlock
operator|=
name|file
operator|.
name|getLastBlock
argument_list|()
expr_stmt|;
assert|assert
operator|!
name|oldBlock
operator|.
name|isComplete
argument_list|()
operator|:
literal|"oldBlock should be under construction"
assert|;
name|truncatedBlockUC
operator|=
operator|(
name|BlockInfoUnderConstruction
operator|)
name|oldBlock
expr_stmt|;
name|truncatedBlockUC
operator|.
name|setTruncateBlock
argument_list|(
operator|new
name|Block
argument_list|(
name|oldBlock
argument_list|)
argument_list|)
expr_stmt|;
name|truncatedBlockUC
operator|.
name|getTruncateBlock
argument_list|()
operator|.
name|setNumBytes
argument_list|(
name|oldBlock
operator|.
name|getNumBytes
argument_list|()
operator|-
name|lastBlockDelta
argument_list|)
expr_stmt|;
name|truncatedBlockUC
operator|.
name|getTruncateBlock
argument_list|()
operator|.
name|setGenerationStamp
argument_list|(
name|newBlock
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"BLOCK* prepareFileForTruncate: {} Scheduling in-place block "
operator|+
literal|"truncate to new size {}"
argument_list|,
name|truncatedBlockUC
operator|.
name|getTruncateBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|truncatedBlockUC
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shouldRecoverNow
condition|)
block|{
name|truncatedBlockUC
operator|.
name|initializeBlockRecovery
argument_list|(
name|newBlock
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|newBlock
return|;
block|}
comment|/**    * Truncate has the following properties:    * 1.) Any block deletions occur now.    * 2.) INode length is truncated now - new clients can only read up to    *     the truncated length.    * 3.) INode will be set to UC and lastBlock set to UNDER_RECOVERY.    * 4.) NN will trigger DN truncation recovery and waits for DNs to report.    * 5.) File is considered UNDER_RECOVERY until truncation recovery    *     completes.    * 6.) Soft and hard Lease expiration require truncation recovery to    *     complete.    *    * @return true if on the block boundary or false if recovery is need    */
DECL|method|unprotectedTruncate (FSNamesystem fsn, INodesInPath iip, long newLength, BlocksMapUpdateInfo collectedBlocks, long mtime, QuotaCounts delta)
specifier|private
specifier|static
name|boolean
name|unprotectedTruncate
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|INodesInPath
name|iip
parameter_list|,
name|long
name|newLength
parameter_list|,
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
name|long
name|mtime
parameter_list|,
name|QuotaCounts
name|delta
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsn
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|INodeFile
name|file
init|=
name|iip
operator|.
name|getLastINode
argument_list|()
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|int
name|latestSnapshot
init|=
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
decl_stmt|;
name|file
operator|.
name|recordModification
argument_list|(
name|latestSnapshot
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verifyQuotaForTruncate
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|,
name|file
argument_list|,
name|newLength
argument_list|,
name|delta
argument_list|)
expr_stmt|;
name|long
name|remainingLength
init|=
name|file
operator|.
name|collectBlocksBeyondMax
argument_list|(
name|newLength
argument_list|,
name|collectedBlocks
argument_list|)
decl_stmt|;
name|file
operator|.
name|excludeSnapshotBlocks
argument_list|(
name|latestSnapshot
argument_list|,
name|collectedBlocks
argument_list|)
expr_stmt|;
name|file
operator|.
name|setModificationTime
argument_list|(
name|mtime
argument_list|)
expr_stmt|;
comment|// return whether on a block boundary
return|return
operator|(
name|remainingLength
operator|-
name|newLength
operator|)
operator|==
literal|0
return|;
block|}
DECL|method|verifyQuotaForTruncate (FSNamesystem fsn, INodesInPath iip, INodeFile file, long newLength, QuotaCounts delta)
specifier|private
specifier|static
name|void
name|verifyQuotaForTruncate
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|INodesInPath
name|iip
parameter_list|,
name|INodeFile
name|file
parameter_list|,
name|long
name|newLength
parameter_list|,
name|QuotaCounts
name|delta
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fsn
operator|.
name|isImageLoaded
argument_list|()
operator|||
name|fsd
operator|.
name|shouldSkipQuotaChecks
argument_list|()
condition|)
block|{
comment|// Do not check quota if edit log is still being processed
return|return;
block|}
specifier|final
name|BlockStoragePolicy
name|policy
init|=
name|fsd
operator|.
name|getBlockStoragePolicySuite
argument_list|()
operator|.
name|getPolicy
argument_list|(
name|file
operator|.
name|getStoragePolicyID
argument_list|()
argument_list|)
decl_stmt|;
name|file
operator|.
name|computeQuotaDeltaForTruncate
argument_list|(
name|newLength
argument_list|,
name|policy
argument_list|,
name|delta
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|FSDirectory
operator|.
name|verifyQuota
argument_list|(
name|iip
argument_list|,
name|iip
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
name|delta
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Defines if a replica needs to be copied on truncate or    * can be truncated in place.    */
DECL|method|shouldCopyOnTruncate (FSNamesystem fsn, INodeFile file, BlockInfo blk)
specifier|private
specifier|static
name|boolean
name|shouldCopyOnTruncate
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|INodeFile
name|file
parameter_list|,
name|BlockInfo
name|blk
parameter_list|)
block|{
if|if
condition|(
operator|!
name|fsn
operator|.
name|isUpgradeFinalized
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|fsn
operator|.
name|isRollingUpgrade
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|file
operator|.
name|isBlockInLatestSnapshot
argument_list|(
name|blk
argument_list|)
return|;
block|}
comment|/**    * Result of truncate operation.    */
DECL|class|TruncateResult
specifier|static
class|class
name|TruncateResult
block|{
DECL|field|result
specifier|private
specifier|final
name|boolean
name|result
decl_stmt|;
DECL|field|stat
specifier|private
specifier|final
name|HdfsFileStatus
name|stat
decl_stmt|;
DECL|method|TruncateResult (boolean result, HdfsFileStatus stat)
specifier|public
name|TruncateResult
parameter_list|(
name|boolean
name|result
parameter_list|,
name|HdfsFileStatus
name|stat
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|stat
operator|=
name|stat
expr_stmt|;
block|}
comment|/**      * @return true if client does not need to wait for block recovery,      *          false if client needs to wait for block recovery.      */
DECL|method|getResult ()
name|boolean
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
comment|/**      * @return file information.      */
DECL|method|getFileStatus ()
name|HdfsFileStatus
name|getFileStatus
parameter_list|()
block|{
return|return
name|stat
return|;
block|}
block|}
block|}
end_class

end_unit

