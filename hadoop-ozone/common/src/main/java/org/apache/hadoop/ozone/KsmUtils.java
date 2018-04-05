begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
package|;
end_package

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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
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
name|hdds
operator|.
name|HddsUtils
operator|.
name|getHostNameFromConfigKeys
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
name|hdds
operator|.
name|HddsUtils
operator|.
name|getPortNumberFromConfigKeys
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
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KSM_ADDRESS_KEY
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
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KSM_BIND_HOST_DEFAULT
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
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KSM_PORT_DEFAULT
import|;
end_import

begin_comment
comment|/**  * Stateless helper functions for the server and client side of KSM  * communication.  */
end_comment

begin_class
DECL|class|KsmUtils
specifier|public
class|class
name|KsmUtils
block|{
DECL|method|KsmUtils ()
specifier|private
name|KsmUtils
parameter_list|()
block|{   }
comment|/**    * Retrieve the socket address that is used by KSM.    * @param conf    * @return Target InetSocketAddress for the SCM service endpoint.    */
DECL|method|getKsmAddress ( Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getKsmAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|host
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|OZONE_KSM_ADDRESS_KEY
argument_list|)
decl_stmt|;
comment|// If no port number is specified then we'll just try the defaultBindPort.
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|OZONE_KSM_ADDRESS_KEY
argument_list|)
decl_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
operator|.
name|or
argument_list|(
name|OZONE_KSM_BIND_HOST_DEFAULT
argument_list|)
operator|+
literal|":"
operator|+
name|port
operator|.
name|or
argument_list|(
name|OZONE_KSM_PORT_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Retrieve the socket address that should be used by clients to connect    * to KSM.    * @param conf    * @return Target InetSocketAddress for the KSM service endpoint.    */
DECL|method|getKsmAddressForClients ( Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getKsmAddressForClients
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|host
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|OZONE_KSM_ADDRESS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|host
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|OZONE_KSM_ADDRESS_KEY
operator|+
literal|" must be defined. See"
operator|+
literal|" https://wiki.apache.org/hadoop/Ozone#Configuration for"
operator|+
literal|" details on configuring Ozone."
argument_list|)
throw|;
block|}
comment|// If no port number is specified then we'll just try the defaultBindPort.
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|OZONE_KSM_ADDRESS_KEY
argument_list|)
decl_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
operator|.
name|get
argument_list|()
operator|+
literal|":"
operator|+
name|port
operator|.
name|or
argument_list|(
name|OZONE_KSM_PORT_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

