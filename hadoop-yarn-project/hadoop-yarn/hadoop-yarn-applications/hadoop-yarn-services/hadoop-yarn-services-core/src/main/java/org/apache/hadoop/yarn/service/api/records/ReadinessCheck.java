begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Serializable
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
name|XmlEnum
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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlType
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
name|JsonValue
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

begin_comment
comment|/**  * A custom command or a pluggable helper container to determine the readiness  * of a container of a component. Readiness for every service is different.  * Hence the need for a simple interface, with scope to support advanced  * usecases.  **/
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
literal|"A custom command or a pluggable helper container to determine the readiness of a container of a component. Readiness for every service is different. Hence the need for a simple interface, with scope to support advanced usecases."
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
DECL|class|ReadinessCheck
specifier|public
class|class
name|ReadinessCheck
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3836839816887186801L
decl_stmt|;
comment|/**    * Type. HTTP and PORT    **/
annotation|@
name|XmlType
argument_list|(
name|name
operator|=
literal|"type"
argument_list|)
annotation|@
name|XmlEnum
DECL|enum|TypeEnum
specifier|public
enum|enum
name|TypeEnum
block|{
DECL|enumConstant|DEFAULT
name|DEFAULT
argument_list|(
literal|"DEFAULT"
argument_list|)
block|,
DECL|enumConstant|HTTP
name|HTTP
argument_list|(
literal|"HTTP"
argument_list|)
block|,
DECL|enumConstant|PORT
name|PORT
argument_list|(
literal|"PORT"
argument_list|)
block|;
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
DECL|method|TypeEnum (String value)
name|TypeEnum
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|JsonValue
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
annotation|@
name|JsonProperty
argument_list|(
literal|"type"
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"type"
argument_list|)
DECL|field|type
specifier|private
name|TypeEnum
name|type
init|=
literal|null
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"properties"
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"properties"
argument_list|)
DECL|field|properties
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|JsonProperty
argument_list|(
literal|"artifact"
argument_list|)
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"artifact"
argument_list|)
DECL|field|artifact
specifier|private
name|Artifact
name|artifact
init|=
literal|null
decl_stmt|;
comment|/**    * E.g. HTTP (YARN will perform a simple REST call at a regular interval and    * expect a 204 No content).    **/
DECL|method|type (TypeEnum type)
specifier|public
name|ReadinessCheck
name|type
parameter_list|(
name|TypeEnum
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
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
literal|"E.g. HTTP (YARN will perform a simple REST call at a regular interval and expect a 204 No content)."
argument_list|)
DECL|method|getType ()
specifier|public
name|TypeEnum
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|setType (TypeEnum type)
specifier|public
name|void
name|setType
parameter_list|(
name|TypeEnum
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|properties (Map<String, String> properties)
specifier|public
name|ReadinessCheck
name|properties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putPropsItem (String key, String propsItem)
specifier|public
name|ReadinessCheck
name|putPropsItem
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|propsItem
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|propsItem
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * A blob of key value pairs that will be used to configure the check.    * @return properties    **/
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"A blob of key value pairs that will be used to configure the check."
argument_list|)
DECL|method|getProperties ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
DECL|method|setProperties (Map<String, String> properties)
specifier|public
name|void
name|setProperties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
comment|/**    * Artifact of the pluggable readiness check helper container (optional). If    * specified, this helper container typically hosts the http uri and    * encapsulates the complex scripts required to perform actual container    * readiness check. At the end it is expected to respond a 204 No content just    * like the simplified use case. This pluggable framework benefits service    * owners who can run services without any packaging modifications. Note,    * artifacts of type docker only is supported for now.    **/
DECL|method|artifact (Artifact artifact)
specifier|public
name|ReadinessCheck
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
literal|"Artifact of the pluggable readiness check helper container (optional). If specified, this helper container typically hosts the http uri and encapsulates the complex scripts required to perform actual container readiness check. At the end it is expected to respond a 204 No content just like the simplified use case. This pluggable framework benefits service owners who can run services without any packaging modifications. Note, artifacts of type docker only is supported for now."
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
name|ReadinessCheck
name|readinessCheck
init|=
operator|(
name|ReadinessCheck
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
name|type
argument_list|,
name|readinessCheck
operator|.
name|type
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|properties
argument_list|,
name|readinessCheck
operator|.
name|properties
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|artifact
argument_list|,
name|readinessCheck
operator|.
name|artifact
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
name|type
argument_list|,
name|properties
argument_list|,
name|artifact
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
literal|"class ReadinessCheck {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    type: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|type
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    properties: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|properties
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
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

