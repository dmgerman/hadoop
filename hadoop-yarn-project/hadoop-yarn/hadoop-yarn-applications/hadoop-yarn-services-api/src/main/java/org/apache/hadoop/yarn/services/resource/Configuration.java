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
comment|/**  * Set of configuration properties that can be injected into the application  * components via envs, files and custom pluggable helper docker containers.  * Files of several standard formats like xml, properties, json, yaml and  * templates will be supported.  **/
end_comment

begin_class
annotation|@
name|ApiModel
argument_list|(
name|description
operator|=
literal|"Set of configuration properties that can be injected into the application components via envs, files and custom pluggable helper docker containers. Files of several standard formats like xml, properties, json, yaml and templates will be supported."
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
name|JsonInclude
argument_list|(
name|JsonInclude
operator|.
name|Include
operator|.
name|NON_NULL
argument_list|)
DECL|class|Configuration
specifier|public
class|class
name|Configuration
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
literal|4330788704981074466L
decl_stmt|;
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
DECL|field|env
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
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
DECL|field|files
specifier|private
name|List
argument_list|<
name|ConfigFile
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<
name|ConfigFile
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * A blob of key-value pairs of common application properties.    **/
DECL|method|properties (Map<String, String> properties)
specifier|public
name|Configuration
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
annotation|@
name|ApiModelProperty
argument_list|(
name|example
operator|=
literal|"null"
argument_list|,
name|value
operator|=
literal|"A blob of key-value pairs of common application properties."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"properties"
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
comment|/**    * A blob of key-value pairs which will be appended to the default system    * properties and handed off to the application at start time. All placeholder    * references to properties will be substituted before injection.    **/
DECL|method|env (Map<String, String> env)
specifier|public
name|Configuration
name|env
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
name|this
operator|.
name|env
operator|=
name|env
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
literal|"A blob of key-value pairs which will be appended to the default system properties and handed off to the application at start time. All placeholder references to properties will be substituted before injection."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"env"
argument_list|)
DECL|method|getEnv ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnv
parameter_list|()
block|{
return|return
name|env
return|;
block|}
DECL|method|setEnv (Map<String, String> env)
specifier|public
name|void
name|setEnv
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
name|this
operator|.
name|env
operator|=
name|env
expr_stmt|;
block|}
comment|/**    * Array of list of files that needs to be created and made available as    * volumes in the application component containers.    **/
DECL|method|files (List<ConfigFile> files)
specifier|public
name|Configuration
name|files
parameter_list|(
name|List
argument_list|<
name|ConfigFile
argument_list|>
name|files
parameter_list|)
block|{
name|this
operator|.
name|files
operator|=
name|files
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
literal|"Array of list of files that needs to be created and made available as volumes in the application component containers."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"files"
argument_list|)
DECL|method|getFiles ()
specifier|public
name|List
argument_list|<
name|ConfigFile
argument_list|>
name|getFiles
parameter_list|()
block|{
return|return
name|files
return|;
block|}
DECL|method|setFiles (List<ConfigFile> files)
specifier|public
name|void
name|setFiles
parameter_list|(
name|List
argument_list|<
name|ConfigFile
argument_list|>
name|files
parameter_list|)
block|{
name|this
operator|.
name|files
operator|=
name|files
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
name|Configuration
name|configuration
init|=
operator|(
name|Configuration
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
name|properties
argument_list|,
name|configuration
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
name|env
argument_list|,
name|configuration
operator|.
name|env
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|files
argument_list|,
name|configuration
operator|.
name|files
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
name|properties
argument_list|,
name|env
argument_list|,
name|files
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
literal|"class Configuration {\n"
argument_list|)
expr_stmt|;
name|sb
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
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    env: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|env
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
literal|"    files: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|files
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

