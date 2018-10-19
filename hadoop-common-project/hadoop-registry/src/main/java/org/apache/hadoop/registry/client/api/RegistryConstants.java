begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.api
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
name|api
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Constants for the registry, including configuration keys and default  * values.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|RegistryConstants
specifier|public
interface|interface
name|RegistryConstants
block|{
comment|/**    * prefix for registry configuration options: {@value}.    */
DECL|field|REGISTRY_PREFIX
name|String
name|REGISTRY_PREFIX
init|=
literal|"hadoop.registry."
decl_stmt|;
comment|/**    * Prefix for zookeeper-specific options: {@value}    *<p>    * For clients using other protocols, these options are not supported.    */
DECL|field|ZK_PREFIX
name|String
name|ZK_PREFIX
init|=
name|REGISTRY_PREFIX
operator|+
literal|"zk."
decl_stmt|;
comment|/**    * Prefix for dns-specific options: {@value}    *<p>    * For clients using other protocols, these options are not supported.    */
DECL|field|DNS_PREFIX
name|String
name|DNS_PREFIX
init|=
name|REGISTRY_PREFIX
operator|+
literal|"dns."
decl_stmt|;
comment|/**    * flag to indicate whether or not the registry should    * be enabled in the RM: {@value}.    */
DECL|field|KEY_DNS_ENABLED
name|String
name|KEY_DNS_ENABLED
init|=
name|DNS_PREFIX
operator|+
literal|"enabled"
decl_stmt|;
comment|/**    * Defaut value for enabling the DNS in the Registry: {@value}.    */
DECL|field|DEFAULT_DNS_ENABLED
name|boolean
name|DEFAULT_DNS_ENABLED
init|=
literal|false
decl_stmt|;
comment|/**    * DNS domain name key.    */
DECL|field|KEY_DNS_DOMAIN
name|String
name|KEY_DNS_DOMAIN
init|=
name|DNS_PREFIX
operator|+
literal|"domain-name"
decl_stmt|;
comment|/**    * Max length of a label (node delimited by a dot in the FQDN).    */
DECL|field|MAX_FQDN_LABEL_LENGTH
name|int
name|MAX_FQDN_LABEL_LENGTH
init|=
literal|63
decl_stmt|;
comment|/**    * DNS bind address.    */
DECL|field|KEY_DNS_BIND_ADDRESS
name|String
name|KEY_DNS_BIND_ADDRESS
init|=
name|DNS_PREFIX
operator|+
literal|"bind-address"
decl_stmt|;
comment|/**    * DNS port number key.    */
DECL|field|KEY_DNS_PORT
name|String
name|KEY_DNS_PORT
init|=
name|DNS_PREFIX
operator|+
literal|"bind-port"
decl_stmt|;
comment|/**    * Default DNS port number.    */
DECL|field|DEFAULT_DNS_PORT
name|int
name|DEFAULT_DNS_PORT
init|=
literal|5335
decl_stmt|;
comment|/**    * DNSSEC Enabled?    */
DECL|field|KEY_DNSSEC_ENABLED
name|String
name|KEY_DNSSEC_ENABLED
init|=
name|DNS_PREFIX
operator|+
literal|"dnssec.enabled"
decl_stmt|;
comment|/**    * DNSSEC Enabled?    */
DECL|field|KEY_DNSSEC_PUBLIC_KEY
name|String
name|KEY_DNSSEC_PUBLIC_KEY
init|=
name|DNS_PREFIX
operator|+
literal|"public-key"
decl_stmt|;
comment|/**    * DNSSEC private key file.    */
DECL|field|KEY_DNSSEC_PRIVATE_KEY_FILE
name|String
name|KEY_DNSSEC_PRIVATE_KEY_FILE
init|=
name|DNS_PREFIX
operator|+
literal|"private-key-file"
decl_stmt|;
comment|/**    * Default DNSSEC private key file path.    */
DECL|field|DEFAULT_DNSSEC_PRIVATE_KEY_FILE
name|String
name|DEFAULT_DNSSEC_PRIVATE_KEY_FILE
init|=
literal|"/etc/hadoop/conf/registryDNS.private"
decl_stmt|;
comment|/**    * Zone subnet.    */
DECL|field|KEY_DNS_ZONE_SUBNET
name|String
name|KEY_DNS_ZONE_SUBNET
init|=
name|DNS_PREFIX
operator|+
literal|"zone-subnet"
decl_stmt|;
comment|/**    * Zone subnet mask.    */
DECL|field|KEY_DNS_ZONE_MASK
name|String
name|KEY_DNS_ZONE_MASK
init|=
name|DNS_PREFIX
operator|+
literal|"zone-mask"
decl_stmt|;
comment|/**    * Zone subnet IP min.    */
DECL|field|KEY_DNS_ZONE_IP_MIN
name|String
name|KEY_DNS_ZONE_IP_MIN
init|=
name|DNS_PREFIX
operator|+
literal|"zone-ip-min"
decl_stmt|;
comment|/**    * Zone subnet IP max.    */
DECL|field|KEY_DNS_ZONE_IP_MAX
name|String
name|KEY_DNS_ZONE_IP_MAX
init|=
name|DNS_PREFIX
operator|+
literal|"zone-ip-max"
decl_stmt|;
comment|/**    * DNS Record TTL.    */
DECL|field|KEY_DNS_TTL
name|String
name|KEY_DNS_TTL
init|=
name|DNS_PREFIX
operator|+
literal|"dns-ttl"
decl_stmt|;
comment|/**    * DNS Record TTL.    */
DECL|field|KEY_DNS_ZONES_DIR
name|String
name|KEY_DNS_ZONES_DIR
init|=
name|DNS_PREFIX
operator|+
literal|"zones-dir"
decl_stmt|;
comment|/**    * Split Reverse Zone.    * It may be necessary to spit large reverse zone subnets    * into multiple zones to handle existing hosts collocated    * with containers.    */
DECL|field|KEY_DNS_SPLIT_REVERSE_ZONE
name|String
name|KEY_DNS_SPLIT_REVERSE_ZONE
init|=
name|DNS_PREFIX
operator|+
literal|"split-reverse-zone"
decl_stmt|;
comment|/**    * Default value for splitting the reverse zone.    */
DECL|field|DEFAULT_DNS_SPLIT_REVERSE_ZONE
name|boolean
name|DEFAULT_DNS_SPLIT_REVERSE_ZONE
init|=
literal|false
decl_stmt|;
comment|/**    * Split Reverse Zone IP Range.    * How many IPs should be part of each reverse zone split    */
DECL|field|KEY_DNS_SPLIT_REVERSE_ZONE_RANGE
name|String
name|KEY_DNS_SPLIT_REVERSE_ZONE_RANGE
init|=
name|DNS_PREFIX
operator|+
literal|"split-reverse-zone-range"
decl_stmt|;
comment|/**    * Key to set if the registry is secure: {@value}.    * Turning it on changes the permissions policy from "open access"    * to restrictions on kerberos with the option of    * a user adding one or more auth key pairs down their    * own tree.    */
DECL|field|KEY_REGISTRY_SECURE
name|String
name|KEY_REGISTRY_SECURE
init|=
name|REGISTRY_PREFIX
operator|+
literal|"secure"
decl_stmt|;
comment|/**    * Default registry security policy: {@value}.    */
DECL|field|DEFAULT_REGISTRY_SECURE
name|boolean
name|DEFAULT_REGISTRY_SECURE
init|=
literal|false
decl_stmt|;
comment|/**    * Root path in the ZK tree for the registry: {@value}.    */
DECL|field|KEY_REGISTRY_ZK_ROOT
name|String
name|KEY_REGISTRY_ZK_ROOT
init|=
name|ZK_PREFIX
operator|+
literal|"root"
decl_stmt|;
comment|/**    * Default root of the Hadoop registry: {@value}.    */
DECL|field|DEFAULT_ZK_REGISTRY_ROOT
name|String
name|DEFAULT_ZK_REGISTRY_ROOT
init|=
literal|"/registry"
decl_stmt|;
comment|/**    * Registry client authentication policy.    *<p>    * This is only used in secure clusters.    *<p>    * If the Factory methods of {@link RegistryOperationsFactory}    * are used, this key does not need to be set: it is set    * up based on the factory method used.    */
DECL|field|KEY_REGISTRY_CLIENT_AUTH
name|String
name|KEY_REGISTRY_CLIENT_AUTH
init|=
name|REGISTRY_PREFIX
operator|+
literal|"client.auth"
decl_stmt|;
comment|/**    * Registry client uses Kerberos: authentication is automatic from    * logged in user.    */
DECL|field|REGISTRY_CLIENT_AUTH_KERBEROS
name|String
name|REGISTRY_CLIENT_AUTH_KERBEROS
init|=
literal|"kerberos"
decl_stmt|;
comment|/**    * Username/password is the authentication mechanism.    * If set then both {@link #KEY_REGISTRY_CLIENT_AUTHENTICATION_ID}    * and {@link #KEY_REGISTRY_CLIENT_AUTHENTICATION_PASSWORD} must be set.    */
DECL|field|REGISTRY_CLIENT_AUTH_DIGEST
name|String
name|REGISTRY_CLIENT_AUTH_DIGEST
init|=
literal|"digest"
decl_stmt|;
comment|/**    * No authentication; client is anonymous.    */
DECL|field|REGISTRY_CLIENT_AUTH_ANONYMOUS
name|String
name|REGISTRY_CLIENT_AUTH_ANONYMOUS
init|=
literal|""
decl_stmt|;
DECL|field|REGISTRY_CLIENT_AUTH_SIMPLE
name|String
name|REGISTRY_CLIENT_AUTH_SIMPLE
init|=
literal|"simple"
decl_stmt|;
comment|/**    * Registry client authentication ID.    *<p>    * This is only used in secure clusters with    * {@link #KEY_REGISTRY_CLIENT_AUTH} set to    * {@link #REGISTRY_CLIENT_AUTH_DIGEST}    *    */
DECL|field|KEY_REGISTRY_CLIENT_AUTHENTICATION_ID
name|String
name|KEY_REGISTRY_CLIENT_AUTHENTICATION_ID
init|=
name|KEY_REGISTRY_CLIENT_AUTH
operator|+
literal|".id"
decl_stmt|;
comment|/**    * Registry client authentication password.    *<p>    * This is only used in secure clusters with the client set to    * use digest (not SASL or anonymouse) authentication.    *<p>    * Specifically, {@link #KEY_REGISTRY_CLIENT_AUTH} set to    * {@link #REGISTRY_CLIENT_AUTH_DIGEST}    *    */
DECL|field|KEY_REGISTRY_CLIENT_AUTHENTICATION_PASSWORD
name|String
name|KEY_REGISTRY_CLIENT_AUTHENTICATION_PASSWORD
init|=
name|KEY_REGISTRY_CLIENT_AUTH
operator|+
literal|".password"
decl_stmt|;
comment|/**    * List of hostname:port pairs defining the    * zookeeper quorum binding for the registry {@value}.    */
DECL|field|KEY_REGISTRY_ZK_QUORUM
name|String
name|KEY_REGISTRY_ZK_QUORUM
init|=
name|ZK_PREFIX
operator|+
literal|"quorum"
decl_stmt|;
comment|/**    * The default zookeeper quorum binding for the registry: {@value}.    */
DECL|field|DEFAULT_REGISTRY_ZK_QUORUM
name|String
name|DEFAULT_REGISTRY_ZK_QUORUM
init|=
literal|"localhost:2181"
decl_stmt|;
comment|/**    * Zookeeper session timeout in milliseconds: {@value}.    */
DECL|field|KEY_REGISTRY_ZK_SESSION_TIMEOUT
name|String
name|KEY_REGISTRY_ZK_SESSION_TIMEOUT
init|=
name|ZK_PREFIX
operator|+
literal|"session.timeout.ms"
decl_stmt|;
comment|/**   * The default ZK session timeout: {@value}.   */
DECL|field|DEFAULT_ZK_SESSION_TIMEOUT
name|int
name|DEFAULT_ZK_SESSION_TIMEOUT
init|=
literal|60000
decl_stmt|;
comment|/**    * Zookeeper connection timeout in milliseconds: {@value}.    */
DECL|field|KEY_REGISTRY_ZK_CONNECTION_TIMEOUT
name|String
name|KEY_REGISTRY_ZK_CONNECTION_TIMEOUT
init|=
name|ZK_PREFIX
operator|+
literal|"connection.timeout.ms"
decl_stmt|;
comment|/**    * The default ZK connection timeout: {@value}.    */
DECL|field|DEFAULT_ZK_CONNECTION_TIMEOUT
name|int
name|DEFAULT_ZK_CONNECTION_TIMEOUT
init|=
literal|15000
decl_stmt|;
comment|/**    * Zookeeper connection retry count before failing: {@value}.    */
DECL|field|KEY_REGISTRY_ZK_RETRY_TIMES
name|String
name|KEY_REGISTRY_ZK_RETRY_TIMES
init|=
name|ZK_PREFIX
operator|+
literal|"retry.times"
decl_stmt|;
comment|/**    * The default # of times to retry a ZK connection: {@value}.    */
DECL|field|DEFAULT_ZK_RETRY_TIMES
name|int
name|DEFAULT_ZK_RETRY_TIMES
init|=
literal|5
decl_stmt|;
comment|/**    * Zookeeper connect interval in milliseconds: {@value}.    */
DECL|field|KEY_REGISTRY_ZK_RETRY_INTERVAL
name|String
name|KEY_REGISTRY_ZK_RETRY_INTERVAL
init|=
name|ZK_PREFIX
operator|+
literal|"retry.interval.ms"
decl_stmt|;
comment|/**    * The default interval between connection retries: {@value}.    */
DECL|field|DEFAULT_ZK_RETRY_INTERVAL
name|int
name|DEFAULT_ZK_RETRY_INTERVAL
init|=
literal|1000
decl_stmt|;
comment|/**    * Zookeeper retry limit in milliseconds, during    * exponential backoff: {@value}.    *    * This places a limit even    * if the retry times and interval limit, combined    * with the backoff policy, result in a long retry    * period    *    */
DECL|field|KEY_REGISTRY_ZK_RETRY_CEILING
name|String
name|KEY_REGISTRY_ZK_RETRY_CEILING
init|=
name|ZK_PREFIX
operator|+
literal|"retry.ceiling.ms"
decl_stmt|;
comment|/**    * Default limit on retries: {@value}.    */
DECL|field|DEFAULT_ZK_RETRY_CEILING
name|int
name|DEFAULT_ZK_RETRY_CEILING
init|=
literal|60000
decl_stmt|;
comment|/**    * A comma separated list of Zookeeper ACL identifiers with    * system access to the registry in a secure cluster: {@value}.    *    * These are given full access to all entries.    *    * If there is an "@" at the end of an entry it    * instructs the registry client to append the kerberos realm as    * derived from the login and {@link #KEY_REGISTRY_KERBEROS_REALM}.    */
DECL|field|KEY_REGISTRY_SYSTEM_ACCOUNTS
name|String
name|KEY_REGISTRY_SYSTEM_ACCOUNTS
init|=
name|REGISTRY_PREFIX
operator|+
literal|"system.accounts"
decl_stmt|;
comment|/**    * Default system accounts given global access to the registry: {@value}.    */
DECL|field|DEFAULT_REGISTRY_SYSTEM_ACCOUNTS
name|String
name|DEFAULT_REGISTRY_SYSTEM_ACCOUNTS
init|=
literal|"sasl:yarn@, sasl:mapred@, sasl:hdfs@, sasl:hadoop@"
decl_stmt|;
comment|/**    * A comma separated list of Zookeeper ACL identifiers with    * system access to the registry in a secure cluster: {@value}.    *    * These are given full access to all entries.    *    * If there is an "@" at the end of an entry it    * instructs the registry client to append the default kerberos domain.    */
DECL|field|KEY_REGISTRY_USER_ACCOUNTS
name|String
name|KEY_REGISTRY_USER_ACCOUNTS
init|=
name|REGISTRY_PREFIX
operator|+
literal|"user.accounts"
decl_stmt|;
comment|/**    * Default system acls: {@value}.    */
DECL|field|DEFAULT_REGISTRY_USER_ACCOUNTS
name|String
name|DEFAULT_REGISTRY_USER_ACCOUNTS
init|=
literal|""
decl_stmt|;
comment|/**    * The kerberos realm: {@value}.    *    * This is used to set the realm of    * system principals which do not declare their realm,    * and any other accounts that need the value.    *    * If empty, the default realm of the running process    * is used.    *    * If neither are known and the realm is needed, then the registry    * service/client will fail.    */
DECL|field|KEY_REGISTRY_KERBEROS_REALM
name|String
name|KEY_REGISTRY_KERBEROS_REALM
init|=
name|REGISTRY_PREFIX
operator|+
literal|"kerberos.realm"
decl_stmt|;
comment|/**    * Key to define the JAAS context. Used in secure registries: {@value}.    */
DECL|field|KEY_REGISTRY_CLIENT_JAAS_CONTEXT
name|String
name|KEY_REGISTRY_CLIENT_JAAS_CONTEXT
init|=
name|REGISTRY_PREFIX
operator|+
literal|"jaas.context"
decl_stmt|;
comment|/**    * default client-side registry JAAS context: {@value}.    */
DECL|field|DEFAULT_REGISTRY_CLIENT_JAAS_CONTEXT
name|String
name|DEFAULT_REGISTRY_CLIENT_JAAS_CONTEXT
init|=
literal|"Client"
decl_stmt|;
comment|/**    *  path to users off the root: {@value}.    */
DECL|field|PATH_USERS
name|String
name|PATH_USERS
init|=
literal|"/users/"
decl_stmt|;
comment|/**    *  path to system services off the root : {@value}.    */
DECL|field|PATH_SYSTEM_SERVICES
name|String
name|PATH_SYSTEM_SERVICES
init|=
literal|"/services/"
decl_stmt|;
comment|/**    *  path to system services under a user's home path : {@value}.    */
DECL|field|PATH_USER_SERVICES
name|String
name|PATH_USER_SERVICES
init|=
literal|"/services/"
decl_stmt|;
comment|/**    *  path under a service record to point to components of that service:    *  {@value}.    */
DECL|field|SUBPATH_COMPONENTS
name|String
name|SUBPATH_COMPONENTS
init|=
literal|"/components/"
decl_stmt|;
block|}
end_interface

end_unit

