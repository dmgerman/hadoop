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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|conf
operator|.
name|Configuration
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
name|CommonConfigurationKeys
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
name|ReflectionUtils
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
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * A user-to-groups mapping service.  *   * {@link Groups} allows for server to get the various group memberships  * of a given user via the {@link #getGroups(String)} call, thus ensuring   * a consistent user-to-groups mapping and protects against vagaries of   * different mappings on servers and clients in a Hadoop cluster.   */
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
DECL|class|Groups
specifier|public
class|class
name|Groups
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Groups
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|impl
specifier|private
specifier|final
name|GroupMappingServiceProvider
name|impl
decl_stmt|;
DECL|field|userToGroupsMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CachedGroups
argument_list|>
name|userToGroupsMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|CachedGroups
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|cacheTimeout
specifier|private
specifier|final
name|long
name|cacheTimeout
decl_stmt|;
DECL|method|Groups (Configuration conf)
specifier|public
name|Groups
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|impl
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getClass
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_MAPPING
argument_list|,
name|ShellBasedUnixGroupsMapping
operator|.
name|class
argument_list|,
name|GroupMappingServiceProvider
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|cacheTimeout
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUPS_CACHE_SECS
argument_list|,
literal|5
operator|*
literal|60
argument_list|)
operator|*
literal|1000
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Group mapping impl="
operator|+
name|impl
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"; cacheTimeout="
operator|+
name|cacheTimeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the group memberships of a given user.    * @param user User's name    * @return the group memberships of the user    * @throws IOException    */
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
comment|// Return cached value if available
name|CachedGroups
name|groups
init|=
name|userToGroupsMap
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
comment|// if cache has a value and it hasn't expired
if|if
condition|(
name|groups
operator|!=
literal|null
operator|&&
operator|(
name|groups
operator|.
name|getTimestamp
argument_list|()
operator|+
name|cacheTimeout
operator|>
name|now
operator|)
condition|)
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
literal|"Returning cached groups for '"
operator|+
name|user
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
return|return
name|groups
operator|.
name|getGroups
argument_list|()
return|;
block|}
comment|// Create and cache user's groups
name|groups
operator|=
operator|new
name|CachedGroups
argument_list|(
name|impl
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|groups
operator|.
name|getGroups
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No groups found for user "
operator|+
name|user
argument_list|)
throw|;
block|}
name|userToGroupsMap
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|groups
argument_list|)
expr_stmt|;
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
literal|"Returning fetched groups for '"
operator|+
name|user
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
return|return
name|groups
operator|.
name|getGroups
argument_list|()
return|;
block|}
comment|/**    * Refresh all user-to-groups mappings.    */
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"clearing userToGroupsMap cache"
argument_list|)
expr_stmt|;
try|try
block|{
name|impl
operator|.
name|cacheGroupsRefresh
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error refreshing groups cache"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|userToGroupsMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Add groups to cache    *    * @param groups list of groups to add to cache    */
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
block|{
try|try
block|{
name|impl
operator|.
name|cacheGroupsAdd
argument_list|(
name|groups
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error caching groups"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class to hold the cached groups    */
DECL|class|CachedGroups
specifier|private
specifier|static
class|class
name|CachedGroups
block|{
DECL|field|timestamp
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|field|groups
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
comment|/**      * Create and initialize group cache      */
DECL|method|CachedGroups (List<String> groups)
name|CachedGroups
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groups
parameter_list|)
block|{
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns time of last cache update      *      * @return time of last cache update      */
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
comment|/**      * Get list of cached groups      *      * @return cached groups      */
DECL|method|getGroups ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|groups
return|;
block|}
block|}
DECL|field|GROUPS
specifier|private
specifier|static
name|Groups
name|GROUPS
init|=
literal|null
decl_stmt|;
comment|/**    * Get the groups being used to map user-to-groups.    * @return the groups being used to map user-to-groups.    */
DECL|method|getUserToGroupsMappingService ()
specifier|public
specifier|static
name|Groups
name|getUserToGroupsMappingService
parameter_list|()
block|{
return|return
name|getUserToGroupsMappingService
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the groups being used to map user-to-groups.    * @param conf    * @return the groups being used to map user-to-groups.    */
DECL|method|getUserToGroupsMappingService ( Configuration conf)
specifier|public
specifier|static
specifier|synchronized
name|Groups
name|getUserToGroupsMappingService
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|GROUPS
operator|==
literal|null
condition|)
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
literal|" Creating new Groups object"
argument_list|)
expr_stmt|;
block|}
name|GROUPS
operator|=
operator|new
name|Groups
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|GROUPS
return|;
block|}
block|}
end_class

end_unit

