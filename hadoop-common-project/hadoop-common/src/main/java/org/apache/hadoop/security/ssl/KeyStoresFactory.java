begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.ssl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
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

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|KeyManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManager
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
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_comment
comment|/**  * Interface that gives access to {@link KeyManager} and {@link TrustManager}  * implementations.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|KeyStoresFactory
specifier|public
interface|interface
name|KeyStoresFactory
extends|extends
name|Configurable
block|{
comment|/**    * Initializes the keystores of the factory.    *    * @param mode if the keystores are to be used in client or server mode.    * @throws IOException thrown if the keystores could not be initialized due    * to an IO error.    * @throws GeneralSecurityException thrown if the keystores could not be    * initialized due to an security error.    */
DECL|method|init (SSLFactory.Mode mode)
specifier|public
name|void
name|init
parameter_list|(
name|SSLFactory
operator|.
name|Mode
name|mode
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
function_decl|;
comment|/**    * Releases any resources being used.    */
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
function_decl|;
comment|/**    * Returns the keymanagers for owned certificates.    *    * @return the keymanagers for owned certificates.    */
DECL|method|getKeyManagers ()
specifier|public
name|KeyManager
index|[]
name|getKeyManagers
parameter_list|()
function_decl|;
comment|/**    * Returns the trustmanagers for trusted certificates.    *    * @return the trustmanagers for trusted certificates.    */
DECL|method|getTrustManagers ()
specifier|public
name|TrustManager
index|[]
name|getTrustManagers
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

