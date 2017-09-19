begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerData
import|;
end_import

begin_comment
comment|/**  * This is an immutable class that represents the state of a container. if the  * container reading encountered an error when we boot up we will post that  * info to a recovery queue and keep the info in the containerMap.  *<p/>  * if and when the issue is fixed, the expectation is that this entry will be  * deleted by the recovery thread from the containerMap and will insert entry  * instead of modifying this class.  */
end_comment

begin_class
DECL|class|ContainerStatus
specifier|public
class|class
name|ContainerStatus
block|{
DECL|field|containerData
specifier|private
specifier|final
name|ContainerData
name|containerData
decl_stmt|;
comment|/**    * Number of pending deletion blocks in container.    */
DECL|field|numPendingDeletionBlocks
specifier|private
name|int
name|numPendingDeletionBlocks
decl_stmt|;
comment|/**    * Creates a Container Status class.    *    * @param containerData - ContainerData.    */
DECL|method|ContainerStatus (ContainerData containerData)
name|ContainerStatus
parameter_list|(
name|ContainerData
name|containerData
parameter_list|)
block|{
name|this
operator|.
name|numPendingDeletionBlocks
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|containerData
operator|=
name|containerData
expr_stmt|;
block|}
comment|/**    * Returns container if it is active. It is not active if we have had an    * error and we are waiting for the background threads to fix the issue.    *    * @return ContainerData.    */
DECL|method|getContainer ()
specifier|public
name|ContainerData
name|getContainer
parameter_list|()
block|{
return|return
name|containerData
return|;
block|}
comment|/**    * Increase the count of pending deletion blocks.    *    * @param numBlocks increment number    */
DECL|method|incrPendingDeletionBlocks (int numBlocks)
specifier|public
name|void
name|incrPendingDeletionBlocks
parameter_list|(
name|int
name|numBlocks
parameter_list|)
block|{
name|this
operator|.
name|numPendingDeletionBlocks
operator|+=
name|numBlocks
expr_stmt|;
block|}
comment|/**    * Decrease the count of pending deletion blocks.    *    * @param numBlocks decrement number    */
DECL|method|decrPendingDeletionBlocks (int numBlocks)
specifier|public
name|void
name|decrPendingDeletionBlocks
parameter_list|(
name|int
name|numBlocks
parameter_list|)
block|{
name|this
operator|.
name|numPendingDeletionBlocks
operator|-=
name|numBlocks
expr_stmt|;
block|}
comment|/**    * Get the number of pending deletion blocks.    */
DECL|method|getNumPendingDeletionBlocks ()
specifier|public
name|int
name|getNumPendingDeletionBlocks
parameter_list|()
block|{
return|return
name|this
operator|.
name|numPendingDeletionBlocks
return|;
block|}
block|}
end_class

end_unit

