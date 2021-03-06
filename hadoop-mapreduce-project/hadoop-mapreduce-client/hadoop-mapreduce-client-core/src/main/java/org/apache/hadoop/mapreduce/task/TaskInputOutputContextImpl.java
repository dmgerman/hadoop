begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|mapreduce
operator|.
name|OutputCommitter
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
name|mapreduce
operator|.
name|RecordWriter
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
name|mapreduce
operator|.
name|StatusReporter
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
name|mapreduce
operator|.
name|TaskAttemptID
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
name|mapreduce
operator|.
name|TaskInputOutputContext
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
name|mapreduce
operator|.
name|Mapper
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
name|mapreduce
operator|.
name|Reducer
import|;
end_import

begin_comment
comment|/**  * A context object that allows input and output from the task. It is only  * supplied to the {@link Mapper} or {@link Reducer}.  * @param<KEYIN> the input key type for the task  * @param<VALUEIN> the input value type for the task  * @param<KEYOUT> the output key type for the task  * @param<VALUEOUT> the output value type for the task  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TaskInputOutputContextImpl
specifier|public
specifier|abstract
class|class
name|TaskInputOutputContextImpl
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
name|TaskAttemptContextImpl
implements|implements
name|TaskInputOutputContext
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
block|{
DECL|field|output
specifier|private
name|RecordWriter
argument_list|<
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|output
decl_stmt|;
DECL|field|committer
specifier|private
name|OutputCommitter
name|committer
decl_stmt|;
DECL|method|TaskInputOutputContextImpl (Configuration conf, TaskAttemptID taskid, RecordWriter<KEYOUT,VALUEOUT> output, OutputCommitter committer, StatusReporter reporter)
specifier|public
name|TaskInputOutputContextImpl
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|TaskAttemptID
name|taskid
parameter_list|,
name|RecordWriter
argument_list|<
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|output
parameter_list|,
name|OutputCommitter
name|committer
parameter_list|,
name|StatusReporter
name|reporter
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|taskid
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
name|this
operator|.
name|committer
operator|=
name|committer
expr_stmt|;
block|}
comment|/**    * Advance to the next key, value pair, returning null if at end.    * @return the key object that was read into, or null if no more    */
specifier|public
specifier|abstract
DECL|method|nextKeyValue ()
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the current key.    * @return the current key object or null if there isn't one    * @throws IOException    * @throws InterruptedException    */
specifier|public
specifier|abstract
DECL|method|getCurrentKey ()
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
specifier|abstract
name|VALUEIN
name|getCurrentValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Generate an output key/value pair.    */
DECL|method|write (KEYOUT key, VALUEOUT value )
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
block|{
name|output
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getOutputCommitter ()
specifier|public
name|OutputCommitter
name|getOutputCommitter
parameter_list|()
block|{
return|return
name|committer
return|;
block|}
block|}
end_class

end_unit

