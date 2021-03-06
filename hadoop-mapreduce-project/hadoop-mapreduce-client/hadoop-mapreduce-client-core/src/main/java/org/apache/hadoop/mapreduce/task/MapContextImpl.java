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
name|InputSplit
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
name|MapContext
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
name|RecordReader
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

begin_comment
comment|/**  * The context that is given to the {@link Mapper}.  * @param<KEYIN> the key input type to the Mapper  * @param<VALUEIN> the value input type to the Mapper  * @param<KEYOUT> the key output type from the Mapper  * @param<VALUEOUT> the value output type from the Mapper  */
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
DECL|class|MapContextImpl
specifier|public
class|class
name|MapContextImpl
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
name|TaskInputOutputContextImpl
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|,
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
implements|implements
name|MapContext
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
DECL|field|reader
specifier|private
name|RecordReader
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|>
name|reader
decl_stmt|;
DECL|field|split
specifier|private
name|InputSplit
name|split
decl_stmt|;
DECL|method|MapContextImpl (Configuration conf, TaskAttemptID taskid, RecordReader<KEYIN,VALUEIN> reader, RecordWriter<KEYOUT,VALUEOUT> writer, OutputCommitter committer, StatusReporter reporter, InputSplit split)
specifier|public
name|MapContextImpl
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|TaskAttemptID
name|taskid
parameter_list|,
name|RecordReader
argument_list|<
name|KEYIN
argument_list|,
name|VALUEIN
argument_list|>
name|reader
parameter_list|,
name|RecordWriter
argument_list|<
name|KEYOUT
argument_list|,
name|VALUEOUT
argument_list|>
name|writer
parameter_list|,
name|OutputCommitter
name|committer
parameter_list|,
name|StatusReporter
name|reporter
parameter_list|,
name|InputSplit
name|split
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|taskid
argument_list|,
name|writer
argument_list|,
name|committer
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|split
operator|=
name|split
expr_stmt|;
block|}
comment|/**    * Get the input split for this map.    */
DECL|method|getInputSplit ()
specifier|public
name|InputSplit
name|getInputSplit
parameter_list|()
block|{
return|return
name|split
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentKey ()
specifier|public
name|KEYIN
name|getCurrentKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|reader
operator|.
name|getCurrentKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCurrentValue ()
specifier|public
name|VALUEIN
name|getCurrentValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|reader
operator|.
name|getCurrentValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextKeyValue ()
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|reader
operator|.
name|nextKeyValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

