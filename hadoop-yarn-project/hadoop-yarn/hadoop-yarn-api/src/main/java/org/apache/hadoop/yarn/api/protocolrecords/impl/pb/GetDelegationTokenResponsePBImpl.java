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
name|security
operator|.
name|proto
operator|.
name|SecurityProtos
operator|.
name|GetDelegationTokenResponseProto
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
name|security
operator|.
name|proto
operator|.
name|SecurityProtos
operator|.
name|GetDelegationTokenResponseProtoOrBuilder
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
name|security
operator|.
name|proto
operator|.
name|SecurityProtos
operator|.
name|TokenProto
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
name|GetDelegationTokenResponse
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
name|api
operator|.
name|records
operator|.
name|Token
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
name|TokenPBImpl
import|;
end_import

begin_class
DECL|class|GetDelegationTokenResponsePBImpl
specifier|public
class|class
name|GetDelegationTokenResponsePBImpl
extends|extends
name|ProtoBase
argument_list|<
name|GetDelegationTokenResponseProto
argument_list|>
implements|implements
name|GetDelegationTokenResponse
block|{
DECL|field|appToken
name|Token
name|appToken
decl_stmt|;
DECL|field|proto
name|GetDelegationTokenResponseProto
name|proto
init|=
name|GetDelegationTokenResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetDelegationTokenResponseProto
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
DECL|method|GetDelegationTokenResponsePBImpl ()
specifier|public
name|GetDelegationTokenResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetDelegationTokenResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetDelegationTokenResponsePBImpl ( GetDelegationTokenResponseProto proto)
specifier|public
name|GetDelegationTokenResponsePBImpl
parameter_list|(
name|GetDelegationTokenResponseProto
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
DECL|method|getRMDelegationToken ()
specifier|public
name|Token
name|getRMDelegationToken
parameter_list|()
block|{
name|GetDelegationTokenResponseProtoOrBuilder
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
name|appToken
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|appToken
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|hasToken
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|this
operator|.
name|appToken
operator|=
name|convertFromProtoFormat
argument_list|(
name|p
operator|.
name|getToken
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|appToken
return|;
block|}
annotation|@
name|Override
DECL|method|setRMDelegationToken (Token appToken)
specifier|public
name|void
name|setRMDelegationToken
parameter_list|(
name|Token
name|appToken
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|appToken
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearToken
argument_list|()
expr_stmt|;
name|this
operator|.
name|appToken
operator|=
name|appToken
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|GetDelegationTokenResponseProto
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
name|appToken
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setToken
argument_list|(
name|convertToProtoFormat
argument_list|(
name|this
operator|.
name|appToken
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
name|GetDelegationTokenResponseProto
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
DECL|method|convertFromProtoFormat (TokenProto p)
specifier|private
name|TokenPBImpl
name|convertFromProtoFormat
parameter_list|(
name|TokenProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|TokenPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (Token t)
specifier|private
name|TokenProto
name|convertToProtoFormat
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TokenPBImpl
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

