begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderFactory
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|KMSClientProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestKeyProviderCache
specifier|public
class|class
name|TestKeyProviderCache
block|{
DECL|class|DummyKeyProvider
specifier|public
specifier|static
class|class
name|DummyKeyProvider
extends|extends
name|KeyProvider
block|{
DECL|method|DummyKeyProvider (Configuration conf)
specifier|public
name|DummyKeyProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKeyVersion (String versionName)
specifier|public
name|KeyVersion
name|getKeyVersion
parameter_list|(
name|String
name|versionName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getKeys ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getKeys
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getKeyVersions (String name)
specifier|public
name|List
argument_list|<
name|KeyVersion
argument_list|>
name|getKeyVersions
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getMetadata (String name)
specifier|public
name|Metadata
name|getMetadata
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|createKey (String name, byte[] material, Options options)
specifier|public
name|KeyVersion
name|createKey
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|material
parameter_list|,
name|Options
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|deleteKey (String name)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|rollNewVersion (String name, byte[] material)
specifier|public
name|KeyVersion
name|rollNewVersion
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|material
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{     }
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
literal|"dummy"
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
return|return
operator|new
name|DummyKeyProvider
argument_list|(
name|conf
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCache ()
specifier|public
name|void
name|testCache
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyProviderCache
name|kpCache
init|=
operator|new
name|KeyProviderCache
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ENCRYPTION_KEY_PROVIDER_URI
argument_list|,
literal|"dummy://foo:bar@test_provider1"
argument_list|)
expr_stmt|;
name|KeyProvider
name|keyProvider1
init|=
name|kpCache
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Returned Key Provider is null !!"
argument_list|,
name|keyProvider1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ENCRYPTION_KEY_PROVIDER_URI
argument_list|,
literal|"dummy://foo:bar@test_provider1"
argument_list|)
expr_stmt|;
name|KeyProvider
name|keyProvider2
init|=
name|kpCache
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Different KeyProviders returned !!"
argument_list|,
name|keyProvider1
operator|==
name|keyProvider2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ENCRYPTION_KEY_PROVIDER_URI
argument_list|,
literal|"dummy://test_provider3"
argument_list|)
expr_stmt|;
name|KeyProvider
name|keyProvider3
init|=
name|kpCache
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Same KeyProviders returned !!"
argument_list|,
name|keyProvider1
operator|==
name|keyProvider3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ENCRYPTION_KEY_PROVIDER_URI
argument_list|,
literal|"dummy://hello:there@test_provider1"
argument_list|)
expr_stmt|;
name|KeyProvider
name|keyProvider4
init|=
name|kpCache
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Same KeyProviders returned !!"
argument_list|,
name|keyProvider1
operator|==
name|keyProvider4
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

