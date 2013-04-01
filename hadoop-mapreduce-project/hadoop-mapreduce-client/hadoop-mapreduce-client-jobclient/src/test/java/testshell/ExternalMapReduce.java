begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|testshell
package|package
name|testshell
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
name|conf
operator|.
name|Configured
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
name|io
operator|.
name|WritableComparable
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
name|util
operator|.
name|Tool
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
name|ToolRunner
import|;
end_import

begin_comment
comment|/**  * will be in an external jar and used for   * test in TestJobShell.java.  */
end_comment

begin_class
DECL|class|ExternalMapReduce
specifier|public
class|class
name|ExternalMapReduce
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
comment|// do nothing
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
DECL|class|MapClass
specifier|public
specifier|static
class|class
name|MapClass
extends|extends
name|MapReduceBase
implements|implements
name|Mapper
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|,
name|WritableComparable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|map (WritableComparable key, Writable value, OutputCollector<WritableComparable, IntWritable> output, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Writable
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|WritableComparable
argument_list|,
name|IntWritable
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
comment|//check for classpath
name|String
name|classpath
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|classpath
operator|.
name|indexOf
argument_list|(
literal|"testjob.jar"
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to find in the library "
operator|+
name|classpath
argument_list|)
throw|;
block|}
if|if
condition|(
name|classpath
operator|.
name|indexOf
argument_list|(
literal|"test.jar"
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failed to find the library test.jar in"
operator|+
name|classpath
argument_list|)
throw|;
block|}
comment|//fork off ls to see if the file exists.
comment|// java file.exists() will not work on
comment|// Windows since it is a symlink
name|String
index|[]
name|argv
init|=
operator|new
name|String
index|[
literal|7
index|]
decl_stmt|;
name|argv
index|[
literal|0
index|]
operator|=
literal|"ls"
expr_stmt|;
name|argv
index|[
literal|1
index|]
operator|=
literal|"files_tmp"
expr_stmt|;
name|argv
index|[
literal|2
index|]
operator|=
literal|"localfilelink"
expr_stmt|;
name|argv
index|[
literal|3
index|]
operator|=
literal|"dfsfilelink"
expr_stmt|;
name|argv
index|[
literal|4
index|]
operator|=
literal|"tarlink"
expr_stmt|;
name|argv
index|[
literal|5
index|]
operator|=
literal|"ziplink"
expr_stmt|;
name|argv
index|[
literal|6
index|]
operator|=
literal|"test.tgz"
expr_stmt|;
name|Process
name|p
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|argv
argument_list|)
decl_stmt|;
name|int
name|ret
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|ret
operator|=
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|//do nothing here.
block|}
if|if
condition|(
name|ret
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"files_tmp does not exist"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|Reduce
specifier|public
specifier|static
class|class
name|Reduce
extends|extends
name|MapReduceBase
implements|implements
name|Reducer
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|,
name|WritableComparable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|reduce (WritableComparable key, Iterator<Writable> values, OutputCollector<WritableComparable, IntWritable> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Iterator
argument_list|<
name|Writable
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|WritableComparable
argument_list|,
name|IntWritable
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
comment|//do nothing
block|}
block|}
DECL|method|run (String[] argv)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ExternalMapReduce<input><output>"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|argv
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|Path
name|input
init|=
operator|new
name|Path
argument_list|(
name|argv
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|JobConf
name|testConf
init|=
operator|new
name|JobConf
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|ExternalMapReduce
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//try to load a class from libjar
try|try
block|{
name|testConf
operator|.
name|getClassByName
argument_list|(
literal|"testjar.ClassWordCount"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Could not find class from libjar"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|testConf
operator|.
name|setJobName
argument_list|(
literal|"external job"
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|testConf
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|testConf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|testConf
operator|.
name|setMapperClass
argument_list|(
name|MapClass
operator|.
name|class
argument_list|)
expr_stmt|;
name|testConf
operator|.
name|setReducerClass
argument_list|(
name|Reduce
operator|.
name|class
argument_list|)
expr_stmt|;
name|testConf
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|JobClient
operator|.
name|runJob
argument_list|(
name|testConf
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|ExternalMapReduce
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

