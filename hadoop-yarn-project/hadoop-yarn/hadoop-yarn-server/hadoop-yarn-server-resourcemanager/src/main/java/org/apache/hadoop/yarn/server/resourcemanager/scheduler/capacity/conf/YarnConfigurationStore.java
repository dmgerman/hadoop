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

begin_interface
DECL|interface|YarnConfigurationStore
specifier|public
interface|interface
name|YarnConfigurationStore
block|{
comment|/**    * LogMutation encapsulates the fields needed for configuration mutation    * audit logging and recovery.    */
DECL|class|LogMutation
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
DECL|field|id
specifier|private
name|long
name|id
decl_stmt|;
comment|/**      * Create log mutation prior to logging.      * @param updates key-value configuration updates      * @param user user who requested configuration change      */
DECL|method|LogMutation (Map<String, String> updates, String user)
specifier|public
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
argument_list|(
name|updates
argument_list|,
name|user
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create log mutation for recovery.      * @param updates key-value configuration updates      * @param user user who requested configuration change      * @param id transaction id of configuration change      */
DECL|method|LogMutation (Map<String, String> updates, String user, long id)
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
parameter_list|,
name|long
name|id
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
name|this
operator|.
name|id
operator|=
name|id
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
comment|/**      * Get transaction id of this configuration change.      * @return transaction id      */
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Set transaction id of this configuration change.      * @param id transaction id      */
DECL|method|setId (long id)
specifier|public
name|void
name|setId
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
block|}
comment|/**    * Initialize the configuration store.    * @param conf configuration to initialize store with    * @param schedConf Initial key-value configuration to persist    * @throws IOException if initialization fails    */
DECL|method|initialize (Configuration conf, Configuration schedConf)
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Configuration
name|schedConf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Logs the configuration change to backing store. Generates an id associated    * with this mutation, sets it in {@code logMutation}, and returns it.    * @param logMutation configuration change to be persisted in write ahead log    * @return id which configuration store associates with this mutation    * @throws IOException if logging fails    */
DECL|method|logMutation (LogMutation logMutation)
name|long
name|logMutation
parameter_list|(
name|LogMutation
name|logMutation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Should be called after {@code logMutation}. Gets the pending mutation    * associated with {@code id} and marks the mutation as persisted (no longer    * pending). If isValid is true, merge the mutation with the persisted    * configuration.    *    * If {@code confirmMutation} is called with ids in a different order than    * was returned by {@code logMutation}, the result is implementation    * dependent.    * @param id id of mutation to be confirmed    * @param isValid if true, update persisted configuration with mutation    *                associated with {@code id}.    * @return true on success    * @throws IOException if mutation confirmation fails    */
DECL|method|confirmMutation (long id, boolean isValid)
name|boolean
name|confirmMutation
parameter_list|(
name|long
name|id
parameter_list|,
name|boolean
name|isValid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Retrieve the persisted configuration.    * @return configuration as key-value    */
DECL|method|retrieve ()
name|Configuration
name|retrieve
parameter_list|()
function_decl|;
comment|/**    * Get the list of pending mutations, in the order they were logged.    * @return list of mutations    */
DECL|method|getPendingMutations ()
name|List
argument_list|<
name|LogMutation
argument_list|>
name|getPendingMutations
parameter_list|()
function_decl|;
comment|/**    * Get a list of confirmed configuration mutations starting from a given id.    * @param fromId id from which to start getting mutations, inclusive    * @return list of configuration mutations    */
DECL|method|getConfirmedConfHistory (long fromId)
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
block|}
end_interface

end_unit

