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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|fs
operator|.
name|MD5MD5CRC32FileChecksum
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
name|DFSClient
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|protocol
operator|.
name|DatanodeInfo
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
name|HdfsServerConstants
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
name|datanode
operator|.
name|DataNode
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
name|datanode
operator|.
name|DatanodeJspHelper
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
name|util
operator|.
name|ServletUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|znerd
operator|.
name|xmlenc
operator|.
name|XMLOutputter
import|;
end_import

begin_comment
comment|/** Servlets for file checksum */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileChecksumServlets
specifier|public
class|class
name|FileChecksumServlets
block|{
comment|/** Redirect file checksum queries to an appropriate datanode. */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RedirectServlet
specifier|public
specifier|static
class|class
name|RedirectServlet
extends|extends
name|DfsServlet
block|{
comment|/** For java.io.Serializable */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/** Create a redirection URL */
DECL|method|createRedirectURL (UserGroupInformation ugi, DatanodeID host, HttpServletRequest request, NameNode nn)
specifier|private
name|URL
name|createRedirectURL
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|DatanodeID
name|host
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|NameNode
name|nn
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|hostname
init|=
name|host
operator|instanceof
name|DatanodeInfo
condition|?
operator|(
operator|(
name|DatanodeInfo
operator|)
name|host
operator|)
operator|.
name|getHostName
argument_list|()
else|:
name|host
operator|.
name|getIpAddr
argument_list|()
decl_stmt|;
specifier|final
name|String
name|scheme
init|=
name|request
operator|.
name|getScheme
argument_list|()
decl_stmt|;
specifier|final
name|int
name|port
init|=
literal|"https"
operator|.
name|equals
argument_list|(
name|scheme
argument_list|)
condition|?
operator|(
name|Integer
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTPS_PORT_KEY
argument_list|)
else|:
name|host
operator|.
name|getInfoPort
argument_list|()
decl_stmt|;
specifier|final
name|String
name|encodedPath
init|=
name|ServletUtil
operator|.
name|getRawPath
argument_list|(
name|request
argument_list|,
literal|"/fileChecksum"
argument_list|)
decl_stmt|;
name|String
name|dtParam
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|String
name|tokenString
init|=
name|ugi
operator|.
name|getTokens
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|encodeToUrlString
argument_list|()
decl_stmt|;
name|dtParam
operator|=
name|JspHelper
operator|.
name|getDelegationTokenUrlParam
argument_list|(
name|tokenString
argument_list|)
expr_stmt|;
block|}
name|String
name|addr
init|=
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|nn
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|addrParam
init|=
name|JspHelper
operator|.
name|getUrlParam
argument_list|(
name|JspHelper
operator|.
name|NAMENODE_ADDRESS
argument_list|,
name|addr
argument_list|)
decl_stmt|;
return|return
operator|new
name|URL
argument_list|(
name|scheme
argument_list|,
name|hostname
argument_list|,
name|port
argument_list|,
literal|"/getFileChecksum"
operator|+
name|encodedPath
operator|+
literal|'?'
operator|+
literal|"ugi="
operator|+
name|ServletUtil
operator|.
name|encodeQueryValue
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|+
name|dtParam
operator|+
name|addrParam
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
specifier|final
name|ServletContext
name|context
init|=
name|getServletContext
argument_list|()
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
name|NameNodeHttpServer
operator|.
name|getConfFromContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|getUGI
argument_list|(
name|request
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|NameNode
name|namenode
init|=
name|NameNodeHttpServer
operator|.
name|getNameNodeFromContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeID
name|datanode
init|=
name|NamenodeJspHelper
operator|.
name|getRandomDatanode
argument_list|(
name|namenode
argument_list|)
decl_stmt|;
try|try
block|{
name|response
operator|.
name|sendRedirect
argument_list|(
name|createRedirectURL
argument_list|(
name|ugi
argument_list|,
name|datanode
argument_list|,
name|request
argument_list|,
name|namenode
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
literal|400
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Get FileChecksum */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|GetServlet
specifier|public
specifier|static
class|class
name|GetServlet
extends|extends
name|DfsServlet
block|{
comment|/** For java.io.Serializable */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
specifier|final
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|ServletUtil
operator|.
name|getDecodedPath
argument_list|(
name|request
argument_list|,
literal|"/getFileChecksum"
argument_list|)
decl_stmt|;
specifier|final
name|XMLOutputter
name|xml
init|=
operator|new
name|XMLOutputter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|xml
operator|.
name|declaration
argument_list|()
expr_stmt|;
specifier|final
name|ServletContext
name|context
init|=
name|getServletContext
argument_list|()
decl_stmt|;
specifier|final
name|DataNode
name|datanode
init|=
operator|(
name|DataNode
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
literal|"datanode"
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|datanode
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|socketTimeout
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
decl_stmt|;
specifier|final
name|SocketFactory
name|socketFactory
init|=
name|NetUtils
operator|.
name|getSocketFactory
argument_list|(
name|conf
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|DFSClient
name|dfs
init|=
name|DatanodeJspHelper
operator|.
name|getDFSClient
argument_list|(
name|request
argument_list|,
name|datanode
argument_list|,
name|conf
argument_list|,
name|getUGI
argument_list|(
name|request
argument_list|,
name|conf
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ClientProtocol
name|nnproxy
init|=
name|dfs
operator|.
name|getNamenode
argument_list|()
decl_stmt|;
specifier|final
name|MD5MD5CRC32FileChecksum
name|checksum
init|=
name|DFSClient
operator|.
name|getFileChecksum
argument_list|(
name|path
argument_list|,
name|nnproxy
argument_list|,
name|socketFactory
argument_list|,
name|socketTimeout
argument_list|)
decl_stmt|;
name|MD5MD5CRC32FileChecksum
operator|.
name|write
argument_list|(
name|xml
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|writeXml
argument_list|(
name|ioe
argument_list|,
name|path
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|writeXml
argument_list|(
name|e
argument_list|,
name|path
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
name|xml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

