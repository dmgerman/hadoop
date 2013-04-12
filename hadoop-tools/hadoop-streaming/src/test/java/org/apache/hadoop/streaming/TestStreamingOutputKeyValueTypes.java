begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
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
name|io
operator|.
name|LongWritable
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
name|Text
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
name|MapReduceBase
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
name|OutputCollector
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
name|Reducer
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
name|Reporter
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
name|TextInputFormat
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
name|MRJobConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Tests stream job with java tasks, commands in MapReduce local mode.  * Validates if user-set config properties  * {@link MRJobConfig#MAP_OUTPUT_KEY_CLASS} and  * {@link MRJobConfig#OUTPUT_KEY_CLASS} are honored by streaming jobs.  */
end_comment

begin_class
DECL|class|TestStreamingOutputKeyValueTypes
specifier|public
class|class
name|TestStreamingOutputKeyValueTypes
extends|extends
name|TestStreaming
block|{
DECL|method|TestStreamingOutputKeyValueTypes ()
specifier|public
name|TestStreamingOutputKeyValueTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|input
operator|=
literal|"one line dummy input\n"
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|args
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|genArgs ()
specifier|protected
name|String
index|[]
name|genArgs
parameter_list|()
block|{
comment|// set the testcase-specific config properties first and the remaining
comment|// arguments are set in TestStreaming.genArgs().
name|args
operator|.
name|add
argument_list|(
literal|"-jobconf"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_KEY_CLASS
operator|+
literal|"=org.apache.hadoop.io.LongWritable"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-jobconf"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|MRJobConfig
operator|.
name|OUTPUT_KEY_CLASS
operator|+
literal|"=org.apache.hadoop.io.LongWritable"
argument_list|)
expr_stmt|;
comment|// Using SequenceFileOutputFormat here because with TextOutputFormat, the
comment|// mapred.output.key.class set in JobConf (which we want to test here) is
comment|// not read/used at all.
name|args
operator|.
name|add
argument_list|(
literal|"-outputformat"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"org.apache.hadoop.mapred.SequenceFileOutputFormat"
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|genArgs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkOutput ()
specifier|protected
name|void
name|checkOutput
parameter_list|()
throws|throws
name|IOException
block|{
comment|// No need to validate output for the test cases in this class
block|}
DECL|class|MyReducer
specifier|public
specifier|static
class|class
name|MyReducer
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|MapReduceBase
implements|implements
name|Reducer
argument_list|<
name|K
argument_list|,
name|V
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|method|reduce (K key, Iterator<V> values, OutputCollector<LongWritable, Text> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|K
name|key
parameter_list|,
name|Iterator
argument_list|<
name|V
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|LongWritable
name|l
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
while|while
condition|(
name|values
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|output
operator|.
name|collect
argument_list|(
name|l
argument_list|,
operator|new
name|Text
argument_list|(
name|values
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Check with Java Mapper, Java Reducer
annotation|@
name|Test
DECL|method|testJavaMapperAndJavaReducer ()
specifier|public
name|void
name|testJavaMapperAndJavaReducer
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
literal|"org.apache.hadoop.mapred.lib.IdentityMapper"
expr_stmt|;
name|reduce
operator|=
literal|"org.apache.hadoop.mapred.lib.IdentityReducer"
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Java Mapper, Java Reducer and -numReduceTasks 0
annotation|@
name|Test
DECL|method|testJavaMapperAndJavaReducerAndZeroReduces ()
specifier|public
name|void
name|testJavaMapperAndJavaReducerAndZeroReduces
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
literal|"org.apache.hadoop.mapred.lib.IdentityMapper"
expr_stmt|;
name|reduce
operator|=
literal|"org.apache.hadoop.mapred.lib.IdentityReducer"
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numReduceTasks"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Java Mapper, Reducer = "NONE"
annotation|@
name|Test
DECL|method|testJavaMapperWithReduceNone ()
specifier|public
name|void
name|testJavaMapperWithReduceNone
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
literal|"org.apache.hadoop.mapred.lib.IdentityMapper"
expr_stmt|;
name|reduce
operator|=
literal|"NONE"
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Java Mapper, command Reducer
annotation|@
name|Test
DECL|method|testJavaMapperAndCommandReducer ()
specifier|public
name|void
name|testJavaMapperAndCommandReducer
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
literal|"org.apache.hadoop.mapred.lib.IdentityMapper"
expr_stmt|;
name|reduce
operator|=
name|CAT
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Java Mapper, command Reducer and -numReduceTasks 0
annotation|@
name|Test
DECL|method|testJavaMapperAndCommandReducerAndZeroReduces ()
specifier|public
name|void
name|testJavaMapperAndCommandReducerAndZeroReduces
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
literal|"org.apache.hadoop.mapred.lib.IdentityMapper"
expr_stmt|;
name|reduce
operator|=
name|CAT
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numReduceTasks"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Command Mapper, Java Reducer
annotation|@
name|Test
DECL|method|testCommandMapperAndJavaReducer ()
specifier|public
name|void
name|testCommandMapperAndJavaReducer
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
name|CAT
expr_stmt|;
name|reduce
operator|=
name|MyReducer
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Command Mapper, Java Reducer and -numReduceTasks 0
annotation|@
name|Test
DECL|method|testCommandMapperAndJavaReducerAndZeroReduces ()
specifier|public
name|void
name|testCommandMapperAndJavaReducerAndZeroReduces
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
name|CAT
expr_stmt|;
name|reduce
operator|=
name|MyReducer
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numReduceTasks"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Command Mapper, Reducer = "NONE"
annotation|@
name|Test
DECL|method|testCommandMapperWithReduceNone ()
specifier|public
name|void
name|testCommandMapperWithReduceNone
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
name|CAT
expr_stmt|;
name|reduce
operator|=
literal|"NONE"
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Command Mapper, Command Reducer
annotation|@
name|Test
DECL|method|testCommandMapperAndCommandReducer ()
specifier|public
name|void
name|testCommandMapperAndCommandReducer
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
name|CAT
expr_stmt|;
name|reduce
operator|=
name|CAT
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
comment|// Check with Command Mapper, Command Reducer and -numReduceTasks 0
annotation|@
name|Test
DECL|method|testCommandMapperAndCommandReducerAndZeroReduces ()
specifier|public
name|void
name|testCommandMapperAndCommandReducerAndZeroReduces
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|=
name|CAT
expr_stmt|;
name|reduce
operator|=
name|CAT
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numReduceTasks"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultToIdentityReducer ()
specifier|public
name|void
name|testDefaultToIdentityReducer
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-mapper"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-jobconf"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"mapreduce.task.files.preserve.failedtasks=true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-jobconf"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"stream.tmpdir="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-inputformat"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|TextInputFormat
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|testCommandLine
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
DECL|method|testCommandLine ()
specifier|public
name|void
name|testCommandLine
parameter_list|()
block|{
comment|// Do nothing
block|}
block|}
end_class

end_unit

