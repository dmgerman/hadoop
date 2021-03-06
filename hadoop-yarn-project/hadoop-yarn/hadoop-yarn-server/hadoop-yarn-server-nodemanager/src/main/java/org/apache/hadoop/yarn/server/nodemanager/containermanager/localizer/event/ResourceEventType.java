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
name|localizer
operator|.
name|LocalizedResource
import|;
end_import

begin_comment
comment|/**  * Events delivered to {@link LocalizedResource}. Each of these  * events is a subclass of {@link ResourceEvent}.  */
end_comment

begin_enum
DECL|enum|ResourceEventType
specifier|public
enum|enum
name|ResourceEventType
block|{
comment|/** See {@link ResourceRequestEvent} */
DECL|enumConstant|REQUEST
name|REQUEST
block|,
comment|/** See {@link ResourceLocalizedEvent} */
DECL|enumConstant|LOCALIZED
name|LOCALIZED
block|,
comment|/** See {@link ResourceReleaseEvent} */
DECL|enumConstant|RELEASE
name|RELEASE
block|,
comment|/** See {@link ResourceFailedLocalizationEvent} */
DECL|enumConstant|LOCALIZATION_FAILED
name|LOCALIZATION_FAILED
block|,
comment|/** See {@link ResourceRecoveredEvent} */
DECL|enumConstant|RECOVERED
name|RECOVERED
block|}
end_enum

end_unit

