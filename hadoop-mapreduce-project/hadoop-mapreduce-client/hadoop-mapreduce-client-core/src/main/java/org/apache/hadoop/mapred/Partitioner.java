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

begin_comment
comment|/**   * Partitions the key space.  *   *<p><code>Partitioner</code> controls the partitioning of the keys of the   * intermediate map-outputs. The key (or a subset of the key) is used to derive  * the partition, typically by a hash function. The total number of partitions  * is the same as the number of reduce tasks for the job. Hence this controls  * which of the<code>m</code> reduce tasks the intermediate key (and hence the   * record) is sent for reduction.</p>  *  *<p>Note: A<code>Partitioner</code> is created only when there are multiple  * reducers.</p>  *   * @see Reducer  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|Partitioner
specifier|public
interface|interface
name|Partitioner
parameter_list|<
name|K2
parameter_list|,
name|V2
parameter_list|>
extends|extends
name|JobConfigurable
block|{
comment|/**     * Get the paritition number for a given key (hence record) given the total     * number of partitions i.e. number of reduce-tasks for the job.    *       *<p>Typically a hash function on a all or a subset of the key.</p>    *    * @param key the key to be paritioned.    * @param value the entry value.    * @param numPartitions the total number of partitions.    * @return the partition number for the<code>key</code>.    */
DECL|method|getPartition (K2 key, V2 value, int numPartitions)
name|int
name|getPartition
parameter_list|(
name|K2
name|key
parameter_list|,
name|V2
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

