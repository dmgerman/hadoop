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
name|fs
operator|.
name|CreateFlag
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
name|FileAlreadyExistsException
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
name|StorageType
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
name|DatanodeInfo
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
name|LastBlockWithStatus
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
name|FSDirectory
operator|.
name|DirOp
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
name|NameNodeLayoutVersion
operator|.
name|Feature
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
name|ipc
operator|.
name|RetriableException
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
comment|/**  * Helper class to perform append operation.  */
end_comment

begin_class
DECL|class|FSDirAppendOp
specifier|final
class|class
name|FSDirAppendOp
block|{
comment|/**    * Private constructor for preventing FSDirAppendOp object creation.    * Static-only class.    */
DECL|method|FSDirAppendOp ()
specifier|private
name|FSDirAppendOp
parameter_list|()
block|{}
comment|/**    * Append to an existing file.    *<p>    *    * The method returns the last block of the file if this is a partial block,    * which can still be used for writing more data. The client uses the    * returned block locations to form the data pipeline for this block.<br>    * The {@link LocatedBlock} will be null if the last block is full.    * The client then allocates a new block with the next call using    * {@link org.apache.hadoop.hdfs.protocol.ClientProtocol#addBlock}.    *<p>    *    * For description of parameters and exceptions thrown see    * {@link org.apache.hadoop.hdfs.protocol.ClientProtocol#append}    *    * @param fsn namespace    * @param srcArg path name    * @param pc permission checker to check fs permission    * @param holder client name    * @param clientMachine client machine info    * @param newBlock if the data is appended to a new block    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding    * @return the last block with status    */
DECL|method|appendFile (final FSNamesystem fsn, final String srcArg, final FSPermissionChecker pc, final String holder, final String clientMachine, final boolean newBlock, final boolean logRetryCache)
specifier|static
name|LastBlockWithStatus
name|appendFile
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
name|FSPermissionChecker
name|pc
parameter_list|,
specifier|final
name|String
name|holder
parameter_list|,
specifier|final
name|String
name|clientMachine
parameter_list|,
specifier|final
name|boolean
name|newBlock
parameter_list|,
specifier|final
name|boolean
name|logRetryCache
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
specifier|final
name|LocatedBlock
name|lb
decl_stmt|;
specifier|final
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
specifier|final
name|INodesInPath
name|iip
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|iip
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|srcArg
argument_list|,
name|DirOp
operator|.
name|WRITE
argument_list|)
expr_stmt|;
comment|// Verify that the destination does not exist as a directory already
specifier|final
name|INode
name|inode
init|=
name|iip
operator|.
name|getLastINode
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|iip
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|inode
operator|!=
literal|null
operator|&&
name|inode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileAlreadyExistsException
argument_list|(
literal|"Cannot append to directory "
operator|+
name|path
operator|+
literal|"; already exists as a directory."
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Failed to append to non-existent file "
operator|+
name|path
operator|+
literal|" for client "
operator|+
name|clientMachine
argument_list|)
throw|;
block|}
specifier|final
name|INodeFile
name|file
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|inode
argument_list|,
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isStriped
argument_list|()
operator|&&
operator|!
name|newBlock
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Append on EC file without new block is not supported. Use "
operator|+
name|CreateFlag
operator|.
name|NEW_BLOCK
operator|+
literal|" create flag while appending file."
argument_list|)
throw|;
block|}
name|BlockManager
name|blockManager
init|=
name|fsd
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|lpPolicy
init|=
name|blockManager
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
literal|"Cannot append to lazy persist file "
operator|+
name|path
argument_list|)
throw|;
block|}
comment|// Opening an existing file for append - may need to recover lease.
name|fsn
operator|.
name|recoverLeaseInternal
argument_list|(
name|RecoverLeaseOp
operator|.
name|APPEND_FILE
argument_list|,
name|iip
argument_list|,
name|path
argument_list|,
name|holder
argument_list|,
name|clientMachine
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|BlockInfo
name|lastBlock
init|=
name|file
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
comment|// Check that the block has at least minimum replication.
if|if
condition|(
name|lastBlock
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|lastBlock
operator|.
name|getBlockUCState
argument_list|()
operator|==
name|BlockUCState
operator|.
name|COMMITTED
condition|)
block|{
throw|throw
operator|new
name|RetriableException
argument_list|(
operator|new
name|NotReplicatedYetException
argument_list|(
literal|"append: lastBlock="
operator|+
name|lastBlock
operator|+
literal|" of src="
operator|+
name|path
operator|+
literal|" is COMMITTED but not yet COMPLETE."
argument_list|)
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|lastBlock
operator|.
name|isComplete
argument_list|()
operator|&&
operator|!
name|blockManager
operator|.
name|isSufficientlyReplicated
argument_list|(
name|lastBlock
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"append: lastBlock="
operator|+
name|lastBlock
operator|+
literal|" of src="
operator|+
name|path
operator|+
literal|" is not sufficiently replicated yet."
argument_list|)
throw|;
block|}
block|}
name|lb
operator|=
name|prepareFileForAppend
argument_list|(
name|fsn
argument_list|,
name|iip
argument_list|,
name|holder
argument_list|,
name|clientMachine
argument_list|,
name|newBlock
argument_list|,
literal|true
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|warn
argument_list|(
literal|"DIR* NameSystem.append: "
operator|+
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ie
throw|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|HdfsFileStatus
name|stat
init|=
name|FSDirStatAndListingOp
operator|.
name|getFileInfo
argument_list|(
name|fsd
argument_list|,
name|iip
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|lb
operator|!=
literal|null
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"DIR* NameSystem.appendFile: file {} for {} at {} block {} block"
operator|+
literal|" size {}"
argument_list|,
name|srcArg
argument_list|,
name|holder
argument_list|,
name|clientMachine
argument_list|,
name|lb
operator|.
name|getBlock
argument_list|()
argument_list|,
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LastBlockWithStatus
argument_list|(
name|lb
argument_list|,
name|stat
argument_list|)
return|;
block|}
comment|/**    * Convert current node to under construction.    * Recreate in-memory lease record.    *    * @param fsn namespace    * @param iip inodes in the path containing the file    * @param leaseHolder identifier of the lease holder on this file    * @param clientMachine identifier of the client machine    * @param newBlock if the data is appended to a new block    * @param writeToEditLog whether to persist this change to the edit log    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding    * @return the last block locations if the block is partial or null otherwise    * @throws IOException    */
DECL|method|prepareFileForAppend (final FSNamesystem fsn, final INodesInPath iip, final String leaseHolder, final String clientMachine, final boolean newBlock, final boolean writeToEditLog, final boolean logRetryCache)
specifier|static
name|LocatedBlock
name|prepareFileForAppend
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|,
specifier|final
name|INodesInPath
name|iip
parameter_list|,
specifier|final
name|String
name|leaseHolder
parameter_list|,
specifier|final
name|String
name|clientMachine
parameter_list|,
specifier|final
name|boolean
name|newBlock
parameter_list|,
specifier|final
name|boolean
name|writeToEditLog
parameter_list|,
specifier|final
name|boolean
name|logRetryCache
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
specifier|final
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
specifier|final
name|QuotaCounts
name|delta
init|=
name|verifyQuotaForUCBlock
argument_list|(
name|fsn
argument_list|,
name|file
argument_list|,
name|iip
argument_list|)
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
name|LocatedBlock
name|ret
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|newBlock
condition|)
block|{
name|FSDirectory
name|fsd
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|ret
operator|=
name|fsd
operator|.
name|getBlockManager
argument_list|()
operator|.
name|convertLastBlockToUnderConstruction
argument_list|(
name|file
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|null
operator|&&
name|delta
operator|!=
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|delta
operator|.
name|getStorageSpace
argument_list|()
operator|>=
literal|0
argument_list|,
literal|"appending to"
operator|+
literal|" a block with size larger than the preferred block size"
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
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
block|}
block|}
else|else
block|{
name|BlockInfo
name|lastBlock
init|=
name|file
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastBlock
operator|!=
literal|null
condition|)
block|{
name|ExtendedBlock
name|blk
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|fsn
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|lastBlock
argument_list|)
decl_stmt|;
name|ret
operator|=
operator|new
name|LocatedBlock
argument_list|(
name|blk
argument_list|,
operator|new
name|DatanodeInfo
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|writeToEditLog
condition|)
block|{
specifier|final
name|String
name|path
init|=
name|iip
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|NameNodeLayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|APPEND_NEW_BLOCK
argument_list|,
name|fsn
operator|.
name|getEffectiveLayoutVersion
argument_list|()
argument_list|)
condition|)
block|{
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logAppendFile
argument_list|(
name|path
argument_list|,
name|file
argument_list|,
name|newBlock
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fsn
operator|.
name|getEditLog
argument_list|()
operator|.
name|logOpenFile
argument_list|(
name|path
argument_list|,
name|file
argument_list|,
literal|false
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Verify quota when using the preferred block size for UC block. This is    * usually used by append and truncate.    *    * @throws QuotaExceededException when violating the storage quota    * @return expected quota usage update. null means no change or no need to    *         update quota usage later    */
DECL|method|verifyQuotaForUCBlock (FSNamesystem fsn, INodeFile file, INodesInPath iip)
specifier|private
specifier|static
name|QuotaCounts
name|verifyQuotaForUCBlock
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|INodeFile
name|file
parameter_list|,
name|INodesInPath
name|iip
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
comment|// Do not check quota if editlog is still being processed
return|return
literal|null
return|;
block|}
if|if
condition|(
name|file
operator|.
name|getLastBlock
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|QuotaCounts
name|delta
init|=
name|computeQuotaDeltaForUCBlock
argument_list|(
name|fsn
argument_list|,
name|file
argument_list|)
decl_stmt|;
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
return|return
name|delta
return|;
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
return|return
literal|null
return|;
block|}
comment|/** Compute quota change for converting a complete block to a UC block. */
DECL|method|computeQuotaDeltaForUCBlock (FSNamesystem fsn, INodeFile file)
specifier|private
specifier|static
name|QuotaCounts
name|computeQuotaDeltaForUCBlock
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|INodeFile
name|file
parameter_list|)
block|{
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
specifier|final
name|BlockInfo
name|lastBlock
init|=
name|file
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastBlock
operator|!=
literal|null
condition|)
block|{
specifier|final
name|long
name|diff
init|=
name|file
operator|.
name|getPreferredBlockSize
argument_list|()
operator|-
name|lastBlock
operator|.
name|getNumBytes
argument_list|()
decl_stmt|;
specifier|final
name|short
name|repl
init|=
name|lastBlock
operator|.
name|getReplication
argument_list|()
decl_stmt|;
name|delta
operator|.
name|addStorageSpace
argument_list|(
name|diff
operator|*
name|repl
argument_list|)
expr_stmt|;
specifier|final
name|BlockStoragePolicy
name|policy
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
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
name|List
argument_list|<
name|StorageType
argument_list|>
name|types
init|=
name|policy
operator|.
name|chooseStorageTypes
argument_list|(
name|repl
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageType
name|t
range|:
name|types
control|)
block|{
if|if
condition|(
name|t
operator|.
name|supportTypeQuota
argument_list|()
condition|)
block|{
name|delta
operator|.
name|addTypeSpace
argument_list|(
name|t
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|delta
return|;
block|}
block|}
end_class

end_unit

