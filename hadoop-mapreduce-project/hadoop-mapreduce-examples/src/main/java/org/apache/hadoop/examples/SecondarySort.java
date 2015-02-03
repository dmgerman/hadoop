begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
package|;
end_package

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
name|StringTokenizer
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
name|lib
operator|.
name|input
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
name|util
operator|.
name|GenericOptionsParser
import|;
end_import

begin_comment
comment|/**  * This is an example Hadoop Map/Reduce application.  * It reads the text input files that must contain two integers per a line.  * The output is sorted by the first and second number and grouped on the   * first number.  *  * To run: bin/hadoop jar build/hadoop-examples.jar secondarysort  *<i>in-dir</i><i>out-dir</i>   */
end_comment

begin_class
DECL|class|SecondarySort
specifier|public
class|class
name|SecondarySort
block|{
comment|/**    * Define a pair of integers that are writable.    * They are serialized in a byte comparable format.    */
DECL|class|IntPair
specifier|public
specifier|static
class|class
name|IntPair
implements|implements
name|WritableComparable
argument_list|<
name|IntPair
argument_list|>
block|{
DECL|field|first
specifier|private
name|int
name|first
init|=
literal|0
decl_stmt|;
DECL|field|second
specifier|private
name|int
name|second
init|=
literal|0
decl_stmt|;
comment|/**      * Set the left and right values.      */
DECL|method|set (int left, int right)
specifier|public
name|void
name|set
parameter_list|(
name|int
name|left
parameter_list|,
name|int
name|right
parameter_list|)
block|{
name|first
operator|=
name|left
expr_stmt|;
name|second
operator|=
name|right
expr_stmt|;
block|}
DECL|method|getFirst ()
specifier|public
name|int
name|getFirst
parameter_list|()
block|{
return|return
name|first
return|;
block|}
DECL|method|getSecond ()
specifier|public
name|int
name|getSecond
parameter_list|()
block|{
return|return
name|second
return|;
block|}
comment|/**      * Read the two integers.       * Encoded as: MIN_VALUE -> 0, 0 -> -MIN_VALUE, MAX_VALUE-> -1      */
annotation|@
name|Override
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
block|{
name|first
operator|=
name|in
operator|.
name|readInt
argument_list|()
operator|+
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
name|second
operator|=
name|in
operator|.
name|readInt
argument_list|()
operator|+
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|first
operator|-
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|second
operator|-
name|Integer
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|first
operator|*
literal|157
operator|+
name|second
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object right)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|right
parameter_list|)
block|{
if|if
condition|(
name|right
operator|instanceof
name|IntPair
condition|)
block|{
name|IntPair
name|r
init|=
operator|(
name|IntPair
operator|)
name|right
decl_stmt|;
return|return
name|r
operator|.
name|first
operator|==
name|first
operator|&&
name|r
operator|.
name|second
operator|==
name|second
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** A Comparator that compares serialized IntPair. */
DECL|class|Comparator
specifier|public
specifier|static
class|class
name|Comparator
extends|extends
name|WritableComparator
block|{
DECL|method|Comparator ()
specifier|public
name|Comparator
parameter_list|()
block|{
name|super
argument_list|(
name|IntPair
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|compare (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|int
name|s1
parameter_list|,
name|int
name|l1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|,
name|int
name|s2
parameter_list|,
name|int
name|l2
parameter_list|)
block|{
return|return
name|compareBytes
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|,
name|l1
argument_list|,
name|b2
argument_list|,
name|s2
argument_list|,
name|l2
argument_list|)
return|;
block|}
block|}
static|static
block|{
comment|// register this comparator
name|WritableComparator
operator|.
name|define
argument_list|(
name|IntPair
operator|.
name|class
argument_list|,
operator|new
name|Comparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo (IntPair o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|IntPair
name|o
parameter_list|)
block|{
if|if
condition|(
name|first
operator|!=
name|o
operator|.
name|first
condition|)
block|{
return|return
name|first
operator|<
name|o
operator|.
name|first
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|second
operator|!=
name|o
operator|.
name|second
condition|)
block|{
return|return
name|second
operator|<
name|o
operator|.
name|second
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
comment|/**    * Partition based on the first part of the pair.    */
DECL|class|FirstPartitioner
specifier|public
specifier|static
class|class
name|FirstPartitioner
extends|extends
name|Partitioner
argument_list|<
name|IntPair
argument_list|,
name|IntWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getPartition (IntPair key, IntWritable value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|IntPair
name|key
parameter_list|,
name|IntWritable
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
return|return
name|Math
operator|.
name|abs
argument_list|(
name|key
operator|.
name|getFirst
argument_list|()
operator|*
literal|127
argument_list|)
operator|%
name|numPartitions
return|;
block|}
block|}
comment|/**    * Compare only the first part of the pair, so that reduce is called once    * for each value of the first part.    */
DECL|class|FirstGroupingComparator
specifier|public
specifier|static
class|class
name|FirstGroupingComparator
implements|implements
name|RawComparator
argument_list|<
name|IntPair
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|int
name|s1
parameter_list|,
name|int
name|l1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|,
name|int
name|s2
parameter_list|,
name|int
name|l2
parameter_list|)
block|{
return|return
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|,
name|Integer
operator|.
name|SIZE
operator|/
literal|8
argument_list|,
name|b2
argument_list|,
name|s2
argument_list|,
name|Integer
operator|.
name|SIZE
operator|/
literal|8
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compare (IntPair o1, IntPair o2)
specifier|public
name|int
name|compare
parameter_list|(
name|IntPair
name|o1
parameter_list|,
name|IntPair
name|o2
parameter_list|)
block|{
name|int
name|l
init|=
name|o1
operator|.
name|getFirst
argument_list|()
decl_stmt|;
name|int
name|r
init|=
name|o2
operator|.
name|getFirst
argument_list|()
decl_stmt|;
return|return
name|l
operator|==
name|r
condition|?
literal|0
else|:
operator|(
name|l
operator|<
name|r
condition|?
operator|-
literal|1
else|:
literal|1
operator|)
return|;
block|}
block|}
comment|/**    * Read two integers from each line and generate a key, value pair    * as ((left, right), right).    */
DECL|class|MapClass
specifier|public
specifier|static
class|class
name|MapClass
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|IntPair
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|field|key
specifier|private
specifier|final
name|IntPair
name|key
init|=
operator|new
name|IntPair
argument_list|()
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|IntWritable
name|value
init|=
operator|new
name|IntWritable
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|map (LongWritable inKey, Text inValue, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|LongWritable
name|inKey
parameter_list|,
name|Text
name|inValue
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|StringTokenizer
name|itr
init|=
operator|new
name|StringTokenizer
argument_list|(
name|inValue
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|left
init|=
literal|0
decl_stmt|;
name|int
name|right
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|itr
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|left
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|itr
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|itr
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|right
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|itr
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|key
operator|.
name|set
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
expr_stmt|;
name|value
operator|.
name|set
argument_list|(
name|right
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * A reducer class that just emits the sum of the input values.    */
DECL|class|Reduce
specifier|public
specifier|static
class|class
name|Reduce
extends|extends
name|Reducer
argument_list|<
name|IntPair
argument_list|,
name|IntWritable
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|field|SEPARATOR
specifier|private
specifier|static
specifier|final
name|Text
name|SEPARATOR
init|=
operator|new
name|Text
argument_list|(
literal|"------------------------------------------------"
argument_list|)
decl_stmt|;
DECL|field|first
specifier|private
specifier|final
name|Text
name|first
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|reduce (IntPair key, Iterable<IntWritable> values, Context context )
specifier|public
name|void
name|reduce
parameter_list|(
name|IntPair
name|key
parameter_list|,
name|Iterable
argument_list|<
name|IntWritable
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
name|context
operator|.
name|write
argument_list|(
name|SEPARATOR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|first
operator|.
name|set
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|key
operator|.
name|getFirst
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|IntWritable
name|value
range|:
name|values
control|)
block|{
name|context
operator|.
name|write
argument_list|(
name|first
argument_list|,
name|value
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
index|[]
name|otherArgs
init|=
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
operator|.
name|getRemainingArgs
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherArgs
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: secondarysort<in><out>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|,
literal|"secondary sort"
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|SecondarySort
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|MapClass
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
comment|// group and partition by the first int in the pair
name|job
operator|.
name|setPartitionerClass
argument_list|(
name|FirstPartitioner
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setGroupingComparatorClass
argument_list|(
name|FirstGroupingComparator
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// the map output is IntPair, IntWritable
name|job
operator|.
name|setMapOutputKeyClass
argument_list|(
name|IntPair
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// the reduce output is Text, IntWritable
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
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|otherArgs
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|otherArgs
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

