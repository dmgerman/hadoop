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
comment|/**  * The current status of a submitted service, returned as a response to the  * GET API.  **/
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
literal|"The current status of a submitted service, returned as a response to the GET API."
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
DECL|class|ServiceStatus
specifier|public
class|class
name|ServiceStatus
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
literal|3469885905347851034L
decl_stmt|;
DECL|field|diagnostics
specifier|private
name|String
name|diagnostics
init|=
literal|null
decl_stmt|;
DECL|field|state
specifier|private
name|ServiceState
name|state
init|=
literal|null
decl_stmt|;
DECL|field|code
specifier|private
name|Integer
name|code
init|=
literal|null
decl_stmt|;
comment|/**    * Diagnostic information (if any) for the reason of the current state of the    * service. It typically has a non-null value, if the service is in a    * non-running state.    **/
DECL|method|diagnostics (String diagnostics)
specifier|public
name|ServiceStatus
name|diagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
block|{
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
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
literal|"Diagnostic information (if any) for the reason of the current state of the service. It typically has a non-null value, if the service is in a non-running state."
argument_list|)
annotation|@
name|JsonProperty
argument_list|(
literal|"diagnostics"
argument_list|)
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
DECL|method|setDiagnostics (String diagnostics)
specifier|public
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
block|{
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
block|}
comment|/**    * Service state.    **/
DECL|method|state (ServiceState state)
specifier|public
name|ServiceStatus
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
literal|"Service state."
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
comment|/**    * An error code specific to a scenario which service owners should be able to    * use to understand the failure in addition to the diagnostic information.    **/
DECL|method|code (Integer code)
specifier|public
name|ServiceStatus
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
literal|"An error code specific to a scenario which service owners should be able to use to understand the failure in addition to the diagnostic information."
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
name|ServiceStatus
name|serviceStatus
init|=
operator|(
name|ServiceStatus
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
name|diagnostics
argument_list|,
name|serviceStatus
operator|.
name|diagnostics
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
name|serviceStatus
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
name|code
argument_list|,
name|serviceStatus
operator|.
name|code
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
name|diagnostics
argument_list|,
name|state
argument_list|,
name|code
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
literal|"class ServiceStatus {\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    diagnostics: "
argument_list|)
operator|.
name|append
argument_list|(
name|toIndentedString
argument_list|(
name|diagnostics
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

