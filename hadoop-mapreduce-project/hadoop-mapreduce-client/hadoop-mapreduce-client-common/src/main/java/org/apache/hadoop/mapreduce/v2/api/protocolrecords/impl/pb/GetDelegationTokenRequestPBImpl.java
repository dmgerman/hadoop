begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**    * Licensed to the Apache Software Foundation (ASF) under one    * or more contributor license agreements.  See the NOTICE file    * distributed with this work for additional information    * regarding copyright ownership.  The ASF licenses this file    * to you under the Apache License, Version 2.0 (the    * "License"); you may not use this file except in compliance    * with the License.  You may obtain a copy of the License at    *    *     http://www.apache.org/licenses/LICENSE-2.0    *    * Unless required by applicable law or agreed to in writing, software    * distributed under the License is distributed on an "AS IS" BASIS,    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    * See the License for the specific language governing permissions and    * limitations under the License.    */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.protocolrecords.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetDelegationTokenRequest
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
name|GetDelegationTokenRequestProto
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
name|GetDelegationTokenRequestProtoOrBuilder
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

begin_class
DECL|class|GetDelegationTokenRequestPBImpl
specifier|public
class|class
name|GetDelegationTokenRequestPBImpl
extends|extends
name|ProtoBase
argument_list|<
name|GetDelegationTokenRequestProto
argument_list|>
implements|implements
name|GetDelegationTokenRequest
block|{
DECL|field|renewer
name|String
name|renewer
decl_stmt|;
DECL|field|proto
name|GetDelegationTokenRequestProto
name|proto
init|=
name|GetDelegationTokenRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetDelegationTokenRequestProto
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
DECL|method|GetDelegationTokenRequestPBImpl ()
specifier|public
name|GetDelegationTokenRequestPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetDelegationTokenRequestProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetDelegationTokenRequestPBImpl ( GetDelegationTokenRequestProto proto)
specifier|public
name|GetDelegationTokenRequestPBImpl
parameter_list|(
name|GetDelegationTokenRequestProto
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
DECL|method|getRenewer ()
specifier|public
name|String
name|getRenewer
parameter_list|()
block|{
name|GetDelegationTokenRequestProtoOrBuilder
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
name|renewer
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|renewer
return|;
block|}
name|this
operator|.
name|renewer
operator|=
name|p
operator|.
name|getRenewer
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|renewer
return|;
block|}
annotation|@
name|Override
DECL|method|setRenewer (String renewer)
specifier|public
name|void
name|setRenewer
parameter_list|(
name|String
name|renewer
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|renewer
operator|==
literal|null
condition|)
name|builder
operator|.
name|clearRenewer
argument_list|()
expr_stmt|;
name|this
operator|.
name|renewer
operator|=
name|renewer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|GetDelegationTokenRequestProto
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
name|renewer
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setRenewer
argument_list|(
name|this
operator|.
name|renewer
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
name|GetDelegationTokenRequestProto
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
block|}
end_class

end_unit

