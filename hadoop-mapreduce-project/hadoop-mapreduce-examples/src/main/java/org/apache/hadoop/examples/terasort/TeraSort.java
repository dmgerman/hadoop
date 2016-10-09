begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.terasort
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|terasort
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|Log
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
name|LogFactory
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
name|Configurable
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
name|JobContext
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Partitioner
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
name|output
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
comment|/**  * Generates the sampled split points, launches the job, and waits for it to  * finish.   *<p>  * To run the program:   *<b>bin/hadoop jar hadoop-*-examples.jar terasort in-dir out-dir</b>  */
end_comment

begin_class
DECL|class|TeraSort
specifier|public
class|class
name|TeraSort
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TeraSort
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A partitioner that splits text keys into roughly equal partitions    * in a global sorted order.    */
DECL|class|TotalOrderPartitioner
specifier|static
class|class
name|TotalOrderPartitioner
extends|extends
name|Partitioner
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|trie
specifier|private
name|TrieNode
name|trie
decl_stmt|;
DECL|field|splitPoints
specifier|private
name|Text
index|[]
name|splitPoints
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/**      * A generic trie node      */
DECL|class|TrieNode
specifier|static
specifier|abstract
class|class
name|TrieNode
block|{
DECL|field|level
specifier|private
name|int
name|level
decl_stmt|;
DECL|method|TrieNode (int level)
name|TrieNode
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
DECL|method|findPartition (Text key)
specifier|abstract
name|int
name|findPartition
parameter_list|(
name|Text
name|key
parameter_list|)
function_decl|;
DECL|method|print (PrintStream strm)
specifier|abstract
name|void
name|print
parameter_list|(
name|PrintStream
name|strm
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getLevel ()
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|level
return|;
block|}
block|}
comment|/**      * An inner trie node that contains 256 children based on the next      * character.      */
DECL|class|InnerTrieNode
specifier|static
class|class
name|InnerTrieNode
extends|extends
name|TrieNode
block|{
DECL|field|child
specifier|private
name|TrieNode
index|[]
name|child
init|=
operator|new
name|TrieNode
index|[
literal|256
index|]
decl_stmt|;
DECL|method|InnerTrieNode (int level)
name|InnerTrieNode
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|super
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
DECL|method|findPartition (Text key)
name|int
name|findPartition
parameter_list|(
name|Text
name|key
parameter_list|)
block|{
name|int
name|level
init|=
name|getLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|getLength
argument_list|()
operator|<=
name|level
condition|)
block|{
return|return
name|child
index|[
literal|0
index|]
operator|.
name|findPartition
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
name|child
index|[
name|key
operator|.
name|getBytes
argument_list|()
index|[
name|level
index|]
operator|&
literal|0xff
index|]
operator|.
name|findPartition
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|setChild (int idx, TrieNode child)
name|void
name|setChild
parameter_list|(
name|int
name|idx
parameter_list|,
name|TrieNode
name|child
parameter_list|)
block|{
name|this
operator|.
name|child
index|[
name|idx
index|]
operator|=
name|child
expr_stmt|;
block|}
DECL|method|print (PrintStream strm)
name|void
name|print
parameter_list|(
name|PrintStream
name|strm
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|ch
init|=
literal|0
init|;
name|ch
operator|<
literal|256
condition|;
operator|++
name|ch
control|)
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
literal|2
operator|*
name|getLevel
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|strm
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|strm
operator|.
name|print
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|strm
operator|.
name|println
argument_list|(
literal|" ->"
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
index|[
name|ch
index|]
operator|!=
literal|null
condition|)
block|{
name|child
index|[
name|ch
index|]
operator|.
name|print
argument_list|(
name|strm
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * A leaf trie node that does string compares to figure out where the given      * key belongs between lower..upper.      */
DECL|class|LeafTrieNode
specifier|static
class|class
name|LeafTrieNode
extends|extends
name|TrieNode
block|{
DECL|field|lower
name|int
name|lower
decl_stmt|;
DECL|field|upper
name|int
name|upper
decl_stmt|;
DECL|field|splitPoints
name|Text
index|[]
name|splitPoints
decl_stmt|;
DECL|method|LeafTrieNode (int level, Text[] splitPoints, int lower, int upper)
name|LeafTrieNode
parameter_list|(
name|int
name|level
parameter_list|,
name|Text
index|[]
name|splitPoints
parameter_list|,
name|int
name|lower
parameter_list|,
name|int
name|upper
parameter_list|)
block|{
name|super
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|this
operator|.
name|splitPoints
operator|=
name|splitPoints
expr_stmt|;
name|this
operator|.
name|lower
operator|=
name|lower
expr_stmt|;
name|this
operator|.
name|upper
operator|=
name|upper
expr_stmt|;
block|}
DECL|method|findPartition (Text key)
name|int
name|findPartition
parameter_list|(
name|Text
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|lower
init|;
name|i
operator|<
name|upper
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|splitPoints
index|[
name|i
index|]
operator|.
name|compareTo
argument_list|(
name|key
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
name|upper
return|;
block|}
DECL|method|print (PrintStream strm)
name|void
name|print
parameter_list|(
name|PrintStream
name|strm
parameter_list|)
throws|throws
name|IOException
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
literal|2
operator|*
name|getLevel
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|strm
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|strm
operator|.
name|print
argument_list|(
name|lower
argument_list|)
expr_stmt|;
name|strm
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|strm
operator|.
name|println
argument_list|(
name|upper
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Read the cut points from the given sequence file.      * @param fs the file system      * @param p the path to read      * @param conf the job config      * @return the strings to split the partitions on      * @throws IOException      */
DECL|method|readPartitions (FileSystem fs, Path p, Configuration conf)
specifier|private
specifier|static
name|Text
index|[]
name|readPartitions
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|reduces
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_REDUCES
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Text
index|[]
name|result
init|=
operator|new
name|Text
index|[
name|reduces
operator|-
literal|1
index|]
decl_stmt|;
name|DataInputStream
name|reader
init|=
name|fs
operator|.
name|open
argument_list|(
name|p
argument_list|)
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
name|reduces
operator|-
literal|1
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
name|result
index|[
name|i
index|]
operator|.
name|readFields
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Given a sorted set of cut points, build a trie that will find the correct      * partition quickly.      * @param splits the list of cut points      * @param lower the lower bound of partitions 0..numPartitions-1      * @param upper the upper bound of partitions 0..numPartitions-1      * @param prefix the prefix that we have already checked against      * @param maxDepth the maximum depth we will build a trie for      * @return the trie node that will divide the splits correctly      */
DECL|method|buildTrie (Text[] splits, int lower, int upper, Text prefix, int maxDepth)
specifier|private
specifier|static
name|TrieNode
name|buildTrie
parameter_list|(
name|Text
index|[]
name|splits
parameter_list|,
name|int
name|lower
parameter_list|,
name|int
name|upper
parameter_list|,
name|Text
name|prefix
parameter_list|,
name|int
name|maxDepth
parameter_list|)
block|{
name|int
name|depth
init|=
name|prefix
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|depth
operator|>=
name|maxDepth
operator|||
name|lower
operator|==
name|upper
condition|)
block|{
return|return
operator|new
name|LeafTrieNode
argument_list|(
name|depth
argument_list|,
name|splits
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|)
return|;
block|}
name|InnerTrieNode
name|result
init|=
operator|new
name|InnerTrieNode
argument_list|(
name|depth
argument_list|)
decl_stmt|;
name|Text
name|trial
init|=
operator|new
name|Text
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
comment|// append an extra byte on to the prefix
name|trial
operator|.
name|append
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|int
name|currentBound
init|=
name|lower
decl_stmt|;
for|for
control|(
name|int
name|ch
init|=
literal|0
init|;
name|ch
operator|<
literal|255
condition|;
operator|++
name|ch
control|)
block|{
name|trial
operator|.
name|getBytes
argument_list|()
index|[
name|depth
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|ch
operator|+
literal|1
argument_list|)
expr_stmt|;
name|lower
operator|=
name|currentBound
expr_stmt|;
while|while
condition|(
name|currentBound
operator|<
name|upper
condition|)
block|{
if|if
condition|(
name|splits
index|[
name|currentBound
index|]
operator|.
name|compareTo
argument_list|(
name|trial
argument_list|)
operator|>=
literal|0
condition|)
block|{
break|break;
block|}
name|currentBound
operator|+=
literal|1
expr_stmt|;
block|}
name|trial
operator|.
name|getBytes
argument_list|()
index|[
name|depth
index|]
operator|=
operator|(
name|byte
operator|)
name|ch
expr_stmt|;
name|result
operator|.
name|child
index|[
name|ch
index|]
operator|=
name|buildTrie
argument_list|(
name|splits
argument_list|,
name|lower
argument_list|,
name|currentBound
argument_list|,
name|trial
argument_list|,
name|maxDepth
argument_list|)
expr_stmt|;
block|}
comment|// pick up the rest
name|trial
operator|.
name|getBytes
argument_list|()
index|[
name|depth
index|]
operator|=
operator|(
name|byte
operator|)
literal|255
expr_stmt|;
name|result
operator|.
name|child
index|[
literal|255
index|]
operator|=
name|buildTrie
argument_list|(
name|splits
argument_list|,
name|currentBound
argument_list|,
name|upper
argument_list|,
name|trial
argument_list|,
name|maxDepth
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
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
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|Path
name|partFile
init|=
operator|new
name|Path
argument_list|(
name|TeraInputFormat
operator|.
name|PARTITION_FILENAME
argument_list|)
decl_stmt|;
name|splitPoints
operator|=
name|readPartitions
argument_list|(
name|fs
argument_list|,
name|partFile
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|trie
operator|=
name|buildTrie
argument_list|(
name|splitPoints
argument_list|,
literal|0
argument_list|,
name|splitPoints
operator|.
name|length
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can't read partitions file"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|TotalOrderPartitioner ()
specifier|public
name|TotalOrderPartitioner
parameter_list|()
block|{     }
DECL|method|getPartition (Text key, Text value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|Text
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
return|return
name|trie
operator|.
name|findPartition
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/**    * A total order partitioner that assigns keys based on their first     * PREFIX_LENGTH bytes, assuming a flat distribution.    */
DECL|class|SimplePartitioner
specifier|public
specifier|static
class|class
name|SimplePartitioner
extends|extends
name|Partitioner
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|prefixesPerReduce
name|int
name|prefixesPerReduce
decl_stmt|;
DECL|field|PREFIX_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|PREFIX_LENGTH
init|=
literal|3
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|prefixesPerReduce
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
literal|1
operator|<<
operator|(
literal|8
operator|*
name|PREFIX_LENGTH
operator|)
operator|)
operator|/
operator|(
name|float
operator|)
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_REDUCES
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|getPartition (Text key, Text value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|Text
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
name|key
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|PREFIX_LENGTH
argument_list|,
name|key
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|prefix
init|=
literal|0
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
name|len
condition|;
operator|++
name|i
control|)
block|{
name|prefix
operator|=
operator|(
name|prefix
operator|<<
literal|8
operator|)
operator||
operator|(
literal|0xff
operator|&
name|bytes
index|[
name|i
index|]
operator|)
expr_stmt|;
block|}
return|return
name|prefix
operator|/
name|prefixesPerReduce
return|;
block|}
block|}
DECL|method|getUseSimplePartitioner (JobContext job)
specifier|public
specifier|static
name|boolean
name|getUseSimplePartitioner
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|TeraSortConfigKeys
operator|.
name|USE_SIMPLE_PARTITIONER
operator|.
name|key
argument_list|()
argument_list|,
name|TeraSortConfigKeys
operator|.
name|DEFAULT_USE_SIMPLE_PARTITIONER
argument_list|)
return|;
block|}
DECL|method|setUseSimplePartitioner (Job job, boolean value)
specifier|public
specifier|static
name|void
name|setUseSimplePartitioner
parameter_list|(
name|Job
name|job
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|TeraSortConfigKeys
operator|.
name|USE_SIMPLE_PARTITIONER
operator|.
name|key
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getOutputReplication (JobContext job)
specifier|public
specifier|static
name|int
name|getOutputReplication
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|TeraSortConfigKeys
operator|.
name|OUTPUT_REPLICATION
operator|.
name|key
argument_list|()
argument_list|,
name|TeraSortConfigKeys
operator|.
name|DEFAULT_OUTPUT_REPLICATION
argument_list|)
return|;
block|}
DECL|method|setOutputReplication (Job job, int value)
specifier|public
specifier|static
name|void
name|setOutputReplication
parameter_list|(
name|Job
name|job
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setInt
argument_list|(
name|TeraSortConfigKeys
operator|.
name|OUTPUT_REPLICATION
operator|.
name|key
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|usage ()
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: terasort [-Dproperty=value]<in><out>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"TeraSort configurations are:"
argument_list|)
expr_stmt|;
for|for
control|(
name|TeraSortConfigKeys
name|teraSortConfigKeys
range|:
name|TeraSortConfigKeys
operator|.
name|values
argument_list|()
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|teraSortConfigKeys
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"If you want to store the output data as "
operator|+
literal|"erasure code striping file, just make sure that the parent dir "
operator|+
literal|"of<out> has erasure code policy set"
argument_list|)
expr_stmt|;
block|}
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
return|return
literal|2
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"starting"
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|inputDir
init|=
operator|new
name|Path
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Path
name|outputDir
init|=
operator|new
name|Path
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|boolean
name|useSimplePartitioner
init|=
name|getUseSimplePartitioner
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|TeraInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|inputDir
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outputDir
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"TeraSort"
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|TeraSort
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|TeraInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|TeraOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|useSimplePartitioner
condition|)
block|{
name|job
operator|.
name|setPartitionerClass
argument_list|(
name|SimplePartitioner
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Path
name|partitionFile
init|=
operator|new
name|Path
argument_list|(
name|outputDir
argument_list|,
name|TeraInputFormat
operator|.
name|PARTITION_FILENAME
argument_list|)
decl_stmt|;
name|URI
name|partitionUri
init|=
operator|new
name|URI
argument_list|(
name|partitionFile
operator|.
name|toString
argument_list|()
operator|+
literal|"#"
operator|+
name|TeraInputFormat
operator|.
name|PARTITION_FILENAME
argument_list|)
decl_stmt|;
try|try
block|{
name|TeraInputFormat
operator|.
name|writePartitionFile
argument_list|(
name|job
argument_list|,
name|partitionFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|job
operator|.
name|addCacheFile
argument_list|(
name|partitionUri
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Spent "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|"ms computing partitions."
argument_list|)
expr_stmt|;
name|job
operator|.
name|setPartitionerClass
argument_list|(
name|TotalOrderPartitioner
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setInt
argument_list|(
literal|"dfs.replication"
argument_list|,
name|getOutputReplication
argument_list|(
name|job
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**    * @param args    */
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
name|TeraSort
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

