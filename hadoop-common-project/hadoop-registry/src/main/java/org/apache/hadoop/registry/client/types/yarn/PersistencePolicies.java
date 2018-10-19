begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.types.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|yarn
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
import|;
end_import

begin_comment
comment|/**  * Persistence policies for {@link ServiceRecord}  */
end_comment

begin_interface
DECL|interface|PersistencePolicies
specifier|public
interface|interface
name|PersistencePolicies
block|{
comment|/**    * The record persists until removed manually: {@value}.    */
DECL|field|PERMANENT
name|String
name|PERMANENT
init|=
literal|"permanent"
decl_stmt|;
comment|/**    * Remove when the YARN application defined in the id field    * terminates: {@value}.    */
DECL|field|APPLICATION
name|String
name|APPLICATION
init|=
literal|"application"
decl_stmt|;
comment|/**    * Remove when the current YARN application attempt ID finishes: {@value}.    */
DECL|field|APPLICATION_ATTEMPT
name|String
name|APPLICATION_ATTEMPT
init|=
literal|"application-attempt"
decl_stmt|;
comment|/**    * Remove when the YARN container in the ID field finishes: {@value}    */
DECL|field|CONTAINER
name|String
name|CONTAINER
init|=
literal|"container"
decl_stmt|;
block|}
end_interface

end_unit

