begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event
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
name|loghandler
operator|.
name|event
package|;
end_package

begin_enum
DECL|enum|LogHandlerEventType
specifier|public
enum|enum
name|LogHandlerEventType
block|{
DECL|enumConstant|APPLICATION_STARTED
name|APPLICATION_STARTED
block|,
DECL|enumConstant|CONTAINER_FINISHED
name|CONTAINER_FINISHED
block|,
DECL|enumConstant|APPLICATION_FINISHED
DECL|enumConstant|LOG_AGG_TOKEN_UPDATE
name|APPLICATION_FINISHED
block|,
name|LOG_AGG_TOKEN_UPDATE
block|}
end_enum

end_unit

