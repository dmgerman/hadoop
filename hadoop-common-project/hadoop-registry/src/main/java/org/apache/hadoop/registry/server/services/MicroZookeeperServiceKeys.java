begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.server.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|server
operator|.
name|services
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
name|api
operator|.
name|RegistryConstants
import|;
end_import

begin_comment
comment|/**  * Service keys for configuring the {@link MicroZookeeperService}.  * These are not used in registry clients or the RM-side service,  * so are kept separate.  */
end_comment

begin_interface
DECL|interface|MicroZookeeperServiceKeys
specifier|public
interface|interface
name|MicroZookeeperServiceKeys
block|{
DECL|field|ZKSERVICE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|ZKSERVICE_PREFIX
init|=
name|RegistryConstants
operator|.
name|REGISTRY_PREFIX
operator|+
literal|"zk.service."
decl_stmt|;
comment|/**    * Key to define the JAAS context for the ZK service: {@value}.    */
DECL|field|KEY_REGISTRY_ZKSERVICE_JAAS_CONTEXT
specifier|public
specifier|static
specifier|final
name|String
name|KEY_REGISTRY_ZKSERVICE_JAAS_CONTEXT
init|=
name|ZKSERVICE_PREFIX
operator|+
literal|"service.jaas.context"
decl_stmt|;
comment|/**    * ZK servertick time: {@value}    */
DECL|field|KEY_ZKSERVICE_TICK_TIME
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ZKSERVICE_TICK_TIME
init|=
name|ZKSERVICE_PREFIX
operator|+
literal|"ticktime"
decl_stmt|;
comment|/**    * host to register on: {@value}.    */
DECL|field|KEY_ZKSERVICE_HOST
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ZKSERVICE_HOST
init|=
name|ZKSERVICE_PREFIX
operator|+
literal|"host"
decl_stmt|;
comment|/**    * Default host to serve on -this is<code>localhost</code> as it    * is the only one guaranteed to be available: {@value}.    */
DECL|field|DEFAULT_ZKSERVICE_HOST
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_ZKSERVICE_HOST
init|=
literal|"localhost"
decl_stmt|;
comment|/**    * port; 0 or below means "any": {@value}    */
DECL|field|KEY_ZKSERVICE_PORT
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ZKSERVICE_PORT
init|=
name|ZKSERVICE_PREFIX
operator|+
literal|"port"
decl_stmt|;
comment|/**    * Directory containing data: {@value}    */
DECL|field|KEY_ZKSERVICE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ZKSERVICE_DIR
init|=
name|ZKSERVICE_PREFIX
operator|+
literal|"dir"
decl_stmt|;
comment|/**    * Should failed SASL clients be allowed: {@value}?    *    * Default is the ZK default: true    */
DECL|field|KEY_ZKSERVICE_ALLOW_FAILED_SASL_CLIENTS
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ZKSERVICE_ALLOW_FAILED_SASL_CLIENTS
init|=
name|ZKSERVICE_PREFIX
operator|+
literal|"allow.failed.sasl.clients"
decl_stmt|;
block|}
end_interface

end_unit

