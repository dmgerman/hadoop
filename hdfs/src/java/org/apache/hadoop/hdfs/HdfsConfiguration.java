begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|conf
operator|.
name|Configuration
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
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * Adds deprecated keys into the configuration.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HdfsConfiguration
specifier|public
class|class
name|HdfsConfiguration
extends|extends
name|Configuration
block|{
static|static
block|{
name|addDeprecatedKeys
argument_list|()
expr_stmt|;
comment|// adds the default resources
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"hdfs-default.xml"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"hdfs-site.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|HdfsConfiguration ()
specifier|public
name|HdfsConfiguration
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|HdfsConfiguration (boolean loadDefaults)
specifier|public
name|HdfsConfiguration
parameter_list|(
name|boolean
name|loadDefaults
parameter_list|)
block|{
name|super
argument_list|(
name|loadDefaults
argument_list|)
expr_stmt|;
block|}
DECL|method|HdfsConfiguration (Configuration conf)
specifier|public
name|HdfsConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method is here so that when invoked, HdfsConfiguration is class-loaded if    * it hasn't already been previously loaded.  Upon loading the class, the static     * initializer block above will be executed to add the deprecated keys and to add    * the default resources.   It is safe for this method to be called multiple times     * as the static initializer block will only get invoked once.    *     * This replaces the previously, dangerous practice of other classes calling    * Configuration.addDefaultResource("hdfs-default.xml") directly without loading     * HdfsConfiguration class first, thereby skipping the key deprecation    */
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{   }
DECL|method|deprecate (String oldKey, String newKey)
specifier|private
specifier|static
name|void
name|deprecate
parameter_list|(
name|String
name|oldKey
parameter_list|,
name|String
name|newKey
parameter_list|)
block|{
name|Configuration
operator|.
name|addDeprecation
argument_list|(
name|oldKey
argument_list|,
operator|new
name|String
index|[]
block|{
name|newKey
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|addDeprecatedKeys ()
specifier|private
specifier|static
name|void
name|addDeprecatedKeys
parameter_list|()
block|{
name|deprecate
argument_list|(
literal|"dfs.backup.address"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.backup.http.address"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.balance.bandwidthPerSec"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.data.dir"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.http.address"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.https.address"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.max.objects"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_OBJECTS_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.name.dir"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.name.dir.restore"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_RESTORE_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.name.edits.dir"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.read.prefetch.size"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_READ_PREFETCH_SIZE_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.safemode.extension"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_EXTENSION_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.safemode.threshold.pct"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.secondary.http.address"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.socket.timeout"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"fs.checkpoint.dir"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_DIR_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"fs.checkpoint.edits.dir"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_EDITS_DIR_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"fs.checkpoint.period"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_PERIOD_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"fs.checkpoint.size"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKPOINT_SIZE_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.upgrade.permission"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_UPGRADE_PERMISSION_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"heartbeat.recheck.interval"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"StorageId"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_STORAGEID_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.https.client.keystore.resource"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.https.need.client.auth"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_HTTPS_NEED_AUTH_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"slave.host.name"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"session.id"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_METRICS_SESSION_ID_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.access.time.precision"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACCESSTIME_PRECISION_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.replication.considerLoad"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_CONSIDERLOAD_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.replication.interval"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.replication.min"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MIN_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.replication.pending.timeout.sec"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.max-repl-streams"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.permissions"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_PERMISSIONS_ENABLED_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.permissions.supergroup"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_PERMISSIONS_SUPERUSERGROUP_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.write.packet.size"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.block.size"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"dfs.datanode.max.xcievers"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_RECEIVER_THREADS_KEY
argument_list|)
expr_stmt|;
name|deprecate
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

