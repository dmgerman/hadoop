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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|NullWritable
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
name|WritableComparator
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
name|TaskCounter
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Formatter
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
name|assertTrue
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
name|fail
import|;
end_import

begin_class
DECL|class|TestReduceFetchFromPartialMem
specifier|public
class|class
name|TestReduceFetchFromPartialMem
block|{
DECL|field|mrCluster
specifier|protected
specifier|static
name|MiniMRCluster
name|mrCluster
init|=
literal|null
decl_stmt|;
DECL|field|dfsCluster
specifier|protected
specifier|static
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|dfsCluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|mrCluster
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|2
argument_list|,
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|tagfmt
specifier|private
specifier|static
specifier|final
name|String
name|tagfmt
init|=
literal|"%04d"
decl_stmt|;
DECL|field|keyfmt
specifier|private
specifier|static
specifier|final
name|String
name|keyfmt
init|=
literal|"KEYKEYKEYKEYKEYKEYKE"
decl_stmt|;
DECL|field|keylen
specifier|private
specifier|static
specifier|final
name|int
name|keylen
init|=
name|keyfmt
operator|.
name|length
argument_list|()
decl_stmt|;
DECL|method|getValLen (int id, int nMaps)
specifier|private
specifier|static
name|int
name|getValLen
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|nMaps
parameter_list|)
block|{
return|return
literal|4096
operator|/
name|nMaps
operator|*
operator|(
name|id
operator|+
literal|1
operator|)
return|;
block|}
comment|/** Verify that at least one segment does not hit disk */
annotation|@
name|Test
DECL|method|testReduceFromPartialMem ()
specifier|public
name|void
name|testReduceFromPartialMem
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|MAP_TASKS
init|=
literal|7
decl_stmt|;
name|JobConf
name|job
init|=
name|mrCluster
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
name|job
operator|.
name|setNumMapTasks
argument_list|(
name|MAP_TASKS
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|REDUCE_MERGE_INMEM_THRESHOLD
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|REDUCE_INPUT_BUFFER_PERCENT
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|SHUFFLE_PARALLEL_COPIES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|IO_SORT_MB
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
argument_list|,
literal|"-Xmx128m"
argument_list|)
expr_stmt|;
name|job
operator|.
name|setLong
argument_list|(
name|JobContext
operator|.
name|REDUCE_MEMORY_TOTAL_BYTES
argument_list|,
literal|128
operator|<<
literal|20
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|SHUFFLE_INPUT_BUFFER_PERCENT
argument_list|,
literal|"0.14"
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|SHUFFLE_MERGE_PERCENT
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|Counters
name|c
init|=
name|runJob
argument_list|(
name|job
argument_list|)
decl_stmt|;
specifier|final
name|long
name|out
init|=
name|c
operator|.
name|findCounter
argument_list|(
name|TaskCounter
operator|.
name|MAP_OUTPUT_RECORDS
argument_list|)
operator|.
name|getCounter
argument_list|()
decl_stmt|;
specifier|final
name|long
name|spill
init|=
name|c
operator|.
name|findCounter
argument_list|(
name|TaskCounter
operator|.
name|SPILLED_RECORDS
argument_list|)
operator|.
name|getCounter
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected some records not spilled during reduce"
operator|+
name|spill
operator|+
literal|")"
argument_list|,
name|spill
operator|<
literal|2
operator|*
name|out
argument_list|)
expr_stmt|;
comment|// spilled map records, some records at the reduce
block|}
comment|/**    * Emit 4096 small keys, 2&quot;tagged&quot; keys. Emits a fixed amount of    * data so the in-memory fetch semantics can be tested.    */
DECL|class|MapMB
specifier|public
specifier|static
class|class
name|MapMB
implements|implements
name|Mapper
argument_list|<
name|NullWritable
argument_list|,
name|NullWritable
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|field|nMaps
specifier|private
name|int
name|nMaps
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|Text
name|key
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|field|val
specifier|private
specifier|final
name|Text
name|val
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|field|b
specifier|private
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
DECL|field|fmt
specifier|private
specifier|final
name|Formatter
name|fmt
init|=
operator|new
name|Formatter
argument_list|(
operator|new
name|StringBuilder
argument_list|(
literal|25
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|nMaps
operator|=
name|conf
operator|.
name|getNumMapTasks
argument_list|()
expr_stmt|;
name|id
operator|=
name|nMaps
operator|-
name|conf
operator|.
name|getInt
argument_list|(
name|JobContext
operator|.
name|TASK_PARTITION
argument_list|,
operator|-
literal|1
argument_list|)
operator|-
literal|1
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|4096
argument_list|,
operator|(
name|byte
operator|)
literal|'V'
argument_list|)
expr_stmt|;
operator|(
operator|(
name|StringBuilder
operator|)
name|fmt
operator|.
name|out
argument_list|()
operator|)
operator|.
name|append
argument_list|(
name|keyfmt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|map (NullWritable nk, NullWritable nv, OutputCollector<Text, Text> output, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|NullWritable
name|nk
parameter_list|,
name|NullWritable
name|nv
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
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
comment|// Emit 4096 fixed-size records
name|val
operator|.
name|set
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|val
operator|.
name|getBytes
argument_list|()
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|id
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4096
condition|;
operator|++
name|i
control|)
block|{
name|key
operator|.
name|set
argument_list|(
name|fmt
operator|.
name|format
argument_list|(
name|tagfmt
argument_list|,
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
operator|(
operator|(
name|StringBuilder
operator|)
name|fmt
operator|.
name|out
argument_list|()
operator|)
operator|.
name|setLength
argument_list|(
name|keylen
argument_list|)
expr_stmt|;
block|}
comment|// Emit two "tagged" records from the map. To validate the merge, segments
comment|// should have both a small and large record such that reading a large
comment|// record from an on-disk segment into an in-memory segment will write
comment|// over the beginning of a record in the in-memory segment, causing the
comment|// merge and/or validation to fail.
comment|// Add small, tagged record
name|val
operator|.
name|set
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|getValLen
argument_list|(
name|id
argument_list|,
name|nMaps
argument_list|)
operator|-
literal|128
argument_list|)
expr_stmt|;
name|val
operator|.
name|getBytes
argument_list|()
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|id
expr_stmt|;
operator|(
operator|(
name|StringBuilder
operator|)
name|fmt
operator|.
name|out
argument_list|()
operator|)
operator|.
name|setLength
argument_list|(
name|keylen
argument_list|)
expr_stmt|;
name|key
operator|.
name|set
argument_list|(
literal|"A"
operator|+
name|fmt
operator|.
name|format
argument_list|(
name|tagfmt
argument_list|,
name|id
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
comment|// Add large, tagged record
name|val
operator|.
name|set
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|getValLen
argument_list|(
name|id
argument_list|,
name|nMaps
argument_list|)
argument_list|)
expr_stmt|;
name|val
operator|.
name|getBytes
argument_list|()
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|id
expr_stmt|;
operator|(
operator|(
name|StringBuilder
operator|)
name|fmt
operator|.
name|out
argument_list|()
operator|)
operator|.
name|setLength
argument_list|(
name|keylen
argument_list|)
expr_stmt|;
name|key
operator|.
name|set
argument_list|(
literal|"B"
operator|+
name|fmt
operator|.
name|format
argument_list|(
name|tagfmt
argument_list|,
name|id
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|val
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
block|{ }
block|}
comment|/**    * Confirm that each small key is emitted once by all maps, each tagged key    * is emitted by only one map, all IDs are consistent with record data, and    * all non-ID record data is consistent.    */
DECL|class|MBValidate
specifier|public
specifier|static
class|class
name|MBValidate
implements|implements
name|Reducer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|nMaps
specifier|private
specifier|static
name|int
name|nMaps
decl_stmt|;
DECL|field|vb
specifier|private
specifier|static
specifier|final
name|Text
name|vb
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
static|static
block|{
name|byte
index|[]
name|v
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|v
argument_list|,
operator|(
name|byte
operator|)
literal|'V'
argument_list|)
expr_stmt|;
name|vb
operator|.
name|set
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
DECL|field|nRec
specifier|private
name|int
name|nRec
init|=
literal|0
decl_stmt|;
DECL|field|nKey
specifier|private
name|int
name|nKey
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|aKey
specifier|private
name|int
name|aKey
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|bKey
specifier|private
name|int
name|bKey
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|kb
specifier|private
specifier|final
name|Text
name|kb
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|field|fmt
specifier|private
specifier|final
name|Formatter
name|fmt
init|=
operator|new
name|Formatter
argument_list|(
operator|new
name|StringBuilder
argument_list|(
literal|25
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|nMaps
operator|=
name|conf
operator|.
name|getNumMapTasks
argument_list|()
expr_stmt|;
operator|(
operator|(
name|StringBuilder
operator|)
name|fmt
operator|.
name|out
argument_list|()
operator|)
operator|.
name|append
argument_list|(
name|keyfmt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reduce (Text key, Iterator<Text> values, OutputCollector<Text,Text> out, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|Text
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
name|Text
argument_list|,
name|Text
argument_list|>
name|out
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|vc
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|vlen
decl_stmt|;
specifier|final
name|int
name|preRec
init|=
name|nRec
decl_stmt|;
specifier|final
name|int
name|vcCheck
decl_stmt|,
name|recCheck
decl_stmt|;
operator|(
operator|(
name|StringBuilder
operator|)
name|fmt
operator|.
name|out
argument_list|()
operator|)
operator|.
name|setLength
argument_list|(
name|keylen
argument_list|)
expr_stmt|;
if|if
condition|(
literal|25
operator|==
name|key
operator|.
name|getLength
argument_list|()
condition|)
block|{
comment|// tagged record
name|recCheck
operator|=
literal|1
expr_stmt|;
comment|// expect only 1 record
switch|switch
condition|(
operator|(
name|char
operator|)
name|key
operator|.
name|getBytes
argument_list|()
index|[
literal|0
index|]
condition|)
block|{
case|case
literal|'A'
case|:
name|vlen
operator|=
name|getValLen
argument_list|(
operator|++
name|aKey
argument_list|,
name|nMaps
argument_list|)
operator|-
literal|128
expr_stmt|;
name|vcCheck
operator|=
name|aKey
expr_stmt|;
comment|// expect eq id
break|break;
case|case
literal|'B'
case|:
name|vlen
operator|=
name|getValLen
argument_list|(
operator|++
name|bKey
argument_list|,
name|nMaps
argument_list|)
expr_stmt|;
name|vcCheck
operator|=
name|bKey
expr_stmt|;
comment|// expect eq id
break|break;
default|default:
name|vlen
operator|=
name|vcCheck
operator|=
operator|-
literal|1
expr_stmt|;
name|fail
argument_list|(
literal|"Unexpected tag on record: "
operator|+
operator|(
operator|(
name|char
operator|)
name|key
operator|.
name|getBytes
argument_list|()
index|[
literal|24
index|]
operator|)
argument_list|)
expr_stmt|;
block|}
name|kb
operator|.
name|set
argument_list|(
operator|(
name|char
operator|)
name|key
operator|.
name|getBytes
argument_list|()
index|[
literal|0
index|]
operator|+
name|fmt
operator|.
name|format
argument_list|(
name|tagfmt
argument_list|,
name|vcCheck
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|kb
operator|.
name|set
argument_list|(
name|fmt
operator|.
name|format
argument_list|(
name|tagfmt
argument_list|,
operator|++
name|nKey
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|vlen
operator|=
literal|1000
expr_stmt|;
name|recCheck
operator|=
name|nMaps
expr_stmt|;
comment|// expect 1 rec per map
name|vcCheck
operator|=
operator|(
name|nMaps
operator|*
operator|(
name|nMaps
operator|-
literal|1
operator|)
operator|)
operator|>>>
literal|1
expr_stmt|;
comment|// expect eq sum(id)
block|}
name|assertEquals
argument_list|(
name|kb
argument_list|,
name|key
argument_list|)
expr_stmt|;
while|while
condition|(
name|values
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Text
name|val
init|=
name|values
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// increment vc by map ID assoc w/ val
name|vc
operator|+=
name|val
operator|.
name|getBytes
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
comment|// verify that all the fixed characters 'V' match
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|vb
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|1
argument_list|,
name|vlen
operator|-
literal|1
argument_list|,
name|val
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|1
argument_list|,
name|val
operator|.
name|getLength
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|collect
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
operator|++
name|nRec
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Bad rec count for "
operator|+
name|key
argument_list|,
name|recCheck
argument_list|,
name|nRec
operator|-
name|preRec
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bad rec group for "
operator|+
name|key
argument_list|,
name|vcCheck
argument_list|,
name|vc
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
block|{
name|assertEquals
argument_list|(
literal|4095
argument_list|,
name|nKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nMaps
operator|-
literal|1
argument_list|,
name|aKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nMaps
operator|-
literal|1
argument_list|,
name|bKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bad record count"
argument_list|,
name|nMaps
operator|*
operator|(
literal|4096
operator|+
literal|2
operator|)
argument_list|,
name|nRec
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FakeSplit
specifier|public
specifier|static
class|class
name|FakeSplit
implements|implements
name|InputSplit
block|{
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
block|{ }
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
block|{ }
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
block|}
DECL|class|FakeIF
specifier|public
specifier|static
class|class
name|FakeIF
implements|implements
name|InputFormat
argument_list|<
name|NullWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
DECL|method|FakeIF ()
specifier|public
name|FakeIF
parameter_list|()
block|{ }
DECL|method|getSplits (JobConf conf, int numSplits)
specifier|public
name|InputSplit
index|[]
name|getSplits
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|int
name|numSplits
parameter_list|)
block|{
name|InputSplit
index|[]
name|splits
init|=
operator|new
name|InputSplit
index|[
name|numSplits
index|]
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
name|splits
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|splits
index|[
name|i
index|]
operator|=
operator|new
name|FakeSplit
argument_list|()
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
DECL|method|getRecordReader ( InputSplit ignored, JobConf conf, Reporter reporter)
specifier|public
name|RecordReader
argument_list|<
name|NullWritable
argument_list|,
name|NullWritable
argument_list|>
name|getRecordReader
parameter_list|(
name|InputSplit
name|ignored
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
block|{
return|return
operator|new
name|RecordReader
argument_list|<
name|NullWritable
argument_list|,
name|NullWritable
argument_list|>
argument_list|()
block|{
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
specifier|public
name|boolean
name|next
parameter_list|(
name|NullWritable
name|key
parameter_list|,
name|NullWritable
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|done
condition|)
return|return
literal|false
return|;
name|done
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|NullWritable
name|createKey
parameter_list|()
block|{
return|return
name|NullWritable
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|NullWritable
name|createValue
parameter_list|()
block|{
return|return
name|NullWritable
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0L
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{ }
specifier|public
name|float
name|getProgress
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0.0f
return|;
block|}
block|}
return|;
block|}
block|}
DECL|method|runJob (JobConf conf)
specifier|public
specifier|static
name|Counters
name|runJob
parameter_list|(
name|JobConf
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setMapperClass
argument_list|(
name|MapMB
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|MBValidate
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
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
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInputFormat
argument_list|(
name|FakeIF
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumTasksToExecutePerJvm
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|REDUCE_MAX_ATTEMPTS
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/in"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|outp
init|=
operator|new
name|Path
argument_list|(
literal|"/out"
argument_list|)
decl_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outp
argument_list|)
expr_stmt|;
name|RunningJob
name|job
init|=
literal|null
decl_stmt|;
try|try
block|{
name|job
operator|=
name|JobClient
operator|.
name|runJob
argument_list|(
name|conf
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
finally|finally
block|{
name|FileSystem
name|fs
init|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|outp
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|outp
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|job
operator|.
name|getCounters
argument_list|()
return|;
block|}
block|}
end_class

end_unit

