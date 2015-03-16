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
name|ConnectException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
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
name|http
operator|.
name|HttpConfig
operator|.
name|Policy
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
name|HttpServer2
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
name|authorize
operator|.
name|AccessControlList
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
name|conf
operator|.
name|YarnConfiguration
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
name|util
operator|.
name|WebAppUtils
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
name|inject
operator|.
name|AbstractModule
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
name|Guice
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
name|GuiceFilter
import|;
end_import

begin_comment
comment|/**  * Helpers to create an embedded webapp.  *  *<b>Quick start:</b>  *<pre>  *   WebApp wa = WebApps.$for(myApp).start();</pre>  * Starts a webapp with default routes binds to 0.0.0.0 (all network interfaces)  * on an ephemeral port, which can be obtained with:<pre>  *   int port = wa.port();</pre>  *<b>With more options:</b>  *<pre>  *   WebApp wa = WebApps.$for(myApp).at(address, port).  *                        with(configuration).  *                        start(new WebApp() {  *&#064;Override public void setup() {  *       route("/foo/action", FooController.class);  *       route("/foo/:id", FooController.class, "show");  *     }  *   });</pre>  */
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
DECL|class|WebApps
specifier|public
class|class
name|WebApps
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
name|WebApps
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
parameter_list|<
name|T
parameter_list|>
block|{
DECL|class|ServletStruct
specifier|static
class|class
name|ServletStruct
block|{
DECL|field|clazz
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|HttpServlet
argument_list|>
name|clazz
decl_stmt|;
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|spec
specifier|public
name|String
name|spec
decl_stmt|;
block|}
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|wsName
specifier|final
name|String
name|wsName
decl_stmt|;
DECL|field|api
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|api
decl_stmt|;
DECL|field|application
specifier|final
name|T
name|application
decl_stmt|;
DECL|field|bindAddress
name|String
name|bindAddress
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|port
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|findPort
name|boolean
name|findPort
init|=
literal|false
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|httpPolicy
name|Policy
name|httpPolicy
init|=
literal|null
decl_stmt|;
DECL|field|devMode
name|boolean
name|devMode
init|=
literal|false
decl_stmt|;
DECL|field|spnegoPrincipalKey
specifier|private
name|String
name|spnegoPrincipalKey
decl_stmt|;
DECL|field|spnegoKeytabKey
specifier|private
name|String
name|spnegoKeytabKey
decl_stmt|;
DECL|field|servlets
specifier|private
specifier|final
name|HashSet
argument_list|<
name|ServletStruct
argument_list|>
name|servlets
init|=
operator|new
name|HashSet
argument_list|<
name|ServletStruct
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|attributes
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Builder (String name, Class<T> api, T application, String wsName)
name|Builder
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|api
parameter_list|,
name|T
name|application
parameter_list|,
name|String
name|wsName
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|api
operator|=
name|api
expr_stmt|;
name|this
operator|.
name|application
operator|=
name|application
expr_stmt|;
name|this
operator|.
name|wsName
operator|=
name|wsName
expr_stmt|;
block|}
DECL|method|Builder (String name, Class<T> api, T application)
name|Builder
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|api
parameter_list|,
name|T
name|application
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|api
argument_list|,
name|application
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|at (String bindAddress)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|at
parameter_list|(
name|String
name|bindAddress
parameter_list|)
block|{
name|String
index|[]
name|parts
init|=
name|StringUtils
operator|.
name|split
argument_list|(
name|bindAddress
argument_list|,
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
return|return
name|at
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|port
argument_list|,
name|port
operator|==
literal|0
argument_list|)
return|;
block|}
return|return
name|at
argument_list|(
name|bindAddress
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|at (int port)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|at
parameter_list|(
name|int
name|port
parameter_list|)
block|{
return|return
name|at
argument_list|(
literal|"0.0.0.0"
argument_list|,
name|port
argument_list|,
name|port
operator|==
literal|0
argument_list|)
return|;
block|}
DECL|method|at (String address, int port, boolean findPort)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|at
parameter_list|(
name|String
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|boolean
name|findPort
parameter_list|)
block|{
name|this
operator|.
name|bindAddress
operator|=
name|checkNotNull
argument_list|(
name|address
argument_list|,
literal|"bind address"
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|findPort
operator|=
name|findPort
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withAttribute (String key, Object value)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|withAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|attributes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withServlet (String name, String pathSpec, Class<? extends HttpServlet> servlet)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|withServlet
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|pathSpec
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|HttpServlet
argument_list|>
name|servlet
parameter_list|)
block|{
name|ServletStruct
name|struct
init|=
operator|new
name|ServletStruct
argument_list|()
decl_stmt|;
name|struct
operator|.
name|clazz
operator|=
name|servlet
expr_stmt|;
name|struct
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|struct
operator|.
name|spec
operator|=
name|pathSpec
expr_stmt|;
name|servlets
operator|.
name|add
argument_list|(
name|struct
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|with (Configuration conf)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|with
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withHttpPolicy (Configuration conf, Policy httpPolicy)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|withHttpPolicy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Policy
name|httpPolicy
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
name|httpPolicy
operator|=
name|httpPolicy
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withHttpSpnegoPrincipalKey (String spnegoPrincipalKey)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|withHttpSpnegoPrincipalKey
parameter_list|(
name|String
name|spnegoPrincipalKey
parameter_list|)
block|{
name|this
operator|.
name|spnegoPrincipalKey
operator|=
name|spnegoPrincipalKey
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withHttpSpnegoKeytabKey (String spnegoKeytabKey)
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|withHttpSpnegoKeytabKey
parameter_list|(
name|String
name|spnegoKeytabKey
parameter_list|)
block|{
name|this
operator|.
name|spnegoKeytabKey
operator|=
name|spnegoKeytabKey
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|inDevMode ()
specifier|public
name|Builder
argument_list|<
name|T
argument_list|>
name|inDevMode
parameter_list|()
block|{
name|devMode
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|start (WebApp webapp)
specifier|public
name|WebApp
name|start
parameter_list|(
name|WebApp
name|webapp
parameter_list|)
block|{
if|if
condition|(
name|webapp
operator|==
literal|null
condition|)
block|{
name|webapp
operator|=
operator|new
name|WebApp
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setup
parameter_list|()
block|{
comment|// Defaults should be fine in usual cases
block|}
block|}
expr_stmt|;
block|}
name|webapp
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|webapp
operator|.
name|setWebServices
argument_list|(
name|wsName
argument_list|)
expr_stmt|;
name|String
name|basePath
init|=
literal|"/"
operator|+
name|name
decl_stmt|;
name|webapp
operator|.
name|setRedirectPath
argument_list|(
name|basePath
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pathList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|basePath
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|webapp
operator|.
name|addServePathSpec
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|pathList
operator|.
name|add
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|webapp
operator|.
name|addServePathSpec
argument_list|(
name|basePath
argument_list|)
expr_stmt|;
name|webapp
operator|.
name|addServePathSpec
argument_list|(
name|basePath
operator|+
literal|"/*"
argument_list|)
expr_stmt|;
name|pathList
operator|.
name|add
argument_list|(
name|basePath
operator|+
literal|"/*"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wsName
operator|!=
literal|null
operator|&&
operator|!
name|wsName
operator|.
name|equals
argument_list|(
name|basePath
argument_list|)
condition|)
block|{
if|if
condition|(
name|wsName
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|webapp
operator|.
name|addServePathSpec
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|pathList
operator|.
name|add
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|webapp
operator|.
name|addServePathSpec
argument_list|(
literal|"/"
operator|+
name|wsName
argument_list|)
expr_stmt|;
name|webapp
operator|.
name|addServePathSpec
argument_list|(
literal|"/"
operator|+
name|wsName
operator|+
literal|"/*"
argument_list|)
expr_stmt|;
name|pathList
operator|.
name|add
argument_list|(
literal|"/"
operator|+
name|wsName
operator|+
literal|"/*"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|application
operator|!=
literal|null
condition|)
block|{
name|webapp
operator|.
name|setHostClass
argument_list|(
name|application
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|cls
init|=
name|inferHostClass
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"setting webapp host class to {}"
argument_list|,
name|cls
argument_list|)
expr_stmt|;
name|webapp
operator|.
name|setHostClass
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|cls
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|devMode
condition|)
block|{
if|if
condition|(
name|port
operator|>
literal|0
condition|)
block|{
try|try
block|{
operator|new
name|URL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/__stop"
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stopping existing webapp instance"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"no existing webapp instance found: {}"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// should not be fatal
name|LOG
operator|.
name|warn
argument_list|(
literal|"error stopping existing instance: {}"
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"dev mode does NOT work with ephemeral port!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|httpScheme
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|httpPolicy
operator|==
literal|null
condition|)
block|{
name|httpScheme
operator|=
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpScheme
operator|=
operator|(
name|httpPolicy
operator|==
name|Policy
operator|.
name|HTTPS_ONLY
operator|)
condition|?
name|WebAppUtils
operator|.
name|HTTPS_PREFIX
else|:
name|WebAppUtils
operator|.
name|HTTP_PREFIX
expr_stmt|;
block|}
name|HttpServer2
operator|.
name|Builder
name|builder
init|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
name|name
argument_list|)
operator|.
name|addEndpoint
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|httpScheme
operator|+
name|bindAddress
operator|+
literal|":"
operator|+
name|port
argument_list|)
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setFindPort
argument_list|(
name|findPort
argument_list|)
operator|.
name|setACL
argument_list|(
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_YARN_ADMIN_ACL
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setPathSpec
argument_list|(
name|pathList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|hasSpnegoConf
init|=
name|spnegoPrincipalKey
operator|!=
literal|null
operator|&&
name|conf
operator|.
name|get
argument_list|(
name|spnegoPrincipalKey
argument_list|)
operator|!=
literal|null
operator|&&
name|spnegoKeytabKey
operator|!=
literal|null
operator|&&
name|conf
operator|.
name|get
argument_list|(
name|spnegoKeytabKey
argument_list|)
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|hasSpnegoConf
condition|)
block|{
name|builder
operator|.
name|setUsernameConfKey
argument_list|(
name|spnegoPrincipalKey
argument_list|)
operator|.
name|setKeytabConfKey
argument_list|(
name|spnegoKeytabKey
argument_list|)
operator|.
name|setSecurityEnabled
argument_list|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|httpScheme
operator|.
name|equals
argument_list|(
name|WebAppUtils
operator|.
name|HTTPS_PREFIX
argument_list|)
condition|)
block|{
name|WebAppUtils
operator|.
name|loadSslConfiguration
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
name|HttpServer2
name|server
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|ServletStruct
name|struct
range|:
name|servlets
control|)
block|{
name|server
operator|.
name|addServlet
argument_list|(
name|struct
operator|.
name|name
argument_list|,
name|struct
operator|.
name|spec
argument_list|,
name|struct
operator|.
name|clazz
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|attributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|server
operator|.
name|setAttribute
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|HttpServer2
operator|.
name|defineFilter
argument_list|(
name|server
operator|.
name|getWebAppContext
argument_list|()
argument_list|,
literal|"guice"
argument_list|,
name|GuiceFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/*"
block|}
argument_list|)
expr_stmt|;
name|webapp
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|webapp
operator|.
name|setHttpServer
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Web app /"
operator|+
name|name
operator|+
literal|" started at "
operator|+
name|server
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
operator|.
name|getPort
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
throw|throw
operator|new
name|WebAppException
argument_list|(
literal|"Error starting http server"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|WebAppException
argument_list|(
literal|"Error starting http server"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Injector
name|injector
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
name|webapp
argument_list|,
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
if|if
condition|(
name|api
operator|!=
literal|null
condition|)
block|{
name|bind
argument_list|(
name|api
argument_list|)
operator|.
name|toInstance
argument_list|(
name|application
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered webapp guice modules"
argument_list|)
expr_stmt|;
comment|// save a guice filter instance for webapp stop (mostly for unit tests)
name|webapp
operator|.
name|setGuiceFilter
argument_list|(
name|injector
operator|.
name|getInstance
argument_list|(
name|GuiceFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|devMode
condition|)
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|Dispatcher
operator|.
name|class
argument_list|)
operator|.
name|setDevMode
argument_list|(
name|devMode
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"in dev mode!"
argument_list|)
expr_stmt|;
block|}
return|return
name|webapp
return|;
block|}
DECL|method|start ()
specifier|public
name|WebApp
name|start
parameter_list|()
block|{
return|return
name|start
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|inferHostClass ()
specifier|private
name|String
name|inferHostClass
parameter_list|()
block|{
name|String
name|thisClass
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|e
range|:
name|t
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
name|thisClass
argument_list|)
condition|)
continue|continue;
return|return
name|e
operator|.
name|getClassName
argument_list|()
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"could not infer host class from"
argument_list|,
name|t
argument_list|)
expr_stmt|;
return|return
name|thisClass
return|;
block|}
block|}
comment|/**    * Create a new webapp builder.    * @see WebApps for a complete example    * @param<T> application (holding the embedded webapp) type    * @param prefix of the webapp    * @param api the api class for the application    * @param app the application instance    * @param wsPrefix the prefix for the webservice api for this app    * @return a webapp builder    */
DECL|method|$for (String prefix, Class<T> api, T app, String wsPrefix)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Builder
argument_list|<
name|T
argument_list|>
name|$for
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|api
parameter_list|,
name|T
name|app
parameter_list|,
name|String
name|wsPrefix
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|<
name|T
argument_list|>
argument_list|(
name|prefix
argument_list|,
name|api
argument_list|,
name|app
argument_list|,
name|wsPrefix
argument_list|)
return|;
block|}
comment|/**    * Create a new webapp builder.    * @see WebApps for a complete example    * @param<T> application (holding the embedded webapp) type    * @param prefix of the webapp    * @param api the api class for the application    * @param app the application instance    * @return a webapp builder    */
DECL|method|$for (String prefix, Class<T> api, T app)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Builder
argument_list|<
name|T
argument_list|>
name|$for
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|api
parameter_list|,
name|T
name|app
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|<
name|T
argument_list|>
argument_list|(
name|prefix
argument_list|,
name|api
argument_list|,
name|app
argument_list|)
return|;
block|}
comment|// Short cut mostly for tests/demos
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|$for (String prefix, T app)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Builder
argument_list|<
name|T
argument_list|>
name|$for
parameter_list|(
name|String
name|prefix
parameter_list|,
name|T
name|app
parameter_list|)
block|{
return|return
name|$for
argument_list|(
name|prefix
argument_list|,
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|app
operator|.
name|getClass
argument_list|()
argument_list|,
name|app
argument_list|)
return|;
block|}
comment|// Ditto
DECL|method|$for (T app)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Builder
argument_list|<
name|T
argument_list|>
name|$for
parameter_list|(
name|T
name|app
parameter_list|)
block|{
return|return
name|$for
argument_list|(
literal|""
argument_list|,
name|app
argument_list|)
return|;
block|}
DECL|method|$for (String prefix)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Builder
argument_list|<
name|T
argument_list|>
name|$for
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|$for
argument_list|(
name|prefix
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

