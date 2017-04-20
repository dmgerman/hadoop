begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.mock
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
package|;
end_package

begin_comment
comment|/**  * Mock role constants.  */
end_comment

begin_interface
DECL|interface|MockRoles
specifier|public
interface|interface
name|MockRoles
block|{
DECL|field|ROLE0
name|String
name|ROLE0
init|=
literal|"role0"
decl_stmt|;
DECL|field|ROLE1
name|String
name|ROLE1
init|=
literal|"role1"
decl_stmt|;
DECL|field|ROLE2
name|String
name|ROLE2
init|=
literal|"role2"
decl_stmt|;
DECL|field|LABEL_GPU
name|String
name|LABEL_GPU
init|=
literal|"gpu"
decl_stmt|;
block|}
end_interface

end_unit

