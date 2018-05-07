begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.common.helpers
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
name|container
operator|.
name|common
operator|.
name|helpers
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
name|hdds
operator|.
name|client
operator|.
name|BlockID
import|;
end_import

begin_comment
comment|/**  * Allocated block wraps the result returned from SCM#allocateBlock which  * contains a Pipeline and the key.  */
end_comment

begin_class
DECL|class|AllocatedBlock
specifier|public
specifier|final
class|class
name|AllocatedBlock
block|{
DECL|field|pipeline
specifier|private
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|blockID
specifier|private
name|BlockID
name|blockID
decl_stmt|;
comment|// Indicates whether the client should create container before writing block.
DECL|field|shouldCreateContainer
specifier|private
name|boolean
name|shouldCreateContainer
decl_stmt|;
comment|/**    * Builder for AllocatedBlock.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|pipeline
specifier|private
name|Pipeline
name|pipeline
decl_stmt|;
DECL|field|blockID
specifier|private
name|BlockID
name|blockID
decl_stmt|;
DECL|field|shouldCreateContainer
specifier|private
name|boolean
name|shouldCreateContainer
decl_stmt|;
DECL|method|setPipeline (Pipeline p)
specifier|public
name|Builder
name|setPipeline
parameter_list|(
name|Pipeline
name|p
parameter_list|)
block|{
name|this
operator|.
name|pipeline
operator|=
name|p
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBlockID (BlockID blockID)
specifier|public
name|Builder
name|setBlockID
parameter_list|(
name|BlockID
name|blockID
parameter_list|)
block|{
name|this
operator|.
name|blockID
operator|=
name|blockID
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setShouldCreateContainer (boolean shouldCreate)
specifier|public
name|Builder
name|setShouldCreateContainer
parameter_list|(
name|boolean
name|shouldCreate
parameter_list|)
block|{
name|this
operator|.
name|shouldCreateContainer
operator|=
name|shouldCreate
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|AllocatedBlock
name|build
parameter_list|()
block|{
return|return
operator|new
name|AllocatedBlock
argument_list|(
name|pipeline
argument_list|,
name|blockID
argument_list|,
name|shouldCreateContainer
argument_list|)
return|;
block|}
block|}
DECL|method|AllocatedBlock (Pipeline pipeline, BlockID blockID, boolean shouldCreateContainer)
specifier|private
name|AllocatedBlock
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|BlockID
name|blockID
parameter_list|,
name|boolean
name|shouldCreateContainer
parameter_list|)
block|{
name|this
operator|.
name|pipeline
operator|=
name|pipeline
expr_stmt|;
name|this
operator|.
name|blockID
operator|=
name|blockID
expr_stmt|;
name|this
operator|.
name|shouldCreateContainer
operator|=
name|shouldCreateContainer
expr_stmt|;
block|}
DECL|method|getPipeline ()
specifier|public
name|Pipeline
name|getPipeline
parameter_list|()
block|{
return|return
name|pipeline
return|;
block|}
DECL|method|getBlockID ()
specifier|public
name|BlockID
name|getBlockID
parameter_list|()
block|{
return|return
name|blockID
return|;
block|}
DECL|method|getCreateContainer ()
specifier|public
name|boolean
name|getCreateContainer
parameter_list|()
block|{
return|return
name|shouldCreateContainer
return|;
block|}
block|}
end_class

end_unit

