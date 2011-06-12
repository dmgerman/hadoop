begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.server.jobtracker
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|server
operator|.
name|jobtracker
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|jsp
operator|.
name|JspWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
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
name|ClusterStatus
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
name|JobInProgress
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
name|JobProfile
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
name|JobStatus
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
name|JobTracker
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
name|TaskTrackerStatus
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
name|JobID
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
name|TaskType
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
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Methods to help format output for JobTracker XML JSPX  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|JobTrackerJspHelper
specifier|public
class|class
name|JobTrackerJspHelper
block|{
DECL|method|JobTrackerJspHelper ()
specifier|public
name|JobTrackerJspHelper
parameter_list|()
block|{
name|percentFormat
operator|=
operator|new
name|DecimalFormat
argument_list|(
literal|"##0.00"
argument_list|)
expr_stmt|;
block|}
DECL|field|percentFormat
specifier|private
name|DecimalFormat
name|percentFormat
decl_stmt|;
comment|/**    * Returns an XML-formatted table of the jobs in the list.    * This is called repeatedly for different lists of jobs (e.g., running, completed, failed).    */
DECL|method|generateJobTable (JspWriter out, String label, List<JobInProgress> jobs)
specifier|public
name|void
name|generateJobTable
parameter_list|(
name|JspWriter
name|out
parameter_list|,
name|String
name|label
parameter_list|,
name|List
argument_list|<
name|JobInProgress
argument_list|>
name|jobs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jobs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|JobInProgress
name|job
range|:
name|jobs
control|)
block|{
name|JobProfile
name|profile
init|=
name|job
operator|.
name|getProfile
argument_list|()
decl_stmt|;
name|JobStatus
name|status
init|=
name|job
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|JobID
name|jobid
init|=
name|profile
operator|.
name|getJobID
argument_list|()
decl_stmt|;
name|int
name|desiredMaps
init|=
name|job
operator|.
name|desiredMaps
argument_list|()
decl_stmt|;
name|int
name|desiredReduces
init|=
name|job
operator|.
name|desiredReduces
argument_list|()
decl_stmt|;
name|int
name|completedMaps
init|=
name|job
operator|.
name|finishedMaps
argument_list|()
decl_stmt|;
name|int
name|completedReduces
init|=
name|job
operator|.
name|finishedReduces
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|profile
operator|.
name|getJobName
argument_list|()
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<"
operator|+
name|label
operator|+
literal|"_job jobid=\""
operator|+
name|jobid
operator|+
literal|"\">\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<jobid>"
operator|+
name|jobid
operator|+
literal|"</jobid>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<user>"
operator|+
name|profile
operator|.
name|getUser
argument_list|()
operator|+
literal|"</user>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<name>"
operator|+
operator|(
literal|""
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|?
literal|"&nbsp;"
else|:
name|name
operator|)
operator|+
literal|"</name>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<map_complete>"
operator|+
name|StringUtils
operator|.
name|formatPercent
argument_list|(
name|status
operator|.
name|mapProgress
argument_list|()
argument_list|,
literal|2
argument_list|)
operator|+
literal|"</map_complete>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<map_total>"
operator|+
name|desiredMaps
operator|+
literal|"</map_total>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<maps_completed>"
operator|+
name|completedMaps
operator|+
literal|"</maps_completed>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<reduce_complete>"
operator|+
name|StringUtils
operator|.
name|formatPercent
argument_list|(
name|status
operator|.
name|reduceProgress
argument_list|()
argument_list|,
literal|2
argument_list|)
operator|+
literal|"</reduce_complete>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<reduce_total>"
operator|+
name|desiredReduces
operator|+
literal|"</reduce_total>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<reduces_completed>"
operator|+
name|completedReduces
operator|+
literal|"</reduces_completed>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</"
operator|+
name|label
operator|+
literal|"_job>\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Generates an XML-formatted block that summarizes the state of the JobTracker.    */
DECL|method|generateSummaryTable (JspWriter out, JobTracker tracker)
specifier|public
name|void
name|generateSummaryTable
parameter_list|(
name|JspWriter
name|out
parameter_list|,
name|JobTracker
name|tracker
parameter_list|)
throws|throws
name|IOException
block|{
name|ClusterStatus
name|status
init|=
name|tracker
operator|.
name|getClusterStatus
argument_list|()
decl_stmt|;
name|int
name|maxMapTasks
init|=
name|status
operator|.
name|getMaxMapTasks
argument_list|()
decl_stmt|;
name|int
name|maxReduceTasks
init|=
name|status
operator|.
name|getMaxReduceTasks
argument_list|()
decl_stmt|;
name|int
name|numTaskTrackers
init|=
name|status
operator|.
name|getTaskTrackers
argument_list|()
decl_stmt|;
name|String
name|tasksPerNodeStr
decl_stmt|;
if|if
condition|(
name|numTaskTrackers
operator|>
literal|0
condition|)
block|{
name|double
name|tasksPerNodePct
init|=
call|(
name|double
call|)
argument_list|(
name|maxMapTasks
operator|+
name|maxReduceTasks
argument_list|)
operator|/
operator|(
name|double
operator|)
name|numTaskTrackers
decl_stmt|;
name|tasksPerNodeStr
operator|=
name|percentFormat
operator|.
name|format
argument_list|(
name|tasksPerNodePct
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tasksPerNodeStr
operator|=
literal|"-"
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"<maps>"
operator|+
name|status
operator|.
name|getMapTasks
argument_list|()
operator|+
literal|"</maps>\n"
operator|+
literal|"<reduces>"
operator|+
name|status
operator|.
name|getReduceTasks
argument_list|()
operator|+
literal|"</reduces>\n"
operator|+
literal|"<total_submissions>"
operator|+
name|tracker
operator|.
name|getTotalSubmissions
argument_list|()
operator|+
literal|"</total_submissions>\n"
operator|+
literal|"<nodes>"
operator|+
name|status
operator|.
name|getTaskTrackers
argument_list|()
operator|+
literal|"</nodes>\n"
operator|+
literal|"<map_task_capacity>"
operator|+
name|status
operator|.
name|getMaxMapTasks
argument_list|()
operator|+
literal|"</map_task_capacity>\n"
operator|+
literal|"<reduce_task_capacity>"
operator|+
name|status
operator|.
name|getMaxReduceTasks
argument_list|()
operator|+
literal|"</reduce_task_capacity>\n"
operator|+
literal|"<avg_tasks_per_node>"
operator|+
name|tasksPerNodeStr
operator|+
literal|"</avg_tasks_per_node>\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

