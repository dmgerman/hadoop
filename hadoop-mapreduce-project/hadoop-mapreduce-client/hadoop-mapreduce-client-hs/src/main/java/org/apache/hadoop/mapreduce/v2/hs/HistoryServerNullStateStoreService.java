begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|HistoryServerNullStateStoreService
specifier|public
class|class
name|HistoryServerNullStateStoreService
extends|extends
name|HistoryServerStateStoreService
block|{
annotation|@
name|Override
DECL|method|initStorage (Configuration conf)
specifier|protected
name|void
name|initStorage
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|startStorage ()
specifier|protected
name|void
name|startStorage
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|closeStorage ()
specifier|protected
name|void
name|closeStorage
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|loadState ()
specifier|public
name|HistoryServerState
name|loadState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot load state from null store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeToken (MRDelegationTokenIdentifier tokenId, Long renewDate)
specifier|public
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
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|updateToken (MRDelegationTokenIdentifier tokenId, Long renewDate)
specifier|public
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
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|removeToken (MRDelegationTokenIdentifier tokenId)
specifier|public
name|void
name|removeToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|storeTokenMasterKey (DelegationKey key)
specifier|public
name|void
name|storeTokenMasterKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|removeTokenMasterKey (DelegationKey key)
specifier|public
name|void
name|removeTokenMasterKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do nothing
block|}
block|}
end_class

end_unit

