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

begin_comment
comment|/** InnerNode represents a switch/router of a data center or rack.  * Different from a leaf node, it has non-null children.  */
end_comment

begin_class
DECL|class|InnerNodeImpl
specifier|public
class|class
name|InnerNodeImpl
extends|extends
name|NodeBase
implements|implements
name|InnerNode
block|{
DECL|class|Factory
specifier|protected
specifier|static
class|class
name|Factory
implements|implements
name|InnerNode
operator|.
name|Factory
argument_list|<
name|InnerNodeImpl
argument_list|>
block|{
DECL|method|Factory ()
specifier|protected
name|Factory
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|newInnerNode (String path)
specifier|public
name|InnerNodeImpl
name|newInnerNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
operator|new
name|InnerNodeImpl
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
DECL|field|FACTORY
specifier|static
specifier|final
name|Factory
name|FACTORY
init|=
operator|new
name|Factory
argument_list|()
decl_stmt|;
DECL|field|children
specifier|protected
specifier|final
name|List
argument_list|<
name|Node
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|childrenMap
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Node
argument_list|>
name|childrenMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|numOfLeaves
specifier|protected
name|int
name|numOfLeaves
decl_stmt|;
comment|/** Construct an InnerNode from a path-like string. */
DECL|method|InnerNodeImpl (String path)
specifier|protected
name|InnerNodeImpl
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/** Construct an InnerNode    * from its name, its network location, its parent, and its level. */
DECL|method|InnerNodeImpl (String name, String location, InnerNode parent, int level)
specifier|protected
name|InnerNodeImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|location
parameter_list|,
name|InnerNode
name|parent
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|location
argument_list|,
name|parent
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getChildren ()
specifier|public
name|List
argument_list|<
name|Node
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|children
return|;
block|}
comment|/** @return the number of children this node has. */
annotation|@
name|Override
DECL|method|getNumOfChildren ()
specifier|public
name|int
name|getNumOfChildren
parameter_list|()
block|{
return|return
name|children
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** Judge if this node represents a rack.    * @return true if it has no child or its children are not InnerNodes    */
DECL|method|isRack ()
specifier|public
name|boolean
name|isRack
parameter_list|()
block|{
if|if
condition|(
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Node
name|firstChild
init|=
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstChild
operator|instanceof
name|InnerNode
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Judge if this node is an ancestor of node<i>n</i>.    *    * @param n a node    * @return true if this node is an ancestor of<i>n</i>    */
DECL|method|isAncestor (Node n)
specifier|public
name|boolean
name|isAncestor
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
return|return
name|getPath
argument_list|(
name|this
argument_list|)
operator|.
name|equals
argument_list|(
name|NodeBase
operator|.
name|PATH_SEPARATOR_STR
argument_list|)
operator|||
operator|(
name|n
operator|.
name|getNetworkLocation
argument_list|()
operator|+
name|NodeBase
operator|.
name|PATH_SEPARATOR_STR
operator|)
operator|.
name|startsWith
argument_list|(
name|getPath
argument_list|(
name|this
argument_list|)
operator|+
name|NodeBase
operator|.
name|PATH_SEPARATOR_STR
argument_list|)
return|;
block|}
comment|/** Judge if this node is the parent of node<i>n</i>.    *    * @param n a node    * @return true if this node is the parent of<i>n</i>    */
DECL|method|isParent (Node n)
specifier|public
name|boolean
name|isParent
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
return|return
name|n
operator|.
name|getNetworkLocation
argument_list|()
operator|.
name|equals
argument_list|(
name|getPath
argument_list|(
name|this
argument_list|)
argument_list|)
return|;
block|}
comment|/* Return a child name of this node who is an ancestor of node<i>n</i> */
DECL|method|getNextAncestorName (Node n)
specifier|public
name|String
name|getNextAncestorName
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isAncestor
argument_list|(
name|n
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|this
operator|+
literal|"is not an ancestor of "
operator|+
name|n
argument_list|)
throw|;
block|}
name|String
name|name
init|=
name|n
operator|.
name|getNetworkLocation
argument_list|()
operator|.
name|substring
argument_list|(
name|getPath
argument_list|(
name|this
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|PATH_SEPARATOR
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|index
init|=
name|name
operator|.
name|indexOf
argument_list|(
name|PATH_SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|add (Node n)
specifier|public
name|boolean
name|add
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isAncestor
argument_list|(
name|n
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|n
operator|.
name|getName
argument_list|()
operator|+
literal|", which is located at "
operator|+
name|n
operator|.
name|getNetworkLocation
argument_list|()
operator|+
literal|", is not a descendant of "
operator|+
name|getPath
argument_list|(
name|this
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|isParent
argument_list|(
name|n
argument_list|)
condition|)
block|{
comment|// this node is the parent of n; add n directly
name|n
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|n
operator|.
name|setLevel
argument_list|(
name|this
operator|.
name|level
operator|+
literal|1
argument_list|)
expr_stmt|;
name|Node
name|prev
init|=
name|childrenMap
operator|.
name|put
argument_list|(
name|n
operator|.
name|getName
argument_list|()
argument_list|,
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|prev
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
name|children
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|n
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|children
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
name|children
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|numOfLeaves
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// find the next ancestor node
name|String
name|parentName
init|=
name|getNextAncestorName
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|InnerNode
name|parentNode
init|=
operator|(
name|InnerNode
operator|)
name|childrenMap
operator|.
name|get
argument_list|(
name|parentName
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentNode
operator|==
literal|null
condition|)
block|{
comment|// create a new InnerNode
name|parentNode
operator|=
name|createParentNode
argument_list|(
name|parentName
argument_list|)
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
name|parentNode
argument_list|)
expr_stmt|;
name|childrenMap
operator|.
name|put
argument_list|(
name|parentNode
operator|.
name|getName
argument_list|()
argument_list|,
name|parentNode
argument_list|)
expr_stmt|;
block|}
comment|// add n to the subtree of the next ancestor node
if|if
condition|(
name|parentNode
operator|.
name|add
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|numOfLeaves
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**    * Creates a parent node to be added to the list of children.    * Creates a node using the InnerNode four argument constructor specifying    * the name, location, parent, and level of this node.    *    *<p>To be overridden in subclasses for specific InnerNode implementations,    * as alternative to overriding the full {@link #add(Node)} method.    *    * @param parentName The name of the parent node    * @return A new inner node    * @see InnerNodeImpl(String, String, InnerNode, int)    */
DECL|method|createParentNode (String parentName)
specifier|private
name|InnerNodeImpl
name|createParentNode
parameter_list|(
name|String
name|parentName
parameter_list|)
block|{
return|return
operator|new
name|InnerNodeImpl
argument_list|(
name|parentName
argument_list|,
name|getPath
argument_list|(
name|this
argument_list|)
argument_list|,
name|this
argument_list|,
name|this
operator|.
name|getLevel
argument_list|()
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|remove (Node n)
specifier|public
name|boolean
name|remove
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isAncestor
argument_list|(
name|n
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|n
operator|.
name|getName
argument_list|()
operator|+
literal|", which is located at "
operator|+
name|n
operator|.
name|getNetworkLocation
argument_list|()
operator|+
literal|", is not a descendant of "
operator|+
name|getPath
argument_list|(
name|this
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|isParent
argument_list|(
name|n
argument_list|)
condition|)
block|{
comment|// this node is the parent of n; remove n directly
if|if
condition|(
name|childrenMap
operator|.
name|containsKey
argument_list|(
name|n
operator|.
name|getName
argument_list|()
argument_list|)
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
name|children
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|n
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|children
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|childrenMap
operator|.
name|remove
argument_list|(
name|n
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|numOfLeaves
operator|--
expr_stmt|;
name|n
operator|.
name|setParent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// find the next ancestor node: the parent node
name|String
name|parentName
init|=
name|getNextAncestorName
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|InnerNodeImpl
name|parentNode
init|=
operator|(
name|InnerNodeImpl
operator|)
name|childrenMap
operator|.
name|get
argument_list|(
name|parentName
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentNode
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// remove n from the parent node
name|boolean
name|isRemoved
init|=
name|parentNode
operator|.
name|remove
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// if the parent node has no children, remove the parent node too
if|if
condition|(
name|isRemoved
condition|)
block|{
if|if
condition|(
name|parentNode
operator|.
name|getNumOfChildren
argument_list|()
operator|==
literal|0
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
name|children
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|parentName
argument_list|)
condition|)
block|{
name|children
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|childrenMap
operator|.
name|remove
argument_list|(
name|parentName
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|numOfLeaves
operator|--
expr_stmt|;
block|}
return|return
name|isRemoved
return|;
block|}
block|}
comment|// end of remove
annotation|@
name|Override
DECL|method|getLoc (String loc)
specifier|public
name|Node
name|getLoc
parameter_list|(
name|String
name|loc
parameter_list|)
block|{
if|if
condition|(
name|loc
operator|==
literal|null
operator|||
name|loc
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|this
return|;
block|}
name|String
index|[]
name|path
init|=
name|loc
operator|.
name|split
argument_list|(
name|PATH_SEPARATOR_STR
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Node
name|childNode
init|=
name|childrenMap
operator|.
name|get
argument_list|(
name|path
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|childNode
operator|==
literal|null
operator|||
name|path
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|childNode
return|;
block|}
elseif|else
if|if
condition|(
name|childNode
operator|instanceof
name|InnerNode
condition|)
block|{
return|return
operator|(
operator|(
name|InnerNode
operator|)
name|childNode
operator|)
operator|.
name|getLoc
argument_list|(
name|path
index|[
literal|1
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLeaf (int leafIndex, Node excludedNode)
specifier|public
name|Node
name|getLeaf
parameter_list|(
name|int
name|leafIndex
parameter_list|,
name|Node
name|excludedNode
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|// check if the excluded node a leaf
name|boolean
name|isLeaf
init|=
operator|!
operator|(
name|excludedNode
operator|instanceof
name|InnerNode
operator|)
decl_stmt|;
comment|// calculate the total number of excluded leaf nodes
name|int
name|numOfExcludedLeaves
init|=
name|isLeaf
condition|?
literal|1
else|:
operator|(
operator|(
name|InnerNode
operator|)
name|excludedNode
operator|)
operator|.
name|getNumOfLeaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|isLeafParent
argument_list|()
condition|)
block|{
comment|// children are leaves
if|if
condition|(
name|isLeaf
condition|)
block|{
comment|// excluded node is a leaf node
if|if
condition|(
name|excludedNode
operator|!=
literal|null
operator|&&
name|childrenMap
operator|.
name|containsKey
argument_list|(
name|excludedNode
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|int
name|excludedIndex
init|=
name|children
operator|.
name|indexOf
argument_list|(
name|excludedNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|excludedIndex
operator|!=
operator|-
literal|1
operator|&&
name|leafIndex
operator|>=
literal|0
condition|)
block|{
comment|// excluded node is one of the children so adjust the leaf index
name|leafIndex
operator|=
name|leafIndex
operator|>=
name|excludedIndex
condition|?
name|leafIndex
operator|+
literal|1
else|:
name|leafIndex
expr_stmt|;
block|}
block|}
block|}
comment|// range check
if|if
condition|(
name|leafIndex
operator|<
literal|0
operator|||
name|leafIndex
operator|>=
name|this
operator|.
name|getNumOfChildren
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|children
operator|.
name|get
argument_list|(
name|leafIndex
argument_list|)
return|;
block|}
else|else
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
name|children
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|InnerNodeImpl
name|child
init|=
operator|(
name|InnerNodeImpl
operator|)
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|excludedNode
operator|==
literal|null
operator|||
name|excludedNode
operator|!=
name|child
condition|)
block|{
comment|// not the excludedNode
name|int
name|numOfLeaves
init|=
name|child
operator|.
name|getNumOfLeaves
argument_list|()
decl_stmt|;
if|if
condition|(
name|excludedNode
operator|!=
literal|null
operator|&&
name|child
operator|.
name|isAncestor
argument_list|(
name|excludedNode
argument_list|)
condition|)
block|{
name|numOfLeaves
operator|-=
name|numOfExcludedLeaves
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|+
name|numOfLeaves
operator|>
name|leafIndex
condition|)
block|{
comment|// the leaf is in the child subtree
return|return
name|child
operator|.
name|getLeaf
argument_list|(
name|leafIndex
operator|-
name|count
argument_list|,
name|excludedNode
argument_list|)
return|;
block|}
else|else
block|{
comment|// go to the next child
name|count
operator|=
name|count
operator|+
name|numOfLeaves
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// it is the excluededNode
comment|// skip it and set the excludedNode to be null
name|excludedNode
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|method|isLeafParent ()
specifier|private
name|boolean
name|isLeafParent
parameter_list|()
block|{
return|return
name|isRack
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumOfLeaves ()
specifier|public
name|int
name|getNumOfLeaves
parameter_list|()
block|{
return|return
name|numOfLeaves
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object to)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|to
argument_list|)
return|;
block|}
block|}
end_class

end_unit

