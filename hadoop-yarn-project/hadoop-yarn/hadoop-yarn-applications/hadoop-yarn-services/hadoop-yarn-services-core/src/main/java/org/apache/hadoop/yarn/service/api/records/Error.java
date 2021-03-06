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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|Error
specifier|public
class|class
name|Error
block|{
DECL|field|code
specifier|private
name|Integer
name|code
init|=
literal|null
decl_stmt|;
DECL|field|message
specifier|private
name|String
name|message
init|=
literal|null
decl_stmt|;
DECL|field|fields
specifier|private
name|String
name|fields
init|=
literal|null
decl_stmt|;
comment|/**    **/
DECL|method|code (Integer code)
specifier|public
name|Error
name|code
parameter_list|(
name|Integer
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
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
literal|""
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"code"
argument_list|)
DECL|method|getCode ()
specifier|public
name|Integer
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
DECL|method|setCode (Integer code)
specifier|public
name|void
name|setCode
parameter_list|(
name|Integer
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
comment|/**    **/
DECL|method|message (String message)
specifier|public
name|Error
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
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
literal|""
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"message"
argument_list|)
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
DECL|method|setMessage (String message)
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/**    **/
DECL|method|fields (String fields)
specifier|public
name|Error
name|fields
parameter_list|(
name|String
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
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
literal|""
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"fields"
argument_list|)
DECL|method|getFields ()
specifier|public
name|String
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
DECL|method|setFields (String fields)
specifier|public
name|void
name|setFields
parameter_list|(
name|String
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
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
name|Error
name|error
init|=
operator|(
name|Error
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
name|code
argument_list|,
name|error
operator|.
name|code
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|message
argument_list|,
name|error
operator|.
name|message
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|fields
argument_list|,
name|error
operator|.
name|fields
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
name|code
argument_list|,
name|message
argument_list|,
name|fields
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
literal|"class Error {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    code: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|code
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
literal|"    message: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|message
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
literal|"    fields: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|fields
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

