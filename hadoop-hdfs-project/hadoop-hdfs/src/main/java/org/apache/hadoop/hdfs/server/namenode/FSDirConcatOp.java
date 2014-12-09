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
name|SnapshotException
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
name|HashSet
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

begin_class
DECL|class|FSDirConcatOp
class|class
name|FSDirConcatOp
block|{
DECL|method|concat ( FSDirectory fsd, String target, String[] srcs, boolean logRetryCache)
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
comment|// We require all files be in the same directory
name|String
name|trgParent
init|=
name|target
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|target
operator|.
name|lastIndexOf
argument_list|(
name|Path
operator|.
name|SEPARATOR_CHAR
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|srcs
control|)
block|{
name|String
name|srcParent
init|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|lastIndexOf
argument_list|(
name|Path
operator|.
name|SEPARATOR_CHAR
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|srcParent
operator|.
name|equals
argument_list|(
name|trgParent
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Sources and target are not in the same directory"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|INodesInPath
name|trgIip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|target
argument_list|)
decl_stmt|;
comment|// write permission for the target
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|FSPermissionChecker
name|pc
init|=
name|fsd
operator|.
name|getPermissionChecker
argument_list|()
decl_stmt|;
name|fsd
operator|.
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|trgIip
argument_list|,
name|FsAction
operator|.
name|WRITE
argument_list|)
expr_stmt|;
comment|// and srcs
for|for
control|(
name|String
name|aSrc
range|:
name|srcs
control|)
block|{
specifier|final
name|INodesInPath
name|srcIip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|aSrc
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|srcIip
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
name|srcIip
argument_list|,
name|FsAction
operator|.
name|WRITE
argument_list|)
expr_stmt|;
comment|// for delete
block|}
block|}
comment|// to make sure no two files are the same
name|Set
argument_list|<
name|INode
argument_list|>
name|si
init|=
operator|new
name|HashSet
argument_list|<
name|INode
argument_list|>
argument_list|()
decl_stmt|;
comment|// we put the following prerequisite for the operation
comment|// replication and blocks sizes should be the same for ALL the blocks
comment|// check the target
if|if
condition|(
name|fsd
operator|.
name|getEZForPath
argument_list|(
name|trgIip
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
name|trgInode
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|trgIip
operator|.
name|getLastINode
argument_list|()
argument_list|,
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|trgInode
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
comment|// per design target shouldn't be empty and all the blocks same size
if|if
condition|(
name|trgInode
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
literal|"concat: target file "
operator|+
name|target
operator|+
literal|" is empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|trgInode
operator|.
name|isWithSnapshot
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
literal|" is in a snapshot"
argument_list|)
throw|;
block|}
name|long
name|blockSize
init|=
name|trgInode
operator|.
name|getPreferredBlockSize
argument_list|()
decl_stmt|;
comment|// check the end block to be full
specifier|final
name|BlockInfo
name|last
init|=
name|trgInode
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|blockSize
operator|!=
name|last
operator|.
name|getNumBytes
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"The last block in "
operator|+
name|target
operator|+
literal|" is not full; last block size = "
operator|+
name|last
operator|.
name|getNumBytes
argument_list|()
operator|+
literal|" but file block size = "
operator|+
name|blockSize
argument_list|)
throw|;
block|}
name|si
operator|.
name|add
argument_list|(
name|trgInode
argument_list|)
expr_stmt|;
specifier|final
name|short
name|repl
init|=
name|trgInode
operator|.
name|getFileReplication
argument_list|()
decl_stmt|;
comment|// now check the srcs
name|boolean
name|endSrc
init|=
literal|false
decl_stmt|;
comment|// final src file doesn't have to have full end block
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|srcs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|src
init|=
name|srcs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|srcs
operator|.
name|length
operator|-
literal|1
condition|)
name|endSrc
operator|=
literal|true
expr_stmt|;
specifier|final
name|INodeFile
name|srcInode
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|fsd
operator|.
name|getINode4Write
argument_list|(
name|src
argument_list|)
argument_list|,
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|.
name|isEmpty
argument_list|()
operator|||
name|srcInode
operator|.
name|isUnderConstruction
argument_list|()
operator|||
name|srcInode
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
comment|// check replication and blocks size
if|if
condition|(
name|repl
operator|!=
name|srcInode
operator|.
name|getBlockReplication
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: the source file "
operator|+
name|src
operator|+
literal|" and the target file "
operator|+
name|target
operator|+
literal|" should have the same replication: source replication is "
operator|+
name|srcInode
operator|.
name|getBlockReplication
argument_list|()
operator|+
literal|" but target replication is "
operator|+
name|repl
argument_list|)
throw|;
block|}
comment|//boolean endBlock=false;
comment|// verify that all the blocks are of the same length as target
comment|// should be enough to check the end blocks
specifier|final
name|BlockInfo
index|[]
name|srcBlocks
init|=
name|srcInode
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
name|srcBlocks
operator|.
name|length
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|endSrc
condition|)
name|idx
operator|=
name|srcBlocks
operator|.
name|length
operator|-
literal|2
expr_stmt|;
comment|// end block of endSrc is OK not to be full
if|if
condition|(
name|idx
operator|>=
literal|0
operator|&&
name|srcBlocks
index|[
name|idx
index|]
operator|.
name|getNumBytes
argument_list|()
operator|!=
name|blockSize
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: the source file "
operator|+
name|src
operator|+
literal|" and the target file "
operator|+
name|target
operator|+
literal|" should have the same blocks sizes: target block size is "
operator|+
name|blockSize
operator|+
literal|" but the size of source block "
operator|+
name|idx
operator|+
literal|" is "
operator|+
name|srcBlocks
index|[
name|idx
index|]
operator|.
name|getNumBytes
argument_list|()
argument_list|)
throw|;
block|}
name|si
operator|.
name|add
argument_list|(
name|srcInode
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
operator|+
literal|1
condition|)
block|{
comment|// trg + srcs
comment|// it means at least two files are the same
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"concat: at least two of the source files are the same"
argument_list|)
throw|;
block|}
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
name|target
argument_list|,
name|srcs
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
name|target
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Concat all the blocks from srcs to trg and delete the srcs files    * @param fsd FSDirectory    * @param target target file to move the blocks to    * @param srcs list of file to move the blocks from    */
DECL|method|unprotectedConcat ( FSDirectory fsd, String target, String[] srcs, long timestamp)
specifier|static
name|void
name|unprotectedConcat
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
name|target
argument_list|)
expr_stmt|;
block|}
comment|// do the move
specifier|final
name|INodesInPath
name|trgIIP
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|target
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|INodeFile
name|trgInode
init|=
name|trgIIP
operator|.
name|getLastINode
argument_list|()
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|INodeDirectory
name|trgParent
init|=
name|trgIIP
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
specifier|final
name|int
name|trgLatestSnapshot
init|=
name|trgIIP
operator|.
name|getLatestSnapshotId
argument_list|()
decl_stmt|;
specifier|final
name|INodeFile
index|[]
name|allSrcInodes
init|=
operator|new
name|INodeFile
index|[
name|srcs
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|srcs
operator|.
name|length
condition|;
name|i
operator|++
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
name|srcs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|latest
init|=
name|iip
operator|.
name|getLatestSnapshotId
argument_list|()
decl_stmt|;
specifier|final
name|INode
name|inode
init|=
name|iip
operator|.
name|getLastINode
argument_list|()
decl_stmt|;
comment|// check if the file in the latest snapshot
if|if
condition|(
name|inode
operator|.
name|isInLatestSnapshot
argument_list|(
name|latest
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"Concat: the source file "
operator|+
name|srcs
index|[
name|i
index|]
operator|+
literal|" is in snapshot "
operator|+
name|latest
argument_list|)
throw|;
block|}
comment|// check if the file has other references.
if|if
condition|(
name|inode
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
name|inode
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
name|srcs
index|[
name|i
index|]
operator|+
literal|" is referred by some other reference in some snapshot."
argument_list|)
throw|;
block|}
name|allSrcInodes
index|[
name|i
index|]
operator|=
name|inode
operator|.
name|asFile
argument_list|()
expr_stmt|;
block|}
name|trgInode
operator|.
name|concatBlocks
argument_list|(
name|allSrcInodes
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
name|allSrcInodes
control|)
block|{
if|if
condition|(
name|nodeToRemove
operator|==
literal|null
condition|)
continue|continue;
name|nodeToRemove
operator|.
name|setBlocks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|trgParent
operator|.
name|removeChild
argument_list|(
name|nodeToRemove
argument_list|,
name|trgLatestSnapshot
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
name|trgInode
operator|.
name|setModificationTime
argument_list|(
name|timestamp
argument_list|,
name|trgLatestSnapshot
argument_list|)
expr_stmt|;
name|trgParent
operator|.
name|updateModificationTime
argument_list|(
name|timestamp
argument_list|,
name|trgLatestSnapshot
argument_list|)
expr_stmt|;
comment|// update quota on the parent directory ('count' files removed, 0 space)
name|FSDirectory
operator|.
name|unprotectedUpdateCount
argument_list|(
name|trgIIP
argument_list|,
name|trgIIP
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
operator|-
name|count
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

