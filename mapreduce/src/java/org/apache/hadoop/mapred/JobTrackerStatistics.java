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
name|Map
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
name|StatisticsCollector
operator|.
name|Stat
import|;
end_import

begin_comment
comment|/**  * Collects the job tracker statistics.  *  */
end_comment

begin_class
DECL|class|JobTrackerStatistics
class|class
name|JobTrackerStatistics
block|{
DECL|field|collector
specifier|final
name|StatisticsCollector
name|collector
decl_stmt|;
DECL|field|ttStats
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TaskTrackerStat
argument_list|>
name|ttStats
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TaskTrackerStat
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|JobTrackerStatistics ()
name|JobTrackerStatistics
parameter_list|()
block|{
name|collector
operator|=
operator|new
name|StatisticsCollector
argument_list|()
expr_stmt|;
name|collector
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|taskTrackerAdded (String name)
specifier|synchronized
name|void
name|taskTrackerAdded
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|TaskTrackerStat
name|stat
init|=
name|ttStats
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|==
literal|null
condition|)
block|{
name|stat
operator|=
operator|new
name|TaskTrackerStat
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|ttStats
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|taskTrackerRemoved (String name)
specifier|synchronized
name|void
name|taskTrackerRemoved
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|TaskTrackerStat
name|stat
init|=
name|ttStats
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|!=
literal|null
condition|)
block|{
name|stat
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getTaskTrackerStat (String name)
specifier|synchronized
name|TaskTrackerStat
name|getTaskTrackerStat
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|ttStats
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|class|TaskTrackerStat
class|class
name|TaskTrackerStat
block|{
DECL|field|totalTasksKey
specifier|final
name|String
name|totalTasksKey
decl_stmt|;
DECL|field|totalTasksStat
specifier|final
name|Stat
name|totalTasksStat
decl_stmt|;
DECL|field|succeededTasksKey
specifier|final
name|String
name|succeededTasksKey
decl_stmt|;
DECL|field|succeededTasksStat
specifier|final
name|Stat
name|succeededTasksStat
decl_stmt|;
DECL|field|healthCheckFailedKey
specifier|final
name|String
name|healthCheckFailedKey
decl_stmt|;
DECL|field|healthCheckFailedStat
specifier|final
name|Stat
name|healthCheckFailedStat
decl_stmt|;
DECL|method|TaskTrackerStat (String trackerName)
name|TaskTrackerStat
parameter_list|(
name|String
name|trackerName
parameter_list|)
block|{
name|totalTasksKey
operator|=
name|trackerName
operator|+
literal|"-"
operator|+
literal|"totalTasks"
expr_stmt|;
name|totalTasksStat
operator|=
name|collector
operator|.
name|createStat
argument_list|(
name|totalTasksKey
argument_list|)
expr_stmt|;
name|succeededTasksKey
operator|=
name|trackerName
operator|+
literal|"-"
operator|+
literal|"succeededTasks"
expr_stmt|;
name|succeededTasksStat
operator|=
name|collector
operator|.
name|createStat
argument_list|(
name|succeededTasksKey
argument_list|)
expr_stmt|;
name|healthCheckFailedKey
operator|=
name|trackerName
operator|+
literal|"-"
operator|+
literal|"healthcheckfailed"
expr_stmt|;
name|healthCheckFailedStat
operator|=
name|collector
operator|.
name|createStat
argument_list|(
name|healthCheckFailedKey
argument_list|)
expr_stmt|;
block|}
DECL|method|incrTotalTasks ()
specifier|synchronized
name|void
name|incrTotalTasks
parameter_list|()
block|{
name|totalTasksStat
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
DECL|method|incrSucceededTasks ()
specifier|synchronized
name|void
name|incrSucceededTasks
parameter_list|()
block|{
name|succeededTasksStat
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
DECL|method|incrHealthCheckFailed ()
specifier|synchronized
name|void
name|incrHealthCheckFailed
parameter_list|()
block|{
name|healthCheckFailedStat
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
DECL|method|remove ()
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
name|collector
operator|.
name|removeStat
argument_list|(
name|totalTasksKey
argument_list|)
expr_stmt|;
name|collector
operator|.
name|removeStat
argument_list|(
name|succeededTasksKey
argument_list|)
expr_stmt|;
name|collector
operator|.
name|removeStat
argument_list|(
name|healthCheckFailedKey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

