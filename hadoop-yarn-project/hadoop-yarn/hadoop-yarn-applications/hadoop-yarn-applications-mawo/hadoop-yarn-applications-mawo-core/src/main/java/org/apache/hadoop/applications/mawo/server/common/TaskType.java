begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.applications.mawo.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|applications
operator|.
name|mawo
operator|.
name|server
operator|.
name|common
package|;
end_package

begin_comment
comment|/**  * Define MaWo Task Type.  */
end_comment

begin_enum
DECL|enum|TaskType
specifier|public
enum|enum
name|TaskType
block|{
comment|/**    * Its a Simple Task.    */
DECL|enumConstant|SIMPLE
name|SIMPLE
block|,
comment|/**    * Its a composite task which consists of multiple simple tasks.    */
DECL|enumConstant|COMPOSITE
name|COMPOSITE
block|,
comment|/**    * Its a null task.    */
DECL|enumConstant|NULL
name|NULL
block|,
comment|/**    * Die Task is to signal for suicide.    */
DECL|enumConstant|DIE
name|DIE
block|,
comment|/**    * Teardown Task is a task which runs after all tasks are finished.    */
DECL|enumConstant|TEARDOWN
name|TEARDOWN
block|}
end_enum

end_unit

