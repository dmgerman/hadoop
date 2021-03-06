begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *   */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

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
name|HashMap
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|JobACL
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
import|;
end_import

begin_comment
comment|/**  * This is a wrapper class around {@link LoggedJob}. This provides also the  * extra information about the job obtained from job history which is not  * written to the JSON trace file.  */
end_comment

begin_class
DECL|class|ParsedJob
specifier|public
class|class
name|ParsedJob
extends|extends
name|LoggedJob
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ParsedJob
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|totalCountersMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|totalCountersMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|mapCountersMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|mapCountersMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reduceCountersMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|reduceCountersMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|jobConfPath
specifier|private
name|String
name|jobConfPath
decl_stmt|;
DECL|field|jobAcls
specifier|private
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|jobAcls
decl_stmt|;
DECL|method|ParsedJob ()
name|ParsedJob
parameter_list|()
block|{    }
DECL|method|ParsedJob (String jobID)
name|ParsedJob
parameter_list|(
name|String
name|jobID
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|setJobID
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
block|}
comment|/** Set the job total counters */
DECL|method|putTotalCounters (Map<String, Long> totalCounters)
name|void
name|putTotalCounters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|totalCounters
parameter_list|)
block|{
name|this
operator|.
name|totalCountersMap
operator|=
name|totalCounters
expr_stmt|;
block|}
comment|/**    * @return the job total counters    */
DECL|method|obtainTotalCounters ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|obtainTotalCounters
parameter_list|()
block|{
return|return
name|totalCountersMap
return|;
block|}
comment|/** Set the job level map tasks' counters */
DECL|method|putMapCounters (Map<String, Long> mapCounters)
name|void
name|putMapCounters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|mapCounters
parameter_list|)
block|{
name|this
operator|.
name|mapCountersMap
operator|=
name|mapCounters
expr_stmt|;
block|}
comment|/**    * @return the job level map tasks' counters    */
DECL|method|obtainMapCounters ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|obtainMapCounters
parameter_list|()
block|{
return|return
name|mapCountersMap
return|;
block|}
comment|/** Set the job level reduce tasks' counters */
DECL|method|putReduceCounters (Map<String, Long> reduceCounters)
name|void
name|putReduceCounters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|reduceCounters
parameter_list|)
block|{
name|this
operator|.
name|reduceCountersMap
operator|=
name|reduceCounters
expr_stmt|;
block|}
comment|/**    * @return the job level reduce tasks' counters    */
DECL|method|obtainReduceCounters ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|obtainReduceCounters
parameter_list|()
block|{
return|return
name|reduceCountersMap
return|;
block|}
comment|/** Set the job conf path in staging dir on hdfs */
DECL|method|putJobConfPath (String confPath)
name|void
name|putJobConfPath
parameter_list|(
name|String
name|confPath
parameter_list|)
block|{
name|jobConfPath
operator|=
name|confPath
expr_stmt|;
block|}
comment|/**    * @return the job conf path in staging dir on hdfs    */
DECL|method|obtainJobConfpath ()
specifier|public
name|String
name|obtainJobConfpath
parameter_list|()
block|{
return|return
name|jobConfPath
return|;
block|}
comment|/** Set the job acls */
DECL|method|putJobAcls (Map<JobACL, AccessControlList> acls)
name|void
name|putJobAcls
parameter_list|(
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|acls
parameter_list|)
block|{
name|jobAcls
operator|=
name|acls
expr_stmt|;
block|}
comment|/**    * @return the job acls    */
DECL|method|obtainJobAcls ()
specifier|public
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|obtainJobAcls
parameter_list|()
block|{
return|return
name|jobAcls
return|;
block|}
comment|/**    * @return the list of map tasks of this job    */
DECL|method|obtainMapTasks ()
specifier|public
name|List
argument_list|<
name|ParsedTask
argument_list|>
name|obtainMapTasks
parameter_list|()
block|{
name|List
argument_list|<
name|LoggedTask
argument_list|>
name|tasks
init|=
name|super
operator|.
name|getMapTasks
argument_list|()
decl_stmt|;
return|return
name|convertTasks
argument_list|(
name|tasks
argument_list|)
return|;
block|}
comment|/**    * @return the list of reduce tasks of this job    */
DECL|method|obtainReduceTasks ()
specifier|public
name|List
argument_list|<
name|ParsedTask
argument_list|>
name|obtainReduceTasks
parameter_list|()
block|{
name|List
argument_list|<
name|LoggedTask
argument_list|>
name|tasks
init|=
name|super
operator|.
name|getReduceTasks
argument_list|()
decl_stmt|;
return|return
name|convertTasks
argument_list|(
name|tasks
argument_list|)
return|;
block|}
comment|/**    * @return the list of other tasks of this job    */
DECL|method|obtainOtherTasks ()
specifier|public
name|List
argument_list|<
name|ParsedTask
argument_list|>
name|obtainOtherTasks
parameter_list|()
block|{
name|List
argument_list|<
name|LoggedTask
argument_list|>
name|tasks
init|=
name|super
operator|.
name|getOtherTasks
argument_list|()
decl_stmt|;
return|return
name|convertTasks
argument_list|(
name|tasks
argument_list|)
return|;
block|}
comment|/** As we know that this list of {@link LoggedTask} objects is actually a list    * of {@link ParsedTask} objects, we go ahead and cast them.    * @return the list of {@link ParsedTask} objects    */
DECL|method|convertTasks (List<LoggedTask> tasks)
specifier|private
name|List
argument_list|<
name|ParsedTask
argument_list|>
name|convertTasks
parameter_list|(
name|List
argument_list|<
name|LoggedTask
argument_list|>
name|tasks
parameter_list|)
block|{
name|List
argument_list|<
name|ParsedTask
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|ParsedTask
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|LoggedTask
name|t
range|:
name|tasks
control|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|ParsedTask
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|ParsedTask
operator|)
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected type of tasks in the list..."
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/** Dump the extra info of ParsedJob */
DECL|method|dumpParsedJob ()
name|void
name|dumpParsedJob
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"ParsedJob details:"
operator|+
name|obtainTotalCounters
argument_list|()
operator|+
literal|";"
operator|+
name|obtainMapCounters
argument_list|()
operator|+
literal|";"
operator|+
name|obtainReduceCounters
argument_list|()
operator|+
literal|"\n"
operator|+
name|obtainJobConfpath
argument_list|()
operator|+
literal|"\n"
operator|+
name|obtainJobAcls
argument_list|()
operator|+
literal|";Q="
operator|+
operator|(
name|getQueue
argument_list|()
operator|==
literal|null
condition|?
literal|"null"
else|:
name|getQueue
argument_list|()
operator|.
name|getValue
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ParsedTask
argument_list|>
name|maps
init|=
name|obtainMapTasks
argument_list|()
decl_stmt|;
for|for
control|(
name|ParsedTask
name|task
range|:
name|maps
control|)
block|{
name|task
operator|.
name|dumpParsedTask
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|ParsedTask
argument_list|>
name|reduces
init|=
name|obtainReduceTasks
argument_list|()
decl_stmt|;
for|for
control|(
name|ParsedTask
name|task
range|:
name|reduces
control|)
block|{
name|task
operator|.
name|dumpParsedTask
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|ParsedTask
argument_list|>
name|others
init|=
name|obtainOtherTasks
argument_list|()
decl_stmt|;
for|for
control|(
name|ParsedTask
name|task
range|:
name|others
control|)
block|{
name|task
operator|.
name|dumpParsedTask
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

