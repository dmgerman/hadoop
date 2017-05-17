begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.docker
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|docker
package|;
end_package

begin_interface
DECL|interface|DockerKeys
specifier|public
interface|interface
name|DockerKeys
block|{
DECL|field|PROVIDER_DOCKER
name|String
name|PROVIDER_DOCKER
init|=
literal|"docker"
decl_stmt|;
DECL|field|DOCKER_PREFIX
name|String
name|DOCKER_PREFIX
init|=
literal|"docker."
decl_stmt|;
DECL|field|DOCKER_IMAGE
name|String
name|DOCKER_IMAGE
init|=
name|DOCKER_PREFIX
operator|+
literal|"image"
decl_stmt|;
DECL|field|DOCKER_NETWORK
name|String
name|DOCKER_NETWORK
init|=
name|DOCKER_PREFIX
operator|+
literal|"network"
decl_stmt|;
DECL|field|DOCKER_USE_PRIVILEGED
name|String
name|DOCKER_USE_PRIVILEGED
init|=
name|DOCKER_PREFIX
operator|+
literal|"usePrivileged"
decl_stmt|;
DECL|field|DOCKER_START_COMMAND
name|String
name|DOCKER_START_COMMAND
init|=
name|DOCKER_PREFIX
operator|+
literal|"startCommand"
decl_stmt|;
DECL|field|DEFAULT_DOCKER_NETWORK
name|String
name|DEFAULT_DOCKER_NETWORK
init|=
literal|"bridge"
decl_stmt|;
DECL|field|DEFAULT_DOCKER_USE_PRIVILEGED
name|Boolean
name|DEFAULT_DOCKER_USE_PRIVILEGED
init|=
literal|false
decl_stmt|;
block|}
end_interface

end_unit

