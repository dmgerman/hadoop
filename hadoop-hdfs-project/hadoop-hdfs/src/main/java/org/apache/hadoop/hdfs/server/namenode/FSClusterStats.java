begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
comment|/**   * This interface is used for retrieving the load related statistics of   * the cluster.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|FSClusterStats
specifier|public
interface|interface
name|FSClusterStats
block|{
comment|/**    * an indication of the total load of the cluster.    *     * @return a count of the total number of block transfers and block    *         writes that are currently occuring on the cluster.    */
DECL|method|getTotalLoad ()
specifier|public
name|int
name|getTotalLoad
parameter_list|()
function_decl|;
comment|/**    * Indicate whether or not the cluster is now avoiding     * to use stale DataNodes for writing.    *     * @return True if the cluster is currently avoiding using stale DataNodes     *         for writing targets, and false otherwise.    */
DECL|method|isAvoidingStaleDataNodesForWrite ()
specifier|public
name|boolean
name|isAvoidingStaleDataNodesForWrite
parameter_list|()
function_decl|;
comment|/**    * Indicates number of datanodes that are in service.    * @return Number of datanodes that are both alive and not decommissioned.    */
DECL|method|getNumDatanodesInService ()
specifier|public
name|int
name|getNumDatanodesInService
parameter_list|()
function_decl|;
comment|/**    * an indication of the average load of non-decommission(ing|ed) nodes    * eligible for block placement    *     * @return average of the in service number of block transfers and block    *         writes that are currently occurring on the cluster.    */
DECL|method|getInServiceXceiverAverage ()
specifier|public
name|double
name|getInServiceXceiverAverage
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

