begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor
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
name|monitor
package|;
end_package

begin_enum
DECL|enum|ContainersMonitorEventType
specifier|public
enum|enum
name|ContainersMonitorEventType
block|{
DECL|enumConstant|START_MONITORING_CONTAINER
name|START_MONITORING_CONTAINER
block|,
DECL|enumConstant|STOP_MONITORING_CONTAINER
name|STOP_MONITORING_CONTAINER
block|,
DECL|enumConstant|CHANGE_MONITORING_CONTAINER_RESOURCE
name|CHANGE_MONITORING_CONTAINER_RESOURCE
block|}
end_enum

end_unit

