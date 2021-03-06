begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|net
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|StorageType
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|net
operator|.
name|InnerNode
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
name|net
operator|.
name|InnerNodeImpl
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
name|net
operator|.
name|Node
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
name|EnumMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
comment|/**  * The HDFS-specific representation of a network topology inner node. The  * difference is this class includes the information about the storage type  * info of this subtree. This info will be used when selecting subtrees  * in block placement.  */
end_comment

begin_class
DECL|class|DFSTopologyNodeImpl
specifier|public
class|class
name|DFSTopologyNodeImpl
extends|extends
name|InnerNodeImpl
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
name|DFSTopologyNodeImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FACTORY
specifier|static
specifier|final
name|InnerNodeImpl
operator|.
name|Factory
name|FACTORY
init|=
operator|new
name|DFSTopologyNodeImpl
operator|.
name|Factory
argument_list|()
decl_stmt|;
DECL|class|Factory
specifier|static
specifier|final
class|class
name|Factory
extends|extends
name|InnerNodeImpl
operator|.
name|Factory
block|{
DECL|method|Factory ()
specifier|private
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
name|DFSTopologyNodeImpl
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
comment|/**    * The core data structure of this class. The information about what storage    * types this subtree has. Basically, a map whose key is a child    * id, value is a enum map including the counts of each storage type. e.g.    * DISK type has count 5 means there are 5 leaf datanodes with DISK type    * available. This value is set/updated upon datanode joining and leaving.    *    * NOTE : It might be sufficient to keep only a map from storage type    * to count, omitting the child node id. But this might make it hard to keep    * consistency when there are updates from children.    *    * For example, if currently R has two children A and B with storage X, Y, and    * A : X=1 Y=1    * B : X=2 Y=2    * so we store X=3 Y=3 as total on R.    *    * Now say A has a new X plugged in and becomes X=2 Y=1.    *    * If we know that "A adds one X", it is easy to update R by +1 on X. However,    * if we don't know "A adds one X", but instead got "A now has X=2 Y=1",    * (which seems to be the case in current heartbeat) we will not know how to    * update R. While if we store on R "A has X=1 and Y=1" then we can simply    * update R by completely replacing the A entry and all will be good.    */
specifier|private
specifier|final
name|HashMap
DECL|field|childrenStorageInfo
argument_list|<
name|String
argument_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|childrenStorageInfo
decl_stmt|;
comment|/**    * This map stores storage type counts of the subtree. We can always get this    * info by iterate over the childrenStorageInfo variable. But for optimization    * purpose, we store this info directly to avoid the iteration.    */
DECL|field|storageTypeCounts
specifier|private
specifier|final
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|storageTypeCounts
decl_stmt|;
DECL|method|DFSTopologyNodeImpl (String path)
name|DFSTopologyNodeImpl
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
name|childrenStorageInfo
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|storageTypeCounts
operator|=
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|DFSTopologyNodeImpl ( String name, String location, InnerNode parent, int level)
name|DFSTopologyNodeImpl
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
name|childrenStorageInfo
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|storageTypeCounts
operator|=
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|getSubtreeStorageCount (StorageType type)
specifier|public
name|int
name|getSubtreeStorageCount
parameter_list|(
name|StorageType
name|type
parameter_list|)
block|{
if|if
condition|(
name|storageTypeCounts
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|storageTypeCounts
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|incStorageTypeCount (StorageType type)
specifier|private
name|void
name|incStorageTypeCount
parameter_list|(
name|StorageType
name|type
parameter_list|)
block|{
comment|// no locking because the caller is synchronized already
if|if
condition|(
name|storageTypeCounts
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|storageTypeCounts
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|storageTypeCounts
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|storageTypeCounts
operator|.
name|put
argument_list|(
name|type
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|decStorageTypeCount (StorageType type)
specifier|private
name|void
name|decStorageTypeCount
parameter_list|(
name|StorageType
name|type
parameter_list|)
block|{
comment|// no locking because the caller is synchronized already
name|int
name|current
init|=
name|storageTypeCounts
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|current
operator|-=
literal|1
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|0
condition|)
block|{
name|storageTypeCounts
operator|.
name|remove
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|storageTypeCounts
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Called when add() is called to add a node that already exist.    *    * In normal execution, nodes are added only once and this should not happen.    * However if node restarts, we may run into the case where the same node    * tries to add itself again with potentially different storage type info.    * In this case this method will update the meta data according to the new    * storage info.    *    * Note that it is important to also update all the ancestors if we do have    * updated the local node storage info.    *    * @param dnDescriptor the node that is added another time, with potentially    *                     different storage types.    */
DECL|method|updateExistingDatanode (DatanodeDescriptor dnDescriptor)
specifier|private
name|void
name|updateExistingDatanode
parameter_list|(
name|DatanodeDescriptor
name|dnDescriptor
parameter_list|)
block|{
if|if
condition|(
name|childrenStorageInfo
operator|.
name|containsKey
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// all existing node should have an entry in childrenStorageInfo
name|boolean
name|same
init|=
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageType
name|type
range|:
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|keySet
argument_list|()
control|)
block|{
name|same
operator|=
name|same
operator|&&
name|dnDescriptor
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|same
condition|)
block|{
comment|// if the storage type hasn't been changed, do nothing.
return|return;
block|}
comment|// not same means we need to update the storage info.
name|DFSTopologyNodeImpl
name|parent
init|=
operator|(
name|DFSTopologyNodeImpl
operator|)
name|getParent
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageType
name|type
range|:
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|dnDescriptor
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// remove this type, because the new storage info does not have it.
comment|// also need to remove decrement the count for all the ancestors.
comment|// since this is the parent of n, where n is a datanode,
comment|// the map must have 1 as the value of all keys
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|decStorageTypeCount
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|childRemoveStorage
argument_list|(
name|getName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|StorageType
name|type
range|:
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// there is a new type in new storage info, add this locally,
comment|// as well as all ancestors.
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|type
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|incStorageTypeCount
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|childAddStorage
argument_list|(
name|getName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"adding node {}"
argument_list|,
name|n
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
comment|// In HDFS topology, the leaf node should always be DatanodeDescriptor
if|if
condition|(
operator|!
operator|(
name|n
operator|instanceof
name|DatanodeDescriptor
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected node type "
operator|+
name|n
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|DatanodeDescriptor
name|dnDescriptor
init|=
operator|(
name|DatanodeDescriptor
operator|)
name|n
decl_stmt|;
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
name|updateExistingDatanode
argument_list|(
name|dnDescriptor
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
if|if
condition|(
operator|!
name|childrenStorageInfo
operator|.
name|containsKey
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|childrenStorageInfo
operator|.
name|put
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|StorageType
name|st
range|:
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|st
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|incStorageTypeCount
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|childrenStorageInfo
operator|.
name|containsKey
argument_list|(
name|parentNode
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|childrenStorageInfo
operator|.
name|put
argument_list|(
name|parentNode
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|StorageType
name|st
range|:
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|parentNode
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|st
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|currentCount
init|=
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|parentNode
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageType
name|st
range|:
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|currentCount
operator|.
name|containsKey
argument_list|(
name|st
argument_list|)
condition|)
block|{
name|currentCount
operator|.
name|put
argument_list|(
name|st
argument_list|,
name|currentCount
operator|.
name|get
argument_list|(
name|st
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentCount
operator|.
name|put
argument_list|(
name|st
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|StorageType
name|st
range|:
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
name|incStorageTypeCount
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|VisibleForTesting
DECL|method|getChildrenStorageInfo ()
name|HashMap
argument_list|<
name|String
argument_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|getChildrenStorageInfo
parameter_list|()
block|{
return|return
name|childrenStorageInfo
return|;
block|}
DECL|method|createParentNode (String parentName)
specifier|private
name|DFSTopologyNodeImpl
name|createParentNode
parameter_list|(
name|String
name|parentName
parameter_list|)
block|{
return|return
operator|new
name|DFSTopologyNodeImpl
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
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
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
DECL|method|remove (Node n)
specifier|public
name|boolean
name|remove
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing node {}"
argument_list|,
name|n
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
comment|// In HDFS topology, the leaf node should always be DatanodeDescriptor
if|if
condition|(
operator|!
operator|(
name|n
operator|instanceof
name|DatanodeDescriptor
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected node type "
operator|+
name|n
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|DatanodeDescriptor
name|dnDescriptor
init|=
operator|(
name|DatanodeDescriptor
operator|)
name|n
decl_stmt|;
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
name|childrenStorageInfo
operator|.
name|remove
argument_list|(
name|dnDescriptor
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|StorageType
name|st
range|:
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
name|decStorageTypeCount
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
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
name|DFSTopologyNodeImpl
name|parentNode
init|=
operator|(
name|DFSTopologyNodeImpl
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
if|if
condition|(
name|isRemoved
condition|)
block|{
comment|// if the parent node has no children, remove the parent node too
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|currentCount
init|=
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|parentNode
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|EnumSet
argument_list|<
name|StorageType
argument_list|>
name|toRemove
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageType
name|st
range|:
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
name|int
name|newCount
init|=
name|currentCount
operator|.
name|get
argument_list|(
name|st
argument_list|)
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|newCount
operator|==
literal|0
condition|)
block|{
name|toRemove
operator|.
name|add
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
name|currentCount
operator|.
name|put
argument_list|(
name|st
argument_list|,
name|newCount
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|StorageType
name|st
range|:
name|toRemove
control|)
block|{
name|currentCount
operator|.
name|remove
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|StorageType
name|st
range|:
name|dnDescriptor
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
name|decStorageTypeCount
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
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
name|childrenStorageInfo
operator|.
name|remove
argument_list|(
name|parentNode
operator|.
name|getName
argument_list|()
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
comment|/**    * Called by a child node of the current node to increment a storage count.    *    * lock is needed as different datanodes may call recursively to modify    * the same parent.    * TODO : this may not happen at all, depending on how heartheat is processed    * @param childName the name of the child that tries to add the storage type    * @param type the type being incremented.    */
DECL|method|childAddStorage ( String childName, StorageType type)
specifier|public
specifier|synchronized
name|void
name|childAddStorage
parameter_list|(
name|String
name|childName
parameter_list|,
name|StorageType
name|type
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"child add storage: {}:{}"
argument_list|,
name|childName
argument_list|,
name|type
argument_list|)
expr_stmt|;
comment|// childrenStorageInfo should definitely contain this node already
comment|// because updateStorage is called after node added
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|childrenStorageInfo
operator|.
name|containsKey
argument_list|(
name|childName
argument_list|)
argument_list|)
expr_stmt|;
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|typeCount
init|=
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|childName
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeCount
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|typeCount
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|typeCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Please be aware that, the counts are always "number of datanodes in
comment|// this subtree" rather than "number of storages in this storage".
comment|// so if the caller is a datanode, it should always be this branch rather
comment|// than the +1 branch above. This depends on the caller in
comment|// DatanodeDescriptor to make sure only when a *new* storage type is added
comment|// it calls this. (should not call this when a already existing storage
comment|// is added).
comment|// but no such restriction for inner nodes.
name|typeCount
operator|.
name|put
argument_list|(
name|type
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storageTypeCounts
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|storageTypeCounts
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|storageTypeCounts
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|storageTypeCounts
operator|.
name|put
argument_list|(
name|type
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|DFSTopologyNodeImpl
operator|)
name|getParent
argument_list|()
operator|)
operator|.
name|childAddStorage
argument_list|(
name|getName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Called by a child node of the current node to decrement a storage count.    *    * @param childName the name of the child removing a storage type.    * @param type the type being removed.    */
DECL|method|childRemoveStorage ( String childName, StorageType type)
specifier|public
specifier|synchronized
name|void
name|childRemoveStorage
parameter_list|(
name|String
name|childName
parameter_list|,
name|StorageType
name|type
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"child remove storage: {}:{}"
argument_list|,
name|childName
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|childrenStorageInfo
operator|.
name|containsKey
argument_list|(
name|childName
argument_list|)
argument_list|)
expr_stmt|;
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|typeCount
init|=
name|childrenStorageInfo
operator|.
name|get
argument_list|(
name|childName
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|typeCount
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|typeCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|>
literal|1
condition|)
block|{
name|typeCount
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|typeCount
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|typeCount
operator|.
name|remove
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|storageTypeCounts
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|storageTypeCounts
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|>
literal|1
condition|)
block|{
name|storageTypeCounts
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|storageTypeCounts
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|storageTypeCounts
operator|.
name|remove
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|DFSTopologyNodeImpl
operator|)
name|getParent
argument_list|()
operator|)
operator|.
name|childRemoveStorage
argument_list|(
name|getName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

