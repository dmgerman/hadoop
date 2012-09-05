begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|servlet
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
name|annotations
operator|.
name|VisibleForTesting
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
name|server
operator|.
name|Server
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
name|javax
operator|.
name|servlet
operator|.
name|ServletContextEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContextListener
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
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_comment
comment|/**  * {@link Server} subclass that implements<code>ServletContextListener</code>  * and uses its lifecycle to start and stop the server.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ServerWebApp
specifier|public
specifier|abstract
class|class
name|ServerWebApp
extends|extends
name|Server
implements|implements
name|ServletContextListener
block|{
DECL|field|HOME_DIR
specifier|private
specifier|static
specifier|final
name|String
name|HOME_DIR
init|=
literal|".home.dir"
decl_stmt|;
DECL|field|CONFIG_DIR
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_DIR
init|=
literal|".config.dir"
decl_stmt|;
DECL|field|LOG_DIR
specifier|private
specifier|static
specifier|final
name|String
name|LOG_DIR
init|=
literal|".log.dir"
decl_stmt|;
DECL|field|TEMP_DIR
specifier|private
specifier|static
specifier|final
name|String
name|TEMP_DIR
init|=
literal|".temp.dir"
decl_stmt|;
DECL|field|HTTP_HOSTNAME
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_HOSTNAME
init|=
literal|".http.hostname"
decl_stmt|;
DECL|field|HTTP_PORT
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_PORT
init|=
literal|".http.port"
decl_stmt|;
DECL|field|HOME_DIR_TL
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|String
argument_list|>
name|HOME_DIR_TL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|authority
specifier|private
name|InetSocketAddress
name|authority
decl_stmt|;
comment|/**    * Method for testing purposes.    */
DECL|method|setHomeDirForCurrentThread (String homeDir)
specifier|public
specifier|static
name|void
name|setHomeDirForCurrentThread
parameter_list|(
name|String
name|homeDir
parameter_list|)
block|{
name|HOME_DIR_TL
operator|.
name|set
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor for testing purposes.    */
DECL|method|ServerWebApp (String name, String homeDir, String configDir, String logDir, String tempDir, Configuration config)
specifier|protected
name|ServerWebApp
parameter_list|(
name|String
name|name
parameter_list|,
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
name|name
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
comment|/**    * Constructor for testing purposes.    */
DECL|method|ServerWebApp (String name, String homeDir, Configuration config)
specifier|protected
name|ServerWebApp
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|homeDir
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|homeDir
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor. Subclasses must have a default constructor specifying    * the server name.    *<p/>    * The server name is used to resolve the Java System properties that define    * the server home, config, log and temp directories.    *<p/>    * The home directory is looked in the Java System property    *<code>#SERVER_NAME#.home.dir</code>.    *<p/>    * The config directory is looked in the Java System property    *<code>#SERVER_NAME#.config.dir</code>, if not defined it resolves to    * the<code>#SERVER_HOME_DIR#/conf</code> directory.    *<p/>    * The log directory is looked in the Java System property    *<code>#SERVER_NAME#.log.dir</code>, if not defined it resolves to    * the<code>#SERVER_HOME_DIR#/log</code> directory.    *<p/>    * The temp directory is looked in the Java System property    *<code>#SERVER_NAME#.temp.dir</code>, if not defined it resolves to    * the<code>#SERVER_HOME_DIR#/temp</code> directory.    *    * @param name server name.    */
DECL|method|ServerWebApp (String name)
specifier|public
name|ServerWebApp
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|getHomeDir
argument_list|(
name|name
argument_list|)
argument_list|,
name|getDir
argument_list|(
name|name
argument_list|,
name|CONFIG_DIR
argument_list|,
name|getHomeDir
argument_list|(
name|name
argument_list|)
operator|+
literal|"/conf"
argument_list|)
argument_list|,
name|getDir
argument_list|(
name|name
argument_list|,
name|LOG_DIR
argument_list|,
name|getHomeDir
argument_list|(
name|name
argument_list|)
operator|+
literal|"/log"
argument_list|)
argument_list|,
name|getDir
argument_list|(
name|name
argument_list|,
name|TEMP_DIR
argument_list|,
name|getHomeDir
argument_list|(
name|name
argument_list|)
operator|+
literal|"/temp"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the server home directory.    *<p/>    * It is looked up in the Java System property    *<code>#SERVER_NAME#.home.dir</code>.    *    * @param name the server home directory.    *    * @return the server home directory.    */
DECL|method|getHomeDir (String name)
specifier|static
name|String
name|getHomeDir
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|homeDir
init|=
name|HOME_DIR_TL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|homeDir
operator|==
literal|null
condition|)
block|{
name|String
name|sysProp
init|=
name|name
operator|+
name|HOME_DIR
decl_stmt|;
name|homeDir
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|sysProp
argument_list|)
expr_stmt|;
if|if
condition|(
name|homeDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"System property [{0}] not defined"
argument_list|,
name|sysProp
argument_list|)
argument_list|)
throw|;
block|}
block|}
return|return
name|homeDir
return|;
block|}
comment|/**    * Convenience method that looks for Java System property defining a    * diretory and if not present defaults to the specified directory.    *    * @param name server name, used as prefix of the Java System property.    * @param dirType dir type, use as postfix of the Java System property.    * @param defaultDir the default directory to return if the Java System    * property<code>name + dirType</code> is not defined.    *    * @return the directory defined in the Java System property or the    *         the default directory if the Java System property is not defined.    */
DECL|method|getDir (String name, String dirType, String defaultDir)
specifier|static
name|String
name|getDir
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|dirType
parameter_list|,
name|String
name|defaultDir
parameter_list|)
block|{
name|String
name|sysProp
init|=
name|name
operator|+
name|dirType
decl_stmt|;
return|return
name|System
operator|.
name|getProperty
argument_list|(
name|sysProp
argument_list|,
name|defaultDir
argument_list|)
return|;
block|}
comment|/**    * Initializes the<code>ServletContextListener</code> which initializes    * the Server.    *    * @param event servelt context event.    */
annotation|@
name|Override
DECL|method|contextInitialized (ServletContextEvent event)
specifier|public
name|void
name|contextInitialized
parameter_list|(
name|ServletContextEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|init
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServerException
name|ex
parameter_list|)
block|{
name|event
operator|.
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"ERROR: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Resolves the host& port InetSocketAddress the web server is listening to.    *<p/>    * This implementation looks for the following 2 properties:    *<ul>    *<li>#SERVER_NAME#.http.hostname</li>    *<li>#SERVER_NAME#.http.port</li>    *</ul>    *    * @return the host& port InetSocketAddress the web server is listening to.    * @throws ServerException thrown if any of the above 2 properties is not defined.    */
DECL|method|resolveAuthority ()
specifier|protected
name|InetSocketAddress
name|resolveAuthority
parameter_list|()
throws|throws
name|ServerException
block|{
name|String
name|hostnameKey
init|=
name|getName
argument_list|()
operator|+
name|HTTP_HOSTNAME
decl_stmt|;
name|String
name|portKey
init|=
name|getName
argument_list|()
operator|+
name|HTTP_PORT
decl_stmt|;
name|String
name|host
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|hostnameKey
argument_list|)
decl_stmt|;
name|String
name|port
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|portKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServerException
argument_list|(
name|ServerException
operator|.
name|ERROR
operator|.
name|S13
argument_list|,
name|hostnameKey
argument_list|)
throw|;
block|}
if|if
condition|(
name|port
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServerException
argument_list|(
name|ServerException
operator|.
name|ERROR
operator|.
name|S13
argument_list|,
name|portKey
argument_list|)
throw|;
block|}
try|try
block|{
name|InetAddress
name|add
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|hostnameKey
argument_list|)
decl_stmt|;
name|int
name|portNum
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|port
argument_list|)
decl_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|add
argument_list|,
name|portNum
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServerException
argument_list|(
name|ServerException
operator|.
name|ERROR
operator|.
name|S14
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Destroys the<code>ServletContextListener</code> which destroys    * the Server.    *    * @param event servelt context event.    */
annotation|@
name|Override
DECL|method|contextDestroyed (ServletContextEvent event)
specifier|public
name|void
name|contextDestroyed
parameter_list|(
name|ServletContextEvent
name|event
parameter_list|)
block|{
name|destroy
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the hostname:port InetSocketAddress the webserver is listening to.    *    * @return the hostname:port InetSocketAddress the webserver is listening to.    */
DECL|method|getAuthority ()
specifier|public
name|InetSocketAddress
name|getAuthority
parameter_list|()
throws|throws
name|ServerException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|authority
operator|==
literal|null
condition|)
block|{
name|authority
operator|=
name|resolveAuthority
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|authority
return|;
block|}
comment|/**    * Sets an alternate hostname:port InetSocketAddress to use.    *<p/>    * For testing purposes.    *     * @param authority alterante authority.    */
annotation|@
name|VisibleForTesting
DECL|method|setAuthority (InetSocketAddress authority)
specifier|public
name|void
name|setAuthority
parameter_list|(
name|InetSocketAddress
name|authority
parameter_list|)
block|{
name|this
operator|.
name|authority
operator|=
name|authority
expr_stmt|;
block|}
block|}
end_class

end_unit

