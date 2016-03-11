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
name|SequenceFile
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
name|io
operator|.
name|serializer
operator|.
name|JavaSerializationComparator
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
name|Counters
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
name|SequenceFileOutputFormat
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
name|junit
operator|.
name|After
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
name|BufferedReader
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
name|InputStreamReader
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
name|assertEquals
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
DECL|class|TestMultipleOutputs
specifier|public
class|class
name|TestMultipleOutputs
extends|extends
name|HadoopTestCase
block|{
DECL|method|TestMultipleOutputs ()
specifier|public
name|TestMultipleOutputs
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
DECL|method|testWithoutCounters ()
specifier|public
name|void
name|testWithoutCounters
parameter_list|()
throws|throws
name|Exception
block|{
name|_testMultipleOutputs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|_testMOWithJavaSerialization
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithCounters ()
specifier|public
name|void
name|testWithCounters
parameter_list|()
throws|throws
name|Exception
block|{
name|_testMultipleOutputs
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|_testMOWithJavaSerialization
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|field|ROOT_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"testing/mo"
argument_list|)
decl_stmt|;
DECL|field|IN_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|IN_DIR
init|=
operator|new
name|Path
argument_list|(
name|ROOT_DIR
argument_list|,
literal|"input"
argument_list|)
decl_stmt|;
DECL|field|OUT_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|OUT_DIR
init|=
operator|new
name|Path
argument_list|(
name|ROOT_DIR
argument_list|,
literal|"output"
argument_list|)
decl_stmt|;
DECL|method|getDir (Path dir)
specifier|private
name|Path
name|getDir
parameter_list|(
name|Path
name|dir
parameter_list|)
block|{
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
name|dir
operator|=
operator|new
name|Path
argument_list|(
name|localPathRoot
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
return|return
name|dir
return|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|Path
name|rootDir
init|=
name|getDir
argument_list|(
name|ROOT_DIR
argument_list|)
decl_stmt|;
name|Path
name|inDir
init|=
name|getDir
argument_list|(
name|IN_DIR
argument_list|)
decl_stmt|;
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
name|rootDir
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
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|rootDir
init|=
name|getDir
argument_list|(
name|ROOT_DIR
argument_list|)
decl_stmt|;
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
name|rootDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|_testMOWithJavaSerialization (boolean withCounters)
specifier|protected
name|void
name|_testMOWithJavaSerialization
parameter_list|(
name|boolean
name|withCounters
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|inDir
init|=
name|getDir
argument_list|(
name|IN_DIR
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
name|getDir
argument_list|(
name|OUT_DIR
argument_list|)
decl_stmt|;
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
name|fs
operator|.
name|delete
argument_list|(
name|inDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|outDir
argument_list|,
literal|true
argument_list|)
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
literal|"mo"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"io.serializations"
argument_list|,
literal|"org.apache.hadoop.io.serializer.JavaSerialization,"
operator|+
literal|"org.apache.hadoop.io.serializer.WritableSerialization"
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
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapOutputValueClass
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputKeyComparatorClass
argument_list|(
name|JavaSerializationComparator
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputValueClass
argument_list|(
name|String
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
name|MultipleOutputs
operator|.
name|addNamedOutput
argument_list|(
name|conf
argument_list|,
literal|"text"
argument_list|,
name|TextOutputFormat
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|MultipleOutputs
operator|.
name|setCountersEnabled
argument_list|(
name|conf
argument_list|,
name|withCounters
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|MOJavaSerDeMap
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|MOJavaSerDeReduce
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
comment|// assert number of named output part files
name|int
name|namedOutputCount
init|=
literal|0
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
if|if
condition|(
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
literal|"text-m-00000"
argument_list|)
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
literal|"text-r-00000"
argument_list|)
condition|)
block|{
name|namedOutputCount
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|namedOutputCount
argument_list|)
expr_stmt|;
comment|// assert TextOutputFormat files correctness
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|FileOutputFormat
operator|.
name|getOutputPath
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"text-r-00000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|line
operator|.
name|endsWith
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|count
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Counters
operator|.
name|Group
name|counters
init|=
name|job
operator|.
name|getCounters
argument_list|()
operator|.
name|getGroup
argument_list|(
name|MultipleOutputs
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|withCounters
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|counters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counters
operator|.
name|getCounter
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|_testMultipleOutputs (boolean withCounters)
specifier|protected
name|void
name|_testMultipleOutputs
parameter_list|(
name|boolean
name|withCounters
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|inDir
init|=
name|getDir
argument_list|(
name|IN_DIR
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
name|getDir
argument_list|(
name|OUT_DIR
argument_list|)
decl_stmt|;
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
literal|"mo"
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
name|MultipleOutputs
operator|.
name|addNamedOutput
argument_list|(
name|conf
argument_list|,
literal|"text"
argument_list|,
name|TextOutputFormat
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
argument_list|)
expr_stmt|;
name|MultipleOutputs
operator|.
name|addMultiNamedOutput
argument_list|(
name|conf
argument_list|,
literal|"sequence"
argument_list|,
name|SequenceFileOutputFormat
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
argument_list|)
expr_stmt|;
name|MultipleOutputs
operator|.
name|setCountersEnabled
argument_list|(
name|conf
argument_list|,
name|withCounters
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|MOMap
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|MOReduce
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
comment|// assert number of named output part files
name|int
name|namedOutputCount
init|=
literal|0
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
if|if
condition|(
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
literal|"text-m-00000"
argument_list|)
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
literal|"text-m-00001"
argument_list|)
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
literal|"text-r-00000"
argument_list|)
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
literal|"sequence_A-m-00000"
argument_list|)
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
literal|"sequence_A-m-00001"
argument_list|)
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
literal|"sequence_B-m-00000"
argument_list|)
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
literal|"sequence_B-m-00001"
argument_list|)
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
literal|"sequence_B-r-00000"
argument_list|)
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
literal|"sequence_C-r-00000"
argument_list|)
condition|)
block|{
name|namedOutputCount
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|namedOutputCount
argument_list|)
expr_stmt|;
comment|// assert TextOutputFormat files correctness
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|FileOutputFormat
operator|.
name|getOutputPath
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"text-r-00000"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|line
operator|.
name|endsWith
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|count
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// assert SequenceOutputFormat files correctness
name|SequenceFile
operator|.
name|Reader
name|seqReader
init|=
operator|new
name|SequenceFile
operator|.
name|Reader
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|FileOutputFormat
operator|.
name|getOutputPath
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"sequence_B-r-00000"
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|,
name|seqReader
operator|.
name|getKeyClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Text
operator|.
name|class
argument_list|,
name|seqReader
operator|.
name|getValueClass
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|LongWritable
name|key
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
name|Text
name|value
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
while|while
condition|(
name|seqReader
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"sequence"
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|seqReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|count
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Counters
operator|.
name|Group
name|counters
init|=
name|job
operator|.
name|getCounters
argument_list|()
operator|.
name|getGroup
argument_list|(
name|MultipleOutputs
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|withCounters
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|counters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|counters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|counters
operator|.
name|getCounter
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counters
operator|.
name|getCounter
argument_list|(
literal|"sequence_A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|counters
operator|.
name|getCounter
argument_list|(
literal|"sequence_B"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counters
operator|.
name|getCounter
argument_list|(
literal|"sequence_C"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|class|MOMap
specifier|public
specifier|static
class|class
name|MOMap
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
DECL|field|mos
specifier|private
name|MultipleOutputs
name|mos
decl_stmt|;
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|mos
operator|=
operator|new
name|MultipleOutputs
argument_list|(
name|conf
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
operator|!
name|value
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"a"
argument_list|)
condition|)
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
else|else
block|{
name|mos
operator|.
name|getCollector
argument_list|(
literal|"text"
argument_list|,
name|reporter
argument_list|)
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|Text
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|mos
operator|.
name|getCollector
argument_list|(
literal|"sequence"
argument_list|,
literal|"A"
argument_list|,
name|reporter
argument_list|)
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|Text
argument_list|(
literal|"sequence"
argument_list|)
argument_list|)
expr_stmt|;
name|mos
operator|.
name|getCollector
argument_list|(
literal|"sequence"
argument_list|,
literal|"B"
argument_list|,
name|reporter
argument_list|)
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|Text
argument_list|(
literal|"sequence"
argument_list|)
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
block|{
name|mos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|class|MOReduce
specifier|public
specifier|static
class|class
name|MOReduce
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
DECL|field|mos
specifier|private
name|MultipleOutputs
name|mos
decl_stmt|;
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|mos
operator|=
operator|new
name|MultipleOutputs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|!
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
else|else
block|{
name|mos
operator|.
name|getCollector
argument_list|(
literal|"text"
argument_list|,
name|reporter
argument_list|)
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|Text
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|mos
operator|.
name|getCollector
argument_list|(
literal|"sequence"
argument_list|,
literal|"B"
argument_list|,
name|reporter
argument_list|)
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|Text
argument_list|(
literal|"sequence"
argument_list|)
argument_list|)
expr_stmt|;
name|mos
operator|.
name|getCollector
argument_list|(
literal|"sequence"
argument_list|,
literal|"C"
argument_list|,
name|reporter
argument_list|)
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|Text
argument_list|(
literal|"sequence"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|mos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|class|MOJavaSerDeMap
specifier|public
specifier|static
class|class
name|MOJavaSerDeMap
implements|implements
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|Long
argument_list|,
name|String
argument_list|>
block|{
DECL|field|mos
specifier|private
name|MultipleOutputs
name|mos
decl_stmt|;
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|mos
operator|=
operator|new
name|MultipleOutputs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|map (LongWritable key, Text value, OutputCollector<Long, String> output, Reporter reporter)
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
name|Long
argument_list|,
name|String
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
operator|!
name|value
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"a"
argument_list|)
condition|)
block|{
name|output
operator|.
name|collect
argument_list|(
name|key
operator|.
name|get
argument_list|()
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mos
operator|.
name|getCollector
argument_list|(
literal|"text"
argument_list|,
name|reporter
argument_list|)
operator|.
name|collect
argument_list|(
name|key
argument_list|,
literal|"text"
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
block|{
name|mos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|class|MOJavaSerDeReduce
specifier|public
specifier|static
class|class
name|MOJavaSerDeReduce
implements|implements
name|Reducer
argument_list|<
name|Long
argument_list|,
name|String
argument_list|,
name|Long
argument_list|,
name|String
argument_list|>
block|{
DECL|field|mos
specifier|private
name|MultipleOutputs
name|mos
decl_stmt|;
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|mos
operator|=
operator|new
name|MultipleOutputs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|reduce (Long key, Iterator<String> values, OutputCollector<Long, String> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|Long
name|key
parameter_list|,
name|Iterator
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|Long
argument_list|,
name|String
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
name|String
name|value
init|=
name|values
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
literal|"b"
argument_list|)
condition|)
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
else|else
block|{
name|mos
operator|.
name|getCollector
argument_list|(
literal|"text"
argument_list|,
name|reporter
argument_list|)
operator|.
name|collect
argument_list|(
name|key
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|mos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

