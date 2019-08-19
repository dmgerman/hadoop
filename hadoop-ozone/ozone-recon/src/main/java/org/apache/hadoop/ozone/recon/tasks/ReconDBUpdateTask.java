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
comment|/**  * Interface used to denote a Recon task that needs to act on OM DB events.  */
end_comment

begin_interface
DECL|interface|ReconDBUpdateTask
specifier|public
interface|interface
name|ReconDBUpdateTask
block|{
comment|/**    * Return task name.    * @return task name    */
DECL|method|getTaskName ()
name|String
name|getTaskName
parameter_list|()
function_decl|;
comment|/**    * Return the list of tables that the task is listening on.    * Empty list means the task is NOT listening on any tables.    * @return Collection of Tables.    */
DECL|method|getTaskTables ()
name|Collection
argument_list|<
name|String
argument_list|>
name|getTaskTables
parameter_list|()
function_decl|;
comment|/**    * Process a set of OM events on tables that the task is listening on.    * @param events Set of events to be processed by the task.    * @return Pair of task name -> task success.    */
DECL|method|process (OMUpdateEventBatch events)
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
end_interface

end_unit

