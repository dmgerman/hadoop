begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfsproxy
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfsproxy
package|;
end_package

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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|*
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_class
DECL|class|AuthorizationFilter
specifier|public
class|class
name|AuthorizationFilter
implements|implements
name|Filter
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AuthorizationFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HDFS_PATH_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|HDFS_PATH_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(^hdfs://([\\w\\-]+(\\.)?)+:\\d+|^hdfs://([\\w\\-]+(\\.)?)+)"
argument_list|)
decl_stmt|;
comment|/** Pattern for a filter to find out if a request is HFTP/HSFTP request */
DECL|field|HFTP_PATTERN
specifier|protected
specifier|static
specifier|final
name|Pattern
name|HFTP_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(/listPaths|/data|/streamFile|/file)$"
argument_list|)
decl_stmt|;
DECL|field|namenode
specifier|protected
name|String
name|namenode
decl_stmt|;
comment|/** {@inheritDoc} **/
DECL|method|init (FilterConfig filterConfig)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|ServletException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
literal|"hdfsproxy-default.xml"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
literal|"hdfsproxy-site.xml"
argument_list|)
expr_stmt|;
name|namenode
operator|=
name|conf
operator|.
name|get
argument_list|(
literal|"fs.default.name"
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} **/
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|doFilter (ServletRequest request, ServletResponse response, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|HttpServletResponse
name|rsp
init|=
operator|(
name|HttpServletResponse
operator|)
name|response
decl_stmt|;
name|HttpServletRequest
name|rqst
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
name|String
name|userId
init|=
name|getUserId
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|groups
init|=
name|getGroups
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|allowedPaths
init|=
name|getAllowedPaths
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|userId
argument_list|)
decl_stmt|;
name|String
name|filePath
init|=
name|getPathFromRequest
argument_list|(
name|rqst
argument_list|)
decl_stmt|;
if|if
condition|(
name|filePath
operator|==
literal|null
operator|||
operator|!
name|checkHdfsPath
argument_list|(
name|filePath
argument_list|,
name|allowedPaths
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"User "
operator|+
name|userId
operator|+
literal|" ("
operator|+
name|groups
operator|+
literal|") is not authorized to access path "
operator|+
name|filePath
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|request
operator|.
name|setAttribute
argument_list|(
literal|"authorized.ugi"
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"User: "
operator|+
name|userId
operator|+
literal|"("
operator|+
name|groups
operator|+
literal|") Request: "
operator|+
name|rqst
operator|.
name|getPathInfo
argument_list|()
operator|+
literal|" From: "
operator|+
name|rqst
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
expr_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|getUserId (ServletRequest rqst)
specifier|protected
name|String
name|getUserId
parameter_list|(
name|ServletRequest
name|rqst
parameter_list|)
block|{
name|String
name|userId
init|=
operator|(
name|String
operator|)
name|rqst
operator|.
name|getAttribute
argument_list|(
literal|"org.apache.hadoop.hdfsproxy.authorized.userID"
argument_list|)
decl_stmt|;
if|if
condition|(
name|userId
operator|!=
literal|null
condition|)
name|userId
operator|=
name|userId
operator|.
name|split
argument_list|(
literal|"[/@]"
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
return|return
name|userId
return|;
block|}
DECL|method|getGroups (ServletRequest request)
specifier|protected
name|String
name|getGroups
parameter_list|(
name|ServletRequest
name|request
parameter_list|)
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|getUserId
argument_list|(
name|request
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Arrays
operator|.
name|toString
argument_list|(
name|ugi
operator|.
name|getGroupNames
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getAllowedPaths (ServletRequest request)
specifier|protected
name|List
argument_list|<
name|Path
argument_list|>
name|getAllowedPaths
parameter_list|(
name|ServletRequest
name|request
parameter_list|)
block|{
return|return
operator|(
name|List
argument_list|<
name|Path
argument_list|>
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"org.apache.hadoop.hdfsproxy.authorized.paths"
argument_list|)
return|;
block|}
DECL|method|getPathFromRequest (HttpServletRequest rqst)
specifier|protected
name|String
name|getPathFromRequest
parameter_list|(
name|HttpServletRequest
name|rqst
parameter_list|)
block|{
name|String
name|filePath
init|=
literal|null
decl_stmt|;
comment|// check request path
name|String
name|servletPath
init|=
name|rqst
operator|.
name|getServletPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|HFTP_PATTERN
operator|.
name|matcher
argument_list|(
name|servletPath
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// file path as part of the URL
name|filePath
operator|=
name|rqst
operator|.
name|getPathInfo
argument_list|()
operator|!=
literal|null
condition|?
name|rqst
operator|.
name|getPathInfo
argument_list|()
else|:
literal|"/"
expr_stmt|;
block|}
return|return
name|filePath
return|;
block|}
comment|/** check that the requested path is listed in the ldap entry    * @param pathInfo - Path to check access    * @param ldapPaths - List of paths allowed access    * @return true if access allowed, false otherwise */
DECL|method|checkHdfsPath (String pathInfo, List<Path> ldapPaths)
specifier|public
name|boolean
name|checkHdfsPath
parameter_list|(
name|String
name|pathInfo
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|ldapPaths
parameter_list|)
block|{
if|if
condition|(
name|pathInfo
operator|==
literal|null
operator|||
name|pathInfo
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Can't get file path from the request"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
for|for
control|(
name|Path
name|ldapPathVar
range|:
name|ldapPaths
control|)
block|{
name|String
name|ldapPath
init|=
name|ldapPathVar
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|isPathQualified
argument_list|(
name|ldapPath
argument_list|)
operator|&&
name|isPathAuthroized
argument_list|(
name|ldapPath
argument_list|)
condition|)
block|{
name|String
name|allowedPath
init|=
name|extractPath
argument_list|(
name|ldapPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathInfo
operator|.
name|startsWith
argument_list|(
name|allowedPath
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
else|else
block|{
if|if
condition|(
name|pathInfo
operator|.
name|startsWith
argument_list|(
name|ldapPath
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|extractPath (String ldapPath)
specifier|private
name|String
name|extractPath
parameter_list|(
name|String
name|ldapPath
parameter_list|)
block|{
return|return
name|HDFS_PATH_PATTERN
operator|.
name|split
argument_list|(
name|ldapPath
argument_list|)
index|[
literal|1
index|]
return|;
block|}
DECL|method|isPathAuthroized (String pathStr)
specifier|private
name|boolean
name|isPathAuthroized
parameter_list|(
name|String
name|pathStr
parameter_list|)
block|{
name|Matcher
name|namenodeMatcher
init|=
name|HDFS_PATH_PATTERN
operator|.
name|matcher
argument_list|(
name|pathStr
argument_list|)
decl_stmt|;
return|return
name|namenodeMatcher
operator|.
name|find
argument_list|()
operator|&&
name|namenodeMatcher
operator|.
name|group
argument_list|()
operator|.
name|contains
argument_list|(
name|namenode
argument_list|)
return|;
block|}
DECL|method|isPathQualified (String pathStr)
specifier|private
name|boolean
name|isPathQualified
parameter_list|(
name|String
name|pathStr
parameter_list|)
block|{
if|if
condition|(
name|pathStr
operator|==
literal|null
operator|||
name|pathStr
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|HDFS_PATH_PATTERN
operator|.
name|matcher
argument_list|(
name|pathStr
argument_list|)
operator|.
name|find
argument_list|()
return|;
block|}
block|}
comment|/** {@inheritDoc} **/
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
block|}
end_class

end_unit

