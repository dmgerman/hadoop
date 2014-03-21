begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|HadoopIllegalArgumentException
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
name|net
operator|.
name|NetUtils
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
name|exceptions
operator|.
name|YarnRuntimeException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HAUtil
specifier|public
class|class
name|HAUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HAUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BAD_CONFIG_MESSAGE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|BAD_CONFIG_MESSAGE_PREFIX
init|=
literal|"Invalid configuration! "
decl_stmt|;
DECL|method|HAUtil ()
specifier|private
name|HAUtil
parameter_list|()
block|{
comment|/* Hidden constructor */
block|}
DECL|method|throwBadConfigurationException (String msg)
specifier|private
specifier|static
name|void
name|throwBadConfigurationException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|BAD_CONFIG_MESSAGE_PREFIX
operator|+
name|msg
argument_list|)
throw|;
block|}
comment|/**    * Returns true if Resource Manager HA is configured.    *    * @param conf Configuration    * @return true if HA is configured in the configuration; else false.    */
DECL|method|isHAEnabled (Configuration conf)
specifier|public
specifier|static
name|boolean
name|isHAEnabled
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_HA_ENABLED
argument_list|)
return|;
block|}
DECL|method|isAutomaticFailoverEnabled (Configuration conf)
specifier|public
specifier|static
name|boolean
name|isAutomaticFailoverEnabled
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_AUTO_FAILOVER_ENABLED
argument_list|)
return|;
block|}
DECL|method|isAutomaticFailoverEnabledAndEmbedded ( Configuration conf)
specifier|public
specifier|static
name|boolean
name|isAutomaticFailoverEnabledAndEmbedded
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|isAutomaticFailoverEnabled
argument_list|(
name|conf
argument_list|)
operator|&&
name|isAutomaticFailoverEmbedded
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|isAutomaticFailoverEmbedded (Configuration conf)
specifier|public
specifier|static
name|boolean
name|isAutomaticFailoverEmbedded
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_EMBEDDED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_AUTO_FAILOVER_EMBEDDED
argument_list|)
return|;
block|}
comment|/**    * Verify configuration for Resource Manager HA.    * @param conf Configuration    * @throws YarnRuntimeException    */
DECL|method|verifyAndSetConfiguration (Configuration conf)
specifier|public
specifier|static
name|void
name|verifyAndSetConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnRuntimeException
block|{
name|verifyAndSetRMHAIdsList
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|verifyAndSetCurrentRMHAId
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|verifyAndSetAllServiceAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify configuration that there are at least two RM-ids    * and RPC addresses are specified for each RM-id.    * Then set the RM-ids.    */
DECL|method|verifyAndSetRMHAIdsList (Configuration conf)
specifier|private
specifier|static
name|void
name|verifyAndSetRMHAIdsList
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
name|throwBadConfigurationException
argument_list|(
name|getInvalidValueMessage
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|)
operator|+
literal|"\nHA mode requires atleast two RMs"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|setValue
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
comment|// verify the RM service addresses configurations for every RMIds
for|for
control|(
name|String
name|prefix
range|:
name|YarnConfiguration
operator|.
name|getServiceAddressConfKeys
argument_list|(
name|conf
argument_list|)
control|)
block|{
name|checkAndSetRMRPCAddress
argument_list|(
name|prefix
argument_list|,
name|id
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|setValue
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|setValue
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
name|setValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|setValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyAndSetCurrentRMHAId (Configuration conf)
specifier|private
specifier|static
name|void
name|verifyAndSetCurrentRMHAId
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|rmId
init|=
name|getRMHAId
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmId
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"Can not find valid RM_HA_ID. None of "
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|)
control|)
block|{
name|msg
operator|.
name|append
argument_list|(
name|addSuffix
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|id
argument_list|)
operator|+
literal|" "
argument_list|)
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
literal|" are matching"
operator|+
literal|" the local address OR "
operator|+
name|YarnConfiguration
operator|.
name|RM_HA_ID
operator|+
literal|" is not"
operator|+
literal|" specified in HA Configuration"
argument_list|)
expr_stmt|;
name|throwBadConfigurationException
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|getRMHAIds
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ids
operator|.
name|contains
argument_list|(
name|rmId
argument_list|)
condition|)
block|{
name|throwBadConfigurationException
argument_list|(
name|getRMHAIdNeedToBeIncludedMessage
argument_list|(
name|ids
operator|.
name|toString
argument_list|()
argument_list|,
name|rmId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|rmId
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyAndSetConfValue (String prefix, Configuration conf)
specifier|private
specifier|static
name|void
name|verifyAndSetConfValue
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|confKey
init|=
literal|null
decl_stmt|;
name|String
name|confValue
init|=
literal|null
decl_stmt|;
try|try
block|{
name|confKey
operator|=
name|getConfKeyForRMInstance
argument_list|(
name|prefix
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|confValue
operator|=
name|getConfValueForRMInstance
argument_list|(
name|prefix
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|prefix
argument_list|,
name|confValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|yre
parameter_list|)
block|{
comment|// Error at getRMHAId()
throw|throw
name|yre
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|String
name|errmsg
decl_stmt|;
if|if
condition|(
name|confKey
operator|==
literal|null
condition|)
block|{
comment|// Error at addSuffix
name|errmsg
operator|=
name|getInvalidValueMessage
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|getRMHAId
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Error at Configuration#set.
name|errmsg
operator|=
name|getNeedToSetValueMessage
argument_list|(
name|confKey
argument_list|)
expr_stmt|;
block|}
name|throwBadConfigurationException
argument_list|(
name|errmsg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyAndSetAllServiceAddresses (Configuration conf)
specifier|public
specifier|static
name|void
name|verifyAndSetAllServiceAddresses
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
for|for
control|(
name|String
name|confKey
range|:
name|YarnConfiguration
operator|.
name|getServiceAddressConfKeys
argument_list|(
name|conf
argument_list|)
control|)
block|{
name|verifyAndSetConfValue
argument_list|(
name|confKey
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @param conf Configuration. Please use getRMHAIds to check.    * @return RM Ids on success    */
DECL|method|getRMHAIds (Configuration conf)
specifier|public
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|getRMHAIds
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getStringCollection
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|)
return|;
block|}
comment|/**    * @param conf Configuration. Please use verifyAndSetRMHAId to check.    * @return RM Id on success    */
DECL|method|getRMHAId (Configuration conf)
specifier|public
specifier|static
name|String
name|getRMHAId
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|found
init|=
literal|0
decl_stmt|;
name|String
name|currentRMId
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentRMId
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|String
name|rmId
range|:
name|getRMHAIds
argument_list|(
name|conf
argument_list|)
control|)
block|{
name|String
name|key
init|=
name|addSuffix
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|rmId
argument_list|)
decl_stmt|;
name|String
name|addr
init|=
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|InetSocketAddress
name|s
decl_stmt|;
try|try
block|{
name|s
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception in creating socket address "
operator|+
name|addr
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|s
operator|.
name|isUnresolved
argument_list|()
operator|&&
name|NetUtils
operator|.
name|isLocalAddress
argument_list|(
name|s
operator|.
name|getAddress
argument_list|()
argument_list|)
condition|)
block|{
name|currentRMId
operator|=
name|rmId
operator|.
name|trim
argument_list|()
expr_stmt|;
name|found
operator|++
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|found
operator|>
literal|1
condition|)
block|{
comment|// Only one address must match the local address
name|String
name|msg
init|=
literal|"The HA Configuration has multiple addresses that match "
operator|+
literal|"local node's address."
decl_stmt|;
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
return|return
name|currentRMId
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNeedToSetValueMessage (String confKey)
specifier|static
name|String
name|getNeedToSetValueMessage
parameter_list|(
name|String
name|confKey
parameter_list|)
block|{
return|return
name|confKey
operator|+
literal|" needs to be set in a HA configuration."
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getInvalidValueMessage (String confKey, String invalidValue)
specifier|static
name|String
name|getInvalidValueMessage
parameter_list|(
name|String
name|confKey
parameter_list|,
name|String
name|invalidValue
parameter_list|)
block|{
return|return
literal|"Invalid value of "
operator|+
name|confKey
operator|+
literal|". "
operator|+
literal|"Current value is "
operator|+
name|invalidValue
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRMHAIdNeedToBeIncludedMessage (String ids, String rmId)
specifier|static
name|String
name|getRMHAIdNeedToBeIncludedMessage
parameter_list|(
name|String
name|ids
parameter_list|,
name|String
name|rmId
parameter_list|)
block|{
return|return
name|YarnConfiguration
operator|.
name|RM_HA_IDS
operator|+
literal|"("
operator|+
name|ids
operator|+
literal|") need to contain "
operator|+
name|YarnConfiguration
operator|.
name|RM_HA_ID
operator|+
literal|"("
operator|+
name|rmId
operator|+
literal|") in a HA configuration."
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRMHAIdsWarningMessage (String ids)
specifier|static
name|String
name|getRMHAIdsWarningMessage
parameter_list|(
name|String
name|ids
parameter_list|)
block|{
return|return
literal|"Resource Manager HA is enabled, but "
operator|+
name|YarnConfiguration
operator|.
name|RM_HA_IDS
operator|+
literal|" has only one id("
operator|+
name|ids
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getConfKeyForRMInstance (String prefix, Configuration conf)
specifier|static
name|String
name|getConfKeyForRMInstance
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
operator|!
name|YarnConfiguration
operator|.
name|getServiceAddressConfKeys
argument_list|(
name|conf
argument_list|)
operator|.
name|contains
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
name|prefix
return|;
block|}
else|else
block|{
name|String
name|RMId
init|=
name|getRMHAId
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|checkAndSetRMRPCAddress
argument_list|(
name|prefix
argument_list|,
name|RMId
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|addSuffix
argument_list|(
name|prefix
argument_list|,
name|RMId
argument_list|)
return|;
block|}
block|}
DECL|method|getConfValueForRMInstance (String prefix, Configuration conf)
specifier|public
specifier|static
name|String
name|getConfValueForRMInstance
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|confKey
init|=
name|getConfKeyForRMInstance
argument_list|(
name|prefix
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|String
name|retVal
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|confKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"getConfValueForRMInstance: prefix = "
operator|+
name|prefix
operator|+
literal|"; confKey being looked up = "
operator|+
name|confKey
operator|+
literal|"; value being set to = "
operator|+
name|retVal
argument_list|)
expr_stmt|;
block|}
return|return
name|retVal
return|;
block|}
DECL|method|getConfValueForRMInstance ( String prefix, String defaultValue, Configuration conf)
specifier|public
specifier|static
name|String
name|getConfValueForRMInstance
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|defaultValue
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|value
init|=
name|getConfValueForRMInstance
argument_list|(
name|prefix
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
operator|(
name|value
operator|==
literal|null
operator|)
condition|?
name|defaultValue
else|:
name|value
return|;
block|}
comment|/** Add non empty and non null suffix to a key */
DECL|method|addSuffix (String key, String suffix)
specifier|public
specifier|static
name|String
name|addSuffix
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|suffix
operator|==
literal|null
operator|||
name|suffix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|key
return|;
block|}
if|if
condition|(
name|suffix
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"suffix '"
operator|+
name|suffix
operator|+
literal|"' should not "
operator|+
literal|"already have '.' prepended."
argument_list|)
throw|;
block|}
return|return
name|key
operator|+
literal|"."
operator|+
name|suffix
return|;
block|}
DECL|method|checkAndSetRMRPCAddress (String prefix, String RMId, Configuration conf)
specifier|private
specifier|static
name|void
name|checkAndSetRMRPCAddress
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|RMId
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|rpcAddressConfKey
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rpcAddressConfKey
operator|=
name|addSuffix
argument_list|(
name|prefix
argument_list|,
name|RMId
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|getTrimmed
argument_list|(
name|rpcAddressConfKey
argument_list|)
operator|==
literal|null
condition|)
block|{
name|String
name|hostNameConfKey
init|=
name|addSuffix
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HOSTNAME
argument_list|,
name|RMId
argument_list|)
decl_stmt|;
name|String
name|confVal
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|hostNameConfKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|confVal
operator|==
literal|null
condition|)
block|{
name|throwBadConfigurationException
argument_list|(
name|getNeedToSetValueMessage
argument_list|(
name|hostNameConfKey
operator|+
literal|" or "
operator|+
name|addSuffix
argument_list|(
name|prefix
argument_list|,
name|RMId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|addSuffix
argument_list|(
name|prefix
argument_list|,
name|RMId
argument_list|)
argument_list|,
name|confVal
operator|+
literal|":"
operator|+
name|YarnConfiguration
operator|.
name|getRMDefaultPortNumber
argument_list|(
name|prefix
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|String
name|errmsg
init|=
name|iae
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|rpcAddressConfKey
operator|==
literal|null
condition|)
block|{
comment|// Error at addSuffix
name|errmsg
operator|=
name|getInvalidValueMessage
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|RMId
argument_list|)
expr_stmt|;
block|}
name|throwBadConfigurationException
argument_list|(
name|errmsg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

