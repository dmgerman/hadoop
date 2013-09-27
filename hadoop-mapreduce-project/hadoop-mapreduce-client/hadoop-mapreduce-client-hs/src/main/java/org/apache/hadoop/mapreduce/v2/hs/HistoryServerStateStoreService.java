begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
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
operator|.
name|Private
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
operator|.
name|Unstable
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|MRDelegationTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationKey
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
comment|/**  * Base class for history server state storage.  * Storage implementations need to implement blocking store and load methods  * to actually store and load the state.  */
DECL|class|HistoryServerStateStoreService
specifier|public
specifier|abstract
class|class
name|HistoryServerStateStoreService
extends|extends
name|AbstractService
block|{
DECL|class|HistoryServerState
specifier|public
specifier|static
class|class
name|HistoryServerState
block|{
DECL|field|tokenState
name|Map
argument_list|<
name|MRDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|tokenState
init|=
operator|new
name|HashMap
argument_list|<
name|MRDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|tokenMasterKeyState
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|tokenMasterKeyState
init|=
operator|new
name|HashSet
argument_list|<
name|DelegationKey
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|getTokenState ()
specifier|public
name|Map
argument_list|<
name|MRDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|getTokenState
parameter_list|()
block|{
return|return
name|tokenState
return|;
block|}
DECL|method|getTokenMasterKeyState ()
specifier|public
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|getTokenMasterKeyState
parameter_list|()
block|{
return|return
name|tokenMasterKeyState
return|;
block|}
block|}
DECL|method|HistoryServerStateStoreService ()
specifier|public
name|HistoryServerStateStoreService
parameter_list|()
block|{
name|super
argument_list|(
name|HistoryServerStateStoreService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize the state storage    *    * @param conf the configuration    * @throws IOException    */
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|initStorage
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start the state storage for use    *    * @throws IOException    */
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|IOException
block|{
name|startStorage
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown the state storage.    *     * @throws IOException    */
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|IOException
block|{
name|closeStorage
argument_list|()
expr_stmt|;
block|}
comment|/**    * Implementation-specific initialization.    *     * @param conf the configuration    * @throws IOException    */
DECL|method|initStorage (Configuration conf)
specifier|protected
specifier|abstract
name|void
name|initStorage
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Implementation-specific startup.    *     * @throws IOException    */
DECL|method|startStorage ()
specifier|protected
specifier|abstract
name|void
name|startStorage
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Implementation-specific shutdown.    *     * @throws IOException    */
DECL|method|closeStorage ()
specifier|protected
specifier|abstract
name|void
name|closeStorage
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Load the history server state from the state storage.    *     * @throws IOException    */
DECL|method|loadState ()
specifier|public
specifier|abstract
name|HistoryServerState
name|loadState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Blocking method to store a delegation token along with the current token    * sequence number to the state storage.    *     * Implementations must not return from this method until the token has been    * committed to the state store.    *     * @param tokenId the token to store    * @param renewDate the token renewal deadline    * @throws IOException    */
DECL|method|storeToken (MRDelegationTokenIdentifier tokenId, Long renewDate)
specifier|public
specifier|abstract
name|void
name|storeToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|,
name|Long
name|renewDate
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Blocking method to update the expiration of a delegation token    * in the state storage.    *     * Implementations must not return from this method until the expiration    * date of the token has been updated in the state store.    *     * @param tokenId the token to update    * @param renewDate the new token renewal deadline    * @throws IOException    */
DECL|method|updateToken (MRDelegationTokenIdentifier tokenId, Long renewDate)
specifier|public
specifier|abstract
name|void
name|updateToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|,
name|Long
name|renewDate
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Blocking method to remove a delegation token from the state storage.    *     * Implementations must not return from this method until the token has been    * removed from the state store.    *     * @param tokenId the token to remove    * @throws IOException    */
DECL|method|removeToken (MRDelegationTokenIdentifier tokenId)
specifier|public
specifier|abstract
name|void
name|removeToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Blocking method to store a delegation token master key.    *     * Implementations must not return from this method until the key has been    * committed to the state store.    *     * @param key the master key to store    * @throws IOException    */
DECL|method|storeTokenMasterKey ( DelegationKey key)
specifier|public
specifier|abstract
name|void
name|storeTokenMasterKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Blocking method to remove a delegation token master key.    *     * Implementations must not return from this method until the key has been    * removed from the state store.    *     * @param key the master key to remove    * @throws IOException    */
DECL|method|removeTokenMasterKey (DelegationKey key)
specifier|public
specifier|abstract
name|void
name|removeTokenMasterKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

