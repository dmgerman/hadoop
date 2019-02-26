begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|ozone
operator|.
name|OzoneAcl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|TimeDuration
import|;
end_import

begin_comment
comment|/**  * Ozone Manager Constants.  */
end_comment

begin_class
DECL|class|OMConfigKeys
specifier|public
specifier|final
class|class
name|OMConfigKeys
block|{
comment|/**    * Never constructed.    */
DECL|method|OMConfigKeys ()
specifier|private
name|OMConfigKeys
parameter_list|()
block|{   }
comment|// Location where the OM stores its DB files. In the future we may support
comment|// multiple entries for performance (sharding)..
DECL|field|OZONE_OM_DB_DIRS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_DB_DIRS
init|=
literal|"ozone.om.db.dirs"
decl_stmt|;
DECL|field|OZONE_OM_HANDLER_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HANDLER_COUNT_KEY
init|=
literal|"ozone.om.handler.count.key"
decl_stmt|;
DECL|field|OZONE_OM_HANDLER_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OM_HANDLER_COUNT_DEFAULT
init|=
literal|20
decl_stmt|;
DECL|field|OZONE_OM_SERVICE_IDS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_SERVICE_IDS_KEY
init|=
literal|"ozone.om.service.ids"
decl_stmt|;
DECL|field|OZONE_OM_NODES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_NODES_KEY
init|=
literal|"ozone.om.nodes"
decl_stmt|;
DECL|field|OZONE_OM_NODE_ID_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_NODE_ID_KEY
init|=
literal|"ozone.om.node.id"
decl_stmt|;
DECL|field|OZONE_OM_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_ADDRESS_KEY
init|=
literal|"ozone.om.address"
decl_stmt|;
DECL|field|OZONE_OM_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_OM_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OM_PORT_DEFAULT
init|=
literal|9862
decl_stmt|;
DECL|field|OZONE_OM_HTTP_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HTTP_ENABLED_KEY
init|=
literal|"ozone.om.http.enabled"
decl_stmt|;
DECL|field|OZONE_OM_HTTP_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HTTP_BIND_HOST_KEY
init|=
literal|"ozone.om.http-bind-host"
decl_stmt|;
DECL|field|OZONE_OM_HTTPS_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HTTPS_BIND_HOST_KEY
init|=
literal|"ozone.om.https-bind-host"
decl_stmt|;
DECL|field|OZONE_OM_HTTP_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HTTP_ADDRESS_KEY
init|=
literal|"ozone.om.http-address"
decl_stmt|;
DECL|field|OZONE_OM_HTTPS_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HTTPS_ADDRESS_KEY
init|=
literal|"ozone.om.https-address"
decl_stmt|;
DECL|field|OZONE_OM_KEYTAB_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_KEYTAB_FILE
init|=
literal|"ozone.om.keytab.file"
decl_stmt|;
DECL|field|OZONE_OM_HTTP_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HTTP_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_OM_HTTP_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OM_HTTP_BIND_PORT_DEFAULT
init|=
literal|9874
decl_stmt|;
DECL|field|OZONE_OM_HTTPS_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OM_HTTPS_BIND_PORT_DEFAULT
init|=
literal|9875
decl_stmt|;
comment|// LevelDB cache file uses an off-heap cache in LevelDB of 128 MB.
DECL|field|OZONE_OM_DB_CACHE_SIZE_MB
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_DB_CACHE_SIZE_MB
init|=
literal|"ozone.om.db.cache.size.mb"
decl_stmt|;
DECL|field|OZONE_OM_DB_CACHE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OM_DB_CACHE_SIZE_DEFAULT
init|=
literal|128
decl_stmt|;
DECL|field|OZONE_OM_USER_MAX_VOLUME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_USER_MAX_VOLUME
init|=
literal|"ozone.om.user.max.volume"
decl_stmt|;
DECL|field|OZONE_OM_USER_MAX_VOLUME_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OM_USER_MAX_VOLUME_DEFAULT
init|=
literal|1024
decl_stmt|;
comment|// OM Default user/group permissions
DECL|field|OZONE_OM_USER_RIGHTS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_USER_RIGHTS
init|=
literal|"ozone.om.user.rights"
decl_stmt|;
DECL|field|OZONE_OM_USER_RIGHTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|OzoneAcl
operator|.
name|OzoneACLRights
name|OZONE_OM_USER_RIGHTS_DEFAULT
init|=
name|OzoneAcl
operator|.
name|OzoneACLRights
operator|.
name|READ_WRITE
decl_stmt|;
DECL|field|OZONE_OM_GROUP_RIGHTS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_GROUP_RIGHTS
init|=
literal|"ozone.om.group.rights"
decl_stmt|;
DECL|field|OZONE_OM_GROUP_RIGHTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|OzoneAcl
operator|.
name|OzoneACLRights
name|OZONE_OM_GROUP_RIGHTS_DEFAULT
init|=
name|OzoneAcl
operator|.
name|OzoneACLRights
operator|.
name|READ_WRITE
decl_stmt|;
DECL|field|OZONE_KEY_DELETING_LIMIT_PER_TASK
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KEY_DELETING_LIMIT_PER_TASK
init|=
literal|"ozone.key.deleting.limit.per.task"
decl_stmt|;
DECL|field|OZONE_KEY_DELETING_LIMIT_PER_TASK_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KEY_DELETING_LIMIT_PER_TASK_DEFAULT
init|=
literal|1000
decl_stmt|;
DECL|field|OZONE_OM_METRICS_SAVE_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_METRICS_SAVE_INTERVAL
init|=
literal|"ozone.om.save.metrics.interval"
decl_stmt|;
DECL|field|OZONE_OM_METRICS_SAVE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_METRICS_SAVE_INTERVAL_DEFAULT
init|=
literal|"5m"
decl_stmt|;
comment|/**    * OM Ratis related configurations.    */
DECL|field|OZONE_OM_RATIS_ENABLE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_ENABLE_KEY
init|=
literal|"ozone.om.ratis.enable"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|OZONE_OM_RATIS_ENABLE_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|OZONE_OM_RATIS_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_PORT_KEY
init|=
literal|"ozone.om.ratis.port"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OM_RATIS_PORT_DEFAULT
init|=
literal|9872
decl_stmt|;
DECL|field|OZONE_OM_RATIS_RPC_TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_RPC_TYPE_KEY
init|=
literal|"ozone.om.ratis.rpc.type"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_RPC_TYPE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_RPC_TYPE_DEFAULT
init|=
literal|"GRPC"
decl_stmt|;
comment|// OM Ratis Log configurations
DECL|field|OZONE_OM_RATIS_STORAGE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_STORAGE_DIR
init|=
literal|"ozone.om.ratis.storage.dir"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_SEGMENT_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_SEGMENT_SIZE_KEY
init|=
literal|"ozone.om.ratis.segment.size"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_SEGMENT_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_SEGMENT_SIZE_DEFAULT
init|=
literal|"16KB"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
init|=
literal|"ozone.om.ratis.segment.preallocated.size"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
init|=
literal|"16KB"
decl_stmt|;
comment|// OM Ratis Log Appender configurations
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_OM_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS
name|OZONE_OM_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS
init|=
literal|"ozone.om.ratis.log.appender.queue.num-elements"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
DECL|field|OZONE_OM_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS_DEFAULT
name|OZONE_OM_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS_DEFAULT
init|=
literal|1024
decl_stmt|;
DECL|field|OZONE_OM_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT
init|=
literal|"ozone.om.ratis.log.appender.queue.byte-limit"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_OM_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT_DEFAULT
name|OZONE_OM_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT_DEFAULT
init|=
literal|"32MB"
decl_stmt|;
comment|// OM Ratis server configurations
DECL|field|OZONE_OM_RATIS_SERVER_REQUEST_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_SERVER_REQUEST_TIMEOUT_KEY
init|=
literal|"ozone.om.ratis.server.request.timeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|OZONE_OM_RATIS_SERVER_REQUEST_TIMEOUT_DEFAULT
name|OZONE_OM_RATIS_SERVER_REQUEST_TIMEOUT_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_OM_RATIS_SERVER_RETRY_CACHE_TIMEOUT_KEY
name|OZONE_OM_RATIS_SERVER_RETRY_CACHE_TIMEOUT_KEY
init|=
literal|"ozone.om.ratis.server.retry.cache.timeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|OZONE_OM_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DEFAULT
name|OZONE_OM_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|600000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
DECL|field|OZONE_OM_RATIS_MINIMUM_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_MINIMUM_TIMEOUT_KEY
init|=
literal|"ozone.om.ratis.minimum.timeout"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_MINIMUM_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|TimeDuration
name|OZONE_OM_RATIS_MINIMUM_TIMEOUT_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
comment|// OM Ratis client configurations
DECL|field|OZONE_OM_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
init|=
literal|"ozone.om.ratis.client.request.timeout.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|OZONE_OM_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
name|OZONE_OM_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
DECL|field|OZONE_OM_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
init|=
literal|"ozone.om.ratis.client.request.max.retries"
decl_stmt|;
DECL|field|OZONE_OM_RATIS_CLIENT_REQUEST_MAX_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OM_RATIS_CLIENT_REQUEST_MAX_RETRIES_DEFAULT
init|=
literal|180
decl_stmt|;
DECL|field|OZONE_OM_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
init|=
literal|"ozone.om.ratis.client.request.retry.interval"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|OZONE_OM_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_DEFAULT
name|OZONE_OM_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
comment|// OM Ratis Leader Election configurations
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_OM_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
name|OZONE_OM_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
init|=
literal|"ozone.om.leader.election.minimum.timeout.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|OZONE_OM_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
name|OZONE_OM_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|field|OZONE_OM_RATIS_SERVER_FAILURE_TIMEOUT_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_RATIS_SERVER_FAILURE_TIMEOUT_DURATION_KEY
init|=
literal|"ozone.om.ratis.server.failure.timeout.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|OZONE_OM_RATIS_SERVER_FAILURE_TIMEOUT_DURATION_DEFAULT
name|OZONE_OM_RATIS_SERVER_FAILURE_TIMEOUT_DURATION_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|120
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|field|OZONE_OM_KERBEROS_KEYTAB_FILE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_KERBEROS_KEYTAB_FILE_KEY
init|=
literal|"ozone.om."
operator|+
literal|"kerberos.keytab.file"
decl_stmt|;
DECL|field|OZONE_OM_KERBEROS_PRINCIPAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_KERBEROS_PRINCIPAL_KEY
init|=
literal|"ozone.om"
operator|+
literal|".kerberos.principal"
decl_stmt|;
DECL|field|OZONE_OM_HTTP_KERBEROS_KEYTAB_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HTTP_KERBEROS_KEYTAB_FILE
init|=
literal|"ozone.om.http.kerberos.keytab.file"
decl_stmt|;
DECL|field|OZONE_OM_HTTP_KERBEROS_PRINCIPAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_HTTP_KERBEROS_PRINCIPAL_KEY
init|=
literal|"ozone.om.http.kerberos.principal"
decl_stmt|;
comment|// Delegation token related keys
DECL|field|DELEGATION_REMOVER_SCAN_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_REMOVER_SCAN_INTERVAL_KEY
init|=
literal|"ozone.manager.delegation.remover.scan.interval"
decl_stmt|;
DECL|field|DELEGATION_REMOVER_SCAN_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DELEGATION_REMOVER_SCAN_INTERVAL_DEFAULT
init|=
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|DELEGATION_TOKEN_RENEW_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_RENEW_INTERVAL_KEY
init|=
literal|"ozone.manager.delegation.token.renew-interval"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_RENEW_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DELEGATION_TOKEN_RENEW_INTERVAL_DEFAULT
init|=
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 day = 86400000 ms
DECL|field|DELEGATION_TOKEN_MAX_LIFETIME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_MAX_LIFETIME_KEY
init|=
literal|"ozone.manager.delegation.token.max-lifetime"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_MAX_LIFETIME_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DELEGATION_TOKEN_MAX_LIFETIME_DEFAULT
init|=
literal|7
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 7 days
DECL|field|OZONE_DB_CHECKPOINT_TRANSFER_RATE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_DB_CHECKPOINT_TRANSFER_RATE_KEY
init|=
literal|"ozone.manager.db.checkpoint.transfer.bandwidthPerSec"
decl_stmt|;
DECL|field|OZONE_DB_CHECKPOINT_TRANSFER_RATE_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|OZONE_DB_CHECKPOINT_TRANSFER_RATE_DEFAULT
init|=
literal|0
decl_stmt|;
comment|//no throttling
comment|// Comma separated acls (users, groups) allowing clients accessing
comment|// OM client protocol
comment|// when hadoop.security.authorization is true, this needs to be set in
comment|// hadoop-policy.xml, "*" allows all users/groups to access.
DECL|field|OZONE_OM_SECURITY_CLIENT_PROTOCOL_ACL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OM_SECURITY_CLIENT_PROTOCOL_ACL
init|=
literal|"ozone.om.security.client.protocol.acl"
decl_stmt|;
block|}
end_class

end_unit

