begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|servlets
operator|.
name|HealthCheckServlet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|servlets
operator|.
name|MetricsServlet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|servlets
operator|.
name|PingServlet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|servlets
operator|.
name|ThreadDumpServlet
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
name|Preconditions
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
name|container
operator|.
name|filter
operator|.
name|GZIPContentEncodingFilter
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
name|core
operator|.
name|ResourceConfig
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
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|ServletContainer
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
name|Dispatcher
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|WebApp
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|management
operator|.
name|MetricsAndMonitoring
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|AMWadlGeneratorConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|AMWebServices
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|RestPaths
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|SliderJacksonJaxbJsonProvider
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|SliderAMWebApp
specifier|public
class|class
name|SliderAMWebApp
extends|extends
name|WebApp
block|{
DECL|field|BASE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|BASE_PATH
init|=
literal|"slideram"
decl_stmt|;
DECL|field|CONTAINER_STATS
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_STATS
init|=
literal|"/stats"
decl_stmt|;
DECL|field|CLUSTER_SPEC
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_SPEC
init|=
literal|"/spec"
decl_stmt|;
DECL|field|webAppApi
specifier|private
specifier|final
name|WebAppApi
name|webAppApi
decl_stmt|;
DECL|method|SliderAMWebApp (WebAppApi webAppApi)
specifier|public
name|SliderAMWebApp
parameter_list|(
name|WebAppApi
name|webAppApi
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|webAppApi
operator|!=
literal|null
argument_list|,
literal|"webAppApi null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|webAppApi
operator|=
name|webAppApi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"com.sun.jersey"
argument_list|)
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
expr_stmt|;
comment|// Make one of these to ensure that the jax-b annotations
comment|// are properly picked up.
name|bind
argument_list|(
name|SliderJacksonJaxbJsonProvider
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Get exceptions printed to the screen
name|bind
argument_list|(
name|GenericExceptionHandler
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// bind the REST interface
name|bind
argument_list|(
name|AMWebServices
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//bind(AMAgentWebServices.class);
name|route
argument_list|(
literal|"/"
argument_list|,
name|SliderAMController
operator|.
name|class
argument_list|)
expr_stmt|;
name|route
argument_list|(
name|CONTAINER_STATS
argument_list|,
name|SliderAMController
operator|.
name|class
argument_list|,
literal|"containerStats"
argument_list|)
expr_stmt|;
name|route
argument_list|(
name|CLUSTER_SPEC
argument_list|,
name|SliderAMController
operator|.
name|class
argument_list|,
literal|"specification"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configureServlets ()
specifier|public
name|void
name|configureServlets
parameter_list|()
block|{
name|setup
argument_list|()
expr_stmt|;
name|serve
argument_list|(
literal|"/"
argument_list|,
literal|"/__stop"
argument_list|)
operator|.
name|with
argument_list|(
name|Dispatcher
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|this
operator|.
name|getServePathSpecs
argument_list|()
control|)
block|{
name|serve
argument_list|(
name|path
argument_list|)
operator|.
name|with
argument_list|(
name|Dispatcher
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// metrics
name|MetricsAndMonitoring
name|monitoring
init|=
name|webAppApi
operator|.
name|getMetricsAndMonitoring
argument_list|()
decl_stmt|;
name|serve
argument_list|(
name|SYSTEM_HEALTHCHECK
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|HealthCheckServlet
argument_list|(
name|monitoring
operator|.
name|getHealth
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|serve
argument_list|(
name|SYSTEM_METRICS
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|MetricsServlet
argument_list|(
name|monitoring
operator|.
name|getMetrics
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|serve
argument_list|(
name|SYSTEM_PING
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PingServlet
argument_list|()
argument_list|)
expr_stmt|;
name|serve
argument_list|(
name|SYSTEM_THREADS
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|ThreadDumpServlet
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|regex
init|=
literal|"(?!/ws)"
decl_stmt|;
name|serveRegex
argument_list|(
name|regex
argument_list|)
operator|.
name|with
argument_list|(
name|SliderDefaultWrapperServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ResourceConfig
operator|.
name|FEATURE_IMPLICIT_VIEWABLES
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ServletContainer
operator|.
name|FEATURE_FILTER_FORWARD_ON_404
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ResourceConfig
operator|.
name|FEATURE_XMLROOTELEMENT_PROCESSING
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ResourceConfig
operator|.
name|PROPERTY_CONTAINER_REQUEST_FILTERS
argument_list|,
name|GZIPContentEncodingFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ResourceConfig
operator|.
name|PROPERTY_CONTAINER_RESPONSE_FILTERS
argument_list|,
name|GZIPContentEncodingFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|//params.put("com.sun.jersey.spi.container.ContainerRequestFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
comment|//params.put("com.sun.jersey.spi.container.ContainerResponseFilters", "com.sun.jersey.api.container.filter.LoggingFilter");
comment|//params.put("com.sun.jersey.config.feature.Trace", "true");
name|params
operator|.
name|put
argument_list|(
literal|"com.sun.jersey.config.property.WadlGeneratorConfig"
argument_list|,
name|AMWadlGeneratorConfig
operator|.
name|CLASSNAME
argument_list|)
expr_stmt|;
name|filter
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|through
argument_list|(
name|GuiceContainer
operator|.
name|class
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

