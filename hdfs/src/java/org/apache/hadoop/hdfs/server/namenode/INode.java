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

begin_comment
comment|/**  * We keep an in-memory representation of the file/block hierarchy.  * This is a base INode class containing common fields for file and   * directory inodes.  */
end_comment

begin_class
DECL|class|INode
specifier|public
specifier|abstract
class|class
name|INode
implements|implements
name|Comparable
argument_list|<
name|byte
index|[]
argument_list|>
implements|,
name|FSInodeInfo
block|{
comment|/*    *  The inode name is in java UTF8 encoding;     *  The name in HdfsFileStatus should keep the same encoding as this.    *  if this encoding is changed, implicitly getFileInfo and listStatus in    *  clientProtocol are changed; The decoding at the client    *  side should change accordingly.    */
DECL|field|name
specifier|protected
name|byte
index|[]
name|name
decl_stmt|;
DECL|field|parent
specifier|protected
name|INodeDirectory
name|parent
decl_stmt|;
DECL|field|modificationTime
specifier|protected
name|long
name|modificationTime
decl_stmt|;
DECL|field|accessTime
specifier|protected
name|long
name|accessTime
decl_stmt|;
comment|/** Simple wrapper for two counters :     *  nsCount (namespace consumed) and dsCount (diskspace consumed).    */
DECL|class|DirCounts
specifier|static
class|class
name|DirCounts
block|{
DECL|field|nsCount
name|long
name|nsCount
init|=
literal|0
decl_stmt|;
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
comment|//Only updated by updatePermissionStatus(...).
comment|//Other codes should not modify it.
DECL|field|permission
specifier|private
name|long
name|permission
decl_stmt|;
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
block|}
DECL|method|INode ()
specifier|protected
name|INode
parameter_list|()
block|{
name|name
operator|=
literal|null
expr_stmt|;
name|parent
operator|=
literal|null
expr_stmt|;
name|modificationTime
operator|=
literal|0
expr_stmt|;
name|accessTime
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|INode (PermissionStatus permissions, long mTime, long atime)
name|INode
parameter_list|(
name|PermissionStatus
name|permissions
parameter_list|,
name|long
name|mTime
parameter_list|,
name|long
name|atime
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|mTime
expr_stmt|;
name|setAccessTime
argument_list|(
name|atime
argument_list|)
expr_stmt|;
name|setPermissionStatus
argument_list|(
name|permissions
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
name|permissions
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|setLocalName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** copy constructor    *     * @param other Other node to be copied    */
DECL|method|INode (INode other)
name|INode
parameter_list|(
name|INode
name|other
parameter_list|)
block|{
name|setLocalName
argument_list|(
name|other
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|other
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|setPermissionStatus
argument_list|(
name|other
operator|.
name|getPermissionStatus
argument_list|()
argument_list|)
expr_stmt|;
name|setModificationTime
argument_list|(
name|other
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|setAccessTime
argument_list|(
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
DECL|method|updatePermissionStatus ( PermissionStatusFormat f, long n)
specifier|private
specifier|synchronized
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
specifier|protected
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
specifier|abstract
name|boolean
name|isDirectory
parameter_list|()
function_decl|;
comment|/**    * Collect all the blocks in all children of this INode.    * Count and return the number of files in the sub tree.    * Also clears references since this INode is deleted.    */
DECL|method|collectSubtreeBlocksAndClear (List<Block> v)
specifier|abstract
name|int
name|collectSubtreeBlocksAndClear
parameter_list|(
name|List
argument_list|<
name|Block
argument_list|>
name|v
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
comment|/**    * Get local file name    * @return local file name    */
DECL|method|getLocalName ()
name|String
name|getLocalName
parameter_list|()
block|{
return|return
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
comment|/**    * Get local file name    * @return local file name    */
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
comment|/** {@inheritDoc} */
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
comment|/** {@inheritDoc} */
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
assert|assert
operator|!
name|isDirectory
argument_list|()
assert|;
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
DECL|method|isLink ()
specifier|public
name|boolean
name|isLink
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
comment|/**    * Given some components, create a path name.    * @param components    * @return concatenated path    */
DECL|method|constructPath (byte[][] components, int start)
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
name|components
operator|.
name|length
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
name|components
operator|.
name|length
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
comment|//
comment|// Comparable interface
comment|//
DECL|method|compareTo (byte[] o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|byte
index|[]
name|o
parameter_list|)
block|{
return|return
name|compareBytes
argument_list|(
name|name
argument_list|,
name|o
argument_list|)
return|;
block|}
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
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
name|o
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
DECL|method|hashCode ()
specifier|public
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
comment|//
comment|// static methods
comment|//
comment|/**    * Compare two byte arrays.    *     * @return a negative integer, zero, or a positive integer     * as defined by {@link #compareTo(byte[])}.    */
DECL|method|compareBytes (byte[] a1, byte[] a2)
specifier|static
name|int
name|compareBytes
parameter_list|(
name|byte
index|[]
name|a1
parameter_list|,
name|byte
index|[]
name|a2
parameter_list|)
block|{
if|if
condition|(
name|a1
operator|==
name|a2
condition|)
return|return
literal|0
return|;
name|int
name|len1
init|=
operator|(
name|a1
operator|==
literal|null
condition|?
literal|0
else|:
name|a1
operator|.
name|length
operator|)
decl_stmt|;
name|int
name|len2
init|=
operator|(
name|a2
operator|==
literal|null
condition|?
literal|0
else|:
name|a2
operator|.
name|length
operator|)
decl_stmt|;
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|len1
argument_list|,
name|len2
argument_list|)
decl_stmt|;
name|byte
name|b1
decl_stmt|,
name|b2
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|b1
operator|=
name|a1
index|[
name|i
index|]
expr_stmt|;
name|b2
operator|=
name|a2
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|b1
operator|!=
name|b2
condition|)
return|return
name|b1
operator|-
name|b2
return|;
block|}
return|return
name|len1
operator|-
name|len2
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
block|}
end_class

end_unit

