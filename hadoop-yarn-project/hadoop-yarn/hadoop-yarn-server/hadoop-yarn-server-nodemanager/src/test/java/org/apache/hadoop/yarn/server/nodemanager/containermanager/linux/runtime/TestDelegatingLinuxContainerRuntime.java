begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime
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
name|linux
operator|.
name|runtime
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
name|conf
operator|.
name|YarnConfiguration
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
name|runtime
operator|.
name|ContainerExecutionException
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
name|runtime
operator|.
name|ContainerRuntime
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
name|runtime
operator|.
name|ContainerRuntimeConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test container runtime delegation.  */
end_comment

begin_class
DECL|class|TestDelegatingLinuxContainerRuntime
specifier|public
class|class
name|TestDelegatingLinuxContainerRuntime
block|{
DECL|field|delegatingLinuxContainerRuntime
specifier|private
name|DelegatingLinuxContainerRuntime
name|delegatingLinuxContainerRuntime
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|env
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|delegatingLinuxContainerRuntime
operator|=
operator|new
name|DelegatingLinuxContainerRuntime
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|env
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsRuntimeAllowedDefault ()
specifier|public
name|void
name|testIsRuntimeAllowedDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|)
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DEFAULT
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DOCKER
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|JAVASANDBOX
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsRuntimeAllowedDocker ()
specifier|public
name|void
name|testIsRuntimeAllowedDocker
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
name|ContainerRuntimeConstants
operator|.
name|CONTAINER_RUNTIME_DOCKER
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DOCKER
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DEFAULT
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|JAVASANDBOX
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsRuntimeAllowedJavaSandbox ()
specifier|public
name|void
name|testIsRuntimeAllowedJavaSandbox
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
literal|"javasandbox"
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|JAVASANDBOX
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DEFAULT
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DOCKER
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsRuntimeAllowedMultiple ()
specifier|public
name|void
name|testIsRuntimeAllowedMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
literal|"docker,javasandbox"
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DOCKER
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|JAVASANDBOX
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DEFAULT
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsRuntimeAllowedAll ()
specifier|public
name|void
name|testIsRuntimeAllowedAll
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
literal|"default,docker,javasandbox"
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DEFAULT
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DOCKER
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|JAVASANDBOX
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitializeMissingRuntimeClass ()
specifier|public
name|void
name|testInitializeMissingRuntimeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
literal|"mock"
argument_list|)
expr_stmt|;
try|try
block|{
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"initialize should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
assert|assert
operator|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid runtime set"
argument_list|)
operator|)
assert|;
block|}
block|}
annotation|@
name|Test
DECL|method|testIsRuntimeAllowedMock ()
specifier|public
name|void
name|testIsRuntimeAllowedMock
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
literal|"mock"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_CLASS_FMT
argument_list|,
literal|"mock"
argument_list|)
argument_list|,
name|MockLinuxContainerRuntime
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DEFAULT
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DOCKER
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|JAVASANDBOX
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delegatingLinuxContainerRuntime
operator|.
name|isRuntimeAllowed
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJavaSandboxNotAllowedButPermissive ()
specifier|public
name|void
name|testJavaSandboxNotAllowedButPermissive
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
literal|"default,docker"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_CONTAINER_SANDBOX
argument_list|,
literal|"permissive"
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ContainerRuntime
name|runtime
init|=
name|delegatingLinuxContainerRuntime
operator|.
name|pickContainerRuntime
argument_list|(
name|env
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|runtime
operator|instanceof
name|DefaultLinuxContainerRuntime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJavaSandboxNotAllowedButPermissiveDockerRequested ()
specifier|public
name|void
name|testJavaSandboxNotAllowedButPermissiveDockerRequested
parameter_list|()
throws|throws
name|Exception
block|{
name|env
operator|.
name|put
argument_list|(
name|ContainerRuntimeConstants
operator|.
name|ENV_CONTAINER_TYPE
argument_list|,
name|ContainerRuntimeConstants
operator|.
name|CONTAINER_RUNTIME_DOCKER
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
literal|"default,docker"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_CONTAINER_SANDBOX
argument_list|,
literal|"permissive"
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ContainerRuntime
name|runtime
init|=
name|delegatingLinuxContainerRuntime
operator|.
name|pickContainerRuntime
argument_list|(
name|env
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|runtime
operator|instanceof
name|DockerLinuxContainerRuntime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMockRuntimeSelected ()
specifier|public
name|void
name|testMockRuntimeSelected
parameter_list|()
throws|throws
name|Exception
block|{
name|env
operator|.
name|put
argument_list|(
name|ContainerRuntimeConstants
operator|.
name|ENV_CONTAINER_TYPE
argument_list|,
literal|"mock"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_CLASS_FMT
argument_list|,
literal|"mock"
argument_list|)
argument_list|,
name|MockLinuxContainerRuntime
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
literal|"mock"
argument_list|)
expr_stmt|;
name|delegatingLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ContainerRuntime
name|runtime
init|=
name|delegatingLinuxContainerRuntime
operator|.
name|pickContainerRuntime
argument_list|(
name|env
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|runtime
operator|instanceof
name|MockLinuxContainerRuntime
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

