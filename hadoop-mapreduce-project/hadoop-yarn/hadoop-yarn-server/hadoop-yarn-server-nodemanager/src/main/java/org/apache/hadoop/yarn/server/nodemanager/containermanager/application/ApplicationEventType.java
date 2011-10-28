begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.application
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
name|application
package|;
end_package

begin_enum
DECL|enum|ApplicationEventType
specifier|public
enum|enum
name|ApplicationEventType
block|{
comment|// Source: ContainerManager
DECL|enumConstant|INIT_APPLICATION
name|INIT_APPLICATION
block|,
DECL|enumConstant|INIT_CONTAINER
name|INIT_CONTAINER
block|,
DECL|enumConstant|FINISH_APPLICATION
name|FINISH_APPLICATION
block|,
comment|// Source: ResourceLocalizationService
DECL|enumConstant|APPLICATION_INITED
name|APPLICATION_INITED
block|,
DECL|enumConstant|APPLICATION_RESOURCES_CLEANEDUP
name|APPLICATION_RESOURCES_CLEANEDUP
block|,
comment|// Source: Container
DECL|enumConstant|APPLICATION_CONTAINER_FINISHED
name|APPLICATION_CONTAINER_FINISHED
block|,
comment|// Source: Log Aggregation
DECL|enumConstant|APPLICATION_LOG_AGGREGATION_FINISHED
name|APPLICATION_LOG_AGGREGATION_FINISHED
block|}
end_enum

end_unit

