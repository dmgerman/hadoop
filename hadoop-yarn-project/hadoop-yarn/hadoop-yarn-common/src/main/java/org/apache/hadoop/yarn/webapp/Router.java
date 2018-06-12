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
name|checkNotNull
import|;
end_import

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
name|djoin
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
name|pjoin
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|base
operator|.
name|CharMatcher
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
name|ImmutableList
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

begin_comment
comment|/**  * Manages path info to controller#action routing.  */
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
DECL|class|Router
class|class
name|Router
block|{
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
name|Router
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EMPTY_LIST
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|EMPTY_LIST
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|SLASH
specifier|static
specifier|final
name|CharMatcher
name|SLASH
init|=
name|CharMatcher
operator|.
name|is
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
DECL|field|controllerRe
specifier|static
specifier|final
name|Pattern
name|controllerRe
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^/[A-Za-z_]\\w*(?:/.*)?"
argument_list|)
decl_stmt|;
DECL|class|Dest
specifier|static
class|class
name|Dest
block|{
DECL|field|prefix
specifier|final
name|String
name|prefix
decl_stmt|;
DECL|field|pathParams
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|pathParams
decl_stmt|;
DECL|field|action
specifier|final
name|Method
name|action
decl_stmt|;
DECL|field|controllerClass
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Controller
argument_list|>
name|controllerClass
decl_stmt|;
DECL|field|defaultViewClass
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|defaultViewClass
decl_stmt|;
DECL|field|methods
specifier|final
name|EnumSet
argument_list|<
name|WebApp
operator|.
name|HTTP
argument_list|>
name|methods
decl_stmt|;
DECL|method|Dest (String path, Method method, Class<? extends Controller> cls, List<String> pathParams, WebApp.HTTP httpMethod)
name|Dest
parameter_list|(
name|String
name|path
parameter_list|,
name|Method
name|method
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Controller
argument_list|>
name|cls
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|pathParams
parameter_list|,
name|WebApp
operator|.
name|HTTP
name|httpMethod
parameter_list|)
block|{
name|prefix
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|action
operator|=
name|checkNotNull
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|controllerClass
operator|=
name|checkNotNull
argument_list|(
name|cls
argument_list|)
expr_stmt|;
name|this
operator|.
name|pathParams
operator|=
name|pathParams
operator|!=
literal|null
condition|?
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|pathParams
argument_list|)
else|:
name|EMPTY_LIST
expr_stmt|;
name|methods
operator|=
name|EnumSet
operator|.
name|of
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|hostClass
name|Class
argument_list|<
name|?
argument_list|>
name|hostClass
decl_stmt|;
comment|// starting point to look for default classes
DECL|field|routes
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Dest
argument_list|>
name|routes
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
comment|// path->dest
DECL|method|add (WebApp.HTTP httpMethod, String path, Class<? extends Controller> cls, String action, List<String> names)
specifier|synchronized
name|Dest
name|add
parameter_list|(
name|WebApp
operator|.
name|HTTP
name|httpMethod
parameter_list|,
name|String
name|path
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Controller
argument_list|>
name|cls
parameter_list|,
name|String
name|action
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
return|return
name|addWithOptionalDefaultView
argument_list|(
name|httpMethod
argument_list|,
name|path
argument_list|,
name|cls
argument_list|,
name|action
argument_list|,
name|names
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|addWithoutDefaultView (WebApp.HTTP httpMethod, String path, Class<? extends Controller> cls, String action, List<String> names)
specifier|synchronized
name|Dest
name|addWithoutDefaultView
parameter_list|(
name|WebApp
operator|.
name|HTTP
name|httpMethod
parameter_list|,
name|String
name|path
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Controller
argument_list|>
name|cls
parameter_list|,
name|String
name|action
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
return|return
name|addWithOptionalDefaultView
argument_list|(
name|httpMethod
argument_list|,
name|path
argument_list|,
name|cls
argument_list|,
name|action
argument_list|,
name|names
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Add a route to the router.    * e.g., add(GET, "/foo/show", FooController.class, "show", [name...]);    * The name list is from /foo/show/:name/...    */
DECL|method|addWithOptionalDefaultView (WebApp.HTTP httpMethod, String path, Class<? extends Controller> cls, String action, List<String> names, boolean defaultViewNeeded)
specifier|synchronized
name|Dest
name|addWithOptionalDefaultView
parameter_list|(
name|WebApp
operator|.
name|HTTP
name|httpMethod
parameter_list|,
name|String
name|path
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Controller
argument_list|>
name|cls
parameter_list|,
name|String
name|action
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|boolean
name|defaultViewNeeded
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"adding {}({})->{}#{}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|path
block|,
name|names
block|,
name|cls
block|,
name|action
block|}
argument_list|)
expr_stmt|;
name|Dest
name|dest
init|=
name|addController
argument_list|(
name|httpMethod
argument_list|,
name|path
argument_list|,
name|cls
argument_list|,
name|action
argument_list|,
name|names
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultViewNeeded
condition|)
block|{
name|addDefaultView
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
DECL|method|addController (WebApp.HTTP httpMethod, String path, Class<? extends Controller> cls, String action, List<String> names)
specifier|private
name|Dest
name|addController
parameter_list|(
name|WebApp
operator|.
name|HTTP
name|httpMethod
parameter_list|,
name|String
name|path
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Controller
argument_list|>
name|cls
parameter_list|,
name|String
name|action
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
try|try
block|{
comment|// Look for the method in all public methods declared in the class
comment|// or inherited by the class.
comment|// Note: this does not distinguish methods with the same signature
comment|// but different return types.
comment|// TODO: We may want to deal with methods that take parameters in the future
name|Method
name|method
init|=
name|cls
operator|.
name|getMethod
argument_list|(
name|action
argument_list|)
decl_stmt|;
name|Dest
name|dest
init|=
name|routes
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
block|{
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// avoid any runtime checks
name|dest
operator|=
operator|new
name|Dest
argument_list|(
name|path
argument_list|,
name|method
argument_list|,
name|cls
argument_list|,
name|names
argument_list|,
name|httpMethod
argument_list|)
expr_stmt|;
name|routes
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|dest
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
name|dest
operator|.
name|methods
operator|.
name|add
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
throw|throw
operator|new
name|WebAppException
argument_list|(
name|action
operator|+
literal|"() not found in "
operator|+
name|cls
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|WebAppException
argument_list|(
literal|"Security exception thrown for "
operator|+
name|action
operator|+
literal|"() in "
operator|+
name|cls
argument_list|)
throw|;
block|}
block|}
DECL|method|addDefaultView (Dest dest)
specifier|private
name|void
name|addDefaultView
parameter_list|(
name|Dest
name|dest
parameter_list|)
block|{
name|String
name|controllerName
init|=
name|dest
operator|.
name|controllerClass
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|controllerName
operator|.
name|endsWith
argument_list|(
literal|"Controller"
argument_list|)
condition|)
block|{
name|controllerName
operator|=
name|controllerName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|controllerName
operator|.
name|length
argument_list|()
operator|-
literal|10
argument_list|)
expr_stmt|;
block|}
name|dest
operator|.
name|defaultViewClass
operator|=
name|find
argument_list|(
name|View
operator|.
name|class
argument_list|,
name|dest
operator|.
name|controllerClass
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|join
argument_list|(
name|controllerName
operator|+
literal|"View"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setHostClass (Class<?> cls)
name|void
name|setHostClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|cls
parameter_list|)
block|{
name|hostClass
operator|=
name|cls
expr_stmt|;
block|}
comment|/**    * Resolve a path to a destination.    */
DECL|method|resolve (String httpMethod, String path)
specifier|synchronized
name|Dest
name|resolve
parameter_list|(
name|String
name|httpMethod
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|WebApp
operator|.
name|HTTP
name|method
init|=
name|WebApp
operator|.
name|HTTP
operator|.
name|valueOf
argument_list|(
name|httpMethod
argument_list|)
decl_stmt|;
comment|// can throw
name|Dest
name|dest
init|=
name|lookupRoute
argument_list|(
name|method
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
block|{
return|return
name|resolveDefault
argument_list|(
name|method
argument_list|,
name|path
argument_list|)
return|;
block|}
return|return
name|dest
return|;
block|}
DECL|method|lookupRoute (WebApp.HTTP method, String path)
specifier|private
name|Dest
name|lookupRoute
parameter_list|(
name|WebApp
operator|.
name|HTTP
name|method
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|String
name|key
init|=
name|path
decl_stmt|;
do|do
block|{
name|Dest
name|dest
init|=
name|routes
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
operator|&&
name|methodAllowed
argument_list|(
name|method
argument_list|,
name|dest
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
name|Object
operator|)
name|key
operator|==
name|path
condition|)
block|{
comment|// shut up warnings
name|LOG
operator|.
name|debug
argument_list|(
literal|"exact match for {}: {}"
argument_list|,
name|key
argument_list|,
name|dest
operator|.
name|action
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
elseif|else
if|if
condition|(
name|isGoodMatch
argument_list|(
name|dest
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"prefix match2 for {}: {}"
argument_list|,
name|key
argument_list|,
name|dest
operator|.
name|action
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
return|return
name|resolveAction
argument_list|(
name|method
argument_list|,
name|dest
argument_list|,
name|path
argument_list|)
return|;
block|}
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Dest
argument_list|>
name|lower
init|=
name|routes
operator|.
name|lowerEntry
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|lower
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|dest
operator|=
name|lower
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefixMatches
argument_list|(
name|dest
argument_list|,
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|methodAllowed
argument_list|(
name|method
argument_list|,
name|dest
argument_list|)
condition|)
block|{
if|if
condition|(
name|isGoodMatch
argument_list|(
name|dest
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"prefix match for {}: {}"
argument_list|,
name|lower
operator|.
name|getKey
argument_list|()
argument_list|,
name|dest
operator|.
name|action
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
return|return
name|resolveAction
argument_list|(
name|method
argument_list|,
name|dest
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|// check other candidates
name|int
name|slashPos
init|=
name|key
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|key
operator|=
name|slashPos
operator|>
literal|0
condition|?
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slashPos
argument_list|)
else|:
literal|"/"
expr_stmt|;
block|}
else|else
block|{
name|key
operator|=
literal|"/"
expr_stmt|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
DECL|method|methodAllowed (WebApp.HTTP method, Dest dest)
specifier|static
name|boolean
name|methodAllowed
parameter_list|(
name|WebApp
operator|.
name|HTTP
name|method
parameter_list|,
name|Dest
name|dest
parameter_list|)
block|{
comment|// Accept all methods by default, unless explicity configured otherwise.
return|return
name|dest
operator|.
name|methods
operator|.
name|contains
argument_list|(
name|method
argument_list|)
operator|||
operator|(
name|dest
operator|.
name|methods
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|dest
operator|.
name|methods
operator|.
name|contains
argument_list|(
name|WebApp
operator|.
name|HTTP
operator|.
name|GET
argument_list|)
operator|)
return|;
block|}
DECL|method|prefixMatches (Dest dest, String path)
specifier|static
name|boolean
name|prefixMatches
parameter_list|(
name|Dest
name|dest
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"checking prefix {}{} for path: {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|dest
operator|.
name|prefix
block|,
name|dest
operator|.
name|pathParams
block|,
name|path
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
name|dest
operator|.
name|prefix
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|prefixLen
init|=
name|dest
operator|.
name|prefix
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefixLen
operator|>
literal|1
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|>
name|prefixLen
operator|&&
name|path
operator|.
name|charAt
argument_list|(
name|prefixLen
argument_list|)
operator|!=
literal|'/'
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// prefix is / or prefix is path or prefix/...
return|return
literal|true
return|;
block|}
DECL|method|isGoodMatch (Dest dest, String path)
specifier|static
name|boolean
name|isGoodMatch
parameter_list|(
name|Dest
name|dest
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|SLASH
operator|.
name|countIn
argument_list|(
name|dest
operator|.
name|prefix
argument_list|)
operator|>
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// We want to match (/foo, :a) for /foo/bar/blah and (/, :a) for /123
comment|// but NOT / for /foo or (/, :a) for /foo or /foo/ because default route
comment|// (FooController#index) for /foo and /foo/ takes precedence.
if|if
condition|(
name|dest
operator|.
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|dest
operator|.
name|pathParams
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|maybeController
argument_list|(
name|path
argument_list|)
return|;
block|}
return|return
name|dest
operator|.
name|pathParams
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
comment|// /foo should match /foo/
operator|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|SLASH
operator|.
name|countIn
argument_list|(
name|path
argument_list|)
operator|==
literal|2
operator|)
return|;
block|}
DECL|method|maybeController (String path)
specifier|static
name|boolean
name|maybeController
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|controllerRe
operator|.
name|matcher
argument_list|(
name|path
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
comment|// Assume /controller/action style path
DECL|method|resolveDefault (WebApp.HTTP method, String path)
specifier|private
name|Dest
name|resolveDefault
parameter_list|(
name|WebApp
operator|.
name|HTTP
name|method
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|WebApp
operator|.
name|parseRoute
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|controller
init|=
name|parts
operator|.
name|get
argument_list|(
name|WebApp
operator|.
name|R_CONTROLLER
argument_list|)
decl_stmt|;
name|String
name|action
init|=
name|parts
operator|.
name|get
argument_list|(
name|WebApp
operator|.
name|R_ACTION
argument_list|)
decl_stmt|;
comment|// NameController is encouraged default
name|Class
argument_list|<
name|?
extends|extends
name|Controller
argument_list|>
name|cls
init|=
name|find
argument_list|(
name|Controller
operator|.
name|class
argument_list|,
name|join
argument_list|(
name|controller
argument_list|,
literal|"Controller"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|cls
operator|==
literal|null
condition|)
block|{
name|cls
operator|=
name|find
argument_list|(
name|Controller
operator|.
name|class
argument_list|,
name|controller
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cls
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|WebAppException
argument_list|(
name|join
argument_list|(
name|path
argument_list|,
literal|": controller for "
argument_list|,
name|controller
argument_list|,
literal|" not found"
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|add
argument_list|(
name|method
argument_list|,
name|defaultPrefix
argument_list|(
name|controller
argument_list|,
name|action
argument_list|)
argument_list|,
name|cls
argument_list|,
name|action
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|defaultPrefix (String controller, String action)
specifier|private
name|String
name|defaultPrefix
parameter_list|(
name|String
name|controller
parameter_list|,
name|String
name|action
parameter_list|)
block|{
if|if
condition|(
name|controller
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
operator|&&
name|action
operator|.
name|equals
argument_list|(
literal|"index"
argument_list|)
condition|)
block|{
return|return
literal|"/"
return|;
block|}
if|if
condition|(
name|action
operator|.
name|equals
argument_list|(
literal|"index"
argument_list|)
condition|)
block|{
return|return
name|join
argument_list|(
literal|'/'
argument_list|,
name|controller
argument_list|)
return|;
block|}
return|return
name|pjoin
argument_list|(
literal|""
argument_list|,
name|controller
argument_list|,
name|action
argument_list|)
return|;
block|}
DECL|method|find (Class<T> cls, String cname)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|find
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|cls
parameter_list|,
name|String
name|cname
parameter_list|)
block|{
name|String
name|pkg
init|=
name|hostClass
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|find
argument_list|(
name|cls
argument_list|,
name|pkg
argument_list|,
name|cname
argument_list|)
return|;
block|}
DECL|method|find (Class<T> cls, String pkg, String cname)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|find
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|cls
parameter_list|,
name|String
name|pkg
parameter_list|,
name|String
name|cname
parameter_list|)
block|{
name|String
name|name
init|=
name|StringUtils
operator|.
name|capitalize
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|found
init|=
name|load
argument_list|(
name|cls
argument_list|,
name|djoin
argument_list|(
name|pkg
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
operator|==
literal|null
condition|)
block|{
name|found
operator|=
name|load
argument_list|(
name|cls
argument_list|,
name|djoin
argument_list|(
name|pkg
argument_list|,
literal|"webapp"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|found
operator|==
literal|null
condition|)
block|{
name|found
operator|=
name|load
argument_list|(
name|cls
argument_list|,
name|join
argument_list|(
name|hostClass
operator|.
name|getName
argument_list|()
argument_list|,
literal|'$'
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|found
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|load (Class<T> cls, String className)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|load
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|cls
parameter_list|,
name|String
name|className
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"trying: {}"
argument_list|,
name|className
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|found
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|cls
operator|.
name|isAssignableFrom
argument_list|(
name|found
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"found {}"
argument_list|,
name|className
argument_list|)
expr_stmt|;
return|return
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
operator|)
name|found
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"found a {} but it's not a {}"
argument_list|,
name|className
argument_list|,
name|cls
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
comment|// OK in this case.
block|}
return|return
literal|null
return|;
block|}
comment|// Dest may contain a candidate controller
DECL|method|resolveAction (WebApp.HTTP method, Dest dest, String path)
specifier|private
name|Dest
name|resolveAction
parameter_list|(
name|WebApp
operator|.
name|HTTP
name|method
parameter_list|,
name|Dest
name|dest
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|dest
operator|.
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
name|checkState
argument_list|(
operator|!
name|isGoodMatch
argument_list|(
name|dest
argument_list|,
name|path
argument_list|)
argument_list|,
name|dest
operator|.
name|prefix
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|SLASH
operator|.
name|countIn
argument_list|(
name|path
argument_list|)
operator|>
literal|1
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|WebApp
operator|.
name|parseRoute
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|controller
init|=
name|parts
operator|.
name|get
argument_list|(
name|WebApp
operator|.
name|R_CONTROLLER
argument_list|)
decl_stmt|;
name|String
name|action
init|=
name|parts
operator|.
name|get
argument_list|(
name|WebApp
operator|.
name|R_ACTION
argument_list|)
decl_stmt|;
return|return
name|add
argument_list|(
name|method
argument_list|,
name|pjoin
argument_list|(
literal|""
argument_list|,
name|controller
argument_list|,
name|action
argument_list|)
argument_list|,
name|dest
operator|.
name|controllerClass
argument_list|,
name|action
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

