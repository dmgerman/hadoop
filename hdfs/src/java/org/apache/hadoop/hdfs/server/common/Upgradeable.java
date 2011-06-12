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
name|protocol
operator|.
name|UpgradeCommand
import|;
end_import

begin_comment
comment|/**  * Common interface for distributed upgrade objects.  *   * Each upgrade object corresponds to a layout version,  * which is the latest version that should be upgraded using this object.  * That is all components whose layout version is greater or equal to the  * one returned by {@link #getVersion()} must be upgraded with this object.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|Upgradeable
specifier|public
interface|interface
name|Upgradeable
extends|extends
name|Comparable
argument_list|<
name|Upgradeable
argument_list|>
block|{
comment|/**    * Get the layout version of the upgrade object.    * @return layout version    */
DECL|method|getVersion ()
name|int
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Get the type of the software component, which this object is upgrading.    * @return type    */
DECL|method|getType ()
name|HdfsConstants
operator|.
name|NodeType
name|getType
parameter_list|()
function_decl|;
comment|/**    * Description of the upgrade object for displaying.    * @return description    */
DECL|method|getDescription ()
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/**    * Upgrade status determines a percentage of the work done out of the total     * amount required by the upgrade.    *     * 100% means that the upgrade is completed.    * Any value< 100 means it is not complete.    *     * The return value should provide at least 2 values, e.g. 0 and 100.    * @return integer value in the range [0, 100].    */
DECL|method|getUpgradeStatus ()
name|short
name|getUpgradeStatus
parameter_list|()
function_decl|;
comment|/**    * Prepare for the upgrade.    * E.g. initialize upgrade data structures and set status to 0.    *     * Returns an upgrade command that is used for broadcasting to other cluster    * components.     * E.g. name-node informs data-nodes that they must perform a distributed upgrade.    *     * @return an UpgradeCommand for broadcasting.    * @throws IOException    */
DECL|method|startUpgrade ()
name|UpgradeCommand
name|startUpgrade
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Complete upgrade.    * E.g. cleanup upgrade data structures or write metadata to disk.    *     * Returns an upgrade command that is used for broadcasting to other cluster    * components.     * E.g. data-nodes inform the name-node that they completed the upgrade    * while other data-nodes are still upgrading.    *     * @throws IOException    */
DECL|method|completeUpgrade ()
name|UpgradeCommand
name|completeUpgrade
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get status report for the upgrade.    *     * @param details true if upgradeStatus details need to be included,     *                false otherwise    * @return {@link UpgradeStatusReport}    * @throws IOException    */
DECL|method|getUpgradeStatusReport (boolean details)
name|UpgradeStatusReport
name|getUpgradeStatusReport
parameter_list|(
name|boolean
name|details
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

