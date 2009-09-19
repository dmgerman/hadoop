begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.kfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|kfs
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
name|fs
operator|.
name|CommonConfigurationKeys
import|;
end_import

begin_comment
comment|/**   * This class contains constants for configuration keys used  * in the kfs file system.   *  */
end_comment

begin_class
DECL|class|KFSConfigKeys
specifier|public
class|class
name|KFSConfigKeys
extends|extends
name|CommonConfigurationKeys
block|{
DECL|field|KFS_BLOCK_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KFS_BLOCK_SIZE_KEY
init|=
literal|"kfs.blocksize"
decl_stmt|;
DECL|field|KFS_BLOCK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|KFS_BLOCK_SIZE_DEFAULT
init|=
literal|64
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|KFS_REPLICATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KFS_REPLICATION_KEY
init|=
literal|"kfs.replication"
decl_stmt|;
DECL|field|KFS_REPLICATION_DEFAULT
specifier|public
specifier|static
specifier|final
name|short
name|KFS_REPLICATION_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|KFS_STREAM_BUFFER_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KFS_STREAM_BUFFER_SIZE_KEY
init|=
literal|"kfs.stream-buffer-size"
decl_stmt|;
DECL|field|KFS_STREAM_BUFFER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|KFS_STREAM_BUFFER_SIZE_DEFAULT
init|=
literal|4096
decl_stmt|;
DECL|field|KFS_BYTES_PER_CHECKSUM_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KFS_BYTES_PER_CHECKSUM_KEY
init|=
literal|"kfs.bytes-per-checksum"
decl_stmt|;
DECL|field|KFS_BYTES_PER_CHECKSUM_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|KFS_BYTES_PER_CHECKSUM_DEFAULT
init|=
literal|512
decl_stmt|;
DECL|field|KFS_CLIENT_WRITE_PACKET_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KFS_CLIENT_WRITE_PACKET_SIZE_KEY
init|=
literal|"kfs.client-write-packet-size"
decl_stmt|;
DECL|field|KFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|KFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
block|}
end_class

end_unit

