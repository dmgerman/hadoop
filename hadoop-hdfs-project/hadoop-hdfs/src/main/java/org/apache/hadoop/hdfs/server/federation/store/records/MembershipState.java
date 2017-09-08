begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
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
name|records
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
name|resolver
operator|.
name|FederationNamenodeServiceState
operator|.
name|ACTIVE
import|;
end_import

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
name|resolver
operator|.
name|FederationNamenodeServiceState
operator|.
name|EXPIRED
import|;
end_import

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
name|resolver
operator|.
name|FederationNamenodeServiceState
operator|.
name|UNAVAILABLE
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|FederationNamenodeContext
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
name|store
operator|.
name|driver
operator|.
name|StateStoreSerializer
import|;
end_import

begin_comment
comment|/**  * Data schema for storing NN registration information in the  * {@link org.apache.hadoop.hdfs.server.federation.store.StateStoreService  * FederationStateStoreService}.  */
end_comment

begin_class
DECL|class|MembershipState
specifier|public
specifier|abstract
class|class
name|MembershipState
extends|extends
name|BaseRecord
implements|implements
name|FederationNamenodeContext
block|{
comment|/** Expiration time in ms for this entry. */
DECL|field|expirationMs
specifier|private
specifier|static
name|long
name|expirationMs
decl_stmt|;
comment|/** Comparator based on the name.*/
DECL|field|NAME_COMPARATOR
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|MembershipState
argument_list|>
name|NAME_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|MembershipState
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|MembershipState
name|m1
parameter_list|,
name|MembershipState
name|m2
parameter_list|)
block|{
return|return
name|m1
operator|.
name|compareNameTo
argument_list|(
name|m2
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Constructors.    */
DECL|method|MembershipState ()
specifier|public
name|MembershipState
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a new membership instance.    * @return Membership instance.    * @throws IOException    */
DECL|method|newInstance ()
specifier|public
specifier|static
name|MembershipState
name|newInstance
parameter_list|()
block|{
name|MembershipState
name|record
init|=
name|StateStoreSerializer
operator|.
name|newRecord
argument_list|(
name|MembershipState
operator|.
name|class
argument_list|)
decl_stmt|;
name|record
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|record
return|;
block|}
comment|/**    * Create a new membership instance.    *    * @param router Identifier of the router.    * @param nameservice Identifier of the nameservice.    * @param namenode Identifier of the namenode.    * @param clusterId Identifier of the cluster.    * @param blockPoolId Identifier of the blockpool.    * @param rpcAddress RPC address.    * @param serviceAddress Service RPC address.    * @param lifelineAddress Lifeline RPC address.    * @param webAddress HTTP address.    * @param state State of the federation.    * @param safemode If the safe mode is enabled.    * @return Membership instance.    * @throws IOException If we cannot create the instance.    */
DECL|method|newInstance (String router, String nameservice, String namenode, String clusterId, String blockPoolId, String rpcAddress, String serviceAddress, String lifelineAddress, String webAddress, FederationNamenodeServiceState state, boolean safemode)
specifier|public
specifier|static
name|MembershipState
name|newInstance
parameter_list|(
name|String
name|router
parameter_list|,
name|String
name|nameservice
parameter_list|,
name|String
name|namenode
parameter_list|,
name|String
name|clusterId
parameter_list|,
name|String
name|blockPoolId
parameter_list|,
name|String
name|rpcAddress
parameter_list|,
name|String
name|serviceAddress
parameter_list|,
name|String
name|lifelineAddress
parameter_list|,
name|String
name|webAddress
parameter_list|,
name|FederationNamenodeServiceState
name|state
parameter_list|,
name|boolean
name|safemode
parameter_list|)
block|{
name|MembershipState
name|record
init|=
name|MembershipState
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|record
operator|.
name|setRouterId
argument_list|(
name|router
argument_list|)
expr_stmt|;
name|record
operator|.
name|setNameserviceId
argument_list|(
name|nameservice
argument_list|)
expr_stmt|;
name|record
operator|.
name|setNamenodeId
argument_list|(
name|namenode
argument_list|)
expr_stmt|;
name|record
operator|.
name|setRpcAddress
argument_list|(
name|rpcAddress
argument_list|)
expr_stmt|;
name|record
operator|.
name|setServiceAddress
argument_list|(
name|serviceAddress
argument_list|)
expr_stmt|;
name|record
operator|.
name|setLifelineAddress
argument_list|(
name|lifelineAddress
argument_list|)
expr_stmt|;
name|record
operator|.
name|setWebAddress
argument_list|(
name|webAddress
argument_list|)
expr_stmt|;
name|record
operator|.
name|setIsSafeMode
argument_list|(
name|safemode
argument_list|)
expr_stmt|;
name|record
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|record
operator|.
name|setClusterId
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
name|record
operator|.
name|setBlockPoolId
argument_list|(
name|blockPoolId
argument_list|)
expr_stmt|;
name|record
operator|.
name|validate
argument_list|()
expr_stmt|;
return|return
name|record
return|;
block|}
DECL|method|setRouterId (String routerId)
specifier|public
specifier|abstract
name|void
name|setRouterId
parameter_list|(
name|String
name|routerId
parameter_list|)
function_decl|;
DECL|method|getRouterId ()
specifier|public
specifier|abstract
name|String
name|getRouterId
parameter_list|()
function_decl|;
DECL|method|setNameserviceId (String nameserviceId)
specifier|public
specifier|abstract
name|void
name|setNameserviceId
parameter_list|(
name|String
name|nameserviceId
parameter_list|)
function_decl|;
DECL|method|setNamenodeId (String namenodeId)
specifier|public
specifier|abstract
name|void
name|setNamenodeId
parameter_list|(
name|String
name|namenodeId
parameter_list|)
function_decl|;
DECL|method|setWebAddress (String webAddress)
specifier|public
specifier|abstract
name|void
name|setWebAddress
parameter_list|(
name|String
name|webAddress
parameter_list|)
function_decl|;
DECL|method|setRpcAddress (String rpcAddress)
specifier|public
specifier|abstract
name|void
name|setRpcAddress
parameter_list|(
name|String
name|rpcAddress
parameter_list|)
function_decl|;
DECL|method|setServiceAddress (String serviceAddress)
specifier|public
specifier|abstract
name|void
name|setServiceAddress
parameter_list|(
name|String
name|serviceAddress
parameter_list|)
function_decl|;
DECL|method|setLifelineAddress (String lifelineAddress)
specifier|public
specifier|abstract
name|void
name|setLifelineAddress
parameter_list|(
name|String
name|lifelineAddress
parameter_list|)
function_decl|;
DECL|method|setIsSafeMode (boolean isSafeMode)
specifier|public
specifier|abstract
name|void
name|setIsSafeMode
parameter_list|(
name|boolean
name|isSafeMode
parameter_list|)
function_decl|;
DECL|method|setClusterId (String clusterId)
specifier|public
specifier|abstract
name|void
name|setClusterId
parameter_list|(
name|String
name|clusterId
parameter_list|)
function_decl|;
DECL|method|setBlockPoolId (String blockPoolId)
specifier|public
specifier|abstract
name|void
name|setBlockPoolId
parameter_list|(
name|String
name|blockPoolId
parameter_list|)
function_decl|;
DECL|method|setState (FederationNamenodeServiceState state)
specifier|public
specifier|abstract
name|void
name|setState
parameter_list|(
name|FederationNamenodeServiceState
name|state
parameter_list|)
function_decl|;
DECL|method|getNameserviceId ()
specifier|public
specifier|abstract
name|String
name|getNameserviceId
parameter_list|()
function_decl|;
DECL|method|getNamenodeId ()
specifier|public
specifier|abstract
name|String
name|getNamenodeId
parameter_list|()
function_decl|;
DECL|method|getClusterId ()
specifier|public
specifier|abstract
name|String
name|getClusterId
parameter_list|()
function_decl|;
DECL|method|getBlockPoolId ()
specifier|public
specifier|abstract
name|String
name|getBlockPoolId
parameter_list|()
function_decl|;
DECL|method|getRpcAddress ()
specifier|public
specifier|abstract
name|String
name|getRpcAddress
parameter_list|()
function_decl|;
DECL|method|getServiceAddress ()
specifier|public
specifier|abstract
name|String
name|getServiceAddress
parameter_list|()
function_decl|;
DECL|method|getLifelineAddress ()
specifier|public
specifier|abstract
name|String
name|getLifelineAddress
parameter_list|()
function_decl|;
DECL|method|getWebAddress ()
specifier|public
specifier|abstract
name|String
name|getWebAddress
parameter_list|()
function_decl|;
DECL|method|getIsSafeMode ()
specifier|public
specifier|abstract
name|boolean
name|getIsSafeMode
parameter_list|()
function_decl|;
DECL|method|getState ()
specifier|public
specifier|abstract
name|FederationNamenodeServiceState
name|getState
parameter_list|()
function_decl|;
DECL|method|setStats (MembershipStats stats)
specifier|public
specifier|abstract
name|void
name|setStats
parameter_list|(
name|MembershipStats
name|stats
parameter_list|)
function_decl|;
DECL|method|getStats ()
specifier|public
specifier|abstract
name|MembershipStats
name|getStats
parameter_list|()
function_decl|;
DECL|method|setLastContact (long contact)
specifier|public
specifier|abstract
name|void
name|setLastContact
parameter_list|(
name|long
name|contact
parameter_list|)
function_decl|;
DECL|method|getLastContact ()
specifier|public
specifier|abstract
name|long
name|getLastContact
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|like (BaseRecord o)
specifier|public
name|boolean
name|like
parameter_list|(
name|BaseRecord
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|MembershipState
condition|)
block|{
name|MembershipState
name|other
init|=
operator|(
name|MembershipState
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|getRouterId
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getRouterId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getRouterId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getNameserviceId
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getNameserviceId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getNamenodeId
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getNamenodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getRpcAddress
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getRpcAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getRpcAddress
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClusterId
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getClusterId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getClusterId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getBlockPoolId
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getBlockPoolId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getState
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getRouterId
argument_list|()
operator|+
literal|"->"
operator|+
name|getNameserviceId
argument_list|()
operator|+
literal|":"
operator|+
name|getNamenodeId
argument_list|()
operator|+
literal|":"
operator|+
name|getRpcAddress
argument_list|()
operator|+
literal|"-"
operator|+
name|getState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPrimaryKeys ()
specifier|public
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPrimaryKeys
parameter_list|()
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"routerId"
argument_list|,
name|getRouterId
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"nameserviceId"
argument_list|,
name|getNameserviceId
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"namenodeId"
argument_list|,
name|getNamenodeId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
comment|/**    * Check if the namenode is available.    *    * @return If the namenode is available.    */
DECL|method|isAvailable ()
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|getState
argument_list|()
operator|==
name|ACTIVE
return|;
block|}
comment|/**    * Validates the entry. Throws an IllegalArgementException if the data record    * is missing required information.    */
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
name|boolean
name|ret
init|=
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|getNameserviceId
argument_list|()
operator|==
literal|null
operator|||
name|getNameserviceId
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//LOG.error("Invalid registration, no nameservice specified " + this);
name|ret
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|getWebAddress
argument_list|()
operator|==
literal|null
operator|||
name|getWebAddress
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//LOG.error("Invalid registration, no web address specified " + this);
name|ret
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|getRpcAddress
argument_list|()
operator|==
literal|null
operator|||
name|getRpcAddress
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//LOG.error("Invalid registration, no rpc address specified " + this);
name|ret
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isBadState
argument_list|()
operator|&&
operator|(
name|getBlockPoolId
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|||
name|getBlockPoolId
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
comment|//LOG.error("Invalid registration, no block pool specified " + this);
name|ret
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Overrides the cached getBlockPoolId() with an update. The state will be    * reset when the cache is flushed    *    * @param newState Service state of the namenode.    */
DECL|method|overrideState (FederationNamenodeServiceState newState)
specifier|public
name|void
name|overrideState
parameter_list|(
name|FederationNamenodeServiceState
name|newState
parameter_list|)
block|{
name|this
operator|.
name|setState
argument_list|(
name|newState
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sort by nameservice, namenode, and router.    *    * @param other Another membership to compare to.    * @return If this object goes before the parameter.    */
DECL|method|compareNameTo (MembershipState other)
specifier|public
name|int
name|compareNameTo
parameter_list|(
name|MembershipState
name|other
parameter_list|)
block|{
name|int
name|ret
init|=
name|this
operator|.
name|getNameserviceId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|ret
operator|=
name|this
operator|.
name|getNamenodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|ret
operator|=
name|this
operator|.
name|getRouterId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getRouterId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Get the identifier of this namenode registration.    * @return Identifier of the namenode.    */
DECL|method|getNamenodeKey ()
specifier|public
name|String
name|getNamenodeKey
parameter_list|()
block|{
return|return
name|getNamenodeKey
argument_list|(
name|this
operator|.
name|getNameserviceId
argument_list|()
argument_list|,
name|this
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Generate the identifier for a Namenode in the HDFS federation.    *    * @param nsId Nameservice of the Namenode.    * @param nnId Namenode within the Nameservice (HA).    * @return Namenode identifier within the federation.    */
DECL|method|getNamenodeKey (String nsId, String nnId)
specifier|public
specifier|static
name|String
name|getNamenodeKey
parameter_list|(
name|String
name|nsId
parameter_list|,
name|String
name|nnId
parameter_list|)
block|{
return|return
name|nsId
operator|+
literal|"-"
operator|+
name|nnId
return|;
block|}
comment|/**    * Check if the membership is in a bad state (expired or unavailable).    * @return If the membership is in a bad state (expired or unavailable).    */
DECL|method|isBadState ()
specifier|private
name|boolean
name|isBadState
parameter_list|()
block|{
return|return
name|this
operator|.
name|getState
argument_list|()
operator|==
name|EXPIRED
operator|||
name|this
operator|.
name|getState
argument_list|()
operator|==
name|UNAVAILABLE
return|;
block|}
annotation|@
name|Override
DECL|method|checkExpired (long currentTime)
specifier|public
name|boolean
name|checkExpired
parameter_list|(
name|long
name|currentTime
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|checkExpired
argument_list|(
name|currentTime
argument_list|)
condition|)
block|{
name|this
operator|.
name|setState
argument_list|(
name|EXPIRED
argument_list|)
expr_stmt|;
comment|// Commit it
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getExpirationMs ()
specifier|public
name|long
name|getExpirationMs
parameter_list|()
block|{
return|return
name|MembershipState
operator|.
name|expirationMs
return|;
block|}
comment|/**    * Set the expiration time for this class.    *    * @param time Expiration time in milliseconds.    */
DECL|method|setExpirationMs (long time)
specifier|public
specifier|static
name|void
name|setExpirationMs
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|MembershipState
operator|.
name|expirationMs
operator|=
name|time
expr_stmt|;
block|}
block|}
end_class

end_unit

