begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|yarn
operator|.
name|server
operator|.
name|records
operator|.
name|Version
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|RMStateVersionIncompatibleException
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|CapacityScheduler
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * YarnConfigurationStore exposes the methods needed for retrieving and  * persisting {@link CapacityScheduler} configuration via key-value  * using write-ahead logging. When configuration mutation is requested, caller  * should first log it with {@code logMutation}, which persists this pending  * mutation. This mutation is merged to the persisted configuration only after  * {@code confirmMutation} is called.  *  * On startup/recovery, caller should call {@code retrieve} to get all  * confirmed mutations, then get pending mutations which were not confirmed via  * {@code getPendingMutations}, and replay/confirm them via  * {@code confirmMutation} as in the normal case.  */
end_comment

begin_class
DECL|class|YarnConfigurationStore
specifier|public
specifier|abstract
class|class
name|YarnConfigurationStore
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|YarnConfigurationStore
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * LogMutation encapsulates the fields needed for configuration mutation    * audit logging and recovery.    */
DECL|class|LogMutation
specifier|static
class|class
name|LogMutation
implements|implements
name|Serializable
block|{
DECL|field|updates
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|updates
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
comment|/**      * Create log mutation.      * @param updates key-value configuration updates      * @param user user who requested configuration change      */
DECL|method|LogMutation (Map<String, String> updates, String user)
name|LogMutation
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|updates
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|updates
operator|=
name|updates
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
comment|/**      * Get key-value configuration updates.      * @return map of configuration updates      */
DECL|method|getUpdates ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUpdates
parameter_list|()
block|{
return|return
name|updates
return|;
block|}
comment|/**      * Get user who requested configuration change.      * @return user who requested configuration change      */
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
block|}
comment|/**    * Initialize the configuration store, with schedConf as the initial    * scheduler configuration. If a persisted store already exists, use the    * scheduler configuration stored there, and ignore schedConf.    * @param conf configuration to initialize store with    * @param schedConf Initial key-value scheduler configuration to persist.    * @param rmContext RMContext for this configuration store    * @throws IOException if initialization fails    */
DECL|method|initialize (Configuration conf, Configuration schedConf, RMContext rmContext)
specifier|public
specifier|abstract
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Configuration
name|schedConf
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Closes the configuration store, releasing any required resources.    * @throws IOException on failure to close    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
comment|/**    * Logs the configuration change to backing store.    * @param logMutation configuration change to be persisted in write ahead log    * @throws IOException if logging fails    */
DECL|method|logMutation (LogMutation logMutation)
specifier|public
specifier|abstract
name|void
name|logMutation
parameter_list|(
name|LogMutation
name|logMutation
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Should be called after {@code logMutation}. Gets the pending mutation    * last logged by {@code logMutation} and marks the mutation as persisted (no    * longer pending). If isValid is true, merge the mutation with the persisted    * configuration.    * @param isValid if true, update persisted configuration with pending    *                mutation.    * @throws Exception if mutation confirmation fails    */
DECL|method|confirmMutation (boolean isValid)
specifier|public
specifier|abstract
name|void
name|confirmMutation
parameter_list|(
name|boolean
name|isValid
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Retrieve the persisted configuration.    * @return configuration as key-value    */
DECL|method|retrieve ()
specifier|public
specifier|abstract
name|Configuration
name|retrieve
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Format the persisted configuration.    * @throws IOException on failure to format    */
DECL|method|format ()
specifier|public
specifier|abstract
name|void
name|format
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Get the last updated config version.    * @return Last updated config version.    */
DECL|method|getConfigVersion ()
specifier|public
specifier|abstract
name|long
name|getConfigVersion
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Get a list of confirmed configuration mutations starting from a given id.    * @param fromId id from which to start getting mutations, inclusive    * @return list of configuration mutations    */
DECL|method|getConfirmedConfHistory (long fromId)
specifier|public
specifier|abstract
name|List
argument_list|<
name|LogMutation
argument_list|>
name|getConfirmedConfHistory
parameter_list|(
name|long
name|fromId
parameter_list|)
function_decl|;
comment|/**    * Get schema version of persisted conf store, for detecting compatibility    * issues when changing conf store schema.    * @return Schema version currently used by the persisted configuration store.    * @throws Exception On version fetch failure    */
DECL|method|getConfStoreVersion ()
specifier|protected
specifier|abstract
name|Version
name|getConfStoreVersion
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Persist the hard-coded schema version to the conf store.    * @throws Exception On storage failure    */
DECL|method|storeVersion ()
specifier|protected
specifier|abstract
name|void
name|storeVersion
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Get the hard-coded schema version, for comparison against the schema    * version currently persisted.    * @return Current hard-coded schema version    */
DECL|method|getCurrentVersion ()
specifier|protected
specifier|abstract
name|Version
name|getCurrentVersion
parameter_list|()
function_decl|;
DECL|method|checkVersion ()
specifier|public
name|void
name|checkVersion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO this was taken from RMStateStore. Should probably refactor
name|Version
name|loadedVersion
init|=
name|getConfStoreVersion
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loaded configuration store version info "
operator|+
name|loadedVersion
argument_list|)
expr_stmt|;
if|if
condition|(
name|loadedVersion
operator|!=
literal|null
operator|&&
name|loadedVersion
operator|.
name|equals
argument_list|(
name|getCurrentVersion
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// if there is no version info, treat it as CURRENT_VERSION_INFO;
if|if
condition|(
name|loadedVersion
operator|==
literal|null
condition|)
block|{
name|loadedVersion
operator|=
name|getCurrentVersion
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|loadedVersion
operator|.
name|isCompatibleTo
argument_list|(
name|getCurrentVersion
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Storing configuration store version info "
operator|+
name|getCurrentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|storeVersion
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RMStateVersionIncompatibleException
argument_list|(
literal|"Expecting configuration store version "
operator|+
name|getCurrentVersion
argument_list|()
operator|+
literal|", but loading version "
operator|+
name|loadedVersion
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

