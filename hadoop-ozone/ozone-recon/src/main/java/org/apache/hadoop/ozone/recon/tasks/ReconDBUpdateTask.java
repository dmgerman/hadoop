begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|tasks
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|om
operator|.
name|OMMetadataManager
import|;
end_import

begin_comment
comment|/**  * Abstract class used to denote a Recon task that needs to act on OM DB events.  */
end_comment

begin_class
DECL|class|ReconDBUpdateTask
specifier|public
specifier|abstract
class|class
name|ReconDBUpdateTask
block|{
DECL|field|taskName
specifier|private
name|String
name|taskName
decl_stmt|;
DECL|method|ReconDBUpdateTask (String taskName)
specifier|protected
name|ReconDBUpdateTask
parameter_list|(
name|String
name|taskName
parameter_list|)
block|{
name|this
operator|.
name|taskName
operator|=
name|taskName
expr_stmt|;
block|}
comment|/**    * Return task name.    * @return task name    */
DECL|method|getTaskName ()
specifier|public
name|String
name|getTaskName
parameter_list|()
block|{
return|return
name|taskName
return|;
block|}
comment|/**    * Return the list of tables that the task is listening on.    * Empty list means the task is NOT listening on any tables.    * @return Collection of Tables.    */
DECL|method|getTaskTables ()
specifier|protected
specifier|abstract
name|Collection
argument_list|<
name|String
argument_list|>
name|getTaskTables
parameter_list|()
function_decl|;
comment|/**    * Process a set of OM events on tables that the task is listening on.    * @param events Set of events to be processed by the task.    * @return Pair of task name -> task success.    */
DECL|method|process (OMUpdateEventBatch events)
specifier|abstract
name|Pair
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|process
parameter_list|(
name|OMUpdateEventBatch
name|events
parameter_list|)
function_decl|;
comment|/**    * Process a  on tables that the task is listening on.    * @param omMetadataManager OM Metadata manager instance.    * @return Pair of task name -> task success.    */
DECL|method|reprocess (OMMetadataManager omMetadataManager)
specifier|abstract
name|Pair
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|reprocess
parameter_list|(
name|OMMetadataManager
name|omMetadataManager
parameter_list|)
function_decl|;
block|}
end_class

end_unit

