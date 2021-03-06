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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Writable
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
name|WritableUtils
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
name|Cluster
operator|.
name|JobTrackerStatus
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
name|StringInterner
import|;
end_import

begin_comment
comment|/**  * Status information on the current state of the Map-Reduce cluster.  *   *<p><code>ClusterStatus</code> provides clients with information such as:  *<ol>  *<li>  *   Size of the cluster.   *</li>  *<li>  *   Name of the trackers.   *</li>  *<li>  *   Task capacity of the cluster.   *</li>  *<li>  *   The number of currently running map and reduce tasks.  *</li>  *<li>  *   State of the<code>JobTracker</code>.  *</li>  *<li>  *   Details regarding black listed trackers.  *</li>  *</ol>  *   *<p>Clients can query for the latest<code>ClusterStatus</code>, via   * {@link JobClient#getClusterStatus()}.</p>  *   * @see JobClient  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ClusterStatus
specifier|public
class|class
name|ClusterStatus
implements|implements
name|Writable
block|{
comment|/**    * Class which encapsulates information about a blacklisted tasktracker.    *      * The information includes the tasktracker's name and reasons for    * getting blacklisted. The toString method of the class will print    * the information in a whitespace separated fashion to enable parsing.    */
DECL|class|BlackListInfo
specifier|public
specifier|static
class|class
name|BlackListInfo
implements|implements
name|Writable
block|{
DECL|field|trackerName
specifier|private
name|String
name|trackerName
decl_stmt|;
DECL|field|reasonForBlackListing
specifier|private
name|String
name|reasonForBlackListing
decl_stmt|;
DECL|field|blackListReport
specifier|private
name|String
name|blackListReport
decl_stmt|;
DECL|method|BlackListInfo ()
name|BlackListInfo
parameter_list|()
block|{     }
comment|/**      * Gets the blacklisted tasktracker's name.      *       * @return tracker's name.      */
DECL|method|getTrackerName ()
specifier|public
name|String
name|getTrackerName
parameter_list|()
block|{
return|return
name|trackerName
return|;
block|}
comment|/**      * Gets the reason for which the tasktracker was blacklisted.      *       * @return reason which tracker was blacklisted      */
DECL|method|getReasonForBlackListing ()
specifier|public
name|String
name|getReasonForBlackListing
parameter_list|()
block|{
return|return
name|reasonForBlackListing
return|;
block|}
comment|/**      * Sets the blacklisted tasktracker's name.      *       * @param trackerName of the tracker.      */
DECL|method|setTrackerName (String trackerName)
name|void
name|setTrackerName
parameter_list|(
name|String
name|trackerName
parameter_list|)
block|{
name|this
operator|.
name|trackerName
operator|=
name|trackerName
expr_stmt|;
block|}
comment|/**      * Sets the reason for which the tasktracker was blacklisted.      *       * @param reasonForBlackListing      */
DECL|method|setReasonForBlackListing (String reasonForBlackListing)
name|void
name|setReasonForBlackListing
parameter_list|(
name|String
name|reasonForBlackListing
parameter_list|)
block|{
name|this
operator|.
name|reasonForBlackListing
operator|=
name|reasonForBlackListing
expr_stmt|;
block|}
comment|/**      * Gets a descriptive report about why the tasktracker was blacklisted.      *       * @return report describing why the tasktracker was blacklisted.      */
DECL|method|getBlackListReport ()
specifier|public
name|String
name|getBlackListReport
parameter_list|()
block|{
return|return
name|blackListReport
return|;
block|}
comment|/**      * Sets a descriptive report about why the tasktracker was blacklisted.      * @param blackListReport report describing why the tasktracker       *                        was blacklisted.      */
DECL|method|setBlackListReport (String blackListReport)
name|void
name|setBlackListReport
parameter_list|(
name|String
name|blackListReport
parameter_list|)
block|{
name|this
operator|.
name|blackListReport
operator|=
name|blackListReport
expr_stmt|;
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
name|trackerName
operator|=
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|reasonForBlackListing
operator|=
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|blackListReport
operator|=
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
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
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|trackerName
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|reasonForBlackListing
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|blackListReport
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|/**      * Print information related to the blacklisted tasktracker in a      * whitespace separated fashion.      *       * The method changes any newlines in the report describing why      * the tasktracker was blacklisted to a ':' for enabling better      * parsing.      */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|trackerName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|reasonForBlackListing
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|blackListReport
operator|.
name|replace
argument_list|(
literal|"\n"
argument_list|,
literal|":"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|trackerName
operator|!=
literal|null
condition|?
name|trackerName
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|reasonForBlackListing
operator|!=
literal|null
condition|?
name|reasonForBlackListing
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|blackListReport
operator|!=
literal|null
condition|?
name|blackListReport
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|BlackListInfo
name|that
init|=
operator|(
name|BlackListInfo
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|trackerName
operator|==
literal|null
condition|?
name|that
operator|.
name|trackerName
operator|!=
literal|null
else|:
operator|!
name|trackerName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|trackerName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|reasonForBlackListing
operator|==
literal|null
condition|?
name|that
operator|.
name|reasonForBlackListing
operator|!=
literal|null
else|:
operator|!
name|reasonForBlackListing
operator|.
name|equals
argument_list|(
name|that
operator|.
name|reasonForBlackListing
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|blackListReport
operator|==
literal|null
condition|?
name|that
operator|.
name|blackListReport
operator|!=
literal|null
else|:
operator|!
name|blackListReport
operator|.
name|equals
argument_list|(
name|that
operator|.
name|blackListReport
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
DECL|field|UNINITIALIZED_MEMORY_VALUE
specifier|public
specifier|static
specifier|final
name|long
name|UNINITIALIZED_MEMORY_VALUE
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numActiveTrackers
specifier|private
name|int
name|numActiveTrackers
decl_stmt|;
DECL|field|activeTrackers
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|activeTrackers
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|numBlacklistedTrackers
specifier|private
name|int
name|numBlacklistedTrackers
decl_stmt|;
DECL|field|numExcludedNodes
specifier|private
name|int
name|numExcludedNodes
decl_stmt|;
DECL|field|ttExpiryInterval
specifier|private
name|long
name|ttExpiryInterval
decl_stmt|;
DECL|field|map_tasks
specifier|private
name|int
name|map_tasks
decl_stmt|;
DECL|field|reduce_tasks
specifier|private
name|int
name|reduce_tasks
decl_stmt|;
DECL|field|max_map_tasks
specifier|private
name|int
name|max_map_tasks
decl_stmt|;
DECL|field|max_reduce_tasks
specifier|private
name|int
name|max_reduce_tasks
decl_stmt|;
DECL|field|status
specifier|private
name|JobTrackerStatus
name|status
decl_stmt|;
DECL|field|blacklistedTrackersInfo
specifier|private
name|Collection
argument_list|<
name|BlackListInfo
argument_list|>
name|blacklistedTrackersInfo
init|=
operator|new
name|ArrayList
argument_list|<
name|BlackListInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|grayListedTrackers
specifier|private
name|int
name|grayListedTrackers
decl_stmt|;
DECL|method|ClusterStatus ()
name|ClusterStatus
parameter_list|()
block|{}
comment|/**    * Construct a new cluster status.    *     * @param trackers no. of tasktrackers in the cluster    * @param blacklists no of blacklisted task trackers in the cluster    * @param ttExpiryInterval the tasktracker expiry interval    * @param maps no. of currently running map-tasks in the cluster    * @param reduces no. of currently running reduce-tasks in the cluster    * @param maxMaps the maximum no. of map tasks in the cluster    * @param maxReduces the maximum no. of reduce tasks in the cluster    * @param status the {@link JobTrackerStatus} of the<code>JobTracker</code>    */
DECL|method|ClusterStatus (int trackers, int blacklists, long ttExpiryInterval, int maps, int reduces, int maxMaps, int maxReduces, JobTrackerStatus status)
name|ClusterStatus
parameter_list|(
name|int
name|trackers
parameter_list|,
name|int
name|blacklists
parameter_list|,
name|long
name|ttExpiryInterval
parameter_list|,
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|int
name|maxMaps
parameter_list|,
name|int
name|maxReduces
parameter_list|,
name|JobTrackerStatus
name|status
parameter_list|)
block|{
name|this
argument_list|(
name|trackers
argument_list|,
name|blacklists
argument_list|,
name|ttExpiryInterval
argument_list|,
name|maps
argument_list|,
name|reduces
argument_list|,
name|maxMaps
argument_list|,
name|maxReduces
argument_list|,
name|status
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a new cluster status.    *     * @param trackers no. of tasktrackers in the cluster    * @param blacklists no of blacklisted task trackers in the cluster    * @param ttExpiryInterval the tasktracker expiry interval    * @param maps no. of currently running map-tasks in the cluster    * @param reduces no. of currently running reduce-tasks in the cluster    * @param maxMaps the maximum no. of map tasks in the cluster    * @param maxReduces the maximum no. of reduce tasks in the cluster    * @param status the {@link JobTrackerStatus} of the<code>JobTracker</code>    * @param numDecommissionedNodes number of decommission trackers    */
DECL|method|ClusterStatus (int trackers, int blacklists, long ttExpiryInterval, int maps, int reduces, int maxMaps, int maxReduces, JobTrackerStatus status, int numDecommissionedNodes)
name|ClusterStatus
parameter_list|(
name|int
name|trackers
parameter_list|,
name|int
name|blacklists
parameter_list|,
name|long
name|ttExpiryInterval
parameter_list|,
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|int
name|maxMaps
parameter_list|,
name|int
name|maxReduces
parameter_list|,
name|JobTrackerStatus
name|status
parameter_list|,
name|int
name|numDecommissionedNodes
parameter_list|)
block|{
name|this
argument_list|(
name|trackers
argument_list|,
name|blacklists
argument_list|,
name|ttExpiryInterval
argument_list|,
name|maps
argument_list|,
name|reduces
argument_list|,
name|maxMaps
argument_list|,
name|maxReduces
argument_list|,
name|status
argument_list|,
name|numDecommissionedNodes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a new cluster status.    *     * @param trackers no. of tasktrackers in the cluster    * @param blacklists no of blacklisted task trackers in the cluster    * @param ttExpiryInterval the tasktracker expiry interval    * @param maps no. of currently running map-tasks in the cluster    * @param reduces no. of currently running reduce-tasks in the cluster    * @param maxMaps the maximum no. of map tasks in the cluster    * @param maxReduces the maximum no. of reduce tasks in the cluster    * @param status the {@link JobTrackerStatus} of the<code>JobTracker</code>    * @param numDecommissionedNodes number of decommission trackers    * @param numGrayListedTrackers number of graylisted trackers    */
DECL|method|ClusterStatus (int trackers, int blacklists, long ttExpiryInterval, int maps, int reduces, int maxMaps, int maxReduces, JobTrackerStatus status, int numDecommissionedNodes, int numGrayListedTrackers)
name|ClusterStatus
parameter_list|(
name|int
name|trackers
parameter_list|,
name|int
name|blacklists
parameter_list|,
name|long
name|ttExpiryInterval
parameter_list|,
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|int
name|maxMaps
parameter_list|,
name|int
name|maxReduces
parameter_list|,
name|JobTrackerStatus
name|status
parameter_list|,
name|int
name|numDecommissionedNodes
parameter_list|,
name|int
name|numGrayListedTrackers
parameter_list|)
block|{
name|numActiveTrackers
operator|=
name|trackers
expr_stmt|;
name|numBlacklistedTrackers
operator|=
name|blacklists
expr_stmt|;
name|this
operator|.
name|numExcludedNodes
operator|=
name|numDecommissionedNodes
expr_stmt|;
name|this
operator|.
name|ttExpiryInterval
operator|=
name|ttExpiryInterval
expr_stmt|;
name|map_tasks
operator|=
name|maps
expr_stmt|;
name|reduce_tasks
operator|=
name|reduces
expr_stmt|;
name|max_map_tasks
operator|=
name|maxMaps
expr_stmt|;
name|max_reduce_tasks
operator|=
name|maxReduces
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|grayListedTrackers
operator|=
name|numGrayListedTrackers
expr_stmt|;
block|}
comment|/**    * Construct a new cluster status.    *     * @param activeTrackers active tasktrackers in the cluster    * @param blacklistedTrackers blacklisted tasktrackers in the cluster    * @param ttExpiryInterval the tasktracker expiry interval    * @param maps no. of currently running map-tasks in the cluster    * @param reduces no. of currently running reduce-tasks in the cluster    * @param maxMaps the maximum no. of map tasks in the cluster    * @param maxReduces the maximum no. of reduce tasks in the cluster    * @param status the {@link JobTrackerStatus} of the<code>JobTracker</code>    */
DECL|method|ClusterStatus (Collection<String> activeTrackers, Collection<BlackListInfo> blacklistedTrackers, long ttExpiryInterval, int maps, int reduces, int maxMaps, int maxReduces, JobTrackerStatus status)
name|ClusterStatus
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|activeTrackers
parameter_list|,
name|Collection
argument_list|<
name|BlackListInfo
argument_list|>
name|blacklistedTrackers
parameter_list|,
name|long
name|ttExpiryInterval
parameter_list|,
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|int
name|maxMaps
parameter_list|,
name|int
name|maxReduces
parameter_list|,
name|JobTrackerStatus
name|status
parameter_list|)
block|{
name|this
argument_list|(
name|activeTrackers
argument_list|,
name|blacklistedTrackers
argument_list|,
name|ttExpiryInterval
argument_list|,
name|maps
argument_list|,
name|reduces
argument_list|,
name|maxMaps
argument_list|,
name|maxReduces
argument_list|,
name|status
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a new cluster status.    *     * @param activeTrackers active tasktrackers in the cluster    * @param blackListedTrackerInfo blacklisted tasktrackers information     * in the cluster    * @param ttExpiryInterval the tasktracker expiry interval    * @param maps no. of currently running map-tasks in the cluster    * @param reduces no. of currently running reduce-tasks in the cluster    * @param maxMaps the maximum no. of map tasks in the cluster    * @param maxReduces the maximum no. of reduce tasks in the cluster    * @param status the {@link JobTrackerStatus} of the<code>JobTracker</code>    * @param numDecommissionNodes number of decommission trackers    */
DECL|method|ClusterStatus (Collection<String> activeTrackers, Collection<BlackListInfo> blackListedTrackerInfo, long ttExpiryInterval, int maps, int reduces, int maxMaps, int maxReduces, JobTrackerStatus status, int numDecommissionNodes)
name|ClusterStatus
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|activeTrackers
parameter_list|,
name|Collection
argument_list|<
name|BlackListInfo
argument_list|>
name|blackListedTrackerInfo
parameter_list|,
name|long
name|ttExpiryInterval
parameter_list|,
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|int
name|maxMaps
parameter_list|,
name|int
name|maxReduces
parameter_list|,
name|JobTrackerStatus
name|status
parameter_list|,
name|int
name|numDecommissionNodes
parameter_list|)
block|{
name|this
argument_list|(
name|activeTrackers
operator|.
name|size
argument_list|()
argument_list|,
name|blackListedTrackerInfo
operator|.
name|size
argument_list|()
argument_list|,
name|ttExpiryInterval
argument_list|,
name|maps
argument_list|,
name|reduces
argument_list|,
name|maxMaps
argument_list|,
name|maxReduces
argument_list|,
name|status
argument_list|,
name|numDecommissionNodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|activeTrackers
operator|=
name|activeTrackers
expr_stmt|;
name|this
operator|.
name|blacklistedTrackersInfo
operator|=
name|blackListedTrackerInfo
expr_stmt|;
block|}
comment|/**    * Get the number of task trackers in the cluster.    *     * @return the number of task trackers in the cluster.    */
DECL|method|getTaskTrackers ()
specifier|public
name|int
name|getTaskTrackers
parameter_list|()
block|{
return|return
name|numActiveTrackers
return|;
block|}
comment|/**    * Get the names of task trackers in the cluster.    *     * @return the active task trackers in the cluster.    */
DECL|method|getActiveTrackerNames ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getActiveTrackerNames
parameter_list|()
block|{
return|return
name|activeTrackers
return|;
block|}
comment|/**    * Get the names of task trackers in the cluster.    *     * @return the blacklisted task trackers in the cluster.    */
DECL|method|getBlacklistedTrackerNames ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getBlacklistedTrackerNames
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|blacklistedTrackers
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|BlackListInfo
name|bi
range|:
name|blacklistedTrackersInfo
control|)
block|{
name|blacklistedTrackers
operator|.
name|add
argument_list|(
name|bi
operator|.
name|getTrackerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|blacklistedTrackers
return|;
block|}
comment|/**    * Get the names of graylisted task trackers in the cluster.    *    * The gray list of trackers is no longer available on M/R 2.x. The function    * is kept to be compatible with M/R 1.x applications.    *    * @return an empty graylisted task trackers in the cluster.    */
annotation|@
name|Deprecated
DECL|method|getGraylistedTrackerNames ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getGraylistedTrackerNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
comment|/**    * Get the number of graylisted task trackers in the cluster.    *    * The gray list of trackers is no longer available on M/R 2.x. The function    * is kept to be compatible with M/R 1.x applications.    *    * @return 0 graylisted task trackers in the cluster.    */
annotation|@
name|Deprecated
DECL|method|getGraylistedTrackers ()
specifier|public
name|int
name|getGraylistedTrackers
parameter_list|()
block|{
return|return
name|grayListedTrackers
return|;
block|}
comment|/**    * Get the number of blacklisted task trackers in the cluster.    *     * @return the number of blacklisted task trackers in the cluster.    */
DECL|method|getBlacklistedTrackers ()
specifier|public
name|int
name|getBlacklistedTrackers
parameter_list|()
block|{
return|return
name|numBlacklistedTrackers
return|;
block|}
comment|/**    * Get the number of excluded hosts in the cluster.    * @return the number of excluded hosts in the cluster.    */
DECL|method|getNumExcludedNodes ()
specifier|public
name|int
name|getNumExcludedNodes
parameter_list|()
block|{
return|return
name|numExcludedNodes
return|;
block|}
comment|/**    * Get the tasktracker expiry interval for the cluster    * @return the expiry interval in msec    */
DECL|method|getTTExpiryInterval ()
specifier|public
name|long
name|getTTExpiryInterval
parameter_list|()
block|{
return|return
name|ttExpiryInterval
return|;
block|}
comment|/**    * Get the number of currently running map tasks in the cluster.    *     * @return the number of currently running map tasks in the cluster.    */
DECL|method|getMapTasks ()
specifier|public
name|int
name|getMapTasks
parameter_list|()
block|{
return|return
name|map_tasks
return|;
block|}
comment|/**    * Get the number of currently running reduce tasks in the cluster.    *     * @return the number of currently running reduce tasks in the cluster.    */
DECL|method|getReduceTasks ()
specifier|public
name|int
name|getReduceTasks
parameter_list|()
block|{
return|return
name|reduce_tasks
return|;
block|}
comment|/**    * Get the maximum capacity for running map tasks in the cluster.    *     * @return the maximum capacity for running map tasks in the cluster.    */
DECL|method|getMaxMapTasks ()
specifier|public
name|int
name|getMaxMapTasks
parameter_list|()
block|{
return|return
name|max_map_tasks
return|;
block|}
comment|/**    * Get the maximum capacity for running reduce tasks in the cluster.    *     * @return the maximum capacity for running reduce tasks in the cluster.    */
DECL|method|getMaxReduceTasks ()
specifier|public
name|int
name|getMaxReduceTasks
parameter_list|()
block|{
return|return
name|max_reduce_tasks
return|;
block|}
comment|/**    * Get the JobTracker's status.    *     * @return {@link JobTrackerStatus} of the JobTracker    */
DECL|method|getJobTrackerStatus ()
specifier|public
name|JobTrackerStatus
name|getJobTrackerStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
comment|/**    * Returns UNINITIALIZED_MEMORY_VALUE (-1)    */
annotation|@
name|Deprecated
DECL|method|getMaxMemory ()
specifier|public
name|long
name|getMaxMemory
parameter_list|()
block|{
return|return
name|UNINITIALIZED_MEMORY_VALUE
return|;
block|}
comment|/**    * Returns UNINITIALIZED_MEMORY_VALUE (-1)    */
annotation|@
name|Deprecated
DECL|method|getUsedMemory ()
specifier|public
name|long
name|getUsedMemory
parameter_list|()
block|{
return|return
name|UNINITIALIZED_MEMORY_VALUE
return|;
block|}
comment|/**    * Gets the list of blacklisted trackers along with reasons for blacklisting.    *     * @return the collection of {@link BlackListInfo} objects.     *     */
DECL|method|getBlackListedTrackersInfo ()
specifier|public
name|Collection
argument_list|<
name|BlackListInfo
argument_list|>
name|getBlackListedTrackersInfo
parameter_list|()
block|{
return|return
name|blacklistedTrackersInfo
return|;
block|}
comment|/**    * Get the current state of the<code>JobTracker</code>,    * as {@link JobTracker.State}    *    * {@link JobTracker.State} should no longer be used on M/R 2.x. The function    * is kept to be compatible with M/R 1.x applications.    *    * @return the invalid state of the<code>JobTracker</code>.    */
annotation|@
name|Deprecated
DECL|method|getJobTrackerState ()
specifier|public
name|JobTracker
operator|.
name|State
name|getJobTrackerState
parameter_list|()
block|{
return|return
name|JobTracker
operator|.
name|State
operator|.
name|RUNNING
return|;
block|}
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
if|if
condition|(
name|activeTrackers
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|numActiveTrackers
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|activeTrackers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|activeTrackers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|tracker
range|:
name|activeTrackers
control|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|tracker
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|blacklistedTrackersInfo
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
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
name|blacklistedTrackersInfo
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|blacklistedTrackersInfo
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|blacklistedTrackersInfo
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BlackListInfo
name|tracker
range|:
name|blacklistedTrackersInfo
control|)
block|{
name|tracker
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|numExcludedNodes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|ttExpiryInterval
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|map_tasks
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|reduce_tasks
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|max_map_tasks
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|max_reduce_tasks
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|grayListedTrackers
argument_list|)
expr_stmt|;
block|}
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
name|numActiveTrackers
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|int
name|numTrackerNames
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numTrackerNames
operator|>
literal|0
condition|)
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
name|numTrackerNames
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|activeTrackers
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|numBlacklistedTrackers
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|int
name|blackListTrackerInfoSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|blackListTrackerInfoSize
operator|>
literal|0
condition|)
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
name|blackListTrackerInfoSize
condition|;
name|i
operator|++
control|)
block|{
name|BlackListInfo
name|info
init|=
operator|new
name|BlackListInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|blacklistedTrackersInfo
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
name|numExcludedNodes
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|ttExpiryInterval
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|map_tasks
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|reduce_tasks
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|max_map_tasks
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|max_reduce_tasks
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|status
operator|=
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|JobTrackerStatus
operator|.
name|class
argument_list|)
expr_stmt|;
name|grayListedTrackers
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

