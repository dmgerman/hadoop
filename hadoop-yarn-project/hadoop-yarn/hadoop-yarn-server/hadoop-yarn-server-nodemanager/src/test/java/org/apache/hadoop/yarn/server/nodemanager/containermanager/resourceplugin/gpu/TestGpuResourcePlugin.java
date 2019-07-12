begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestGpuResourcePlugin
specifier|public
class|class
name|TestGpuResourcePlugin
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|YarnException
operator|.
name|class
argument_list|)
DECL|method|testResourceHandlerNotInitialized ()
specifier|public
name|void
name|testResourceHandlerNotInitialized
parameter_list|()
throws|throws
name|YarnException
block|{
name|GpuDiscoverer
name|gpuDiscoverer
init|=
name|mock
argument_list|(
name|GpuDiscoverer
operator|.
name|class
argument_list|)
decl_stmt|;
name|GpuNodeResourceUpdateHandler
name|gpuNodeResourceUpdateHandler
init|=
name|mock
argument_list|(
name|GpuNodeResourceUpdateHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|GpuResourcePlugin
name|target
init|=
operator|new
name|GpuResourcePlugin
argument_list|(
name|gpuNodeResourceUpdateHandler
argument_list|,
name|gpuDiscoverer
argument_list|)
decl_stmt|;
name|target
operator|.
name|getNMResourceInfo
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResourceHandlerIsInitialized ()
specifier|public
name|void
name|testResourceHandlerIsInitialized
parameter_list|()
throws|throws
name|YarnException
block|{
name|GpuDiscoverer
name|gpuDiscoverer
init|=
name|mock
argument_list|(
name|GpuDiscoverer
operator|.
name|class
argument_list|)
decl_stmt|;
name|GpuNodeResourceUpdateHandler
name|gpuNodeResourceUpdateHandler
init|=
name|mock
argument_list|(
name|GpuNodeResourceUpdateHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|GpuResourcePlugin
name|target
init|=
operator|new
name|GpuResourcePlugin
argument_list|(
name|gpuNodeResourceUpdateHandler
argument_list|,
name|gpuDiscoverer
argument_list|)
decl_stmt|;
name|target
operator|.
name|createResourceHandler
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//Not throwing any exception
name|target
operator|.
name|getNMResourceInfo
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

