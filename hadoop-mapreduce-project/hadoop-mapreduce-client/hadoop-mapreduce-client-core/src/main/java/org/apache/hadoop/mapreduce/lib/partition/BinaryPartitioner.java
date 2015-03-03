begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.partition
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
name|partition
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
name|io
operator|.
name|BinaryComparable
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
name|Partitioner
import|;
end_import

begin_comment
comment|/**  *<p>Partition {@link BinaryComparable} keys using a configurable part of   * the bytes array returned by {@link BinaryComparable#getBytes()}.</p>  *   *<p>The subarray to be used for the partitioning can be defined by means  * of the following properties:  *<ul>  *<li>  *<i>mapreduce.partition.binarypartitioner.left.offset</i>:  *     left offset in array (0 by default)  *</li>  *<li>  *<i>mapreduce.partition.binarypartitioner.right.offset</i>:   *     right offset in array (-1 by default)  *</li>  *</ul>  * Like in Python, both negative and positive offsets are allowed, but  * the meaning is slightly different. In case of an array of length 5,  * for instance, the possible offsets are:  *<pre><code>  *  +---+---+---+---+---+  *  | B | B | B | B | B |  *  +---+---+---+---+---+  *    0   1   2   3   4  *   -5  -4  -3  -2  -1  *</code></pre>  * The first row of numbers gives the position of the offsets 0...5 in   * the array; the second row gives the corresponding negative offsets.   * Contrary to Python, the specified subarray has byte<code>i</code>   * and<code>j</code> as first and last element, repectively, when   *<code>i</code> and<code>j</code> are the left and right offset.  *   *<p>For Hadoop programs written in Java, it is advisable to use one of   * the following static convenience methods for setting the offsets:  *<ul>  *<li>{@link #setOffsets}</li>  *<li>{@link #setLeftOffset}</li>  *<li>{@link #setRightOffset}</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BinaryPartitioner
specifier|public
class|class
name|BinaryPartitioner
parameter_list|<
name|V
parameter_list|>
extends|extends
name|Partitioner
argument_list|<
name|BinaryComparable
argument_list|,
name|V
argument_list|>
implements|implements
name|Configurable
block|{
DECL|field|LEFT_OFFSET_PROPERTY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|LEFT_OFFSET_PROPERTY_NAME
init|=
literal|"mapreduce.partition.binarypartitioner.left.offset"
decl_stmt|;
DECL|field|RIGHT_OFFSET_PROPERTY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|RIGHT_OFFSET_PROPERTY_NAME
init|=
literal|"mapreduce.partition.binarypartitioner.right.offset"
decl_stmt|;
comment|/**    * Set the subarray to be used for partitioning to     *<code>bytes[left:(right+1)]</code> in Python syntax.    *     * @param conf configuration object    * @param left left Python-style offset    * @param right right Python-style offset    */
DECL|method|setOffsets (Configuration conf, int left, int right)
specifier|public
specifier|static
name|void
name|setOffsets
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|left
parameter_list|,
name|int
name|right
parameter_list|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|LEFT_OFFSET_PROPERTY_NAME
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|RIGHT_OFFSET_PROPERTY_NAME
argument_list|,
name|right
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the subarray to be used for partitioning to     *<code>bytes[offset:]</code> in Python syntax.    *     * @param conf configuration object    * @param offset left Python-style offset    */
DECL|method|setLeftOffset (Configuration conf, int offset)
specifier|public
specifier|static
name|void
name|setLeftOffset
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|LEFT_OFFSET_PROPERTY_NAME
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the subarray to be used for partitioning to     *<code>bytes[:(offset+1)]</code> in Python syntax.    *     * @param conf configuration object    * @param offset right Python-style offset    */
DECL|method|setRightOffset (Configuration conf, int offset)
specifier|public
specifier|static
name|void
name|setRightOffset
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|RIGHT_OFFSET_PROPERTY_NAME
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|leftOffset
DECL|field|rightOffset
specifier|private
name|int
name|leftOffset
decl_stmt|,
name|rightOffset
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
name|leftOffset
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|LEFT_OFFSET_PROPERTY_NAME
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|rightOffset
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|RIGHT_OFFSET_PROPERTY_NAME
argument_list|,
operator|-
literal|1
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
comment|/**     * Use (the specified slice of the array returned by)     * {@link BinaryComparable#getBytes()} to partition.     */
annotation|@
name|Override
DECL|method|getPartition (BinaryComparable key, V value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|BinaryComparable
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
name|int
name|length
init|=
name|key
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|int
name|leftIndex
init|=
operator|(
name|leftOffset
operator|+
name|length
operator|)
operator|%
name|length
decl_stmt|;
name|int
name|rightIndex
init|=
operator|(
name|rightOffset
operator|+
name|length
operator|)
operator|%
name|length
decl_stmt|;
name|int
name|hash
init|=
name|WritableComparator
operator|.
name|hashBytes
argument_list|(
name|key
operator|.
name|getBytes
argument_list|()
argument_list|,
name|leftIndex
argument_list|,
name|rightIndex
operator|-
name|leftIndex
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
operator|(
name|hash
operator|&
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|%
name|numPartitions
return|;
block|}
block|}
end_class

end_unit

