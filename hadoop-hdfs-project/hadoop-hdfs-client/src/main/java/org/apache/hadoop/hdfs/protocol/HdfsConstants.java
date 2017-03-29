begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HdfsConstants
specifier|public
specifier|final
class|class
name|HdfsConstants
block|{
comment|// Long that indicates "leave current quota unchanged"
DECL|field|QUOTA_DONT_SET
specifier|public
specifier|static
specifier|final
name|long
name|QUOTA_DONT_SET
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|QUOTA_RESET
specifier|public
specifier|static
specifier|final
name|long
name|QUOTA_RESET
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|BYTES_IN_INTEGER
specifier|public
specifier|static
specifier|final
name|int
name|BYTES_IN_INTEGER
init|=
name|Integer
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
comment|/**    * URI Scheme for hdfs://namenode/ URIs.    */
DECL|field|HDFS_URI_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|HDFS_URI_SCHEME
init|=
literal|"hdfs"
decl_stmt|;
DECL|field|MEMORY_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|MEMORY_STORAGE_POLICY_ID
init|=
literal|15
decl_stmt|;
DECL|field|MEMORY_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|MEMORY_STORAGE_POLICY_NAME
init|=
literal|"LAZY_PERSIST"
decl_stmt|;
DECL|field|ALLSSD_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|ALLSSD_STORAGE_POLICY_ID
init|=
literal|12
decl_stmt|;
DECL|field|ALLSSD_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ALLSSD_STORAGE_POLICY_NAME
init|=
literal|"ALL_SSD"
decl_stmt|;
DECL|field|ONESSD_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|ONESSD_STORAGE_POLICY_ID
init|=
literal|10
decl_stmt|;
DECL|field|ONESSD_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ONESSD_STORAGE_POLICY_NAME
init|=
literal|"ONE_SSD"
decl_stmt|;
DECL|field|HOT_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|HOT_STORAGE_POLICY_ID
init|=
literal|7
decl_stmt|;
DECL|field|HOT_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HOT_STORAGE_POLICY_NAME
init|=
literal|"HOT"
decl_stmt|;
DECL|field|WARM_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|WARM_STORAGE_POLICY_ID
init|=
literal|5
decl_stmt|;
DECL|field|WARM_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|WARM_STORAGE_POLICY_NAME
init|=
literal|"WARM"
decl_stmt|;
DECL|field|COLD_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|COLD_STORAGE_POLICY_ID
init|=
literal|2
decl_stmt|;
DECL|field|COLD_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COLD_STORAGE_POLICY_NAME
init|=
literal|"COLD"
decl_stmt|;
comment|// branch HDFS-9806 XXX temporary until HDFS-7076
DECL|field|PROVIDED_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|PROVIDED_STORAGE_POLICY_ID
init|=
literal|1
decl_stmt|;
DECL|field|PROVIDED_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|PROVIDED_STORAGE_POLICY_NAME
init|=
literal|"PROVIDED"
decl_stmt|;
DECL|field|DEFAULT_DATA_SOCKET_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_DATA_SOCKET_SIZE
init|=
literal|0
decl_stmt|;
comment|/**    * A special path component contained in the path for a snapshot file/dir    */
DECL|field|DOT_SNAPSHOT_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DOT_SNAPSHOT_DIR
init|=
literal|".snapshot"
decl_stmt|;
DECL|field|SEPARATOR_DOT_SNAPSHOT_DIR
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR_DOT_SNAPSHOT_DIR
init|=
name|Path
operator|.
name|SEPARATOR
operator|+
name|DOT_SNAPSHOT_DIR
decl_stmt|;
DECL|field|SEPARATOR_DOT_SNAPSHOT_DIR_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR_DOT_SNAPSHOT_DIR_SEPARATOR
init|=
name|Path
operator|.
name|SEPARATOR
operator|+
name|DOT_SNAPSHOT_DIR
operator|+
name|Path
operator|.
name|SEPARATOR
decl_stmt|;
DECL|field|DOT_RESERVED_STRING
specifier|public
specifier|final
specifier|static
name|String
name|DOT_RESERVED_STRING
init|=
literal|".reserved"
decl_stmt|;
DECL|field|DOT_RESERVED_PATH_PREFIX
specifier|public
specifier|final
specifier|static
name|String
name|DOT_RESERVED_PATH_PREFIX
init|=
name|Path
operator|.
name|SEPARATOR
operator|+
name|DOT_RESERVED_STRING
decl_stmt|;
DECL|field|DOT_INODES_STRING
specifier|public
specifier|final
specifier|static
name|String
name|DOT_INODES_STRING
init|=
literal|".inodes"
decl_stmt|;
comment|/**    * Generation stamp of blocks that pre-date the introduction    * of a generation stamp.    */
DECL|field|GRANDFATHER_GENERATION_STAMP
specifier|public
specifier|static
specifier|final
name|long
name|GRANDFATHER_GENERATION_STAMP
init|=
literal|0
decl_stmt|;
comment|/**    * The inode id validation of lease check will be skipped when the request    * uses GRANDFATHER_INODE_ID for backward compatibility.    */
DECL|field|GRANDFATHER_INODE_ID
specifier|public
specifier|static
specifier|final
name|long
name|GRANDFATHER_INODE_ID
init|=
literal|0
decl_stmt|;
DECL|field|BLOCK_STORAGE_POLICY_ID_UNSPECIFIED
specifier|public
specifier|static
specifier|final
name|byte
name|BLOCK_STORAGE_POLICY_ID_UNSPECIFIED
init|=
literal|0
decl_stmt|;
comment|/**    * A prefix put before the namenode URI inside the "service" field    * of a delgation token, indicating that the URI is a logical (HA)    * URI.    */
DECL|field|HA_DT_SERVICE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|HA_DT_SERVICE_PREFIX
init|=
literal|"ha-"
decl_stmt|;
comment|// The name of the SafeModeException. FileSystem should retry if it sees
comment|// the below exception in RPC
DECL|field|SAFEMODE_EXCEPTION_CLASS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SAFEMODE_EXCEPTION_CLASS_NAME
init|=
literal|"org.apache.hadoop.hdfs.server.namenode.SafeModeException"
decl_stmt|;
comment|/**    * HDFS Protocol Names:    */
DECL|field|CLIENT_NAMENODE_PROTOCOL_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_NAMENODE_PROTOCOL_NAME
init|=
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol"
decl_stmt|;
comment|// Timeouts for communicating with DataNode for streaming writes/reads
DECL|field|READ_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|READ_TIMEOUT
init|=
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|READ_TIMEOUT_EXTENSION
specifier|public
specifier|static
specifier|final
name|int
name|READ_TIMEOUT_EXTENSION
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
DECL|field|WRITE_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|WRITE_TIMEOUT
init|=
literal|8
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|//for write pipeline
DECL|field|WRITE_TIMEOUT_EXTENSION
specifier|public
specifier|static
specifier|final
name|int
name|WRITE_TIMEOUT_EXTENSION
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
comment|/**    * For a HDFS client to write to a file, a lease is granted; During the lease    * period, no other client can write to the file. The writing client can    * periodically renew the lease. When the file is closed, the lease is    * revoked. The lease duration is bound by this soft limit and a    * {@link HdfsConstants#LEASE_HARDLIMIT_PERIOD hard limit}. Until the    * soft limit expires, the writer has sole write access to the file. If the    * soft limit expires and the client fails to close the file or renew the    * lease, another client can preempt the lease.    */
DECL|field|LEASE_SOFTLIMIT_PERIOD
specifier|public
specifier|static
specifier|final
name|long
name|LEASE_SOFTLIMIT_PERIOD
init|=
literal|60
operator|*
literal|1000
decl_stmt|;
comment|/**    * For a HDFS client to write to a file, a lease is granted; During the lease    * period, no other client can write to the file. The writing client can    * periodically renew the lease. When the file is closed, the lease is    * revoked. The lease duration is bound by a    * {@link HdfsConstants#LEASE_SOFTLIMIT_PERIOD soft limit} and this hard    * limit. If after the hard limit expires and the client has failed to renew    * the lease, HDFS assumes that the client has quit and will automatically    * close the file on behalf of the writer, and recover the lease.    */
DECL|field|LEASE_HARDLIMIT_PERIOD
specifier|public
specifier|static
specifier|final
name|long
name|LEASE_HARDLIMIT_PERIOD
init|=
literal|60
operator|*
name|LEASE_SOFTLIMIT_PERIOD
decl_stmt|;
comment|// SafeMode actions
DECL|enum|SafeModeAction
specifier|public
enum|enum
name|SafeModeAction
block|{
DECL|enumConstant|SAFEMODE_LEAVE
DECL|enumConstant|SAFEMODE_ENTER
DECL|enumConstant|SAFEMODE_GET
DECL|enumConstant|SAFEMODE_FORCE_EXIT
name|SAFEMODE_LEAVE
block|,
name|SAFEMODE_ENTER
block|,
name|SAFEMODE_GET
block|,
name|SAFEMODE_FORCE_EXIT
block|}
DECL|enum|RollingUpgradeAction
specifier|public
enum|enum
name|RollingUpgradeAction
block|{
DECL|enumConstant|QUERY
DECL|enumConstant|PREPARE
DECL|enumConstant|FINALIZE
name|QUERY
block|,
name|PREPARE
block|,
name|FINALIZE
block|;
DECL|field|MAP
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RollingUpgradeAction
argument_list|>
name|MAP
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|MAP
operator|.
name|put
argument_list|(
literal|""
argument_list|,
name|QUERY
argument_list|)
expr_stmt|;
for|for
control|(
name|RollingUpgradeAction
name|a
range|:
name|values
argument_list|()
control|)
block|{
name|MAP
operator|.
name|put
argument_list|(
name|a
operator|.
name|name
argument_list|()
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Convert the given String to a RollingUpgradeAction. */
DECL|method|fromString (String s)
specifier|public
specifier|static
name|RollingUpgradeAction
name|fromString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|MAP
operator|.
name|get
argument_list|(
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|// type of the datanode report
DECL|enum|DatanodeReportType
specifier|public
enum|enum
name|DatanodeReportType
block|{
DECL|enumConstant|ALL
DECL|enumConstant|LIVE
DECL|enumConstant|DEAD
DECL|enumConstant|DECOMMISSIONING
DECL|enumConstant|ENTERING_MAINTENANCE
DECL|enumConstant|IN_MAINTENANCE
name|ALL
block|,
name|LIVE
block|,
name|DEAD
block|,
name|DECOMMISSIONING
block|,
name|ENTERING_MAINTENANCE
block|,
name|IN_MAINTENANCE
block|}
comment|/**    * Re-encrypt encryption zone actions.    */
DECL|enum|ReencryptAction
specifier|public
enum|enum
name|ReencryptAction
block|{
DECL|enumConstant|CANCEL
DECL|enumConstant|START
name|CANCEL
block|,
name|START
block|}
comment|/* Hidden constructor */
DECL|method|HdfsConstants ()
specifier|protected
name|HdfsConstants
parameter_list|()
block|{   }
block|}
end_class

end_unit

