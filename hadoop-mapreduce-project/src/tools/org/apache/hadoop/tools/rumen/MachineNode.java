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

begin_comment
comment|/**  * {@link MachineNode} represents the configuration of a cluster node.  * {@link MachineNode} should be constructed by {@link MachineNode.Builder}.  */
end_comment

begin_class
DECL|class|MachineNode
specifier|public
specifier|final
class|class
name|MachineNode
extends|extends
name|Node
block|{
DECL|field|memory
name|long
name|memory
init|=
operator|-
literal|1
decl_stmt|;
comment|// in KB
DECL|field|mapSlots
name|int
name|mapSlots
init|=
literal|1
decl_stmt|;
DECL|field|reduceSlots
name|int
name|reduceSlots
init|=
literal|1
decl_stmt|;
DECL|field|memoryPerMapSlot
name|long
name|memoryPerMapSlot
init|=
operator|-
literal|1
decl_stmt|;
comment|// in KB
DECL|field|memoryPerReduceSlot
name|long
name|memoryPerReduceSlot
init|=
operator|-
literal|1
decl_stmt|;
comment|// in KB
DECL|field|numCores
name|int
name|numCores
init|=
literal|1
decl_stmt|;
DECL|method|MachineNode (String name, int level)
name|MachineNode
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// name/level sufficient
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
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
comment|// match equals
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Get the available physical RAM of the node.    * @return The available physical RAM of the node, in KB.    */
DECL|method|getMemory ()
specifier|public
name|long
name|getMemory
parameter_list|()
block|{
return|return
name|memory
return|;
block|}
comment|/**    * Get the number of map slots of the node.    * @return The number of map slots of the node.    */
DECL|method|getMapSlots ()
specifier|public
name|int
name|getMapSlots
parameter_list|()
block|{
return|return
name|mapSlots
return|;
block|}
comment|/**    * Get the number of reduce slots of the node.    * @return The number of reduce slots fo the node.    */
DECL|method|getReduceSlots ()
specifier|public
name|int
name|getReduceSlots
parameter_list|()
block|{
return|return
name|reduceSlots
return|;
block|}
comment|/**    * Get the amount of RAM reserved for each map slot.    * @return the amount of RAM reserved for each map slot, in KB.    */
DECL|method|getMemoryPerMapSlot ()
specifier|public
name|long
name|getMemoryPerMapSlot
parameter_list|()
block|{
return|return
name|memoryPerMapSlot
return|;
block|}
comment|/**    * Get the amount of RAM reserved for each reduce slot.    * @return the amount of RAM reserved for each reduce slot, in KB.    */
DECL|method|getMemoryPerReduceSlot ()
specifier|public
name|long
name|getMemoryPerReduceSlot
parameter_list|()
block|{
return|return
name|memoryPerReduceSlot
return|;
block|}
comment|/**    * Get the number of cores of the node.    * @return the number of cores of the node.    */
DECL|method|getNumCores ()
specifier|public
name|int
name|getNumCores
parameter_list|()
block|{
return|return
name|numCores
return|;
block|}
comment|/**    * Get the rack node that the machine belongs to.    *     * @return The rack node that the machine belongs to. Returns null if the    *         machine does not belong to any rack.    */
DECL|method|getRackNode ()
specifier|public
name|RackNode
name|getRackNode
parameter_list|()
block|{
return|return
operator|(
name|RackNode
operator|)
name|getParent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addChild (Node child)
specifier|public
specifier|synchronized
name|boolean
name|addChild
parameter_list|(
name|Node
name|child
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add child to MachineNode"
argument_list|)
throw|;
block|}
comment|/**    * Builder for a NodeInfo object    */
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|node
specifier|private
name|MachineNode
name|node
decl_stmt|;
comment|/**      * Start building a new NodeInfo object.      * @param name      *          Unique name of the node. Typically the fully qualified domain      *          name.      */
DECL|method|Builder (String name, int level)
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|node
operator|=
operator|new
name|MachineNode
argument_list|(
name|name
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the physical memory of the node.      * @param memory Available RAM in KB.      */
DECL|method|setMemory (long memory)
specifier|public
name|Builder
name|setMemory
parameter_list|(
name|long
name|memory
parameter_list|)
block|{
name|node
operator|.
name|memory
operator|=
name|memory
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the number of map slot for the node.      * @param mapSlots The number of map slots for the node.      */
DECL|method|setMapSlots (int mapSlots)
specifier|public
name|Builder
name|setMapSlots
parameter_list|(
name|int
name|mapSlots
parameter_list|)
block|{
name|node
operator|.
name|mapSlots
operator|=
name|mapSlots
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the number of reduce slot for the node.      * @param reduceSlots The number of reduce slots for the node.      */
DECL|method|setReduceSlots (int reduceSlots)
specifier|public
name|Builder
name|setReduceSlots
parameter_list|(
name|int
name|reduceSlots
parameter_list|)
block|{
name|node
operator|.
name|reduceSlots
operator|=
name|reduceSlots
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the amount of RAM reserved for each map slot.      * @param memoryPerMapSlot The amount of RAM reserved for each map slot, in KB.      */
DECL|method|setMemoryPerMapSlot (long memoryPerMapSlot)
specifier|public
name|Builder
name|setMemoryPerMapSlot
parameter_list|(
name|long
name|memoryPerMapSlot
parameter_list|)
block|{
name|node
operator|.
name|memoryPerMapSlot
operator|=
name|memoryPerMapSlot
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the amount of RAM reserved for each reduce slot.      * @param memoryPerReduceSlot The amount of RAM reserved for each reduce slot, in KB.      */
DECL|method|setMemoryPerReduceSlot (long memoryPerReduceSlot)
specifier|public
name|Builder
name|setMemoryPerReduceSlot
parameter_list|(
name|long
name|memoryPerReduceSlot
parameter_list|)
block|{
name|node
operator|.
name|memoryPerReduceSlot
operator|=
name|memoryPerReduceSlot
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the number of cores for the node.      * @param numCores Number of cores for the node.      */
DECL|method|setNumCores (int numCores)
specifier|public
name|Builder
name|setNumCores
parameter_list|(
name|int
name|numCores
parameter_list|)
block|{
name|node
operator|.
name|numCores
operator|=
name|numCores
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Clone the settings from a reference {@link MachineNode} object.      * @param ref The reference {@link MachineNode} object.      */
DECL|method|cloneFrom (MachineNode ref)
specifier|public
name|Builder
name|cloneFrom
parameter_list|(
name|MachineNode
name|ref
parameter_list|)
block|{
name|node
operator|.
name|memory
operator|=
name|ref
operator|.
name|memory
expr_stmt|;
name|node
operator|.
name|mapSlots
operator|=
name|ref
operator|.
name|mapSlots
expr_stmt|;
name|node
operator|.
name|reduceSlots
operator|=
name|ref
operator|.
name|reduceSlots
expr_stmt|;
name|node
operator|.
name|memoryPerMapSlot
operator|=
name|ref
operator|.
name|memoryPerMapSlot
expr_stmt|;
name|node
operator|.
name|memoryPerReduceSlot
operator|=
name|ref
operator|.
name|memoryPerReduceSlot
expr_stmt|;
name|node
operator|.
name|numCores
operator|=
name|ref
operator|.
name|numCores
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Build the {@link MachineNode} object.      * @return The {@link MachineNode} object being built.      */
DECL|method|build ()
specifier|public
name|MachineNode
name|build
parameter_list|()
block|{
name|MachineNode
name|retVal
init|=
name|node
decl_stmt|;
name|node
operator|=
literal|null
expr_stmt|;
return|return
name|retVal
return|;
block|}
block|}
block|}
end_class

end_unit

