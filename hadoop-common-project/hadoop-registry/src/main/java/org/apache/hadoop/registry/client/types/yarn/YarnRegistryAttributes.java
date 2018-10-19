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

begin_comment
comment|/**  * YARN specific attributes in the registry.  */
end_comment

begin_class
DECL|class|YarnRegistryAttributes
specifier|public
specifier|final
class|class
name|YarnRegistryAttributes
block|{
comment|/**    * Hidden constructor.    */
DECL|method|YarnRegistryAttributes ()
specifier|private
name|YarnRegistryAttributes
parameter_list|()
block|{   }
comment|/**    * ID. For containers: container ID. For application instances,    * application ID.    */
DECL|field|YARN_ID
specifier|public
specifier|static
specifier|final
name|String
name|YARN_ID
init|=
literal|"yarn:id"
decl_stmt|;
DECL|field|YARN_PERSISTENCE
specifier|public
specifier|static
specifier|final
name|String
name|YARN_PERSISTENCE
init|=
literal|"yarn:persistence"
decl_stmt|;
DECL|field|YARN_PATH
specifier|public
specifier|static
specifier|final
name|String
name|YARN_PATH
init|=
literal|"yarn:path"
decl_stmt|;
DECL|field|YARN_HOSTNAME
specifier|public
specifier|static
specifier|final
name|String
name|YARN_HOSTNAME
init|=
literal|"yarn:hostname"
decl_stmt|;
DECL|field|YARN_IP
specifier|public
specifier|static
specifier|final
name|String
name|YARN_IP
init|=
literal|"yarn:ip"
decl_stmt|;
DECL|field|YARN_COMPONENT
specifier|public
specifier|static
specifier|final
name|String
name|YARN_COMPONENT
init|=
literal|"yarn:component"
decl_stmt|;
block|}
end_class

end_unit

