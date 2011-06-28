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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|FSInputStream
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
name|DFSInputStream
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
name|io
operator|.
name|IOUtils
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
name|mortbay
operator|.
name|jetty
operator|.
name|InclusiveByteRange
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StreamFile
specifier|public
class|class
name|StreamFile
extends|extends
name|DfsServlet
block|{
comment|/** for java.io.Serializable */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|CONTENT_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_LENGTH
init|=
literal|"Content-Length"
decl_stmt|;
comment|/** getting a client for connecting to dfs */
DECL|method|getDFSClient (HttpServletRequest request)
specifier|protected
name|DFSClient
name|getDFSClient
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|(
name|Configuration
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|JspHelper
operator|.
name|CURRENT_CONF
argument_list|)
decl_stmt|;
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
return|return
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
name|ugi
argument_list|)
return|;
block|}
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response)
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
name|String
name|path
init|=
name|request
operator|.
name|getPathInfo
argument_list|()
operator|!=
literal|null
condition|?
name|request
operator|.
name|getPathInfo
argument_list|()
else|:
literal|"/"
decl_stmt|;
specifier|final
name|String
name|filename
init|=
name|JspHelper
operator|.
name|validatePath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|filename
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"Invalid input"
argument_list|)
expr_stmt|;
return|return;
block|}
name|Enumeration
argument_list|<
name|?
argument_list|>
name|reqRanges
init|=
name|request
operator|.
name|getHeaders
argument_list|(
literal|"Range"
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqRanges
operator|!=
literal|null
operator|&&
operator|!
name|reqRanges
operator|.
name|hasMoreElements
argument_list|()
condition|)
name|reqRanges
operator|=
literal|null
expr_stmt|;
name|DFSClient
name|dfs
decl_stmt|;
try|try
block|{
name|dfs
operator|=
name|getDFSClient
argument_list|(
name|request
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
return|return;
block|}
specifier|final
name|DFSInputStream
name|in
init|=
name|dfs
operator|.
name|open
argument_list|(
name|filename
argument_list|)
decl_stmt|;
specifier|final
name|long
name|fileLen
init|=
name|in
operator|.
name|getFileLength
argument_list|()
decl_stmt|;
name|OutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|reqRanges
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|?
argument_list|>
name|ranges
init|=
name|InclusiveByteRange
operator|.
name|satisfiableRanges
argument_list|(
name|reqRanges
argument_list|,
name|fileLen
argument_list|)
decl_stmt|;
name|StreamFile
operator|.
name|sendPartialData
argument_list|(
name|in
argument_list|,
name|os
argument_list|,
name|response
argument_list|,
name|fileLen
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// No ranges, so send entire file
name|response
operator|.
name|setHeader
argument_list|(
literal|"Content-Disposition"
argument_list|,
literal|"attachment; filename=\""
operator|+
name|filename
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/octet-stream"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
name|CONTENT_LENGTH
argument_list|,
literal|""
operator|+
name|fileLen
argument_list|)
expr_stmt|;
name|StreamFile
operator|.
name|copyFromOffset
argument_list|(
name|in
argument_list|,
name|os
argument_list|,
literal|0L
argument_list|,
name|fileLen
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"response.isCommitted()="
operator|+
name|response
operator|.
name|isCommitted
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Send a partial content response with the given range. If there are    * no satisfiable ranges, or if multiple ranges are requested, which    * is unsupported, respond with range not satisfiable.    *    * @param in stream to read from    * @param out stream to write to    * @param response http response to use    * @param contentLength for the response header    * @param ranges to write to respond with    * @throws IOException on error sending the response    */
DECL|method|sendPartialData (FSInputStream in, OutputStream out, HttpServletResponse response, long contentLength, List<?> ranges)
specifier|static
name|void
name|sendPartialData
parameter_list|(
name|FSInputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|long
name|contentLength
parameter_list|,
name|List
argument_list|<
name|?
argument_list|>
name|ranges
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ranges
operator|==
literal|null
operator|||
name|ranges
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|response
operator|.
name|setContentLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_REQUESTED_RANGE_NOT_SATISFIABLE
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
literal|"Content-Range"
argument_list|,
name|InclusiveByteRange
operator|.
name|to416HeaderRangeString
argument_list|(
name|contentLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|InclusiveByteRange
name|singleSatisfiableRange
init|=
operator|(
name|InclusiveByteRange
operator|)
name|ranges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|singleLength
init|=
name|singleSatisfiableRange
operator|.
name|getSize
argument_list|(
name|contentLength
argument_list|)
decl_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_PARTIAL_CONTENT
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
literal|"Content-Range"
argument_list|,
name|singleSatisfiableRange
operator|.
name|toHeaderRangeString
argument_list|(
name|contentLength
argument_list|)
argument_list|)
expr_stmt|;
name|copyFromOffset
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|singleSatisfiableRange
operator|.
name|getFirst
argument_list|(
name|contentLength
argument_list|)
argument_list|,
name|singleLength
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* Copy count bytes at the given offset from one stream to another */
DECL|method|copyFromOffset (FSInputStream in, OutputStream out, long offset, long count)
specifier|static
name|void
name|copyFromOffset
parameter_list|(
name|FSInputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

