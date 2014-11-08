begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
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
name|server
operator|.
name|resourcemanager
operator|.
name|MockRM
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|resourcemanager
operator|.
name|ResourceManager
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|NodeLabelsInfo
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|NodeToLabelsInfo
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
name|GenericExceptionHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|GuiceServletContextListener
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
name|ServletModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|WebResource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONJAXBContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONMarshaller
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONUnmarshaller
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|guice
operator|.
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|GuiceContainer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|test
operator|.
name|framework
operator|.
name|JerseyTest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|test
operator|.
name|framework
operator|.
name|WebAppDescriptor
import|;
end_import

begin_class
DECL|class|TestRMWebServicesNodeLabels
specifier|public
class|class
name|TestRMWebServicesNodeLabels
extends|extends
name|JerseyTest
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
name|TestRMWebServicesNodeLabels
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rm
specifier|private
specifier|static
name|MockRM
name|rm
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|userName
specifier|private
name|String
name|userName
decl_stmt|;
DECL|field|notUserName
specifier|private
name|String
name|notUserName
decl_stmt|;
DECL|field|injector
specifier|private
name|Injector
name|injector
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|ServletModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|bind
argument_list|(
name|JAXBContextResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RMWebServices
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GenericExceptionHandler
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|userName
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to get current user name "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|notUserName
operator|=
name|userName
operator|+
literal|"abc123"
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
name|userName
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|rm
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
expr_stmt|;
name|filter
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|through
argument_list|(
name|TestRMWebServicesAppsModification
operator|.
name|TestRMCustomAuthFilter
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|with
argument_list|(
name|GuiceContainer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
DECL|class|GuiceServletConfig
specifier|public
class|class
name|GuiceServletConfig
extends|extends
name|GuiceServletContextListener
block|{
annotation|@
name|Override
DECL|method|getInjector ()
specifier|protected
name|Injector
name|getInjector
parameter_list|()
block|{
return|return
name|injector
return|;
block|}
block|}
DECL|method|TestRMWebServicesNodeLabels ()
specifier|public
name|TestRMWebServicesNodeLabels
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|WebAppDescriptor
operator|.
name|Builder
argument_list|(
literal|"org.apache.hadoop.yarn.server.resourcemanager.webapp"
argument_list|)
operator|.
name|contextListenerClass
argument_list|(
name|GuiceServletConfig
operator|.
name|class
argument_list|)
operator|.
name|filterClass
argument_list|(
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|GuiceFilter
operator|.
name|class
argument_list|)
operator|.
name|contextPath
argument_list|(
literal|"jersey-guice-filter"
argument_list|)
operator|.
name|servletPath
argument_list|(
literal|"/"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeLabels ()
specifier|public
name|void
name|testNodeLabels
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
decl_stmt|;
name|JSONObject
name|json
decl_stmt|;
name|JSONArray
name|jarr
decl_stmt|;
name|String
name|responseString
decl_stmt|;
comment|// Add a label
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"add-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\":\"a\"}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"nodeLabels"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add another
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"add-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\":\"b\"}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|jarr
operator|=
name|json
operator|.
name|getJSONArray
argument_list|(
literal|"nodeLabels"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|jarr
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add labels to a node
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"replace-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\": [\"a\", \"b\"]}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"posted node nodelabel"
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|jarr
operator|=
name|json
operator|.
name|getJSONArray
argument_list|(
literal|"nodeLabels"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|jarr
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Replace
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"replace-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\":\"a\"}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"posted node nodelabel"
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"nodeLabels"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Replace labels using node-to-labels
name|NodeToLabelsInfo
name|ntli
init|=
operator|new
name|NodeToLabelsInfo
argument_list|()
decl_stmt|;
name|NodeLabelsInfo
name|nli
init|=
operator|new
name|NodeLabelsInfo
argument_list|()
decl_stmt|;
name|nli
operator|.
name|getNodeLabels
argument_list|()
operator|.
name|add
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|nli
operator|.
name|getNodeLabels
argument_list|()
operator|.
name|add
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|ntli
operator|.
name|getNodeToLabels
argument_list|()
operator|.
name|put
argument_list|(
literal|"nid:0"
argument_list|,
name|nli
argument_list|)
expr_stmt|;
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"replace-node-to-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
name|toJson
argument_list|(
name|ntli
argument_list|,
name|NodeToLabelsInfo
operator|.
name|class
argument_list|)
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Verify, using node-to-labels
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-node-to-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|ntli
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|NodeToLabelsInfo
operator|.
name|class
argument_list|)
expr_stmt|;
name|nli
operator|=
name|ntli
operator|.
name|getNodeToLabels
argument_list|()
operator|.
name|get
argument_list|(
literal|"nid:0"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nli
operator|.
name|getNodeLabels
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nli
operator|.
name|getNodeLabels
argument_list|()
operator|.
name|contains
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nli
operator|.
name|getNodeLabels
argument_list|()
operator|.
name|contains
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove all
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"replace-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\"}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"posted node nodelabel"
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"nodeLabels"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add a label back for auth tests
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"replace-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\": \"a\"}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"posted node nodelabel"
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"nodeLabels"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Auth fail replace labels on node
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"replace-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|notUserName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\": [\"a\", \"b\"]}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nodes"
argument_list|)
operator|.
name|path
argument_list|(
literal|"nid:0"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"nodeLabels"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fail to add a label with post
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"add-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|notUserName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\":\"c\"}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|jarr
operator|=
name|json
operator|.
name|getJSONArray
argument_list|(
literal|"nodeLabels"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|jarr
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove cluster label (succeed, we no longer need it)
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"remove-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\":\"b\"}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"nodeLabels"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove cluster label with post
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"remove-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
literal|"{\"nodeLabels\":\"a\"}"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Verify
name|response
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"get-node-labels"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|userName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|res
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|res
operator|.
name|equals
argument_list|(
literal|"null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|toJson (Object nsli, Class klass)
specifier|private
name|String
name|toJson
parameter_list|(
name|Object
name|nsli
parameter_list|,
name|Class
name|klass
parameter_list|)
throws|throws
name|Exception
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JSONJAXBContext
name|ctx
init|=
operator|new
name|JSONJAXBContext
argument_list|(
name|klass
argument_list|)
decl_stmt|;
name|JSONMarshaller
name|jm
init|=
name|ctx
operator|.
name|createJSONMarshaller
argument_list|()
decl_stmt|;
name|jm
operator|.
name|marshallToJSON
argument_list|(
name|nsli
argument_list|,
name|sw
argument_list|)
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|fromJson (String json, Class klass)
specifier|private
name|Object
name|fromJson
parameter_list|(
name|String
name|json
parameter_list|,
name|Class
name|klass
parameter_list|)
throws|throws
name|Exception
block|{
name|StringReader
name|sr
init|=
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|JSONJAXBContext
name|ctx
init|=
operator|new
name|JSONJAXBContext
argument_list|(
name|klass
argument_list|)
decl_stmt|;
name|JSONUnmarshaller
name|jm
init|=
name|ctx
operator|.
name|createJSONUnmarshaller
argument_list|()
decl_stmt|;
return|return
name|jm
operator|.
name|unmarshalFromJSON
argument_list|(
name|sr
argument_list|,
name|klass
argument_list|)
return|;
block|}
block|}
end_class

end_unit

