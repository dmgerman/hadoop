begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
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
package|;
end_package

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
name|Preconditions
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|client
operator|.
name|protocol
operator|.
name|ClientProtocol
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
name|ozone
operator|.
name|client
operator|.
name|rest
operator|.
name|RestClient
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
name|ozone
operator|.
name|client
operator|.
name|rpc
operator|.
name|RpcClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
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
name|OzoneConfigKeys
operator|.
name|OZONE_CLIENT_PROTOCOL
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
name|OZONE_KSM_HTTP_ADDRESS_KEY
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
name|OZONE_KSM_HTTP_BIND_PORT_DEFAULT
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
name|OZONE_KSM_PORT_DEFAULT
import|;
end_import

begin_comment
comment|/**  * Factory class to create different types of OzoneClients.  * Based on<code>ozone.client.protocol</code>, it decides which  * protocol to use for the communication.  * Default value is  *<code>org.apache.hadoop.ozone.client.rpc.RpcClient</code>.<br>  * OzoneClientFactory constructs a proxy using  * {@link OzoneClientInvocationHandler}  * and creates OzoneClient instance with it.  * {@link OzoneClientInvocationHandler} dispatches the call to  * underlying {@link ClientProtocol} implementation.  */
end_comment

begin_class
DECL|class|OzoneClientFactory
specifier|public
specifier|final
class|class
name|OzoneClientFactory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OzoneClientFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Private constructor, class is not meant to be initialized.    */
DECL|method|OzoneClientFactory ()
specifier|private
name|OzoneClientFactory
parameter_list|()
block|{}
comment|/**    * Constructs and return an OzoneClient with default configuration.    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getClient ()
specifier|public
specifier|static
name|OzoneClient
name|getClient
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating OzoneClient with default configuration."
argument_list|)
expr_stmt|;
return|return
name|getClient
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Constructs and return an OzoneClient based on the configuration object.    * Protocol type is decided by<code>ozone.client.protocol</code>.    *    * @param config    *        Configuration to be used for OzoneClient creation    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getClient (Configuration config)
specifier|public
specifier|static
name|OzoneClient
name|getClient
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|ClientProtocol
argument_list|>
name|clazz
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|ClientProtocol
argument_list|>
operator|)
name|config
operator|.
name|getClass
argument_list|(
name|OZONE_CLIENT_PROTOCOL
argument_list|,
name|RpcClient
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|getClient
argument_list|(
name|getClientProtocol
argument_list|(
name|clazz
argument_list|,
name|config
argument_list|)
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    * Returns an OzoneClient which will use RPC protocol.    *    * @param ksmHost    *        hostname of KeySpaceManager to connect.    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getRpcClient (String ksmHost)
specifier|public
specifier|static
name|OzoneClient
name|getRpcClient
parameter_list|(
name|String
name|ksmHost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRpcClient
argument_list|(
name|ksmHost
argument_list|,
name|OZONE_KSM_PORT_DEFAULT
argument_list|,
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns an OzoneClient which will use RPC protocol.    *    * @param ksmHost    *        hostname of KeySpaceManager to connect.    *    * @param ksmRpcPort    *        RPC port of KeySpaceManager.    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getRpcClient (String ksmHost, Integer ksmRpcPort)
specifier|public
specifier|static
name|OzoneClient
name|getRpcClient
parameter_list|(
name|String
name|ksmHost
parameter_list|,
name|Integer
name|ksmRpcPort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRpcClient
argument_list|(
name|ksmHost
argument_list|,
name|ksmRpcPort
argument_list|,
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns an OzoneClient which will use RPC protocol.    *    * @param ksmHost    *        hostname of KeySpaceManager to connect.    *    * @param ksmRpcPort    *        RPC port of KeySpaceManager.    *    * @param config    *        Configuration to be used for OzoneClient creation    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getRpcClient (String ksmHost, Integer ksmRpcPort, Configuration config)
specifier|public
specifier|static
name|OzoneClient
name|getRpcClient
parameter_list|(
name|String
name|ksmHost
parameter_list|,
name|Integer
name|ksmRpcPort
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ksmHost
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ksmRpcPort
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|OZONE_KSM_ADDRESS_KEY
argument_list|,
name|ksmHost
operator|+
literal|":"
operator|+
name|ksmRpcPort
argument_list|)
expr_stmt|;
return|return
name|getRpcClient
argument_list|(
name|config
argument_list|)
return|;
block|}
comment|/**    * Returns an OzoneClient which will use RPC protocol.    *    * @param config    *        used for OzoneClient creation    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getRpcClient (Configuration config)
specifier|public
specifier|static
name|OzoneClient
name|getRpcClient
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|getClient
argument_list|(
name|getClientProtocol
argument_list|(
name|RpcClient
operator|.
name|class
argument_list|,
name|config
argument_list|)
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    * Returns an OzoneClient which will use REST protocol.    *    * @param ksmHost    *        hostname of KeySpaceManager to connect.    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getRestClient (String ksmHost)
specifier|public
specifier|static
name|OzoneClient
name|getRestClient
parameter_list|(
name|String
name|ksmHost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRestClient
argument_list|(
name|ksmHost
argument_list|,
name|OZONE_KSM_HTTP_BIND_PORT_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * Returns an OzoneClient which will use REST protocol.    *    * @param ksmHost    *        hostname of KeySpaceManager to connect.    *    * @param ksmHttpPort    *        HTTP port of KeySpaceManager.    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getRestClient (String ksmHost, Integer ksmHttpPort)
specifier|public
specifier|static
name|OzoneClient
name|getRestClient
parameter_list|(
name|String
name|ksmHost
parameter_list|,
name|Integer
name|ksmHttpPort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRestClient
argument_list|(
name|ksmHost
argument_list|,
name|ksmHttpPort
argument_list|,
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns an OzoneClient which will use REST protocol.    *    * @param ksmHost    *        hostname of KeySpaceManager to connect.    *    * @param ksmHttpPort    *        HTTP port of KeySpaceManager.    *    * @param config    *        Configuration to be used for OzoneClient creation    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getRestClient (String ksmHost, Integer ksmHttpPort, Configuration config)
specifier|public
specifier|static
name|OzoneClient
name|getRestClient
parameter_list|(
name|String
name|ksmHost
parameter_list|,
name|Integer
name|ksmHttpPort
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ksmHost
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ksmHttpPort
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|OZONE_KSM_HTTP_ADDRESS_KEY
argument_list|,
name|ksmHost
operator|+
literal|":"
operator|+
name|ksmHttpPort
argument_list|)
expr_stmt|;
return|return
name|getRestClient
argument_list|(
name|config
argument_list|)
return|;
block|}
comment|/**    * Returns an OzoneClient which will use REST protocol.    *    * @param config    *        Configuration to be used for OzoneClient creation    *    * @return OzoneClient    *    * @throws IOException    */
DECL|method|getRestClient (Configuration config)
specifier|public
specifier|static
name|OzoneClient
name|getRestClient
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|getClient
argument_list|(
name|getClientProtocol
argument_list|(
name|RestClient
operator|.
name|class
argument_list|,
name|config
argument_list|)
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    * Creates OzoneClient with the given ClientProtocol and Configuration.    *    * @param clientProtocol    *        Protocol to be used by the OzoneClient    *    * @param config    *        Configuration to be used for OzoneClient creation    */
DECL|method|getClient (ClientProtocol clientProtocol, Configuration config)
specifier|private
specifier|static
name|OzoneClient
name|getClient
parameter_list|(
name|ClientProtocol
name|clientProtocol
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|OzoneClientInvocationHandler
name|clientHandler
init|=
operator|new
name|OzoneClientInvocationHandler
argument_list|(
name|clientProtocol
argument_list|)
decl_stmt|;
name|ClientProtocol
name|proxy
init|=
operator|(
name|ClientProtocol
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|OzoneClientInvocationHandler
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|ClientProtocol
operator|.
name|class
block|}
operator|,
name|clientHandler
block|)
function|;
return|return
operator|new
name|OzoneClient
argument_list|(
name|config
argument_list|,
name|proxy
argument_list|)
return|;
block|}
end_class

begin_comment
comment|/**    * Returns an instance of Protocol class.    *    * @param protocolClass    *        Class object of the ClientProtocol.    *    * @param config    *        Configuration used to initialize ClientProtocol.    *    * @return ClientProtocol    *    * @throws IOException    */
end_comment

begin_function
DECL|method|getClientProtocol ( Class<? extends ClientProtocol> protocolClass, Configuration config)
specifier|private
specifier|static
name|ClientProtocol
name|getClientProtocol
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|ClientProtocol
argument_list|>
name|protocolClass
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using {} as client protocol."
argument_list|,
name|protocolClass
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|ClientProtocol
argument_list|>
name|ctor
init|=
name|protocolClass
operator|.
name|getConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|ctor
operator|.
name|newInstance
argument_list|(
name|config
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Couldn't create protocol "
operator|+
name|protocolClass
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
operator|+
literal|" exception:"
operator|+
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|InvocationTargetException
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|,
operator|(
operator|(
name|InvocationTargetException
operator|)
name|e
operator|)
operator|.
name|getTargetException
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_function

unit|}
end_unit

