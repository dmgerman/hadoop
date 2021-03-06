begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|lib
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
name|fs
operator|.
name|FileSystem
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
name|FileInputFormat
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
name|FileOutputFormat
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
name|mapred
operator|.
name|JobClient
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
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|RunningJob
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
name|mapred
operator|.
name|TextOutputFormat
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
name|lib
operator|.
name|map
operator|.
name|MultithreadedMapper
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
name|DataOutputStream
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
DECL|class|TestMultithreadedMapRunner
specifier|public
class|class
name|TestMultithreadedMapRunner
extends|extends
name|HadoopTestCase
block|{
DECL|method|TestMultithreadedMapRunner ()
specifier|public
name|TestMultithreadedMapRunner
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
annotation|@
name|Test
DECL|method|testOKRun ()
specifier|public
name|void
name|testOKRun
parameter_list|()
throws|throws
name|Exception
block|{
name|run
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIOExRun ()
specifier|public
name|void
name|testIOExRun
parameter_list|()
throws|throws
name|Exception
block|{
name|run
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRuntimeExRun ()
specifier|public
name|void
name|testRuntimeExRun
parameter_list|()
throws|throws
name|Exception
block|{
name|run
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|run (boolean ioEx, boolean rtEx)
specifier|private
name|void
name|run
parameter_list|(
name|boolean
name|ioEx
parameter_list|,
name|boolean
name|rtEx
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
literal|"testing/mt/input"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
literal|"testing/mt/output"
argument_list|)
decl_stmt|;
comment|// Hack for local FS that does not have the concept of a 'mounting point'
if|if
condition|(
name|isLocalFS
argument_list|()
condition|)
block|{
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
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'+'
argument_list|)
decl_stmt|;
name|inDir
operator|=
operator|new
name|Path
argument_list|(
name|localPathRoot
argument_list|,
name|inDir
argument_list|)
expr_stmt|;
name|outDir
operator|=
operator|new
name|Path
argument_list|(
name|localPathRoot
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
block|}
name|JobConf
name|conf
init|=
name|createJobConf
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|outDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|inDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|inDir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|{
name|DataOutputStream
name|file
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
literal|"part-0"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|writeBytes
argument_list|(
literal|"a\nb\n\nc\nd\ne"
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|setJobName
argument_list|(
literal|"mt"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapOutputKeyClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|IDMap
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|IDReduce
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapRunnerClass
argument_list|(
name|MultithreadedMapRunner
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MultithreadedMapper
operator|.
name|NUM_THREADS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|ioEx
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"multithreaded.ioException"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rtEx
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"multithreaded.runtimeException"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|JobClient
name|jc
init|=
operator|new
name|JobClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RunningJob
name|job
init|=
name|jc
operator|.
name|submitJob
argument_list|(
name|conf
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|job
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|job
operator|.
name|isSuccessful
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
name|ioEx
operator|||
name|rtEx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|ioEx
operator|||
name|rtEx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|IDMap
specifier|public
specifier|static
class|class
name|IDMap
implements|implements
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
DECL|field|ioEx
specifier|private
name|boolean
name|ioEx
init|=
literal|false
decl_stmt|;
DECL|field|rtEx
specifier|private
name|boolean
name|rtEx
init|=
literal|false
decl_stmt|;
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|ioEx
operator|=
name|job
operator|.
name|getBoolean
argument_list|(
literal|"multithreaded.ioException"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rtEx
operator|=
name|job
operator|.
name|getBoolean
argument_list|(
literal|"multithreaded.runtimeException"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|map (LongWritable key, Text value, OutputCollector<LongWritable, Text> output, Reporter reporter)
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
if|if
condition|(
name|ioEx
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|()
throw|;
block|}
if|if
condition|(
name|rtEx
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
block|}
DECL|class|IDReduce
specifier|public
specifier|static
class|class
name|IDReduce
implements|implements
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
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{     }
DECL|method|reduce (LongWritable key, Iterator<Text> values, OutputCollector<LongWritable, Text> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Iterator
argument_list|<
name|Text
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
name|key
argument_list|,
name|values
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
block|}
block|}
end_class

end_unit

