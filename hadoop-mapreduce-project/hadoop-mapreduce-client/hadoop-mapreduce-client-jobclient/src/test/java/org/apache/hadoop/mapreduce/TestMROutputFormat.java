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
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configurable
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
name|io
operator|.
name|IntWritable
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
name|io
operator|.
name|Writable
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
name|mapred
operator|.
name|JobConf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestMROutputFormat
specifier|public
class|class
name|TestMROutputFormat
block|{
annotation|@
name|Test
DECL|method|testJobSubmission ()
specifier|public
name|void
name|testJobSubmission
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
operator|new
name|Job
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|TestInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|TestMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|TestOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|TestMapper
specifier|public
specifier|static
class|class
name|TestMapper
extends|extends
name|Mapper
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|map (IntWritable key, IntWritable value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|IntWritable
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_class
DECL|class|TestInputFormat
class|class
name|TestInputFormat
extends|extends
name|InputFormat
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createRecordReader ( InputSplit split, TaskAttemptContext context)
specifier|public
name|RecordReader
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
name|createRecordReader
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|RecordReader
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
argument_list|()
block|{
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|IntWritable
name|getCurrentKey
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|IntWritable
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IntWritable
name|getCurrentValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|IntWritable
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|done
condition|?
literal|0
else|:
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{       }
annotation|@
name|Override
specifier|public
name|boolean
name|nextKeyValue
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
operator|!
name|done
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getSplits (JobContext context)
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|getSplits
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|InputSplit
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|TestInputSplit
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
block|}
end_class

begin_class
DECL|class|TestInputSplit
class|class
name|TestInputSplit
extends|extends
name|InputSplit
implements|implements
name|Writable
block|{
annotation|@
name|Override
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|String
index|[]
name|hosts
init|=
block|{
literal|"localhost"
block|}
decl_stmt|;
return|return
name|hosts
return|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{   }
block|}
end_class

begin_class
DECL|class|TestOutputFormat
class|class
name|TestOutputFormat
extends|extends
name|OutputFormat
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|TEST_CONFIG_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TEST_CONFIG_NAME
init|=
literal|"mapred.test.jobsubmission"
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Override
DECL|method|checkOutputSpecs (JobContext context)
specifier|public
name|void
name|checkOutputSpecs
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|TEST_CONFIG_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOutputCommitter (TaskAttemptContext context)
specifier|public
name|OutputCommitter
name|getOutputCommitter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|OutputCommitter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|abortTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|commitTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|boolean
name|needsTaskCommit
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setupJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|setupTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{       }
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getRecordWriter ( TaskAttemptContext context)
specifier|public
name|RecordWriter
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|assertTrue
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|TEST_CONFIG_NAME
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|RecordWriter
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{	       }
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|IntWritable
name|value
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{	       }
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
block|}
end_class

end_unit

