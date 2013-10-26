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

begin_class
DECL|class|SchedulerAppUtils
specifier|public
class|class
name|SchedulerAppUtils
block|{
DECL|method|isBlacklisted (SchedulerApplication application, SchedulerNode node, Log LOG)
specifier|public
specifier|static
name|boolean
name|isBlacklisted
parameter_list|(
name|SchedulerApplication
name|application
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|Log
name|LOG
parameter_list|)
block|{
if|if
condition|(
name|application
operator|.
name|isBlacklisted
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping 'host' "
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
operator|+
literal|" for "
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" since it has been blacklisted"
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
if|if
condition|(
name|application
operator|.
name|isBlacklisted
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping 'rack' "
operator|+
name|node
operator|.
name|getRackName
argument_list|()
operator|+
literal|" for "
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" since it has been blacklisted"
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

