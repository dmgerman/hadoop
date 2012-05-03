begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|io
operator|.
name|retry
operator|.
name|Idempotent
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
name|AccessControlException
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
name|KerberosInfo
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

begin_comment
comment|/**  * Protocol exposed by the ZKFailoverController, allowing for graceful  * failover.  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_USER_NAME_KEY
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|ZKFCProtocol
specifier|public
interface|interface
name|ZKFCProtocol
block|{
comment|/**    * Initial version of the protocol    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * Request that this service yield from the active node election for the    * specified time period.    *     * If the node is not currently active, it simply prevents any attempts    * to become active for the specified time period. Otherwise, it first    * tries to transition the local service to standby state, and then quits    * the election.    *     * If the attempt to transition to standby succeeds, then the ZKFC receiving    * this RPC will delete its own breadcrumb node in ZooKeeper. Thus, the    * next node to become active will not run any fencing process. Otherwise,    * the breadcrumb will be left, such that the next active will fence this    * node.    *     * After the specified time period elapses, the node will attempt to re-join    * the election, provided that its service is healthy.    *     * If the node has previously been instructed to cede active, and is still    * within the specified time period, the later command's time period will    * take precedence, resetting the timer.    *     * A call to cedeActive which specifies a 0 or negative time period will    * allow the target node to immediately rejoin the election, so long as    * it is healthy.    *      * @param millisToCede period for which the node should not attempt to    * become active    * @throws IOException if the operation fails    * @throws AccessControlException if the operation is disallowed    */
annotation|@
name|Idempotent
DECL|method|cedeActive (int millisToCede)
specifier|public
name|void
name|cedeActive
parameter_list|(
name|int
name|millisToCede
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
function_decl|;
comment|/**    * Request that this node try to become active through a graceful failover.    *     * If the node is already active, this is a no-op and simply returns success    * without taking any further action.    *     * If the node is not healthy, it will throw an exception indicating that it    * is not able to become active.    *     * If the node is healthy and not active, it will try to initiate a graceful    * failover to become active, returning only when it has successfully become    * active. See {@link ZKFailoverController#gracefulFailoverToYou()} for the    * implementation details.    *     * If the node fails to successfully coordinate the failover, throws an    * exception indicating the reason for failure.    *     * @throws IOException if graceful failover fails    * @throws AccessControlException if the operation is disallowed    */
annotation|@
name|Idempotent
DECL|method|gracefulFailover ()
specifier|public
name|void
name|gracefulFailover
parameter_list|()
throws|throws
name|IOException
throws|,
name|AccessControlException
function_decl|;
block|}
end_interface

end_unit

