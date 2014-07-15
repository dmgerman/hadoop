begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.alias
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|alias
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
name|util
operator|.
name|List
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * A provider of credentials or password for Hadoop applications. Provides an  * abstraction to separate credential storage from users of them. It  * is intended to support getting or storing passwords in a variety of ways,  * including third party bindings.  *   *<code>CredentialProvider</code> implementations must be thread safe.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|CredentialProvider
specifier|public
specifier|abstract
class|class
name|CredentialProvider
block|{
DECL|field|CLEAR_TEXT_FALLBACK
specifier|public
specifier|static
specifier|final
name|String
name|CLEAR_TEXT_FALLBACK
init|=
literal|"hadoop.security.credential.clear-text-fallback"
decl_stmt|;
comment|/**    * The combination of both the alias and the actual credential value.    */
DECL|class|CredentialEntry
specifier|public
specifier|static
class|class
name|CredentialEntry
block|{
DECL|field|alias
specifier|private
specifier|final
name|String
name|alias
decl_stmt|;
DECL|field|credential
specifier|private
specifier|final
name|char
index|[]
name|credential
decl_stmt|;
DECL|method|CredentialEntry (String alias, char[] credential)
specifier|protected
name|CredentialEntry
parameter_list|(
name|String
name|alias
parameter_list|,
name|char
index|[]
name|credential
parameter_list|)
block|{
name|this
operator|.
name|alias
operator|=
name|alias
expr_stmt|;
name|this
operator|.
name|credential
operator|=
name|credential
expr_stmt|;
block|}
DECL|method|getAlias ()
specifier|public
name|String
name|getAlias
parameter_list|()
block|{
return|return
name|alias
return|;
block|}
DECL|method|getCredential ()
specifier|public
name|char
index|[]
name|getCredential
parameter_list|()
block|{
return|return
name|credential
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"alias("
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|alias
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|")="
argument_list|)
expr_stmt|;
if|if
condition|(
name|credential
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|char
name|c
range|:
name|credential
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Indicates whether this provider represents a store    * that is intended for transient use - such as the UserProvider    * is. These providers are generally used to provide job access to    * passwords rather than for long term storage.    * @return true if transient, false otherwise    */
DECL|method|isTransient ()
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Ensures that any changes to the credentials are written to persistent store.    * @throws IOException    */
DECL|method|flush ()
specifier|public
specifier|abstract
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the credential entry for a specific alias.    * @param alias the name of a specific credential    * @return the credentialEntry    * @throws IOException    */
DECL|method|getCredentialEntry (String alias)
specifier|public
specifier|abstract
name|CredentialEntry
name|getCredentialEntry
parameter_list|(
name|String
name|alias
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the aliases for all credentials.    * @return the list of alias names    * @throws IOException    */
DECL|method|getAliases ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getAliases
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a new credential. The given alias must not already exist.    * @param name the alias of the credential    * @param credential the credential value for the alias.    * @throws IOException    */
DECL|method|createCredentialEntry (String name, char[] credential)
specifier|public
specifier|abstract
name|CredentialEntry
name|createCredentialEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|char
index|[]
name|credential
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete the given credential.    * @param name the alias of the credential to delete    * @throws IOException    */
DECL|method|deleteCredentialEntry (String name)
specifier|public
specifier|abstract
name|void
name|deleteCredentialEntry
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

