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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_ADMIN
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_ADDRESS_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_DEFAULT
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|HdfsConfiguration
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
name|AuthFilter
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
name|http
operator|.
name|HttpServer
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
name|authentication
operator|.
name|server
operator|.
name|AuthenticationFilter
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
name|authorize
operator|.
name|AccessControlList
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
name|HttpServer
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
DECL|field|LOG
specifier|private
specifier|final
name|Log
name|LOG
init|=
name|NameNode
operator|.
name|LOG
decl_stmt|;
DECL|field|httpAddress
specifier|private
name|InetSocketAddress
name|httpAddress
decl_stmt|;
DECL|field|bindAddress
specifier|private
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
DECL|method|NameNodeHttpServer ( Configuration conf, NameNode nn, InetSocketAddress bindAddress)
specifier|public
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
DECL|method|getDefaultServerPrincipal ()
specifier|private
name|String
name|getDefaultServerPrincipal
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|)
argument_list|,
name|nn
operator|.
name|getNameNodeAddress
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|infoHost
init|=
name|bindAddress
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|int
name|infoPort
init|=
name|bindAddress
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|httpServer
operator|=
operator|new
name|HttpServer
argument_list|(
literal|"hdfs"
argument_list|,
name|infoHost
argument_list|,
name|infoPort
argument_list|,
name|infoPort
operator|==
literal|0
argument_list|,
name|conf
argument_list|,
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFS_ADMIN
argument_list|,
literal|" "
argument_list|)
argument_list|)
argument_list|)
block|{
block|{
comment|// Add SPNEGO support to NameNode
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
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
name|DFS_NAMENODE_INTERNAL_SPENGO_USER_NAME_KEY
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
literal|"kerberos.principal"
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|principalInConf
argument_list|,
name|infoHost
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|httpKeytab
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
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
literal|"kerberos.keytab"
argument_list|,
name|httpKeytab
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|put
argument_list|(
name|AuthenticationFilter
operator|.
name|AUTH_TYPE
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|defineFilter
argument_list|(
name|webAppContext
argument_list|,
name|SPNEGO_FILTER
argument_list|,
name|AuthenticationFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|params
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|WebHdfsFileSystem
operator|.
name|isEnabled
argument_list|(
name|conf
argument_list|,
name|LOG
argument_list|)
condition|)
block|{
comment|//add SPNEGO authentication filter for webhdfs
specifier|final
name|String
name|name
init|=
literal|"SPNEGO"
decl_stmt|;
specifier|final
name|String
name|classname
init|=
name|AuthFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|defineFilter
argument_list|(
name|webAppContext
argument_list|,
name|name
argument_list|,
name|classname
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
name|classname
operator|+
literal|")"
argument_list|)
expr_stmt|;
comment|// add webhdfs packages
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
block|}
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
name|String
name|httpKeytab
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_WEB_AUTHENTICATION_KERBEROS_KEYTAB_KEY
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
return|return
name|params
return|;
block|}
block|}
expr_stmt|;
name|boolean
name|certSSL
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
literal|"dfs.https.enable"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|certSSL
condition|)
block|{
name|boolean
name|needClientAuth
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
literal|"dfs.https.need.client.auth"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|secInfoSocAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|infoHost
operator|+
literal|":"
operator|+
name|conf
operator|.
name|get
argument_list|(
literal|"dfs.https.port"
argument_list|,
name|infoHost
operator|+
literal|":"
operator|+
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|sslConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|certSSL
condition|)
block|{
name|sslConf
operator|.
name|addResource
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"dfs.https.server.keystore.resource"
argument_list|,
literal|"ssl-server.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|httpServer
operator|.
name|addSslListener
argument_list|(
name|secInfoSocAddr
argument_list|,
name|sslConf
argument_list|,
name|needClientAuth
argument_list|)
expr_stmt|;
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
name|get
argument_list|(
literal|"dfs.datanode.https.address"
argument_list|,
name|infoHost
operator|+
literal|":"
operator|+
literal|50475
argument_list|)
argument_list|)
decl_stmt|;
name|httpServer
operator|.
name|setAttribute
argument_list|(
literal|"datanode.https.port"
argument_list|,
name|datanodeSslPort
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|NAMENODE_ADDRESS_ATTRIBUTE_KEY
argument_list|,
name|nn
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|setAttribute
argument_list|(
name|FSIMAGE_ATTRIBUTE_KEY
argument_list|,
name|nn
operator|.
name|getFSImage
argument_list|()
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
name|httpAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|bindAddress
operator|.
name|getAddress
argument_list|()
argument_list|,
name|httpServer
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
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
specifier|public
name|InetSocketAddress
name|getHttpAddress
parameter_list|()
block|{
return|return
name|httpAddress
return|;
block|}
DECL|method|setupServlets (HttpServer httpServer, Configuration conf)
specifier|private
specifier|static
name|void
name|setupServlets
parameter_list|(
name|HttpServer
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
literal|"getDelegationToken"
argument_list|,
name|GetDelegationTokenServlet
operator|.
name|PATH_SPEC
argument_list|,
name|GetDelegationTokenServlet
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
literal|"renewDelegationToken"
argument_list|,
name|RenewDelegationTokenServlet
operator|.
name|PATH_SPEC
argument_list|,
name|RenewDelegationTokenServlet
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
literal|"cancelDelegationToken"
argument_list|,
name|CancelDelegationTokenServlet
operator|.
name|PATH_SPEC
argument_list|,
name|CancelDelegationTokenServlet
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
literal|"getimage"
argument_list|,
literal|"/getimage"
argument_list|,
name|GetImageServlet
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
literal|"listPaths"
argument_list|,
literal|"/listPaths/*"
argument_list|,
name|ListPathsServlet
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|addInternalServlet
argument_list|(
literal|"data"
argument_list|,
literal|"/data/*"
argument_list|,
name|FileDataServlet
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|addInternalServlet
argument_list|(
literal|"checksum"
argument_list|,
literal|"/fileChecksum/*"
argument_list|,
name|FileChecksumServlets
operator|.
name|RedirectServlet
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|addInternalServlet
argument_list|(
literal|"contentSummary"
argument_list|,
literal|"/contentSummary/*"
argument_list|,
name|ContentSummaryServlet
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getFsImageFromContext (ServletContext context)
specifier|public
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
specifier|public
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
block|}
end_class

end_unit

