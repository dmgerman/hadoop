begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.impl
package|package
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
name|federation
operator|.
name|store
operator|.
name|impl
package|;
end_package

begin_import
import|import static
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
name|federation
operator|.
name|store
operator|.
name|StateStoreUtils
operator|.
name|filterMultiple
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
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
name|federation
operator|.
name|resolver
operator|.
name|FederationNamespaceInfo
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
name|federation
operator|.
name|store
operator|.
name|MembershipStore
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
name|federation
operator|.
name|store
operator|.
name|StateStoreCache
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
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|StateStoreDriver
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetNamenodeRegistrationsRequest
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetNamenodeRegistrationsResponse
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetNamespaceInfoRequest
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetNamespaceInfoResponse
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|NamenodeHeartbeatRequest
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|NamenodeHeartbeatResponse
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|UpdateNamenodeRegistrationRequest
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|UpdateNamenodeRegistrationResponse
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MembershipState
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|Query
import|;
end_import

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

begin_comment
comment|/**  * Implementation of the {@link MembershipStore} State Store API.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MembershipStoreImpl
specifier|public
class|class
name|MembershipStoreImpl
extends|extends
name|MembershipStore
implements|implements
name|StateStoreCache
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MembershipStoreImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Reported namespaces that are not decommissioned. */
DECL|field|activeNamespaces
specifier|private
specifier|final
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|activeNamespaces
decl_stmt|;
comment|/** Namenodes (after evaluating the quorum) that are active in the cluster. */
DECL|field|activeRegistrations
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MembershipState
argument_list|>
name|activeRegistrations
decl_stmt|;
comment|/** Namenode status reports (raw) that were discarded for being too old. */
DECL|field|expiredRegistrations
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MembershipState
argument_list|>
name|expiredRegistrations
decl_stmt|;
comment|/** Lock to access the local memory cache. */
DECL|field|cacheReadWriteLock
specifier|private
specifier|final
name|ReadWriteLock
name|cacheReadWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|cacheReadLock
specifier|private
specifier|final
name|Lock
name|cacheReadLock
init|=
name|cacheReadWriteLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
DECL|field|cacheWriteLock
specifier|private
specifier|final
name|Lock
name|cacheWriteLock
init|=
name|cacheReadWriteLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
DECL|method|MembershipStoreImpl (StateStoreDriver driver)
specifier|public
name|MembershipStoreImpl
parameter_list|(
name|StateStoreDriver
name|driver
parameter_list|)
block|{
name|super
argument_list|(
name|driver
argument_list|)
expr_stmt|;
name|this
operator|.
name|activeRegistrations
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|expiredRegistrations
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|activeNamespaces
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExpiredNamenodeRegistrations ( GetNamenodeRegistrationsRequest request)
specifier|public
name|GetNamenodeRegistrationsResponse
name|getExpiredNamenodeRegistrations
parameter_list|(
name|GetNamenodeRegistrationsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetNamenodeRegistrationsResponse
name|response
init|=
name|GetNamenodeRegistrationsResponse
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|cacheReadLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Collection
argument_list|<
name|MembershipState
argument_list|>
name|vals
init|=
name|this
operator|.
name|expiredRegistrations
operator|.
name|values
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MembershipState
argument_list|>
name|copyVals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|vals
argument_list|)
decl_stmt|;
name|response
operator|.
name|setNamenodeMemberships
argument_list|(
name|copyVals
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cacheReadLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getNamespaceInfo ( GetNamespaceInfoRequest request)
specifier|public
name|GetNamespaceInfoResponse
name|getNamespaceInfo
parameter_list|(
name|GetNamespaceInfoRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|namespaces
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|cacheReadLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|namespaces
operator|.
name|addAll
argument_list|(
name|activeNamespaces
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cacheReadLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|GetNamespaceInfoResponse
name|response
init|=
name|GetNamespaceInfoResponse
operator|.
name|newInstance
argument_list|(
name|namespaces
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getNamenodeRegistrations ( final GetNamenodeRegistrationsRequest request)
specifier|public
name|GetNamenodeRegistrationsResponse
name|getNamenodeRegistrations
parameter_list|(
specifier|final
name|GetNamenodeRegistrationsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO Cache some common queries and sorts
name|List
argument_list|<
name|MembershipState
argument_list|>
name|ret
init|=
literal|null
decl_stmt|;
name|cacheReadLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Collection
argument_list|<
name|MembershipState
argument_list|>
name|registrations
init|=
name|activeRegistrations
operator|.
name|values
argument_list|()
decl_stmt|;
name|MembershipState
name|partialMembership
init|=
name|request
operator|.
name|getPartialMembership
argument_list|()
decl_stmt|;
if|if
condition|(
name|partialMembership
operator|==
literal|null
condition|)
block|{
name|ret
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|registrations
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Query
argument_list|<
name|MembershipState
argument_list|>
name|query
init|=
operator|new
name|Query
argument_list|<>
argument_list|(
name|partialMembership
argument_list|)
decl_stmt|;
name|ret
operator|=
name|filterMultiple
argument_list|(
name|query
argument_list|,
name|registrations
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cacheReadLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|// Sort in ascending update date order
name|Collections
operator|.
name|sort
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|GetNamenodeRegistrationsResponse
name|response
init|=
name|GetNamenodeRegistrationsResponse
operator|.
name|newInstance
argument_list|(
name|ret
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|namenodeHeartbeat ( NamenodeHeartbeatRequest request)
specifier|public
name|NamenodeHeartbeatResponse
name|namenodeHeartbeat
parameter_list|(
name|NamenodeHeartbeatRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|MembershipState
name|record
init|=
name|request
operator|.
name|getNamenodeMembership
argument_list|()
decl_stmt|;
name|String
name|nnId
init|=
name|record
operator|.
name|getNamenodeKey
argument_list|()
decl_stmt|;
name|MembershipState
name|existingEntry
init|=
literal|null
decl_stmt|;
name|cacheReadLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|existingEntry
operator|=
name|this
operator|.
name|activeRegistrations
operator|.
name|get
argument_list|(
name|nnId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cacheReadLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|existingEntry
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|existingEntry
operator|.
name|getState
argument_list|()
operator|!=
name|record
operator|.
name|getState
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NN registration state has changed: {} -> {}"
argument_list|,
name|existingEntry
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updating NN registration: {} -> {}"
argument_list|,
name|existingEntry
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Inserting new NN registration: {}"
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
name|boolean
name|status
init|=
name|getDriver
argument_list|()
operator|.
name|put
argument_list|(
name|record
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NamenodeHeartbeatResponse
name|response
init|=
name|NamenodeHeartbeatResponse
operator|.
name|newInstance
argument_list|(
name|status
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|loadCache (boolean force)
specifier|public
name|boolean
name|loadCache
parameter_list|(
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|loadCache
argument_list|(
name|force
argument_list|)
expr_stmt|;
comment|// Update local cache atomically
name|cacheWriteLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|activeRegistrations
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|expiredRegistrations
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|activeNamespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Build list of NN registrations: nnId -> registration list
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|MembershipState
argument_list|>
argument_list|>
name|nnRegistrations
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MembershipState
argument_list|>
name|cachedRecords
init|=
name|getCachedRecords
argument_list|()
decl_stmt|;
for|for
control|(
name|MembershipState
name|membership
range|:
name|cachedRecords
control|)
block|{
name|String
name|nnId
init|=
name|membership
operator|.
name|getNamenodeKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|membership
operator|.
name|getState
argument_list|()
operator|==
name|FederationNamenodeServiceState
operator|.
name|EXPIRED
condition|)
block|{
comment|// Expired, RPC service does not use these
name|String
name|key
init|=
name|membership
operator|.
name|getPrimaryKey
argument_list|()
decl_stmt|;
name|this
operator|.
name|expiredRegistrations
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|membership
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// This is a valid NN registration, build a list of all registrations
comment|// using the NN id to use for the quorum calculation.
name|List
argument_list|<
name|MembershipState
argument_list|>
name|nnRegistration
init|=
name|nnRegistrations
operator|.
name|get
argument_list|(
name|nnId
argument_list|)
decl_stmt|;
if|if
condition|(
name|nnRegistration
operator|==
literal|null
condition|)
block|{
name|nnRegistration
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|nnRegistrations
operator|.
name|put
argument_list|(
name|nnId
argument_list|,
name|nnRegistration
argument_list|)
expr_stmt|;
block|}
name|nnRegistration
operator|.
name|add
argument_list|(
name|membership
argument_list|)
expr_stmt|;
name|String
name|bpId
init|=
name|membership
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|String
name|cId
init|=
name|membership
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
name|String
name|nsId
init|=
name|membership
operator|.
name|getNameserviceId
argument_list|()
decl_stmt|;
name|FederationNamespaceInfo
name|nsInfo
init|=
operator|new
name|FederationNamespaceInfo
argument_list|(
name|bpId
argument_list|,
name|cId
argument_list|,
name|nsId
argument_list|)
decl_stmt|;
name|this
operator|.
name|activeNamespaces
operator|.
name|add
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Calculate most representative entry for each active NN id
for|for
control|(
name|List
argument_list|<
name|MembershipState
argument_list|>
name|nnRegistration
range|:
name|nnRegistrations
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Run quorum based on NN state
name|MembershipState
name|representativeRecord
init|=
name|getRepresentativeQuorum
argument_list|(
name|nnRegistration
argument_list|)
decl_stmt|;
name|String
name|nnKey
init|=
name|representativeRecord
operator|.
name|getNamenodeKey
argument_list|()
decl_stmt|;
name|this
operator|.
name|activeRegistrations
operator|.
name|put
argument_list|(
name|nnKey
argument_list|,
name|representativeRecord
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Refreshed {} NN registrations from State Store"
argument_list|,
name|cachedRecords
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cacheWriteLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|updateNamenodeRegistration ( UpdateNamenodeRegistrationRequest request)
specifier|public
name|UpdateNamenodeRegistrationResponse
name|updateNamenodeRegistration
parameter_list|(
name|UpdateNamenodeRegistrationRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|status
init|=
literal|false
decl_stmt|;
name|cacheWriteLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|String
name|namenode
init|=
name|MembershipState
operator|.
name|getNamenodeKey
argument_list|(
name|request
operator|.
name|getNameserviceId
argument_list|()
argument_list|,
name|request
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
decl_stmt|;
name|MembershipState
name|member
init|=
name|this
operator|.
name|activeRegistrations
operator|.
name|get
argument_list|(
name|namenode
argument_list|)
decl_stmt|;
if|if
condition|(
name|member
operator|!=
literal|null
condition|)
block|{
name|member
operator|.
name|setState
argument_list|(
name|request
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
literal|true
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cacheWriteLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|UpdateNamenodeRegistrationResponse
name|response
init|=
name|UpdateNamenodeRegistrationResponse
operator|.
name|newInstance
argument_list|(
name|status
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Picks the most recent entry in the subset that is most agreeable on the    * specified field. 1) If a majority of the collection has the same value for    * the field, the first sorted entry within the subset the matches the    * majority value 2) Otherwise the first sorted entry in the set of all    * entries    *    * @param records - Collection of state store record objects of the same type    * @return record that is most representative of the field name    */
DECL|method|getRepresentativeQuorum ( Collection<MembershipState> records)
specifier|private
name|MembershipState
name|getRepresentativeQuorum
parameter_list|(
name|Collection
argument_list|<
name|MembershipState
argument_list|>
name|records
parameter_list|)
block|{
comment|// Collate objects by field value: field value -> order set of records
name|Map
argument_list|<
name|FederationNamenodeServiceState
argument_list|,
name|TreeSet
argument_list|<
name|MembershipState
argument_list|>
argument_list|>
name|occurenceMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MembershipState
name|record
range|:
name|records
control|)
block|{
name|FederationNamenodeServiceState
name|state
init|=
name|record
operator|.
name|getState
argument_list|()
decl_stmt|;
name|TreeSet
argument_list|<
name|MembershipState
argument_list|>
name|matchingSet
init|=
name|occurenceMap
operator|.
name|get
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchingSet
operator|==
literal|null
condition|)
block|{
comment|// TreeSet orders elements by descending date via comparators
name|matchingSet
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|occurenceMap
operator|.
name|put
argument_list|(
name|state
argument_list|,
name|matchingSet
argument_list|)
expr_stmt|;
block|}
name|matchingSet
operator|.
name|add
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
comment|// Select largest group
name|TreeSet
argument_list|<
name|MembershipState
argument_list|>
name|largestSet
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TreeSet
argument_list|<
name|MembershipState
argument_list|>
name|matchingSet
range|:
name|occurenceMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|largestSet
operator|.
name|size
argument_list|()
operator|<
name|matchingSet
operator|.
name|size
argument_list|()
condition|)
block|{
name|largestSet
operator|=
name|matchingSet
expr_stmt|;
block|}
block|}
comment|// If quorum, use the newest element here
if|if
condition|(
name|largestSet
operator|.
name|size
argument_list|()
operator|>
name|records
operator|.
name|size
argument_list|()
operator|/
literal|2
condition|)
block|{
return|return
name|largestSet
operator|.
name|first
argument_list|()
return|;
comment|// Otherwise, return most recent by class comparator
block|}
elseif|else
if|if
condition|(
name|records
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|TreeSet
argument_list|<
name|MembershipState
argument_list|>
name|sortedList
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|records
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Quorum failed, using most recent: {}"
argument_list|,
name|sortedList
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sortedList
operator|.
name|first
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

