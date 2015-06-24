begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|HashMap
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
name|servlet
operator|.
name|ServletContext
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
name|ha
operator|.
name|HAServiceProtocol
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|DFSUtil
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|JspHelper
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|startupprogress
operator|.
name|StartupProgress
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|web
operator|.
name|resources
operator|.
name|NamenodeWebHdfsMethods
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
name|hdfs
operator|.
name|web
operator|.
name|WebHdfsFileSystem
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
name|hdfs
operator|.
name|web
operator|.
name|resources
operator|.
name|Param
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
name|hdfs
operator|.
name|web
operator|.
name|resources
operator|.
name|UserParam
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
name|http
operator|.
name|HttpServer2
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * Encapsulates the HTTP server started by the NameNode.   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NameNodeHttpServer
specifier|public
class|class
name|NameNodeHttpServer
block|{
DECL|field|httpServer
specifier|private
name|HttpServer2
name|httpServer
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|nn
specifier|private
specifier|final
name|NameNode
name|nn
decl_stmt|;
DECL|field|httpAddress
specifier|private
name|InetSocketAddress
name|httpAddress
decl_stmt|;
DECL|field|httpsAddress
specifier|private
name|InetSocketAddress
name|httpsAddress
decl_stmt|;
DECL|field|bindAddress
specifier|private
specifier|final
name|InetSocketAddress
name|bindAddress
decl_stmt|;
DECL|field|NAMENODE_ADDRESS_ATTRIBUTE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NAMENODE_ADDRESS_ATTRIBUTE_KEY
init|=
literal|"name.node.address"
decl_stmt|;
DECL|field|FSIMAGE_ATTRIBUTE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FSIMAGE_ATTRIBUTE_KEY
init|=
literal|"name.system.image"
decl_stmt|;
DECL|field|NAMENODE_ATTRIBUTE_KEY
specifier|protected
specifier|static
specifier|final
name|String
name|NAMENODE_ATTRIBUTE_KEY
init|=
literal|"name.node"
decl_stmt|;
DECL|field|STARTUP_PROGRESS_ATTRIBUTE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|STARTUP_PROGRESS_ATTRIBUTE_KEY
init|=
literal|"startup.progress"
decl_stmt|;
DECL|method|NameNodeHttpServer (Configuration conf, NameNode nn, InetSocketAddress bindAddress)
name|NameNodeHttpServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NameNode
name|nn
parameter_list|,
name|InetSocketAddress
name|bindAddress
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|nn
operator|=
name|nn
expr_stmt|;
name|this
operator|.
name|bindAddress
operator|=
name|bindAddress
expr_stmt|;
block|}
DECL|method|initWebHdfs (Configuration conf)
specifier|private
name|void
name|initWebHdfs
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// set user pattern based on configuration file
name|UserParam
operator|.
name|setUserPattern
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_USER_PATTERN_KEY
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|DFS_WEBHDFS_USER_PATTERN_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
comment|// add authentication filter for webhdfs
specifier|final
name|String
name|className
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_AUTHENTICATION_FILTER_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_WEBHDFS_AUTHENTICATION_FILTER_DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|className
decl_stmt|;
specifier|final
name|String
name|pathSpec
init|=
name|WebHdfsFileSystem
operator|.
name|PATH_PREFIX
operator|+
literal|"/*"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|getAuthFilterParams
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|HttpServer2
operator|.
name|defineFilter
argument_list|(
name|httpServer
operator|.
name|getWebAppContext
argument_list|()
argument_list|,
name|name
argument_list|,
name|className
argument_list|,
name|params
argument_list|,
operator|new
name|String
index|[]
block|{
name|pathSpec
block|}
argument_list|)
expr_stmt|;
name|HttpServer2
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Added filter '"
operator|+
name|name
operator|+
literal|"' (class="
operator|+
name|className
operator|+
literal|")"
argument_list|)
expr_stmt|;
comment|// add webhdfs packages
name|httpServer
operator|.
name|addJerseyResourcePackage
argument_list|(
name|NamenodeWebHdfsMethods
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|";"
operator|+
name|Param
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|pathSpec
argument_list|)
expr_stmt|;
block|}
comment|/**    * @see DFSUtil#getHttpPolicy(org.apache.hadoop.conf.Configuration)    * for information related to the different configuration options and    * Http Policy is decided.    */
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpConfig
operator|.
name|Policy
name|policy
init|=
name|DFSUtil
operator|.
name|getHttpPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|String
name|infoHost
init|=
name|bindAddress
operator|.
name|getHostName
argument_list|()
decl_stmt|;
specifier|final
name|InetSocketAddress
name|httpAddr
init|=
name|bindAddress
decl_stmt|;
specifier|final
name|String
name|httpsAddrString
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|httpsAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|httpsAddrString
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpsAddr
operator|!=
literal|null
condition|)
block|{
comment|// If DFS_NAMENODE_HTTPS_BIND_HOST_KEY exists then it overrides the
comment|// host name portion of DFS_NAMENODE_HTTPS_ADDRESS_KEY.
specifier|final
name|String
name|bindHost
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_BIND_HOST_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|bindHost
operator|!=
literal|null
operator|&&
operator|!
name|bindHost
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|httpsAddr
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|bindHost
argument_list|,
name|httpsAddr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|HttpServer2
operator|.
name|Builder
name|builder
init|=
name|DFSUtil
operator|.
name|httpServerTemplateForNNAndJN
argument_list|(
name|conf
argument_list|,
name|httpAddr
argument_list|,
name|httpsAddr
argument_list|,
literal|"hdfs"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KERBEROS_INTERNAL_SPNEGO_PRINCIPAL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|)
decl_stmt|;
name|httpServer
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
if|if
condition|(
name|policy
operator|.
name|isHttpsEnabled
argument_list|()
condition|)
block|{
comment|// assume same ssl port for all datanodes
name|InetSocketAddress
name|datanodeSslPort
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
argument_list|,
name|infoHost
operator|+
literal|":"
operator|+
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_DEFAULT_PORT
argument_list|)
argument_list|)
decl_stmt|;
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_PORT_KEY
argument_list|,
name|datanodeSslPort
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|initWebHdfs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|NAMENODE_ATTRIBUTE_KEY
argument_list|,
name|nn
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|setupServlets
argument_list|(
name|httpServer
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|connIdx
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|policy
operator|.
name|isHttpEnabled
argument_list|()
condition|)
block|{
name|httpAddress
operator|=
name|httpServer
operator|.
name|getConnectorAddress
argument_list|(
name|connIdx
operator|++
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|httpAddress
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|policy
operator|.
name|isHttpsEnabled
argument_list|()
condition|)
block|{
name|httpsAddress
operator|=
name|httpServer
operator|.
name|getConnectorAddress
argument_list|(
name|connIdx
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
argument_list|,
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|httpsAddress
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAuthFilterParams (Configuration conf)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAuthFilterParams
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|principalInConf
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|principalInConf
operator|!=
literal|null
operator|&&
operator|!
name|principalInConf
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|principalInConf
argument_list|,
name|bindAddress
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|HttpServer2
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"WebHDFS and security are enabled, but configuration property '"
operator|+
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL_KEY
operator|+
literal|"' is not set."
argument_list|)
expr_stmt|;
block|}
name|String
name|httpKeytab
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSUtil
operator|.
name|getSpnegoKeytabKey
argument_list|(
name|conf
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpKeytab
operator|!=
literal|null
operator|&&
operator|!
name|httpKeytab
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_KEYTAB_KEY
argument_list|,
name|httpKeytab
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|HttpServer2
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"WebHDFS and security are enabled, but configuration property '"
operator|+
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_KEYTAB_KEY
operator|+
literal|"' is not set."
argument_list|)
expr_stmt|;
block|}
name|String
name|anonymousAllowed
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_SIMPLE_ANONYMOUS_ALLOWED
argument_list|)
decl_stmt|;
if|if
condition|(
name|anonymousAllowed
operator|!=
literal|null
operator|&&
operator|!
name|anonymousAllowed
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_SIMPLE_ANONYMOUS_ALLOWED
argument_list|,
name|anonymousAllowed
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|httpServer
operator|!=
literal|null
condition|)
block|{
name|httpServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getHttpAddress ()
name|InetSocketAddress
name|getHttpAddress
parameter_list|()
block|{
return|return
name|httpAddress
return|;
block|}
DECL|method|getHttpsAddress ()
name|InetSocketAddress
name|getHttpsAddress
parameter_list|()
block|{
return|return
name|httpsAddress
return|;
block|}
comment|/**    * Sets fsimage for use by servlets.    *     * @param fsImage FSImage to set    */
DECL|method|setFSImage (FSImage fsImage)
name|void
name|setFSImage
parameter_list|(
name|FSImage
name|fsImage
parameter_list|)
block|{
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|FSIMAGE_ATTRIBUTE_KEY
argument_list|,
name|fsImage
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets address of namenode for use by servlets.    *     * @param nameNodeAddress InetSocketAddress to set    */
DECL|method|setNameNodeAddress (InetSocketAddress nameNodeAddress)
name|void
name|setNameNodeAddress
parameter_list|(
name|InetSocketAddress
name|nameNodeAddress
parameter_list|)
block|{
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|NAMENODE_ADDRESS_ATTRIBUTE_KEY
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|nameNodeAddress
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets startup progress of namenode for use by servlets.    *     * @param prog StartupProgress to set    */
DECL|method|setStartupProgress (StartupProgress prog)
name|void
name|setStartupProgress
parameter_list|(
name|StartupProgress
name|prog
parameter_list|)
block|{
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|STARTUP_PROGRESS_ATTRIBUTE_KEY
argument_list|,
name|prog
argument_list|)
expr_stmt|;
block|}
DECL|method|setupServlets (HttpServer2 httpServer, Configuration conf)
specifier|private
specifier|static
name|void
name|setupServlets
parameter_list|(
name|HttpServer2
name|httpServer
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|httpServer
operator|.
name|addInternalServlet
argument_list|(
literal|"startupProgress"
argument_list|,
name|StartupProgressServlet
operator|.
name|PATH_SPEC
argument_list|,
name|StartupProgressServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|addInternalServlet
argument_list|(
literal|"fsck"
argument_list|,
literal|"/fsck"
argument_list|,
name|FsckServlet
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|addInternalServlet
argument_list|(
literal|"imagetransfer"
argument_list|,
name|ImageServlet
operator|.
name|PATH_SPEC
argument_list|,
name|ImageServlet
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getFsImageFromContext (ServletContext context)
specifier|static
name|FSImage
name|getFsImageFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
return|return
operator|(
name|FSImage
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|FSIMAGE_ATTRIBUTE_KEY
argument_list|)
return|;
block|}
DECL|method|getNameNodeFromContext (ServletContext context)
specifier|public
specifier|static
name|NameNode
name|getNameNodeFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
return|return
operator|(
name|NameNode
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|NAMENODE_ATTRIBUTE_KEY
argument_list|)
return|;
block|}
DECL|method|getConfFromContext (ServletContext context)
specifier|static
name|Configuration
name|getConfFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
return|return
operator|(
name|Configuration
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|)
return|;
block|}
DECL|method|getNameNodeAddressFromContext ( ServletContext context)
specifier|public
specifier|static
name|InetSocketAddress
name|getNameNodeAddressFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
return|return
operator|(
name|InetSocketAddress
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|NAMENODE_ADDRESS_ATTRIBUTE_KEY
argument_list|)
return|;
block|}
comment|/**    * Returns StartupProgress associated with ServletContext.    *     * @param context ServletContext to get    * @return StartupProgress associated with context    */
DECL|method|getStartupProgressFromContext ( ServletContext context)
specifier|static
name|StartupProgress
name|getStartupProgressFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
return|return
operator|(
name|StartupProgress
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|STARTUP_PROGRESS_ATTRIBUTE_KEY
argument_list|)
return|;
block|}
DECL|method|getNameNodeStateFromContext (ServletContext context)
specifier|public
specifier|static
name|HAServiceProtocol
operator|.
name|HAServiceState
name|getNameNodeStateFromContext
parameter_list|(
name|ServletContext
name|context
parameter_list|)
block|{
return|return
name|getNameNodeFromContext
argument_list|(
name|context
argument_list|)
operator|.
name|getServiceState
argument_list|()
return|;
block|}
block|}
end_class

end_unit

