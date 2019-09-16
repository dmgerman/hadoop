begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|net
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|CollectionUtils
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Utility class to facilitate network topology functions.  */
end_comment

begin_class
DECL|class|NetUtils
specifier|public
specifier|final
class|class
name|NetUtils
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NetUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|NetUtils ()
specifier|private
name|NetUtils
parameter_list|()
block|{
comment|// Prevent instantiation
block|}
comment|/**    * Normalize a path by stripping off any trailing.    * {@link NetConstants#PATH_SEPARATOR}    * @param path path to normalize.    * @return the normalised path    * If<i>path</i>is empty or null, then {@link NetConstants#ROOT} is returned    */
DECL|method|normalize (String path)
specifier|public
specifier|static
name|String
name|normalize
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|NetConstants
operator|.
name|ROOT
return|;
block|}
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
name|NetConstants
operator|.
name|PATH_SEPARATOR
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Network Location path does not start with "
operator|+
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
operator|+
literal|": "
operator|+
name|path
argument_list|)
throw|;
block|}
comment|// Remove any trailing NetConstants.PATH_SEPARATOR
return|return
name|path
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|?
name|path
else|:
name|path
operator|.
name|replaceAll
argument_list|(
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
operator|+
literal|"+$"
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/**    *  Given a network topology location string, return its network topology    *  depth, E.g. the depth of /dc1/rack1/ng1/node1 is 5.    */
DECL|method|locationToDepth (String location)
specifier|public
specifier|static
name|int
name|locationToDepth
parameter_list|(
name|String
name|location
parameter_list|)
block|{
name|String
name|newLocation
init|=
name|normalize
argument_list|(
name|location
argument_list|)
decl_stmt|;
return|return
name|newLocation
operator|.
name|equals
argument_list|(
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
argument_list|)
condition|?
literal|1
else|:
name|newLocation
operator|.
name|split
argument_list|(
name|NetConstants
operator|.
name|PATH_SEPARATOR_STR
argument_list|)
operator|.
name|length
return|;
block|}
comment|/**    *  Remove node from mutableExcludedNodes if it's covered by excludedScope.    *  Please noted that mutableExcludedNodes content might be changed after the    *  function call.    */
DECL|method|removeDuplicate (NetworkTopology topology, Collection<Node> mutableExcludedNodes, List<String> mutableExcludedScopes, int ancestorGen)
specifier|public
specifier|static
name|void
name|removeDuplicate
parameter_list|(
name|NetworkTopology
name|topology
parameter_list|,
name|Collection
argument_list|<
name|Node
argument_list|>
name|mutableExcludedNodes
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|mutableExcludedScopes
parameter_list|,
name|int
name|ancestorGen
parameter_list|)
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|mutableExcludedNodes
argument_list|)
operator|||
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|mutableExcludedScopes
argument_list|)
operator|||
name|topology
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterator
argument_list|<
name|Node
argument_list|>
name|iterator
init|=
name|mutableExcludedNodes
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
operator|(
operator|!
name|mutableExcludedScopes
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|Node
name|node
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Node
name|ancestor
init|=
name|topology
operator|.
name|getAncestor
argument_list|(
name|node
argument_list|,
name|ancestorGen
argument_list|)
decl_stmt|;
if|if
condition|(
name|ancestor
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Fail to get ancestor generation "
operator|+
name|ancestorGen
operator|+
literal|" of node :"
operator|+
name|node
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// excludedScope is child of ancestor
name|List
argument_list|<
name|String
argument_list|>
name|duplicateList
init|=
name|mutableExcludedScopes
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|scope
lambda|->
name|scope
operator|.
name|startsWith
argument_list|(
name|ancestor
operator|.
name|getNetworkFullPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|mutableExcludedScopes
operator|.
name|removeAll
argument_list|(
name|duplicateList
argument_list|)
expr_stmt|;
comment|// ancestor is covered by excludedScope
name|mutableExcludedScopes
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|scope
lambda|->
block|{
if|if
condition|(
name|ancestor
operator|.
name|getNetworkFullPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|scope
argument_list|)
condition|)
block|{
comment|// remove exclude node if it's covered by excludedScope
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *  Remove node from mutableExcludedNodes if it's not part of scope    *  Please noted that mutableExcludedNodes content might be changed after the    *  function call.    */
DECL|method|removeOutscope (Collection<Node> mutableExcludedNodes, String scope)
specifier|public
specifier|static
name|void
name|removeOutscope
parameter_list|(
name|Collection
argument_list|<
name|Node
argument_list|>
name|mutableExcludedNodes
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|mutableExcludedNodes
argument_list|)
operator|||
name|scope
operator|==
literal|null
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|mutableExcludedNodes
init|)
block|{
name|Iterator
argument_list|<
name|Node
argument_list|>
name|iterator
init|=
name|mutableExcludedNodes
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|next
operator|.
name|getNetworkFullPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|scope
argument_list|)
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Get a ancestor list for nodes on generation<i>generation</i>.    *    * @param nodes a collection of leaf nodes    * @param generation  the ancestor generation    * @return the ancestor list. If no ancestor is found, then a empty list is    * returned.    */
DECL|method|getAncestorList (NetworkTopology topology, Collection<Node> nodes, int generation)
specifier|public
specifier|static
name|List
argument_list|<
name|Node
argument_list|>
name|getAncestorList
parameter_list|(
name|NetworkTopology
name|topology
parameter_list|,
name|Collection
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|int
name|generation
parameter_list|)
block|{
name|List
argument_list|<
name|Node
argument_list|>
name|ancestorList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|topology
operator|==
literal|null
operator|||
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|nodes
argument_list|)
operator|||
name|generation
operator|==
literal|0
condition|)
block|{
return|return
name|ancestorList
return|;
block|}
name|Iterator
argument_list|<
name|Node
argument_list|>
name|iterator
init|=
name|nodes
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|node
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Node
name|ancestor
init|=
name|topology
operator|.
name|getAncestor
argument_list|(
name|node
argument_list|,
name|generation
argument_list|)
decl_stmt|;
if|if
condition|(
name|ancestor
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Fail to get ancestor generation "
operator|+
name|generation
operator|+
literal|" of node :"
operator|+
name|node
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|ancestorList
operator|.
name|contains
argument_list|(
name|ancestor
argument_list|)
condition|)
block|{
name|ancestorList
operator|.
name|add
argument_list|(
name|ancestor
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ancestorList
return|;
block|}
block|}
end_class

end_unit

