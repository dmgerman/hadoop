begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
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
operator|.
name|LimitedPrivate
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
operator|.
name|Evolving
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
name|security
operator|.
name|token
operator|.
name|TokenInfo
import|;
end_import

begin_class
annotation|@
name|Evolving
annotation|@
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|,
literal|"HDFS"
block|}
argument_list|)
comment|/**  * Interface used by RPC to get the Security information for a given   * protocol.  */
DECL|class|SecurityInfo
specifier|public
specifier|abstract
class|class
name|SecurityInfo
block|{
comment|/**    * Get the KerberosInfo for a given protocol.    * @param protocol interface class    * @param conf configuration    * @return KerberosInfo    */
DECL|method|getKerberosInfo (Class<?> protocol, Configuration conf)
specifier|public
specifier|abstract
name|KerberosInfo
name|getKerberosInfo
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * Get the TokenInfo for a given protocol.    * @param protocol interface class    * @param conf configuration object.    * @return TokenInfo instance    */
DECL|method|getTokenInfo (Class<?> protocol, Configuration conf)
specifier|public
specifier|abstract
name|TokenInfo
name|getTokenInfo
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Configuration
name|conf
parameter_list|)
function_decl|;
block|}
end_class

end_unit

