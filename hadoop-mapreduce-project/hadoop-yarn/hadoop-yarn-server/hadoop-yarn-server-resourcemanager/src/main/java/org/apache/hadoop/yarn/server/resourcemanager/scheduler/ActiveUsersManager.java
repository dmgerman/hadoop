begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
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
operator|.
name|Private
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
name|yarn
operator|.
name|Lock
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
import|;
end_import

begin_comment
comment|/**  * {@link ActiveUsersManager} tracks active users in the system.  * A user is deemed to be active if he has any running applications with  * outstanding resource requests.  *   * An active user is defined as someone with outstanding resource requests.  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|ActiveUsersManager
specifier|public
class|class
name|ActiveUsersManager
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
name|ActiveUsersManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|QueueMetrics
name|metrics
decl_stmt|;
DECL|field|activeUsers
specifier|private
name|int
name|activeUsers
init|=
literal|0
decl_stmt|;
DECL|field|usersApplications
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|ApplicationId
argument_list|>
argument_list|>
name|usersApplications
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|ApplicationId
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ActiveUsersManager (QueueMetrics metrics)
specifier|public
name|ActiveUsersManager
parameter_list|(
name|QueueMetrics
name|metrics
parameter_list|)
block|{
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
block|}
comment|/**    * An application has new outstanding requests.    *     * @param user application user     * @param applicationId activated application    */
annotation|@
name|Lock
argument_list|(
block|{
name|Queue
operator|.
name|class
block|,
name|SchedulerApp
operator|.
name|class
block|}
argument_list|)
DECL|method|activateApplication ( String user, ApplicationId applicationId)
specifier|synchronized
specifier|public
name|void
name|activateApplication
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|Set
argument_list|<
name|ApplicationId
argument_list|>
name|userApps
init|=
name|usersApplications
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userApps
operator|==
literal|null
condition|)
block|{
name|userApps
operator|=
operator|new
name|HashSet
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
expr_stmt|;
name|usersApplications
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|userApps
argument_list|)
expr_stmt|;
operator|++
name|activeUsers
expr_stmt|;
name|metrics
operator|.
name|incrActiveUsers
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|user
operator|+
literal|" added to activeUsers, currently: "
operator|+
name|activeUsers
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|userApps
operator|.
name|add
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
name|metrics
operator|.
name|activateApp
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * An application has no more outstanding requests.    *     * @param user application user     * @param applicationId deactivated application    */
annotation|@
name|Lock
argument_list|(
block|{
name|Queue
operator|.
name|class
block|,
name|SchedulerApp
operator|.
name|class
block|}
argument_list|)
DECL|method|deactivateApplication ( String user, ApplicationId applicationId)
specifier|synchronized
specifier|public
name|void
name|deactivateApplication
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|Set
argument_list|<
name|ApplicationId
argument_list|>
name|userApps
init|=
name|usersApplications
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|userApps
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|userApps
operator|.
name|remove
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
name|metrics
operator|.
name|deactivateApp
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|userApps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|usersApplications
operator|.
name|remove
argument_list|(
name|user
argument_list|)
expr_stmt|;
operator|--
name|activeUsers
expr_stmt|;
name|metrics
operator|.
name|decrActiveUsers
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|user
operator|+
literal|" removed from activeUsers, currently: "
operator|+
name|activeUsers
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get number of active users i.e. users with applications which have pending    * resource requests.    * @return number of active users    */
annotation|@
name|Lock
argument_list|(
block|{
name|Queue
operator|.
name|class
block|,
name|SchedulerApp
operator|.
name|class
block|}
argument_list|)
DECL|method|getNumActiveUsers ()
specifier|synchronized
specifier|public
name|int
name|getNumActiveUsers
parameter_list|()
block|{
return|return
name|activeUsers
return|;
block|}
block|}
end_class

end_unit

