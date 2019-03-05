begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.placement
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
name|placement
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
name|security
operator|.
name|Groups
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
name|ApplicationSubmissionContext
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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FSLeafQueue
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FairScheduler
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
import|import static
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
name|placement
operator|.
name|FairQueuePlacementUtils
operator|.
name|DOT
import|;
end_import

begin_import
import|import static
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
name|placement
operator|.
name|FairQueuePlacementUtils
operator|.
name|assureRoot
import|;
end_import

begin_import
import|import static
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
name|placement
operator|.
name|FairQueuePlacementUtils
operator|.
name|cleanName
import|;
end_import

begin_comment
comment|/**  * Places apps in queues by the secondary group of the submitter, if the  * submitter is a member of more than one group.  * The first "matching" queue based on the group list is returned. The match  * takes into account the parent rule and create flag,  */
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
DECL|class|SecondaryGroupExistingPlacementRule
specifier|public
class|class
name|SecondaryGroupExistingPlacementRule
extends|extends
name|FSPlacementRule
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
name|SecondaryGroupExistingPlacementRule
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|groupProvider
specifier|private
name|Groups
name|groupProvider
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (ResourceScheduler scheduler)
specifier|public
name|boolean
name|initialize
parameter_list|(
name|ResourceScheduler
name|scheduler
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|initialize
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|groupProvider
operator|=
operator|new
name|Groups
argument_list|(
operator|(
operator|(
name|FairScheduler
operator|)
name|scheduler
operator|)
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getPlacementForApp ( ApplicationSubmissionContext asc, String user)
specifier|public
name|ApplicationPlacementContext
name|getPlacementForApp
parameter_list|(
name|ApplicationSubmissionContext
name|asc
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|YarnException
block|{
comment|// All users should have at least one group the primary group. If no groups
comment|// are returned then there is a real issue.
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|groupList
decl_stmt|;
try|try
block|{
name|groupList
operator|=
name|groupProvider
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Group resolution failed"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|String
name|parentQueue
init|=
literal|null
decl_stmt|;
name|PlacementRule
name|parentRule
init|=
name|getParentRule
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentRule
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SecondaryGroupExisting rule: parent rule found: {}"
argument_list|,
name|parentRule
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationPlacementContext
name|parent
init|=
name|parentRule
operator|.
name|getPlacementForApp
argument_list|(
name|asc
argument_list|,
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
operator|||
name|getQueueManager
argument_list|()
operator|.
name|getQueue
argument_list|(
name|parent
operator|.
name|getQueue
argument_list|()
argument_list|)
operator|instanceof
name|FSLeafQueue
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SecondaryGroupExisting rule: parent rule failed"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|parentQueue
operator|=
name|parent
operator|.
name|getQueue
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"SecondaryGroupExisting rule: parent rule result: {}"
argument_list|,
name|parentQueue
argument_list|)
expr_stmt|;
block|}
comment|// now check the groups inside the parent
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|groupList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|group
init|=
name|cleanName
argument_list|(
name|groupList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|queueName
init|=
name|parentQueue
operator|==
literal|null
condition|?
name|assureRoot
argument_list|(
name|group
argument_list|)
else|:
name|parentQueue
operator|+
name|DOT
operator|+
name|group
decl_stmt|;
if|if
condition|(
name|configuredQueue
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
return|return
operator|new
name|ApplicationPlacementContext
argument_list|(
name|queueName
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

