begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb
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
name|applicationhistoryservice
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
name|ApplicationId
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
name|ApplicationIdPBImpl
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
name|ApplicationHistoryServerProtos
operator|.
name|ApplicationStartDataProto
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
name|ApplicationHistoryServerProtos
operator|.
name|ApplicationStartDataProtoOrBuilder
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
name|ApplicationIdProto
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationStartData
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
import|;
end_import

begin_class
DECL|class|ApplicationStartDataPBImpl
specifier|public
class|class
name|ApplicationStartDataPBImpl
extends|extends
name|ApplicationStartData
block|{
DECL|field|proto
name|ApplicationStartDataProto
name|proto
init|=
name|ApplicationStartDataProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|ApplicationStartDataProto
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
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|method|ApplicationStartDataPBImpl ()
specifier|public
name|ApplicationStartDataPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ApplicationStartDataProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ApplicationStartDataPBImpl (ApplicationStartDataProto proto)
specifier|public
name|ApplicationStartDataPBImpl
parameter_list|(
name|ApplicationStartDataProto
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
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|applicationId
return|;
block|}
name|ApplicationStartDataProtoOrBuilder
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
name|hasApplicationId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|applicationId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|applicationId
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationId (ApplicationId applicationId)
specifier|public
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationId
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplicationId
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|applicationId
operator|=
name|applicationId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationName ()
specifier|public
name|String
name|getApplicationName
parameter_list|()
block|{
name|ApplicationStartDataProtoOrBuilder
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
name|hasApplicationName
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getApplicationName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationName (String applicationName)
specifier|public
name|void
name|setApplicationName
parameter_list|(
name|String
name|applicationName
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationName
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplicationName
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setApplicationName
argument_list|(
name|applicationName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationType ()
specifier|public
name|String
name|getApplicationType
parameter_list|()
block|{
name|ApplicationStartDataProtoOrBuilder
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
name|hasApplicationType
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getApplicationType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationType (String applicationType)
specifier|public
name|void
name|setApplicationType
parameter_list|(
name|String
name|applicationType
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationType
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearApplicationType
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setApplicationType
argument_list|(
name|applicationType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
name|ApplicationStartDataProtoOrBuilder
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
name|hasUser
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getUser
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearUser
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
name|ApplicationStartDataProtoOrBuilder
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
name|hasQueue
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|p
operator|.
name|getQueue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearQueue
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubmitTime ()
specifier|public
name|long
name|getSubmitTime
parameter_list|()
block|{
name|ApplicationStartDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getSubmitTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setSubmitTime (long submitTime)
specifier|public
name|void
name|setSubmitTime
parameter_list|(
name|long
name|submitTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setSubmitTime
argument_list|(
name|submitTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
name|ApplicationStartDataProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getStartTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setStartTime (long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|ApplicationStartDataProto
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
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
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
name|applicationId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ApplicationIdPBImpl
operator|)
name|this
operator|.
name|applicationId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setApplicationId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|applicationId
argument_list|)
argument_list|)
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
name|ApplicationStartDataProto
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
DECL|method|convertToProtoFormat (ApplicationId applicationId)
specifier|private
name|ApplicationIdProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationIdPBImpl
operator|)
name|applicationId
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat ( ApplicationIdProto applicationId)
specifier|private
name|ApplicationIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationIdProto
name|applicationId
parameter_list|)
block|{
return|return
operator|new
name|ApplicationIdPBImpl
argument_list|(
name|applicationId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

