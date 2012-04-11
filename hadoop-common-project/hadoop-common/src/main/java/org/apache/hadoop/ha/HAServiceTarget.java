begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|ha
operator|.
name|protocolPB
operator|.
name|HAServiceProtocolClientSideTranslatorPB
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_comment
comment|/**  * Represents a target of the client side HA administration commands.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|HAServiceTarget
specifier|public
specifier|abstract
class|class
name|HAServiceTarget
block|{
DECL|field|HOST_SUBST_KEY
specifier|private
specifier|static
specifier|final
name|String
name|HOST_SUBST_KEY
init|=
literal|"host"
decl_stmt|;
DECL|field|PORT_SUBST_KEY
specifier|private
specifier|static
specifier|final
name|String
name|PORT_SUBST_KEY
init|=
literal|"port"
decl_stmt|;
DECL|field|ADDRESS_SUBST_KEY
specifier|private
specifier|static
specifier|final
name|String
name|ADDRESS_SUBST_KEY
init|=
literal|"address"
decl_stmt|;
comment|/**    * @return the IPC address of the target node.    */
DECL|method|getAddress ()
specifier|public
specifier|abstract
name|InetSocketAddress
name|getAddress
parameter_list|()
function_decl|;
comment|/**    * @return a Fencer implementation configured for this target node    */
DECL|method|getFencer ()
specifier|public
specifier|abstract
name|NodeFencer
name|getFencer
parameter_list|()
function_decl|;
comment|/**    * @throws BadFencingConfigurationException if the fencing configuration    * appears to be invalid. This is divorced from the above    * {@link #getFencer()} method so that the configuration can be checked    * during the pre-flight phase of failover.    */
DECL|method|checkFencingConfigured ()
specifier|public
specifier|abstract
name|void
name|checkFencingConfigured
parameter_list|()
throws|throws
name|BadFencingConfigurationException
function_decl|;
comment|/**    * @return a proxy to connect to the target HA Service.    */
DECL|method|getProxy (Configuration conf, int timeoutMs)
specifier|public
name|HAServiceProtocol
name|getProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|timeoutMs
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|confCopy
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Lower the timeout so we quickly fail to connect
name|confCopy
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|SocketFactory
name|factory
init|=
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|confCopy
argument_list|)
decl_stmt|;
return|return
operator|new
name|HAServiceProtocolClientSideTranslatorPB
argument_list|(
name|getAddress
argument_list|()
argument_list|,
name|confCopy
argument_list|,
name|factory
argument_list|,
name|timeoutMs
argument_list|)
return|;
block|}
DECL|method|getFencingParameters ()
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getFencingParameters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ret
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|addFencingParameters
argument_list|(
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**    * Hook to allow subclasses to add any parameters they would like to    * expose to fencing implementations/scripts. Fencing methods are free    * to use this map as they see fit -- notably, the shell script    * implementation takes each entry, prepends 'target_', substitutes    * '_' for '.', and adds it to the environment of the script.    *    * Subclass implementations should be sure to delegate to the superclass    * implementation as well as adding their own keys.    *    * @param ret map which can be mutated to pass parameters to the fencer    */
DECL|method|addFencingParameters (Map<String, String> ret)
specifier|protected
name|void
name|addFencingParameters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ret
parameter_list|)
block|{
name|ret
operator|.
name|put
argument_list|(
name|ADDRESS_SUBST_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|getAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|.
name|put
argument_list|(
name|HOST_SUBST_KEY
argument_list|,
name|getAddress
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|put
argument_list|(
name|PORT_SUBST_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|getAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return true if auto failover should be considered enabled    */
DECL|method|isAutoFailoverEnabled ()
specifier|public
name|boolean
name|isAutoFailoverEnabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

