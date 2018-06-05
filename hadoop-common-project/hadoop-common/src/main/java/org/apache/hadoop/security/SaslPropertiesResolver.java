begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|Sasl
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
name|Configurable
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
name|security
operator|.
name|SaslRpcServer
operator|.
name|QualityOfProtection
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
name|util
operator|.
name|ReflectionUtils
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Provides SaslProperties to be used for a connection.  * The default implementation is to read the values from configuration.  * This class can be overridden to provide custom SaslProperties.   * The custom class can be specified via configuration.  *  */
end_comment

begin_class
DECL|class|SaslPropertiesResolver
specifier|public
class|class
name|SaslPropertiesResolver
implements|implements
name|Configurable
block|{
DECL|field|properties
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
comment|/**    * Returns an instance of SaslPropertiesResolver.    * Looks up the configuration to see if there is custom class specified.    * Constructs the instance by passing the configuration directly to the    * constructor to achieve thread safety using final fields.    * @param conf    * @return SaslPropertiesResolver    */
DECL|method|getInstance (Configuration conf)
specifier|public
specifier|static
name|SaslPropertiesResolver
name|getInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|SaslPropertiesResolver
argument_list|>
name|clazz
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_SASL_PROPS_RESOLVER_CLASS
argument_list|,
name|SaslPropertiesResolver
operator|.
name|class
argument_list|,
name|SaslPropertiesResolver
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|clazz
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|properties
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|String
index|[]
name|qop
init|=
name|conf
operator|.
name|getTrimmedStrings
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_RPC_PROTECTION
argument_list|,
name|QualityOfProtection
operator|.
name|AUTHENTICATION
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|qop
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|qop
index|[
name|i
index|]
operator|=
name|QualityOfProtection
operator|.
name|valueOf
argument_list|(
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|qop
index|[
name|i
index|]
argument_list|)
argument_list|)
operator|.
name|getSaslQop
argument_list|()
expr_stmt|;
block|}
name|properties
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|qop
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|SERVER_AUTH
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * The default Sasl Properties read from the configuration    * @return sasl Properties    */
DECL|method|getDefaultProperties ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDefaultProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
comment|/**    * Identify the Sasl Properties to be used for a connection with a  client.    * @param clientAddress client's address    * @return the sasl properties to be used for the connection.    */
DECL|method|getServerProperties (InetAddress clientAddress)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getServerProperties
parameter_list|(
name|InetAddress
name|clientAddress
parameter_list|)
block|{
return|return
name|properties
return|;
block|}
comment|/**    * Identify the Sasl Properties to be used for a connection with a  client.    * @param clientAddress  client's address    * @param ingressPort the port that the client is connecting    * @return the sasl properties to be used for the connection.    */
DECL|method|getServerProperties (InetAddress clientAddress, int ingressPort)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getServerProperties
parameter_list|(
name|InetAddress
name|clientAddress
parameter_list|,
name|int
name|ingressPort
parameter_list|)
block|{
return|return
name|properties
return|;
block|}
comment|/**    * Identify the Sasl Properties to be used for a connection with a server.    * @param serverAddress server's address    * @return the sasl properties to be used for the connection.    */
DECL|method|getClientProperties (InetAddress serverAddress)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getClientProperties
parameter_list|(
name|InetAddress
name|serverAddress
parameter_list|)
block|{
return|return
name|properties
return|;
block|}
comment|/**    * Identify the Sasl Properties to be used for a connection with a server.    * @param serverAddress server's address    * @param ingressPort the port that is used to connect to server    * @return the sasl properties to be used for the connection.    */
DECL|method|getClientProperties (InetAddress serverAddress, int ingressPort)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getClientProperties
parameter_list|(
name|InetAddress
name|serverAddress
parameter_list|,
name|int
name|ingressPort
parameter_list|)
block|{
return|return
name|properties
return|;
block|}
comment|/**    * A util function to retrieve specific additional sasl property from config.    * Used by subclasses to read sasl properties used by themselves.    * @param conf the configuration    * @param configKey the config key to look for    * @param defaultQOP the default QOP if the key is missing    * @return sasl property associated with the given key    */
DECL|method|getSaslProperties (Configuration conf, String configKey, QualityOfProtection defaultQOP)
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSaslProperties
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|configKey
parameter_list|,
name|QualityOfProtection
name|defaultQOP
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|saslProps
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
index|[]
name|qop
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|configKey
argument_list|,
name|defaultQOP
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|qop
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|qop
index|[
name|i
index|]
operator|=
name|QualityOfProtection
operator|.
name|valueOf
argument_list|(
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|qop
index|[
name|i
index|]
argument_list|)
argument_list|)
operator|.
name|getSaslQop
argument_list|()
expr_stmt|;
block|}
name|saslProps
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|QOP
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|qop
argument_list|)
argument_list|)
expr_stmt|;
name|saslProps
operator|.
name|put
argument_list|(
name|Sasl
operator|.
name|SERVER_AUTH
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|saslProps
return|;
block|}
block|}
end_class

end_unit

