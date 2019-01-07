begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|resourceplugin
operator|.
name|gpu
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|ResourceInformation
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ResourceMappings
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|runtime
operator|.
name|docker
operator|.
name|DockerRunCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * test for NvidiaDockerV2CommandPlugin.  */
end_comment

begin_class
DECL|class|TestNvidiaDockerV2CommandPlugin
specifier|public
class|class
name|TestNvidiaDockerV2CommandPlugin
block|{
DECL|method|copyCommandLine ( Map<String, List<String>> map)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|copyCommandLine
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|ret
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ret
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|commandlinesEquals (Map<String, List<String>> cli1, Map<String, List<String>> cli2)
specifier|private
name|boolean
name|commandlinesEquals
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|cli1
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|cli2
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|cli1
operator|.
name|keySet
argument_list|()
argument_list|,
name|cli2
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|key
range|:
name|cli1
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|value1
init|=
name|cli1
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|value2
init|=
name|cli2
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|value1
operator|.
name|equals
argument_list|(
name|value2
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|class|MyNvidiaDockerV2CommandPlugin
specifier|static
class|class
name|MyNvidiaDockerV2CommandPlugin
extends|extends
name|NvidiaDockerV2CommandPlugin
block|{
DECL|field|requestsGpu
specifier|private
name|boolean
name|requestsGpu
init|=
literal|false
decl_stmt|;
DECL|method|MyNvidiaDockerV2CommandPlugin ()
name|MyNvidiaDockerV2CommandPlugin
parameter_list|()
block|{}
DECL|method|setRequestsGpu (boolean r)
specifier|public
name|void
name|setRequestsGpu
parameter_list|(
name|boolean
name|r
parameter_list|)
block|{
name|requestsGpu
operator|=
name|r
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requestsGpu (Container container)
specifier|protected
name|boolean
name|requestsGpu
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
return|return
name|requestsGpu
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPlugin ()
specifier|public
name|void
name|testPlugin
parameter_list|()
throws|throws
name|Exception
block|{
name|DockerRunCommand
name|runCommand
init|=
operator|new
name|DockerRunCommand
argument_list|(
literal|"container_1"
argument_list|,
literal|"user"
argument_list|,
literal|"fakeimage"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|originalCommandline
init|=
name|copyCommandLine
argument_list|(
name|runCommand
operator|.
name|getDockerCommandWithArguments
argument_list|()
argument_list|)
decl_stmt|;
name|MyNvidiaDockerV2CommandPlugin
name|commandPlugin
init|=
operator|new
name|MyNvidiaDockerV2CommandPlugin
argument_list|()
decl_stmt|;
name|Container
name|nmContainer
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// getResourceMapping is null, so commandline won't be updated
name|commandPlugin
operator|.
name|updateDockerRunCommand
argument_list|(
name|runCommand
argument_list|,
name|nmContainer
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|commandlinesEquals
argument_list|(
name|originalCommandline
argument_list|,
name|runCommand
operator|.
name|getDockerCommandWithArguments
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// no GPU resource assigned, so commandline won't be updated
name|ResourceMappings
name|resourceMappings
init|=
operator|new
name|ResourceMappings
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|nmContainer
operator|.
name|getResourceMappings
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|resourceMappings
argument_list|)
expr_stmt|;
name|commandPlugin
operator|.
name|updateDockerRunCommand
argument_list|(
name|runCommand
argument_list|,
name|nmContainer
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|commandlinesEquals
argument_list|(
name|originalCommandline
argument_list|,
name|runCommand
operator|.
name|getDockerCommandWithArguments
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assign GPU resource
name|ResourceMappings
operator|.
name|AssignedResources
name|assigned
init|=
operator|new
name|ResourceMappings
operator|.
name|AssignedResources
argument_list|()
decl_stmt|;
name|assigned
operator|.
name|updateAssignedResources
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|GpuDevice
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|GpuDevice
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|resourceMappings
operator|.
name|addAssignedResources
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|,
name|assigned
argument_list|)
expr_stmt|;
name|commandPlugin
operator|.
name|setRequestsGpu
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|commandPlugin
operator|.
name|updateDockerRunCommand
argument_list|(
name|runCommand
argument_list|,
name|nmContainer
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|newCommandLine
init|=
name|runCommand
operator|.
name|getDockerCommandWithArguments
argument_list|()
decl_stmt|;
comment|// Command line will be updated
name|Assert
operator|.
name|assertFalse
argument_list|(
name|commandlinesEquals
argument_list|(
name|originalCommandline
argument_list|,
name|newCommandLine
argument_list|)
argument_list|)
expr_stmt|;
comment|// NVIDIA_VISIBLE_DEVICES will be set
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runCommand
operator|.
name|getEnv
argument_list|()
operator|.
name|get
argument_list|(
literal|"NVIDIA_VISIBLE_DEVICES"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"0,1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// runtime should exist
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newCommandLine
operator|.
name|containsKey
argument_list|(
literal|"runtime"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

