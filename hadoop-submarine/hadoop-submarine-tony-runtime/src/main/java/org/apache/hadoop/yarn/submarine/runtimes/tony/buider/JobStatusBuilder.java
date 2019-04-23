begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.tony.buider
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
name|tony
operator|.
name|buider
package|;
end_package

begin_import
import|import
name|com
operator|.
name|linkedin
operator|.
name|tony
operator|.
name|rpc
operator|.
name|TaskInfo
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * JobStatusBuilder builds the job status from a set of TaskInfos.  */
end_comment

begin_class
DECL|class|JobStatusBuilder
specifier|public
specifier|final
class|class
name|JobStatusBuilder
block|{
DECL|method|fromTaskInfoSet (final Set<TaskInfo> taskInfos)
specifier|public
specifier|static
name|JobStatus
name|fromTaskInfoSet
parameter_list|(
specifier|final
name|Set
argument_list|<
name|TaskInfo
argument_list|>
name|taskInfos
parameter_list|)
block|{
name|JobStatus
name|status
init|=
operator|new
name|JobStatus
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|jobNames
init|=
name|taskInfos
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|TaskInfo
operator|::
name|getName
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|JobComponentStatus
argument_list|>
name|jobComponentStatusList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|jobName
range|:
name|jobNames
control|)
block|{
name|Set
argument_list|<
name|TaskInfo
argument_list|>
name|filterTasks
init|=
name|taskInfos
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|taskInfo
lambda|->
name|taskInfo
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|jobName
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|numReadyContainers
init|=
literal|0
decl_stmt|;
name|long
name|numRunningContainers
init|=
literal|0
decl_stmt|;
name|long
name|totalAskedContainers
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TaskInfo
name|taskInfo
range|:
name|filterTasks
control|)
block|{
name|totalAskedContainers
operator|+=
literal|1
expr_stmt|;
switch|switch
condition|(
name|taskInfo
operator|.
name|getStatus
argument_list|()
condition|)
block|{
case|case
name|READY
case|:
name|numReadyContainers
operator|+=
literal|1
expr_stmt|;
break|break;
case|case
name|RUNNING
case|:
name|numRunningContainers
operator|+=
literal|1
expr_stmt|;
break|break;
default|default:
block|}
block|}
name|jobComponentStatusList
operator|.
name|add
argument_list|(
operator|new
name|JobComponentStatus
argument_list|(
name|jobName
argument_list|,
name|numReadyContainers
argument_list|,
name|numRunningContainers
argument_list|,
name|totalAskedContainers
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|status
operator|.
name|setComponentStatus
argument_list|(
name|jobComponentStatusList
argument_list|)
expr_stmt|;
return|return
name|status
return|;
block|}
DECL|method|JobStatusBuilder ()
specifier|private
name|JobStatusBuilder
parameter_list|()
block|{ }
block|}
end_class

end_unit

