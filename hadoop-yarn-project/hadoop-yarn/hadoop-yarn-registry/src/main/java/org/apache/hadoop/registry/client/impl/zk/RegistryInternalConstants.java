begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.impl.zk
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
name|impl
operator|.
name|zk
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
import|;
end_import

begin_comment
comment|/**  * Internal constants for the registry.  *  * These are the things which aren't visible to users.  *  */
end_comment

begin_interface
DECL|interface|RegistryInternalConstants
specifier|public
interface|interface
name|RegistryInternalConstants
block|{
comment|/**    * Pattern of a single entry in the registry path. : {@value}.    *<p>    * This is what constitutes a valid hostname according to current RFCs.    * Alphanumeric first two and last one digit, alphanumeric    * and hyphens allowed in between.    *<p>    * No upper limit is placed on the size of an entry.    */
DECL|field|VALID_PATH_ENTRY_PATTERN
name|String
name|VALID_PATH_ENTRY_PATTERN
init|=
literal|"([a-z0-9]|[a-z0-9][a-z0-9\\-]*[a-z0-9])"
decl_stmt|;
comment|/**    * Permissions for readers: {@value}.    */
DECL|field|PERMISSIONS_REGISTRY_READERS
name|int
name|PERMISSIONS_REGISTRY_READERS
init|=
name|ZooDefs
operator|.
name|Perms
operator|.
name|READ
decl_stmt|;
comment|/**    * Permissions for system services: {@value}    */
DECL|field|PERMISSIONS_REGISTRY_SYSTEM_SERVICES
name|int
name|PERMISSIONS_REGISTRY_SYSTEM_SERVICES
init|=
name|ZooDefs
operator|.
name|Perms
operator|.
name|ALL
decl_stmt|;
comment|/**    * Permissions for a user's root entry: {@value}.    * All except the admin permissions (ACL access) on a node    */
DECL|field|PERMISSIONS_REGISTRY_USER_ROOT
name|int
name|PERMISSIONS_REGISTRY_USER_ROOT
init|=
name|ZooDefs
operator|.
name|Perms
operator|.
name|READ
operator||
name|ZooDefs
operator|.
name|Perms
operator|.
name|WRITE
operator||
name|ZooDefs
operator|.
name|Perms
operator|.
name|CREATE
operator||
name|ZooDefs
operator|.
name|Perms
operator|.
name|DELETE
decl_stmt|;
comment|/**    * Name of the SASL auth provider which has to be added to ZK server to enable    * sasl: auth patterns: {@value}.    *    * Without this callers can connect via SASL, but    * they can't use it in ACLs    */
DECL|field|SASLAUTHENTICATION_PROVIDER
name|String
name|SASLAUTHENTICATION_PROVIDER
init|=
literal|"org.apache.zookeeper.server.auth.SASLAuthenticationProvider"
decl_stmt|;
comment|/**    * String to use as the prefix when declaring a new auth provider: {@value}.    */
DECL|field|ZOOKEEPER_AUTH_PROVIDER
name|String
name|ZOOKEEPER_AUTH_PROVIDER
init|=
literal|"zookeeper.authProvider"
decl_stmt|;
comment|/**    * This the Hadoop environment variable which propagates the identity    * of a user in an insecure cluster    */
DECL|field|HADOOP_USER_NAME
name|String
name|HADOOP_USER_NAME
init|=
literal|"HADOOP_USER_NAME"
decl_stmt|;
block|}
end_interface

end_unit

