begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|router
operator|.
name|webapp
package|;
end_package

begin_comment
comment|/**  * HTTP verbs.  **/
end_comment

begin_enum
DECL|enum|HTTPMethods
specifier|public
enum|enum
name|HTTPMethods
block|{
comment|/* to retrieve resource representation/information */
DECL|enumConstant|GET
name|GET
block|,
comment|/* to update existing resource */
DECL|enumConstant|PUT
name|PUT
block|,
comment|/* to delete resources */
DECL|enumConstant|DELETE
name|DELETE
block|,
comment|/* to create new subordinate resources */
DECL|enumConstant|POST
name|POST
block|}
end_enum

end_unit

