begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.launcher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|launcher
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|event
operator|.
name|EventHandler
import|;
end_import

begin_interface
DECL|interface|ContainerLauncher
specifier|public
interface|interface
name|ContainerLauncher
extends|extends
name|EventHandler
argument_list|<
name|ContainerLauncherEvent
argument_list|>
block|{
DECL|enum|EventType
enum|enum
name|EventType
block|{
DECL|enumConstant|CONTAINER_REMOTE_LAUNCH
name|CONTAINER_REMOTE_LAUNCH
block|,
DECL|enumConstant|CONTAINER_REMOTE_CLEANUP
name|CONTAINER_REMOTE_CLEANUP
block|,
comment|// When TaskAttempt receives TA_CONTAINER_COMPLETED,
comment|// it will notify ContainerLauncher so that the container can be removed
comment|// from ContainerLauncher's launched containers list
comment|// Otherwise, ContainerLauncher will try to stop the containers as part of
comment|// serviceStop.
DECL|enumConstant|CONTAINER_COMPLETED
name|CONTAINER_COMPLETED
block|}
block|}
end_interface

end_unit

