begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
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
name|net
operator|.
name|URI
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|InterfaceStability
operator|.
name|Unstable
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineEntities
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
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineEntity
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
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelinePutResponse
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
name|client
operator|.
name|api
operator|.
name|TimelineClient
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
name|exceptions
operator|.
name|YarnException
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
name|YarnJacksonJaxbJsonProvider
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Joiner
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
name|Client
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
name|client
operator|.
name|config
operator|.
name|ClientConfig
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
name|config
operator|.
name|DefaultClientConfig
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineClientImpl
specifier|public
class|class
name|TimelineClientImpl
extends|extends
name|TimelineClient
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
name|TimelineClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RESOURCE_URI_STR
specifier|private
specifier|static
specifier|final
name|String
name|RESOURCE_URI_STR
init|=
literal|"/ws/v1/timeline/"
decl_stmt|;
DECL|field|JOINER
specifier|private
specifier|static
specifier|final
name|Joiner
name|JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|field|resURI
specifier|private
name|URI
name|resURI
decl_stmt|;
DECL|method|TimelineClientImpl ()
specifier|public
name|TimelineClientImpl
parameter_list|()
block|{
name|super
argument_list|(
name|TimelineClientImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ClientConfig
name|cc
init|=
operator|new
name|DefaultClientConfig
argument_list|()
decl_stmt|;
name|cc
operator|.
name|getClasses
argument_list|()
operator|.
name|add
argument_list|(
name|YarnJacksonJaxbJsonProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|client
operator|=
name|Client
operator|.
name|create
argument_list|(
name|cc
argument_list|)
expr_stmt|;
block|}
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|resURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|JOINER
operator|.
name|join
argument_list|(
literal|"https://"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS
argument_list|)
argument_list|,
name|RESOURCE_URI_STR
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|JOINER
operator|.
name|join
argument_list|(
literal|"http://"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|)
argument_list|,
name|RESOURCE_URI_STR
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Timeline service address: "
operator|+
name|resURI
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putEntities ( TimelineEntity... entities)
specifier|public
name|TimelinePutResponse
name|putEntities
parameter_list|(
name|TimelineEntity
modifier|...
name|entities
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|TimelineEntities
name|entitiesContainer
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|entitiesContainer
operator|.
name|addEntities
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|entities
argument_list|)
argument_list|)
expr_stmt|;
name|ClientResponse
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|doPostingEntities
argument_list|(
name|entitiesContainer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
comment|// runtime exception is expected if the client cannot connect the server
name|String
name|msg
init|=
literal|"Failed to get the response from the timeline server."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|re
argument_list|)
expr_stmt|;
throw|throw
name|re
throw|;
block|}
if|if
condition|(
name|resp
operator|==
literal|null
operator|||
name|resp
operator|.
name|getClientResponseStatus
argument_list|()
operator|!=
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
condition|)
block|{
name|String
name|msg
init|=
literal|"Failed to get the response from the timeline server."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
name|resp
operator|!=
literal|null
condition|)
block|{
name|String
name|output
init|=
name|resp
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"HTTP error code: "
operator|+
name|resp
operator|.
name|getStatus
argument_list|()
operator|+
literal|" Server response : \n"
operator|+
name|output
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|YarnException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
return|return
name|resp
operator|.
name|getEntity
argument_list|(
name|TimelinePutResponse
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|doPostingEntities (TimelineEntities entities)
specifier|public
name|ClientResponse
name|doPostingEntities
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|)
block|{
name|WebResource
name|webResource
init|=
name|client
operator|.
name|resource
argument_list|(
name|resURI
argument_list|)
decl_stmt|;
return|return
name|webResource
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|type
argument_list|(
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
argument_list|,
name|entities
argument_list|)
return|;
block|}
block|}
end_class

end_unit

