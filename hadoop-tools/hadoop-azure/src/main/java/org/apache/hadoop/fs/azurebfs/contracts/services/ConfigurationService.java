begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.services
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
name|contracts
operator|.
name|services
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|ConfigurationPropertyNotFoundException
import|;
end_import

begin_comment
comment|/**  * Configuration service collects required Azure Hadoop configurations and provides it to the consumers.  */
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
DECL|interface|ConfigurationService
specifier|public
interface|interface
name|ConfigurationService
extends|extends
name|InjectableService
block|{
comment|/**    * Checks if ABFS is running from Emulator;    * @return is emulator mode.    */
DECL|method|isEmulator ()
name|boolean
name|isEmulator
parameter_list|()
function_decl|;
comment|/**    * Retrieves storage secure mode from Hadoop configuration;    * @return storage secure mode;    */
DECL|method|isSecureMode ()
name|boolean
name|isSecureMode
parameter_list|()
function_decl|;
comment|/**    * Retrieves storage account key for provided account name from Hadoop configuration.    * @param accountName the account name to retrieve the key.    * @return storage account key;    */
DECL|method|getStorageAccountKey (String accountName)
name|String
name|getStorageAccountKey
parameter_list|(
name|String
name|accountName
parameter_list|)
throws|throws
name|ConfigurationPropertyNotFoundException
function_decl|;
comment|/**    * Returns Hadoop configuration.    * @return Hadoop configuration.    */
DECL|method|getConfiguration ()
name|Configuration
name|getConfiguration
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured write buffer size    * @return the size of the write buffer    */
DECL|method|getWriteBufferSize ()
name|int
name|getWriteBufferSize
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured read buffer size    * @return the size of the read buffer    */
DECL|method|getReadBufferSize ()
name|int
name|getReadBufferSize
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured min backoff interval    * @return min backoff interval    */
DECL|method|getMinBackoffIntervalMilliseconds ()
name|int
name|getMinBackoffIntervalMilliseconds
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured max backoff interval    * @return max backoff interval    */
DECL|method|getMaxBackoffIntervalMilliseconds ()
name|int
name|getMaxBackoffIntervalMilliseconds
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured backoff interval    * @return backoff interval    */
DECL|method|getBackoffIntervalMilliseconds ()
name|int
name|getBackoffIntervalMilliseconds
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured num of retries    * @return num of retries    */
DECL|method|getMaxIoRetries ()
name|int
name|getMaxIoRetries
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured azure block size    * @return azure block size    */
DECL|method|getAzureBlockSize ()
name|long
name|getAzureBlockSize
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured azure block location host    * @return azure block location host    */
DECL|method|getAzureBlockLocationHost ()
name|String
name|getAzureBlockLocationHost
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured number of concurrent threads    * @return number of concurrent write threads    */
DECL|method|getMaxConcurrentWriteThreads ()
name|int
name|getMaxConcurrentWriteThreads
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured number of concurrent threads    * @return number of concurrent read threads    */
DECL|method|getMaxConcurrentReadThreads ()
name|int
name|getMaxConcurrentReadThreads
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured boolean for tolerating out of band writes to files    * @return configured boolean for tolerating out of band writes to files    */
DECL|method|getTolerateOobAppends ()
name|boolean
name|getTolerateOobAppends
parameter_list|()
function_decl|;
comment|/**    * Retrieves the comma-separated list of directories to receive special treatment so that folder    * rename is made atomic. The default value for this setting is just '/hbase'.    * Example directories list :<value>/hbase,/data</value>    * @see<a href="https://hadoop.apache.org/docs/stable/hadoop-azure/index.html#Configuring_Credentials">AtomicRenameProperty</a>    * @return atomic rename directories    */
DECL|method|getAzureAtomicRenameDirs ()
name|String
name|getAzureAtomicRenameDirs
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured boolean for creating remote file system during initialization    * @return configured boolean for creating remote file system during initialization    */
DECL|method|getCreateRemoteFileSystemDuringInitialization ()
name|boolean
name|getCreateRemoteFileSystemDuringInitialization
parameter_list|()
function_decl|;
comment|/**    * Retrieves configured value of read ahead queue    * @return depth of read ahead    */
DECL|method|getReadAheadQueueDepth ()
name|int
name|getReadAheadQueueDepth
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

