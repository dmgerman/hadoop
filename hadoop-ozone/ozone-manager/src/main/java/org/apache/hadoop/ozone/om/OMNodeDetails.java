begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|http
operator|.
name|HttpConfig
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
name|java
operator|.
name|net
operator|.
name|InetAddress
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OM_RATIS_SNAPSHOT_BEFORE_DB_CHECKPOINT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OZONE_OM_DB_CHECKPOINT_HTTP_ENDPOINT
import|;
end_import

begin_comment
comment|/**  * This class stores OM node details.  */
end_comment

begin_class
DECL|class|OMNodeDetails
specifier|public
specifier|final
class|class
name|OMNodeDetails
block|{
DECL|field|omServiceId
specifier|private
name|String
name|omServiceId
decl_stmt|;
DECL|field|omNodeId
specifier|private
name|String
name|omNodeId
decl_stmt|;
DECL|field|rpcAddress
specifier|private
name|InetSocketAddress
name|rpcAddress
decl_stmt|;
DECL|field|rpcPort
specifier|private
name|int
name|rpcPort
decl_stmt|;
DECL|field|ratisPort
specifier|private
name|int
name|ratisPort
decl_stmt|;
DECL|field|httpAddress
specifier|private
name|String
name|httpAddress
decl_stmt|;
DECL|field|httpsAddress
specifier|private
name|String
name|httpsAddress
decl_stmt|;
comment|/**    * Constructs OMNodeDetails object.    */
DECL|method|OMNodeDetails (String serviceId, String nodeId, InetSocketAddress rpcAddr, int rpcPort, int ratisPort, String httpAddress, String httpsAddress)
specifier|private
name|OMNodeDetails
parameter_list|(
name|String
name|serviceId
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|InetSocketAddress
name|rpcAddr
parameter_list|,
name|int
name|rpcPort
parameter_list|,
name|int
name|ratisPort
parameter_list|,
name|String
name|httpAddress
parameter_list|,
name|String
name|httpsAddress
parameter_list|)
block|{
name|this
operator|.
name|omServiceId
operator|=
name|serviceId
expr_stmt|;
name|this
operator|.
name|omNodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|rpcAddress
operator|=
name|rpcAddr
expr_stmt|;
name|this
operator|.
name|rpcPort
operator|=
name|rpcPort
expr_stmt|;
name|this
operator|.
name|ratisPort
operator|=
name|ratisPort
expr_stmt|;
name|this
operator|.
name|httpAddress
operator|=
name|httpAddress
expr_stmt|;
name|this
operator|.
name|httpsAddress
operator|=
name|httpsAddress
expr_stmt|;
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
literal|"OMNodeDetails["
operator|+
literal|"omServiceId="
operator|+
name|omServiceId
operator|+
literal|", omNodeId="
operator|+
name|omNodeId
operator|+
literal|", rpcAddress="
operator|+
name|rpcAddress
operator|+
literal|", rpcPort="
operator|+
name|rpcPort
operator|+
literal|", ratisPort="
operator|+
name|ratisPort
operator|+
literal|", httpAddress="
operator|+
name|httpAddress
operator|+
literal|", httpsAddress="
operator|+
name|httpsAddress
operator|+
literal|"]"
return|;
block|}
comment|/**    * Builder class for OMNodeDetails.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|omServiceId
specifier|private
name|String
name|omServiceId
decl_stmt|;
DECL|field|omNodeId
specifier|private
name|String
name|omNodeId
decl_stmt|;
DECL|field|rpcAddress
specifier|private
name|InetSocketAddress
name|rpcAddress
decl_stmt|;
DECL|field|rpcPort
specifier|private
name|int
name|rpcPort
decl_stmt|;
DECL|field|ratisPort
specifier|private
name|int
name|ratisPort
decl_stmt|;
DECL|field|httpAddr
specifier|private
name|String
name|httpAddr
decl_stmt|;
DECL|field|httpsAddr
specifier|private
name|String
name|httpsAddr
decl_stmt|;
DECL|method|setRpcAddress (InetSocketAddress rpcAddr)
specifier|public
name|Builder
name|setRpcAddress
parameter_list|(
name|InetSocketAddress
name|rpcAddr
parameter_list|)
block|{
name|this
operator|.
name|rpcAddress
operator|=
name|rpcAddr
expr_stmt|;
name|this
operator|.
name|rpcPort
operator|=
name|rpcAddress
operator|.
name|getPort
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRatisPort (int port)
specifier|public
name|Builder
name|setRatisPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|ratisPort
operator|=
name|port
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOMServiceId (String serviceId)
specifier|public
name|Builder
name|setOMServiceId
parameter_list|(
name|String
name|serviceId
parameter_list|)
block|{
name|this
operator|.
name|omServiceId
operator|=
name|serviceId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOMNodeId (String nodeId)
specifier|public
name|Builder
name|setOMNodeId
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
name|this
operator|.
name|omNodeId
operator|=
name|nodeId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setHttpAddress (String httpAddress)
specifier|public
name|Builder
name|setHttpAddress
parameter_list|(
name|String
name|httpAddress
parameter_list|)
block|{
name|this
operator|.
name|httpAddr
operator|=
name|httpAddress
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setHttpsAddress (String httpsAddress)
specifier|public
name|Builder
name|setHttpsAddress
parameter_list|(
name|String
name|httpsAddress
parameter_list|)
block|{
name|this
operator|.
name|httpsAddr
operator|=
name|httpsAddress
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|OMNodeDetails
name|build
parameter_list|()
block|{
return|return
operator|new
name|OMNodeDetails
argument_list|(
name|omServiceId
argument_list|,
name|omNodeId
argument_list|,
name|rpcAddress
argument_list|,
name|rpcPort
argument_list|,
name|ratisPort
argument_list|,
name|httpAddr
argument_list|,
name|httpsAddr
argument_list|)
return|;
block|}
block|}
DECL|method|getOMServiceId ()
specifier|public
name|String
name|getOMServiceId
parameter_list|()
block|{
return|return
name|omServiceId
return|;
block|}
DECL|method|getOMNodeId ()
specifier|public
name|String
name|getOMNodeId
parameter_list|()
block|{
return|return
name|omNodeId
return|;
block|}
DECL|method|getRpcAddress ()
specifier|public
name|InetSocketAddress
name|getRpcAddress
parameter_list|()
block|{
return|return
name|rpcAddress
return|;
block|}
DECL|method|getAddress ()
specifier|public
name|InetAddress
name|getAddress
parameter_list|()
block|{
return|return
name|rpcAddress
operator|.
name|getAddress
argument_list|()
return|;
block|}
DECL|method|getRatisPort ()
specifier|public
name|int
name|getRatisPort
parameter_list|()
block|{
return|return
name|ratisPort
return|;
block|}
DECL|method|getRpcPort ()
specifier|public
name|int
name|getRpcPort
parameter_list|()
block|{
return|return
name|rpcPort
return|;
block|}
DECL|method|getRpcAddressString ()
specifier|public
name|String
name|getRpcAddressString
parameter_list|()
block|{
return|return
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|rpcAddress
argument_list|)
return|;
block|}
DECL|method|getOMDBCheckpointEnpointUrl (HttpConfig.Policy httpPolicy)
specifier|public
name|String
name|getOMDBCheckpointEnpointUrl
parameter_list|(
name|HttpConfig
operator|.
name|Policy
name|httpPolicy
parameter_list|)
block|{
if|if
condition|(
name|httpPolicy
operator|.
name|isHttpEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|httpAddress
argument_list|)
condition|)
block|{
return|return
literal|"http://"
operator|+
name|httpAddress
operator|+
name|OZONE_OM_DB_CHECKPOINT_HTTP_ENDPOINT
operator|+
literal|"?"
operator|+
name|OM_RATIS_SNAPSHOT_BEFORE_DB_CHECKPOINT
operator|+
literal|"=true"
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|httpsAddress
argument_list|)
condition|)
block|{
return|return
literal|"https://"
operator|+
name|httpsAddress
operator|+
name|OZONE_OM_DB_CHECKPOINT_HTTP_ENDPOINT
operator|+
literal|"?"
operator|+
name|OM_RATIS_SNAPSHOT_BEFORE_DB_CHECKPOINT
operator|+
literal|"=true"
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

