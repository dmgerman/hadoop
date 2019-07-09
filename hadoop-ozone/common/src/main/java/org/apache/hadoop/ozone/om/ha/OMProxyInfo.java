begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ha
package|package
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
name|io
operator|.
name|Text
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
name|net
operator|.
name|NetUtils
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
name|SecurityUtil
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
comment|/**  * Class to store OM proxy information.  */
end_comment

begin_class
DECL|class|OMProxyInfo
specifier|public
class|class
name|OMProxyInfo
block|{
DECL|field|nodeId
specifier|private
name|String
name|nodeId
decl_stmt|;
DECL|field|rpcAddrStr
specifier|private
name|String
name|rpcAddrStr
decl_stmt|;
DECL|field|rpcAddr
specifier|private
name|InetSocketAddress
name|rpcAddr
decl_stmt|;
DECL|field|dtService
specifier|private
name|Text
name|dtService
decl_stmt|;
DECL|method|OMProxyInfo (String nodeID, String rpcAddress)
name|OMProxyInfo
parameter_list|(
name|String
name|nodeID
parameter_list|,
name|String
name|rpcAddress
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeID
expr_stmt|;
name|this
operator|.
name|rpcAddrStr
operator|=
name|rpcAddress
expr_stmt|;
name|this
operator|.
name|rpcAddr
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|rpcAddrStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|dtService
operator|=
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|rpcAddr
argument_list|)
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"nodeId="
argument_list|)
operator|.
name|append
argument_list|(
name|nodeId
argument_list|)
operator|.
name|append
argument_list|(
literal|",nodeAddress="
argument_list|)
operator|.
name|append
argument_list|(
name|rpcAddrStr
argument_list|)
decl_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|rpcAddr
return|;
block|}
DECL|method|getDelegationTokenService ()
specifier|public
name|Text
name|getDelegationTokenService
parameter_list|()
block|{
return|return
name|dtService
return|;
block|}
block|}
end_class

end_unit

