begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
package|;
end_package

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

begin_comment
comment|/**  * A cached implementation of DNSToSwitchMapping that takes an  * raw DNSToSwitchMapping and stores the resolved network location in   * a cache. The following calls to a resolved network location  * will get its location from the cache.   *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|CachedDNSToSwitchMapping
specifier|public
class|class
name|CachedDNSToSwitchMapping
extends|extends
name|AbstractDNSToSwitchMapping
block|{
DECL|field|cache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|cache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * The uncached mapping    */
DECL|field|rawMapping
specifier|protected
specifier|final
name|DNSToSwitchMapping
name|rawMapping
decl_stmt|;
comment|/**    * cache a raw DNS mapping    * @param rawMapping the raw mapping to cache    */
DECL|method|CachedDNSToSwitchMapping (DNSToSwitchMapping rawMapping)
specifier|public
name|CachedDNSToSwitchMapping
parameter_list|(
name|DNSToSwitchMapping
name|rawMapping
parameter_list|)
block|{
name|this
operator|.
name|rawMapping
operator|=
name|rawMapping
expr_stmt|;
block|}
comment|/**    * @param names a list of hostnames to probe for being cached    * @return the hosts from 'names' that have not been cached previously    */
DECL|method|getUncachedHosts (List<String> names)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getUncachedHosts
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
comment|// find out all names without cached resolved location
name|List
argument_list|<
name|String
argument_list|>
name|unCachedHosts
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
if|if
condition|(
name|cache
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
name|unCachedHosts
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|unCachedHosts
return|;
block|}
comment|/**    * Caches the resolved host:rack mappings. The two list    * parameters must be of equal size.    *    * @param uncachedHosts a list of hosts that were uncached    * @param resolvedHosts a list of resolved host entries where the element    * at index(i) is the resolved value for the entry in uncachedHosts[i]    */
DECL|method|cacheResolvedHosts (List<String> uncachedHosts, List<String> resolvedHosts)
specifier|private
name|void
name|cacheResolvedHosts
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|uncachedHosts
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|resolvedHosts
parameter_list|)
block|{
comment|// Cache the result
if|if
condition|(
name|resolvedHosts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|uncachedHosts
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|uncachedHosts
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|resolvedHosts
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @param names a list of hostnames to look up (can be be empty)    * @return the cached resolution of the list of hostnames/addresses.    *  or null if any of the names are not currently in the cache    */
DECL|method|getCachedHosts (List<String> names)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getCachedHosts
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// Construct the result
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|String
name|networkLocation
init|=
name|cache
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|networkLocation
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|networkLocation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|resolve (List<String> names)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|resolve
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
comment|// normalize all input names to be in the form of IP addresses
name|names
operator|=
name|NetUtils
operator|.
name|normalizeHostNames
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|names
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|result
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|uncachedHosts
init|=
name|getUncachedHosts
argument_list|(
name|names
argument_list|)
decl_stmt|;
comment|// Resolve the uncached hosts
name|List
argument_list|<
name|String
argument_list|>
name|resolvedHosts
init|=
name|rawMapping
operator|.
name|resolve
argument_list|(
name|uncachedHosts
argument_list|)
decl_stmt|;
comment|//cache them
name|cacheResolvedHosts
argument_list|(
name|uncachedHosts
argument_list|,
name|resolvedHosts
argument_list|)
expr_stmt|;
comment|//now look up the entire list in the cache
return|return
name|getCachedHosts
argument_list|(
name|names
argument_list|)
return|;
block|}
comment|/**    * Get the (host x switch) map.    * @return a copy of the cached map of hosts to rack    */
annotation|@
name|Override
DECL|method|getSwitchMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSwitchMap
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|switchMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|cache
argument_list|)
decl_stmt|;
return|return
name|switchMap
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"cached switch mapping relaying to "
operator|+
name|rawMapping
return|;
block|}
comment|/**    * Delegate the switch topology query to the raw mapping, via    * {@link AbstractDNSToSwitchMapping#isMappingSingleSwitch(DNSToSwitchMapping)}    * @return true iff the raw mapper is considered single-switch.    */
annotation|@
name|Override
DECL|method|isSingleSwitch ()
specifier|public
name|boolean
name|isSingleSwitch
parameter_list|()
block|{
return|return
name|isMappingSingleSwitch
argument_list|(
name|rawMapping
argument_list|)
return|;
block|}
block|}
end_class

end_unit

