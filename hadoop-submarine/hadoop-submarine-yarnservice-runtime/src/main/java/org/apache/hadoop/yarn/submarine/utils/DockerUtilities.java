begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.utils
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
name|utils
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
name|Artifact
import|;
end_import

begin_comment
comment|/**  * Utilities for Docker-related operations.  */
end_comment

begin_class
DECL|class|DockerUtilities
specifier|public
specifier|final
class|class
name|DockerUtilities
block|{
DECL|method|DockerUtilities ()
specifier|private
name|DockerUtilities
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
DECL|method|getDockerArtifact (String dockerImageName)
specifier|public
specifier|static
name|Artifact
name|getDockerArtifact
parameter_list|(
name|String
name|dockerImageName
parameter_list|)
block|{
return|return
operator|new
name|Artifact
argument_list|()
operator|.
name|type
argument_list|(
name|Artifact
operator|.
name|TypeEnum
operator|.
name|DOCKER
argument_list|)
operator|.
name|id
argument_list|(
name|dockerImageName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

