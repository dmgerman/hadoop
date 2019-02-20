begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.rpc.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
operator|.
name|rpc
operator|.
name|ha
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
name|ozone
operator|.
name|om
operator|.
name|protocolPB
operator|.
name|OzoneManagerProtocolClientSideTranslatorPB
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_comment
comment|/**  * Proxy information of OM.  */
end_comment

begin_class
DECL|class|OMProxyInfo
specifier|public
specifier|final
class|class
name|OMProxyInfo
block|{
DECL|field|address
specifier|private
name|InetSocketAddress
name|address
decl_stmt|;
DECL|field|omClient
specifier|private
name|OzoneManagerProtocolClientSideTranslatorPB
name|omClient
decl_stmt|;
DECL|method|OMProxyInfo (InetSocketAddress addr)
specifier|public
name|OMProxyInfo
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|)
block|{
name|this
operator|.
name|address
operator|=
name|addr
expr_stmt|;
block|}
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|address
return|;
block|}
DECL|method|getOMProxy ()
specifier|public
name|OzoneManagerProtocolClientSideTranslatorPB
name|getOMProxy
parameter_list|()
block|{
return|return
name|omClient
return|;
block|}
DECL|method|setOMProxy ( OzoneManagerProtocolClientSideTranslatorPB clientProxy)
specifier|public
name|void
name|setOMProxy
parameter_list|(
name|OzoneManagerProtocolClientSideTranslatorPB
name|clientProxy
parameter_list|)
block|{
name|this
operator|.
name|omClient
operator|=
name|clientProxy
expr_stmt|;
block|}
block|}
end_class

end_unit

