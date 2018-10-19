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
name|client
operator|.
name|ZooKeeperSaslClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|ZooKeeperSaslServer
import|;
end_import

begin_comment
comment|/**  * Configuration options which are internal to Zookeeper,  * as well as some other ZK constants  *<p>  * Zookeeper options are passed via system properties prior to the ZK  * Methods/classes being invoked. This implies that:  *<ol>  *<li>There can only be one instance of a ZK client or service class  *   in a single JVM âelse their configuration options will conflict.</li>  *<li>It is safest to set these properties immediately before  *   invoking ZK operations.</li>  *</ol>  *  */
end_comment

begin_interface
DECL|interface|ZookeeperConfigOptions
specifier|public
interface|interface
name|ZookeeperConfigOptions
block|{
comment|/**    * Enable SASL secure clients: {@value}.    * This is usually set to true, with ZK set to fall back to    * non-SASL authentication if the SASL auth fails    * by the property    * {@link #PROP_ZK_SERVER_MAINTAIN_CONNECTION_DESPITE_SASL_FAILURE}.    *<p>    * As a result, clients will default to attempting SASL-authentication,    * but revert to classic authentication/anonymous access on failure.    */
DECL|field|PROP_ZK_ENABLE_SASL_CLIENT
name|String
name|PROP_ZK_ENABLE_SASL_CLIENT
init|=
literal|"zookeeper.sasl.client"
decl_stmt|;
comment|/**    * Default flag for the ZK client: {@value}.    */
DECL|field|DEFAULT_ZK_ENABLE_SASL_CLIENT
name|String
name|DEFAULT_ZK_ENABLE_SASL_CLIENT
init|=
literal|"true"
decl_stmt|;
comment|/**    * System property for the JAAS client context : {@value}.    *    * For SASL authentication to work, this must point to a    * context within the    *    *<p>    *   Default value is derived from    *   {@link ZooKeeperSaslClient#LOGIN_CONTEXT_NAME_KEY}    */
DECL|field|PROP_ZK_SASL_CLIENT_CONTEXT
name|String
name|PROP_ZK_SASL_CLIENT_CONTEXT
init|=
name|ZooKeeperSaslClient
operator|.
name|LOGIN_CONTEXT_NAME_KEY
decl_stmt|;
comment|/**    * The SASL client username: {@value}.    *<p>    * Set this to the<i>short</i> name of the client, e.g, "user",    * not {@code user/host}, or {@code user/host@REALM}    */
DECL|field|PROP_ZK_SASL_CLIENT_USERNAME
name|String
name|PROP_ZK_SASL_CLIENT_USERNAME
init|=
literal|"zookeeper.sasl.client.username"
decl_stmt|;
comment|/**    * The SASL Server context, referring to a context in the JVM's    * JAAS context file: {@value}    */
DECL|field|PROP_ZK_SERVER_SASL_CONTEXT
name|String
name|PROP_ZK_SERVER_SASL_CONTEXT
init|=
name|ZooKeeperSaslServer
operator|.
name|LOGIN_CONTEXT_NAME_KEY
decl_stmt|;
comment|/**    * Should ZK Server allow failed SASL clients to downgrade to classic    * authentication on a SASL auth failure: {@value}.    */
DECL|field|PROP_ZK_SERVER_MAINTAIN_CONNECTION_DESPITE_SASL_FAILURE
name|String
name|PROP_ZK_SERVER_MAINTAIN_CONNECTION_DESPITE_SASL_FAILURE
init|=
literal|"zookeeper.maintain_connection_despite_sasl_failure"
decl_stmt|;
comment|/**    * should the ZK Server Allow failed SASL clients: {@value}.    */
DECL|field|PROP_ZK_ALLOW_FAILED_SASL_CLIENTS
name|String
name|PROP_ZK_ALLOW_FAILED_SASL_CLIENTS
init|=
literal|"zookeeper.allowSaslFailedClients"
decl_stmt|;
comment|/**    * Kerberos realm of the server: {@value}.    */
DECL|field|PROP_ZK_SERVER_REALM
name|String
name|PROP_ZK_SERVER_REALM
init|=
literal|"zookeeper.server.realm"
decl_stmt|;
comment|/**    * Path to a kinit binary: {@value}.    * Defaults to<code>"/usr/bin/kinit"</code>    */
DECL|field|PROP_ZK_KINIT_PATH
name|String
name|PROP_ZK_KINIT_PATH
init|=
literal|"zookeeper.kinit"
decl_stmt|;
comment|/**    * ID scheme for SASL: {@value}.    */
DECL|field|SCHEME_SASL
name|String
name|SCHEME_SASL
init|=
literal|"sasl"
decl_stmt|;
comment|/**    * ID scheme for digest auth: {@value}.    */
DECL|field|SCHEME_DIGEST
name|String
name|SCHEME_DIGEST
init|=
literal|"digest"
decl_stmt|;
block|}
end_interface

end_unit

