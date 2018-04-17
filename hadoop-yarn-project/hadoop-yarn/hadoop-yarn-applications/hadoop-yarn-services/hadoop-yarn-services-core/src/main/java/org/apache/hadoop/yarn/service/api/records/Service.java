begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonInclude
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonPropertyOrder
import|;
end_import

begin_import
import|import
name|io
operator|.
name|swagger
operator|.
name|annotations
operator|.
name|ApiModel
import|;
end_import

begin_import
import|import
name|io
operator|.
name|swagger
operator|.
name|annotations
operator|.
name|ApiModelProperty
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
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
name|Date
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * An Service resource has the following attributes.  **/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|ApiModel
argument_list|(
name|description
operator|=
literal|"An Service resource has the following attributes."
argument_list|)
annotation|@
name|javax
operator|.
name|annotation
operator|.
name|Generated
argument_list|(
name|value
operator|=
literal|"class io.swagger.codegen.languages.JavaClientCodegen"
argument_list|,
name|date
operator|=
literal|"2016-06-02T08:15:05.615-07:00"
argument_list|)
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|JsonInclude
argument_list|(
name|JsonInclude
operator|.
name|Include
operator|.
name|NON_NULL
argument_list|)
annotation|@
name|JsonPropertyOrder
argument_list|(
block|{
literal|"name"
block|,
literal|"version"
block|,
literal|"description"
block|,
literal|"state"
block|,
literal|"resource"
block|,
literal|"number_of_containers"
block|,
literal|"lifetime"
block|,
literal|"containers"
block|}
argument_list|)
DECL|class|Service
specifier|public
class|class
name|Service
extends|extends
name|BaseResource
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4491694636566094885L
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
init|=
literal|null
decl_stmt|;
DECL|field|artifact
specifier|private
name|Artifact
name|artifact
init|=
literal|null
decl_stmt|;
DECL|field|resource
specifier|private
name|Resource
name|resource
init|=
literal|null
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"launch_time"
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"launch_time"
argument_list|)
DECL|field|launchTime
specifier|private
name|Date
name|launchTime
init|=
literal|null
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"number_of_running_containers"
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"number_of_running_containers"
argument_list|)
DECL|field|numberOfRunningContainers
specifier|private
name|Long
name|numberOfRunningContainers
init|=
literal|null
decl_stmt|;
DECL|field|lifetime
specifier|private
name|Long
name|lifetime
init|=
literal|null
decl_stmt|;
DECL|field|components
specifier|private
name|List
argument_list|<
name|Component
argument_list|>
name|components
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|configuration
specifier|private
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|state
specifier|private
name|ServiceState
name|state
init|=
literal|null
decl_stmt|;
DECL|field|quicklinks
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|quicklinks
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queue
specifier|private
name|String
name|queue
init|=
literal|null
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"kerberos_principal"
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"kerberos_principal"
argument_list|)
DECL|field|kerberosPrincipal
specifier|private
name|KerberosPrincipal
name|kerberosPrincipal
init|=
operator|new
name|KerberosPrincipal
argument_list|()
decl_stmt|;
DECL|field|version
specifier|private
name|String
name|version
init|=
literal|null
decl_stmt|;
DECL|field|description
specifier|private
name|String
name|description
init|=
literal|null
decl_stmt|;
DECL|field|dockerClientConfig
specifier|private
name|String
name|dockerClientConfig
init|=
literal|null
decl_stmt|;
comment|/**    * A unique service name.    **/
DECL|method|name (String name)
specifier|public
name|Service
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|value
operator|=
literal|"A unique service name."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"name"
argument_list|)
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * A unique service id.    **/
DECL|method|id (String id)
specifier|public
name|Service
name|id
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"A unique service id."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"id"
argument_list|)
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|setId (String id)
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|value
operator|=
literal|"Version of the service."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"version"
argument_list|)
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|setVersion (String version)
specifier|public
name|void
name|setVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
comment|/**    * Version of the service.    */
DECL|method|version (String version)
specifier|public
name|Service
name|version
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"Description of the service."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"description"
argument_list|)
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|setDescription (String description)
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/**    * Description of the service.    */
DECL|method|description (String description)
specifier|public
name|Service
name|description
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Artifact of single-component services. Mandatory if components    * attribute is not specified.    **/
DECL|method|artifact (Artifact artifact)
specifier|public
name|Service
name|artifact
parameter_list|(
name|Artifact
name|artifact
parameter_list|)
block|{
name|this
operator|.
name|artifact
operator|=
name|artifact
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"Artifact of single-component services. Mandatory if components attribute is not specified."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"artifact"
argument_list|)
DECL|method|getArtifact ()
specifier|public
name|Artifact
name|getArtifact
parameter_list|()
block|{
return|return
name|artifact
return|;
block|}
DECL|method|setArtifact (Artifact artifact)
specifier|public
name|void
name|setArtifact
parameter_list|(
name|Artifact
name|artifact
parameter_list|)
block|{
name|this
operator|.
name|artifact
operator|=
name|artifact
expr_stmt|;
block|}
comment|/**    * Resource of single-component services or the global default for    * multi-component services. Mandatory if it is a single-component    * service and if cpus and memory are not specified at the Service    * level.    **/
DECL|method|resource (Resource resource)
specifier|public
name|Service
name|resource
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"Resource of single-component services or the global default for multi-component services. Mandatory if it is a single-component service and if cpus and memory are not specified at the Service level."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"resource"
argument_list|)
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
return|return
name|resource
return|;
block|}
DECL|method|setResource (Resource resource)
specifier|public
name|void
name|setResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
comment|/**    * The time when the service was created, e.g. 2016-03-16T01:01:49.000Z.    **/
DECL|method|launchTime (Date launchTime)
specifier|public
name|Service
name|launchTime
parameter_list|(
name|Date
name|launchTime
parameter_list|)
block|{
name|this
operator|.
name|launchTime
operator|=
name|launchTime
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|Date
operator|)
name|launchTime
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"The time when the service was created, e.g. 2016-03-16T01:01:49.000Z."
argument_list|)
DECL|method|getLaunchTime ()
specifier|public
name|Date
name|getLaunchTime
parameter_list|()
block|{
return|return
name|launchTime
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|Date
operator|)
name|launchTime
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|setLaunchTime (Date launchTime)
specifier|public
name|void
name|setLaunchTime
parameter_list|(
name|Date
name|launchTime
parameter_list|)
block|{
name|this
operator|.
name|launchTime
operator|=
name|launchTime
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|Date
operator|)
name|launchTime
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
comment|/**    * In get response this provides the total number of running containers for    * this service (across all components) at the time of request. Note, a    * subsequent request can return a different number as and when more    * containers get allocated until it reaches the total number of containers or    * if a flex request has been made between the two requests.    **/
DECL|method|numberOfRunningContainers (Long numberOfRunningContainers)
specifier|public
name|Service
name|numberOfRunningContainers
parameter_list|(
name|Long
name|numberOfRunningContainers
parameter_list|)
block|{
name|this
operator|.
name|numberOfRunningContainers
operator|=
name|numberOfRunningContainers
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"In get response this provides the total number of running containers for this service (across all components) at the time of request. Note, a subsequent request can return a different number as and when more containers get allocated until it reaches the total number of containers or if a flex request has been made between the two requests."
argument_list|)
DECL|method|getNumberOfRunningContainers ()
specifier|public
name|Long
name|getNumberOfRunningContainers
parameter_list|()
block|{
return|return
name|numberOfRunningContainers
return|;
block|}
DECL|method|setNumberOfRunningContainers (Long numberOfRunningContainers)
specifier|public
name|void
name|setNumberOfRunningContainers
parameter_list|(
name|Long
name|numberOfRunningContainers
parameter_list|)
block|{
name|this
operator|.
name|numberOfRunningContainers
operator|=
name|numberOfRunningContainers
expr_stmt|;
block|}
comment|/**    * Life time (in seconds) of the service from the time it reaches the    * RUNNING_BUT_UNREADY state (after which it is automatically destroyed by YARN). For    * unlimited lifetime do not set a lifetime value.    **/
DECL|method|lifetime (Long lifetime)
specifier|public
name|Service
name|lifetime
parameter_list|(
name|Long
name|lifetime
parameter_list|)
block|{
name|this
operator|.
name|lifetime
operator|=
name|lifetime
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"Life time (in seconds) of the service from the time it reaches the RUNNING_BUT_UNREADY state (after which it is automatically destroyed by YARN). For unlimited lifetime do not set a lifetime value."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"lifetime"
argument_list|)
DECL|method|getLifetime ()
specifier|public
name|Long
name|getLifetime
parameter_list|()
block|{
return|return
name|lifetime
return|;
block|}
DECL|method|setLifetime (Long lifetime)
specifier|public
name|void
name|setLifetime
parameter_list|(
name|Long
name|lifetime
parameter_list|)
block|{
name|this
operator|.
name|lifetime
operator|=
name|lifetime
expr_stmt|;
block|}
comment|/**    * Components of an service.    **/
DECL|method|components (List<Component> components)
specifier|public
name|Service
name|components
parameter_list|(
name|List
argument_list|<
name|Component
argument_list|>
name|components
parameter_list|)
block|{
name|this
operator|.
name|components
operator|=
name|components
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"Components of an service."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"components"
argument_list|)
DECL|method|getComponents ()
specifier|public
name|List
argument_list|<
name|Component
argument_list|>
name|getComponents
parameter_list|()
block|{
return|return
name|components
return|;
block|}
DECL|method|setComponents (List<Component> components)
specifier|public
name|void
name|setComponents
parameter_list|(
name|List
argument_list|<
name|Component
argument_list|>
name|components
parameter_list|)
block|{
name|this
operator|.
name|components
operator|=
name|components
expr_stmt|;
block|}
DECL|method|addComponent (Component component)
specifier|public
name|void
name|addComponent
parameter_list|(
name|Component
name|component
parameter_list|)
block|{
name|components
operator|.
name|add
argument_list|(
name|component
argument_list|)
expr_stmt|;
block|}
DECL|method|getComponent (String name)
specifier|public
name|Component
name|getComponent
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Component
name|component
range|:
name|components
control|)
block|{
if|if
condition|(
name|component
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|component
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Config properties of an service. Configurations provided at the    * service/global level are available to all the components. Specific    * properties can be overridden at the component level.    **/
DECL|method|configuration (Configuration configuration)
specifier|public
name|Service
name|configuration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"Config properties of an service. Configurations provided at the service/global level are available to all the components. Specific properties can be overridden at the component level."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"configuration"
argument_list|)
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
DECL|method|setConfiguration (Configuration configuration)
specifier|public
name|void
name|setConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
block|}
comment|/**    * State of the service. Specifying a value for this attribute for the    * POST payload raises a validation error. This attribute is available only in    * the GET response of a started service.    **/
DECL|method|state (ServiceState state)
specifier|public
name|Service
name|state
parameter_list|(
name|ServiceState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"State of the service. Specifying a value for this attribute for the POST payload raises a validation error. This attribute is available only in the GET response of a started service."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"state"
argument_list|)
DECL|method|getState ()
specifier|public
name|ServiceState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|setState (ServiceState state)
specifier|public
name|void
name|setState
parameter_list|(
name|ServiceState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
comment|/**    * A blob of key-value pairs of quicklinks to be exported for an service.    **/
DECL|method|quicklinks (Map<String, String> quicklinks)
specifier|public
name|Service
name|quicklinks
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|quicklinks
parameter_list|)
block|{
name|this
operator|.
name|quicklinks
operator|=
name|quicklinks
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"A blob of key-value pairs of quicklinks to be exported for an service."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"quicklinks"
argument_list|)
DECL|method|getQuicklinks ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getQuicklinks
parameter_list|()
block|{
return|return
name|quicklinks
return|;
block|}
DECL|method|setQuicklinks (Map<String, String> quicklinks)
specifier|public
name|void
name|setQuicklinks
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|quicklinks
parameter_list|)
block|{
name|this
operator|.
name|quicklinks
operator|=
name|quicklinks
expr_stmt|;
block|}
comment|/**    * The YARN queue that this service should be submitted to.    **/
DECL|method|queue (String queue)
specifier|public
name|Service
name|queue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"The YARN queue that this service should be submitted to."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"queue"
argument_list|)
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
DECL|method|kerberosPrincipal (KerberosPrincipal kerberosPrincipal)
specifier|public
name|Service
name|kerberosPrincipal
parameter_list|(
name|KerberosPrincipal
name|kerberosPrincipal
parameter_list|)
block|{
name|this
operator|.
name|kerberosPrincipal
operator|=
name|kerberosPrincipal
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * The Kerberos Principal of the service.    * @return kerberosPrincipal    **/
annotation|@
name|ApiModelProperty
argument_list|(
name|value
operator|=
literal|"The Kerberos Principal of the service"
argument_list|)
DECL|method|getKerberosPrincipal ()
specifier|public
name|KerberosPrincipal
name|getKerberosPrincipal
parameter_list|()
block|{
return|return
name|kerberosPrincipal
return|;
block|}
DECL|method|setKerberosPrincipal (KerberosPrincipal kerberosPrincipal)
specifier|public
name|void
name|setKerberosPrincipal
parameter_list|(
name|KerberosPrincipal
name|kerberosPrincipal
parameter_list|)
block|{
name|this
operator|.
name|kerberosPrincipal
operator|=
name|kerberosPrincipal
expr_stmt|;
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"docker_client_config"
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"docker_client_config"
argument_list|)
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:hiddenfield"
argument_list|)
DECL|method|dockerClientConfig (String dockerClientConfig)
specifier|public
name|Service
name|dockerClientConfig
parameter_list|(
name|String
name|dockerClientConfig
parameter_list|)
block|{
name|this
operator|.
name|dockerClientConfig
operator|=
name|dockerClientConfig
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * The Docker client config for the service.    * @return dockerClientConfig    */
annotation|@
name|ApiModelProperty
argument_list|(
name|value
operator|=
literal|"The Docker client config for the service"
argument_list|)
DECL|method|getDockerClientConfig ()
specifier|public
name|String
name|getDockerClientConfig
parameter_list|()
block|{
return|return
name|dockerClientConfig
return|;
block|}
DECL|method|setDockerClientConfig (String dockerClientConfig)
specifier|public
name|void
name|setDockerClientConfig
parameter_list|(
name|String
name|dockerClientConfig
parameter_list|)
block|{
name|this
operator|.
name|dockerClientConfig
operator|=
name|dockerClientConfig
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (java.lang.Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Service
name|service
init|=
operator|(
name|Service
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|name
argument_list|,
name|service
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"class Service {\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    name: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|name
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    id: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    version: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|version
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    description: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|description
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    artifact: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|artifact
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    resource: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|resource
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    launchTime: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|launchTime
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    numberOfRunningContainers: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|numberOfRunningContainers
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    lifetime: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|lifetime
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    components: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|components
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    configuration: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|configuration
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    state: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|state
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    quicklinks: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|quicklinks
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    queue: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|queue
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    kerberosPrincipal: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|kerberosPrincipal
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    dockerClientConfig: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|dockerClientConfig
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Convert the given object to string with each line indented by 4 spaces    * (except the first line).    */
DECL|method|toIndentedString (java.lang.Object o)
specifier|private
name|String
name|toIndentedString
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|"null"
return|;
block|}
return|return
name|o
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|"\n"
argument_list|,
literal|"\n    "
argument_list|)
return|;
block|}
block|}
end_class

end_unit

