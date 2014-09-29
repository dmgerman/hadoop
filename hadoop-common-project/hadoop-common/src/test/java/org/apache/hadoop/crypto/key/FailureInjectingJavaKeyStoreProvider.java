begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
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
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|fs
operator|.
name|Path
import|;
end_import

begin_class
DECL|class|FailureInjectingJavaKeyStoreProvider
specifier|public
class|class
name|FailureInjectingJavaKeyStoreProvider
extends|extends
name|JavaKeyStoreProvider
block|{
DECL|field|SCHEME_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME_NAME
init|=
literal|"failjceks"
decl_stmt|;
DECL|field|backupFail
specifier|private
name|boolean
name|backupFail
init|=
literal|false
decl_stmt|;
DECL|field|writeFail
specifier|private
name|boolean
name|writeFail
init|=
literal|false
decl_stmt|;
DECL|method|FailureInjectingJavaKeyStoreProvider (JavaKeyStoreProvider prov)
name|FailureInjectingJavaKeyStoreProvider
parameter_list|(
name|JavaKeyStoreProvider
name|prov
parameter_list|)
block|{
name|super
argument_list|(
name|prov
argument_list|)
expr_stmt|;
block|}
DECL|method|setBackupFail (boolean b)
specifier|public
name|void
name|setBackupFail
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|backupFail
operator|=
name|b
expr_stmt|;
block|}
DECL|method|setWriteFail (boolean b)
specifier|public
name|void
name|setWriteFail
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|backupFail
operator|=
name|b
expr_stmt|;
block|}
comment|// Failure injection methods..
annotation|@
name|Override
DECL|method|writeToNew (Path newPath)
specifier|public
name|void
name|writeToNew
parameter_list|(
name|Path
name|newPath
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|writeFail
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Injecting failure on write"
argument_list|)
throw|;
block|}
name|super
operator|.
name|writeToNew
argument_list|(
name|newPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|backupToOld (Path oldPath)
specifier|public
name|boolean
name|backupToOld
parameter_list|(
name|Path
name|oldPath
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|backupFail
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Inejection Failure on backup"
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|backupToOld
argument_list|(
name|oldPath
argument_list|)
return|;
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|KeyProviderFactory
block|{
annotation|@
name|Override
DECL|method|createProvider (URI providerName, Configuration conf)
specifier|public
name|KeyProvider
name|createProvider
parameter_list|(
name|URI
name|providerName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|SCHEME_NAME
operator|.
name|equals
argument_list|(
name|providerName
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
return|return
operator|new
name|FailureInjectingJavaKeyStoreProvider
argument_list|(
operator|(
name|JavaKeyStoreProvider
operator|)
operator|new
name|JavaKeyStoreProvider
operator|.
name|Factory
argument_list|()
operator|.
name|createProvider
argument_list|(
operator|new
name|URI
argument_list|(
name|providerName
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
name|SCHEME_NAME
argument_list|,
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
argument_list|)
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

