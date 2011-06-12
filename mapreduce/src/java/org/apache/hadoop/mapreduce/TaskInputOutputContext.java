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
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * A context object that allows input and output from the task. It is only  * supplied to the {@link Mapper} or {@link Reducer}.  * @param<KEYIN> the input key type for the task  * @param<VALUEIN> the input value type for the task  * @param<KEYOUT> the output key type for the task  * @param<VALUEOUT> the output value type for the task  */
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
DECL|interface|TaskInputOutputContext
specifier|public
interface|interface
name|TaskInputOutputContext
parameter_list|<
name|KEYIN
parameter_list|,
name|VALUEIN
parameter_list|,
name|KEYOUT
parameter_list|,
name|VALUEOUT
parameter_list|>
extends|extends
name|TaskAttemptContext
block|{
comment|/**    * Advance to the next key, value pair, returning null if at end.    * @return the key object that was read into, or null if no more    */
DECL|method|nextKeyValue ()
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the current key.    * @return the current key object or null if there isn't one    * @throws IOException    * @throws InterruptedException    */
DECL|method|getCurrentKey ()
specifier|public
name|KEYIN
name|getCurrentKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the current value.    * @return the value object that was read into    * @throws IOException    * @throws InterruptedException    */
DECL|method|getCurrentValue ()
specifier|public
name|VALUEIN
name|getCurrentValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Generate an output key/value pair.    */
DECL|method|write (KEYOUT key, VALUEOUT value)
specifier|public
name|void
name|write
parameter_list|(
name|KEYOUT
name|key
parameter_list|,
name|VALUEOUT
name|value
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the {@link OutputCommitter} for the task-attempt.    * @return the<code>OutputCommitter</code> for the task-attempt    */
DECL|method|getOutputCommitter ()
specifier|public
name|OutputCommitter
name|getOutputCommitter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

