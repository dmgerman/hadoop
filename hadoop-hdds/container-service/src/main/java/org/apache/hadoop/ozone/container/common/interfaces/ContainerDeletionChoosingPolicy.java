begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
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
name|interfaces
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerData
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
comment|/**  * This interface is used for choosing desired containers for  * block deletion.  */
end_comment

begin_comment
comment|// TODO: Fix ContainerDeletionChoosingPolicy to work with new StorageLayer
end_comment

begin_interface
DECL|interface|ContainerDeletionChoosingPolicy
specifier|public
interface|interface
name|ContainerDeletionChoosingPolicy
block|{
comment|/**    * Chooses desired containers for block deletion.    * @param count    *          how many to return    * @param candidateContainers    *          candidate containers collection    * @return container data list    * @throws StorageContainerException    */
DECL|method|chooseContainerForBlockDeletion (int count, Map<Long, ContainerData> candidateContainers)
name|List
argument_list|<
name|ContainerData
argument_list|>
name|chooseContainerForBlockDeletion
parameter_list|(
name|int
name|count
parameter_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|ContainerData
argument_list|>
name|candidateContainers
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
block|}
end_interface

end_unit

