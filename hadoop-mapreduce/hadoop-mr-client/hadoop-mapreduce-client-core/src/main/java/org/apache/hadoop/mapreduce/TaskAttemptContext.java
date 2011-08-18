begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|util
operator|.
name|Progressable
import|;
end_import

begin_comment
comment|/**  * The context for task attempts.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|TaskAttemptContext
specifier|public
interface|interface
name|TaskAttemptContext
extends|extends
name|JobContext
extends|,
name|Progressable
block|{
comment|/**    * Get the unique name for this task attempt.    */
DECL|method|getTaskAttemptID ()
specifier|public
name|TaskAttemptID
name|getTaskAttemptID
parameter_list|()
function_decl|;
comment|/**    * Set the current status of the task to the given string.    */
DECL|method|setStatus (String msg)
specifier|public
name|void
name|setStatus
parameter_list|(
name|String
name|msg
parameter_list|)
function_decl|;
comment|/**    * Get the last set status message.    * @return the current status message    */
DECL|method|getStatus ()
specifier|public
name|String
name|getStatus
parameter_list|()
function_decl|;
comment|/**    * The current progress of the task attempt.    * @return a number between 0.0 and 1.0 (inclusive) indicating the attempt's    * progress.    */
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
parameter_list|()
function_decl|;
comment|/**    * Get the {@link Counter} for the given<code>counterName</code>.    * @param counterName counter name    * @return the<code>Counter</code> for the given<code>counterName</code>    */
DECL|method|getCounter (Enum<?> counterName)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|counterName
parameter_list|)
function_decl|;
comment|/**    * Get the {@link Counter} for the given<code>groupName</code> and     *<code>counterName</code>.    * @param counterName counter name    * @return the<code>Counter</code> for the given<code>groupName</code> and     *<code>counterName</code>    */
DECL|method|getCounter (String groupName, String counterName)
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|String
name|groupName
parameter_list|,
name|String
name|counterName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

