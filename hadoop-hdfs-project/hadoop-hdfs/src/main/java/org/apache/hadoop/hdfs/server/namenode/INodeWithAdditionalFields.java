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
name|namenode
operator|.
name|snapshot
operator|.
name|Snapshot
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
name|LightWeightGSet
operator|.
name|LinkedElement
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
comment|/**  * {@link INode} with additional fields including id, name, permission,  * access time and modification time.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeWithAdditionalFields
specifier|public
specifier|abstract
class|class
name|INodeWithAdditionalFields
extends|extends
name|INode
implements|implements
name|LinkedElement
block|{
DECL|enum|PermissionStatusFormat
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
comment|/** Encode the {@link PermissionStatus} to a long. */
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
comment|/** The inode id. */
DECL|field|id
specifier|final
specifier|private
name|long
name|id
decl_stmt|;
comment|/**    *  The inode name is in java UTF8 encoding;     *  The name in HdfsFileStatus should keep the same encoding as this.    *  if this encoding is changed, implicitly getFileInfo and listStatus in    *  clientProtocol are changed; The decoding at the client    *  side should change accordingly.    */
DECL|field|name
specifier|private
name|byte
index|[]
name|name
init|=
literal|null
decl_stmt|;
comment|/**     * Permission encoded using {@link PermissionStatusFormat}.    * Codes other than {@link #clonePermissionStatus(INodeWithAdditionalFields)}    * and {@link #updatePermissionStatus(PermissionStatusFormat, long)}    * should not modify it.    */
DECL|field|permission
specifier|private
name|long
name|permission
init|=
literal|0L
decl_stmt|;
comment|/** The last modification time*/
DECL|field|modificationTime
specifier|private
name|long
name|modificationTime
init|=
literal|0L
decl_stmt|;
comment|/** The last access time*/
DECL|field|accessTime
specifier|private
name|long
name|accessTime
init|=
literal|0L
decl_stmt|;
comment|/** For implementing {@link LinkedElement}. */
DECL|field|next
specifier|private
name|LinkedElement
name|next
init|=
literal|null
decl_stmt|;
comment|/** An array {@link Feature}s. */
DECL|field|EMPTY_FEATURE
specifier|private
specifier|static
specifier|final
name|Feature
index|[]
name|EMPTY_FEATURE
init|=
operator|new
name|Feature
index|[
literal|0
index|]
decl_stmt|;
DECL|field|features
specifier|protected
name|Feature
index|[]
name|features
init|=
name|EMPTY_FEATURE
decl_stmt|;
DECL|method|INodeWithAdditionalFields (INode parent, long id, byte[] name, long permission, long modificationTime, long accessTime)
specifier|private
name|INodeWithAdditionalFields
parameter_list|(
name|INode
name|parent
parameter_list|,
name|long
name|id
parameter_list|,
name|byte
index|[]
name|name
parameter_list|,
name|long
name|permission
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|accessTime
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
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
DECL|method|INodeWithAdditionalFields (long id, byte[] name, PermissionStatus permissions, long modificationTime, long accessTime)
name|INodeWithAdditionalFields
parameter_list|(
name|long
name|id
parameter_list|,
name|byte
index|[]
name|name
parameter_list|,
name|PermissionStatus
name|permissions
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
literal|null
argument_list|,
name|id
argument_list|,
name|name
argument_list|,
name|PermissionStatusFormat
operator|.
name|toLong
argument_list|(
name|permissions
argument_list|)
argument_list|,
name|modificationTime
argument_list|,
name|accessTime
argument_list|)
expr_stmt|;
block|}
comment|/** @param other Other node to be copied */
DECL|method|INodeWithAdditionalFields (INodeWithAdditionalFields other)
name|INodeWithAdditionalFields
parameter_list|(
name|INodeWithAdditionalFields
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
operator|.
name|getParentReference
argument_list|()
operator|!=
literal|null
condition|?
name|other
operator|.
name|getParentReference
argument_list|()
else|:
name|other
operator|.
name|getParent
argument_list|()
argument_list|,
name|other
operator|.
name|getId
argument_list|()
argument_list|,
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
name|modificationTime
argument_list|,
name|other
operator|.
name|accessTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNext (LinkedElement next)
specifier|public
name|void
name|setNext
parameter_list|(
name|LinkedElement
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNext ()
specifier|public
name|LinkedElement
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
comment|/** Get inode id */
annotation|@
name|Override
DECL|method|getId ()
specifier|public
specifier|final
name|long
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalNameBytes ()
specifier|public
specifier|final
name|byte
index|[]
name|getLocalNameBytes
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|setLocalName (byte[] name)
specifier|public
specifier|final
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
comment|/** Clone the {@link PermissionStatus}. */
DECL|method|clonePermissionStatus (INodeWithAdditionalFields that)
specifier|final
name|void
name|clonePermissionStatus
parameter_list|(
name|INodeWithAdditionalFields
name|that
parameter_list|)
block|{
name|this
operator|.
name|permission
operator|=
name|that
operator|.
name|permission
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPermissionStatus (Snapshot snapshot)
specifier|final
name|PermissionStatus
name|getPermissionStatus
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
operator|new
name|PermissionStatus
argument_list|(
name|getUserName
argument_list|(
name|snapshot
argument_list|)
argument_list|,
name|getGroupName
argument_list|(
name|snapshot
argument_list|)
argument_list|,
name|getFsPermission
argument_list|(
name|snapshot
argument_list|)
argument_list|)
return|;
block|}
DECL|method|updatePermissionStatus (PermissionStatusFormat f, long n)
specifier|private
specifier|final
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
name|this
operator|.
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
annotation|@
name|Override
DECL|method|getUserName (Snapshot snapshot)
specifier|final
name|String
name|getUserName
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
block|{
return|return
name|getSnapshotINode
argument_list|(
name|snapshot
argument_list|)
operator|.
name|getUserName
argument_list|()
return|;
block|}
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
annotation|@
name|Override
DECL|method|setUser (String user)
specifier|final
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
annotation|@
name|Override
DECL|method|getGroupName (Snapshot snapshot)
specifier|final
name|String
name|getGroupName
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
block|{
return|return
name|getSnapshotINode
argument_list|(
name|snapshot
argument_list|)
operator|.
name|getGroupName
argument_list|()
return|;
block|}
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
annotation|@
name|Override
DECL|method|setGroup (String group)
specifier|final
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
annotation|@
name|Override
DECL|method|getFsPermission (Snapshot snapshot)
specifier|final
name|FsPermission
name|getFsPermission
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
block|{
return|return
name|getSnapshotINode
argument_list|(
name|snapshot
argument_list|)
operator|.
name|getFsPermission
argument_list|()
return|;
block|}
return|return
operator|new
name|FsPermission
argument_list|(
name|getFsPermissionShort
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFsPermissionShort ()
specifier|public
specifier|final
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
annotation|@
name|Override
DECL|method|setPermission (FsPermission permission)
name|void
name|setPermission
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
block|{
specifier|final
name|short
name|mode
init|=
name|permission
operator|.
name|toShort
argument_list|()
decl_stmt|;
name|updatePermissionStatus
argument_list|(
name|PermissionStatusFormat
operator|.
name|MODE
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPermissionLong ()
specifier|public
name|long
name|getPermissionLong
parameter_list|()
block|{
return|return
name|permission
return|;
block|}
annotation|@
name|Override
DECL|method|getModificationTime (Snapshot snapshot)
specifier|final
name|long
name|getModificationTime
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
block|{
return|return
name|getSnapshotINode
argument_list|(
name|snapshot
argument_list|)
operator|.
name|getModificationTime
argument_list|()
return|;
block|}
return|return
name|this
operator|.
name|modificationTime
return|;
block|}
comment|/** Update modification time if it is larger than the current value. */
annotation|@
name|Override
DECL|method|updateModificationTime (long mtime, Snapshot latest, final INodeMap inodeMap)
specifier|public
specifier|final
name|INode
name|updateModificationTime
parameter_list|(
name|long
name|mtime
parameter_list|,
name|Snapshot
name|latest
parameter_list|,
specifier|final
name|INodeMap
name|inodeMap
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mtime
operator|<=
name|modificationTime
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|setModificationTime
argument_list|(
name|mtime
argument_list|,
name|latest
argument_list|,
name|inodeMap
argument_list|)
return|;
block|}
DECL|method|cloneModificationTime (INodeWithAdditionalFields that)
specifier|final
name|void
name|cloneModificationTime
parameter_list|(
name|INodeWithAdditionalFields
name|that
parameter_list|)
block|{
name|this
operator|.
name|modificationTime
operator|=
name|that
operator|.
name|modificationTime
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setModificationTime (long modificationTime)
specifier|public
specifier|final
name|void
name|setModificationTime
parameter_list|(
name|long
name|modificationTime
parameter_list|)
block|{
name|this
operator|.
name|modificationTime
operator|=
name|modificationTime
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAccessTime (Snapshot snapshot)
specifier|final
name|long
name|getAccessTime
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
block|{
return|return
name|getSnapshotINode
argument_list|(
name|snapshot
argument_list|)
operator|.
name|getAccessTime
argument_list|()
return|;
block|}
return|return
name|accessTime
return|;
block|}
comment|/**    * Set last access time of inode.    */
annotation|@
name|Override
DECL|method|setAccessTime (long accessTime)
specifier|public
specifier|final
name|void
name|setAccessTime
parameter_list|(
name|long
name|accessTime
parameter_list|)
block|{
name|this
operator|.
name|accessTime
operator|=
name|accessTime
expr_stmt|;
block|}
DECL|method|addFeature (Feature f)
specifier|protected
name|void
name|addFeature
parameter_list|(
name|Feature
name|f
parameter_list|)
block|{
name|int
name|size
init|=
name|features
operator|.
name|length
decl_stmt|;
name|Feature
index|[]
name|arr
init|=
operator|new
name|Feature
index|[
name|size
operator|+
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|size
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|features
argument_list|,
literal|0
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
name|arr
index|[
name|size
index|]
operator|=
name|f
expr_stmt|;
name|features
operator|=
name|arr
expr_stmt|;
block|}
DECL|method|removeFeature (Feature f)
specifier|protected
name|void
name|removeFeature
parameter_list|(
name|Feature
name|f
parameter_list|)
block|{
name|int
name|size
init|=
name|features
operator|.
name|length
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|size
operator|>
literal|0
argument_list|,
literal|"Feature "
operator|+
name|f
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" not found."
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|==
literal|1
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|features
index|[
literal|0
index|]
operator|==
name|f
argument_list|,
literal|"Feature "
operator|+
name|f
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" not found."
argument_list|)
expr_stmt|;
name|features
operator|=
name|EMPTY_FEATURE
expr_stmt|;
return|return;
block|}
name|Feature
index|[]
name|arr
init|=
operator|new
name|Feature
index|[
name|size
operator|-
literal|1
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
name|boolean
name|overflow
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Feature
name|f1
range|:
name|features
control|)
block|{
if|if
condition|(
name|f1
operator|!=
name|f
condition|)
block|{
if|if
condition|(
name|j
operator|==
name|size
operator|-
literal|1
condition|)
block|{
name|overflow
operator|=
literal|true
expr_stmt|;
break|break;
block|}
else|else
block|{
name|arr
index|[
name|j
operator|++
index|]
operator|=
name|f1
expr_stmt|;
block|}
block|}
block|}
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|overflow
operator|&&
name|j
operator|==
name|size
operator|-
literal|1
argument_list|,
literal|"Feature "
operator|+
name|f
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" not found."
argument_list|)
expr_stmt|;
name|features
operator|=
name|arr
expr_stmt|;
block|}
block|}
end_class

end_unit

