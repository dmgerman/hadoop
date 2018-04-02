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
name|resource
operator|.
name|PlacementConstraints
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
name|io
operator|.
name|swagger
operator|.
name|annotations
operator|.
name|ApiModel
import|;
end_import

begin_comment
comment|/**  * The scope of placement for the containers of a component.  **/
end_comment

begin_enum
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
literal|"The scope of placement for the containers of a "
operator|+
literal|"component."
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
literal|"2018-02-16T10:20:12.927-07:00"
argument_list|)
DECL|enum|PlacementScope
specifier|public
enum|enum
name|PlacementScope
block|{
DECL|enumConstant|NODE
DECL|enumConstant|RACK
name|NODE
parameter_list|(
name|PlacementConstraints
operator|.
name|NODE
parameter_list|)
operator|,
constructor|RACK(PlacementConstraints.RACK
block|)
enum|;
end_enum

begin_decl_stmt
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|PlacementScope (String value)
name|PlacementScope
argument_list|(
name|String
name|value
argument_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
block|;   }
DECL|method|getValue ()
specifier|public
name|String
name|getValue
argument_list|()
block|{
return|return
name|value
return|;
block|}
end_expr_stmt

begin_function
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
end_function

unit|}
end_unit

