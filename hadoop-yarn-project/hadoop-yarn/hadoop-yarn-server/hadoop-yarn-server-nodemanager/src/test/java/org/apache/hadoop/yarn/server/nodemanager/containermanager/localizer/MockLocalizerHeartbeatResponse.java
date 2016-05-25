begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|api
operator|.
name|ResourceLocalizationSpec
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
name|api
operator|.
name|protocolrecords
operator|.
name|LocalizerAction
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
name|api
operator|.
name|protocolrecords
operator|.
name|LocalizerHeartbeatResponse
import|;
end_import

begin_class
DECL|class|MockLocalizerHeartbeatResponse
specifier|public
class|class
name|MockLocalizerHeartbeatResponse
implements|implements
name|LocalizerHeartbeatResponse
block|{
DECL|field|action
name|LocalizerAction
name|action
decl_stmt|;
DECL|field|resourceSpecs
name|List
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
name|resourceSpecs
decl_stmt|;
DECL|method|MockLocalizerHeartbeatResponse ( LocalizerAction action, List<ResourceLocalizationSpec> resources)
specifier|public
name|MockLocalizerHeartbeatResponse
parameter_list|(
name|LocalizerAction
name|action
parameter_list|,
name|List
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
name|resources
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|resourceSpecs
operator|=
name|resources
expr_stmt|;
block|}
DECL|method|getLocalizerAction ()
specifier|public
name|LocalizerAction
name|getLocalizerAction
parameter_list|()
block|{
return|return
name|action
return|;
block|}
DECL|method|setLocalizerAction (LocalizerAction action)
specifier|public
name|void
name|setLocalizerAction
parameter_list|(
name|LocalizerAction
name|action
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResourceSpecs ()
specifier|public
name|List
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
name|getResourceSpecs
parameter_list|()
block|{
return|return
name|resourceSpecs
return|;
block|}
annotation|@
name|Override
DECL|method|setResourceSpecs (List<ResourceLocalizationSpec> resourceSpecs)
specifier|public
name|void
name|setResourceSpecs
parameter_list|(
name|List
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
name|resourceSpecs
parameter_list|)
block|{
name|this
operator|.
name|resourceSpecs
operator|=
name|resourceSpecs
expr_stmt|;
block|}
block|}
end_class

end_unit

