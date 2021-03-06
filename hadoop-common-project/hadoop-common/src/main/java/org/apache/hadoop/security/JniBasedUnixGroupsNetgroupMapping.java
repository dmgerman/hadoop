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
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|util
operator|.
name|NativeCodeLoader
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
name|security
operator|.
name|NetgroupCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A JNI-based implementation of {@link GroupMappingServiceProvider}   * that invokes libC calls to get the group  * memberships of a given user.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|JniBasedUnixGroupsNetgroupMapping
specifier|public
class|class
name|JniBasedUnixGroupsNetgroupMapping
extends|extends
name|JniBasedUnixGroupsMapping
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JniBasedUnixGroupsNetgroupMapping
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getUsersForNetgroupJNI (String group)
specifier|native
name|String
index|[]
name|getUsersForNetgroupJNI
parameter_list|(
name|String
name|group
parameter_list|)
function_decl|;
static|static
block|{
if|if
condition|(
operator|!
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bailing out since native library couldn't "
operator|+
literal|"be loaded"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using JniBasedUnixGroupsNetgroupMapping for Netgroup resolution"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets unix groups and netgroups for the user.    *    * It gets all unix groups as returned by id -Gn but it    * only returns netgroups that are used in ACLs (there is    * no way to get all netgroups for a given user, see    * documentation for getent netgroup)    */
annotation|@
name|Override
DECL|method|getGroups (String user)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
comment|// parent gets unix groups
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|(
name|super
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
argument_list|)
decl_stmt|;
name|NetgroupCache
operator|.
name|getNetgroups
argument_list|(
name|user
argument_list|,
name|groups
argument_list|)
expr_stmt|;
return|return
name|groups
return|;
block|}
comment|/**    * Refresh the netgroup cache    */
annotation|@
name|Override
DECL|method|cacheGroupsRefresh ()
specifier|public
name|void
name|cacheGroupsRefresh
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|NetgroupCache
operator|.
name|getNetgroupNames
argument_list|()
decl_stmt|;
name|NetgroupCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cacheGroupsAdd
argument_list|(
name|groups
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a group to cache, only netgroups are cached    *    * @param groups list of group names to add to cache    */
annotation|@
name|Override
DECL|method|cacheGroupsAdd (List<String> groups)
specifier|public
name|void
name|cacheGroupsAdd
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groups
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|group
range|:
name|groups
control|)
block|{
if|if
condition|(
name|group
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// better safe than sorry (should never happen)
block|}
elseif|else
if|if
condition|(
name|group
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'@'
condition|)
block|{
if|if
condition|(
operator|!
name|NetgroupCache
operator|.
name|isCached
argument_list|(
name|group
argument_list|)
condition|)
block|{
name|NetgroupCache
operator|.
name|add
argument_list|(
name|group
argument_list|,
name|getUsersForNetgroup
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// unix group, not caching
block|}
block|}
block|}
comment|/**    * Calls JNI function to get users for a netgroup, since C functions    * are not reentrant we need to make this synchronized (see    * documentation for setnetgrent, getnetgrent and endnetgrent)    *    * @param netgroup return users for this netgroup    * @return list of users for a given netgroup    */
DECL|method|getUsersForNetgroup (String netgroup)
specifier|protected
specifier|synchronized
name|List
argument_list|<
name|String
argument_list|>
name|getUsersForNetgroup
parameter_list|(
name|String
name|netgroup
parameter_list|)
block|{
name|String
index|[]
name|users
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// JNI code does not expect '@' at the beginning of the group name
name|users
operator|=
name|getUsersForNetgroupJNI
argument_list|(
name|netgroup
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error getting users for netgroup "
operator|+
name|netgroup
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error getting users for netgroup "
operator|+
name|netgroup
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|users
operator|!=
literal|null
operator|&&
name|users
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|users
argument_list|)
return|;
block|}
return|return
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
return|;
block|}
block|}
end_class

end_unit

