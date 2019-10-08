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
name|interfaces
operator|.
name|ContainerDeletionChoosingPolicy
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
name|keyvalue
operator|.
name|KeyValueContainerData
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * TopN Ordered choosing policy that choosing containers based on pending  * deletion blocks' number.  */
end_comment

begin_class
DECL|class|TopNOrderedContainerDeletionChoosingPolicy
specifier|public
class|class
name|TopNOrderedContainerDeletionChoosingPolicy
implements|implements
name|ContainerDeletionChoosingPolicy
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TopNOrderedContainerDeletionChoosingPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** customized comparator used to compare differentiate container data. **/
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|KeyValueContainerData
argument_list|>
DECL|field|KEY_VALUE_CONTAINER_DATA_COMPARATOR
name|KEY_VALUE_CONTAINER_DATA_COMPARATOR
init|=
parameter_list|(
name|KeyValueContainerData
name|c1
parameter_list|,
name|KeyValueContainerData
name|c2
parameter_list|)
lambda|->
name|Integer
operator|.
name|compare
argument_list|(
name|c2
operator|.
name|getNumPendingDeletionBlocks
argument_list|()
argument_list|,
name|c1
operator|.
name|getNumPendingDeletionBlocks
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|chooseContainerForBlockDeletion (int count, Map<Long, ContainerData> candidateContainers)
specifier|public
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
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|candidateContainers
argument_list|,
literal|"Internal assertion: candidate containers cannot be null"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerData
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|KeyValueContainerData
argument_list|>
name|orderedList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerData
name|entry
range|:
name|candidateContainers
operator|.
name|values
argument_list|()
control|)
block|{
name|orderedList
operator|.
name|add
argument_list|(
operator|(
name|KeyValueContainerData
operator|)
name|entry
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|orderedList
argument_list|,
name|KEY_VALUE_CONTAINER_DATA_COMPARATOR
argument_list|)
expr_stmt|;
comment|// get top N list ordered by pending deletion blocks' number
name|int
name|currentCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|KeyValueContainerData
name|entry
range|:
name|orderedList
control|)
block|{
if|if
condition|(
name|currentCount
operator|<
name|count
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getNumPendingDeletionBlocks
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|currentCount
operator|++
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Select container {} for block deletion, "
operator|+
literal|"pending deletion blocks num: {}."
argument_list|,
name|entry
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|entry
operator|.
name|getNumPendingDeletionBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stop looking for next container, there is no"
operator|+
literal|" pending deletion block contained in remaining containers."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

