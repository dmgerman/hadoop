begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.impl.pb
package|package
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|ResourceBlacklistRequest
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
name|YarnProtos
operator|.
name|ResourceBlacklistRequestProto
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
name|YarnProtos
operator|.
name|ResourceBlacklistRequestProtoOrBuilder
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ResourceBlacklistRequestPBImpl
specifier|public
class|class
name|ResourceBlacklistRequestPBImpl
extends|extends
name|ResourceBlacklistRequest
block|{
DECL|field|proto
name|ResourceBlacklistRequestProto
name|proto
init|=
literal|null
decl_stmt|;
DECL|field|builder
name|ResourceBlacklistRequestProto
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
DECL|field|blacklistAdditions
name|List
argument_list|<
name|String
argument_list|>
name|blacklistAdditions
init|=
literal|null
decl_stmt|;
DECL|field|blacklistRemovals
name|List
argument_list|<
name|String
argument_list|>
name|blacklistRemovals
init|=
literal|null
decl_stmt|;
DECL|method|ResourceBlacklistRequestPBImpl ()
specifier|public
name|ResourceBlacklistRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ResourceBlacklistRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ResourceBlacklistRequestPBImpl (ResourceBlacklistRequestProto proto)
specifier|public
name|ResourceBlacklistRequestPBImpl
parameter_list|(
name|ResourceBlacklistRequestProto
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
name|ResourceBlacklistRequestProto
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
name|ResourceBlacklistRequestProto
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
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
block|}
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
DECL|method|mergeLocalToBuilder ()
specifier|private
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|blacklistAdditions
operator|!=
literal|null
condition|)
block|{
name|addBlacklistAdditionsToProto
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|blacklistRemovals
operator|!=
literal|null
condition|)
block|{
name|addBlacklistRemovalsToProto
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addBlacklistAdditionsToProto ()
specifier|private
name|void
name|addBlacklistAdditionsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearBlacklistAdditions
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|blacklistAdditions
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|builder
operator|.
name|addAllBlacklistAdditions
argument_list|(
name|this
operator|.
name|blacklistAdditions
argument_list|)
expr_stmt|;
block|}
DECL|method|addBlacklistRemovalsToProto ()
specifier|private
name|void
name|addBlacklistRemovalsToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearBlacklistAdditions
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|blacklistRemovals
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|builder
operator|.
name|addAllBlacklistRemovals
argument_list|(
name|this
operator|.
name|blacklistRemovals
argument_list|)
expr_stmt|;
block|}
DECL|method|initBlacklistAdditions ()
specifier|private
name|void
name|initBlacklistAdditions
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|blacklistAdditions
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ResourceBlacklistRequestProtoOrBuilder
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
name|String
argument_list|>
name|list
init|=
name|p
operator|.
name|getBlacklistAdditionsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|blacklistAdditions
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|blacklistAdditions
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
DECL|method|initBlacklistRemovals ()
specifier|private
name|void
name|initBlacklistRemovals
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|blacklistRemovals
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|ResourceBlacklistRequestProtoOrBuilder
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
name|String
argument_list|>
name|list
init|=
name|p
operator|.
name|getBlacklistRemovalsList
argument_list|()
decl_stmt|;
name|this
operator|.
name|blacklistRemovals
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|blacklistRemovals
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBlacklistAdditions ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getBlacklistAdditions
parameter_list|()
block|{
name|initBlacklistAdditions
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|blacklistAdditions
return|;
block|}
annotation|@
name|Override
DECL|method|setBlacklistAdditions (List<String> resourceNames)
specifier|public
name|void
name|setBlacklistAdditions
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|resourceNames
parameter_list|)
block|{
if|if
condition|(
name|resourceNames
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|blacklistAdditions
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|blacklistAdditions
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
name|initBlacklistAdditions
argument_list|()
expr_stmt|;
name|this
operator|.
name|blacklistAdditions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|blacklistAdditions
operator|.
name|addAll
argument_list|(
name|resourceNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBlacklistRemovals ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getBlacklistRemovals
parameter_list|()
block|{
name|initBlacklistRemovals
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|blacklistRemovals
return|;
block|}
annotation|@
name|Override
DECL|method|setBlacklistRemovals (List<String> resourceNames)
specifier|public
name|void
name|setBlacklistRemovals
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|resourceNames
parameter_list|)
block|{
if|if
condition|(
name|resourceNames
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|blacklistRemovals
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|blacklistRemovals
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
name|initBlacklistRemovals
argument_list|()
expr_stmt|;
name|this
operator|.
name|blacklistRemovals
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|blacklistRemovals
operator|.
name|addAll
argument_list|(
name|resourceNames
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

