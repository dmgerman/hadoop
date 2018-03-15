begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container.placement.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * Interface that defines Node Stats.  */
end_comment

begin_interface
DECL|interface|NodeStat
interface|interface
name|NodeStat
block|{
comment|/**    * Get capacity of the node.    * @return capacity of the node.    */
DECL|method|getCapacity ()
name|LongMetric
name|getCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the used space of the node.    * @return the used space of the node.    */
DECL|method|getScmUsed ()
name|LongMetric
name|getScmUsed
parameter_list|()
function_decl|;
comment|/**    * Get the remaining space of the node.    * @return the remaining space of the node.    */
DECL|method|getRemaining ()
name|LongMetric
name|getRemaining
parameter_list|()
function_decl|;
comment|/**    * Set the total/used/remaining space.    * @param capacity - total space.    * @param used - used space.    * @param remain - remaining space.    */
annotation|@
name|VisibleForTesting
DECL|method|set (long capacity, long used, long remain)
name|void
name|set
parameter_list|(
name|long
name|capacity
parameter_list|,
name|long
name|used
parameter_list|,
name|long
name|remain
parameter_list|)
function_decl|;
comment|/**    * Adding of the stat.    * @param stat - stat to be added.    * @return updated node stat.    */
DECL|method|add (NodeStat stat)
name|NodeStat
name|add
parameter_list|(
name|NodeStat
name|stat
parameter_list|)
function_decl|;
comment|/**    * Subtract of the stat.    * @param stat - stat to be subtracted.    * @return updated nodestat.    */
DECL|method|subtract (NodeStat stat)
name|NodeStat
name|subtract
parameter_list|(
name|NodeStat
name|stat
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

