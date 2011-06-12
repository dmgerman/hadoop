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
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
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
name|conf
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestSequenceFileInputFormat
specifier|public
class|class
name|TestSequenceFileInputFormat
extends|extends
name|TestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|FileInputFormat
operator|.
name|LOG
decl_stmt|;
DECL|field|MAX_LENGTH
specifier|private
specifier|static
name|int
name|MAX_LENGTH
init|=
literal|10000
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|testFormat ()
specifier|public
name|void
name|testFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
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
literal|"test.seq"
argument_list|)
decl_stmt|;
name|Reporter
name|reporter
init|=
name|Reporter
operator|.
name|NULL
decl_stmt|;
name|int
name|seed
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
comment|//LOG.info("seed = "+seed);
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
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
comment|// for a variety of lengths
for|for
control|(
name|int
name|length
init|=
literal|0
init|;
name|length
operator|<
name|MAX_LENGTH
condition|;
name|length
operator|+=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_LENGTH
operator|/
literal|10
argument_list|)
operator|+
literal|1
control|)
block|{
comment|//LOG.info("creating; entries = " + length);
comment|// create a file with length entries
name|SequenceFile
operator|.
name|Writer
name|writer
init|=
name|SequenceFile
operator|.
name|createWriter
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|,
name|file
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|BytesWritable
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IntWritable
name|key
init|=
operator|new
name|IntWritable
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|BytesWritable
name|value
init|=
operator|new
name|BytesWritable
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|key
argument_list|,
name|value
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
comment|// try splitting the file in a variety of sizes
name|InputFormat
argument_list|<
name|IntWritable
argument_list|,
name|BytesWritable
argument_list|>
name|format
init|=
operator|new
name|SequenceFileInputFormat
argument_list|<
name|IntWritable
argument_list|,
name|BytesWritable
argument_list|>
argument_list|()
decl_stmt|;
name|IntWritable
name|key
init|=
operator|new
name|IntWritable
argument_list|()
decl_stmt|;
name|BytesWritable
name|value
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numSplits
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_LENGTH
operator|/
operator|(
name|SequenceFile
operator|.
name|SYNC_INTERVAL
operator|/
literal|20
operator|)
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|//LOG.info("splitting: requesting = " + numSplits);
name|InputSplit
index|[]
name|splits
init|=
name|format
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
name|numSplits
argument_list|)
decl_stmt|;
comment|//LOG.info("splitting: got =        " + splits.length);
comment|// check each split
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|splits
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|RecordReader
argument_list|<
name|IntWritable
argument_list|,
name|BytesWritable
argument_list|>
name|reader
init|=
name|format
operator|.
name|getRecordReader
argument_list|(
name|splits
index|[
name|j
index|]
argument_list|,
name|job
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
comment|// if (bits.get(key.get())) {
comment|// LOG.info("splits["+j+"]="+splits[j]+" : " + key.get());
comment|// LOG.info("@"+reader.getPos());
comment|// }
name|assertFalse
argument_list|(
literal|"Key in multiple partitions."
argument_list|,
name|bits
operator|.
name|get
argument_list|(
name|key
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|key
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
comment|//LOG.info("splits["+j+"]="+splits[j]+" count=" + count);
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
literal|"Some keys in no partition."
argument_list|,
name|length
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
operator|new
name|TestSequenceFileInputFormat
argument_list|()
operator|.
name|testFormat
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

