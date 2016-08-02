begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
package|;
end_package

begin_comment
comment|/**  * Enumeration of state values  */
end_comment

begin_class
DECL|class|StateValues
specifier|public
class|class
name|StateValues
block|{
comment|/**    * Specification is incomplete& cannot    * be used: {@value}    */
DECL|field|STATE_INCOMPLETE
specifier|public
specifier|static
specifier|final
name|int
name|STATE_INCOMPLETE
init|=
literal|0
decl_stmt|;
comment|/**    * Spec has been submitted: {@value}    */
DECL|field|STATE_SUBMITTED
specifier|public
specifier|static
specifier|final
name|int
name|STATE_SUBMITTED
init|=
literal|1
decl_stmt|;
comment|/**    * Cluster created: {@value}    */
DECL|field|STATE_CREATED
specifier|public
specifier|static
specifier|final
name|int
name|STATE_CREATED
init|=
literal|2
decl_stmt|;
comment|/**    * Live: {@value}    */
DECL|field|STATE_LIVE
specifier|public
specifier|static
specifier|final
name|int
name|STATE_LIVE
init|=
literal|3
decl_stmt|;
comment|/**    * Stopped    */
DECL|field|STATE_STOPPED
specifier|public
specifier|static
specifier|final
name|int
name|STATE_STOPPED
init|=
literal|4
decl_stmt|;
comment|/**    * destroyed    */
DECL|field|STATE_DESTROYED
specifier|public
specifier|static
specifier|final
name|int
name|STATE_DESTROYED
init|=
literal|5
decl_stmt|;
block|}
end_class

end_unit

