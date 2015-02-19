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
name|Locale
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|DFSUtil
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DataNodeLayoutVersion
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNodeLayoutVersion
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSDirectory
import|;
end_import

begin_comment
comment|/************************************  * Some handy constants  *   ************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HdfsConstants
specifier|public
class|class
name|HdfsConstants
block|{
comment|/* Hidden constructor */
DECL|method|HdfsConstants ()
specifier|protected
name|HdfsConstants
parameter_list|()
block|{   }
comment|/**    * HDFS Protocol Names:      */
DECL|field|CLIENT_NAMENODE_PROTOCOL_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_NAMENODE_PROTOCOL_NAME
init|=
literal|"org.apache.hadoop.hdfs.protocol.ClientProtocol"
decl_stmt|;
DECL|field|CLIENT_DATANODE_PROTOCOL_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_DATANODE_PROTOCOL_NAME
init|=
literal|"org.apache.hadoop.hdfs.protocol.ClientDatanodeProtocol"
decl_stmt|;
DECL|field|MIN_BLOCKS_FOR_WRITE
specifier|public
specifier|static
specifier|final
name|int
name|MIN_BLOCKS_FOR_WRITE
init|=
literal|1
decl_stmt|;
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
comment|//
comment|// Timeouts, constants
comment|//
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
DECL|field|LEASE_RECOVER_PERIOD
specifier|public
specifier|static
specifier|final
name|long
name|LEASE_RECOVER_PERIOD
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
comment|// in ms
comment|// We need to limit the length and depth of a path in the filesystem.
comment|// HADOOP-438
comment|// Currently we set the maximum length to 8k characters and the maximum depth
comment|// to 1k.
DECL|field|MAX_PATH_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|MAX_PATH_LENGTH
init|=
literal|8000
decl_stmt|;
DECL|field|MAX_PATH_DEPTH
specifier|public
specifier|static
specifier|final
name|int
name|MAX_PATH_DEPTH
init|=
literal|1000
decl_stmt|;
comment|// TODO should be conf injected?
DECL|field|DEFAULT_DATA_SOCKET_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_DATA_SOCKET_SIZE
init|=
literal|128
operator|*
literal|1024
decl_stmt|;
DECL|field|IO_FILE_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|IO_FILE_BUFFER_SIZE
init|=
operator|new
name|HdfsConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|)
decl_stmt|;
comment|// Used for writing header etc.
DECL|field|SMALL_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|SMALL_BUFFER_SIZE
init|=
name|Math
operator|.
name|min
argument_list|(
name|IO_FILE_BUFFER_SIZE
operator|/
literal|2
argument_list|,
literal|512
argument_list|)
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
comment|// SafeMode actions
DECL|enum|SafeModeAction
specifier|public
specifier|static
enum|enum
name|SafeModeAction
block|{
DECL|enumConstant|SAFEMODE_LEAVE
DECL|enumConstant|SAFEMODE_ENTER
DECL|enumConstant|SAFEMODE_GET
name|SAFEMODE_LEAVE
block|,
name|SAFEMODE_ENTER
block|,
name|SAFEMODE_GET
block|;   }
DECL|enum|RollingUpgradeAction
specifier|public
specifier|static
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
argument_list|<
name|String
argument_list|,
name|RollingUpgradeAction
argument_list|>
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
comment|/** Covert the given String to a RollingUpgradeAction. */
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
name|s
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|// type of the datanode report
DECL|enum|DatanodeReportType
specifier|public
specifier|static
enum|enum
name|DatanodeReportType
block|{
DECL|enumConstant|ALL
DECL|enumConstant|LIVE
DECL|enumConstant|DEAD
DECL|enumConstant|DECOMMISSIONING
name|ALL
block|,
name|LIVE
block|,
name|DEAD
block|,
name|DECOMMISSIONING
block|}
comment|// An invalid transaction ID that will never be seen in a real namesystem.
DECL|field|INVALID_TXID
specifier|public
specifier|static
specifier|final
name|long
name|INVALID_TXID
init|=
operator|-
literal|12345
decl_stmt|;
comment|// Number of generation stamps reserved for legacy blocks.
DECL|field|RESERVED_GENERATION_STAMPS_V1
specifier|public
specifier|static
specifier|final
name|long
name|RESERVED_GENERATION_STAMPS_V1
init|=
literal|1024L
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
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
comment|/**    * Current layout version for NameNode.    * Please see {@link NameNodeLayoutVersion.Feature} on adding new layout version.    */
DECL|field|NAMENODE_LAYOUT_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|NAMENODE_LAYOUT_VERSION
init|=
name|NameNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
decl_stmt|;
comment|/**    * Current layout version for DataNode.    * Please see {@link DataNodeLayoutVersion.Feature} on adding new layout version.    */
DECL|field|DATANODE_LAYOUT_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|DATANODE_LAYOUT_VERSION
init|=
name|DataNodeLayoutVersion
operator|.
name|CURRENT_LAYOUT_VERSION
decl_stmt|;
comment|/**    * Path components that are reserved in HDFS.    *<p>    * .reserved is only reserved under root ("/").    */
DECL|field|RESERVED_PATH_COMPONENTS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|RESERVED_PATH_COMPONENTS
init|=
operator|new
name|String
index|[]
block|{
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
block|,
name|FSDirectory
operator|.
name|DOT_RESERVED_STRING
block|}
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
DECL|field|DOT_SNAPSHOT_DIR_BYTES
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|DOT_SNAPSHOT_DIR_BYTES
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|DOT_SNAPSHOT_DIR
argument_list|)
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
DECL|field|MEMORY_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|MEMORY_STORAGE_POLICY_NAME
init|=
literal|"LAZY_PERSIST"
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
DECL|field|ONESSD_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ONESSD_STORAGE_POLICY_NAME
init|=
literal|"ONE_SSD"
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
DECL|field|WARM_STORAGE_POLICY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|WARM_STORAGE_POLICY_NAME
init|=
literal|"WARM"
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
DECL|field|MEMORY_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|MEMORY_STORAGE_POLICY_ID
init|=
literal|15
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
DECL|field|ONESSD_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|ONESSD_STORAGE_POLICY_ID
init|=
literal|10
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
DECL|field|WARM_STORAGE_POLICY_ID
specifier|public
specifier|static
specifier|final
name|byte
name|WARM_STORAGE_POLICY_ID
init|=
literal|5
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
block|}
end_class

end_unit

