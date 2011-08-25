begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A listener for changes in a {@link JobInProgress job}'s lifecycle in the  * {@link JobTracker}.  */
end_comment

begin_class
DECL|class|JobInProgressListener
specifier|abstract
class|class
name|JobInProgressListener
block|{
comment|/**    * Invoked when a new job has been added to the {@link JobTracker}.    * @param job The added job.    * @throws IOException     */
DECL|method|jobAdded (JobInProgress job)
specifier|public
specifier|abstract
name|void
name|jobAdded
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Invoked when a job has been removed from the {@link JobTracker}.    * @param job The removed job.    */
DECL|method|jobRemoved (JobInProgress job)
specifier|public
specifier|abstract
name|void
name|jobRemoved
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
function_decl|;
comment|/**    * Invoked when a job has been updated in the {@link JobTracker}.    * This change in the job is tracker using {@link JobChangeEvent}.    * @param event the event that tracks the change    */
DECL|method|jobUpdated (JobChangeEvent event)
specifier|public
specifier|abstract
name|void
name|jobUpdated
parameter_list|(
name|JobChangeEvent
name|event
parameter_list|)
function_decl|;
block|}
end_class

end_unit

