begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|PrintStream
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
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|Token
import|;
end_import

begin_comment
comment|/**  * Renew delegation tokens over http for use in hftp.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|RenewDelegationTokenServlet
specifier|public
class|class
name|RenewDelegationTokenServlet
extends|extends
name|DfsServlet
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RenewDelegationTokenServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PATH_SPEC
specifier|public
specifier|static
specifier|final
name|String
name|PATH_SPEC
init|=
literal|"/renewDelegationToken"
decl_stmt|;
DECL|field|TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN
init|=
literal|"token"
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (final HttpServletRequest req, final HttpServletResponse resp)
specifier|protected
name|void
name|doGet
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
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
decl_stmt|;
try|try
block|{
name|ugi
operator|=
name|getUGI
argument_list|(
name|req
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Request for token received with no authentication from "
operator|+
name|req
operator|.
name|getRemoteAddr
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|resp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
literal|"Unable to identify or authenticate user"
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|NameNode
name|nn
init|=
operator|(
name|NameNode
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
literal|"name.node"
argument_list|)
decl_stmt|;
name|String
name|tokenString
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|TOKEN
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenString
operator|==
literal|null
condition|)
block|{
name|resp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_MULTIPLE_CHOICES
argument_list|,
literal|"Token to renew not specified"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|token
operator|.
name|decodeFromUrlString
argument_list|(
name|tokenString
argument_list|)
expr_stmt|;
try|try
block|{
name|long
name|result
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|Long
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|nn
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|PrintStream
name|os
init|=
operator|new
name|PrintStream
argument_list|(
name|resp
operator|.
name|getOutputStream
argument_list|()
argument_list|)
decl_stmt|;
name|os
operator|.
name|println
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// transfer exception over the http
name|String
name|exceptionClass
init|=
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|exceptionMsg
init|=
name|e
operator|.
name|getLocalizedMessage
argument_list|()
decl_stmt|;
name|String
name|strException
init|=
name|exceptionClass
operator|+
literal|";"
operator|+
name|exceptionMsg
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception while renewing token. Re-throwing. s="
operator|+
name|strException
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|resp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|strException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

