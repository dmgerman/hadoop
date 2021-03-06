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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
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
name|mapreduce
operator|.
name|MRConfig
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
name|io
operator|.
name|OutputStream
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
DECL|class|TestFileOutputFormat
specifier|public
class|class
name|TestFileOutputFormat
extends|extends
name|HadoopTestCase
block|{
DECL|method|TestFileOutputFormat ()
specifier|public
name|TestFileOutputFormat
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
DECL|method|testCustomFile ()
specifier|public
name|void
name|testCustomFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
literal|"testing/fileoutputformat/input"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
literal|"testing/fileoutputformat/output"
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
name|file
operator|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
literal|"part-1"
argument_list|)
argument_list|)
expr_stmt|;
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
name|conf
operator|.
name|setJobName
argument_list|(
literal|"fof"
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
name|TestMap
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|TestReduce
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|FRAMEWORK_NAME
argument_list|,
name|MRConfig
operator|.
name|LOCAL_FRAMEWORK_NAME
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
name|assertTrue
argument_list|(
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|map0
init|=
literal|false
decl_stmt|;
name|boolean
name|map1
init|=
literal|false
decl_stmt|;
name|boolean
name|reduce
init|=
literal|false
decl_stmt|;
name|FileStatus
index|[]
name|statuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|outDir
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|status
range|:
name|statuses
control|)
block|{
name|map0
operator|=
name|map0
operator|||
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test-m-00000"
argument_list|)
expr_stmt|;
name|map1
operator|=
name|map1
operator|||
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test-m-00001"
argument_list|)
expr_stmt|;
name|reduce
operator|=
name|reduce
operator|||
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test-r-00000"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|map0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reduce
argument_list|)
expr_stmt|;
block|}
DECL|class|TestMap
specifier|public
specifier|static
class|class
name|TestMap
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
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
try|try
block|{
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
name|OutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|FileOutputFormat
operator|.
name|getPathForCustomFile
argument_list|(
name|conf
argument_list|,
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
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
DECL|class|TestReduce
specifier|public
specifier|static
class|class
name|TestReduce
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
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
try|try
block|{
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
name|OutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|FileOutputFormat
operator|.
name|getPathForCustomFile
argument_list|(
name|conf
argument_list|,
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|Text
name|value
init|=
name|values
operator|.
name|next
argument_list|()
decl_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|value
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

