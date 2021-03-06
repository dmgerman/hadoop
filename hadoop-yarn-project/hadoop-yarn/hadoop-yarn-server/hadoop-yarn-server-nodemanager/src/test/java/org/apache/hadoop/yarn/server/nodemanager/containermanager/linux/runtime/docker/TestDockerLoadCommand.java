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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
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

begin_comment
comment|/**  * Tests the docker load command and its command  * line arguments.  */
end_comment

begin_class
DECL|class|TestDockerLoadCommand
specifier|public
class|class
name|TestDockerLoadCommand
block|{
DECL|field|dockerLoadCommand
specifier|private
name|DockerLoadCommand
name|dockerLoadCommand
decl_stmt|;
DECL|field|LOCAL_IMAGE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|LOCAL_IMAGE_NAME
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
name|dockerLoadCommand
operator|=
operator|new
name|DockerLoadCommand
argument_list|(
name|LOCAL_IMAGE_NAME
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
literal|"load"
argument_list|,
name|dockerLoadCommand
operator|.
name|getCommandOption
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetCommandWithArguments ()
specifier|public
name|void
name|testGetCommandWithArguments
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"load"
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|dockerLoadCommand
operator|.
name|getDockerCommandWithArguments
argument_list|()
operator|.
name|get
argument_list|(
literal|"docker-command"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|dockerLoadCommand
operator|.
name|getDockerCommandWithArguments
argument_list|()
operator|.
name|get
argument_list|(
literal|"image"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dockerLoadCommand
operator|.
name|getDockerCommandWithArguments
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

