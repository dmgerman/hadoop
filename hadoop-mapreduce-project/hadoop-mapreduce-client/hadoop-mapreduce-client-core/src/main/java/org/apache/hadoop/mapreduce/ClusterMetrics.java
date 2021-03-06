begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/**  * Status information on the current state of the Map-Reduce cluster.  *   *<p><code>ClusterMetrics</code> provides clients with information such as:  *<ol>  *<li>  *   Size of the cluster.    *</li>  *<li>  *   Number of blacklisted and decommissioned trackers.    *</li>  *<li>  *   Slot capacity of the cluster.   *</li>  *<li>  *   The number of currently occupied/reserved map and reduce slots.  *</li>  *<li>  *   The number of currently running map and reduce tasks.  *</li>  *<li>  *   The number of job submissions.  *</li>  *</ol>  *   *<p>Clients can query for the latest<code>ClusterMetrics</code>, via   * {@link Cluster#getClusterStatus()}.</p>  *   * @see Cluster  */
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
DECL|class|ClusterMetrics
specifier|public
class|class
name|ClusterMetrics
implements|implements
name|Writable
block|{
DECL|field|runningMaps
specifier|private
name|int
name|runningMaps
decl_stmt|;
DECL|field|runningReduces
specifier|private
name|int
name|runningReduces
decl_stmt|;
DECL|field|occupiedMapSlots
specifier|private
name|int
name|occupiedMapSlots
decl_stmt|;
DECL|field|occupiedReduceSlots
specifier|private
name|int
name|occupiedReduceSlots
decl_stmt|;
DECL|field|reservedMapSlots
specifier|private
name|int
name|reservedMapSlots
decl_stmt|;
DECL|field|reservedReduceSlots
specifier|private
name|int
name|reservedReduceSlots
decl_stmt|;
DECL|field|totalMapSlots
specifier|private
name|int
name|totalMapSlots
decl_stmt|;
DECL|field|totalReduceSlots
specifier|private
name|int
name|totalReduceSlots
decl_stmt|;
DECL|field|totalJobSubmissions
specifier|private
name|int
name|totalJobSubmissions
decl_stmt|;
DECL|field|numTrackers
specifier|private
name|int
name|numTrackers
decl_stmt|;
DECL|field|numBlacklistedTrackers
specifier|private
name|int
name|numBlacklistedTrackers
decl_stmt|;
DECL|field|numGraylistedTrackers
specifier|private
name|int
name|numGraylistedTrackers
decl_stmt|;
DECL|field|numDecommissionedTrackers
specifier|private
name|int
name|numDecommissionedTrackers
decl_stmt|;
DECL|method|ClusterMetrics ()
specifier|public
name|ClusterMetrics
parameter_list|()
block|{   }
DECL|method|ClusterMetrics (int runningMaps, int runningReduces, int occupiedMapSlots, int occupiedReduceSlots, int reservedMapSlots, int reservedReduceSlots, int mapSlots, int reduceSlots, int totalJobSubmissions, int numTrackers, int numBlacklistedTrackers, int numDecommissionedNodes)
specifier|public
name|ClusterMetrics
parameter_list|(
name|int
name|runningMaps
parameter_list|,
name|int
name|runningReduces
parameter_list|,
name|int
name|occupiedMapSlots
parameter_list|,
name|int
name|occupiedReduceSlots
parameter_list|,
name|int
name|reservedMapSlots
parameter_list|,
name|int
name|reservedReduceSlots
parameter_list|,
name|int
name|mapSlots
parameter_list|,
name|int
name|reduceSlots
parameter_list|,
name|int
name|totalJobSubmissions
parameter_list|,
name|int
name|numTrackers
parameter_list|,
name|int
name|numBlacklistedTrackers
parameter_list|,
name|int
name|numDecommissionedNodes
parameter_list|)
block|{
name|this
argument_list|(
name|runningMaps
argument_list|,
name|runningReduces
argument_list|,
name|occupiedMapSlots
argument_list|,
name|occupiedReduceSlots
argument_list|,
name|reservedMapSlots
argument_list|,
name|reservedReduceSlots
argument_list|,
name|mapSlots
argument_list|,
name|reduceSlots
argument_list|,
name|totalJobSubmissions
argument_list|,
name|numTrackers
argument_list|,
name|numBlacklistedTrackers
argument_list|,
literal|0
argument_list|,
name|numDecommissionedNodes
argument_list|)
expr_stmt|;
block|}
DECL|method|ClusterMetrics (int runningMaps, int runningReduces, int occupiedMapSlots, int occupiedReduceSlots, int reservedMapSlots, int reservedReduceSlots, int mapSlots, int reduceSlots, int totalJobSubmissions, int numTrackers, int numBlacklistedTrackers, int numGraylistedTrackers, int numDecommissionedNodes)
specifier|public
name|ClusterMetrics
parameter_list|(
name|int
name|runningMaps
parameter_list|,
name|int
name|runningReduces
parameter_list|,
name|int
name|occupiedMapSlots
parameter_list|,
name|int
name|occupiedReduceSlots
parameter_list|,
name|int
name|reservedMapSlots
parameter_list|,
name|int
name|reservedReduceSlots
parameter_list|,
name|int
name|mapSlots
parameter_list|,
name|int
name|reduceSlots
parameter_list|,
name|int
name|totalJobSubmissions
parameter_list|,
name|int
name|numTrackers
parameter_list|,
name|int
name|numBlacklistedTrackers
parameter_list|,
name|int
name|numGraylistedTrackers
parameter_list|,
name|int
name|numDecommissionedNodes
parameter_list|)
block|{
name|this
operator|.
name|runningMaps
operator|=
name|runningMaps
expr_stmt|;
name|this
operator|.
name|runningReduces
operator|=
name|runningReduces
expr_stmt|;
name|this
operator|.
name|occupiedMapSlots
operator|=
name|occupiedMapSlots
expr_stmt|;
name|this
operator|.
name|occupiedReduceSlots
operator|=
name|occupiedReduceSlots
expr_stmt|;
name|this
operator|.
name|reservedMapSlots
operator|=
name|reservedMapSlots
expr_stmt|;
name|this
operator|.
name|reservedReduceSlots
operator|=
name|reservedReduceSlots
expr_stmt|;
name|this
operator|.
name|totalMapSlots
operator|=
name|mapSlots
expr_stmt|;
name|this
operator|.
name|totalReduceSlots
operator|=
name|reduceSlots
expr_stmt|;
name|this
operator|.
name|totalJobSubmissions
operator|=
name|totalJobSubmissions
expr_stmt|;
name|this
operator|.
name|numTrackers
operator|=
name|numTrackers
expr_stmt|;
name|this
operator|.
name|numBlacklistedTrackers
operator|=
name|numBlacklistedTrackers
expr_stmt|;
name|this
operator|.
name|numGraylistedTrackers
operator|=
name|numGraylistedTrackers
expr_stmt|;
name|this
operator|.
name|numDecommissionedTrackers
operator|=
name|numDecommissionedNodes
expr_stmt|;
block|}
comment|/**    * Get the number of running map tasks in the cluster.    *     * @return running maps    */
DECL|method|getRunningMaps ()
specifier|public
name|int
name|getRunningMaps
parameter_list|()
block|{
return|return
name|runningMaps
return|;
block|}
comment|/**    * Get the number of running reduce tasks in the cluster.    *     * @return running reduces    */
DECL|method|getRunningReduces ()
specifier|public
name|int
name|getRunningReduces
parameter_list|()
block|{
return|return
name|runningReduces
return|;
block|}
comment|/**    * Get number of occupied map slots in the cluster.    *     * @return occupied map slot count    */
DECL|method|getOccupiedMapSlots ()
specifier|public
name|int
name|getOccupiedMapSlots
parameter_list|()
block|{
return|return
name|occupiedMapSlots
return|;
block|}
comment|/**    * Get the number of occupied reduce slots in the cluster.    *     * @return occupied reduce slot count    */
DECL|method|getOccupiedReduceSlots ()
specifier|public
name|int
name|getOccupiedReduceSlots
parameter_list|()
block|{
return|return
name|occupiedReduceSlots
return|;
block|}
comment|/**    * Get number of reserved map slots in the cluster.    *     * @return reserved map slot count    */
DECL|method|getReservedMapSlots ()
specifier|public
name|int
name|getReservedMapSlots
parameter_list|()
block|{
return|return
name|reservedMapSlots
return|;
block|}
comment|/**    * Get the number of reserved reduce slots in the cluster.    *     * @return reserved reduce slot count    */
DECL|method|getReservedReduceSlots ()
specifier|public
name|int
name|getReservedReduceSlots
parameter_list|()
block|{
return|return
name|reservedReduceSlots
return|;
block|}
comment|/**    * Get the total number of map slots in the cluster.    *     * @return map slot capacity    */
DECL|method|getMapSlotCapacity ()
specifier|public
name|int
name|getMapSlotCapacity
parameter_list|()
block|{
return|return
name|totalMapSlots
return|;
block|}
comment|/**    * Get the total number of reduce slots in the cluster.    *     * @return reduce slot capacity    */
DECL|method|getReduceSlotCapacity ()
specifier|public
name|int
name|getReduceSlotCapacity
parameter_list|()
block|{
return|return
name|totalReduceSlots
return|;
block|}
comment|/**    * Get the total number of job submissions in the cluster.    *     * @return total number of job submissions    */
DECL|method|getTotalJobSubmissions ()
specifier|public
name|int
name|getTotalJobSubmissions
parameter_list|()
block|{
return|return
name|totalJobSubmissions
return|;
block|}
comment|/**    * Get the number of active trackers in the cluster.    *     * @return active tracker count.    */
DECL|method|getTaskTrackerCount ()
specifier|public
name|int
name|getTaskTrackerCount
parameter_list|()
block|{
return|return
name|numTrackers
return|;
block|}
comment|/**    * Get the number of blacklisted trackers in the cluster.    *     * @return blacklisted tracker count    */
DECL|method|getBlackListedTaskTrackerCount ()
specifier|public
name|int
name|getBlackListedTaskTrackerCount
parameter_list|()
block|{
return|return
name|numBlacklistedTrackers
return|;
block|}
comment|/**    * Get the number of graylisted trackers in the cluster.    *     * @return graylisted tracker count    */
DECL|method|getGrayListedTaskTrackerCount ()
specifier|public
name|int
name|getGrayListedTaskTrackerCount
parameter_list|()
block|{
return|return
name|numGraylistedTrackers
return|;
block|}
comment|/**    * Get the number of decommissioned trackers in the cluster.    *     * @return decommissioned tracker count    */
DECL|method|getDecommissionedTaskTrackerCount ()
specifier|public
name|int
name|getDecommissionedTaskTrackerCount
parameter_list|()
block|{
return|return
name|numDecommissionedTrackers
return|;
block|}
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
name|runningMaps
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|runningReduces
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|occupiedMapSlots
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|occupiedReduceSlots
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|reservedMapSlots
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|reservedReduceSlots
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|totalMapSlots
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|totalReduceSlots
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|totalJobSubmissions
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|numTrackers
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|numBlacklistedTrackers
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|numGraylistedTrackers
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|numDecommissionedTrackers
operator|=
name|in
operator|.
name|readInt
argument_list|()
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
name|runningMaps
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|runningReduces
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|occupiedMapSlots
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|occupiedReduceSlots
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|reservedMapSlots
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|reservedReduceSlots
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|totalMapSlots
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|totalReduceSlots
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|totalJobSubmissions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numTrackers
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numBlacklistedTrackers
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numGraylistedTrackers
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numDecommissionedTrackers
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

