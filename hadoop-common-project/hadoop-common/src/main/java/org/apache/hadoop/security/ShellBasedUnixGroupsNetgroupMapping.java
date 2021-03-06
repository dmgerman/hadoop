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
name|LinkedList
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
name|util
operator|.
name|Shell
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
name|Shell
operator|.
name|ExitCodeException
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
comment|/**  * A simple shell-based implementation of {@link GroupMappingServiceProvider}   * that exec's the<code>groups</code> shell command to fetch the group  * memberships of a given user.  */
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
DECL|class|ShellBasedUnixGroupsNetgroupMapping
specifier|public
class|class
name|ShellBasedUnixGroupsNetgroupMapping
extends|extends
name|ShellBasedUnixGroupsMapping
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
name|ShellBasedUnixGroupsNetgroupMapping
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Get unix groups (parent) and netgroups for given user    *    * @param user get groups and netgroups for this user    * @return groups and netgroups for user    */
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
comment|// parent get unix groups
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
comment|/**    * Gets users for a netgroup    *    * @param netgroup return users for this netgroup    * @return list of users for a given netgroup    */
DECL|method|getUsersForNetgroup (String netgroup)
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getUsersForNetgroup
parameter_list|(
name|String
name|netgroup
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|users
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// returns a string similar to this:
comment|// group               ( , user, ) ( domain, user1, host.com )
name|String
name|usersRaw
init|=
name|execShellGetUserForNetgroup
argument_list|(
name|netgroup
argument_list|)
decl_stmt|;
comment|// get rid of spaces, makes splitting much easier
name|usersRaw
operator|=
name|usersRaw
operator|.
name|replaceAll
argument_list|(
literal|" +"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// remove netgroup name at the beginning of the string
name|usersRaw
operator|=
name|usersRaw
operator|.
name|replaceFirst
argument_list|(
name|netgroup
operator|.
name|replaceFirst
argument_list|(
literal|"@"
argument_list|,
literal|""
argument_list|)
operator|+
literal|"[()]+"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// split string into user infos
name|String
index|[]
name|userInfos
init|=
name|usersRaw
operator|.
name|split
argument_list|(
literal|"[()]+"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|userInfo
range|:
name|userInfos
control|)
block|{
comment|// userInfo: xxx,user,yyy (xxx, yyy can be empty strings)
comment|// get rid of everything before first and after last comma
name|String
name|user
init|=
name|userInfo
operator|.
name|replaceFirst
argument_list|(
literal|"[^,]*,"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|user
operator|=
name|user
operator|.
name|replaceFirst
argument_list|(
literal|",.*$"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// voila! got username!
name|users
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
return|return
name|users
return|;
block|}
comment|/**    * Calls shell to get users for a netgroup by calling getent    * netgroup, this is a low level function that just returns string    * that     *    * @param netgroup get users for this netgroup    * @return string of users for a given netgroup in getent netgroups format    */
DECL|method|execShellGetUserForNetgroup (final String netgroup)
specifier|protected
name|String
name|execShellGetUserForNetgroup
parameter_list|(
specifier|final
name|String
name|netgroup
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|result
init|=
literal|""
decl_stmt|;
try|try
block|{
comment|// shell command does not expect '@' at the beginning of the group name
name|result
operator|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|getUsersForNetgroupCommand
argument_list|(
name|netgroup
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitCodeException
name|e
parameter_list|)
block|{
comment|// if we didn't get the group - just return empty list;
name|LOG
operator|.
name|warn
argument_list|(
literal|"error getting users for netgroup "
operator|+
name|netgroup
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

