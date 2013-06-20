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
name|server
operator|.
name|namenode
operator|.
name|INodeWithAdditionalFields
operator|.
name|PermissionStatusFormat
import|;
end_import

begin_comment
comment|/**  * The attributes of an inode.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|INodeAttributes
specifier|public
interface|interface
name|INodeAttributes
block|{
comment|/**    * @return null if the local name is null;    *         otherwise, return the local name byte array.    */
DECL|method|getLocalNameBytes ()
specifier|public
name|byte
index|[]
name|getLocalNameBytes
parameter_list|()
function_decl|;
comment|/** @return the user name. */
DECL|method|getUserName ()
specifier|public
name|String
name|getUserName
parameter_list|()
function_decl|;
comment|/** @return the group name. */
DECL|method|getGroupName ()
specifier|public
name|String
name|getGroupName
parameter_list|()
function_decl|;
comment|/** @return the permission. */
DECL|method|getFsPermission ()
specifier|public
name|FsPermission
name|getFsPermission
parameter_list|()
function_decl|;
comment|/** @return the permission as a short. */
DECL|method|getFsPermissionShort ()
specifier|public
name|short
name|getFsPermissionShort
parameter_list|()
function_decl|;
comment|/** @return the permission information as a long. */
DECL|method|getPermissionLong ()
specifier|public
name|long
name|getPermissionLong
parameter_list|()
function_decl|;
comment|/** @return the modification time. */
DECL|method|getModificationTime ()
specifier|public
name|long
name|getModificationTime
parameter_list|()
function_decl|;
comment|/** @return the access time. */
DECL|method|getAccessTime ()
specifier|public
name|long
name|getAccessTime
parameter_list|()
function_decl|;
comment|/** A read-only copy of the inode attributes. */
DECL|class|SnapshotCopy
specifier|public
specifier|static
specifier|abstract
class|class
name|SnapshotCopy
implements|implements
name|INodeAttributes
block|{
DECL|field|name
specifier|private
specifier|final
name|byte
index|[]
name|name
decl_stmt|;
DECL|field|permission
specifier|private
specifier|final
name|long
name|permission
decl_stmt|;
DECL|field|modificationTime
specifier|private
specifier|final
name|long
name|modificationTime
decl_stmt|;
DECL|field|accessTime
specifier|private
specifier|final
name|long
name|accessTime
decl_stmt|;
DECL|method|SnapshotCopy (byte[] name, PermissionStatus permissions, long modificationTime, long accessTime)
name|SnapshotCopy
parameter_list|(
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
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|PermissionStatusFormat
operator|.
name|toLong
argument_list|(
name|permissions
argument_list|)
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
DECL|method|SnapshotCopy (INode inode)
name|SnapshotCopy
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|inode
operator|.
name|getLocalNameBytes
argument_list|()
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|inode
operator|.
name|getPermissionLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|inode
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|accessTime
operator|=
name|inode
operator|.
name|getAccessTime
argument_list|()
expr_stmt|;
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
DECL|method|getUserName ()
specifier|public
specifier|final
name|String
name|getUserName
parameter_list|()
block|{
specifier|final
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
DECL|method|getGroupName ()
specifier|public
specifier|final
name|String
name|getGroupName
parameter_list|()
block|{
specifier|final
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
DECL|method|getFsPermission ()
specifier|public
specifier|final
name|FsPermission
name|getFsPermission
parameter_list|()
block|{
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
DECL|method|getModificationTime ()
specifier|public
specifier|final
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
name|modificationTime
return|;
block|}
annotation|@
name|Override
DECL|method|getAccessTime ()
specifier|public
specifier|final
name|long
name|getAccessTime
parameter_list|()
block|{
return|return
name|accessTime
return|;
block|}
block|}
block|}
end_interface

end_unit

