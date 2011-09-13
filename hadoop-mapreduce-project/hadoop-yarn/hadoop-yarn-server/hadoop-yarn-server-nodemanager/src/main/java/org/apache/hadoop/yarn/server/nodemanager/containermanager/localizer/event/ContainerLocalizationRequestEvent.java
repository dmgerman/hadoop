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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|api
operator|.
name|records
operator|.
name|LocalResourceVisibility
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|LocalResourceRequest
import|;
end_import

begin_class
DECL|class|ContainerLocalizationRequestEvent
specifier|public
class|class
name|ContainerLocalizationRequestEvent
extends|extends
name|ContainerLocalizationEvent
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|LocalResourceVisibility
argument_list|,
name|Collection
argument_list|<
name|LocalResourceRequest
argument_list|>
argument_list|>
DECL|field|rsrc
name|rsrc
decl_stmt|;
comment|/**    * Event requesting the localization of the rsrc.    * @param c    * @param rsrc    */
DECL|method|ContainerLocalizationRequestEvent (Container c, Map<LocalResourceVisibility, Collection<LocalResourceRequest>> rsrc)
specifier|public
name|ContainerLocalizationRequestEvent
parameter_list|(
name|Container
name|c
parameter_list|,
name|Map
argument_list|<
name|LocalResourceVisibility
argument_list|,
name|Collection
argument_list|<
name|LocalResourceRequest
argument_list|>
argument_list|>
name|rsrc
parameter_list|)
block|{
name|super
argument_list|(
name|LocalizationEventType
operator|.
name|INIT_CONTAINER_RESOURCES
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|this
operator|.
name|rsrc
operator|=
name|rsrc
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|LocalResourceVisibility
argument_list|,
name|Collection
argument_list|<
name|LocalResourceRequest
argument_list|>
argument_list|>
DECL|method|getRequestedResources ()
name|getRequestedResources
parameter_list|()
block|{
return|return
name|rsrc
return|;
block|}
block|}
end_class

end_unit

