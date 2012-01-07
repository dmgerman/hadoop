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
name|conf
operator|.
name|Configurable
import|;
end_import

begin_comment
comment|/**  * A fencing method is a method by which one node can forcibly prevent  * another node from making continued progress. This might be implemented  * by killing a process on the other node, by denying the other node's  * access to shared storage, or by accessing a PDU to cut the other node's  * power.  *<p>  * Since these methods are often vendor- or device-specific, operators  * may implement this interface in order to achieve fencing.  *<p>  * Fencing is configured by the operator as an ordered list of methods to  * attempt. Each method will be tried in turn, and the next in the list  * will only be attempted if the previous one fails. See {@link NodeFencer}  * for more information.  *<p>  * If an implementation also implements {@link Configurable} then its  *<code>setConf</code> method will be called upon instantiation.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|FenceMethod
specifier|public
interface|interface
name|FenceMethod
block|{
comment|/**    * Verify that the given fencing method's arguments are valid.    * @param args the arguments provided in the configuration. This may    *        be null if the operator did not configure any arguments.    * @throws BadFencingConfigurationException if the arguments are invalid    */
DECL|method|checkArgs (String args)
specifier|public
name|void
name|checkArgs
parameter_list|(
name|String
name|args
parameter_list|)
throws|throws
name|BadFencingConfigurationException
function_decl|;
comment|/**    * Attempt to fence the target node.    * @param args the configured arguments, which were checked at startup by    *             {@link #checkArgs(String)}    * @return true if fencing was successful, false if unsuccessful or    *              indeterminate    * @throws BadFencingConfigurationException if the configuration was    *         determined to be invalid only at runtime    */
DECL|method|tryFence (String args)
specifier|public
name|boolean
name|tryFence
parameter_list|(
name|String
name|args
parameter_list|)
throws|throws
name|BadFencingConfigurationException
function_decl|;
block|}
end_interface

end_unit

