begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker
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
operator|.
name|docker
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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

begin_comment
comment|/**  * Tests the docker inspect command and its command  * line arguments.  */
end_comment

begin_class
DECL|class|TestDockerInspectCommand
specifier|public
class|class
name|TestDockerInspectCommand
block|{
DECL|field|dockerInspectCommand
specifier|private
name|DockerInspectCommand
name|dockerInspectCommand
decl_stmt|;
DECL|field|CONTAINER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CONTAINER_NAME
init|=
literal|"foo"
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|dockerInspectCommand
operator|=
operator|new
name|DockerInspectCommand
argument_list|(
name|CONTAINER_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetCommandOption ()
specifier|public
name|void
name|testGetCommandOption
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"inspect"
argument_list|,
name|dockerInspectCommand
operator|.
name|getCommandOption
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContainerStatus ()
specifier|public
name|void
name|testGetContainerStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|dockerInspectCommand
operator|.
name|getContainerStatus
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"inspect --format='{{.State.Status}}' foo"
argument_list|,
name|dockerInspectCommand
operator|.
name|getCommandWithArguments
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

