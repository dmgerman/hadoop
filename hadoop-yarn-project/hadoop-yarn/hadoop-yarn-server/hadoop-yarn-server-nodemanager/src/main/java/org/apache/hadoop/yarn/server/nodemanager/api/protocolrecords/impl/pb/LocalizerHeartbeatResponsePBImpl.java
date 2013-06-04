begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.impl.pb
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
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|ProtoBase
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
name|proto
operator|.
name|YarnServerNodemanagerServiceProtos
operator|.
name|LocalizerActionProto
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
name|proto
operator|.
name|YarnServerNodemanagerServiceProtos
operator|.
name|LocalizerHeartbeatResponseProto
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
name|proto
operator|.
name|YarnServerNodemanagerServiceProtos
operator|.
name|LocalizerHeartbeatResponseProtoOrBuilder
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
name|proto
operator|.
name|YarnServerNodemanagerServiceProtos
operator|.
name|ResourceLocalizationSpecProto
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
name|impl
operator|.
name|pb
operator|.
name|ResourceLocalizationSpecPBImpl
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
DECL|class|LocalizerHeartbeatResponsePBImpl
specifier|public
class|class
name|LocalizerHeartbeatResponsePBImpl
extends|extends
name|ProtoBase
argument_list|<
name|LocalizerHeartbeatResponseProto
argument_list|>
implements|implements
name|LocalizerHeartbeatResponse
block|{
DECL|field|proto
name|LocalizerHeartbeatResponseProto
name|proto
init|=
name|LocalizerHeartbeatResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|LocalizerHeartbeatResponseProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|field|resourceSpecs
specifier|private
name|List
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
name|resourceSpecs
decl_stmt|;
DECL|method|LocalizerHeartbeatResponsePBImpl ()
specifier|public
name|LocalizerHeartbeatResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|LocalizerHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|LocalizerHeartbeatResponsePBImpl ( LocalizerHeartbeatResponseProto proto)
specifier|public
name|LocalizerHeartbeatResponsePBImpl
parameter_list|(
name|LocalizerHeartbeatResponseProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|LocalizerHeartbeatResponseProto
name|getProto
parameter_list|()
block|{
name|mergeLocalToProto
argument_list|()
expr_stmt|;
name|proto
operator|=
name|viaProto
condition|?
name|proto
else|:
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
return|return
name|proto
return|;
block|}
DECL|method|mergeLocalToBuilder ()
specifier|private
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|resourceSpecs
operator|!=
literal|null
condition|)
block|{
name|addResourcesToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mergeLocalToProto ()
specifier|private
name|void
name|mergeLocalToProto
parameter_list|()
block|{
if|if
condition|(
name|viaProto
condition|)
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|mergeLocalToBuilder
argument_list|()
expr_stmt|;
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|maybeInitBuilder ()
specifier|private
name|void
name|maybeInitBuilder
parameter_list|()
block|{
if|if
condition|(
name|viaProto
operator|||
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
name|LocalizerHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
name|viaProto
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLocalizerAction ()
specifier|public
name|LocalizerAction
name|getLocalizerAction
parameter_list|()
block|{
name|LocalizerHeartbeatResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|hasAction
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getAction
argument_list|()
argument_list|)
return|;
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
name|initResources
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|resourceSpecs
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
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearAction
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setAction
argument_list|(
name|convertToProtoFormat
argument_list|(
name|action
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setResourceSpecs (List<ResourceLocalizationSpec> rsrcs)
specifier|public
name|void
name|setResourceSpecs
parameter_list|(
name|List
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
name|rsrcs
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|rsrcs
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearResources
argument_list|()
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|resourceSpecs
operator|=
name|rsrcs
expr_stmt|;
block|}
DECL|method|initResources ()
specifier|private
name|void
name|initResources
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|resourceSpecs
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|LocalizerHeartbeatResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|ResourceLocalizationSpecProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getResourcesList
argument_list|()
decl_stmt|;
name|this
operator|.
name|resourceSpecs
operator|=
operator|new
name|ArrayList
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ResourceLocalizationSpecProto
name|c
range|:
name|list
control|)
block|{
name|this
operator|.
name|resourceSpecs
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addResourcesToProto ()
specifier|private
name|void
name|addResourcesToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearResources
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|resourceSpecs
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|ResourceLocalizationSpecProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|ResourceLocalizationSpecProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ResourceLocalizationSpecProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ResourceLocalizationSpecProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
name|iter
init|=
name|resourceSpecs
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceLocalizationSpecProto
name|next
parameter_list|()
block|{
name|ResourceLocalizationSpec
name|resource
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|ResourceLocalizationSpecPBImpl
operator|)
name|resource
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|builder
operator|.
name|addAllResources
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat ( ResourceLocalizationSpecProto p)
specifier|private
name|ResourceLocalizationSpec
name|convertFromProtoFormat
parameter_list|(
name|ResourceLocalizationSpecProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ResourceLocalizationSpecPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (LocalizerAction a)
specifier|private
name|LocalizerActionProto
name|convertToProtoFormat
parameter_list|(
name|LocalizerAction
name|a
parameter_list|)
block|{
return|return
name|LocalizerActionProto
operator|.
name|valueOf
argument_list|(
name|a
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (LocalizerActionProto a)
specifier|private
name|LocalizerAction
name|convertFromProtoFormat
parameter_list|(
name|LocalizerActionProto
name|a
parameter_list|)
block|{
return|return
name|LocalizerAction
operator|.
name|valueOf
argument_list|(
name|a
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

