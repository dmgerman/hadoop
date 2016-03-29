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
name|junit
operator|.
name|Assert
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
name|RawComparator
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_class
DECL|class|TestOldCombinerGrouping
specifier|public
class|class
name|TestOldCombinerGrouping
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|class|Map
specifier|public
specifier|static
class|class
name|Map
implements|implements
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|map (LongWritable key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter)
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
name|Text
argument_list|,
name|LongWritable
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|v
init|=
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|k
init|=
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|v
operator|.
name|indexOf
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|v
operator|=
name|v
operator|.
name|substring
argument_list|(
name|v
operator|.
name|indexOf
argument_list|(
literal|","
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
operator|new
name|Text
argument_list|(
name|k
argument_list|)
argument_list|,
operator|new
name|LongWritable
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{     }
block|}
DECL|class|Reduce
specifier|public
specifier|static
class|class
name|Reduce
implements|implements
name|Reducer
argument_list|<
name|Text
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|reduce (Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|Text
name|key
parameter_list|,
name|Iterator
argument_list|<
name|LongWritable
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|LongWritable
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
name|maxValue
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|values
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|LongWritable
name|value
init|=
name|values
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxValue
operator|==
literal|null
condition|)
block|{
name|maxValue
operator|=
name|value
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|compareTo
argument_list|(
name|maxValue
argument_list|)
operator|>
literal|0
condition|)
block|{
name|maxValue
operator|=
name|value
expr_stmt|;
block|}
block|}
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{     }
block|}
DECL|class|Combiner
specifier|public
specifier|static
class|class
name|Combiner
extends|extends
name|Reduce
block|{   }
DECL|class|GroupComparator
specifier|public
specifier|static
class|class
name|GroupComparator
implements|implements
name|RawComparator
argument_list|<
name|Text
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (byte[] bytes, int i, int i2, byte[] bytes2, int i3, int i4)
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|i
parameter_list|,
name|int
name|i2
parameter_list|,
name|byte
index|[]
name|bytes2
parameter_list|,
name|int
name|i3
parameter_list|,
name|int
name|i4
parameter_list|)
block|{
name|byte
index|[]
name|b1
init|=
operator|new
name|byte
index|[
name|i2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
name|i
argument_list|,
name|b1
argument_list|,
literal|0
argument_list|,
name|i2
argument_list|)
expr_stmt|;
name|byte
index|[]
name|b2
init|=
operator|new
name|byte
index|[
name|i4
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes2
argument_list|,
name|i3
argument_list|,
name|b2
argument_list|,
literal|0
argument_list|,
name|i4
argument_list|)
expr_stmt|;
return|return
name|compare
argument_list|(
operator|new
name|Text
argument_list|(
operator|new
name|String
argument_list|(
name|b1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
operator|new
name|String
argument_list|(
name|b2
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compare (Text o1, Text o2)
specifier|public
name|int
name|compare
parameter_list|(
name|Text
name|o1
parameter_list|,
name|Text
name|o2
parameter_list|)
block|{
name|String
name|s1
init|=
name|o1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|o2
operator|.
name|toString
argument_list|()
decl_stmt|;
name|s1
operator|=
name|s1
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s1
operator|.
name|indexOf
argument_list|(
literal|"|"
argument_list|)
argument_list|)
expr_stmt|;
name|s2
operator|=
name|s2
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s2
operator|.
name|indexOf
argument_list|(
literal|"|"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCombiner ()
specifier|public
name|void
name|testCombiner
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not create test dir: "
operator|+
name|TEST_ROOT_DIR
argument_list|)
throw|;
block|}
name|File
name|in
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"input"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|in
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not create test dir: "
operator|+
name|in
argument_list|)
throw|;
block|}
name|File
name|out
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"output"
argument_list|)
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
operator|new
name|File
argument_list|(
name|in
argument_list|,
literal|"data.txt"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"A|a,1"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"A|b,2"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"B|a,3"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"B|b,4"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"B|c,5"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|job
operator|.
name|set
argument_list|(
literal|"mapreduce.framework.name"
argument_list|,
literal|"local"
argument_list|)
expr_stmt|;
name|TextInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|in
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TextOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|out
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|Map
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|Reduce
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueGroupingComparator
argument_list|(
name|GroupComparator
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setCombinerClass
argument_list|(
name|Combiner
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setCombinerKeyGroupingComparator
argument_list|(
name|GroupComparator
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
literal|"min.num.spills.for.combine"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|JobClient
name|client
init|=
operator|new
name|JobClient
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|RunningJob
name|runningJob
init|=
name|client
operator|.
name|submitJob
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|runningJob
operator|.
name|waitForCompletion
argument_list|()
expr_stmt|;
if|if
condition|(
name|runningJob
operator|.
name|isSuccessful
argument_list|()
condition|)
block|{
name|Counters
name|counters
init|=
name|runningJob
operator|.
name|getCounters
argument_list|()
decl_stmt|;
name|long
name|combinerInputRecords
init|=
name|counters
operator|.
name|getGroup
argument_list|(
literal|"org.apache.hadoop.mapreduce.TaskCounter"
argument_list|)
operator|.
name|getCounter
argument_list|(
literal|"COMBINE_INPUT_RECORDS"
argument_list|)
decl_stmt|;
name|long
name|combinerOutputRecords
init|=
name|counters
operator|.
name|getGroup
argument_list|(
literal|"org.apache.hadoop.mapreduce.TaskCounter"
argument_list|)
operator|.
name|getCounter
argument_list|(
literal|"COMBINE_OUTPUT_RECORDS"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|combinerInputRecords
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|combinerInputRecords
operator|>
name|combinerOutputRecords
argument_list|)
expr_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
operator|new
name|File
argument_list|(
name|out
argument_list|,
literal|"part-00000"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|output
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|+
name|line
operator|.
name|substring
argument_list|(
literal|4
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|output
operator|.
name|add
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|+
name|line
operator|.
name|substring
argument_list|(
literal|4
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"A2"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"B5"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Job failed"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

