begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
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
name|common
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

begin_comment
comment|/**  * Base upgrade upgradeStatus class.  * Overload this class if specific status fields need to be reported.  *   * Describes status of current upgrade.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|UpgradeStatusReport
specifier|public
class|class
name|UpgradeStatusReport
block|{
DECL|field|version
specifier|protected
specifier|final
name|int
name|version
decl_stmt|;
DECL|field|upgradeStatus
specifier|protected
specifier|final
name|short
name|upgradeStatus
decl_stmt|;
DECL|field|finalized
specifier|protected
specifier|final
name|boolean
name|finalized
decl_stmt|;
DECL|method|UpgradeStatusReport (int version, short status, boolean isFinalized)
specifier|public
name|UpgradeStatusReport
parameter_list|(
name|int
name|version
parameter_list|,
name|short
name|status
parameter_list|,
name|boolean
name|isFinalized
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|upgradeStatus
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|finalized
operator|=
name|isFinalized
expr_stmt|;
block|}
comment|/**    * Get the layout version of the currently running upgrade.    * @return layout version    */
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
comment|/**    * Get upgrade upgradeStatus as a percentage of the total upgrade done.    *     * @see Upgradeable#getUpgradeStatus()     */
DECL|method|getUpgradeStatus ()
specifier|public
name|short
name|getUpgradeStatus
parameter_list|()
block|{
return|return
name|upgradeStatus
return|;
block|}
comment|/**    * Is current upgrade finalized.    * @return true if finalized or false otherwise.    */
DECL|method|isFinalized ()
specifier|public
name|boolean
name|isFinalized
parameter_list|()
block|{
return|return
name|this
operator|.
name|finalized
return|;
block|}
comment|/**    * Get upgradeStatus data as a text for reporting.    * Should be overloaded for a particular upgrade specific upgradeStatus data.    *     * @param details true if upgradeStatus details need to be included,     *                false otherwise    * @return text    */
DECL|method|getStatusText (boolean details)
specifier|public
name|String
name|getStatusText
parameter_list|(
name|boolean
name|details
parameter_list|)
block|{
return|return
literal|"Upgrade for version "
operator|+
name|getVersion
argument_list|()
operator|+
operator|(
name|upgradeStatus
operator|<
literal|100
condition|?
literal|" is in progress. Status = "
operator|+
name|upgradeStatus
operator|+
literal|"%"
else|:
literal|" has been completed."
operator|+
literal|"\nUpgrade is "
operator|+
operator|(
name|finalized
condition|?
literal|""
else|:
literal|"not "
operator|)
operator|+
literal|"finalized."
operator|)
return|;
block|}
comment|/**    * Print basic upgradeStatus details.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getStatusText
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
end_class

end_unit

