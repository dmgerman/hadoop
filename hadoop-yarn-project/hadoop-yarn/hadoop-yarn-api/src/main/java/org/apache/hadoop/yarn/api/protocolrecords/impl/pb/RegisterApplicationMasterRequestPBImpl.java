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
name|RegisterApplicationMasterRequest
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
name|ApplicationAttemptId
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
name|ApplicationAttemptIdPBImpl
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
name|ApplicationAttemptIdProto
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
name|RegisterApplicationMasterRequestProto
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
name|RegisterApplicationMasterRequestProtoOrBuilder
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|RegisterApplicationMasterRequestPBImpl
specifier|public
class|class
name|RegisterApplicationMasterRequestPBImpl
extends|extends
name|RegisterApplicationMasterRequest
block|{
DECL|field|proto
name|RegisterApplicationMasterRequestProto
name|proto
init|=
name|RegisterApplicationMasterRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|RegisterApplicationMasterRequestProto
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
DECL|field|applicationAttemptId
specifier|private
name|ApplicationAttemptId
name|applicationAttemptId
init|=
literal|null
decl_stmt|;
DECL|method|RegisterApplicationMasterRequestPBImpl ()
specifier|public
name|RegisterApplicationMasterRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|RegisterApplicationMasterRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|RegisterApplicationMasterRequestPBImpl (RegisterApplicationMasterRequestProto proto)
specifier|public
name|RegisterApplicationMasterRequestPBImpl
parameter_list|(
name|RegisterApplicationMasterRequestProto
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
name|RegisterApplicationMasterRequestProto
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
name|applicationAttemptId
operator|!=
literal|null
operator|&&
operator|!
operator|(
operator|(
name|ApplicationAttemptIdPBImpl
operator|)
name|this
operator|.
name|applicationAttemptId
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setApplicationAttemptId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|applicationAttemptId
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
name|RegisterApplicationMasterRequestProto
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
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
name|RegisterApplicationMasterRequestProtoOrBuilder
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
name|this
operator|.
name|applicationAttemptId
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|applicationAttemptId
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasApplicationAttemptId
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|applicationAttemptId
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|applicationAttemptId
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationAttemptId (ApplicationAttemptId applicationMaster)
specifier|public
name|void
name|setApplicationAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationMaster
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|applicationMaster
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearApplicationAttemptId
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|applicationMaster
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHost ()
specifier|public
name|String
name|getHost
parameter_list|()
block|{
name|RegisterApplicationMasterRequestProtoOrBuilder
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
name|getHost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setHost (String host)
specifier|public
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRpcPort ()
specifier|public
name|int
name|getRpcPort
parameter_list|()
block|{
name|RegisterApplicationMasterRequestProtoOrBuilder
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
name|getRpcPort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setRpcPort (int port)
specifier|public
name|void
name|setRpcPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setRpcPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
name|RegisterApplicationMasterRequestProtoOrBuilder
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
name|getTrackingUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setTrackingUrl (String url)
specifier|public
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setTrackingUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ApplicationAttemptIdProto p)
specifier|private
name|ApplicationAttemptIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationAttemptIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ApplicationAttemptIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ApplicationAttemptId t)
specifier|private
name|ApplicationAttemptIdProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationAttemptId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationAttemptIdPBImpl
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

