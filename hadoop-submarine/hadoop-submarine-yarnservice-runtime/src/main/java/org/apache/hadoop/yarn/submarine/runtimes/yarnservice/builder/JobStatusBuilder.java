begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.builder
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
operator|.
name|builder
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Service
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ServiceState
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
name|submarine
operator|.
name|common
operator|.
name|api
operator|.
name|JobComponentStatus
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
name|submarine
operator|.
name|common
operator|.
name|api
operator|.
name|JobState
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
name|submarine
operator|.
name|common
operator|.
name|api
operator|.
name|JobStatus
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
name|List
import|;
end_import

begin_class
DECL|class|JobStatusBuilder
specifier|public
class|class
name|JobStatusBuilder
block|{
DECL|method|fromServiceSpec (Service serviceSpec)
specifier|public
specifier|static
name|JobStatus
name|fromServiceSpec
parameter_list|(
name|Service
name|serviceSpec
parameter_list|)
block|{
name|JobStatus
name|status
init|=
operator|new
name|JobStatus
argument_list|()
decl_stmt|;
name|status
operator|.
name|setState
argument_list|(
name|fromServiceState
argument_list|(
name|serviceSpec
operator|.
name|getState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// If it is a final state, return.
if|if
condition|(
name|JobState
operator|.
name|isFinal
argument_list|(
name|status
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|status
return|;
block|}
name|List
argument_list|<
name|JobComponentStatus
argument_list|>
name|componentStatusList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Component
name|component
range|:
name|serviceSpec
operator|.
name|getComponents
argument_list|()
control|)
block|{
name|componentStatusList
operator|.
name|add
argument_list|(
name|JobComponentStatusBuilder
operator|.
name|fromServiceComponent
argument_list|(
name|component
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|status
operator|.
name|setComponentStatus
argument_list|(
name|componentStatusList
argument_list|)
expr_stmt|;
comment|// TODO, handle tensorboard differently.
comment|// status.setTensorboardLink(getTensorboardLink(serviceSpec, clientContext));
name|status
operator|.
name|setJobName
argument_list|(
name|serviceSpec
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|status
return|;
block|}
DECL|method|fromServiceState (ServiceState serviceState)
specifier|private
specifier|static
name|JobState
name|fromServiceState
parameter_list|(
name|ServiceState
name|serviceState
parameter_list|)
block|{
switch|switch
condition|(
name|serviceState
condition|)
block|{
case|case
name|STOPPED
case|:
comment|// TODO, once YARN-8488 gets committed, we need to update this.
return|return
name|JobState
operator|.
name|SUCCEEDED
return|;
case|case
name|FAILED
case|:
return|return
name|JobState
operator|.
name|FAILED
return|;
block|}
return|return
name|JobState
operator|.
name|RUNNING
return|;
block|}
block|}
end_class

end_unit

