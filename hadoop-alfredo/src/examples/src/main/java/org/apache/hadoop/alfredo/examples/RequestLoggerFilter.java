begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.alfredo.examples
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|alfredo
operator|.
name|examples
package|;
end_package

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
name|javax
operator|.
name|servlet
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
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
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
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
name|Cookie
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
name|HttpServletRequestWrapper
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponseWrapper
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
name|ArrayList
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
name|HashMap
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Servlet filter that logs HTTP request/response headers  */
end_comment

begin_class
DECL|class|RequestLoggerFilter
specifier|public
class|class
name|RequestLoggerFilter
implements|implements
name|Filter
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RequestLoggerFilter
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
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
block|{   }
annotation|@
name|Override
DECL|method|doFilter (ServletRequest request, ServletResponse response, FilterChain filterChain)
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
name|filterChain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
if|if
condition|(
operator|!
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|filterChain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XHttpServletRequest
name|xRequest
init|=
operator|new
name|XHttpServletRequest
argument_list|(
operator|(
name|HttpServletRequest
operator|)
name|request
argument_list|)
decl_stmt|;
name|XHttpServletResponse
name|xResponse
init|=
operator|new
name|XHttpServletResponse
argument_list|(
operator|(
name|HttpServletResponse
operator|)
name|response
argument_list|)
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|xRequest
operator|.
name|getResquestInfo
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|filterChain
operator|.
name|doFilter
argument_list|(
name|xRequest
argument_list|,
name|xResponse
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|xResponse
operator|.
name|getResponseInfo
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
DECL|class|XHttpServletRequest
specifier|private
specifier|static
class|class
name|XHttpServletRequest
extends|extends
name|HttpServletRequestWrapper
block|{
DECL|method|XHttpServletRequest (HttpServletRequest request)
specifier|public
name|XHttpServletRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|getResquestInfo ()
specifier|public
name|StringBuffer
name|getResquestInfo
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|512
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
operator|.
name|append
argument_list|(
name|getMethod
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|getRequestURL
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getQueryString
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
operator|.
name|append
argument_list|(
name|getQueryString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|Enumeration
name|names
init|=
name|getHeaderNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Enumeration
name|values
init|=
name|getHeaders
argument_list|(
name|name
argument_list|)
decl_stmt|;
while|while
condition|(
name|values
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|values
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|sb
return|;
block|}
block|}
DECL|class|XHttpServletResponse
specifier|private
specifier|static
class|class
name|XHttpServletResponse
extends|extends
name|HttpServletResponseWrapper
block|{
DECL|field|headers
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|status
specifier|private
name|int
name|status
decl_stmt|;
DECL|field|message
specifier|private
name|String
name|message
decl_stmt|;
DECL|method|XHttpServletResponse (HttpServletResponse response)
specifier|public
name|XHttpServletResponse
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|)
block|{
name|super
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|getHeaderValues (String name, boolean reset)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getHeaderValues
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|reset
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|headers
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|reset
operator|||
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|addCookie (Cookie cookie)
specifier|public
name|void
name|addCookie
parameter_list|(
name|Cookie
name|cookie
parameter_list|)
block|{
name|super
operator|.
name|addCookie
argument_list|(
name|cookie
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|cookies
init|=
name|getHeaderValues
argument_list|(
literal|"Set-Cookie"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cookies
operator|.
name|add
argument_list|(
name|cookie
operator|.
name|getName
argument_list|()
operator|+
literal|"="
operator|+
name|cookie
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendError (int sc, String msg)
specifier|public
name|void
name|sendError
parameter_list|(
name|int
name|sc
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|sendError
argument_list|(
name|sc
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|status
operator|=
name|sc
expr_stmt|;
name|message
operator|=
name|msg
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendError (int sc)
specifier|public
name|void
name|sendError
parameter_list|(
name|int
name|sc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|sendError
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|status
operator|=
name|sc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setStatus (int sc)
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|sc
parameter_list|)
block|{
name|super
operator|.
name|setStatus
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|status
operator|=
name|sc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setStatus (int sc, String msg)
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|sc
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
operator|.
name|setStatus
argument_list|(
name|sc
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|status
operator|=
name|sc
expr_stmt|;
name|message
operator|=
name|msg
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setHeader (String name, String value)
specifier|public
name|void
name|setHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
operator|.
name|setHeader
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|getHeaderValues
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addHeader (String name, String value)
specifier|public
name|void
name|addHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
operator|.
name|addHeader
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|getHeaderValues
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getResponseInfo ()
specifier|public
name|StringBuffer
name|getResponseInfo
parameter_list|()
block|{
if|if
condition|(
name|status
operator|==
literal|0
condition|)
block|{
name|status
operator|=
literal|200
expr_stmt|;
name|message
operator|=
literal|"OK"
expr_stmt|;
block|}
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|512
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"< "
argument_list|)
operator|.
name|append
argument_list|(
literal|"status code: "
argument_list|)
operator|.
name|append
argument_list|(
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", message: "
argument_list|)
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|headers
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|value
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"< "
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
return|return
name|sb
return|;
block|}
block|}
block|}
end_class

end_unit

