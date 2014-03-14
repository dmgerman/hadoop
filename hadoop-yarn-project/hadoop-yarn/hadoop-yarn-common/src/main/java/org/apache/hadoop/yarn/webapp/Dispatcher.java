begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|HttpServlet
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
name|http
operator|.
name|HtmlQuoting
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
name|yarn
operator|.
name|webapp
operator|.
name|Controller
operator|.
name|RequestContext
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
name|yarn
operator|.
name|webapp
operator|.
name|Router
operator|.
name|Dest
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
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|ErrorPage
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_comment
comment|/**  * The servlet that dispatch request to various controllers  * according to the user defined routes in the router.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|Singleton
DECL|class|Dispatcher
specifier|public
class|class
name|Dispatcher
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Dispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ERROR_COOKIE
specifier|static
specifier|final
name|String
name|ERROR_COOKIE
init|=
literal|"last-error"
decl_stmt|;
DECL|field|STATUS_COOKIE
specifier|static
specifier|final
name|String
name|STATUS_COOKIE
init|=
literal|"last-status"
decl_stmt|;
DECL|field|injector
specifier|private
specifier|transient
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|field|router
specifier|private
specifier|transient
specifier|final
name|Router
name|router
decl_stmt|;
DECL|field|webApp
specifier|private
specifier|transient
specifier|final
name|WebApp
name|webApp
decl_stmt|;
DECL|field|devMode
specifier|private
specifier|volatile
name|boolean
name|devMode
init|=
literal|false
decl_stmt|;
annotation|@
name|Inject
DECL|method|Dispatcher (WebApp webApp, Injector injector, Router router)
name|Dispatcher
parameter_list|(
name|WebApp
name|webApp
parameter_list|,
name|Injector
name|injector
parameter_list|,
name|Router
name|router
parameter_list|)
block|{
name|this
operator|.
name|webApp
operator|=
name|webApp
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doOptions (HttpServletRequest req, HttpServletResponse res)
specifier|public
name|void
name|doOptions
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
block|{
comment|// for simplicity
name|res
operator|.
name|setHeader
argument_list|(
literal|"Allow"
argument_list|,
literal|"GET, POST"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|service (HttpServletRequest req, HttpServletResponse res)
specifier|public
name|void
name|service
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|res
operator|.
name|setCharacterEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|HtmlQuoting
operator|.
name|quoteHtmlChars
argument_list|(
name|req
operator|.
name|getRequestURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
literal|"/"
expr_stmt|;
block|}
if|if
condition|(
name|devMode
operator|&&
name|uri
operator|.
name|equals
argument_list|(
literal|"/__stop"
argument_list|)
condition|)
block|{
comment|// quick hack to restart servers in dev mode without OS commands
name|res
operator|.
name|setStatus
argument_list|(
name|res
operator|.
name|SC_NO_CONTENT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"dev mode restart requested"
argument_list|)
expr_stmt|;
name|prepareToExit
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// if they provide a redirectPath go there instead of going to
comment|// "/" so that filters can differentiate the webapps.
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|String
name|redirectPath
init|=
name|webApp
operator|.
name|getRedirectPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|redirectPath
operator|!=
literal|null
operator|&&
operator|!
name|redirectPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|res
operator|.
name|sendRedirect
argument_list|(
name|redirectPath
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|String
name|method
init|=
name|req
operator|.
name|getMethod
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"OPTIONS"
argument_list|)
condition|)
block|{
name|doOptions
argument_list|(
name|req
argument_list|,
name|res
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"TRACE"
argument_list|)
condition|)
block|{
name|doTrace
argument_list|(
name|req
argument_list|,
name|res
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"HEAD"
argument_list|)
condition|)
block|{
name|doGet
argument_list|(
name|req
argument_list|,
name|res
argument_list|)
expr_stmt|;
comment|// default to bad request
return|return;
block|}
name|String
name|pathInfo
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathInfo
operator|==
literal|null
condition|)
block|{
name|pathInfo
operator|=
literal|"/"
expr_stmt|;
block|}
name|Controller
operator|.
name|RequestContext
name|rc
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|Controller
operator|.
name|RequestContext
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|setCookieParams
argument_list|(
name|rc
argument_list|,
name|req
argument_list|)
operator|>
literal|0
condition|)
block|{
name|Cookie
name|ec
init|=
name|rc
operator|.
name|cookies
argument_list|()
operator|.
name|get
argument_list|(
name|ERROR_COOKIE
argument_list|)
decl_stmt|;
if|if
condition|(
name|ec
operator|!=
literal|null
condition|)
block|{
name|rc
operator|.
name|setStatus
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|rc
operator|.
name|cookies
argument_list|()
operator|.
name|get
argument_list|(
name|STATUS_COOKIE
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|removeErrorCookies
argument_list|(
name|res
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|rc
operator|.
name|set
argument_list|(
name|Params
operator|.
name|ERROR_DETAILS
argument_list|,
name|ec
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|ErrorPage
operator|.
name|class
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|rc
operator|.
name|prefix
operator|=
name|webApp
operator|.
name|name
argument_list|()
expr_stmt|;
name|Router
operator|.
name|Dest
name|dest
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dest
operator|=
name|router
operator|.
name|resolve
argument_list|(
name|method
argument_list|,
name|pathInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WebAppException
name|e
parameter_list|)
block|{
name|rc
operator|.
name|error
operator|=
name|e
expr_stmt|;
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not found"
argument_list|)
condition|)
block|{
name|rc
operator|.
name|setStatus
argument_list|(
name|res
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|ErrorPage
operator|.
name|class
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
block|{
name|rc
operator|.
name|setStatus
argument_list|(
name|res
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|ErrorPage
operator|.
name|class
argument_list|)
expr_stmt|;
return|return;
block|}
name|rc
operator|.
name|devMode
operator|=
name|devMode
expr_stmt|;
name|setMoreParams
argument_list|(
name|rc
argument_list|,
name|pathInfo
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|Controller
name|controller
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|dest
operator|.
name|controllerClass
argument_list|)
decl_stmt|;
try|try
block|{
comment|// TODO: support args converted from /path/:arg1/...
name|dest
operator|.
name|action
operator|.
name|invoke
argument_list|(
name|controller
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|rc
operator|.
name|rendered
condition|)
block|{
if|if
condition|(
name|dest
operator|.
name|defaultViewClass
operator|!=
literal|null
condition|)
block|{
name|render
argument_list|(
name|dest
operator|.
name|defaultViewClass
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rc
operator|.
name|status
operator|==
literal|200
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No view rendered for 200"
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error handling URI: "
operator|+
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// Page could be half rendered (but still not flushed). So redirect.
name|redirectToErrorPage
argument_list|(
name|res
argument_list|,
name|e
argument_list|,
name|uri
argument_list|,
name|devMode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|redirectToErrorPage (HttpServletResponse res, Throwable e, String path, boolean devMode)
specifier|public
specifier|static
name|void
name|redirectToErrorPage
parameter_list|(
name|HttpServletResponse
name|res
parameter_list|,
name|Throwable
name|e
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|devMode
parameter_list|)
block|{
name|String
name|st
init|=
name|devMode
condition|?
name|ErrorPage
operator|.
name|toStackTrace
argument_list|(
name|e
argument_list|,
literal|1024
operator|*
literal|3
argument_list|)
comment|// spec: min 4KB
else|:
literal|"See logs for stack trace"
decl_stmt|;
name|res
operator|.
name|setStatus
argument_list|(
name|res
operator|.
name|SC_FOUND
argument_list|)
expr_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
name|STATUS_COOKIE
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|500
argument_list|)
argument_list|)
decl_stmt|;
name|cookie
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|res
operator|.
name|addCookie
argument_list|(
name|cookie
argument_list|)
expr_stmt|;
name|cookie
operator|=
operator|new
name|Cookie
argument_list|(
name|ERROR_COOKIE
argument_list|,
name|st
argument_list|)
expr_stmt|;
name|cookie
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|res
operator|.
name|addCookie
argument_list|(
name|cookie
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
literal|"Location"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|removeErrorCookies (HttpServletResponse res, String path)
specifier|public
specifier|static
name|void
name|removeErrorCookies
parameter_list|(
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|removeCookie
argument_list|(
name|res
argument_list|,
name|ERROR_COOKIE
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|removeCookie
argument_list|(
name|res
argument_list|,
name|STATUS_COOKIE
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|removeCookie (HttpServletResponse res, String name, String path)
specifier|public
specifier|static
name|void
name|removeCookie
parameter_list|(
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing cookie {} on {}"
argument_list|,
name|name
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|Cookie
name|c
init|=
operator|new
name|Cookie
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|c
operator|.
name|setMaxAge
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|c
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|res
operator|.
name|addCookie
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|render (Class<? extends View> cls)
specifier|private
name|void
name|render
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|cls
parameter_list|)
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|cls
argument_list|)
operator|.
name|render
argument_list|()
expr_stmt|;
block|}
comment|// /path/foo/bar with /path/:arg1/:arg2 will set {arg1=>foo, arg2=>bar}
DECL|method|setMoreParams (RequestContext rc, String pathInfo, Dest dest)
specifier|private
name|void
name|setMoreParams
parameter_list|(
name|RequestContext
name|rc
parameter_list|,
name|String
name|pathInfo
parameter_list|,
name|Dest
name|dest
parameter_list|)
block|{
name|checkState
argument_list|(
name|pathInfo
operator|.
name|startsWith
argument_list|(
name|dest
operator|.
name|prefix
argument_list|)
argument_list|,
literal|"prefix should match"
argument_list|)
expr_stmt|;
if|if
condition|(
name|dest
operator|.
name|pathParams
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
name|dest
operator|.
name|prefix
operator|.
name|length
argument_list|()
operator|==
name|pathInfo
operator|.
name|length
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
index|[]
name|parts
init|=
name|Iterables
operator|.
name|toArray
argument_list|(
name|WebApp
operator|.
name|pathSplitter
operator|.
name|split
argument_list|(
name|pathInfo
operator|.
name|substring
argument_list|(
name|dest
operator|.
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"parts={}, params={}"
argument_list|,
name|parts
argument_list|,
name|dest
operator|.
name|pathParams
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dest
operator|.
name|pathParams
operator|.
name|size
argument_list|()
operator|&&
name|i
operator|<
name|parts
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|String
name|key
init|=
name|dest
operator|.
name|pathParams
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|':'
condition|)
block|{
name|rc
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|parts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setCookieParams (RequestContext rc, HttpServletRequest req)
specifier|private
name|int
name|setCookieParams
parameter_list|(
name|RequestContext
name|rc
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|Cookie
index|[]
name|cookies
init|=
name|req
operator|.
name|getCookies
argument_list|()
decl_stmt|;
if|if
condition|(
name|cookies
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Cookie
name|cookie
range|:
name|cookies
control|)
block|{
name|rc
operator|.
name|cookies
argument_list|()
operator|.
name|put
argument_list|(
name|cookie
operator|.
name|getName
argument_list|()
argument_list|,
name|cookie
argument_list|)
expr_stmt|;
block|}
return|return
name|cookies
operator|.
name|length
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|setDevMode (boolean choice)
specifier|public
name|void
name|setDevMode
parameter_list|(
name|boolean
name|choice
parameter_list|)
block|{
name|devMode
operator|=
name|choice
expr_stmt|;
block|}
DECL|method|prepareToExit ()
specifier|private
name|void
name|prepareToExit
parameter_list|()
block|{
name|checkState
argument_list|(
name|devMode
argument_list|,
literal|"only in dev mode"
argument_list|)
expr_stmt|;
operator|new
name|Timer
argument_list|(
literal|"webapp exit"
argument_list|,
literal|true
argument_list|)
operator|.
name|schedule
argument_list|(
operator|new
name|TimerTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"WebAppp /{} exiting..."
argument_list|,
name|webApp
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|webApp
operator|.
name|stop
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// FINDBUG: this is intended in dev mode
block|}
block|}
argument_list|,
literal|18
argument_list|)
expr_stmt|;
comment|// enough time for the last local request to complete
block|}
block|}
end_class

end_unit

