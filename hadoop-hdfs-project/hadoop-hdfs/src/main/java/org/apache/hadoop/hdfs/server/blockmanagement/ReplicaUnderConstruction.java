begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
package|;
end_package

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
name|protocol
operator|.
name|Block
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
name|common
operator|.
name|HdfsServerConstants
import|;
end_import

begin_comment
comment|/**  * ReplicaUnderConstruction contains information about replicas (or blocks  * belonging to a block group) while they are under construction.  *  * The GS, the length and the state of the replica is as reported by the  * datanode.  *  * It is not guaranteed, but expected, that datanodes actually have  * corresponding replicas.  */
end_comment

begin_class
DECL|class|ReplicaUnderConstruction
class|class
name|ReplicaUnderConstruction
extends|extends
name|Block
block|{
DECL|field|expectedLocation
specifier|private
specifier|final
name|DatanodeStorageInfo
name|expectedLocation
decl_stmt|;
DECL|field|state
specifier|private
name|HdfsServerConstants
operator|.
name|ReplicaState
name|state
decl_stmt|;
DECL|field|chosenAsPrimary
specifier|private
name|boolean
name|chosenAsPrimary
decl_stmt|;
DECL|method|ReplicaUnderConstruction (Block block, DatanodeStorageInfo target, HdfsServerConstants.ReplicaState state)
name|ReplicaUnderConstruction
parameter_list|(
name|Block
name|block
parameter_list|,
name|DatanodeStorageInfo
name|target
parameter_list|,
name|HdfsServerConstants
operator|.
name|ReplicaState
name|state
parameter_list|)
block|{
name|super
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedLocation
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|chosenAsPrimary
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Expected block replica location as assigned when the block was allocated.    * This defines the pipeline order.    * It is not guaranteed, but expected, that the data-node actually has    * the replica.    */
DECL|method|getExpectedStorageLocation ()
name|DatanodeStorageInfo
name|getExpectedStorageLocation
parameter_list|()
block|{
return|return
name|expectedLocation
return|;
block|}
comment|/**    * Get replica state as reported by the data-node.    */
DECL|method|getState ()
name|HdfsServerConstants
operator|.
name|ReplicaState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * Whether the replica was chosen for recovery.    */
DECL|method|getChosenAsPrimary ()
name|boolean
name|getChosenAsPrimary
parameter_list|()
block|{
return|return
name|chosenAsPrimary
return|;
block|}
comment|/**    * Set replica state.    */
DECL|method|setState (HdfsServerConstants.ReplicaState s)
name|void
name|setState
parameter_list|(
name|HdfsServerConstants
operator|.
name|ReplicaState
name|s
parameter_list|)
block|{
name|state
operator|=
name|s
expr_stmt|;
block|}
comment|/**    * Set whether this replica was chosen for recovery.    */
DECL|method|setChosenAsPrimary (boolean chosenAsPrimary)
name|void
name|setChosenAsPrimary
parameter_list|(
name|boolean
name|chosenAsPrimary
parameter_list|)
block|{
name|this
operator|.
name|chosenAsPrimary
operator|=
name|chosenAsPrimary
expr_stmt|;
block|}
comment|/**    * Is data-node the replica belongs to alive.    */
DECL|method|isAlive ()
name|boolean
name|isAlive
parameter_list|()
block|{
return|return
name|expectedLocation
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|isAlive
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// Block
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
comment|// Block
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// Sufficient to rely on super's implementation
return|return
operator|(
name|this
operator|==
name|obj
operator|)
operator|||
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
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|50
argument_list|)
decl_stmt|;
name|appendStringTo
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|appendStringTo (StringBuilder sb)
specifier|public
name|void
name|appendStringTo
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"ReplicaUC["
argument_list|)
operator|.
name|append
argument_list|(
name|expectedLocation
argument_list|)
operator|.
name|append
argument_list|(
literal|"|"
argument_list|)
operator|.
name|append
argument_list|(
name|state
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

