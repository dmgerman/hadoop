begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

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
name|CommonConfigurationKeysPublic
import|;
end_import

begin_comment
comment|/**  * An interface for the implementation of {@literal<}userId,  * userName{@literal>} mapping and {@literal<}groupId, groupName{@literal>}  * mapping.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|IdMappingServiceProvider
specifier|public
interface|interface
name|IdMappingServiceProvider
block|{
comment|// Return uid for given user name
DECL|method|getUid (String user)
specifier|public
name|int
name|getUid
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// Return gid for given group name
DECL|method|getGid (String group)
specifier|public
name|int
name|getGid
parameter_list|(
name|String
name|group
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// Return user name for given user id uid, if not found, return
comment|//<unknown> passed to this method
DECL|method|getUserName (int uid, String unknown)
specifier|public
name|String
name|getUserName
parameter_list|(
name|int
name|uid
parameter_list|,
name|String
name|unknown
parameter_list|)
function_decl|;
comment|// Return group name for given groupd id gid, if not found, return
comment|//<unknown> passed to this method
DECL|method|getGroupName (int gid, String unknown)
specifier|public
name|String
name|getGroupName
parameter_list|(
name|int
name|gid
parameter_list|,
name|String
name|unknown
parameter_list|)
function_decl|;
comment|// Return uid for given user name.
comment|// When can't map user, return user name's string hashcode
DECL|method|getUidAllowingUnknown (String user)
specifier|public
name|int
name|getUidAllowingUnknown
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|// Return gid for given group name.
comment|// When can't map group, return group name's string hashcode
DECL|method|getGidAllowingUnknown (String group)
specifier|public
name|int
name|getGidAllowingUnknown
parameter_list|(
name|String
name|group
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

