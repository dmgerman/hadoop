begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ksm.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ksm
operator|.
name|helpers
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|KeySpaceManagerProtocolProtos
operator|.
name|OzoneAclInfo
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|KeySpaceManagerProtocolProtos
operator|.
name|OzoneAclInfo
operator|.
name|OzoneAclRights
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|KeySpaceManagerProtocolProtos
operator|.
name|OzoneAclInfo
operator|.
name|OzoneAclType
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
name|LinkedList
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
name|HashMap
import|;
end_import

begin_comment
comment|/**  * This helper class keeps a map of all user and their permissions.  */
end_comment

begin_class
DECL|class|KsmOzoneAclMap
specifier|public
class|class
name|KsmOzoneAclMap
block|{
comment|// per Acl Type user:rights map
DECL|field|aclMaps
specifier|private
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneAclRights
argument_list|>
argument_list|>
name|aclMaps
decl_stmt|;
DECL|method|KsmOzoneAclMap ()
name|KsmOzoneAclMap
parameter_list|()
block|{
name|aclMaps
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|OzoneAclType
name|aclType
range|:
name|OzoneAclType
operator|.
name|values
argument_list|()
control|)
block|{
name|aclMaps
operator|.
name|add
argument_list|(
name|aclType
operator|.
name|ordinal
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMap (OzoneAclType type)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneAclRights
argument_list|>
name|getMap
parameter_list|(
name|OzoneAclType
name|type
parameter_list|)
block|{
return|return
name|aclMaps
operator|.
name|get
argument_list|(
name|type
operator|.
name|ordinal
argument_list|()
argument_list|)
return|;
block|}
comment|// For a given acl type and user, get the stored acl
DECL|method|getAcl (OzoneAclType type, String user)
specifier|private
name|OzoneAclRights
name|getAcl
parameter_list|(
name|OzoneAclType
name|type
parameter_list|,
name|String
name|user
parameter_list|)
block|{
return|return
name|getMap
argument_list|(
name|type
argument_list|)
operator|.
name|get
argument_list|(
name|user
argument_list|)
return|;
block|}
comment|// Add a new acl to the map
DECL|method|addAcl (OzoneAclInfo acl)
specifier|public
name|void
name|addAcl
parameter_list|(
name|OzoneAclInfo
name|acl
parameter_list|)
block|{
name|getMap
argument_list|(
name|acl
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|acl
operator|.
name|getName
argument_list|()
argument_list|,
name|acl
operator|.
name|getRights
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// for a given acl, check if the user has access rights
DECL|method|hasAccess (OzoneAclInfo acl)
specifier|public
name|boolean
name|hasAccess
parameter_list|(
name|OzoneAclInfo
name|acl
parameter_list|)
block|{
name|OzoneAclRights
name|storedRights
init|=
name|getAcl
argument_list|(
name|acl
operator|.
name|getType
argument_list|()
argument_list|,
name|acl
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|storedRights
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|acl
operator|.
name|getRights
argument_list|()
condition|)
block|{
case|case
name|READ
case|:
return|return
operator|(
name|storedRights
operator|==
name|OzoneAclRights
operator|.
name|READ
operator|)
operator|||
operator|(
name|storedRights
operator|==
name|OzoneAclRights
operator|.
name|READ_WRITE
operator|)
return|;
case|case
name|WRITE
case|:
return|return
operator|(
name|storedRights
operator|==
name|OzoneAclRights
operator|.
name|WRITE
operator|)
operator|||
operator|(
name|storedRights
operator|==
name|OzoneAclRights
operator|.
name|READ_WRITE
operator|)
return|;
case|case
name|READ_WRITE
case|:
return|return
operator|(
name|storedRights
operator|==
name|OzoneAclRights
operator|.
name|READ_WRITE
operator|)
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Convert this map to OzoneAclInfo Protobuf List
DECL|method|ozoneAclGetProtobuf ()
specifier|public
name|List
argument_list|<
name|OzoneAclInfo
argument_list|>
name|ozoneAclGetProtobuf
parameter_list|()
block|{
name|List
argument_list|<
name|OzoneAclInfo
argument_list|>
name|aclList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|OzoneAclType
name|type
range|:
name|OzoneAclType
operator|.
name|values
argument_list|()
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
name|OzoneAclRights
argument_list|>
name|entry
range|:
name|aclMaps
operator|.
name|get
argument_list|(
name|type
operator|.
name|ordinal
argument_list|()
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|OzoneAclInfo
name|aclInfo
init|=
name|OzoneAclInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setRights
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|aclList
operator|.
name|add
argument_list|(
name|aclInfo
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|aclList
return|;
block|}
comment|// Create map from list of OzoneAclInfos
DECL|method|ozoneAclGetFromProtobuf ( List<OzoneAclInfo> aclList)
specifier|public
specifier|static
name|KsmOzoneAclMap
name|ozoneAclGetFromProtobuf
parameter_list|(
name|List
argument_list|<
name|OzoneAclInfo
argument_list|>
name|aclList
parameter_list|)
block|{
name|KsmOzoneAclMap
name|aclMap
init|=
operator|new
name|KsmOzoneAclMap
argument_list|()
decl_stmt|;
for|for
control|(
name|OzoneAclInfo
name|acl
range|:
name|aclList
control|)
block|{
name|aclMap
operator|.
name|addAcl
argument_list|(
name|acl
argument_list|)
expr_stmt|;
block|}
return|return
name|aclMap
return|;
block|}
block|}
end_class

end_unit

