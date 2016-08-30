begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
operator|.
name|AdminStates
import|;
end_import

begin_comment
comment|/**  * The class describes the configured admin properties for a datanode.  *  * It is the static configuration specified by administrators via dfsadmin  * command; different from the runtime state. CombinedHostFileManager uses  * the class to deserialize the configurations from json-based file format.  *  * To decommission a node, use AdminStates.DECOMMISSIONED.  */
end_comment

begin_class
DECL|class|DatanodeAdminProperties
specifier|public
class|class
name|DatanodeAdminProperties
block|{
DECL|field|hostName
specifier|private
name|String
name|hostName
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|upgradeDomain
specifier|private
name|String
name|upgradeDomain
decl_stmt|;
DECL|field|adminState
specifier|private
name|AdminStates
name|adminState
init|=
name|AdminStates
operator|.
name|NORMAL
decl_stmt|;
DECL|field|maintenanceExpireTimeInMS
specifier|private
name|long
name|maintenanceExpireTimeInMS
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**    * Return the host name of the datanode.    * @return the host name of the datanode.    */
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
comment|/**    * Set the host name of the datanode.    * @param hostName the host name of the datanode.    */
DECL|method|setHostName (final String hostName)
specifier|public
name|void
name|setHostName
parameter_list|(
specifier|final
name|String
name|hostName
parameter_list|)
block|{
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
block|}
comment|/**    * Get the port number of the datanode.    * @return the port number of the datanode.    */
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
comment|/**    * Set the port number of the datanode.    * @param port the port number of the datanode.    */
DECL|method|setPort (final int port)
specifier|public
name|void
name|setPort
parameter_list|(
specifier|final
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
comment|/**    * Get the upgrade domain of the datanode.    * @return the upgrade domain of the datanode.    */
DECL|method|getUpgradeDomain ()
specifier|public
name|String
name|getUpgradeDomain
parameter_list|()
block|{
return|return
name|upgradeDomain
return|;
block|}
comment|/**    * Set the upgrade domain of the datanode.    * @param upgradeDomain the upgrade domain of the datanode.    */
DECL|method|setUpgradeDomain (final String upgradeDomain)
specifier|public
name|void
name|setUpgradeDomain
parameter_list|(
specifier|final
name|String
name|upgradeDomain
parameter_list|)
block|{
name|this
operator|.
name|upgradeDomain
operator|=
name|upgradeDomain
expr_stmt|;
block|}
comment|/**    * Get the admin state of the datanode.    * @return the admin state of the datanode.    */
DECL|method|getAdminState ()
specifier|public
name|AdminStates
name|getAdminState
parameter_list|()
block|{
return|return
name|adminState
return|;
block|}
comment|/**    * Set the admin state of the datanode.    * @param adminState the admin state of the datanode.    */
DECL|method|setAdminState (final AdminStates adminState)
specifier|public
name|void
name|setAdminState
parameter_list|(
specifier|final
name|AdminStates
name|adminState
parameter_list|)
block|{
name|this
operator|.
name|adminState
operator|=
name|adminState
expr_stmt|;
block|}
comment|/**    * Get the maintenance expiration time in milliseconds.    * @return the maintenance expiration time in milliseconds.    */
DECL|method|getMaintenanceExpireTimeInMS ()
specifier|public
name|long
name|getMaintenanceExpireTimeInMS
parameter_list|()
block|{
return|return
name|this
operator|.
name|maintenanceExpireTimeInMS
return|;
block|}
comment|/**    * Get the maintenance expiration time in milliseconds.    * @param maintenanceExpireTimeInMS    *        the maintenance expiration time in milliseconds.    */
DECL|method|setMaintenanceExpireTimeInMS ( final long maintenanceExpireTimeInMS)
specifier|public
name|void
name|setMaintenanceExpireTimeInMS
parameter_list|(
specifier|final
name|long
name|maintenanceExpireTimeInMS
parameter_list|)
block|{
name|this
operator|.
name|maintenanceExpireTimeInMS
operator|=
name|maintenanceExpireTimeInMS
expr_stmt|;
block|}
block|}
end_class

end_unit

