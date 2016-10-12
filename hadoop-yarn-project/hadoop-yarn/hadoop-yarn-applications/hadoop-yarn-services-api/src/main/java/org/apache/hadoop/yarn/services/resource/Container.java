begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.services.resource
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|services
operator|.
name|resource
package|;
end_package

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
name|Objects
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

begin_comment
comment|/**  * An instance of a running application container.  **/
end_comment

begin_class
annotation|@
name|ApiModel
argument_list|(
name|description
operator|=
literal|"An instance of a running application container"
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
name|JsonInclude
argument_list|(
name|JsonInclude
operator|.
name|Include
operator|.
name|NON_NULL
argument_list|)
DECL|class|Container
specifier|public
class|class
name|Container
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
literal|8955788064529288L
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
init|=
literal|null
decl_stmt|;
DECL|field|launchTime
specifier|private
name|Date
name|launchTime
init|=
literal|null
decl_stmt|;
DECL|field|ip
specifier|private
name|String
name|ip
init|=
literal|null
decl_stmt|;
DECL|field|hostname
specifier|private
name|String
name|hostname
init|=
literal|null
decl_stmt|;
DECL|field|bareHost
specifier|private
name|String
name|bareHost
init|=
literal|null
decl_stmt|;
DECL|field|state
specifier|private
name|ContainerState
name|state
init|=
literal|null
decl_stmt|;
DECL|field|componentName
specifier|private
name|String
name|componentName
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
comment|/**    * Unique container id of a running application, e.g.    * container_e3751_1458061340047_0008_01_000002.    **/
DECL|method|id (String id)
specifier|public
name|Container
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
literal|"Unique container id of a running application, e.g. container_e3751_1458061340047_0008_01_000002."
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
comment|/**    * The time when the container was created, e.g. 2016-03-16T01:01:49.000Z.    * This will most likely be different from cluster launch time.    **/
DECL|method|launchTime (Date launchTime)
specifier|public
name|Container
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
literal|"The time when the container was created, e.g. 2016-03-16T01:01:49.000Z. This will most likely be different from cluster launch time."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"launch_time"
argument_list|)
DECL|method|getLaunchTime ()
specifier|public
name|String
name|getLaunchTime
parameter_list|()
block|{
return|return
name|launchTime
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"launch_time"
argument_list|)
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
expr_stmt|;
block|}
comment|/**    * IP address of a running container, e.g. 172.31.42.141. The IP address and    * hostname attribute values are dependent on the cluster/docker network setup    * as per YARN-4007.    **/
DECL|method|ip (String ip)
specifier|public
name|Container
name|ip
parameter_list|(
name|String
name|ip
parameter_list|)
block|{
name|this
operator|.
name|ip
operator|=
name|ip
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
literal|"IP address of a running container, e.g. 172.31.42.141. The IP address and hostname attribute values are dependent on the cluster/docker network setup as per YARN-4007."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"ip"
argument_list|)
DECL|method|getIp ()
specifier|public
name|String
name|getIp
parameter_list|()
block|{
return|return
name|ip
return|;
block|}
DECL|method|setIp (String ip)
specifier|public
name|void
name|setIp
parameter_list|(
name|String
name|ip
parameter_list|)
block|{
name|this
operator|.
name|ip
operator|=
name|ip
expr_stmt|;
block|}
comment|/**    * Fully qualified hostname of a running container, e.g.    * ctr-e3751-1458061340047-0008-01-000002.examplestg.site. The IP address and    * hostname attribute values are dependent on the cluster/docker network setup    * as per YARN-4007.    **/
DECL|method|hostname (String hostname)
specifier|public
name|Container
name|hostname
parameter_list|(
name|String
name|hostname
parameter_list|)
block|{
name|this
operator|.
name|hostname
operator|=
name|hostname
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
literal|"Fully qualified hostname of a running container, e.g. ctr-e3751-1458061340047-0008-01-000002.examplestg.site. The IP address and hostname attribute values are dependent on the cluster/docker network setup as per YARN-4007."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"hostname"
argument_list|)
DECL|method|getHostname ()
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|hostname
return|;
block|}
DECL|method|setHostname (String hostname)
specifier|public
name|void
name|setHostname
parameter_list|(
name|String
name|hostname
parameter_list|)
block|{
name|this
operator|.
name|hostname
operator|=
name|hostname
expr_stmt|;
block|}
comment|/**    * The bare node or host in which the container is running, e.g.    * cn008.example.com.    **/
DECL|method|bareHost (String bareHost)
specifier|public
name|Container
name|bareHost
parameter_list|(
name|String
name|bareHost
parameter_list|)
block|{
name|this
operator|.
name|bareHost
operator|=
name|bareHost
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
literal|"The bare node or host in which the container is running, e.g. cn008.example.com."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"bare_host"
argument_list|)
DECL|method|getBareHost ()
specifier|public
name|String
name|getBareHost
parameter_list|()
block|{
return|return
name|bareHost
return|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"bare_host"
argument_list|)
DECL|method|setBareHost (String bareHost)
specifier|public
name|void
name|setBareHost
parameter_list|(
name|String
name|bareHost
parameter_list|)
block|{
name|this
operator|.
name|bareHost
operator|=
name|bareHost
expr_stmt|;
block|}
comment|/**    * State of the container of an application.    **/
DECL|method|state (ContainerState state)
specifier|public
name|Container
name|state
parameter_list|(
name|ContainerState
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
literal|"State of the container of an application."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"state"
argument_list|)
DECL|method|getState ()
specifier|public
name|ContainerState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|setState (ContainerState state)
specifier|public
name|void
name|setState
parameter_list|(
name|ContainerState
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
comment|/**    * Name of the component that this container instance belongs to.    **/
DECL|method|componentName (String componentName)
specifier|public
name|Container
name|componentName
parameter_list|(
name|String
name|componentName
parameter_list|)
block|{
name|this
operator|.
name|componentName
operator|=
name|componentName
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
literal|"Name of the component that this container instance belongs to."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"component_name"
argument_list|)
DECL|method|getComponentName ()
specifier|public
name|String
name|getComponentName
parameter_list|()
block|{
return|return
name|componentName
return|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"component_name"
argument_list|)
DECL|method|setComponentName (String componentName)
specifier|public
name|void
name|setComponentName
parameter_list|(
name|String
name|componentName
parameter_list|)
block|{
name|this
operator|.
name|componentName
operator|=
name|componentName
expr_stmt|;
block|}
comment|/**    * Resource used for this container.    **/
DECL|method|resource (Resource resource)
specifier|public
name|Container
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
literal|"Resource used for this container."
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
name|Container
name|container
init|=
operator|(
name|Container
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
name|id
argument_list|,
name|container
operator|.
name|id
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|launchTime
argument_list|,
name|container
operator|.
name|launchTime
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|ip
argument_list|,
name|container
operator|.
name|ip
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|hostname
argument_list|,
name|container
operator|.
name|hostname
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|bareHost
argument_list|,
name|container
operator|.
name|bareHost
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|state
argument_list|,
name|container
operator|.
name|state
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|componentName
argument_list|,
name|container
operator|.
name|componentName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|resource
argument_list|,
name|container
operator|.
name|resource
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
name|id
argument_list|,
name|launchTime
argument_list|,
name|ip
argument_list|,
name|hostname
argument_list|,
name|bareHost
argument_list|,
name|state
argument_list|,
name|componentName
argument_list|,
name|resource
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
literal|"class Container {\n"
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
literal|"    ip: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|ip
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
literal|"    hostname: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|hostname
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
literal|"    bareHost: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|bareHost
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
literal|"    componentName: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|componentName
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

