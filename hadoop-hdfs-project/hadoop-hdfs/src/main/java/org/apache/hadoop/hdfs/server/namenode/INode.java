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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|ContentSummary
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
name|FsPermission
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
name|PermissionStatus
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
name|DFSUtil
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
name|util
operator|.
name|StringUtils
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|SignedBytes
import|;
end_import

begin_comment
comment|/**  * We keep an in-memory representation of the file/block hierarchy.  * This is a base INode class containing common fields for file and   * directory inodes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INode
specifier|abstract
class|class
name|INode
implements|implements
name|Comparable
argument_list|<
name|byte
index|[]
argument_list|>
block|{
DECL|field|EMPTY_LIST
specifier|static
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|EMPTY_LIST
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|INode
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Wrapper of two counters for namespace consumed and diskspace consumed. */
DECL|class|DirCounts
specifier|static
class|class
name|DirCounts
block|{
comment|/** namespace count */
DECL|field|nsCount
name|long
name|nsCount
init|=
literal|0
decl_stmt|;
comment|/** diskspace count */
DECL|field|dsCount
name|long
name|dsCount
init|=
literal|0
decl_stmt|;
comment|/** returns namespace count */
DECL|method|getNsCount ()
name|long
name|getNsCount
parameter_list|()
block|{
return|return
name|nsCount
return|;
block|}
comment|/** returns diskspace count */
DECL|method|getDsCount ()
name|long
name|getDsCount
parameter_list|()
block|{
return|return
name|dsCount
return|;
block|}
block|}
DECL|enum|PermissionStatusFormat
specifier|private
specifier|static
enum|enum
name|PermissionStatusFormat
block|{
DECL|enumConstant|MODE
name|MODE
argument_list|(
literal|0
argument_list|,
literal|16
argument_list|)
block|,
DECL|enumConstant|GROUP
name|GROUP
argument_list|(
name|MODE
operator|.
name|OFFSET
operator|+
name|MODE
operator|.
name|LENGTH
argument_list|,
literal|25
argument_list|)
block|,
DECL|enumConstant|USER
name|USER
argument_list|(
name|GROUP
operator|.
name|OFFSET
operator|+
name|GROUP
operator|.
name|LENGTH
argument_list|,
literal|23
argument_list|)
block|;
DECL|field|OFFSET
specifier|final
name|int
name|OFFSET
decl_stmt|;
DECL|field|LENGTH
specifier|final
name|int
name|LENGTH
decl_stmt|;
comment|//bit length
DECL|field|MASK
specifier|final
name|long
name|MASK
decl_stmt|;
DECL|method|PermissionStatusFormat (int offset, int length)
name|PermissionStatusFormat
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|OFFSET
operator|=
name|offset
expr_stmt|;
name|LENGTH
operator|=
name|length
expr_stmt|;
name|MASK
operator|=
operator|(
operator|(
operator|-
literal|1L
operator|)
operator|>>>
operator|(
literal|64
operator|-
name|LENGTH
operator|)
operator|)
operator|<<
name|OFFSET
expr_stmt|;
block|}
DECL|method|retrieve (long record)
name|long
name|retrieve
parameter_list|(
name|long
name|record
parameter_list|)
block|{
return|return
operator|(
name|record
operator|&
name|MASK
operator|)
operator|>>>
name|OFFSET
return|;
block|}
DECL|method|combine (long bits, long record)
name|long
name|combine
parameter_list|(
name|long
name|bits
parameter_list|,
name|long
name|record
parameter_list|)
block|{
return|return
operator|(
name|record
operator|&
operator|~
name|MASK
operator|)
operator||
operator|(
name|bits
operator|<<
name|OFFSET
operator|)
return|;
block|}
comment|/** Set the {@link PermissionStatus} */
DECL|method|toLong (PermissionStatus ps)
specifier|static
name|long
name|toLong
parameter_list|(
name|PermissionStatus
name|ps
parameter_list|)
block|{
name|long
name|permission
init|=
literal|0L
decl_stmt|;
specifier|final
name|int
name|user
init|=
name|SerialNumberManager
operator|.
name|INSTANCE
operator|.
name|getUserSerialNumber
argument_list|(
name|ps
operator|.
name|getUserName
argument_list|()
argument_list|)
decl_stmt|;
name|permission
operator|=
name|PermissionStatusFormat
operator|.
name|USER
operator|.
name|combine
argument_list|(
name|user
argument_list|,
name|permission
argument_list|)
expr_stmt|;
specifier|final
name|int
name|group
init|=
name|SerialNumberManager
operator|.
name|INSTANCE
operator|.
name|getGroupSerialNumber
argument_list|(
name|ps
operator|.
name|getGroupName
argument_list|()
argument_list|)
decl_stmt|;
name|permission
operator|=
name|PermissionStatusFormat
operator|.
name|GROUP
operator|.
name|combine
argument_list|(
name|group
argument_list|,
name|permission
argument_list|)
expr_stmt|;
specifier|final
name|int
name|mode
init|=
name|ps
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
decl_stmt|;
name|permission
operator|=
name|PermissionStatusFormat
operator|.
name|MODE
operator|.
name|combine
argument_list|(
name|mode
argument_list|,
name|permission
argument_list|)
expr_stmt|;
return|return
name|permission
return|;
block|}
block|}
comment|/**    *  The inode name is in java UTF8 encoding;     *  The name in HdfsFileStatus should keep the same encoding as this.    *  if this encoding is changed, implicitly getFileInfo and listStatus in    *  clientProtocol are changed; The decoding at the client    *  side should change accordingly.    */
DECL|field|name
specifier|private
name|byte
index|[]
name|name
init|=
literal|null
decl_stmt|;
comment|/**     * Permission encoded using PermissionStatusFormat.    * Codes other than {@link #updatePermissionStatus(PermissionStatusFormat, long)}.    * should not modify it.    */
DECL|field|permission
specifier|private
name|long
name|permission
init|=
literal|0L
decl_stmt|;
DECL|field|parent
specifier|protected
name|INodeDirectory
name|parent
init|=
literal|null
decl_stmt|;
DECL|field|modificationTime
specifier|protected
name|long
name|modificationTime
init|=
literal|0L
decl_stmt|;
DECL|field|accessTime
specifier|protected
name|long
name|accessTime
init|=
literal|0L
decl_stmt|;
DECL|method|INode (byte[] name, long permission, INodeDirectory parent, long modificationTime, long accessTime)
specifier|private
name|INode
parameter_list|(
name|byte
index|[]
name|name
parameter_list|,
name|long
name|permission
parameter_list|,
name|INodeDirectory
name|parent
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|accessTime
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|modificationTime
expr_stmt|;
name|this
operator|.
name|accessTime
operator|=
name|accessTime
expr_stmt|;
block|}
DECL|method|INode (byte[] name, PermissionStatus permissions, INodeDirectory parent, long modificationTime, long accessTime)
name|INode
parameter_list|(
name|byte
index|[]
name|name
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|,
name|INodeDirectory
name|parent
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|accessTime
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|PermissionStatusFormat
operator|.
name|toLong
argument_list|(
name|permissions
argument_list|)
argument_list|,
name|parent
argument_list|,
name|modificationTime
argument_list|,
name|accessTime
argument_list|)
expr_stmt|;
block|}
DECL|method|INode (PermissionStatus permissions, long mtime, long atime)
name|INode
parameter_list|(
name|PermissionStatus
name|permissions
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|permissions
argument_list|,
literal|null
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
block|}
DECL|method|INode (String name, PermissionStatus permissions)
specifier|protected
name|INode
parameter_list|(
name|String
name|name
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|)
block|{
name|this
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|name
argument_list|)
argument_list|,
name|permissions
argument_list|,
literal|null
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/** @param other Other node to be copied */
DECL|method|INode (INode other)
name|INode
parameter_list|(
name|INode
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
operator|.
name|getLocalNameBytes
argument_list|()
argument_list|,
name|other
operator|.
name|permission
argument_list|,
name|other
operator|.
name|getParent
argument_list|()
argument_list|,
name|other
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|other
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check whether this is the root inode.    */
DECL|method|isRoot ()
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
name|name
operator|.
name|length
operator|==
literal|0
return|;
block|}
comment|/** Set the {@link PermissionStatus} */
DECL|method|setPermissionStatus (PermissionStatus ps)
specifier|protected
name|void
name|setPermissionStatus
parameter_list|(
name|PermissionStatus
name|ps
parameter_list|)
block|{
name|setUser
argument_list|(
name|ps
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|setGroup
argument_list|(
name|ps
operator|.
name|getGroupName
argument_list|()
argument_list|)
expr_stmt|;
name|setPermission
argument_list|(
name|ps
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Get the {@link PermissionStatus} */
DECL|method|getPermissionStatus ()
specifier|protected
name|PermissionStatus
name|getPermissionStatus
parameter_list|()
block|{
return|return
operator|new
name|PermissionStatus
argument_list|(
name|getUserName
argument_list|()
argument_list|,
name|getGroupName
argument_list|()
argument_list|,
name|getFsPermission
argument_list|()
argument_list|)
return|;
block|}
DECL|method|updatePermissionStatus (PermissionStatusFormat f, long n)
specifier|private
name|void
name|updatePermissionStatus
parameter_list|(
name|PermissionStatusFormat
name|f
parameter_list|,
name|long
name|n
parameter_list|)
block|{
name|permission
operator|=
name|f
operator|.
name|combine
argument_list|(
name|n
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
comment|/** Get user name */
DECL|method|getUserName ()
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
name|int
name|n
init|=
operator|(
name|int
operator|)
name|PermissionStatusFormat
operator|.
name|USER
operator|.
name|retrieve
argument_list|(
name|permission
argument_list|)
decl_stmt|;
return|return
name|SerialNumberManager
operator|.
name|INSTANCE
operator|.
name|getUser
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/** Set user */
DECL|method|setUser (String user)
specifier|protected
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|int
name|n
init|=
name|SerialNumberManager
operator|.
name|INSTANCE
operator|.
name|getUserSerialNumber
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|updatePermissionStatus
argument_list|(
name|PermissionStatusFormat
operator|.
name|USER
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
comment|/** Get group name */
DECL|method|getGroupName ()
specifier|public
name|String
name|getGroupName
parameter_list|()
block|{
name|int
name|n
init|=
operator|(
name|int
operator|)
name|PermissionStatusFormat
operator|.
name|GROUP
operator|.
name|retrieve
argument_list|(
name|permission
argument_list|)
decl_stmt|;
return|return
name|SerialNumberManager
operator|.
name|INSTANCE
operator|.
name|getGroup
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/** Set group */
DECL|method|setGroup (String group)
specifier|protected
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|int
name|n
init|=
name|SerialNumberManager
operator|.
name|INSTANCE
operator|.
name|getGroupSerialNumber
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|updatePermissionStatus
argument_list|(
name|PermissionStatusFormat
operator|.
name|GROUP
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
comment|/** Get the {@link FsPermission} */
DECL|method|getFsPermission ()
specifier|public
name|FsPermission
name|getFsPermission
parameter_list|()
block|{
return|return
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
name|PermissionStatusFormat
operator|.
name|MODE
operator|.
name|retrieve
argument_list|(
name|permission
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getFsPermissionShort ()
specifier|protected
name|short
name|getFsPermissionShort
parameter_list|()
block|{
return|return
operator|(
name|short
operator|)
name|PermissionStatusFormat
operator|.
name|MODE
operator|.
name|retrieve
argument_list|(
name|permission
argument_list|)
return|;
block|}
comment|/** Set the {@link FsPermission} of this {@link INode} */
DECL|method|setPermission (FsPermission permission)
name|void
name|setPermission
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
block|{
name|updatePermissionStatus
argument_list|(
name|PermissionStatusFormat
operator|.
name|MODE
argument_list|,
name|permission
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check whether it's a directory    */
DECL|method|isDirectory ()
specifier|public
name|boolean
name|isDirectory
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Collect all the blocks in all children of this INode. Count and return the    * number of files in the sub tree. Also clears references since this INode is    * deleted.    *     * @param info    *          Containing all the blocks collected from the children of this    *          INode. These blocks later should be removed from the blocksMap.    */
DECL|method|collectSubtreeBlocksAndClear (BlocksMapUpdateInfo info)
specifier|abstract
name|int
name|collectSubtreeBlocksAndClear
parameter_list|(
name|BlocksMapUpdateInfo
name|info
parameter_list|)
function_decl|;
comment|/** Compute {@link ContentSummary}. */
DECL|method|computeContentSummary ()
specifier|public
specifier|final
name|ContentSummary
name|computeContentSummary
parameter_list|()
block|{
name|long
index|[]
name|a
init|=
name|computeContentSummary
argument_list|(
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|ContentSummary
argument_list|(
name|a
index|[
literal|0
index|]
argument_list|,
name|a
index|[
literal|1
index|]
argument_list|,
name|a
index|[
literal|2
index|]
argument_list|,
name|getNsQuota
argument_list|()
argument_list|,
name|a
index|[
literal|3
index|]
argument_list|,
name|getDsQuota
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return an array of three longs.     * 0: length, 1: file count, 2: directory count 3: disk space    */
DECL|method|computeContentSummary (long[] summary)
specifier|abstract
name|long
index|[]
name|computeContentSummary
parameter_list|(
name|long
index|[]
name|summary
parameter_list|)
function_decl|;
comment|/**    * Get the quota set for this inode    * @return the quota if it is set; -1 otherwise    */
DECL|method|getNsQuota ()
name|long
name|getNsQuota
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getDsQuota ()
name|long
name|getDsQuota
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|isQuotaSet ()
name|boolean
name|isQuotaSet
parameter_list|()
block|{
return|return
name|getNsQuota
argument_list|()
operator|>=
literal|0
operator|||
name|getDsQuota
argument_list|()
operator|>=
literal|0
return|;
block|}
comment|/**    * Adds total number of names and total disk space taken under     * this tree to counts.    * Returns updated counts object.    */
DECL|method|spaceConsumedInTree (DirCounts counts)
specifier|abstract
name|DirCounts
name|spaceConsumedInTree
parameter_list|(
name|DirCounts
name|counts
parameter_list|)
function_decl|;
comment|/**    * @return null if the local name is null; otherwise, return the local name.    */
DECL|method|getLocalName ()
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|name
operator|==
literal|null
condition|?
literal|null
else|:
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getLocalParentDir ()
name|String
name|getLocalParentDir
parameter_list|()
block|{
name|INode
name|inode
init|=
name|isRoot
argument_list|()
condition|?
name|this
else|:
name|getParent
argument_list|()
decl_stmt|;
return|return
operator|(
name|inode
operator|!=
literal|null
operator|)
condition|?
name|inode
operator|.
name|getFullPathName
argument_list|()
else|:
literal|""
return|;
block|}
comment|/**    * @return null if the local name is null;    *         otherwise, return the local name byte array.    */
DECL|method|getLocalNameBytes ()
name|byte
index|[]
name|getLocalNameBytes
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Set local file name    */
DECL|method|setLocalName (String name)
name|void
name|setLocalName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set local file name    */
DECL|method|setLocalName (byte[] name)
name|void
name|setLocalName
parameter_list|(
name|byte
index|[]
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getFullPathName ()
specifier|public
name|String
name|getFullPathName
parameter_list|()
block|{
comment|// Get the full path name of this inode.
return|return
name|FSDirectory
operator|.
name|getFullPathName
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"\""
operator|+
name|getFullPathName
argument_list|()
operator|+
literal|"\":"
operator|+
name|getUserName
argument_list|()
operator|+
literal|":"
operator|+
name|getGroupName
argument_list|()
operator|+
literal|":"
operator|+
operator|(
name|isDirectory
argument_list|()
condition|?
literal|"d"
else|:
literal|"-"
operator|)
operator|+
name|getFsPermission
argument_list|()
return|;
block|}
comment|/**    * Get parent directory     * @return parent INode    */
DECL|method|getParent ()
name|INodeDirectory
name|getParent
parameter_list|()
block|{
return|return
name|this
operator|.
name|parent
return|;
block|}
comment|/**     * Get last modification time of inode.    * @return access time    */
DECL|method|getModificationTime ()
specifier|public
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|modificationTime
return|;
block|}
comment|/**    * Set last modification time of inode.    */
DECL|method|setModificationTime (long modtime)
name|void
name|setModificationTime
parameter_list|(
name|long
name|modtime
parameter_list|)
block|{
assert|assert
name|isDirectory
argument_list|()
assert|;
if|if
condition|(
name|this
operator|.
name|modificationTime
operator|<=
name|modtime
condition|)
block|{
name|this
operator|.
name|modificationTime
operator|=
name|modtime
expr_stmt|;
block|}
block|}
comment|/**    * Always set the last modification time of inode.    */
DECL|method|setModificationTimeForce (long modtime)
name|void
name|setModificationTimeForce
parameter_list|(
name|long
name|modtime
parameter_list|)
block|{
name|this
operator|.
name|modificationTime
operator|=
name|modtime
expr_stmt|;
block|}
comment|/**    * Get access time of inode.    * @return access time    */
DECL|method|getAccessTime ()
specifier|public
name|long
name|getAccessTime
parameter_list|()
block|{
return|return
name|accessTime
return|;
block|}
comment|/**    * Set last access time of inode.    */
DECL|method|setAccessTime (long atime)
name|void
name|setAccessTime
parameter_list|(
name|long
name|atime
parameter_list|)
block|{
name|accessTime
operator|=
name|atime
expr_stmt|;
block|}
comment|/**    * Is this inode being constructed?    */
DECL|method|isUnderConstruction ()
specifier|public
name|boolean
name|isUnderConstruction
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Check whether it's a symlink    */
DECL|method|isSymlink ()
specifier|public
name|boolean
name|isSymlink
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Breaks file path into components.    * @param path    * @return array of byte arrays each of which represents     * a single path component.    */
DECL|method|getPathComponents (String path)
specifier|static
name|byte
index|[]
index|[]
name|getPathComponents
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|getPathComponents
argument_list|(
name|getPathNames
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
comment|/** Convert strings to byte arrays for path components. */
DECL|method|getPathComponents (String[] strings)
specifier|static
name|byte
index|[]
index|[]
name|getPathComponents
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
block|{
if|if
condition|(
name|strings
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|byte
index|[]
index|[]
block|{
literal|null
block|}
return|;
block|}
name|byte
index|[]
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|strings
operator|.
name|length
index|]
index|[]
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
name|strings
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|bytes
index|[
name|i
index|]
operator|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|strings
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|/**    * Splits an absolute path into an array of path components.    * @param path    * @throws AssertionError if the given path is invalid.    * @return array of path components.    */
DECL|method|getPathNames (String path)
specifier|static
name|String
index|[]
name|getPathNames
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
operator|||
operator|!
name|path
operator|.
name|startsWith
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Absolute path required"
argument_list|)
throw|;
block|}
return|return
name|StringUtils
operator|.
name|split
argument_list|(
name|path
argument_list|,
name|Path
operator|.
name|SEPARATOR_CHAR
argument_list|)
return|;
block|}
comment|/**    * Given some components, create a path name.    * @param components The path components    * @param start index    * @param end index    * @return concatenated path    */
DECL|method|constructPath (byte[][] components, int start, int end)
specifier|static
name|String
name|constructPath
parameter_list|(
name|byte
index|[]
index|[]
name|components
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|components
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|end
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|removeNode ()
name|boolean
name|removeNode
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|parent
operator|.
name|removeChild
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|parent
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|field|EMPTY_BYTES
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY_BYTES
init|=
block|{}
decl_stmt|;
annotation|@
name|Override
DECL|method|compareTo (byte[] bytes)
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|left
init|=
name|name
operator|==
literal|null
condition|?
name|EMPTY_BYTES
else|:
name|name
decl_stmt|;
specifier|final
name|byte
index|[]
name|right
init|=
name|bytes
operator|==
literal|null
condition|?
name|EMPTY_BYTES
else|:
name|bytes
decl_stmt|;
return|return
name|SignedBytes
operator|.
name|lexicographicalComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object that)
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|that
operator|==
literal|null
operator|||
operator|!
operator|(
name|that
operator|instanceof
name|INode
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|name
argument_list|,
operator|(
operator|(
name|INode
operator|)
name|that
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|this
operator|.
name|name
argument_list|)
return|;
block|}
comment|/**    * Create an INode; the inode's name is not set yet    *     * @param permissions permissions    * @param blocks blocks if a file    * @param symlink symblic link if a symbolic link    * @param replication replication factor    * @param modificationTime modification time    * @param atime access time    * @param nsQuota namespace quota    * @param dsQuota disk quota    * @param preferredBlockSize block size    * @return an inode    */
DECL|method|newINode (PermissionStatus permissions, BlockInfo[] blocks, String symlink, short replication, long modificationTime, long atime, long nsQuota, long dsQuota, long preferredBlockSize)
specifier|static
name|INode
name|newINode
parameter_list|(
name|PermissionStatus
name|permissions
parameter_list|,
name|BlockInfo
index|[]
name|blocks
parameter_list|,
name|String
name|symlink
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|atime
parameter_list|,
name|long
name|nsQuota
parameter_list|,
name|long
name|dsQuota
parameter_list|,
name|long
name|preferredBlockSize
parameter_list|)
block|{
if|if
condition|(
name|symlink
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|// check if symbolic link
return|return
operator|new
name|INodeSymlink
argument_list|(
name|symlink
argument_list|,
name|modificationTime
argument_list|,
name|atime
argument_list|,
name|permissions
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|blocks
operator|==
literal|null
condition|)
block|{
comment|//not sym link and blocks null? directory!
if|if
condition|(
name|nsQuota
operator|>=
literal|0
operator|||
name|dsQuota
operator|>=
literal|0
condition|)
block|{
return|return
operator|new
name|INodeDirectoryWithQuota
argument_list|(
name|permissions
argument_list|,
name|modificationTime
argument_list|,
name|nsQuota
argument_list|,
name|dsQuota
argument_list|)
return|;
block|}
comment|// regular directory
return|return
operator|new
name|INodeDirectory
argument_list|(
name|permissions
argument_list|,
name|modificationTime
argument_list|)
return|;
block|}
comment|// file
return|return
operator|new
name|INodeFile
argument_list|(
name|permissions
argument_list|,
name|blocks
argument_list|,
name|replication
argument_list|,
name|modificationTime
argument_list|,
name|atime
argument_list|,
name|preferredBlockSize
argument_list|)
return|;
block|}
comment|/**    * Dump the subtree starting from this inode.    * @return a text representation of the tree.    */
annotation|@
name|VisibleForTesting
DECL|method|dumpTreeRecursively ()
specifier|public
name|StringBuffer
name|dumpTreeRecursively
parameter_list|()
block|{
specifier|final
name|StringWriter
name|out
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|dumpTreeRecursively
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|out
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|StringBuilder
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|getBuffer
argument_list|()
return|;
block|}
comment|/**    * Dump tree recursively.    * @param prefix The prefix string that each line should print.    */
annotation|@
name|VisibleForTesting
DECL|method|dumpTreeRecursively (PrintWriter out, StringBuilder prefix)
specifier|public
name|void
name|dumpTreeRecursively
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|StringBuilder
name|prefix
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"   ("
argument_list|)
expr_stmt|;
specifier|final
name|String
name|s
init|=
name|super
operator|.
name|toString
argument_list|()
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|s
operator|.
name|lastIndexOf
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Information used for updating the blocksMap when deleting files.    */
DECL|class|BlocksMapUpdateInfo
specifier|public
specifier|static
class|class
name|BlocksMapUpdateInfo
block|{
comment|/**      * The list of blocks that need to be removed from blocksMap      */
DECL|field|toDeleteList
specifier|private
name|List
argument_list|<
name|Block
argument_list|>
name|toDeleteList
decl_stmt|;
DECL|method|BlocksMapUpdateInfo (List<Block> toDeleteList)
specifier|public
name|BlocksMapUpdateInfo
parameter_list|(
name|List
argument_list|<
name|Block
argument_list|>
name|toDeleteList
parameter_list|)
block|{
name|this
operator|.
name|toDeleteList
operator|=
name|toDeleteList
operator|==
literal|null
condition|?
operator|new
name|ArrayList
argument_list|<
name|Block
argument_list|>
argument_list|()
else|:
name|toDeleteList
expr_stmt|;
block|}
DECL|method|BlocksMapUpdateInfo ()
specifier|public
name|BlocksMapUpdateInfo
parameter_list|()
block|{
name|toDeleteList
operator|=
operator|new
name|ArrayList
argument_list|<
name|Block
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return The list of blocks that need to be removed from blocksMap      */
DECL|method|getToDeleteList ()
specifier|public
name|List
argument_list|<
name|Block
argument_list|>
name|getToDeleteList
parameter_list|()
block|{
return|return
name|toDeleteList
return|;
block|}
comment|/**      * Add a to-be-deleted block into the      * {@link BlocksMapUpdateInfo#toDeleteList}      * @param toDelete the to-be-deleted block      */
DECL|method|addDeleteBlock (Block toDelete)
specifier|public
name|void
name|addDeleteBlock
parameter_list|(
name|Block
name|toDelete
parameter_list|)
block|{
if|if
condition|(
name|toDelete
operator|!=
literal|null
condition|)
block|{
name|toDeleteList
operator|.
name|add
argument_list|(
name|toDelete
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Clear {@link BlocksMapUpdateInfo#toDeleteList}      */
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|toDeleteList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

