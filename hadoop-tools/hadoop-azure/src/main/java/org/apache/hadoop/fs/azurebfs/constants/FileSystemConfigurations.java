begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.constants
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
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
comment|/**  * Responsible to keep all the Azure Blob File System related configurations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|FileSystemConfigurations
specifier|public
specifier|final
class|class
name|FileSystemConfigurations
block|{
DECL|field|USER_HOME_DIRECTORY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|USER_HOME_DIRECTORY_PREFIX
init|=
literal|"/user"
decl_stmt|;
comment|// Retry parameter defaults.
DECL|field|DEFAULT_MIN_BACKOFF_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_BACKOFF_INTERVAL
init|=
literal|3
operator|*
literal|1000
decl_stmt|;
comment|// 3s
DECL|field|DEFAULT_MAX_BACKOFF_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_BACKOFF_INTERVAL
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
comment|// 30s
DECL|field|DEFAULT_BACKOFF_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BACKOFF_INTERVAL
init|=
literal|3
operator|*
literal|1000
decl_stmt|;
comment|// 3s
DECL|field|DEFAULT_MAX_RETRY_ATTEMPTS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_RETRY_ATTEMPTS
init|=
literal|30
decl_stmt|;
DECL|field|ONE_KB
specifier|private
specifier|static
specifier|final
name|int
name|ONE_KB
init|=
literal|1024
decl_stmt|;
DECL|field|ONE_MB
specifier|private
specifier|static
specifier|final
name|int
name|ONE_MB
init|=
name|ONE_KB
operator|*
name|ONE_KB
decl_stmt|;
comment|// Default upload and download buffer size
DECL|field|DEFAULT_WRITE_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_WRITE_BUFFER_SIZE
init|=
literal|8
operator|*
name|ONE_MB
decl_stmt|;
comment|// 8 MB
DECL|field|DEFAULT_READ_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_READ_BUFFER_SIZE
init|=
literal|4
operator|*
name|ONE_MB
decl_stmt|;
comment|// 4 MB
DECL|field|MIN_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|MIN_BUFFER_SIZE
init|=
literal|16
operator|*
name|ONE_KB
decl_stmt|;
comment|// 16 KB
DECL|field|MAX_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_BUFFER_SIZE
init|=
literal|100
operator|*
name|ONE_MB
decl_stmt|;
comment|// 100 MB
DECL|field|MAX_AZURE_BLOCK_SIZE
specifier|public
specifier|static
specifier|final
name|long
name|MAX_AZURE_BLOCK_SIZE
init|=
literal|512
operator|*
literal|1024
operator|*
literal|1024L
decl_stmt|;
DECL|field|AZURE_BLOCK_LOCATION_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_BLOCK_LOCATION_HOST_DEFAULT
init|=
literal|"localhost"
decl_stmt|;
DECL|field|MAX_CONCURRENT_READ_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|MAX_CONCURRENT_READ_THREADS
init|=
literal|12
decl_stmt|;
DECL|field|MAX_CONCURRENT_WRITE_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|MAX_CONCURRENT_WRITE_THREADS
init|=
literal|8
decl_stmt|;
DECL|field|DEFAULT_READ_TOLERATE_CONCURRENT_APPEND
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_READ_TOLERATE_CONCURRENT_APPEND
init|=
literal|false
decl_stmt|;
DECL|field|DEFAULT_AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
init|=
literal|false
decl_stmt|;
DECL|field|DEFAULT_AZURE_SKIP_USER_GROUP_METADATA_DURING_INITIALIZATION
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_AZURE_SKIP_USER_GROUP_METADATA_DURING_INITIALIZATION
init|=
literal|false
decl_stmt|;
DECL|field|DEFAULT_FS_AZURE_ATOMIC_RENAME_DIRECTORIES
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FS_AZURE_ATOMIC_RENAME_DIRECTORIES
init|=
literal|"/hbase"
decl_stmt|;
DECL|field|DEFAULT_READ_AHEAD_QUEUE_DEPTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_READ_AHEAD_QUEUE_DEPTH
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|DEFAULT_ENABLE_FLUSH
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_ENABLE_FLUSH
init|=
literal|true
decl_stmt|;
DECL|method|FileSystemConfigurations ()
specifier|private
name|FileSystemConfigurations
parameter_list|()
block|{}
block|}
end_class

end_unit

