begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.chain
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|chain
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
name|fs
operator|.
name|Path
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
name|HadoopTestCase
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
name|Job
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
name|MapReduceTestUtil
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
comment|/**  * Tests error conditions in ChainMapper/ChainReducer.  */
end_comment

begin_class
DECL|class|TestChainErrors
specifier|public
class|class
name|TestChainErrors
extends|extends
name|HadoopTestCase
block|{
DECL|field|localPathRoot
specifier|private
specifier|static
name|String
name|localPathRoot
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
decl_stmt|;
DECL|method|TestChainErrors ()
specifier|public
name|TestChainErrors
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|HadoopTestCase
operator|.
name|LOCAL_MR
argument_list|,
name|HadoopTestCase
operator|.
name|LOCAL_FS
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|field|inDir
specifier|private
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|localPathRoot
argument_list|,
literal|"testing/chain/input"
argument_list|)
decl_stmt|;
DECL|field|outDir
specifier|private
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|localPathRoot
argument_list|,
literal|"testing/chain/output"
argument_list|)
decl_stmt|;
DECL|field|input
specifier|private
name|String
name|input
init|=
literal|"a\nb\nc\nd\n"
decl_stmt|;
comment|/**    * Tests errors during submission.    *     * @throws Exception    */
DECL|method|testChainSubmission ()
specifier|public
name|void
name|testChainSubmission
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|createJobConf
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createJob
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"chain"
argument_list|)
expr_stmt|;
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
comment|// output key,value classes of first map are not same as that of second map
try|try
block|{
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|th
operator|=
name|iae
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|th
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|th
operator|=
literal|null
expr_stmt|;
comment|// output key,value classes of reducer are not
comment|// same as that of mapper in the chain
try|try
block|{
name|ChainReducer
operator|.
name|setReducer
argument_list|(
name|job
argument_list|,
name|Reducer
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|th
operator|=
name|iae
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|th
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests one of the mappers throwing exception.    *     * @throws Exception    */
DECL|method|testChainFail ()
specifier|public
name|void
name|testChainFail
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|createJobConf
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createJob
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"chain"
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|FailMap
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
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
literal|"Job Not failed"
argument_list|,
operator|!
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests Reducer throwing exception.    *     * @throws Exception    */
DECL|method|testReducerFail ()
specifier|public
name|void
name|testReducerFail
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|createJobConf
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createJob
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"chain"
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainReducer
operator|.
name|setReducer
argument_list|(
name|job
argument_list|,
name|FailReduce
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainReducer
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
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
literal|"Job Not failed"
argument_list|,
operator|!
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests one of the maps consuming output.    *     * @throws Exception    */
DECL|method|testChainMapNoOuptut ()
specifier|public
name|void
name|testChainMapNoOuptut
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|createJobConf
argument_list|()
decl_stmt|;
name|String
name|expectedOutput
init|=
literal|""
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createJob
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"chain"
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|ConsumeMap
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
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
literal|"Job failed"
argument_list|,
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Outputs doesn't match"
argument_list|,
name|expectedOutput
argument_list|,
name|MapReduceTestUtil
operator|.
name|readOutput
argument_list|(
name|outDir
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests reducer consuming output.    *     * @throws Exception    */
DECL|method|testChainReduceNoOuptut ()
specifier|public
name|void
name|testChainReduceNoOuptut
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|createJobConf
argument_list|()
decl_stmt|;
name|String
name|expectedOutput
init|=
literal|""
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createJob
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"chain"
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainReducer
operator|.
name|setReducer
argument_list|(
name|job
argument_list|,
name|ConsumeReduce
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainReducer
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|Mapper
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
literal|null
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
literal|"Job failed"
argument_list|,
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Outputs doesn't match"
argument_list|,
name|expectedOutput
argument_list|,
name|MapReduceTestUtil
operator|.
name|readOutput
argument_list|(
name|outDir
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// this map consumes all the input and output nothing
DECL|class|ConsumeMap
specifier|public
specifier|static
class|class
name|ConsumeMap
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|method|map (LongWritable key, Text value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{     }
block|}
comment|// this reduce consumes all the input and output nothing
DECL|class|ConsumeReduce
specifier|public
specifier|static
class|class
name|ConsumeReduce
extends|extends
name|Reducer
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|method|reduce (LongWritable key, Iterable<Text> values, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Iterable
argument_list|<
name|Text
argument_list|>
name|values
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{     }
block|}
comment|// this map throws IOException for input value "b"
DECL|class|FailMap
specifier|public
specifier|static
class|class
name|FailMap
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|method|map (LongWritable key, Text value, Context context)
specifier|protected
name|void
name|map
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Text
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
if|if
condition|(
name|value
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"b"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|()
throw|;
block|}
block|}
block|}
comment|// this reduce throws IOEexception for any input
DECL|class|FailReduce
specifier|public
specifier|static
class|class
name|FailReduce
extends|extends
name|Reducer
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|method|reduce (LongWritable key, Iterable<Text> values, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Iterable
argument_list|<
name|Text
argument_list|>
name|values
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
throw|throw
operator|new
name|IOException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

