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

begin_comment
comment|/**  * {@link JobChangeEvent} is used to capture state changes in a job. A job can   * change its state w.r.t priority, progress, run-state etc.  */
end_comment

begin_class
DECL|class|JobChangeEvent
specifier|abstract
class|class
name|JobChangeEvent
block|{
DECL|field|jip
specifier|private
name|JobInProgress
name|jip
decl_stmt|;
DECL|method|JobChangeEvent (JobInProgress jip)
name|JobChangeEvent
parameter_list|(
name|JobInProgress
name|jip
parameter_list|)
block|{
name|this
operator|.
name|jip
operator|=
name|jip
expr_stmt|;
block|}
comment|/**    * Get the job object for which the change is reported    */
DECL|method|getJobInProgress ()
name|JobInProgress
name|getJobInProgress
parameter_list|()
block|{
return|return
name|jip
return|;
block|}
block|}
end_class

end_unit

