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

begin_comment
comment|/**  * The current state of an application.  **/
end_comment

begin_enum
annotation|@
name|ApiModel
argument_list|(
name|description
operator|=
literal|"The current state of an application."
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
DECL|enum|ApplicationState
specifier|public
enum|enum
name|ApplicationState
block|{
DECL|enumConstant|ACCEPTED
DECL|enumConstant|STARTED
DECL|enumConstant|READY
DECL|enumConstant|STOPPED
DECL|enumConstant|FAILED
name|ACCEPTED
block|,
name|STARTED
block|,
name|READY
block|,
name|STOPPED
block|,
name|FAILED
block|; }
end_enum

end_unit

