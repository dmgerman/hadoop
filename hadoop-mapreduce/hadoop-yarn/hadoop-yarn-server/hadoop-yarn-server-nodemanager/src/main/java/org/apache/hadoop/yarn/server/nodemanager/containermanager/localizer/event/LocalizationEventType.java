begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event
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
name|localizer
operator|.
name|event
package|;
end_package

begin_enum
DECL|enum|LocalizationEventType
specifier|public
enum|enum
name|LocalizationEventType
block|{
DECL|enumConstant|INIT_APPLICATION_RESOURCES
name|INIT_APPLICATION_RESOURCES
block|,
DECL|enumConstant|INIT_CONTAINER_RESOURCES
name|INIT_CONTAINER_RESOURCES
block|,
DECL|enumConstant|CACHE_CLEANUP
name|CACHE_CLEANUP
block|,
DECL|enumConstant|CLEANUP_CONTAINER_RESOURCES
name|CLEANUP_CONTAINER_RESOURCES
block|,
DECL|enumConstant|DESTROY_APPLICATION_RESOURCES
name|DESTROY_APPLICATION_RESOURCES
block|, }
end_enum

end_unit

