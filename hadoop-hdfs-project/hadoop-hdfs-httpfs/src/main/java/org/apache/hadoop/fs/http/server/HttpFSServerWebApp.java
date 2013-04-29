begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
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
name|lib
operator|.
name|server
operator|.
name|ServerException
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
name|service
operator|.
name|FileSystemAccess
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
name|servlet
operator|.
name|ServerWebApp
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
name|wsrs
operator|.
name|UserProvider
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

begin_comment
comment|/**  * Bootstrap class that manages the initialization and destruction of the  * HttpFSServer server, it is a<code>javax.servlet.ServletContextListener  *</code> implementation that is wired in HttpFSServer's WAR  *<code>WEB-INF/web.xml</code>.  *<p/>  * It provides acces to the server context via the singleton {@link #get}.  *<p/>  * All the configuration is loaded from configuration properties prefixed  * with<code>httpfs.</code>.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HttpFSServerWebApp
specifier|public
class|class
name|HttpFSServerWebApp
extends|extends
name|ServerWebApp
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
name|HttpFSServerWebApp
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Server name and prefix for all configuration properties.    */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"httpfs"
decl_stmt|;
comment|/**    * Configuration property that defines HttpFSServer admin group.    */
DECL|field|CONF_ADMIN_GROUP
specifier|public
specifier|static
specifier|final
name|String
name|CONF_ADMIN_GROUP
init|=
literal|"admin.group"
decl_stmt|;
DECL|field|SERVER
specifier|private
specifier|static
name|HttpFSServerWebApp
name|SERVER
decl_stmt|;
DECL|field|adminGroup
specifier|private
name|String
name|adminGroup
decl_stmt|;
comment|/**    * Default constructor.    *    * @throws IOException thrown if the home/conf/log/temp directory paths    * could not be resolved.    */
DECL|method|HttpFSServerWebApp ()
specifier|public
name|HttpFSServerWebApp
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor used for testing purposes.    */
DECL|method|HttpFSServerWebApp (String homeDir, String configDir, String logDir, String tempDir, Configuration config)
specifier|public
name|HttpFSServerWebApp
parameter_list|(
name|String
name|homeDir
parameter_list|,
name|String
name|configDir
parameter_list|,
name|String
name|logDir
parameter_list|,
name|String
name|tempDir
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|homeDir
argument_list|,
name|configDir
argument_list|,
name|logDir
argument_list|,
name|tempDir
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor used for testing purposes.    */
DECL|method|HttpFSServerWebApp (String homeDir, Configuration config)
specifier|public
name|HttpFSServerWebApp
parameter_list|(
name|String
name|homeDir
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|homeDir
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the HttpFSServer server, loads configuration and required    * services.    *    * @throws ServerException thrown if HttpFSServer server could not be    * initialized.    */
annotation|@
name|Override
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServerException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|SERVER
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"HttpFSServer server already initialized"
argument_list|)
throw|;
block|}
name|SERVER
operator|=
name|this
expr_stmt|;
name|adminGroup
operator|=
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|getPrefixedName
argument_list|(
name|CONF_ADMIN_GROUP
argument_list|)
argument_list|,
literal|"admin"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connects to Namenode [{}]"
argument_list|,
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|FileSystemAccess
operator|.
name|class
argument_list|)
operator|.
name|getFileSystemConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|userPattern
init|=
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|UserProvider
operator|.
name|USER_PATTERN_KEY
argument_list|,
name|UserProvider
operator|.
name|USER_PATTERN_DEFAULT
argument_list|)
decl_stmt|;
name|UserProvider
operator|.
name|setUserPattern
argument_list|(
name|userPattern
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdowns all running services.    */
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|SERVER
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns HttpFSServer server singleton, configuration and services are    * accessible through it.    *    * @return the HttpFSServer server singleton.    */
DECL|method|get ()
specifier|public
specifier|static
name|HttpFSServerWebApp
name|get
parameter_list|()
block|{
return|return
name|SERVER
return|;
block|}
comment|/**    * Returns HttpFSServer admin group.    *    * @return httpfs admin group.    */
DECL|method|getAdminGroup ()
specifier|public
name|String
name|getAdminGroup
parameter_list|()
block|{
return|return
name|adminGroup
return|;
block|}
block|}
end_class

end_unit

