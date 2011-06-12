begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.input
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
name|input
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
name|Random
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
name|*
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
name|*
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
name|InputFormat
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
name|TaskAttemptContext
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
name|task
operator|.
name|MapContextImpl
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestMRSequenceFileAsBinaryInputFormat
specifier|public
class|class
name|TestMRSequenceFileAsBinaryInputFormat
extends|extends
name|TestCase
block|{
DECL|field|RECORDS
specifier|private
specifier|static
specifier|final
name|int
name|RECORDS
init|=
literal|10000
decl_stmt|;
DECL|method|testBinary ()
specifier|public
name|void
name|testBinary
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"."
argument_list|)
operator|+
literal|"/mapred"
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"testbinary.seq"
argument_list|)
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|Text
name|tkey
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|Text
name|tval
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|SequenceFile
operator|.
name|Writer
name|writer
init|=
operator|new
name|SequenceFile
operator|.
name|Writer
argument_list|(
name|fs
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|file
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|RECORDS
condition|;
operator|++
name|i
control|)
block|{
name|tkey
operator|.
name|set
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|36
argument_list|)
argument_list|)
expr_stmt|;
name|tval
operator|.
name|set
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|,
literal|36
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|tkey
argument_list|,
name|tval
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|TaskAttemptContext
name|context
init|=
name|MapReduceTestUtil
operator|.
name|createDummyMapTaskAttemptContext
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|InputFormat
argument_list|<
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|>
name|bformat
init|=
operator|new
name|SequenceFileAsBinaryInputFormat
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|BytesWritable
name|bkey
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|BytesWritable
name|bval
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|Text
name|cmpkey
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|Text
name|cmpval
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|DataInputBuffer
name|buf
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|file
argument_list|)
expr_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
name|bformat
operator|.
name|getSplits
argument_list|(
name|job
argument_list|)
control|)
block|{
name|RecordReader
argument_list|<
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|>
name|reader
init|=
name|bformat
operator|.
name|createRecordReader
argument_list|(
name|split
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|MapContext
argument_list|<
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|>
name|mcontext
init|=
operator|new
name|MapContextImpl
argument_list|<
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|,
name|BytesWritable
argument_list|>
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|,
name|reader
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|MapReduceTestUtil
operator|.
name|createDummyReporter
argument_list|()
argument_list|,
name|split
argument_list|)
decl_stmt|;
name|reader
operator|.
name|initialize
argument_list|(
name|split
argument_list|,
name|mcontext
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|reader
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
name|bkey
operator|=
name|reader
operator|.
name|getCurrentKey
argument_list|()
expr_stmt|;
name|bval
operator|=
name|reader
operator|.
name|getCurrentValue
argument_list|()
expr_stmt|;
name|tkey
operator|.
name|set
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|36
argument_list|)
argument_list|)
expr_stmt|;
name|tval
operator|.
name|set
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|,
literal|36
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|reset
argument_list|(
name|bkey
operator|.
name|getBytes
argument_list|()
argument_list|,
name|bkey
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|cmpkey
operator|.
name|readFields
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|reset
argument_list|(
name|bval
operator|.
name|getBytes
argument_list|()
argument_list|,
name|bval
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|cmpval
operator|.
name|readFields
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Keys don't match: "
operator|+
literal|"*"
operator|+
name|cmpkey
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|tkey
operator|.
name|toString
argument_list|()
operator|+
literal|"*"
argument_list|,
name|cmpkey
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|tkey
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Vals don't match: "
operator|+
literal|"*"
operator|+
name|cmpval
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|tval
operator|.
name|toString
argument_list|()
operator|+
literal|"*"
argument_list|,
name|cmpval
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|tval
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|count
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Some records not found"
argument_list|,
name|RECORDS
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

