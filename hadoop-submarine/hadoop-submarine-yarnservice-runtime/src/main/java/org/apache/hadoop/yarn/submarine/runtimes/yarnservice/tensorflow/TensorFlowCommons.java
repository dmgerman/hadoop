begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice.tensorflow
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
name|tensorflow
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
name|conf
operator|.
name|Configuration
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
name|ServiceApiConstants
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
name|submarine
operator|.
name|common
operator|.
name|Envs
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
name|Role
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
name|runtimes
operator|.
name|yarnservice
operator|.
name|YarnServiceUtils
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

begin_comment
comment|/**  * This class has common helper methods for TensorFlow.  */
end_comment

begin_class
DECL|class|TensorFlowCommons
specifier|public
specifier|final
class|class
name|TensorFlowCommons
block|{
DECL|method|TensorFlowCommons ()
specifier|private
name|TensorFlowCommons
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This class should not be "
operator|+
literal|"instantiated!"
argument_list|)
throw|;
block|}
DECL|method|addCommonEnvironments (Component component, Role role)
specifier|public
specifier|static
name|void
name|addCommonEnvironments
parameter_list|(
name|Component
name|component
parameter_list|,
name|Role
name|role
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|envs
init|=
name|component
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getEnv
argument_list|()
decl_stmt|;
name|envs
operator|.
name|put
argument_list|(
name|Envs
operator|.
name|TASK_INDEX_ENV
argument_list|,
name|ServiceApiConstants
operator|.
name|COMPONENT_ID
argument_list|)
expr_stmt|;
name|envs
operator|.
name|put
argument_list|(
name|Envs
operator|.
name|TASK_TYPE_ENV
argument_list|,
name|role
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getUserName ()
specifier|public
specifier|static
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
return|;
block|}
DECL|method|getDNSDomain (Configuration yarnConfig)
specifier|public
specifier|static
name|String
name|getDNSDomain
parameter_list|(
name|Configuration
name|yarnConfig
parameter_list|)
block|{
return|return
name|yarnConfig
operator|.
name|get
argument_list|(
literal|"hadoop.registry.dns.domain-name"
argument_list|)
return|;
block|}
DECL|method|getScriptFileName (Role role)
specifier|public
specifier|static
name|String
name|getScriptFileName
parameter_list|(
name|Role
name|role
parameter_list|)
block|{
return|return
literal|"run-"
operator|+
name|role
operator|.
name|getName
argument_list|()
operator|+
literal|".sh"
return|;
block|}
DECL|method|getTFConfigEnv (String componentName, int nWorkers, int nPs, String serviceName, String userName, String domain)
specifier|public
specifier|static
name|String
name|getTFConfigEnv
parameter_list|(
name|String
name|componentName
parameter_list|,
name|int
name|nWorkers
parameter_list|,
name|int
name|nPs
parameter_list|,
name|String
name|serviceName
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|domain
parameter_list|)
block|{
name|String
name|commonEndpointSuffix
init|=
name|YarnServiceUtils
operator|.
name|getDNSNameCommonSuffix
argument_list|(
name|serviceName
argument_list|,
name|userName
argument_list|,
name|domain
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
name|String
name|json
init|=
literal|"{\\\"cluster\\\":{"
decl_stmt|;
name|String
name|master
init|=
name|getComponentArrayJson
argument_list|(
literal|"master"
argument_list|,
literal|1
argument_list|,
name|commonEndpointSuffix
argument_list|)
operator|+
literal|","
decl_stmt|;
name|String
name|worker
init|=
name|getComponentArrayJson
argument_list|(
literal|"worker"
argument_list|,
name|nWorkers
operator|-
literal|1
argument_list|,
name|commonEndpointSuffix
argument_list|)
operator|+
literal|","
decl_stmt|;
name|String
name|ps
init|=
name|getComponentArrayJson
argument_list|(
literal|"ps"
argument_list|,
name|nPs
argument_list|,
name|commonEndpointSuffix
argument_list|)
operator|+
literal|"},"
decl_stmt|;
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
literal|"\\\"task\\\":{"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" \\\"type\\\":\\\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|componentName
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\\\","
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" \\\"index\\\":"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'$'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Envs
operator|.
name|TASK_INDEX_ENV
operator|+
literal|"},"
argument_list|)
expr_stmt|;
name|String
name|task
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|environment
init|=
literal|"\\\"environment\\\":\\\"cloud\\\"}"
decl_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|worker
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|environment
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getComponentArrayJson (String componentName, int count, String endpointSuffix)
specifier|private
specifier|static
name|String
name|getComponentArrayJson
parameter_list|(
name|String
name|componentName
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|endpointSuffix
parameter_list|)
block|{
name|String
name|component
init|=
literal|"\\\""
operator|+
name|componentName
operator|+
literal|"\\\":"
decl_stmt|;
name|StringBuilder
name|array
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|array
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|array
operator|.
name|append
argument_list|(
literal|"\\\""
argument_list|)
expr_stmt|;
name|array
operator|.
name|append
argument_list|(
name|componentName
argument_list|)
expr_stmt|;
name|array
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
name|array
operator|.
name|append
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|array
operator|.
name|append
argument_list|(
name|endpointSuffix
argument_list|)
expr_stmt|;
name|array
operator|.
name|append
argument_list|(
literal|"\\\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|count
operator|-
literal|1
condition|)
block|{
name|array
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
name|array
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|component
operator|+
name|array
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

