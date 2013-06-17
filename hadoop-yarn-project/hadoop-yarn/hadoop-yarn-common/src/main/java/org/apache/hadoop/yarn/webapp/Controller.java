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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|join
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
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|ujoin
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
name|Map
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
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|DefaultPage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|Maps
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
name|servlet
operator|.
name|RequestScoped
import|;
end_import

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
DECL|class|Controller
specifier|public
specifier|abstract
class|class
name|Controller
implements|implements
name|Params
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Controller
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|jsonMapper
specifier|static
specifier|final
name|ObjectMapper
name|jsonMapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
annotation|@
name|RequestScoped
DECL|class|RequestContext
specifier|public
specifier|static
class|class
name|RequestContext
block|{
DECL|field|injector
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|field|request
specifier|final
name|HttpServletRequest
name|request
decl_stmt|;
DECL|field|response
specifier|final
name|HttpServletResponse
name|response
decl_stmt|;
DECL|field|moreParams
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moreParams
decl_stmt|;
DECL|field|cookies
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Cookie
argument_list|>
name|cookies
decl_stmt|;
DECL|field|status
name|int
name|status
init|=
literal|200
decl_stmt|;
comment|// pre 3.0 servlet-api doesn't have getStatus
DECL|field|rendered
name|boolean
name|rendered
init|=
literal|false
decl_stmt|;
DECL|field|error
name|Throwable
name|error
decl_stmt|;
DECL|field|devMode
name|boolean
name|devMode
init|=
literal|false
decl_stmt|;
DECL|field|prefix
name|String
name|prefix
decl_stmt|;
DECL|method|RequestContext (Injector injector, HttpServletRequest request, HttpServletResponse response)
annotation|@
name|Inject
name|RequestContext
parameter_list|(
name|Injector
name|injector
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
block|{
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
DECL|method|status ()
specifier|public
name|int
name|status
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|setStatus (int status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
DECL|method|setRendered (boolean rendered)
specifier|public
name|void
name|setRendered
parameter_list|(
name|boolean
name|rendered
parameter_list|)
block|{
name|this
operator|.
name|rendered
operator|=
name|rendered
expr_stmt|;
block|}
DECL|method|moreParams ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moreParams
parameter_list|()
block|{
if|if
condition|(
name|moreParams
operator|==
literal|null
condition|)
block|{
name|moreParams
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
return|return
name|moreParams
return|;
comment|// OK
block|}
DECL|method|cookies ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Cookie
argument_list|>
name|cookies
parameter_list|()
block|{
if|if
condition|(
name|cookies
operator|==
literal|null
condition|)
block|{
name|cookies
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|Cookie
index|[]
name|rcookies
init|=
name|request
operator|.
name|getCookies
argument_list|()
decl_stmt|;
if|if
condition|(
name|rcookies
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Cookie
name|cookie
range|:
name|rcookies
control|)
block|{
name|cookies
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
block|}
block|}
return|return
name|cookies
return|;
comment|// OK
block|}
DECL|method|set (String key, String value)
specifier|public
name|void
name|set
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|get (String key, String defaultValue)
specifier|public
name|String
name|get
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|moreParams
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
name|request
operator|.
name|getParameter
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|value
operator|==
literal|null
condition|?
name|defaultValue
else|:
name|value
return|;
block|}
DECL|method|prefix ()
specifier|public
name|String
name|prefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
block|}
DECL|field|context
specifier|private
name|RequestContext
name|context
decl_stmt|;
DECL|field|injector
annotation|@
name|Inject
name|Injector
name|injector
decl_stmt|;
DECL|method|Controller ()
specifier|public
name|Controller
parameter_list|()
block|{
comment|// Makes injection in subclasses optional.
comment|// Time will tell if this buy us more than the NPEs :)
block|}
DECL|method|Controller (RequestContext ctx)
specifier|public
name|Controller
parameter_list|(
name|RequestContext
name|ctx
parameter_list|)
block|{
name|context
operator|=
name|ctx
expr_stmt|;
block|}
DECL|method|context ()
specifier|public
name|RequestContext
name|context
parameter_list|()
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|injector
operator|==
literal|null
condition|)
block|{
comment|// One of the downsides of making injection in subclasses optional.
throw|throw
operator|new
name|WebAppException
argument_list|(
name|join
argument_list|(
literal|"Error accessing RequestContext from\n"
argument_list|,
literal|"a child constructor, either move the usage of the Controller\n"
argument_list|,
literal|"methods out of the constructor or inject the RequestContext\n"
argument_list|,
literal|"into the constructor"
argument_list|)
argument_list|)
throw|;
block|}
name|context
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|RequestContext
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|context
return|;
block|}
DECL|method|error ()
specifier|public
name|Throwable
name|error
parameter_list|()
block|{
return|return
name|context
argument_list|()
operator|.
name|error
return|;
block|}
DECL|method|status ()
specifier|public
name|int
name|status
parameter_list|()
block|{
return|return
name|context
argument_list|()
operator|.
name|status
return|;
block|}
DECL|method|setStatus (int status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|context
argument_list|()
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
DECL|method|inDevMode ()
specifier|public
name|boolean
name|inDevMode
parameter_list|()
block|{
return|return
name|context
argument_list|()
operator|.
name|devMode
return|;
block|}
DECL|method|injector ()
specifier|public
name|Injector
name|injector
parameter_list|()
block|{
return|return
name|context
argument_list|()
operator|.
name|injector
return|;
block|}
DECL|method|getInstance (Class<T> cls)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getInstance
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|cls
parameter_list|)
block|{
return|return
name|injector
operator|.
name|getInstance
argument_list|(
name|cls
argument_list|)
return|;
block|}
DECL|method|request ()
specifier|public
name|HttpServletRequest
name|request
parameter_list|()
block|{
return|return
name|context
argument_list|()
operator|.
name|request
return|;
block|}
DECL|method|response ()
specifier|public
name|HttpServletResponse
name|response
parameter_list|()
block|{
return|return
name|context
argument_list|()
operator|.
name|response
return|;
block|}
DECL|method|set (String key, String value)
specifier|public
name|void
name|set
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|context
argument_list|()
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|get (String key, String defaultValue)
specifier|public
name|String
name|get
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
name|context
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
DECL|method|$ (String key)
specifier|public
name|String
name|$
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|key
argument_list|,
literal|""
argument_list|)
return|;
block|}
DECL|method|setTitle (String title)
specifier|public
name|void
name|setTitle
parameter_list|(
name|String
name|title
parameter_list|)
block|{
name|set
argument_list|(
name|TITLE
argument_list|,
name|title
argument_list|)
expr_stmt|;
block|}
DECL|method|setTitle (String title, String url)
specifier|public
name|void
name|setTitle
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|setTitle
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|TITLE_LINK
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
DECL|method|info (String about)
specifier|public
name|ResponseInfo
name|info
parameter_list|(
name|String
name|about
parameter_list|)
block|{
return|return
name|getInstance
argument_list|(
name|ResponseInfo
operator|.
name|class
argument_list|)
operator|.
name|about
argument_list|(
name|about
argument_list|)
return|;
block|}
comment|/**    * Get the cookies    * @return the cookies map    */
DECL|method|cookies ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Cookie
argument_list|>
name|cookies
parameter_list|()
block|{
return|return
name|context
argument_list|()
operator|.
name|cookies
argument_list|()
return|;
block|}
comment|/**    * Create an url from url components    * @param parts components to join    * @return an url string    */
DECL|method|url (String... parts)
specifier|public
name|String
name|url
parameter_list|(
name|String
modifier|...
name|parts
parameter_list|)
block|{
return|return
name|ujoin
argument_list|(
name|context
argument_list|()
operator|.
name|prefix
argument_list|,
name|parts
argument_list|)
return|;
block|}
comment|/**    * The default action.    */
DECL|method|index ()
specifier|public
specifier|abstract
name|void
name|index
parameter_list|()
function_decl|;
DECL|method|echo ()
specifier|public
name|void
name|echo
parameter_list|()
block|{
name|render
argument_list|(
name|DefaultPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|render (Class<? extends View> cls)
specifier|protected
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
name|context
argument_list|()
operator|.
name|rendered
operator|=
literal|true
expr_stmt|;
name|getInstance
argument_list|(
name|cls
argument_list|)
operator|.
name|render
argument_list|()
expr_stmt|;
block|}
comment|/**    * Convenience method for REST APIs (without explicit views)    * @param object - the object as the response (in JSON)    */
DECL|method|renderJSON (Object object)
specifier|protected
name|void
name|renderJSON
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}: {}"
argument_list|,
name|MimeType
operator|.
name|JSON
argument_list|,
name|object
argument_list|)
expr_stmt|;
name|context
argument_list|()
operator|.
name|rendered
operator|=
literal|true
expr_stmt|;
name|context
argument_list|()
operator|.
name|response
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|JSON
argument_list|)
expr_stmt|;
try|try
block|{
name|jsonMapper
operator|.
name|writeValue
argument_list|(
name|writer
argument_list|()
argument_list|,
name|object
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|WebAppException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|renderJSON (Class<? extends ToJSON> cls)
specifier|protected
name|void
name|renderJSON
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|ToJSON
argument_list|>
name|cls
parameter_list|)
block|{
name|context
argument_list|()
operator|.
name|rendered
operator|=
literal|true
expr_stmt|;
name|response
argument_list|()
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|getInstance
argument_list|(
name|cls
argument_list|)
operator|.
name|toJSON
argument_list|(
name|writer
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convenience method for hello world :)    * @param s - the content to render as plain text    */
DECL|method|renderText (String s)
specifier|protected
name|void
name|renderText
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}: {}"
argument_list|,
name|MimeType
operator|.
name|TEXT
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|context
argument_list|()
operator|.
name|rendered
operator|=
literal|true
expr_stmt|;
name|response
argument_list|()
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|writer
argument_list|()
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|writer ()
specifier|protected
name|PrintWriter
name|writer
parameter_list|()
block|{
try|try
block|{
return|return
name|response
argument_list|()
operator|.
name|getWriter
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|WebAppException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

