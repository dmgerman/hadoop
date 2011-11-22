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
name|URL
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
name|Path
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
name|protocol
operator|.
name|HdfsFileStatus
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
name|LocatedBlocks
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

begin_comment
comment|/** Redirect queries about the hosted filesystem to an appropriate datanode.  * @see org.apache.hadoop.hdfs.HftpFileSystem  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileDataServlet
specifier|public
class|class
name|FileDataServlet
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
DECL|method|createRedirectURL (String path, String encodedPath, HdfsFileStatus status, UserGroupInformation ugi, ClientProtocol nnproxy, HttpServletRequest request, String dt)
specifier|private
name|URL
name|createRedirectURL
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|encodedPath
parameter_list|,
name|HdfsFileStatus
name|status
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|ClientProtocol
name|nnproxy
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|dt
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|scheme
init|=
name|request
operator|.
name|getScheme
argument_list|()
decl_stmt|;
specifier|final
name|LocatedBlocks
name|blks
init|=
name|nnproxy
operator|.
name|getBlockLocations
argument_list|(
name|status
operator|.
name|getFullPath
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
name|NameNodeHttpServer
operator|.
name|getConfFromContext
argument_list|(
name|getServletContext
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeID
name|host
init|=
name|pickSrcDatanode
argument_list|(
name|blks
argument_list|,
name|status
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|String
name|hostname
decl_stmt|;
if|if
condition|(
name|host
operator|instanceof
name|DatanodeInfo
condition|)
block|{
name|hostname
operator|=
operator|(
operator|(
name|DatanodeInfo
operator|)
name|host
operator|)
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|hostname
operator|=
name|host
operator|.
name|getHost
argument_list|()
expr_stmt|;
block|}
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
literal|"datanode.https.port"
argument_list|)
else|:
name|host
operator|.
name|getInfoPort
argument_list|()
decl_stmt|;
name|String
name|dtParam
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|dt
operator|!=
literal|null
condition|)
block|{
name|dtParam
operator|=
name|JspHelper
operator|.
name|getDelegationTokenUrlParam
argument_list|(
name|dt
argument_list|)
expr_stmt|;
block|}
comment|// Add namenode address to the url params
name|NameNode
name|nn
init|=
name|NameNodeHttpServer
operator|.
name|getNameNodeFromContext
argument_list|(
name|getServletContext
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|addr
init|=
name|NameNode
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
literal|"/streamFile"
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
comment|/** Select a datanode to service this request.    * Currently, this looks at no more than the first five blocks of a file,    * selecting a datanode randomly from the most represented.    * @param conf     */
DECL|method|pickSrcDatanode (LocatedBlocks blks, HdfsFileStatus i, Configuration conf)
specifier|private
name|DatanodeID
name|pickSrcDatanode
parameter_list|(
name|LocatedBlocks
name|blks
parameter_list|,
name|HdfsFileStatus
name|i
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|i
operator|.
name|getLen
argument_list|()
operator|==
literal|0
operator|||
name|blks
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
operator|<=
literal|0
condition|)
block|{
comment|// pick a random datanode
name|NameNode
name|nn
init|=
name|NameNodeHttpServer
operator|.
name|getNameNodeFromContext
argument_list|(
name|getServletContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|NamenodeJspHelper
operator|.
name|getRandomDatanode
argument_list|(
name|nn
argument_list|)
return|;
block|}
return|return
name|JspHelper
operator|.
name|bestNode
argument_list|(
name|blks
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Service a GET request as described below.    * Request:    * {@code    * GET http://<nn>:<port>/data[/<path>] HTTP/1.1    * }    */
DECL|method|doGet (final HttpServletRequest request, final HttpServletResponse response)
specifier|public
name|void
name|doGet
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Configuration
name|conf
init|=
name|NameNodeHttpServer
operator|.
name|getConfFromContext
argument_list|(
name|getServletContext
argument_list|()
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
try|try
block|{
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|ClientProtocol
name|nn
init|=
name|createNameNodeProxy
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
literal|"/data"
argument_list|)
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
literal|"/data"
argument_list|)
decl_stmt|;
name|String
name|delegationToken
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|JspHelper
operator|.
name|DELEGATION_PARAMETER_NAME
argument_list|)
decl_stmt|;
name|HdfsFileStatus
name|info
init|=
name|nn
operator|.
name|getFileInfo
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
operator|&&
operator|!
name|info
operator|.
name|isDir
argument_list|()
condition|)
block|{
name|response
operator|.
name|sendRedirect
argument_list|(
name|createRedirectURL
argument_list|(
name|path
argument_list|,
name|encodedPath
argument_list|,
name|info
argument_list|,
name|ugi
argument_list|,
name|nn
argument_list|,
name|request
argument_list|,
name|delegationToken
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
literal|400
argument_list|,
literal|"File not found "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|response
operator|.
name|sendError
argument_list|(
literal|400
argument_list|,
name|path
operator|+
literal|": is a directory"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
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
catch|catch
parameter_list|(
name|InterruptedException
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
end_class

end_unit

