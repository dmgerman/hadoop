begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.server.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|server
operator|.
name|services
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
name|commons
operator|.
name|lang
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
name|curator
operator|.
name|ensemble
operator|.
name|fixed
operator|.
name|FixedEnsembleProvider
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
name|FileUtil
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
name|service
operator|.
name|AbstractService
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryConstants
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|BindingInformation
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|RegistryBindingSource
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|RegistryInternalConstants
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|RegistrySecurity
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|ZookeeperConfigOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|ServerCnxnFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|ZooKeeperServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|persistence
operator|.
name|FileTxnSnapLog
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
name|File
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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

begin_comment
comment|/**  * This is a small, localhost Zookeeper service instance that is contained  * in a YARN service...it's been derived from Apache Twill.  *  * It implements {@link RegistryBindingSource} and provides binding information,  *<i>once started</i>. Until<code>start()</code> is called, the hostname&  * port may be undefined. Accordingly, the service raises an exception in this  * condition.  *  * If you wish to chain together a registry service with this one under  * the same<code>CompositeService</code>, this service must be added  * as a child first.  *  * It also sets the configuration parameter  * {@link RegistryConstants#KEY_REGISTRY_ZK_QUORUM}  * to its connection string. Any code with access to the service configuration  * can view it.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MicroZookeeperService
specifier|public
class|class
name|MicroZookeeperService
extends|extends
name|AbstractService
implements|implements
name|RegistryBindingSource
implements|,
name|RegistryConstants
implements|,
name|ZookeeperConfigOptions
implements|,
name|MicroZookeeperServiceKeys
block|{
specifier|private
specifier|static
specifier|final
name|Logger
DECL|field|LOG
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MicroZookeeperService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|instanceDir
specifier|private
name|File
name|instanceDir
decl_stmt|;
DECL|field|dataDir
specifier|private
name|File
name|dataDir
decl_stmt|;
DECL|field|tickTime
specifier|private
name|int
name|tickTime
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|host
specifier|private
name|String
name|host
decl_stmt|;
DECL|field|secureServer
specifier|private
name|boolean
name|secureServer
decl_stmt|;
DECL|field|factory
specifier|private
name|ServerCnxnFactory
name|factory
decl_stmt|;
DECL|field|binding
specifier|private
name|BindingInformation
name|binding
decl_stmt|;
DECL|field|confDir
specifier|private
name|File
name|confDir
decl_stmt|;
DECL|field|diagnostics
specifier|private
name|StringBuilder
name|diagnostics
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|/**    * Create an instance    * @param name service name    */
DECL|method|MicroZookeeperService (String name)
specifier|public
name|MicroZookeeperService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the connection string.    * @return the string    * @throws IllegalStateException if the connection is not yet valid    */
DECL|method|getConnectionString ()
specifier|public
name|String
name|getConnectionString
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|factory
operator|!=
literal|null
argument_list|,
literal|"service not started"
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|factory
operator|.
name|getLocalAddress
argument_list|()
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s:%d"
argument_list|,
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the connection address    * @return the connection as an address    * @throws IllegalStateException if the connection is not yet valid    */
DECL|method|getConnectionAddress ()
specifier|public
name|InetSocketAddress
name|getConnectionAddress
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|factory
operator|!=
literal|null
argument_list|,
literal|"service not started"
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|getLocalAddress
argument_list|()
return|;
block|}
comment|/**    * Create an inet socket addr from the local host + port number    * @param port port to use    * @return a (hostname, port) pair    * @throws UnknownHostException if the server cannot resolve the host    */
DECL|method|getAddress (int port)
specifier|private
name|InetSocketAddress
name|getAddress
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|UnknownHostException
block|{
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|host
argument_list|,
name|port
operator|<
literal|0
condition|?
literal|0
else|:
name|port
argument_list|)
return|;
block|}
comment|/**    * Initialize the service, including choosing a path for the data    * @param conf configuration    * @throws Exception    */
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|port
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|KEY_ZKSERVICE_PORT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|tickTime
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|KEY_ZKSERVICE_TICK_TIME
argument_list|,
name|ZooKeeperServer
operator|.
name|DEFAULT_TICK_TIME
argument_list|)
expr_stmt|;
name|String
name|instancedirname
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|KEY_ZKSERVICE_DIR
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|host
operator|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|KEY_ZKSERVICE_HOST
argument_list|,
name|DEFAULT_ZKSERVICE_HOST
argument_list|)
expr_stmt|;
if|if
condition|(
name|instancedirname
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|File
name|testdir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.dir"
argument_list|,
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
name|instanceDir
operator|=
operator|new
name|File
argument_list|(
name|testdir
argument_list|,
literal|"zookeeper"
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|instanceDir
operator|=
operator|new
name|File
argument_list|(
name|instancedirname
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|instanceDir
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Instance directory is {}"
argument_list|,
name|instanceDir
argument_list|)
expr_stmt|;
name|mkdirStrict
argument_list|(
name|instanceDir
argument_list|)
expr_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
name|confDir
operator|=
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|mkdirStrict
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
name|mkdirStrict
argument_list|(
name|confDir
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a directory, ignoring if the dir is already there,    * and failing if a file or something else was at the end of that    * path    * @param dir dir to guarantee the existence of    * @throws IOException IO problems, or path exists but is not a dir    */
DECL|method|mkdirStrict (File dir)
specifier|private
name|void
name|mkdirStrict
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to mkdir "
operator|+
name|dir
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Append a formatted string to the diagnostics.    *<p>    * A newline is appended afterwards.    * @param text text including any format commands    * @param args arguments for the forma operation.    */
DECL|method|addDiagnostics (String text, Object ... args)
specifier|protected
name|void
name|addDiagnostics
parameter_list|(
name|String
name|text
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|diagnostics
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|text
argument_list|,
name|args
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the diagnostics info    * @return the diagnostics string built up    */
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * set up security. this must be done prior to creating    * the ZK instance, as it sets up JAAS if that has not been done already.    *    * @return true if the cluster has security enabled.    */
DECL|method|setupSecurity ()
specifier|public
name|boolean
name|setupSecurity
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|String
name|jaasContext
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|KEY_REGISTRY_ZKSERVICE_JAAS_CONTEXT
argument_list|)
decl_stmt|;
name|secureServer
operator|=
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|jaasContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|secureServer
condition|)
block|{
name|RegistrySecurity
operator|.
name|validateContext
argument_list|(
name|jaasContext
argument_list|)
expr_stmt|;
name|RegistrySecurity
operator|.
name|bindZKToServerJAASContext
argument_list|(
name|jaasContext
argument_list|)
expr_stmt|;
comment|// policy on failed auth
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_ZK_ALLOW_FAILED_SASL_CLIENTS
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|KEY_ZKSERVICE_ALLOW_FAILED_SASL_CLIENTS
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
comment|//needed so that you can use sasl: strings in the registry
name|System
operator|.
name|setProperty
argument_list|(
name|RegistryInternalConstants
operator|.
name|ZOOKEEPER_AUTH_PROVIDER
operator|+
literal|".1"
argument_list|,
name|RegistryInternalConstants
operator|.
name|SASLAUTHENTICATION_PROVIDER
argument_list|)
expr_stmt|;
name|String
name|serverContext
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_ZK_SERVER_SASL_CONTEXT
argument_list|)
decl_stmt|;
name|addDiagnostics
argument_list|(
literal|"Server JAAS context s = %s"
argument_list|,
name|serverContext
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Startup: start ZK. It is only after this that    * the binding information is valid.    * @throws Exception    */
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|setupSecurity
argument_list|()
expr_stmt|;
name|ZooKeeperServer
name|zkServer
init|=
operator|new
name|ZooKeeperServer
argument_list|()
decl_stmt|;
name|FileTxnSnapLog
name|ftxn
init|=
operator|new
name|FileTxnSnapLog
argument_list|(
name|dataDir
argument_list|,
name|dataDir
argument_list|)
decl_stmt|;
name|zkServer
operator|.
name|setTxnLogFactory
argument_list|(
name|ftxn
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|setTickTime
argument_list|(
name|tickTime
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting Local Zookeeper service"
argument_list|)
expr_stmt|;
name|factory
operator|=
name|ServerCnxnFactory
operator|.
name|createFactory
argument_list|()
expr_stmt|;
name|factory
operator|.
name|configure
argument_list|(
name|getAddress
argument_list|(
name|port
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|factory
operator|.
name|startup
argument_list|(
name|zkServer
argument_list|)
expr_stmt|;
name|String
name|connectString
init|=
name|getConnectionString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"In memory ZK started at {}\n"
argument_list|,
name|connectString
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|zkServer
operator|.
name|dumpConf
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|binding
operator|=
operator|new
name|BindingInformation
argument_list|()
expr_stmt|;
name|binding
operator|.
name|ensembleProvider
operator|=
operator|new
name|FixedEnsembleProvider
argument_list|(
name|connectString
argument_list|)
expr_stmt|;
name|binding
operator|.
name|description
operator|=
name|getName
argument_list|()
operator|+
literal|" reachable at \""
operator|+
name|connectString
operator|+
literal|"\""
expr_stmt|;
name|addDiagnostics
argument_list|(
name|binding
operator|.
name|description
argument_list|)
expr_stmt|;
comment|// finally: set the binding information in the config
name|getConfig
argument_list|()
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_ZK_QUORUM
argument_list|,
name|connectString
argument_list|)
expr_stmt|;
block|}
comment|/**    * When the service is stopped, it deletes the data directory    * and its contents    * @throws Exception    */
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
name|factory
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|factory
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|dataDir
operator|!=
literal|null
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|supplyBindingInformation ()
specifier|public
name|BindingInformation
name|supplyBindingInformation
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|binding
argument_list|,
literal|"Service is not started: binding information undefined"
argument_list|)
expr_stmt|;
return|return
name|binding
return|;
block|}
block|}
end_class

end_unit

