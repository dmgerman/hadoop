begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|CancelDelegationTokenResponseProto
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
name|CancelDelegationTokenResponse
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
DECL|class|CancelDelegationTokenResponsePBImpl
specifier|public
class|class
name|CancelDelegationTokenResponsePBImpl
extends|extends
name|ProtoBase
argument_list|<
name|CancelDelegationTokenResponseProto
argument_list|>
implements|implements
name|CancelDelegationTokenResponse
block|{
DECL|field|proto
name|CancelDelegationTokenResponseProto
name|proto
init|=
name|CancelDelegationTokenResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|method|CancelDelegationTokenResponsePBImpl ()
specifier|public
name|CancelDelegationTokenResponsePBImpl
parameter_list|()
block|{   }
DECL|method|CancelDelegationTokenResponsePBImpl ( CancelDelegationTokenResponseProto proto)
specifier|public
name|CancelDelegationTokenResponsePBImpl
parameter_list|(
name|CancelDelegationTokenResponseProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|CancelDelegationTokenResponseProto
name|getProto
parameter_list|()
block|{
return|return
name|proto
return|;
block|}
block|}
end_class

end_unit

