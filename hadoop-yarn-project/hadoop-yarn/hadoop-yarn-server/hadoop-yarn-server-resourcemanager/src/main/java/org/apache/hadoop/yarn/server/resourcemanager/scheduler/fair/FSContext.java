begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
operator|.
name|fair
package|;
end_package

begin_comment
comment|/**  * Helper class that holds basic information to be passed around  * FairScheduler classes. Think of this as a glorified map that holds key  * information about the scheduler.  */
end_comment

begin_class
DECL|class|FSContext
specifier|public
class|class
name|FSContext
block|{
comment|// Preemption-related info
DECL|field|preemptionEnabled
specifier|private
name|boolean
name|preemptionEnabled
init|=
literal|false
decl_stmt|;
DECL|field|preemptionUtilizationThreshold
specifier|private
name|float
name|preemptionUtilizationThreshold
decl_stmt|;
DECL|field|starvedApps
specifier|private
name|FSStarvedApps
name|starvedApps
decl_stmt|;
DECL|method|isPreemptionEnabled ()
specifier|public
name|boolean
name|isPreemptionEnabled
parameter_list|()
block|{
return|return
name|preemptionEnabled
return|;
block|}
DECL|method|setPreemptionEnabled ()
specifier|public
name|void
name|setPreemptionEnabled
parameter_list|()
block|{
name|this
operator|.
name|preemptionEnabled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|starvedApps
operator|==
literal|null
condition|)
block|{
name|starvedApps
operator|=
operator|new
name|FSStarvedApps
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getStarvedApps ()
specifier|public
name|FSStarvedApps
name|getStarvedApps
parameter_list|()
block|{
return|return
name|starvedApps
return|;
block|}
DECL|method|getPreemptionUtilizationThreshold ()
specifier|public
name|float
name|getPreemptionUtilizationThreshold
parameter_list|()
block|{
return|return
name|preemptionUtilizationThreshold
return|;
block|}
DECL|method|setPreemptionUtilizationThreshold ( float preemptionUtilizationThreshold)
specifier|public
name|void
name|setPreemptionUtilizationThreshold
parameter_list|(
name|float
name|preemptionUtilizationThreshold
parameter_list|)
block|{
name|this
operator|.
name|preemptionUtilizationThreshold
operator|=
name|preemptionUtilizationThreshold
expr_stmt|;
block|}
block|}
end_class

end_unit

