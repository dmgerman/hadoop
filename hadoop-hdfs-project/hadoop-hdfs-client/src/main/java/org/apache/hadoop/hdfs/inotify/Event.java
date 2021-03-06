begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.inotify
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|inotify
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
name|classification
operator|.
name|InterfaceStability
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
name|XAttr
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
name|AclEntry
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * Events sent by the inotify system. Note that no events are necessarily sent  * when a file is opened for read (although a MetadataUpdateEvent will be sent  * if the atime is updated).  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Event
specifier|public
specifier|abstract
class|class
name|Event
block|{
DECL|enum|EventType
specifier|public
enum|enum
name|EventType
block|{
DECL|enumConstant|CREATE
DECL|enumConstant|CLOSE
DECL|enumConstant|APPEND
DECL|enumConstant|RENAME
DECL|enumConstant|METADATA
DECL|enumConstant|UNLINK
DECL|enumConstant|TRUNCATE
name|CREATE
block|,
name|CLOSE
block|,
name|APPEND
block|,
name|RENAME
block|,
name|METADATA
block|,
name|UNLINK
block|,
name|TRUNCATE
block|}
DECL|field|eventType
specifier|private
name|EventType
name|eventType
decl_stmt|;
DECL|method|getEventType ()
specifier|public
name|EventType
name|getEventType
parameter_list|()
block|{
return|return
name|eventType
return|;
block|}
DECL|method|Event (EventType eventType)
specifier|public
name|Event
parameter_list|(
name|EventType
name|eventType
parameter_list|)
block|{
name|this
operator|.
name|eventType
operator|=
name|eventType
expr_stmt|;
block|}
comment|/**    * Sent when a file is closed after append or create.    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|CloseEvent
specifier|public
specifier|static
class|class
name|CloseEvent
extends|extends
name|Event
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|fileSize
specifier|private
name|long
name|fileSize
decl_stmt|;
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|method|CloseEvent (String path, long fileSize, long timestamp)
specifier|public
name|CloseEvent
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|fileSize
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|super
argument_list|(
name|EventType
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|fileSize
operator|=
name|fileSize
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * The size of the closed file in bytes. May be -1 if the size is not      * available (e.g. in the case of a close generated by a concat operation).      */
DECL|method|getFileSize ()
specifier|public
name|long
name|getFileSize
parameter_list|()
block|{
return|return
name|fileSize
return|;
block|}
comment|/**      * The time when this event occurred, in milliseconds since the epoch.      */
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CloseEvent [path="
operator|+
name|path
operator|+
literal|", fileSize="
operator|+
name|fileSize
operator|+
literal|", timestamp="
operator|+
name|timestamp
operator|+
literal|"]"
return|;
block|}
block|}
comment|/**    * Sent when a new file is created (including overwrite).    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|CreateEvent
specifier|public
specifier|static
class|class
name|CreateEvent
extends|extends
name|Event
block|{
DECL|enum|INodeType
specifier|public
enum|enum
name|INodeType
block|{
DECL|enumConstant|FILE
DECL|enumConstant|DIRECTORY
DECL|enumConstant|SYMLINK
name|FILE
block|,
name|DIRECTORY
block|,
name|SYMLINK
block|}
DECL|field|iNodeType
specifier|private
name|INodeType
name|iNodeType
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|ctime
specifier|private
name|long
name|ctime
decl_stmt|;
DECL|field|replication
specifier|private
name|int
name|replication
decl_stmt|;
DECL|field|ownerName
specifier|private
name|String
name|ownerName
decl_stmt|;
DECL|field|groupName
specifier|private
name|String
name|groupName
decl_stmt|;
DECL|field|perms
specifier|private
name|FsPermission
name|perms
decl_stmt|;
DECL|field|symlinkTarget
specifier|private
name|String
name|symlinkTarget
decl_stmt|;
DECL|field|overwrite
specifier|private
name|boolean
name|overwrite
decl_stmt|;
DECL|field|defaultBlockSize
specifier|private
name|long
name|defaultBlockSize
decl_stmt|;
DECL|field|erasureCoded
specifier|private
name|Optional
argument_list|<
name|Boolean
argument_list|>
name|erasureCoded
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|iNodeType
specifier|private
name|INodeType
name|iNodeType
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|ctime
specifier|private
name|long
name|ctime
decl_stmt|;
DECL|field|replication
specifier|private
name|int
name|replication
decl_stmt|;
DECL|field|ownerName
specifier|private
name|String
name|ownerName
decl_stmt|;
DECL|field|groupName
specifier|private
name|String
name|groupName
decl_stmt|;
DECL|field|perms
specifier|private
name|FsPermission
name|perms
decl_stmt|;
DECL|field|symlinkTarget
specifier|private
name|String
name|symlinkTarget
decl_stmt|;
DECL|field|overwrite
specifier|private
name|boolean
name|overwrite
decl_stmt|;
DECL|field|defaultBlockSize
specifier|private
name|long
name|defaultBlockSize
init|=
literal|0
decl_stmt|;
DECL|field|erasureCoded
specifier|private
name|Optional
argument_list|<
name|Boolean
argument_list|>
name|erasureCoded
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|method|iNodeType (INodeType type)
specifier|public
name|Builder
name|iNodeType
parameter_list|(
name|INodeType
name|type
parameter_list|)
block|{
name|this
operator|.
name|iNodeType
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|path (String path)
specifier|public
name|Builder
name|path
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|ctime (long ctime)
specifier|public
name|Builder
name|ctime
parameter_list|(
name|long
name|ctime
parameter_list|)
block|{
name|this
operator|.
name|ctime
operator|=
name|ctime
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|replication (int replication)
specifier|public
name|Builder
name|replication
parameter_list|(
name|int
name|replication
parameter_list|)
block|{
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|ownerName (String ownerName)
specifier|public
name|Builder
name|ownerName
parameter_list|(
name|String
name|ownerName
parameter_list|)
block|{
name|this
operator|.
name|ownerName
operator|=
name|ownerName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|groupName (String groupName)
specifier|public
name|Builder
name|groupName
parameter_list|(
name|String
name|groupName
parameter_list|)
block|{
name|this
operator|.
name|groupName
operator|=
name|groupName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|perms (FsPermission perms)
specifier|public
name|Builder
name|perms
parameter_list|(
name|FsPermission
name|perms
parameter_list|)
block|{
name|this
operator|.
name|perms
operator|=
name|perms
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|symlinkTarget (String symlinkTarget)
specifier|public
name|Builder
name|symlinkTarget
parameter_list|(
name|String
name|symlinkTarget
parameter_list|)
block|{
name|this
operator|.
name|symlinkTarget
operator|=
name|symlinkTarget
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|overwrite (boolean overwrite)
specifier|public
name|Builder
name|overwrite
parameter_list|(
name|boolean
name|overwrite
parameter_list|)
block|{
name|this
operator|.
name|overwrite
operator|=
name|overwrite
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|defaultBlockSize (long defaultBlockSize)
specifier|public
name|Builder
name|defaultBlockSize
parameter_list|(
name|long
name|defaultBlockSize
parameter_list|)
block|{
name|this
operator|.
name|defaultBlockSize
operator|=
name|defaultBlockSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|erasureCoded (boolean ecCoded)
specifier|public
name|Builder
name|erasureCoded
parameter_list|(
name|boolean
name|ecCoded
parameter_list|)
block|{
name|this
operator|.
name|erasureCoded
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|ecCoded
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|CreateEvent
name|build
parameter_list|()
block|{
return|return
operator|new
name|CreateEvent
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|CreateEvent (Builder b)
specifier|private
name|CreateEvent
parameter_list|(
name|Builder
name|b
parameter_list|)
block|{
name|super
argument_list|(
name|EventType
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|this
operator|.
name|iNodeType
operator|=
name|b
operator|.
name|iNodeType
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|b
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|ctime
operator|=
name|b
operator|.
name|ctime
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|b
operator|.
name|replication
expr_stmt|;
name|this
operator|.
name|ownerName
operator|=
name|b
operator|.
name|ownerName
expr_stmt|;
name|this
operator|.
name|groupName
operator|=
name|b
operator|.
name|groupName
expr_stmt|;
name|this
operator|.
name|perms
operator|=
name|b
operator|.
name|perms
expr_stmt|;
name|this
operator|.
name|symlinkTarget
operator|=
name|b
operator|.
name|symlinkTarget
expr_stmt|;
name|this
operator|.
name|overwrite
operator|=
name|b
operator|.
name|overwrite
expr_stmt|;
name|this
operator|.
name|defaultBlockSize
operator|=
name|b
operator|.
name|defaultBlockSize
expr_stmt|;
name|this
operator|.
name|erasureCoded
operator|=
name|b
operator|.
name|erasureCoded
expr_stmt|;
block|}
DECL|method|getiNodeType ()
specifier|public
name|INodeType
name|getiNodeType
parameter_list|()
block|{
return|return
name|iNodeType
return|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * Creation time of the file, directory, or symlink.      */
DECL|method|getCtime ()
specifier|public
name|long
name|getCtime
parameter_list|()
block|{
return|return
name|ctime
return|;
block|}
comment|/**      * Replication is zero if the CreateEvent iNodeType is directory or symlink.      */
DECL|method|getReplication ()
specifier|public
name|int
name|getReplication
parameter_list|()
block|{
return|return
name|replication
return|;
block|}
DECL|method|getOwnerName ()
specifier|public
name|String
name|getOwnerName
parameter_list|()
block|{
return|return
name|ownerName
return|;
block|}
DECL|method|getGroupName ()
specifier|public
name|String
name|getGroupName
parameter_list|()
block|{
return|return
name|groupName
return|;
block|}
DECL|method|getPerms ()
specifier|public
name|FsPermission
name|getPerms
parameter_list|()
block|{
return|return
name|perms
return|;
block|}
comment|/**      * Symlink target is null if the CreateEvent iNodeType is not symlink.      */
DECL|method|getSymlinkTarget ()
specifier|public
name|String
name|getSymlinkTarget
parameter_list|()
block|{
return|return
name|symlinkTarget
return|;
block|}
DECL|method|getOverwrite ()
specifier|public
name|boolean
name|getOverwrite
parameter_list|()
block|{
return|return
name|overwrite
return|;
block|}
DECL|method|getDefaultBlockSize ()
specifier|public
name|long
name|getDefaultBlockSize
parameter_list|()
block|{
return|return
name|defaultBlockSize
return|;
block|}
DECL|method|isErasureCoded ()
specifier|public
name|Optional
argument_list|<
name|Boolean
argument_list|>
name|isErasureCoded
parameter_list|()
block|{
return|return
name|erasureCoded
return|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|content
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|content
operator|.
name|append
argument_list|(
literal|"CreateEvent [INodeType="
argument_list|)
operator|.
name|append
argument_list|(
name|iNodeType
argument_list|)
operator|.
name|append
argument_list|(
literal|", path="
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|", ctime="
argument_list|)
operator|.
name|append
argument_list|(
name|ctime
argument_list|)
operator|.
name|append
argument_list|(
literal|", replication="
argument_list|)
operator|.
name|append
argument_list|(
name|replication
argument_list|)
operator|.
name|append
argument_list|(
literal|", ownerName="
argument_list|)
operator|.
name|append
argument_list|(
name|ownerName
argument_list|)
operator|.
name|append
argument_list|(
literal|", groupName="
argument_list|)
operator|.
name|append
argument_list|(
name|groupName
argument_list|)
operator|.
name|append
argument_list|(
literal|", perms="
argument_list|)
operator|.
name|append
argument_list|(
name|perms
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
if|if
condition|(
name|symlinkTarget
operator|!=
literal|null
condition|)
block|{
name|content
operator|.
name|append
argument_list|(
literal|"symlinkTarget="
argument_list|)
operator|.
name|append
argument_list|(
name|symlinkTarget
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|content
operator|.
name|append
argument_list|(
literal|"overwrite="
argument_list|)
operator|.
name|append
argument_list|(
name|overwrite
argument_list|)
operator|.
name|append
argument_list|(
literal|", defaultBlockSize="
argument_list|)
operator|.
name|append
argument_list|(
name|defaultBlockSize
argument_list|)
operator|.
name|append
argument_list|(
literal|", erasureCoded="
argument_list|)
operator|.
name|append
argument_list|(
name|erasureCoded
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|content
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Sent when there is an update to directory or file (none of the metadata    * tracked here applies to symlinks) that is not associated with another    * inotify event. The tracked metadata includes atime/mtime, replication,    * owner/group, permissions, ACLs, and XAttributes. Fields not relevant to the    * metadataType of the MetadataUpdateEvent will be null or will have their default    * values.    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|MetadataUpdateEvent
specifier|public
specifier|static
class|class
name|MetadataUpdateEvent
extends|extends
name|Event
block|{
DECL|enum|MetadataType
specifier|public
enum|enum
name|MetadataType
block|{
DECL|enumConstant|TIMES
DECL|enumConstant|REPLICATION
DECL|enumConstant|OWNER
DECL|enumConstant|PERMS
DECL|enumConstant|ACLS
DECL|enumConstant|XATTRS
name|TIMES
block|,
name|REPLICATION
block|,
name|OWNER
block|,
name|PERMS
block|,
name|ACLS
block|,
name|XATTRS
block|}
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|metadataType
specifier|private
name|MetadataType
name|metadataType
decl_stmt|;
DECL|field|mtime
specifier|private
name|long
name|mtime
decl_stmt|;
DECL|field|atime
specifier|private
name|long
name|atime
decl_stmt|;
DECL|field|replication
specifier|private
name|int
name|replication
decl_stmt|;
DECL|field|ownerName
specifier|private
name|String
name|ownerName
decl_stmt|;
DECL|field|groupName
specifier|private
name|String
name|groupName
decl_stmt|;
DECL|field|perms
specifier|private
name|FsPermission
name|perms
decl_stmt|;
DECL|field|acls
specifier|private
name|List
argument_list|<
name|AclEntry
argument_list|>
name|acls
decl_stmt|;
DECL|field|xAttrs
specifier|private
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
decl_stmt|;
DECL|field|xAttrsRemoved
specifier|private
name|boolean
name|xAttrsRemoved
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|metadataType
specifier|private
name|MetadataType
name|metadataType
decl_stmt|;
DECL|field|mtime
specifier|private
name|long
name|mtime
decl_stmt|;
DECL|field|atime
specifier|private
name|long
name|atime
decl_stmt|;
DECL|field|replication
specifier|private
name|int
name|replication
decl_stmt|;
DECL|field|ownerName
specifier|private
name|String
name|ownerName
decl_stmt|;
DECL|field|groupName
specifier|private
name|String
name|groupName
decl_stmt|;
DECL|field|perms
specifier|private
name|FsPermission
name|perms
decl_stmt|;
DECL|field|acls
specifier|private
name|List
argument_list|<
name|AclEntry
argument_list|>
name|acls
decl_stmt|;
DECL|field|xAttrs
specifier|private
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
decl_stmt|;
DECL|field|xAttrsRemoved
specifier|private
name|boolean
name|xAttrsRemoved
decl_stmt|;
DECL|method|path (String path)
specifier|public
name|Builder
name|path
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|metadataType (MetadataType type)
specifier|public
name|Builder
name|metadataType
parameter_list|(
name|MetadataType
name|type
parameter_list|)
block|{
name|this
operator|.
name|metadataType
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|mtime (long mtime)
specifier|public
name|Builder
name|mtime
parameter_list|(
name|long
name|mtime
parameter_list|)
block|{
name|this
operator|.
name|mtime
operator|=
name|mtime
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|atime (long atime)
specifier|public
name|Builder
name|atime
parameter_list|(
name|long
name|atime
parameter_list|)
block|{
name|this
operator|.
name|atime
operator|=
name|atime
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|replication (int replication)
specifier|public
name|Builder
name|replication
parameter_list|(
name|int
name|replication
parameter_list|)
block|{
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|ownerName (String ownerName)
specifier|public
name|Builder
name|ownerName
parameter_list|(
name|String
name|ownerName
parameter_list|)
block|{
name|this
operator|.
name|ownerName
operator|=
name|ownerName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|groupName (String groupName)
specifier|public
name|Builder
name|groupName
parameter_list|(
name|String
name|groupName
parameter_list|)
block|{
name|this
operator|.
name|groupName
operator|=
name|groupName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|perms (FsPermission perms)
specifier|public
name|Builder
name|perms
parameter_list|(
name|FsPermission
name|perms
parameter_list|)
block|{
name|this
operator|.
name|perms
operator|=
name|perms
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|acls (List<AclEntry> acls)
specifier|public
name|Builder
name|acls
parameter_list|(
name|List
argument_list|<
name|AclEntry
argument_list|>
name|acls
parameter_list|)
block|{
name|this
operator|.
name|acls
operator|=
name|acls
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|xAttrs (List<XAttr> xAttrs)
specifier|public
name|Builder
name|xAttrs
parameter_list|(
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
parameter_list|)
block|{
name|this
operator|.
name|xAttrs
operator|=
name|xAttrs
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|xAttrsRemoved (boolean xAttrsRemoved)
specifier|public
name|Builder
name|xAttrsRemoved
parameter_list|(
name|boolean
name|xAttrsRemoved
parameter_list|)
block|{
name|this
operator|.
name|xAttrsRemoved
operator|=
name|xAttrsRemoved
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|MetadataUpdateEvent
name|build
parameter_list|()
block|{
return|return
operator|new
name|MetadataUpdateEvent
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|MetadataUpdateEvent (Builder b)
specifier|private
name|MetadataUpdateEvent
parameter_list|(
name|Builder
name|b
parameter_list|)
block|{
name|super
argument_list|(
name|EventType
operator|.
name|METADATA
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|b
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|metadataType
operator|=
name|b
operator|.
name|metadataType
expr_stmt|;
name|this
operator|.
name|mtime
operator|=
name|b
operator|.
name|mtime
expr_stmt|;
name|this
operator|.
name|atime
operator|=
name|b
operator|.
name|atime
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|b
operator|.
name|replication
expr_stmt|;
name|this
operator|.
name|ownerName
operator|=
name|b
operator|.
name|ownerName
expr_stmt|;
name|this
operator|.
name|groupName
operator|=
name|b
operator|.
name|groupName
expr_stmt|;
name|this
operator|.
name|perms
operator|=
name|b
operator|.
name|perms
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|b
operator|.
name|acls
expr_stmt|;
name|this
operator|.
name|xAttrs
operator|=
name|b
operator|.
name|xAttrs
expr_stmt|;
name|this
operator|.
name|xAttrsRemoved
operator|=
name|b
operator|.
name|xAttrsRemoved
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getMetadataType ()
specifier|public
name|MetadataType
name|getMetadataType
parameter_list|()
block|{
return|return
name|metadataType
return|;
block|}
DECL|method|getMtime ()
specifier|public
name|long
name|getMtime
parameter_list|()
block|{
return|return
name|mtime
return|;
block|}
DECL|method|getAtime ()
specifier|public
name|long
name|getAtime
parameter_list|()
block|{
return|return
name|atime
return|;
block|}
DECL|method|getReplication ()
specifier|public
name|int
name|getReplication
parameter_list|()
block|{
return|return
name|replication
return|;
block|}
DECL|method|getOwnerName ()
specifier|public
name|String
name|getOwnerName
parameter_list|()
block|{
return|return
name|ownerName
return|;
block|}
DECL|method|getGroupName ()
specifier|public
name|String
name|getGroupName
parameter_list|()
block|{
return|return
name|groupName
return|;
block|}
DECL|method|getPerms ()
specifier|public
name|FsPermission
name|getPerms
parameter_list|()
block|{
return|return
name|perms
return|;
block|}
comment|/**      * The full set of ACLs currently associated with this file or directory.      * May be null if all ACLs were removed.      */
DECL|method|getAcls ()
specifier|public
name|List
argument_list|<
name|AclEntry
argument_list|>
name|getAcls
parameter_list|()
block|{
return|return
name|acls
return|;
block|}
DECL|method|getxAttrs ()
specifier|public
name|List
argument_list|<
name|XAttr
argument_list|>
name|getxAttrs
parameter_list|()
block|{
return|return
name|xAttrs
return|;
block|}
comment|/**      * Whether the xAttrs returned by getxAttrs() were removed (as opposed to      * added).      */
DECL|method|isxAttrsRemoved ()
specifier|public
name|boolean
name|isxAttrsRemoved
parameter_list|()
block|{
return|return
name|xAttrsRemoved
return|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|content
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|content
operator|.
name|append
argument_list|(
literal|"MetadataUpdateEvent [path="
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|", metadataType="
argument_list|)
operator|.
name|append
argument_list|(
name|metadataType
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|metadataType
condition|)
block|{
case|case
name|TIMES
case|:
name|content
operator|.
name|append
argument_list|(
literal|", mtime="
argument_list|)
operator|.
name|append
argument_list|(
name|mtime
argument_list|)
operator|.
name|append
argument_list|(
literal|", atime="
argument_list|)
operator|.
name|append
argument_list|(
name|atime
argument_list|)
expr_stmt|;
break|break;
case|case
name|REPLICATION
case|:
name|content
operator|.
name|append
argument_list|(
literal|", replication="
argument_list|)
operator|.
name|append
argument_list|(
name|replication
argument_list|)
expr_stmt|;
break|break;
case|case
name|OWNER
case|:
name|content
operator|.
name|append
argument_list|(
literal|", ownerName="
argument_list|)
operator|.
name|append
argument_list|(
name|ownerName
argument_list|)
operator|.
name|append
argument_list|(
literal|", groupName="
argument_list|)
operator|.
name|append
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
break|break;
case|case
name|PERMS
case|:
name|content
operator|.
name|append
argument_list|(
literal|", perms="
argument_list|)
operator|.
name|append
argument_list|(
name|perms
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACLS
case|:
name|content
operator|.
name|append
argument_list|(
literal|", acls="
argument_list|)
operator|.
name|append
argument_list|(
name|acls
argument_list|)
expr_stmt|;
break|break;
case|case
name|XATTRS
case|:
name|content
operator|.
name|append
argument_list|(
literal|", xAttrs="
argument_list|)
operator|.
name|append
argument_list|(
name|xAttrs
argument_list|)
operator|.
name|append
argument_list|(
literal|", xAttrsRemoved="
argument_list|)
operator|.
name|append
argument_list|(
name|xAttrsRemoved
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
name|content
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|content
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Sent when a file, directory, or symlink is renamed.    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|RenameEvent
specifier|public
specifier|static
class|class
name|RenameEvent
extends|extends
name|Event
block|{
DECL|field|srcPath
specifier|private
name|String
name|srcPath
decl_stmt|;
DECL|field|dstPath
specifier|private
name|String
name|dstPath
decl_stmt|;
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|srcPath
specifier|private
name|String
name|srcPath
decl_stmt|;
DECL|field|dstPath
specifier|private
name|String
name|dstPath
decl_stmt|;
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|method|srcPath (String srcPath)
specifier|public
name|Builder
name|srcPath
parameter_list|(
name|String
name|srcPath
parameter_list|)
block|{
name|this
operator|.
name|srcPath
operator|=
name|srcPath
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|dstPath (String dstPath)
specifier|public
name|Builder
name|dstPath
parameter_list|(
name|String
name|dstPath
parameter_list|)
block|{
name|this
operator|.
name|dstPath
operator|=
name|dstPath
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|timestamp (long timestamp)
specifier|public
name|Builder
name|timestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|RenameEvent
name|build
parameter_list|()
block|{
return|return
operator|new
name|RenameEvent
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|RenameEvent (Builder builder)
specifier|private
name|RenameEvent
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|EventType
operator|.
name|RENAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|srcPath
operator|=
name|builder
operator|.
name|srcPath
expr_stmt|;
name|this
operator|.
name|dstPath
operator|=
name|builder
operator|.
name|dstPath
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|builder
operator|.
name|timestamp
expr_stmt|;
block|}
DECL|method|getSrcPath ()
specifier|public
name|String
name|getSrcPath
parameter_list|()
block|{
return|return
name|srcPath
return|;
block|}
DECL|method|getDstPath ()
specifier|public
name|String
name|getDstPath
parameter_list|()
block|{
return|return
name|dstPath
return|;
block|}
comment|/**      * The time when this event occurred, in milliseconds since the epoch.      */
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"RenameEvent [srcPath="
operator|+
name|srcPath
operator|+
literal|", dstPath="
operator|+
name|dstPath
operator|+
literal|", timestamp="
operator|+
name|timestamp
operator|+
literal|"]"
return|;
block|}
block|}
comment|/**    * Sent when an existing file is opened for append.    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|AppendEvent
specifier|public
specifier|static
class|class
name|AppendEvent
extends|extends
name|Event
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|newBlock
specifier|private
name|boolean
name|newBlock
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|newBlock
specifier|private
name|boolean
name|newBlock
decl_stmt|;
DECL|method|path (String path)
specifier|public
name|Builder
name|path
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|newBlock (boolean newBlock)
specifier|public
name|Builder
name|newBlock
parameter_list|(
name|boolean
name|newBlock
parameter_list|)
block|{
name|this
operator|.
name|newBlock
operator|=
name|newBlock
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|AppendEvent
name|build
parameter_list|()
block|{
return|return
operator|new
name|AppendEvent
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|AppendEvent (Builder b)
specifier|private
name|AppendEvent
parameter_list|(
name|Builder
name|b
parameter_list|)
block|{
name|super
argument_list|(
name|EventType
operator|.
name|APPEND
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|b
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|newBlock
operator|=
name|b
operator|.
name|newBlock
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|toNewBlock ()
specifier|public
name|boolean
name|toNewBlock
parameter_list|()
block|{
return|return
name|newBlock
return|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AppendEvent [path="
operator|+
name|path
operator|+
literal|", newBlock="
operator|+
name|newBlock
operator|+
literal|"]"
return|;
block|}
block|}
comment|/**    * Sent when a file, directory, or symlink is deleted.    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|UnlinkEvent
specifier|public
specifier|static
class|class
name|UnlinkEvent
extends|extends
name|Event
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|method|path (String path)
specifier|public
name|Builder
name|path
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|timestamp (long timestamp)
specifier|public
name|Builder
name|timestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|UnlinkEvent
name|build
parameter_list|()
block|{
return|return
operator|new
name|UnlinkEvent
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|UnlinkEvent (Builder builder)
specifier|private
name|UnlinkEvent
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|EventType
operator|.
name|UNLINK
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|builder
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|builder
operator|.
name|timestamp
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * The time when this event occurred, in milliseconds since the epoch.      */
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"UnlinkEvent [path="
operator|+
name|path
operator|+
literal|", timestamp="
operator|+
name|timestamp
operator|+
literal|"]"
return|;
block|}
block|}
comment|/**    * Sent when a file is truncated.    */
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|TruncateEvent
specifier|public
specifier|static
class|class
name|TruncateEvent
extends|extends
name|Event
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|fileSize
specifier|private
name|long
name|fileSize
decl_stmt|;
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|method|TruncateEvent (String path, long fileSize, long timestamp)
specifier|public
name|TruncateEvent
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|fileSize
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|super
argument_list|(
name|EventType
operator|.
name|TRUNCATE
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|fileSize
operator|=
name|fileSize
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * The size of the truncated file in bytes.      */
DECL|method|getFileSize ()
specifier|public
name|long
name|getFileSize
parameter_list|()
block|{
return|return
name|fileSize
return|;
block|}
comment|/**      * The time when this event occurred, in milliseconds since the epoch.      */
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TruncateEvent [path="
operator|+
name|path
operator|+
literal|", fileSize="
operator|+
name|fileSize
operator|+
literal|", timestamp="
operator|+
name|timestamp
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit

