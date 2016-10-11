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

begin_comment
comment|/**  * A config file that needs to be created and made available as a volume in an  * application component container.  **/
end_comment

begin_class
annotation|@
name|ApiModel
argument_list|(
name|description
operator|=
literal|"A config file that needs to be created and made available as a volume in an application component container."
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
DECL|class|ConfigFile
specifier|public
class|class
name|ConfigFile
block|{
DECL|enum|TypeEnum
specifier|public
enum|enum
name|TypeEnum
block|{
DECL|enumConstant|XML
DECL|enumConstant|PROPERTIES
DECL|enumConstant|JSON
DECL|enumConstant|YAML
DECL|enumConstant|TEMPLATE
name|XML
argument_list|(
literal|"xml"
argument_list|)
block|,
name|PROPERTIES
argument_list|(
literal|"properties"
argument_list|)
block|,
name|JSON
argument_list|(
literal|"json"
argument_list|)
block|,
name|YAML
argument_list|(
literal|"yaml"
argument_list|)
block|,
name|TEMPLATE
argument_list|(
DECL|enumConstant|ENV
DECL|enumConstant|HADOOP_XML
literal|"template"
argument_list|)
block|,
name|ENV
argument_list|(
literal|"env"
argument_list|)
block|,
name|HADOOP_XML
argument_list|(
literal|"hadoop_xml"
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
DECL|field|type
specifier|private
name|TypeEnum
name|type
init|=
literal|null
decl_stmt|;
DECL|field|destFile
specifier|private
name|String
name|destFile
init|=
literal|null
decl_stmt|;
DECL|field|srcFile
specifier|private
name|String
name|srcFile
init|=
literal|null
decl_stmt|;
DECL|field|props
specifier|private
name|Object
name|props
init|=
literal|null
decl_stmt|;
comment|/**    * Config file in the standard format like xml, properties, json, yaml,    * template.    **/
DECL|method|type (TypeEnum type)
specifier|public
name|ConfigFile
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
literal|"Config file in the standard format like xml, properties, json, yaml, template."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"type"
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
comment|/**    * The absolute path that this configuration file should be mounted as, in the    * application container.    **/
DECL|method|destFile (String destFile)
specifier|public
name|ConfigFile
name|destFile
parameter_list|(
name|String
name|destFile
parameter_list|)
block|{
name|this
operator|.
name|destFile
operator|=
name|destFile
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
literal|"The absolute path that this configuration file should be mounted as, in the application container."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"dest_file"
argument_list|)
DECL|method|getDestFile ()
specifier|public
name|String
name|getDestFile
parameter_list|()
block|{
return|return
name|destFile
return|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"dest_file"
argument_list|)
DECL|method|setDestFile (String destFile)
specifier|public
name|void
name|setDestFile
parameter_list|(
name|String
name|destFile
parameter_list|)
block|{
name|this
operator|.
name|destFile
operator|=
name|destFile
expr_stmt|;
block|}
comment|/**    * Required for type template. This provides the source location of the    * template which needs to be mounted as dest_file post property    * substitutions. Typically the src_file would point to a source controlled    * network accessible file maintained by tools like puppet, chef, etc.    **/
DECL|method|srcFile (String srcFile)
specifier|public
name|ConfigFile
name|srcFile
parameter_list|(
name|String
name|srcFile
parameter_list|)
block|{
name|this
operator|.
name|srcFile
operator|=
name|srcFile
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
literal|"Required for type template. This provides the source location of the template which needs to be mounted as dest_file post property substitutions. Typically the src_file would point to a source controlled network accessible file maintained by tools like puppet, chef, etc."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"src_file"
argument_list|)
DECL|method|getSrcFile ()
specifier|public
name|String
name|getSrcFile
parameter_list|()
block|{
return|return
name|srcFile
return|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"src_file"
argument_list|)
DECL|method|setSrcFile (String srcFile)
specifier|public
name|void
name|setSrcFile
parameter_list|(
name|String
name|srcFile
parameter_list|)
block|{
name|this
operator|.
name|srcFile
operator|=
name|srcFile
expr_stmt|;
block|}
comment|/**    * A blob of key value pairs that will be dumped in the dest_file in the    * format as specified in type. If the type is template then the attribute    * src_file is mandatory and the src_file content is dumped to dest_file post    * property substitutions.    **/
DECL|method|props (Object props)
specifier|public
name|ConfigFile
name|props
parameter_list|(
name|Object
name|props
parameter_list|)
block|{
name|this
operator|.
name|props
operator|=
name|props
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
literal|"A blob of key value pairs that will be dumped in the dest_file in the format as specified in type. If the type is template then the attribute src_file is mandatory and the src_file content is dumped to dest_file post property substitutions."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"props"
argument_list|)
DECL|method|getProps ()
specifier|public
name|Object
name|getProps
parameter_list|()
block|{
return|return
name|props
return|;
block|}
DECL|method|setProps (Object props)
specifier|public
name|void
name|setProps
parameter_list|(
name|Object
name|props
parameter_list|)
block|{
name|this
operator|.
name|props
operator|=
name|props
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
name|ConfigFile
name|configFile
init|=
operator|(
name|ConfigFile
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
name|configFile
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
name|destFile
argument_list|,
name|configFile
operator|.
name|destFile
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|srcFile
argument_list|,
name|configFile
operator|.
name|srcFile
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|props
argument_list|,
name|configFile
operator|.
name|props
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
name|destFile
argument_list|,
name|srcFile
argument_list|,
name|props
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
literal|"class ConfigFile {\n"
argument_list|)
expr_stmt|;
name|sb
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
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    destFile: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|destFile
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
literal|"    srcFile: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|srcFile
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
literal|"    props: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|props
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

