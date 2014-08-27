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
name|INodeFile
operator|.
name|HeaderFormat
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
name|XAttrFeature
import|;
end_import

begin_comment
comment|/**  * The attributes of a file.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|INodeFileAttributes
specifier|public
interface|interface
name|INodeFileAttributes
extends|extends
name|INodeAttributes
block|{
comment|/** @return the file replication. */
DECL|method|getFileReplication ()
specifier|public
name|short
name|getFileReplication
parameter_list|()
function_decl|;
comment|/** @return preferred block size in bytes */
DECL|method|getPreferredBlockSize ()
specifier|public
name|long
name|getPreferredBlockSize
parameter_list|()
function_decl|;
DECL|method|getLazyPersistFlag ()
specifier|public
name|boolean
name|getLazyPersistFlag
parameter_list|()
function_decl|;
comment|/** @return the header as a long. */
DECL|method|getHeaderLong ()
specifier|public
name|long
name|getHeaderLong
parameter_list|()
function_decl|;
DECL|method|metadataEquals (INodeFileAttributes other)
specifier|public
name|boolean
name|metadataEquals
parameter_list|(
name|INodeFileAttributes
name|other
parameter_list|)
function_decl|;
comment|/** A copy of the inode file attributes */
DECL|class|SnapshotCopy
specifier|public
specifier|static
class|class
name|SnapshotCopy
extends|extends
name|INodeAttributes
operator|.
name|SnapshotCopy
implements|implements
name|INodeFileAttributes
block|{
DECL|field|header
specifier|private
specifier|final
name|long
name|header
decl_stmt|;
DECL|method|SnapshotCopy (byte[] name, PermissionStatus permissions, AclFeature aclFeature, long modificationTime, long accessTime, short replication, long preferredBlockSize, boolean isTransient, XAttrFeature xAttrsFeature)
specifier|public
name|SnapshotCopy
parameter_list|(
name|byte
index|[]
name|name
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|,
name|AclFeature
name|aclFeature
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|accessTime
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|preferredBlockSize
parameter_list|,
name|boolean
name|isTransient
parameter_list|,
name|XAttrFeature
name|xAttrsFeature
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|permissions
argument_list|,
name|aclFeature
argument_list|,
name|modificationTime
argument_list|,
name|accessTime
argument_list|,
name|xAttrsFeature
argument_list|)
expr_stmt|;
name|header
operator|=
name|HeaderFormat
operator|.
name|toLong
argument_list|(
name|preferredBlockSize
argument_list|,
name|replication
argument_list|,
name|isTransient
argument_list|)
expr_stmt|;
block|}
DECL|method|SnapshotCopy (INodeFile file)
specifier|public
name|SnapshotCopy
parameter_list|(
name|INodeFile
name|file
parameter_list|)
block|{
name|super
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|header
operator|=
name|file
operator|.
name|getHeaderLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFileReplication ()
specifier|public
name|short
name|getFileReplication
parameter_list|()
block|{
return|return
name|HeaderFormat
operator|.
name|getReplication
argument_list|(
name|header
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPreferredBlockSize ()
specifier|public
name|long
name|getPreferredBlockSize
parameter_list|()
block|{
return|return
name|HeaderFormat
operator|.
name|getPreferredBlockSize
argument_list|(
name|header
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLazyPersistFlag ()
specifier|public
name|boolean
name|getLazyPersistFlag
parameter_list|()
block|{
return|return
name|HeaderFormat
operator|.
name|getLazyPersistFlag
argument_list|(
name|header
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHeaderLong ()
specifier|public
name|long
name|getHeaderLong
parameter_list|()
block|{
return|return
name|header
return|;
block|}
annotation|@
name|Override
DECL|method|metadataEquals (INodeFileAttributes other)
specifier|public
name|boolean
name|metadataEquals
parameter_list|(
name|INodeFileAttributes
name|other
parameter_list|)
block|{
return|return
name|other
operator|!=
literal|null
operator|&&
name|getHeaderLong
argument_list|()
operator|==
name|other
operator|.
name|getHeaderLong
argument_list|()
operator|&&
name|getPermissionLong
argument_list|()
operator|==
name|other
operator|.
name|getPermissionLong
argument_list|()
operator|&&
name|getAclFeature
argument_list|()
operator|==
name|other
operator|.
name|getAclFeature
argument_list|()
operator|&&
name|getXAttrFeature
argument_list|()
operator|==
name|other
operator|.
name|getXAttrFeature
argument_list|()
return|;
block|}
block|}
block|}
end_interface

end_unit

