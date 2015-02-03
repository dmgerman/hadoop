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
name|ArrayList
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|mapred
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
name|mapreduce
operator|.
name|Job
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|InputSampler
specifier|public
class|class
name|InputSampler
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
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
name|partition
operator|.
name|InputSampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
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
name|InputSampler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|InputSampler (JobConf conf)
specifier|public
name|InputSampler
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|writePartitionFile (JobConf job, Sampler<K,V> sampler)
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|void
name|writePartitionFile
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|Sampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|sampler
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
throws|,
name|InterruptedException
block|{
name|writePartitionFile
argument_list|(
name|Job
operator|.
name|getInstance
argument_list|(
name|job
argument_list|)
argument_list|,
name|sampler
argument_list|)
expr_stmt|;
block|}
comment|/**    * Interface to sample using an {@link org.apache.hadoop.mapred.InputFormat}.    */
DECL|interface|Sampler
specifier|public
interface|interface
name|Sampler
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
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
name|partition
operator|.
name|InputSampler
operator|.
name|Sampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|/**      * For a given job, collect and return a subset of the keys from the      * input data.      */
DECL|method|getSample (InputFormat<K,V> inf, JobConf job)
name|K
index|[]
name|getSample
parameter_list|(
name|InputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|inf
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Samples the first n records from s splits.    * Inexpensive way to sample random data.    */
DECL|class|SplitSampler
specifier|public
specifier|static
class|class
name|SplitSampler
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
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
name|partition
operator|.
name|InputSampler
operator|.
name|SplitSampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|implements
name|Sampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|/**      * Create a SplitSampler sampling<em>all</em> splits.      * Takes the first numSamples / numSplits records from each split.      * @param numSamples Total number of samples to obtain from all selected      *                   splits.      */
DECL|method|SplitSampler (int numSamples)
specifier|public
name|SplitSampler
parameter_list|(
name|int
name|numSamples
parameter_list|)
block|{
name|this
argument_list|(
name|numSamples
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new SplitSampler.      * @param numSamples Total number of samples to obtain from all selected      *                   splits.      * @param maxSplitsSampled The maximum number of splits to examine.      */
DECL|method|SplitSampler (int numSamples, int maxSplitsSampled)
specifier|public
name|SplitSampler
parameter_list|(
name|int
name|numSamples
parameter_list|,
name|int
name|maxSplitsSampled
parameter_list|)
block|{
name|super
argument_list|(
name|numSamples
argument_list|,
name|maxSplitsSampled
argument_list|)
expr_stmt|;
block|}
comment|/**      * From each split sampled, take the first numSamples / numSplits records.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// ArrayList::toArray doesn't preserve type
DECL|method|getSample (InputFormat<K,V> inf, JobConf job)
specifier|public
name|K
index|[]
name|getSample
parameter_list|(
name|InputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|inf
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|InputSplit
index|[]
name|splits
init|=
name|inf
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
name|job
operator|.
name|getNumMapTasks
argument_list|()
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|K
argument_list|>
name|samples
init|=
operator|new
name|ArrayList
argument_list|<
name|K
argument_list|>
argument_list|(
name|numSamples
argument_list|)
decl_stmt|;
name|int
name|splitsToSample
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxSplitsSampled
argument_list|,
name|splits
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|splitStep
init|=
name|splits
operator|.
name|length
operator|/
name|splitsToSample
decl_stmt|;
name|int
name|samplesPerSplit
init|=
name|numSamples
operator|/
name|splitsToSample
decl_stmt|;
name|long
name|records
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
name|splitsToSample
condition|;
operator|++
name|i
control|)
block|{
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|reader
init|=
name|inf
operator|.
name|getRecordReader
argument_list|(
name|splits
index|[
name|i
operator|*
name|splitStep
index|]
argument_list|,
name|job
argument_list|,
name|Reporter
operator|.
name|NULL
argument_list|)
decl_stmt|;
name|K
name|key
init|=
name|reader
operator|.
name|createKey
argument_list|()
decl_stmt|;
name|V
name|value
init|=
name|reader
operator|.
name|createValue
argument_list|()
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
name|samples
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|key
operator|=
name|reader
operator|.
name|createKey
argument_list|()
expr_stmt|;
operator|++
name|records
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|*
name|samplesPerSplit
operator|<=
name|records
condition|)
block|{
break|break;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|K
index|[]
operator|)
name|samples
operator|.
name|toArray
argument_list|()
return|;
block|}
block|}
comment|/**    * Sample from random points in the input.    * General-purpose sampler. Takes numSamples / maxSplitsSampled inputs from    * each split.    */
DECL|class|RandomSampler
specifier|public
specifier|static
class|class
name|RandomSampler
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
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
name|partition
operator|.
name|InputSampler
operator|.
name|RandomSampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|implements
name|Sampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|/**      * Create a new RandomSampler sampling<em>all</em> splits.      * This will read every split at the client, which is very expensive.      * @param freq Probability with which a key will be chosen.      * @param numSamples Total number of samples to obtain from all selected      *                   splits.      */
DECL|method|RandomSampler (double freq, int numSamples)
specifier|public
name|RandomSampler
parameter_list|(
name|double
name|freq
parameter_list|,
name|int
name|numSamples
parameter_list|)
block|{
name|this
argument_list|(
name|freq
argument_list|,
name|numSamples
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new RandomSampler.      * @param freq Probability with which a key will be chosen.      * @param numSamples Total number of samples to obtain from all selected      *                   splits.      * @param maxSplitsSampled The maximum number of splits to examine.      */
DECL|method|RandomSampler (double freq, int numSamples, int maxSplitsSampled)
specifier|public
name|RandomSampler
parameter_list|(
name|double
name|freq
parameter_list|,
name|int
name|numSamples
parameter_list|,
name|int
name|maxSplitsSampled
parameter_list|)
block|{
name|super
argument_list|(
name|freq
argument_list|,
name|numSamples
argument_list|,
name|maxSplitsSampled
argument_list|)
expr_stmt|;
block|}
comment|/**      * Randomize the split order, then take the specified number of keys from      * each split sampled, where each key is selected with the specified      * probability and possibly replaced by a subsequently selected key when      * the quota of keys from that split is satisfied.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// ArrayList::toArray doesn't preserve type
DECL|method|getSample (InputFormat<K,V> inf, JobConf job)
specifier|public
name|K
index|[]
name|getSample
parameter_list|(
name|InputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|inf
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|InputSplit
index|[]
name|splits
init|=
name|inf
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
name|job
operator|.
name|getNumMapTasks
argument_list|()
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|K
argument_list|>
name|samples
init|=
operator|new
name|ArrayList
argument_list|<
name|K
argument_list|>
argument_list|(
name|numSamples
argument_list|)
decl_stmt|;
name|int
name|splitsToSample
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxSplitsSampled
argument_list|,
name|splits
operator|.
name|length
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
comment|// shuffle splits
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
name|InputSplit
name|tmp
init|=
name|splits
index|[
name|i
index|]
decl_stmt|;
name|int
name|j
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|splits
operator|.
name|length
argument_list|)
decl_stmt|;
name|splits
index|[
name|i
index|]
operator|=
name|splits
index|[
name|j
index|]
expr_stmt|;
name|splits
index|[
name|j
index|]
operator|=
name|tmp
expr_stmt|;
block|}
comment|// our target rate is in terms of the maximum number of sample splits,
comment|// but we accept the possibility of sampling additional splits to hit
comment|// the target sample keyset
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|splitsToSample
operator|||
operator|(
name|i
operator|<
name|splits
operator|.
name|length
operator|&&
name|samples
operator|.
name|size
argument_list|()
operator|<
name|numSamples
operator|)
condition|;
operator|++
name|i
control|)
block|{
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|reader
init|=
name|inf
operator|.
name|getRecordReader
argument_list|(
name|splits
index|[
name|i
index|]
argument_list|,
name|job
argument_list|,
name|Reporter
operator|.
name|NULL
argument_list|)
decl_stmt|;
name|K
name|key
init|=
name|reader
operator|.
name|createKey
argument_list|()
decl_stmt|;
name|V
name|value
init|=
name|reader
operator|.
name|createValue
argument_list|()
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
if|if
condition|(
name|r
operator|.
name|nextDouble
argument_list|()
operator|<=
name|freq
condition|)
block|{
if|if
condition|(
name|samples
operator|.
name|size
argument_list|()
operator|<
name|numSamples
condition|)
block|{
name|samples
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// When exceeding the maximum number of samples, replace a
comment|// random element with this one, then adjust the frequency
comment|// to reflect the possibility of existing elements being
comment|// pushed out
name|int
name|ind
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|numSamples
argument_list|)
decl_stmt|;
if|if
condition|(
name|ind
operator|!=
name|numSamples
condition|)
block|{
name|samples
operator|.
name|set
argument_list|(
name|ind
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
name|freq
operator|*=
operator|(
name|numSamples
operator|-
literal|1
operator|)
operator|/
operator|(
name|double
operator|)
name|numSamples
expr_stmt|;
block|}
name|key
operator|=
name|reader
operator|.
name|createKey
argument_list|()
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|K
index|[]
operator|)
name|samples
operator|.
name|toArray
argument_list|()
return|;
block|}
block|}
comment|/**    * Sample from s splits at regular intervals.    * Useful for sorted data.    */
DECL|class|IntervalSampler
specifier|public
specifier|static
class|class
name|IntervalSampler
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
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
name|partition
operator|.
name|InputSampler
operator|.
name|IntervalSampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|implements
name|Sampler
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|/**      * Create a new IntervalSampler sampling<em>all</em> splits.      * @param freq The frequency with which records will be emitted.      */
DECL|method|IntervalSampler (double freq)
specifier|public
name|IntervalSampler
parameter_list|(
name|double
name|freq
parameter_list|)
block|{
name|this
argument_list|(
name|freq
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new IntervalSampler.      * @param freq The frequency with which records will be emitted.      * @param maxSplitsSampled The maximum number of splits to examine.      * @see #getSample      */
DECL|method|IntervalSampler (double freq, int maxSplitsSampled)
specifier|public
name|IntervalSampler
parameter_list|(
name|double
name|freq
parameter_list|,
name|int
name|maxSplitsSampled
parameter_list|)
block|{
name|super
argument_list|(
name|freq
argument_list|,
name|maxSplitsSampled
argument_list|)
expr_stmt|;
block|}
comment|/**      * For each split sampled, emit when the ratio of the number of records      * retained to the total record count is less than the specified      * frequency.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// ArrayList::toArray doesn't preserve type
DECL|method|getSample (InputFormat<K,V> inf, JobConf job)
specifier|public
name|K
index|[]
name|getSample
parameter_list|(
name|InputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|inf
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|InputSplit
index|[]
name|splits
init|=
name|inf
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
name|job
operator|.
name|getNumMapTasks
argument_list|()
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|K
argument_list|>
name|samples
init|=
operator|new
name|ArrayList
argument_list|<
name|K
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|splitsToSample
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxSplitsSampled
argument_list|,
name|splits
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|splitStep
init|=
name|splits
operator|.
name|length
operator|/
name|splitsToSample
decl_stmt|;
name|long
name|records
init|=
literal|0
decl_stmt|;
name|long
name|kept
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
name|splitsToSample
condition|;
operator|++
name|i
control|)
block|{
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|reader
init|=
name|inf
operator|.
name|getRecordReader
argument_list|(
name|splits
index|[
name|i
operator|*
name|splitStep
index|]
argument_list|,
name|job
argument_list|,
name|Reporter
operator|.
name|NULL
argument_list|)
decl_stmt|;
name|K
name|key
init|=
name|reader
operator|.
name|createKey
argument_list|()
decl_stmt|;
name|V
name|value
init|=
name|reader
operator|.
name|createValue
argument_list|()
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
operator|++
name|records
expr_stmt|;
if|if
condition|(
operator|(
name|double
operator|)
name|kept
operator|/
name|records
operator|<
name|freq
condition|)
block|{
operator|++
name|kept
expr_stmt|;
name|samples
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|key
operator|=
name|reader
operator|.
name|createKey
argument_list|()
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|K
index|[]
operator|)
name|samples
operator|.
name|toArray
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

