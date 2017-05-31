begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.deletion.recovery
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|recovery
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|task
operator|.
name|DeletionTask
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

begin_comment
comment|/**  * Encapsulates the recovery info needed to recover a DeletionTask from the NM  * state store.  */
end_comment

begin_class
DECL|class|DeletionTaskRecoveryInfo
specifier|public
class|class
name|DeletionTaskRecoveryInfo
block|{
DECL|field|task
specifier|private
name|DeletionTask
name|task
decl_stmt|;
DECL|field|successorTaskIds
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|successorTaskIds
decl_stmt|;
DECL|field|deletionTimestamp
specifier|private
name|long
name|deletionTimestamp
decl_stmt|;
comment|/**    * Information needed for recovering the DeletionTask.    *    * @param task the DeletionTask    * @param successorTaskIds the dependent DeletionTasks.    * @param deletionTimestamp the scheduled times of deletion.    */
DECL|method|DeletionTaskRecoveryInfo (DeletionTask task, List<Integer> successorTaskIds, long deletionTimestamp)
specifier|public
name|DeletionTaskRecoveryInfo
parameter_list|(
name|DeletionTask
name|task
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|successorTaskIds
parameter_list|,
name|long
name|deletionTimestamp
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|successorTaskIds
operator|=
name|successorTaskIds
expr_stmt|;
name|this
operator|.
name|deletionTimestamp
operator|=
name|deletionTimestamp
expr_stmt|;
block|}
comment|/**    * Return the recovered DeletionTask.    *    * @return the recovered DeletionTask.    */
DECL|method|getTask ()
specifier|public
name|DeletionTask
name|getTask
parameter_list|()
block|{
return|return
name|task
return|;
block|}
comment|/**    * Return all of the dependent DeletionTasks.    *    * @return the dependent DeletionTasks.    */
DECL|method|getSuccessorTaskIds ()
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getSuccessorTaskIds
parameter_list|()
block|{
return|return
name|successorTaskIds
return|;
block|}
comment|/**    * Return the deletion timestamp.    *    * @return the deletion timestamp.    */
DECL|method|getDeletionTimestamp ()
specifier|public
name|long
name|getDeletionTimestamp
parameter_list|()
block|{
return|return
name|deletionTimestamp
return|;
block|}
block|}
end_class

end_unit

