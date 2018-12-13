begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
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
name|utils
operator|.
name|db
operator|.
name|DBProfile
import|;
end_import

begin_comment
comment|/**  * This class contains constants for configuration keys and default values  * used in hdds.  */
end_comment

begin_class
DECL|class|HddsConfigKeys
specifier|public
specifier|final
class|class
name|HddsConfigKeys
block|{
DECL|field|HDDS_HEARTBEAT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_HEARTBEAT_INTERVAL
init|=
literal|"hdds.heartbeat.interval"
decl_stmt|;
DECL|field|HDDS_HEARTBEAT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_HEARTBEAT_INTERVAL_DEFAULT
init|=
literal|"30s"
decl_stmt|;
DECL|field|HDDS_NODE_REPORT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_NODE_REPORT_INTERVAL
init|=
literal|"hdds.node.report.interval"
decl_stmt|;
DECL|field|HDDS_NODE_REPORT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_NODE_REPORT_INTERVAL_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|HDDS_CONTAINER_REPORT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_CONTAINER_REPORT_INTERVAL
init|=
literal|"hdds.container.report.interval"
decl_stmt|;
DECL|field|HDDS_CONTAINER_REPORT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_CONTAINER_REPORT_INTERVAL_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|HDDS_PIPELINE_REPORT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PIPELINE_REPORT_INTERVAL
init|=
literal|"hdds.pipeline.report.interval"
decl_stmt|;
DECL|field|HDDS_PIPELINE_REPORT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PIPELINE_REPORT_INTERVAL_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|HDDS_COMMAND_STATUS_REPORT_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_COMMAND_STATUS_REPORT_INTERVAL
init|=
literal|"hdds.command.status.report.interval"
decl_stmt|;
DECL|field|HDDS_COMMAND_STATUS_REPORT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_COMMAND_STATUS_REPORT_INTERVAL_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|HDDS_CONTAINER_ACTION_MAX_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_CONTAINER_ACTION_MAX_LIMIT
init|=
literal|"hdds.container.action.max.limit"
decl_stmt|;
DECL|field|HDDS_CONTAINER_ACTION_MAX_LIMIT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_CONTAINER_ACTION_MAX_LIMIT_DEFAULT
init|=
literal|20
decl_stmt|;
DECL|field|HDDS_PIPELINE_ACTION_MAX_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PIPELINE_ACTION_MAX_LIMIT
init|=
literal|"hdds.pipeline.action.max.limit"
decl_stmt|;
DECL|field|HDDS_PIPELINE_ACTION_MAX_LIMIT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_PIPELINE_ACTION_MAX_LIMIT_DEFAULT
init|=
literal|20
decl_stmt|;
comment|// Configuration to allow volume choosing policy.
DECL|field|HDDS_DATANODE_VOLUME_CHOOSING_POLICY
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_DATANODE_VOLUME_CHOOSING_POLICY
init|=
literal|"hdds.datanode.volume.choosing.policy"
decl_stmt|;
comment|// DB Profiles used by ROCKDB instances.
DECL|field|HDDS_DB_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_DB_PROFILE
init|=
literal|"hdds.db.profile"
decl_stmt|;
DECL|field|HDDS_DEFAULT_DB_PROFILE
specifier|public
specifier|static
specifier|final
name|DBProfile
name|HDDS_DEFAULT_DB_PROFILE
init|=
name|DBProfile
operator|.
name|DISK
decl_stmt|;
comment|// Once a container usage crosses this threshold, it is eligible for
comment|// closing.
DECL|field|HDDS_CONTAINER_CLOSE_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_CONTAINER_CLOSE_THRESHOLD
init|=
literal|"hdds.container.close.threshold"
decl_stmt|;
DECL|field|HDDS_CONTAINER_CLOSE_THRESHOLD_DEFAULT
specifier|public
specifier|static
specifier|final
name|float
name|HDDS_CONTAINER_CLOSE_THRESHOLD_DEFAULT
init|=
literal|0.9f
decl_stmt|;
DECL|field|HDDS_SCM_CHILLMODE_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_SCM_CHILLMODE_ENABLED
init|=
literal|"hdds.scm.chillmode.enabled"
decl_stmt|;
DECL|field|HDDS_SCM_CHILLMODE_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|HDDS_SCM_CHILLMODE_ENABLED_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|HDDS_SCM_CHILLMODE_MIN_DATANODE
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_SCM_CHILLMODE_MIN_DATANODE
init|=
literal|"hdds.scm.chillmode.min.datanode"
decl_stmt|;
DECL|field|HDDS_SCM_CHILLMODE_MIN_DATANODE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_SCM_CHILLMODE_MIN_DATANODE_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK
init|=
literal|"hdds.scm.chillmode.pipeline-availability.check"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
DECL|field|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK_DEFAULT
name|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK_DEFAULT
init|=
literal|false
decl_stmt|;
comment|// % of containers which should have at least one reported replica
comment|// before SCM comes out of chill mode.
DECL|field|HDDS_SCM_CHILLMODE_THRESHOLD_PCT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_SCM_CHILLMODE_THRESHOLD_PCT
init|=
literal|"hdds.scm.chillmode.threshold.pct"
decl_stmt|;
DECL|field|HDDS_SCM_CHILLMODE_THRESHOLD_PCT_DEFAULT
specifier|public
specifier|static
specifier|final
name|double
name|HDDS_SCM_CHILLMODE_THRESHOLD_PCT_DEFAULT
init|=
literal|0.99
decl_stmt|;
DECL|field|HDDS_LOCK_MAX_CONCURRENCY
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_LOCK_MAX_CONCURRENCY
init|=
literal|"hdds.lock.max.concurrency"
decl_stmt|;
DECL|field|HDDS_LOCK_MAX_CONCURRENCY_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_LOCK_MAX_CONCURRENCY_DEFAULT
init|=
literal|100
decl_stmt|;
comment|// This configuration setting is used as a fallback location by all
comment|// Ozone/HDDS services for their metadata. It is useful as a single
comment|// config point for test/PoC clusters.
comment|//
comment|// In any real cluster where performance matters, the SCM, OM and DN
comment|// metadata locations must be configured explicitly.
DECL|field|OZONE_METADATA_DIRS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_DIRS
init|=
literal|"ozone.metadata.dirs"
decl_stmt|;
DECL|field|HDDS_PROMETHEUS_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PROMETHEUS_ENABLED
init|=
literal|"hdds.prometheus.endpoint.enabled"
decl_stmt|;
DECL|field|HDDS_KEY_LEN
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_KEY_LEN
init|=
literal|"hdds.key.len"
decl_stmt|;
DECL|field|HDDS_DEFAULT_KEY_LEN
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_DEFAULT_KEY_LEN
init|=
literal|2048
decl_stmt|;
DECL|field|HDDS_KEY_ALGORITHM
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_KEY_ALGORITHM
init|=
literal|"hdds.key.algo"
decl_stmt|;
DECL|field|HDDS_DEFAULT_KEY_ALGORITHM
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_DEFAULT_KEY_ALGORITHM
init|=
literal|"RSA"
decl_stmt|;
DECL|field|HDDS_SECURITY_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_SECURITY_PROVIDER
init|=
literal|"hdds.security.provider"
decl_stmt|;
DECL|field|HDDS_DEFAULT_SECURITY_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_DEFAULT_SECURITY_PROVIDER
init|=
literal|"BC"
decl_stmt|;
DECL|field|HDDS_KEY_DIR_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_KEY_DIR_NAME
init|=
literal|"hdds.key.dir.name"
decl_stmt|;
DECL|field|HDDS_KEY_DIR_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_KEY_DIR_NAME_DEFAULT
init|=
literal|"keys"
decl_stmt|;
comment|// TODO : Talk to StorageIO classes and see if they can return a secure
comment|// storage location for each node.
DECL|field|HDDS_METADATA_DIR_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_METADATA_DIR_NAME
init|=
literal|"hdds.metadata.dir"
decl_stmt|;
DECL|field|HDDS_PRIVATE_KEY_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PRIVATE_KEY_FILE_NAME
init|=
literal|"hdds.priv.key.file.name"
decl_stmt|;
DECL|field|HDDS_PRIVATE_KEY_FILE_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PRIVATE_KEY_FILE_NAME_DEFAULT
init|=
literal|"private.pem"
decl_stmt|;
DECL|field|HDDS_PUBLIC_KEY_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PUBLIC_KEY_FILE_NAME
init|=
literal|"hdds.public.key.file"
operator|+
literal|".name"
decl_stmt|;
DECL|field|HDDS_PUBLIC_KEY_FILE_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_PUBLIC_KEY_FILE_NAME_DEFAULT
init|=
literal|"public.pem"
decl_stmt|;
comment|/**    * Maximum duration of certificates issued by SCM including Self-Signed Roots.    * The formats accepted are based on the ISO-8601 duration format PnDTnHnMn.nS    * Default value is 5 years and written as P1865D.    */
DECL|field|HDDS_X509_MAX_DURATION
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_X509_MAX_DURATION
init|=
literal|"hdds.x509.max.duration"
decl_stmt|;
comment|// Limit Certificate duration to a max value of 5 years.
DECL|field|HDDS_X509_MAX_DURATION_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_X509_MAX_DURATION_DEFAULT
init|=
literal|"P1865D"
decl_stmt|;
DECL|field|HDDS_X509_SIGNATURE_ALGO
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_X509_SIGNATURE_ALGO
init|=
literal|"hdds.x509.signature.algorithm"
decl_stmt|;
DECL|field|HDDS_X509_SIGNATURE_ALGO_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_X509_SIGNATURE_ALGO_DEFAULT
init|=
literal|"SHA256withRSA"
decl_stmt|;
DECL|field|HDDS_GRPC_BLOCK_TOKEN_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_GRPC_BLOCK_TOKEN_ENABLED
init|=
literal|"hdds.grpc.block.token.enabled"
decl_stmt|;
DECL|field|HDDS_GRPC_BLOCK_TOKEN_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|HDDS_GRPC_BLOCK_TOKEN_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|HDDS_X509_DIR_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_X509_DIR_NAME
init|=
literal|"hdds.x509.dir.name"
decl_stmt|;
DECL|field|HDDS_X509_DIR_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_X509_DIR_NAME_DEFAULT
init|=
literal|"certs"
decl_stmt|;
DECL|field|HDDS_X509_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_X509_FILE_NAME
init|=
literal|"hdds.x509.file.name"
decl_stmt|;
DECL|field|HDDS_X509_FILE_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_X509_FILE_NAME_DEFAULT
init|=
literal|"certificate.crt"
decl_stmt|;
comment|/**    * Do not instantiate.    */
DECL|method|HddsConfigKeys ()
specifier|private
name|HddsConfigKeys
parameter_list|()
block|{   }
DECL|field|HDDS_GRPC_TLS_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_GRPC_TLS_ENABLED
init|=
literal|"hdds.grpc.tls.enabled"
decl_stmt|;
DECL|field|HDDS_GRPC_TLS_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|HDDS_GRPC_TLS_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|HDDS_GRPC_MUTUAL_TLS_REQUIRED
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_GRPC_MUTUAL_TLS_REQUIRED
init|=
literal|"hdds.grpc.mutual.tls.required"
decl_stmt|;
DECL|field|HDDS_GRPC_MUTUAL_TLS_REQUIRED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|HDDS_GRPC_MUTUAL_TLS_REQUIRED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|HDDS_GRPC_TLS_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_GRPC_TLS_PROVIDER
init|=
literal|"hdds.grpc.tls.provider"
decl_stmt|;
DECL|field|HDDS_GRPC_TLS_PROVIDER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_GRPC_TLS_PROVIDER_DEFAULT
init|=
literal|"OPENSSL"
decl_stmt|;
DECL|field|HDDS_TRUST_STORE_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_TRUST_STORE_FILE_NAME
init|=
literal|"hdds.trust.cert.collection.file.name"
decl_stmt|;
DECL|field|HDDS_TRUST_STORE_FILE_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_TRUST_STORE_FILE_NAME_DEFAULT
init|=
literal|"ca.crt"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HDDS_SERVER_CERTIFICATE_CHAIN_FILE_NAME
name|HDDS_SERVER_CERTIFICATE_CHAIN_FILE_NAME
init|=
literal|"hdds.server.cert.chain.file.name"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HDDS_SERVER_CERTIFICATE_CHAIN_FILE_NAME_DEFAULT
name|HDDS_SERVER_CERTIFICATE_CHAIN_FILE_NAME_DEFAULT
init|=
literal|"server.crt"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HDDS_CLIENT_CERTIFICATE_CHAIN_FILE_NAME
name|HDDS_CLIENT_CERTIFICATE_CHAIN_FILE_NAME
init|=
literal|"hdds.client.cert.chain.file.name"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HDDS_CLIENT_CERTIFICATE_CHAIN_FILE_NAME_DEFAULT
name|HDDS_CLIENT_CERTIFICATE_CHAIN_FILE_NAME_DEFAULT
init|=
literal|"client.crt"
decl_stmt|;
DECL|field|HDDS_GRPC_TLS_TEST_CERT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_GRPC_TLS_TEST_CERT
init|=
literal|"hdds.grpc.tls"
operator|+
literal|".test_cert"
decl_stmt|;
DECL|field|HDDS_GRPC_TLS_TEST_CERT_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|HDDS_GRPC_TLS_TEST_CERT_DEFAULT
init|=
literal|false
decl_stmt|;
block|}
end_class

end_unit

