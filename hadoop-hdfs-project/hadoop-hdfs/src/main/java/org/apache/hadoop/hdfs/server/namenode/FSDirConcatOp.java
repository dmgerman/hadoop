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
name|SnapshotException
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
operator|.
name|now
import|;
end_import

begin_comment
comment|/**  * Restrictions for a concat operation:  *<pre>  * 1. the src file and the target file are in the same dir  * 2. all the source files are not in snapshot  * 3. any source file cannot be the same with the target file  * 4. source files cannot be under construction or empty  * 5. source file's preferred block size cannot be greater than the target file  *</pre>  */
end_comment

begin_class
DECL|class|FSDirConcatOp
class|class
name|FSDirConcatOp
block|{
DECL|method|concat (FSDirectory fsd, String target, String[] srcs, boolean logRetryCache)
specifier|static
name|HdfsFileStatus
name|concat
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|String
name|target
parameter_list|,
name|String
index|[]
name|srcs
parameter_list|,
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|target
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Target file name is empty"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|srcs
operator|!=
literal|null
operator|&&
name|srcs
operator|.
name|length
operator|>
literal|0
argument_list|,
literal|"No sources given"
argument_list|)
expr_stmt|;
assert|assert
name|srcs
operator|!=
literal|null
assert|;
if|if
condition|(
name|FSDirectory
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|FSDirectory
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"concat {} to {}"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|srcs
argument_list|)
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
specifier|final
name|INodesInPath
name|targetIIP
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|target
argument_list|)
decl_stmt|;
comment|// write permission for the target
name|FSPermissionChecker
name|pc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|pc
operator|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
expr_stmt|;
name|fsd
operator|.
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|targetIIP
argument_list|,
name|FsAction
operator|.
name|WRITE
argument_list|)
expr_stmt|;
block|}
comment|// check the target
name|verifyTargetFile
argument_list|(
name|fsd
argument_list|,
name|target
argument_list|,
name|targetIIP
argument_list|)
expr_stmt|;
comment|// check the srcs
name|INodeFile
index|[]
name|srcFiles
init|=
name|verifySrcFiles
argument_list|(
name|fsd
argument_list|,
name|srcs
argument_list|,
name|targetIIP
argument_list|,
name|pc
argument_list|)
decl_stmt|;
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"DIR* NameSystem.concat: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|srcs
argument_list|)
operator|+
literal|" to "
operator|+
name|target
argument_list|)
expr_stmt|;
block|}
name|long
name|timestamp
init|=
name|now
argument_list|()
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|unprotectedConcat
argument_list|(
name|fsd
argument_list|,
name|targetIIP
argument_list|,
name|srcFiles
argument_list|,
name|timestamp
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
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logConcat
argument_list|(
name|target
argument_list|,
name|srcs
argument_list|,
name|timestamp
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|targetIIP
argument_list|)
return|;
block|}
DECL|method|verifyTargetFile (FSDirectory fsd, final String target, final INodesInPath targetIIP)
specifier|private
specifier|static
name|void
name|verifyTargetFile
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|target
parameter_list|,
specifier|final
name|INodesInPath
name|targetIIP
parameter_list|)
throws|throws
name|IOException
block|{
comment|// check the target
if|if
condition|(
name|fsd
operator|.
name|getEZForPath
argument_list|(
name|targetIIP
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat can not be called for files in an encryption zone."
argument_list|)
throw|;
block|}
specifier|final
name|INodeFile
name|targetINode
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|targetIIP
operator|.
name|getLastINode
argument_list|()
argument_list|,
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetINode
operator|.
name|isUnderConstruction
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: target file "
operator|+
name|target
operator|+
literal|" is under construction"
argument_list|)
throw|;
block|}
block|}
DECL|method|verifySrcFiles (FSDirectory fsd, String[] srcs, INodesInPath targetIIP, FSPermissionChecker pc)
specifier|private
specifier|static
name|INodeFile
index|[]
name|verifySrcFiles
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|String
index|[]
name|srcs
parameter_list|,
name|INodesInPath
name|targetIIP
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// to make sure no two files are the same
name|Set
argument_list|<
name|INodeFile
argument_list|>
name|si
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|INodeFile
name|targetINode
init|=
name|targetIIP
operator|.
name|getLastINode
argument_list|()
operator|.
name|asFile
argument_list|()
decl_stmt|;
specifier|final
name|INodeDirectory
name|targetParent
init|=
name|targetINode
operator|.
name|getParent
argument_list|()
decl_stmt|;
comment|// now check the srcs
for|for
control|(
name|String
name|src
range|:
name|srcs
control|)
block|{
specifier|final
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|src
argument_list|)
decl_stmt|;
comment|// permission check for srcs
if|if
condition|(
name|pc
operator|!=
literal|null
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
name|READ
argument_list|)
expr_stmt|;
comment|// read the file
name|fsd
operator|.
name|checkParentAccess
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
comment|// for delete
block|}
specifier|final
name|INode
name|srcINode
init|=
name|iip
operator|.
name|getLastINode
argument_list|()
decl_stmt|;
specifier|final
name|INodeFile
name|srcINodeFile
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|srcINode
argument_list|,
name|src
argument_list|)
decl_stmt|;
comment|// make sure the src file and the target file are in the same dir
if|if
condition|(
name|srcINodeFile
operator|.
name|getParent
argument_list|()
operator|!=
name|targetParent
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Source file "
operator|+
name|src
operator|+
literal|" is not in the same directory with the target "
operator|+
name|targetIIP
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
comment|// make sure all the source files are not in snapshot
if|if
condition|(
name|srcINode
operator|.
name|isInLatestSnapshot
argument_list|(
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"Concat: the source file "
operator|+
name|src
operator|+
literal|" is in snapshot"
argument_list|)
throw|;
block|}
comment|// check if the file has other references.
if|if
condition|(
name|srcINode
operator|.
name|isReference
argument_list|()
operator|&&
operator|(
operator|(
name|INodeReference
operator|.
name|WithCount
operator|)
name|srcINode
operator|.
name|asReference
argument_list|()
operator|.
name|getReferredINode
argument_list|()
operator|)
operator|.
name|getReferenceCount
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"Concat: the source file "
operator|+
name|src
operator|+
literal|" is referred by some other reference in some snapshot."
argument_list|)
throw|;
block|}
comment|// source file cannot be the same with the target file
if|if
condition|(
name|srcINode
operator|==
name|targetINode
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: the src file "
operator|+
name|src
operator|+
literal|" is the same with the target file "
operator|+
name|targetIIP
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
comment|// source file cannot be under construction or empty
if|if
condition|(
name|srcINodeFile
operator|.
name|isUnderConstruction
argument_list|()
operator|||
name|srcINodeFile
operator|.
name|numBlocks
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: source file "
operator|+
name|src
operator|+
literal|" is invalid or empty or underConstruction"
argument_list|)
throw|;
block|}
comment|// source file's preferred block size cannot be greater than the target
comment|// file
if|if
condition|(
name|srcINodeFile
operator|.
name|getPreferredBlockSize
argument_list|()
operator|>
name|targetINode
operator|.
name|getPreferredBlockSize
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: source file "
operator|+
name|src
operator|+
literal|" has preferred block size "
operator|+
name|srcINodeFile
operator|.
name|getPreferredBlockSize
argument_list|()
operator|+
literal|" which is greater than the target file's preferred block size "
operator|+
name|targetINode
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
throw|;
block|}
comment|// TODO currently we do not support concatenating EC files
if|if
condition|(
name|srcINodeFile
operator|.
name|isStriped
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: the src file "
operator|+
name|src
operator|+
literal|" is with striped blocks"
argument_list|)
throw|;
block|}
name|si
operator|.
name|add
argument_list|(
name|srcINodeFile
argument_list|)
expr_stmt|;
block|}
comment|// make sure no two files are the same
if|if
condition|(
name|si
operator|.
name|size
argument_list|()
operator|<
name|srcs
operator|.
name|length
condition|)
block|{
comment|// it means at least two files are the same
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: at least two of the source files are the same"
argument_list|)
throw|;
block|}
return|return
name|si
operator|.
name|toArray
argument_list|(
operator|new
name|INodeFile
index|[
name|si
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|computeQuotaDeltas (FSDirectory fsd, INodeFile target, INodeFile[] srcList)
specifier|private
specifier|static
name|QuotaCounts
name|computeQuotaDeltas
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|INodeFile
name|target
parameter_list|,
name|INodeFile
index|[]
name|srcList
parameter_list|)
block|{
name|QuotaCounts
name|deltas
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
name|short
name|targetRepl
init|=
name|target
operator|.
name|getPreferredBlockReplication
argument_list|()
decl_stmt|;
for|for
control|(
name|INodeFile
name|src
range|:
name|srcList
control|)
block|{
name|short
name|srcRepl
init|=
name|src
operator|.
name|getFileReplication
argument_list|()
decl_stmt|;
name|long
name|fileSize
init|=
name|src
operator|.
name|computeFileSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetRepl
operator|!=
name|srcRepl
condition|)
block|{
name|deltas
operator|.
name|addStorageSpace
argument_list|(
name|fileSize
operator|*
operator|(
name|targetRepl
operator|-
name|srcRepl
operator|)
argument_list|)
expr_stmt|;
name|BlockStoragePolicy
name|bsp
init|=
name|fsd
operator|.
name|getBlockStoragePolicySuite
argument_list|()
operator|.
name|getPolicy
argument_list|(
name|src
operator|.
name|getStoragePolicyID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bsp
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|StorageType
argument_list|>
name|srcTypeChosen
init|=
name|bsp
operator|.
name|chooseStorageTypes
argument_list|(
name|srcRepl
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageType
name|t
range|:
name|srcTypeChosen
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
name|deltas
operator|.
name|addTypeSpace
argument_list|(
name|t
argument_list|,
operator|-
name|fileSize
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|StorageType
argument_list|>
name|targetTypeChosen
init|=
name|bsp
operator|.
name|chooseStorageTypes
argument_list|(
name|targetRepl
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageType
name|t
range|:
name|targetTypeChosen
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
name|deltas
operator|.
name|addTypeSpace
argument_list|(
name|t
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|deltas
return|;
block|}
DECL|method|verifyQuota (FSDirectory fsd, INodesInPath targetIIP, QuotaCounts deltas)
specifier|private
specifier|static
name|void
name|verifyQuota
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|INodesInPath
name|targetIIP
parameter_list|,
name|QuotaCounts
name|deltas
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
if|if
condition|(
operator|!
name|fsd
operator|.
name|getFSNamesystem
argument_list|()
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
return|return;
block|}
name|FSDirectory
operator|.
name|verifyQuota
argument_list|(
name|targetIIP
argument_list|,
name|targetIIP
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
name|deltas
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Concat all the blocks from srcs to trg and delete the srcs files    * @param fsd FSDirectory    */
DECL|method|unprotectedConcat (FSDirectory fsd, INodesInPath targetIIP, INodeFile[] srcList, long timestamp)
specifier|static
name|void
name|unprotectedConcat
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|INodesInPath
name|targetIIP
parameter_list|,
name|INodeFile
index|[]
name|srcList
parameter_list|,
name|long
name|timestamp
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|fsd
operator|.
name|hasWriteLock
argument_list|()
assert|;
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|debug
argument_list|(
literal|"DIR* FSNamesystem.concat to "
operator|+
name|targetIIP
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|INodeFile
name|trgInode
init|=
name|targetIIP
operator|.
name|getLastINode
argument_list|()
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|QuotaCounts
name|deltas
init|=
name|computeQuotaDeltas
argument_list|(
name|fsd
argument_list|,
name|trgInode
argument_list|,
name|srcList
argument_list|)
decl_stmt|;
name|verifyQuota
argument_list|(
name|fsd
argument_list|,
name|targetIIP
argument_list|,
name|deltas
argument_list|)
expr_stmt|;
comment|// the target file can be included in a snapshot
name|trgInode
operator|.
name|recordModification
argument_list|(
name|targetIIP
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
name|INodeDirectory
name|trgParent
init|=
name|targetIIP
operator|.
name|getINode
argument_list|(
operator|-
literal|2
argument_list|)
operator|.
name|asDirectory
argument_list|()
decl_stmt|;
name|trgInode
operator|.
name|concatBlocks
argument_list|(
name|srcList
argument_list|,
name|fsd
operator|.
name|getBlockManager
argument_list|()
argument_list|)
expr_stmt|;
comment|// since we are in the same dir - we can use same parent to remove files
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|INodeFile
name|nodeToRemove
range|:
name|srcList
control|)
block|{
if|if
condition|(
name|nodeToRemove
operator|!=
literal|null
condition|)
block|{
name|nodeToRemove
operator|.
name|clearBlocks
argument_list|()
expr_stmt|;
name|nodeToRemove
operator|.
name|getParent
argument_list|()
operator|.
name|removeChild
argument_list|(
name|nodeToRemove
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|getINodeMap
argument_list|()
operator|.
name|remove
argument_list|(
name|nodeToRemove
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
name|trgInode
operator|.
name|setModificationTime
argument_list|(
name|timestamp
argument_list|,
name|targetIIP
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
name|trgParent
operator|.
name|updateModificationTime
argument_list|(
name|timestamp
argument_list|,
name|targetIIP
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
comment|// update quota on the parent directory with deltas
name|FSDirectory
operator|.
name|unprotectedUpdateCount
argument_list|(
name|targetIIP
argument_list|,
name|targetIIP
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
name|deltas
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

