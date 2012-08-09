begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ftp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ftp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|classification
operator|.
name|InterfaceStability
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
name|CommonConfigurationKeys
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
name|FsServerDefaults
import|;
end_import

begin_comment
comment|/**   * This class contains constants for configuration keys used  * in the ftp file system.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FtpConfigKeys
specifier|public
class|class
name|FtpConfigKeys
extends|extends
name|CommonConfigurationKeys
block|{
DECL|field|BLOCK_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_SIZE_KEY
init|=
literal|"ftp.blocksize"
decl_stmt|;
DECL|field|BLOCK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|BLOCK_SIZE_DEFAULT
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
DECL|field|REPLICATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|REPLICATION_KEY
init|=
literal|"ftp.replication"
decl_stmt|;
DECL|field|REPLICATION_DEFAULT
specifier|public
specifier|static
specifier|final
name|short
name|REPLICATION_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|STREAM_BUFFER_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_BUFFER_SIZE_KEY
init|=
literal|"ftp.stream-buffer-size"
decl_stmt|;
DECL|field|STREAM_BUFFER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|STREAM_BUFFER_SIZE_DEFAULT
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|BYTES_PER_CHECKSUM_KEY
specifier|public
specifier|static
specifier|final
name|String
name|BYTES_PER_CHECKSUM_KEY
init|=
literal|"ftp.bytes-per-checksum"
decl_stmt|;
DECL|field|BYTES_PER_CHECKSUM_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|BYTES_PER_CHECKSUM_DEFAULT
init|=
literal|512
decl_stmt|;
DECL|field|CLIENT_WRITE_PACKET_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_WRITE_PACKET_SIZE_KEY
init|=
literal|"ftp.client-write-packet-size"
decl_stmt|;
DECL|field|CLIENT_WRITE_PACKET_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|CLIENT_WRITE_PACKET_SIZE_DEFAULT
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|ENCRYPT_DATA_TRANSFER_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|ENCRYPT_DATA_TRANSFER_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|method|getServerDefaults ()
specifier|protected
specifier|static
name|FsServerDefaults
name|getServerDefaults
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FsServerDefaults
argument_list|(
name|BLOCK_SIZE_DEFAULT
argument_list|,
name|BYTES_PER_CHECKSUM_DEFAULT
argument_list|,
name|CLIENT_WRITE_PACKET_SIZE_DEFAULT
argument_list|,
name|REPLICATION_DEFAULT
argument_list|,
name|STREAM_BUFFER_SIZE_DEFAULT
argument_list|,
name|ENCRYPT_DATA_TRANSFER_DEFAULT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

