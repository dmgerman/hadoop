begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.extensions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|extensions
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Text
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
name|Token
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
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenIdentifier
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
operator|.
name|FS_AZURE_DELEGATION_TOKEN_PROVIDER_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
operator|.
name|FS_AZURE_ENABLE_DELEGATION_TOKEN
import|;
end_import

begin_comment
comment|/**  * This is a Stub DT manager which adds support for {@link BoundDTExtension}  * to {@link ClassicDelegationTokenManager}.  */
end_comment

begin_class
DECL|class|StubDelegationTokenManager
specifier|public
class|class
name|StubDelegationTokenManager
extends|extends
name|ClassicDelegationTokenManager
implements|implements
name|BoundDTExtension
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StubDelegationTokenManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Classname.    */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.fs.azurebfs.extensions.StubDelegationTokenManager"
decl_stmt|;
comment|/**    * Instantiate.    */
DECL|method|StubDelegationTokenManager ()
specifier|public
name|StubDelegationTokenManager
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|bind (final URI uri, final Configuration conf)
specifier|public
name|void
name|bind
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|innerBind
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a token.    *    * @param sequenceNumber sequence number.    * @param uri FS URI    * @param owner FS owner    * @param renewer renewer    * @return a token.    */
DECL|method|createToken ( final int sequenceNumber, final URI uri, final Text owner, final Text renewer)
specifier|public
specifier|static
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|createToken
parameter_list|(
specifier|final
name|int
name|sequenceNumber
parameter_list|,
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|Text
name|owner
parameter_list|,
specifier|final
name|Text
name|renewer
parameter_list|)
block|{
return|return
name|ClassicDelegationTokenManager
operator|.
name|createToken
argument_list|(
name|sequenceNumber
argument_list|,
name|uri
argument_list|,
name|owner
argument_list|,
name|renewer
argument_list|)
return|;
block|}
comment|/**    * Patch a configuration to declare this the DT provider for a filesystem    * built off the given configuration.    * The ABFS Filesystem still needs to come up with security enabled.    * @param conf configuration.    * @return the patched configuration.    */
DECL|method|useStubDTManager (Configuration conf)
specifier|public
specifier|static
name|Configuration
name|useStubDTManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|FS_AZURE_ENABLE_DELEGATION_TOKEN
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FS_AZURE_DELEGATION_TOKEN_PROVIDER_TYPE
argument_list|,
name|StubDelegationTokenManager
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

