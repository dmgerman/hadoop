begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
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
name|namenode
package|;
end_package

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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|DatanodeDescriptor
operator|.
name|CachedBlocksList
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
operator|.
name|CachedBlocksList
operator|.
name|Type
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
name|IntrusiveCollection
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
name|LightWeightGSet
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
name|IntrusiveCollection
operator|.
name|Element
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
name|LightWeightGSet
operator|.
name|LinkedElement
import|;
end_import

begin_comment
comment|/**  * Represents a cached block.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
DECL|class|CachedBlock
specifier|public
specifier|final
class|class
name|CachedBlock
implements|implements
name|Element
implements|,
name|LightWeightGSet
operator|.
name|LinkedElement
block|{
DECL|field|EMPTY_ARRAY
specifier|private
specifier|static
specifier|final
name|Object
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Object
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Block id.    */
DECL|field|blockId
specifier|private
specifier|final
name|long
name|blockId
decl_stmt|;
comment|/**    * Used to implement #{LightWeightGSet.LinkedElement}    */
DECL|field|nextElement
specifier|private
name|LinkedElement
name|nextElement
decl_stmt|;
comment|/**    * Bit 15: Mark    * Bit 0-14: cache replication factor.    */
DECL|field|replicationAndMark
specifier|private
name|short
name|replicationAndMark
decl_stmt|;
comment|/**    * Used to implement the CachedBlocksList.    *    * Since this CachedBlock can be in multiple CachedBlocksList objects,    * we need to be able to store multiple 'prev' and 'next' pointers.    * The triplets array does this.    *    * Each triplet contains a CachedBlockList object followed by a    * prev pointer, followed by a next pointer.    */
DECL|field|triplets
specifier|private
name|Object
index|[]
name|triplets
decl_stmt|;
DECL|method|CachedBlock (long blockId, short replication, boolean mark)
specifier|public
name|CachedBlock
parameter_list|(
name|long
name|blockId
parameter_list|,
name|short
name|replication
parameter_list|,
name|boolean
name|mark
parameter_list|)
block|{
name|this
operator|.
name|blockId
operator|=
name|blockId
expr_stmt|;
name|this
operator|.
name|triplets
operator|=
name|EMPTY_ARRAY
expr_stmt|;
name|setReplicationAndMark
argument_list|(
name|replication
argument_list|,
name|mark
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlockId ()
specifier|public
name|long
name|getBlockId
parameter_list|()
block|{
return|return
name|blockId
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
call|(
name|int
call|)
argument_list|(
name|blockId
operator|^
operator|(
name|blockId
operator|>>>
literal|32
operator|)
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
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CachedBlock
name|other
init|=
operator|(
name|CachedBlock
operator|)
name|o
decl_stmt|;
return|return
name|other
operator|.
name|blockId
operator|==
name|blockId
return|;
block|}
DECL|method|setReplicationAndMark (short replication, boolean mark)
specifier|public
name|void
name|setReplicationAndMark
parameter_list|(
name|short
name|replication
parameter_list|,
name|boolean
name|mark
parameter_list|)
block|{
assert|assert
name|replication
operator|>=
literal|0
assert|;
name|replicationAndMark
operator|=
call|(
name|short
call|)
argument_list|(
operator|(
name|replication
operator|<<
literal|1
operator|)
operator||
operator|(
name|mark
condition|?
literal|0x1
else|:
literal|0x0
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getMark ()
specifier|public
name|boolean
name|getMark
parameter_list|()
block|{
return|return
operator|(
operator|(
name|replicationAndMark
operator|&
literal|0x1
operator|)
operator|!=
literal|0
operator|)
return|;
block|}
DECL|method|getReplication ()
specifier|public
name|short
name|getReplication
parameter_list|()
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|replicationAndMark
operator|>>>
literal|1
argument_list|)
return|;
block|}
comment|/**    * Return true if this CachedBlock is present on the given list.    */
DECL|method|isPresent (CachedBlocksList cachedBlocksList)
specifier|public
name|boolean
name|isPresent
parameter_list|(
name|CachedBlocksList
name|cachedBlocksList
parameter_list|)
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
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
name|CachedBlocksList
name|list
init|=
operator|(
name|CachedBlocksList
operator|)
name|triplets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|list
operator|==
name|cachedBlocksList
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Get a list of the datanodes which this block is cached,    * planned to be cached, or planned to be uncached on.    *    * @param type      If null, this parameter is ignored.    *                  If it is non-null, we match only datanodes which    *                  have it on this list.    *                  See {@link DatanodeDescriptor.CachedBlocksList.Type}    *                  for a description of all the lists.    *                      * @return          The list of datanodes.  Modifying this list does not    *                  alter the state of the CachedBlock.    */
DECL|method|getDatanodes (Type type)
specifier|public
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|getDatanodes
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|nodes
init|=
operator|new
name|LinkedList
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
name|CachedBlocksList
name|list
init|=
operator|(
name|CachedBlocksList
operator|)
name|triplets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|type
operator|==
literal|null
operator|)
operator|||
operator|(
name|list
operator|.
name|getType
argument_list|()
operator|==
name|type
operator|)
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|list
operator|.
name|getDatanode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
DECL|method|insertInternal (IntrusiveCollection<? extends Element> list, Element prev, Element next)
specifier|public
name|void
name|insertInternal
parameter_list|(
name|IntrusiveCollection
argument_list|<
name|?
extends|extends
name|Element
argument_list|>
name|list
parameter_list|,
name|Element
name|prev
parameter_list|,
name|Element
name|next
parameter_list|)
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
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
if|if
condition|(
name|triplets
index|[
name|i
index|]
operator|==
name|list
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Trying to re-insert an element that "
operator|+
literal|"is already in the list."
argument_list|)
throw|;
block|}
block|}
name|Object
name|newTriplets
index|[]
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|triplets
argument_list|,
name|triplets
operator|.
name|length
operator|+
literal|3
argument_list|)
decl_stmt|;
name|newTriplets
index|[
name|triplets
operator|.
name|length
index|]
operator|=
name|list
expr_stmt|;
name|newTriplets
index|[
name|triplets
operator|.
name|length
operator|+
literal|1
index|]
operator|=
name|prev
expr_stmt|;
name|newTriplets
index|[
name|triplets
operator|.
name|length
operator|+
literal|2
index|]
operator|=
name|next
expr_stmt|;
name|triplets
operator|=
name|newTriplets
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setPrev (IntrusiveCollection<? extends Element> list, Element prev)
specifier|public
name|void
name|setPrev
parameter_list|(
name|IntrusiveCollection
argument_list|<
name|?
extends|extends
name|Element
argument_list|>
name|list
parameter_list|,
name|Element
name|prev
parameter_list|)
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
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
if|if
condition|(
name|triplets
index|[
name|i
index|]
operator|==
name|list
condition|)
block|{
name|triplets
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|prev
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Called setPrev on an element that wasn't "
operator|+
literal|"in the list."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setNext (IntrusiveCollection<? extends Element> list, Element next)
specifier|public
name|void
name|setNext
parameter_list|(
name|IntrusiveCollection
argument_list|<
name|?
extends|extends
name|Element
argument_list|>
name|list
parameter_list|,
name|Element
name|next
parameter_list|)
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
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
if|if
condition|(
name|triplets
index|[
name|i
index|]
operator|==
name|list
condition|)
block|{
name|triplets
index|[
name|i
operator|+
literal|2
index|]
operator|=
name|next
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Called setNext on an element that wasn't "
operator|+
literal|"in the list."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|removeInternal (IntrusiveCollection<? extends Element> list)
specifier|public
name|void
name|removeInternal
parameter_list|(
name|IntrusiveCollection
argument_list|<
name|?
extends|extends
name|Element
argument_list|>
name|list
parameter_list|)
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
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
if|if
condition|(
name|triplets
index|[
name|i
index|]
operator|==
name|list
condition|)
block|{
name|Object
index|[]
name|newTriplets
init|=
operator|new
name|Object
index|[
name|triplets
operator|.
name|length
operator|-
literal|3
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|triplets
argument_list|,
literal|0
argument_list|,
name|newTriplets
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|triplets
argument_list|,
name|i
operator|+
literal|3
argument_list|,
name|newTriplets
argument_list|,
name|i
argument_list|,
name|triplets
operator|.
name|length
operator|-
operator|(
name|i
operator|+
literal|3
operator|)
argument_list|)
expr_stmt|;
name|triplets
operator|=
name|newTriplets
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Called remove on an element that wasn't "
operator|+
literal|"in the list."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getPrev (IntrusiveCollection<? extends Element> list)
specifier|public
name|Element
name|getPrev
parameter_list|(
name|IntrusiveCollection
argument_list|<
name|?
extends|extends
name|Element
argument_list|>
name|list
parameter_list|)
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
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
if|if
condition|(
name|triplets
index|[
name|i
index|]
operator|==
name|list
condition|)
block|{
return|return
operator|(
name|Element
operator|)
name|triplets
index|[
name|i
operator|+
literal|1
index|]
return|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Called getPrev on an element that wasn't "
operator|+
literal|"in the list."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getNext (IntrusiveCollection<? extends Element> list)
specifier|public
name|Element
name|getNext
parameter_list|(
name|IntrusiveCollection
argument_list|<
name|?
extends|extends
name|Element
argument_list|>
name|list
parameter_list|)
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
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
if|if
condition|(
name|triplets
index|[
name|i
index|]
operator|==
name|list
condition|)
block|{
return|return
operator|(
name|Element
operator|)
name|triplets
index|[
name|i
operator|+
literal|2
index|]
return|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Called getNext on an element that wasn't "
operator|+
literal|"in the list."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|isInList (IntrusiveCollection<? extends Element> list)
specifier|public
name|boolean
name|isInList
parameter_list|(
name|IntrusiveCollection
argument_list|<
name|?
extends|extends
name|Element
argument_list|>
name|list
parameter_list|)
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
name|triplets
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
if|if
condition|(
name|triplets
index|[
name|i
index|]
operator|==
name|list
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"blockId="
argument_list|)
operator|.
name|append
argument_list|(
name|blockId
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"replication="
argument_list|)
operator|.
name|append
argument_list|(
name|getReplication
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
literal|"mark="
argument_list|)
operator|.
name|append
argument_list|(
name|getMark
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// LightWeightGSet.LinkedElement
DECL|method|setNext (LinkedElement next)
specifier|public
name|void
name|setNext
parameter_list|(
name|LinkedElement
name|next
parameter_list|)
block|{
name|this
operator|.
name|nextElement
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
comment|// LightWeightGSet.LinkedElement
DECL|method|getNext ()
specifier|public
name|LinkedElement
name|getNext
parameter_list|()
block|{
return|return
name|nextElement
return|;
block|}
block|}
end_class

end_unit

