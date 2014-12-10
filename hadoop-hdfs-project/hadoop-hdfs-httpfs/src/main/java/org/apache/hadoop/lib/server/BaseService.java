begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|server
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
name|lib
operator|.
name|util
operator|.
name|ConfigurationUtils
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

begin_comment
comment|/**  * Convenience class implementing the {@link Service} interface.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BaseService
specifier|public
specifier|abstract
class|class
name|BaseService
implements|implements
name|Service
block|{
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|serviceConfig
specifier|private
name|Configuration
name|serviceConfig
decl_stmt|;
comment|/**    * Service constructor.    *    * @param prefix service prefix.    */
DECL|method|BaseService (String prefix)
specifier|public
name|BaseService
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
comment|/**    * Initializes the service.    *<p>    * It collects all service properties (properties having the    *<code>#SERVER#.#SERVICE#.</code> prefix). The property names are then    * trimmed from the<code>#SERVER#.#SERVICE#.</code> prefix.    *<p>    * After collecting  the service properties it delegates to the    * {@link #init()} method.    *    * @param server the server initializing the service, give access to the    * server context.    *    * @throws ServiceException thrown if the service could not be initialized.    */
annotation|@
name|Override
DECL|method|init (Server server)
specifier|public
specifier|final
name|void
name|init
parameter_list|(
name|Server
name|server
parameter_list|)
throws|throws
name|ServiceException
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
name|String
name|servicePrefix
init|=
name|getPrefixedName
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|serviceConfig
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|ConfigurationUtils
operator|.
name|resolve
argument_list|(
name|server
operator|.
name|getConfig
argument_list|()
argument_list|)
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|servicePrefix
argument_list|)
condition|)
block|{
name|serviceConfig
operator|.
name|set
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|servicePrefix
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * Post initializes the service. This method is called by the    * {@link Server} after all services of the server have been initialized.    *<p>    * This method does a NOP.    *    * @throws ServiceException thrown if the service could not be    * post-initialized.    */
annotation|@
name|Override
DECL|method|postInit ()
specifier|public
name|void
name|postInit
parameter_list|()
throws|throws
name|ServiceException
block|{   }
comment|/**    * Destroy the services.  This method is called once, when the    * {@link Server} owning the service is being destroyed.    *<p>    * This method does a NOP.    */
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
comment|/**    * Returns the service dependencies of this service. The service will be    * instantiated only if all the service dependencies are already initialized.    *<p>    * This method returns an empty array (size 0)    *    * @return an empty array (size 0).    */
annotation|@
name|Override
DECL|method|getServiceDependencies ()
specifier|public
name|Class
index|[]
name|getServiceDependencies
parameter_list|()
block|{
return|return
operator|new
name|Class
index|[
literal|0
index|]
return|;
block|}
comment|/**    * Notification callback when the server changes its status.    *<p>    * This method returns an empty array (size 0)    *    * @param oldStatus old server status.    * @param newStatus new server status.    *    * @throws ServiceException thrown if the service could not process the status change.    */
annotation|@
name|Override
DECL|method|serverStatusChange (Server.Status oldStatus, Server.Status newStatus)
specifier|public
name|void
name|serverStatusChange
parameter_list|(
name|Server
operator|.
name|Status
name|oldStatus
parameter_list|,
name|Server
operator|.
name|Status
name|newStatus
parameter_list|)
throws|throws
name|ServiceException
block|{   }
comment|/**    * Returns the service prefix.    *    * @return the service prefix.    */
DECL|method|getPrefix ()
specifier|protected
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
comment|/**    * Returns the server owning the service.    *    * @return the server owning the service.    */
DECL|method|getServer ()
specifier|protected
name|Server
name|getServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
comment|/**    * Returns the full prefixed name of a service property.    *    * @param name of the property.    *    * @return prefixed name of the property.    */
DECL|method|getPrefixedName (String name)
specifier|protected
name|String
name|getPrefixedName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|server
operator|.
name|getPrefixedName
argument_list|(
name|prefix
operator|+
literal|"."
operator|+
name|name
argument_list|)
return|;
block|}
comment|/**    * Returns the service configuration properties. Property    * names are trimmed off from its prefix.    *<p>    * The sevice configuration properties are all properties    * with names starting with<code>#SERVER#.#SERVICE#.</code>    * in the server configuration.    *    * @return the service configuration properties with names    *         trimmed off from their<code>#SERVER#.#SERVICE#.</code>    *         prefix.    */
DECL|method|getServiceConfig ()
specifier|protected
name|Configuration
name|getServiceConfig
parameter_list|()
block|{
return|return
name|serviceConfig
return|;
block|}
comment|/**    * Initializes the server.    *<p>    * This method is called by {@link #init(Server)} after all service properties    * (properties prefixed with    *    * @throws ServiceException thrown if the service could not be initialized.    */
DECL|method|init ()
specifier|protected
specifier|abstract
name|void
name|init
parameter_list|()
throws|throws
name|ServiceException
function_decl|;
block|}
end_class

end_unit

