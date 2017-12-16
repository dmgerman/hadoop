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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|FileStatus
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

begin_comment
comment|/**  * Pluggable class for mapping ownership and permissions from an external  * store to an FSImage.  */
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
DECL|class|UGIResolver
specifier|public
specifier|abstract
class|class
name|UGIResolver
block|{
DECL|field|USER_STRID_OFFSET
specifier|static
specifier|final
name|int
name|USER_STRID_OFFSET
init|=
literal|40
decl_stmt|;
DECL|field|GROUP_STRID_OFFSET
specifier|static
specifier|final
name|int
name|GROUP_STRID_OFFSET
init|=
literal|16
decl_stmt|;
DECL|field|USER_GROUP_STRID_MASK
specifier|static
specifier|final
name|long
name|USER_GROUP_STRID_MASK
init|=
operator|(
literal|1
operator|<<
literal|24
operator|)
operator|-
literal|1
decl_stmt|;
comment|/**    * Permission is serialized as a 64-bit long. [0:24):[25:48):[48:64) (in Big    * Endian).    * The first and the second parts are the string ids of the user and    * group name, and the last 16 bits are the permission bits.    * @param owner name of owner    * @param group name of group    * @param permission Permission octects    * @return FSImage encoding of permissions    */
DECL|method|buildPermissionStatus ( String owner, String group, short permission)
specifier|protected
specifier|final
name|long
name|buildPermissionStatus
parameter_list|(
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|short
name|permission
parameter_list|)
block|{
name|long
name|userId
init|=
name|users
operator|.
name|get
argument_list|(
name|owner
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0L
operator|!=
operator|(
operator|(
operator|~
name|USER_GROUP_STRID_MASK
operator|)
operator|&
name|userId
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"UID must fit in 24 bits"
argument_list|)
throw|;
block|}
name|long
name|groupId
init|=
name|groups
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0L
operator|!=
operator|(
operator|(
operator|~
name|USER_GROUP_STRID_MASK
operator|)
operator|&
name|groupId
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"GID must fit in 24 bits"
argument_list|)
throw|;
block|}
return|return
operator|(
operator|(
name|userId
operator|&
name|USER_GROUP_STRID_MASK
operator|)
operator|<<
name|USER_STRID_OFFSET
operator|)
operator||
operator|(
operator|(
name|groupId
operator|&
name|USER_GROUP_STRID_MASK
operator|)
operator|<<
name|GROUP_STRID_OFFSET
operator|)
operator||
name|permission
return|;
block|}
DECL|field|users
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|users
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|groups
decl_stmt|;
DECL|method|UGIResolver ()
specifier|public
name|UGIResolver
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|UGIResolver (Map<String, Integer> users, Map<String, Integer> groups)
name|UGIResolver
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|users
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|groups
parameter_list|)
block|{
name|this
operator|.
name|users
operator|=
name|users
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
block|}
DECL|method|ugiMap ()
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|ugiMap
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|ret
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|m
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|users
argument_list|,
name|groups
argument_list|)
control|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|e
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|s
init|=
name|ret
operator|.
name|put
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Duplicate mapping: "
operator|+
name|e
operator|.
name|getValue
argument_list|()
operator|+
literal|" "
operator|+
name|s
operator|+
literal|" "
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|addUser (String name)
specifier|public
specifier|abstract
name|void
name|addUser
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|addUser (String name, int id)
specifier|protected
name|void
name|addUser
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|Integer
name|uid
init|=
name|users
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|uid
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Duplicate mapping: "
operator|+
name|name
operator|+
literal|" "
operator|+
name|uid
operator|+
literal|" "
operator|+
name|id
argument_list|)
throw|;
block|}
block|}
DECL|method|addGroup (String name)
specifier|public
specifier|abstract
name|void
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|addGroup (String name, int id)
specifier|protected
name|void
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|Integer
name|gid
init|=
name|groups
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|gid
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Duplicate mapping: "
operator|+
name|name
operator|+
literal|" "
operator|+
name|gid
operator|+
literal|" "
operator|+
name|id
argument_list|)
throw|;
block|}
block|}
DECL|method|resetUGInfo ()
specifier|protected
name|void
name|resetUGInfo
parameter_list|()
block|{
name|users
operator|.
name|clear
argument_list|()
expr_stmt|;
name|groups
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|resolve (FileStatus s)
specifier|public
name|long
name|resolve
parameter_list|(
name|FileStatus
name|s
parameter_list|)
block|{
return|return
name|buildPermissionStatus
argument_list|(
name|user
argument_list|(
name|s
argument_list|)
argument_list|,
name|group
argument_list|(
name|s
argument_list|)
argument_list|,
name|permission
argument_list|(
name|s
argument_list|)
operator|.
name|toShort
argument_list|()
argument_list|)
return|;
block|}
DECL|method|user (FileStatus s)
specifier|public
name|String
name|user
parameter_list|(
name|FileStatus
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|getOwner
argument_list|()
return|;
block|}
DECL|method|group (FileStatus s)
specifier|public
name|String
name|group
parameter_list|(
name|FileStatus
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|getGroup
argument_list|()
return|;
block|}
DECL|method|permission (FileStatus s)
specifier|public
name|FsPermission
name|permission
parameter_list|(
name|FileStatus
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|getPermission
argument_list|()
return|;
block|}
block|}
end_class

end_unit

