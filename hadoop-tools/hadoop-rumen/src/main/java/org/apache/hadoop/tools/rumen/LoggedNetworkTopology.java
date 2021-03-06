begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|Comparator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonAnySetter
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
name|tools
operator|.
name|rumen
operator|.
name|datatypes
operator|.
name|NodeName
import|;
end_import

begin_comment
comment|/**  * A {@link LoggedNetworkTopology} represents a tree that in turn represents a  * hierarchy of hosts. The current version requires the tree to have all leaves  * at the same level.  *   * All of the public methods are simply accessors for the instance variables we  * want to write out in the JSON files.  *   */
end_comment

begin_class
DECL|class|LoggedNetworkTopology
specifier|public
class|class
name|LoggedNetworkTopology
implements|implements
name|DeepCompare
block|{
DECL|field|name
name|NodeName
name|name
decl_stmt|;
DECL|field|children
name|List
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<
name|LoggedNetworkTopology
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|alreadySeenAnySetterAttributes
specifier|static
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|alreadySeenAnySetterAttributes
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|LoggedNetworkTopology ()
specifier|public
name|LoggedNetworkTopology
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|// for input parameter ignored.
annotation|@
name|JsonAnySetter
DECL|method|setUnknownAttribute (String attributeName, Object ignored)
specifier|public
name|void
name|setUnknownAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|,
name|Object
name|ignored
parameter_list|)
block|{
if|if
condition|(
operator|!
name|alreadySeenAnySetterAttributes
operator|.
name|contains
argument_list|(
name|attributeName
argument_list|)
condition|)
block|{
name|alreadySeenAnySetterAttributes
operator|.
name|add
argument_list|(
name|attributeName
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"In LoggedJob, we saw the unknown attribute "
operator|+
name|attributeName
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * We need this because we have to sort the {@code children} field. That field    * is set-valued, but if we sort these fields we ensure that comparisons won't    * bogusly fail because the hash table happened to enumerate in a different    * order.    *     */
DECL|class|TopoSort
specifier|static
class|class
name|TopoSort
implements|implements
name|Comparator
argument_list|<
name|LoggedNetworkTopology
argument_list|>
implements|,
name|Serializable
block|{
DECL|method|compare (LoggedNetworkTopology t1, LoggedNetworkTopology t2)
specifier|public
name|int
name|compare
parameter_list|(
name|LoggedNetworkTopology
name|t1
parameter_list|,
name|LoggedNetworkTopology
name|t2
parameter_list|)
block|{
return|return
name|t1
operator|.
name|name
operator|.
name|getValue
argument_list|()
operator|.
name|compareTo
argument_list|(
name|t2
operator|.
name|name
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * @param hosts    *          a HashSet of the {@link ParsedHost}    * @param name    *          the name of this level's host [for recursive descent]    * @param level    *          the level number    */
DECL|method|LoggedNetworkTopology (Set<ParsedHost> hosts, String name, int level)
name|LoggedNetworkTopology
parameter_list|(
name|Set
argument_list|<
name|ParsedHost
argument_list|>
name|hosts
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|name
operator|=
name|NodeName
operator|.
name|ROOT
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|name
operator|=
operator|new
name|NodeName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|children
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|level
operator|<
name|ParsedHost
operator|.
name|numberOfDistances
argument_list|()
operator|-
literal|1
condition|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|HashSet
argument_list|<
name|ParsedHost
argument_list|>
argument_list|>
name|topologies
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|HashSet
argument_list|<
name|ParsedHost
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ParsedHost
argument_list|>
name|iter
init|=
name|hosts
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ParsedHost
name|host
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|thisComponent
init|=
name|host
operator|.
name|nameComponent
argument_list|(
name|level
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|ParsedHost
argument_list|>
name|thisSet
init|=
name|topologies
operator|.
name|get
argument_list|(
name|thisComponent
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisSet
operator|==
literal|null
condition|)
block|{
name|thisSet
operator|=
operator|new
name|HashSet
argument_list|<
name|ParsedHost
argument_list|>
argument_list|()
expr_stmt|;
name|topologies
operator|.
name|put
argument_list|(
name|thisComponent
argument_list|,
name|thisSet
argument_list|)
expr_stmt|;
block|}
name|thisSet
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
name|children
operator|=
operator|new
name|ArrayList
argument_list|<
name|LoggedNetworkTopology
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|HashSet
argument_list|<
name|ParsedHost
argument_list|>
argument_list|>
name|ent
range|:
name|topologies
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|children
operator|.
name|add
argument_list|(
operator|new
name|LoggedNetworkTopology
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|level
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// nothing to do here
block|}
block|}
DECL|method|LoggedNetworkTopology (Set<ParsedHost> hosts)
name|LoggedNetworkTopology
parameter_list|(
name|Set
argument_list|<
name|ParsedHost
argument_list|>
name|hosts
parameter_list|)
block|{
name|this
argument_list|(
name|hosts
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|NodeName
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
operator|new
name|NodeName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|getChildren ()
specifier|public
name|List
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|children
return|;
block|}
DECL|method|setChildren (List<LoggedNetworkTopology> children)
name|void
name|setChildren
parameter_list|(
name|List
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|children
parameter_list|)
block|{
name|this
operator|.
name|children
operator|=
name|children
expr_stmt|;
block|}
DECL|method|compare1 (List<LoggedNetworkTopology> c1, List<LoggedNetworkTopology> c2, TreePath loc, String eltname)
specifier|private
name|void
name|compare1
parameter_list|(
name|List
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|c1
parameter_list|,
name|List
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|c2
parameter_list|,
name|TreePath
name|loc
parameter_list|,
name|String
name|eltname
parameter_list|)
throws|throws
name|DeepInequalityException
block|{
if|if
condition|(
name|c1
operator|==
literal|null
operator|&&
name|c2
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|c1
operator|==
literal|null
operator|||
name|c2
operator|==
literal|null
operator|||
name|c1
operator|.
name|size
argument_list|()
operator|!=
name|c2
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DeepInequalityException
argument_list|(
name|eltname
operator|+
literal|" miscompared"
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|)
argument_list|)
throw|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|c1
argument_list|,
operator|new
name|TopoSort
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|c2
argument_list|,
operator|new
name|TopoSort
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|c1
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|c1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|deepCompare
argument_list|(
name|c2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deepCompare (DeepCompare comparand, TreePath loc)
specifier|public
name|void
name|deepCompare
parameter_list|(
name|DeepCompare
name|comparand
parameter_list|,
name|TreePath
name|loc
parameter_list|)
throws|throws
name|DeepInequalityException
block|{
if|if
condition|(
operator|!
operator|(
name|comparand
operator|instanceof
name|LoggedNetworkTopology
operator|)
condition|)
block|{
throw|throw
operator|new
name|DeepInequalityException
argument_list|(
literal|"comparand has wrong type"
argument_list|,
name|loc
argument_list|)
throw|;
block|}
name|LoggedNetworkTopology
name|other
init|=
operator|(
name|LoggedNetworkTopology
operator|)
name|comparand
decl_stmt|;
name|compare1
argument_list|(
name|children
argument_list|,
name|other
operator|.
name|children
argument_list|,
name|loc
argument_list|,
literal|"children"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

