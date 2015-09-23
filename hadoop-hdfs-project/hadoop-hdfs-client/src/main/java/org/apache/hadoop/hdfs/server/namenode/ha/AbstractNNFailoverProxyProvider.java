begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
operator|.
name|ha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|io
operator|.
name|retry
operator|.
name|FailoverProxyProvider
import|;
end_import

begin_class
DECL|class|AbstractNNFailoverProxyProvider
specifier|public
specifier|abstract
class|class
name|AbstractNNFailoverProxyProvider
parameter_list|<
name|T
parameter_list|>
implements|implements
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
block|{
DECL|field|fallbackToSimpleAuth
specifier|private
name|AtomicBoolean
name|fallbackToSimpleAuth
decl_stmt|;
comment|/**    * Inquire whether logical HA URI is used for the implementation. If it is    * used, a special token handling may be needed to make sure a token acquired     * from a node in the HA pair can be used against the other node.     *    * @return true if logical HA URI is used. false, if not used.    */
DECL|method|useLogicalURI ()
specifier|public
specifier|abstract
name|boolean
name|useLogicalURI
parameter_list|()
function_decl|;
comment|/**    * Set for tracking if a secure client falls back to simple auth.  This method    * is synchronized only to stifle a Findbugs warning.    *    * @param fallbackToSimpleAuth - set to true or false during this method to    *   indicate if a secure client falls back to simple auth    */
DECL|method|setFallbackToSimpleAuth ( AtomicBoolean fallbackToSimpleAuth)
specifier|public
specifier|synchronized
name|void
name|setFallbackToSimpleAuth
parameter_list|(
name|AtomicBoolean
name|fallbackToSimpleAuth
parameter_list|)
block|{
name|this
operator|.
name|fallbackToSimpleAuth
operator|=
name|fallbackToSimpleAuth
expr_stmt|;
block|}
DECL|method|getFallbackToSimpleAuth ()
specifier|public
specifier|synchronized
name|AtomicBoolean
name|getFallbackToSimpleAuth
parameter_list|()
block|{
return|return
name|fallbackToSimpleAuth
return|;
block|}
block|}
end_class

end_unit

