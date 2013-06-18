begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords.impl.pb
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
name|protocolrecords
operator|.
name|GetQueueUserAclsInfoResponse
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
name|QueueUserACLInfo
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
name|QueueUserACLInfoPBImpl
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
name|QueueUserACLInfoProto
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
name|YarnServiceProtos
operator|.
name|GetQueueUserAclsInfoResponseProto
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
name|YarnServiceProtos
operator|.
name|GetQueueUserAclsInfoResponseProtoOrBuilder
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|GetQueueUserAclsInfoResponsePBImpl
specifier|public
class|class
name|GetQueueUserAclsInfoResponsePBImpl
extends|extends
name|GetQueueUserAclsInfoResponse
block|{
DECL|field|queueUserAclsInfoList
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|queueUserAclsInfoList
decl_stmt|;
DECL|field|proto
name|GetQueueUserAclsInfoResponseProto
name|proto
init|=
name|GetQueueUserAclsInfoResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetQueueUserAclsInfoResponseProto
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
DECL|method|GetQueueUserAclsInfoResponsePBImpl ()
specifier|public
name|GetQueueUserAclsInfoResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetQueueUserAclsInfoResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetQueueUserAclsInfoResponsePBImpl ( GetQueueUserAclsInfoResponseProto proto)
specifier|public
name|GetQueueUserAclsInfoResponsePBImpl
parameter_list|(
name|GetQueueUserAclsInfoResponseProto
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
annotation|@
name|Override
DECL|method|getUserAclsInfoList ()
specifier|public
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getUserAclsInfoList
parameter_list|()
block|{
name|initLocalQueueUserAclsList
argument_list|()
expr_stmt|;
return|return
name|queueUserAclsInfoList
return|;
block|}
annotation|@
name|Override
DECL|method|setUserAclsInfoList (List<QueueUserACLInfo> queueUserAclsList)
specifier|public
name|void
name|setUserAclsInfoList
parameter_list|(
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|queueUserAclsList
parameter_list|)
block|{
if|if
condition|(
name|queueUserAclsList
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearQueueUserAcls
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|queueUserAclsInfoList
operator|=
name|queueUserAclsList
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|GetQueueUserAclsInfoResponseProto
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
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|getProto
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|", "
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
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
name|this
operator|.
name|queueUserAclsInfoList
operator|!=
literal|null
condition|)
block|{
name|addLocalQueueUserACLInfosToProto
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
name|GetQueueUserAclsInfoResponseProto
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
comment|// Once this is called. containerList will never be null - until a getProto
comment|// is called.
DECL|method|initLocalQueueUserAclsList ()
specifier|private
name|void
name|initLocalQueueUserAclsList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|queueUserAclsInfoList
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetQueueUserAclsInfoResponseProtoOrBuilder
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
name|QueueUserACLInfoProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getQueueUserAclsList
argument_list|()
decl_stmt|;
name|queueUserAclsInfoList
operator|=
operator|new
name|ArrayList
argument_list|<
name|QueueUserACLInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|QueueUserACLInfoProto
name|a
range|:
name|list
control|)
block|{
name|queueUserAclsInfoList
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addLocalQueueUserACLInfosToProto ()
specifier|private
name|void
name|addLocalQueueUserACLInfosToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearQueueUserAcls
argument_list|()
expr_stmt|;
if|if
condition|(
name|queueUserAclsInfoList
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|QueueUserACLInfoProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|QueueUserACLInfoProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|QueueUserACLInfoProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|QueueUserACLInfoProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|iter
init|=
name|queueUserAclsInfoList
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
name|QueueUserACLInfoProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
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
name|addAllQueueUserAcls
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (QueueUserACLInfoProto p)
specifier|private
name|QueueUserACLInfoPBImpl
name|convertFromProtoFormat
parameter_list|(
name|QueueUserACLInfoProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|QueueUserACLInfoPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (QueueUserACLInfo t)
specifier|private
name|QueueUserACLInfoProto
name|convertToProtoFormat
parameter_list|(
name|QueueUserACLInfo
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|QueueUserACLInfoPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

