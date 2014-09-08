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
name|File
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
name|java
operator|.
name|util
operator|.
name|UUID
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
operator|.
name|KeyVersion
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
name|FileStatus
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
name|FileSystem
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
name|permission
operator|.
name|FsPermission
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
name|Credentials
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
name|ProviderUtils
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
name|UserGroupInformation
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
name|Before
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestKeyProviderFactory
specifier|public
class|class
name|TestKeyProviderFactory
block|{
DECL|field|tmpDir
specifier|private
specifier|static
name|File
name|tmpDir
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|tmpDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"target"
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFactory ()
specifier|public
name|void
name|testFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|userUri
init|=
name|UserProvider
operator|.
name|SCHEME_NAME
operator|+
literal|":///"
decl_stmt|;
specifier|final
name|Path
name|jksPath
init|=
operator|new
name|Path
argument_list|(
name|tmpDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|jksUri
init|=
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
name|jksPath
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
name|userUri
operator|+
literal|","
operator|+
name|jksUri
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|KeyProvider
argument_list|>
name|providers
init|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|providers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|UserProvider
operator|.
name|class
argument_list|,
name|providers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|JavaKeyStoreProvider
operator|.
name|class
argument_list|,
name|providers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|userUri
argument_list|,
name|providers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jksUri
argument_list|,
name|providers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFactoryErrors ()
specifier|public
name|void
name|testFactoryErrors
parameter_list|()
throws|throws
name|Exception
block|{
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
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
literal|"unknown:///"
argument_list|)
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|KeyProvider
argument_list|>
name|providers
init|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"should throw!"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"No KeyProviderFactory for unknown:/// in "
operator|+
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUriErrors ()
specifier|public
name|void
name|testUriErrors
parameter_list|()
throws|throws
name|Exception
block|{
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
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
literal|"unkn@own:/x/y"
argument_list|)
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|KeyProvider
argument_list|>
name|providers
init|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"should throw!"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Bad configuration of "
operator|+
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
operator|+
literal|" at unkn@own:/x/y"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkSpecificProvider (Configuration conf, String ourUrl)
specifier|static
name|void
name|checkSpecificProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|ourUrl
parameter_list|)
throws|throws
name|Exception
block|{
name|KeyProvider
name|provider
init|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|byte
index|[]
name|key1
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
name|byte
index|[]
name|key2
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
name|byte
index|[]
name|key3
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|key1
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|key1
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
name|key2
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|*
literal|2
argument_list|)
expr_stmt|;
name|key3
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
comment|// ensure that we get nulls when the key isn't there
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|provider
operator|.
name|getKeyVersion
argument_list|(
literal|"no-such-key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|provider
operator|.
name|getMetadata
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
comment|// create a new key
try|try
block|{
name|provider
operator|.
name|createKey
argument_list|(
literal|"key3"
argument_list|,
name|key3
argument_list|,
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|// check the metadata for key3
name|KeyProvider
operator|.
name|Metadata
name|meta
init|=
name|provider
operator|.
name|getMetadata
argument_list|(
literal|"key3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|KeyProvider
operator|.
name|DEFAULT_CIPHER
argument_list|,
name|meta
operator|.
name|getCipher
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|KeyProvider
operator|.
name|DEFAULT_BITLENGTH
argument_list|,
name|meta
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure we get back the right key
name|assertArrayEquals
argument_list|(
name|key3
argument_list|,
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key3"
argument_list|)
operator|.
name|getMaterial
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key3@0"
argument_list|,
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key3"
argument_list|)
operator|.
name|getVersionName
argument_list|()
argument_list|)
expr_stmt|;
comment|// try recreating key3
try|try
block|{
name|provider
operator|.
name|createKey
argument_list|(
literal|"key3"
argument_list|,
name|key3
argument_list|,
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should throw"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Key key3 already exists in "
operator|+
name|ourUrl
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|deleteKey
argument_list|(
literal|"key3"
argument_list|)
expr_stmt|;
try|try
block|{
name|provider
operator|.
name|deleteKey
argument_list|(
literal|"key3"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should throw"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Key key3 does not exist in "
operator|+
name|ourUrl
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|createKey
argument_list|(
literal|"key3"
argument_list|,
name|key3
argument_list|,
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|provider
operator|.
name|createKey
argument_list|(
literal|"key4"
argument_list|,
name|key3
argument_list|,
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
operator|.
name|setBitLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should throw"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong key length. Required 8, but got 128"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|createKey
argument_list|(
literal|"key4"
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
operator|.
name|setBitLength
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|provider
operator|.
name|rollNewVersion
argument_list|(
literal|"key4"
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
name|meta
operator|=
name|provider
operator|.
name|getMetadata
argument_list|(
literal|"key4"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|2
block|}
argument_list|,
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key4"
argument_list|)
operator|.
name|getMaterial
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
name|provider
operator|.
name|getKeyVersion
argument_list|(
literal|"key4@0"
argument_list|)
operator|.
name|getMaterial
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key4@1"
argument_list|,
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key4"
argument_list|)
operator|.
name|getVersionName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|provider
operator|.
name|rollNewVersion
argument_list|(
literal|"key4"
argument_list|,
name|key1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should throw"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong key length. Required 8, but got 128"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|provider
operator|.
name|rollNewVersion
argument_list|(
literal|"no-such-key"
argument_list|,
name|key1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should throw"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Key no-such-key not found"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// get a new instance of the provider to ensure it was saved correctly
name|provider
operator|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|2
block|}
argument_list|,
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key4"
argument_list|)
operator|.
name|getMaterial
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|key3
argument_list|,
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key3"
argument_list|)
operator|.
name|getMaterial
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"key3@0"
argument_list|,
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key3"
argument_list|)
operator|.
name|getVersionName
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|provider
operator|.
name|getKeys
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Keys should have been returned."
argument_list|,
name|keys
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Returned Keys should have included key3."
argument_list|,
name|keys
operator|.
name|contains
argument_list|(
literal|"key3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Returned Keys should have included key4."
argument_list|,
name|keys
operator|.
name|contains
argument_list|(
literal|"key4"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|KeyVersion
argument_list|>
name|kvl
init|=
name|provider
operator|.
name|getKeyVersions
argument_list|(
literal|"key3"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"KeyVersions should have been returned for key3."
argument_list|,
name|kvl
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"KeyVersions should have included key3@0."
argument_list|,
name|kvl
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getVersionName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"key3@0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|key3
argument_list|,
name|kvl
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMaterial
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUserProvider ()
specifier|public
name|void
name|testUserProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|ourUrl
init|=
name|UserProvider
operator|.
name|SCHEME_NAME
operator|+
literal|":///"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
name|ourUrl
argument_list|)
expr_stmt|;
name|checkSpecificProvider
argument_list|(
name|conf
argument_list|,
name|ourUrl
argument_list|)
expr_stmt|;
comment|// see if the credentials are actually in the UGI
name|Credentials
name|credentials
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
name|credentials
operator|.
name|getSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
literal|"key4@0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|2
block|}
argument_list|,
name|credentials
operator|.
name|getSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
literal|"key4@1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJksProvider ()
specifier|public
name|void
name|testJksProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|jksPath
init|=
operator|new
name|Path
argument_list|(
name|tmpDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ourUrl
init|=
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
name|jksPath
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
name|ourUrl
argument_list|)
expr_stmt|;
name|checkSpecificProvider
argument_list|(
name|conf
argument_list|,
name|ourUrl
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|ProviderUtils
operator|.
name|unnestUri
argument_list|(
operator|new
name|URI
argument_list|(
name|ourUrl
argument_list|)
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileStatus
name|s
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|getPermission
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"rwx------"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|file
operator|+
literal|" should exist"
argument_list|,
name|file
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
comment|// Corrupt file and Check if JKS can reload from _OLD file
name|File
name|oldFile
init|=
operator|new
name|File
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
operator|+
literal|"_OLD"
argument_list|)
decl_stmt|;
name|file
operator|.
name|renameTo
argument_list|(
name|oldFile
argument_list|)
expr_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
name|file
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|oldFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|KeyProvider
name|provider
init|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|oldFile
operator|+
literal|"should be deleted"
argument_list|,
operator|!
name|oldFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|verifyAfterReload
argument_list|(
name|file
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|oldFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// _NEW and current file should not exist together
name|File
name|newFile
init|=
operator|new
name|File
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
operator|+
literal|"_NEW"
argument_list|)
decl_stmt|;
name|newFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
try|try
block|{
name|provider
operator|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"_NEW and current file should not exist together !!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore
block|}
finally|finally
block|{
if|if
condition|(
name|newFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|newFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Load from _NEW file
name|file
operator|.
name|renameTo
argument_list|(
name|newFile
argument_list|)
expr_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
try|try
block|{
name|provider
operator|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|newFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|oldFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"JKS should load from _NEW file !!"
argument_list|)
expr_stmt|;
comment|// Ignore
block|}
name|verifyAfterReload
argument_list|(
name|file
argument_list|,
name|provider
argument_list|)
expr_stmt|;
comment|// _NEW exists but corrupt.. must load from _OLD
name|newFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|file
operator|.
name|renameTo
argument_list|(
name|oldFile
argument_list|)
expr_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
try|try
block|{
name|provider
operator|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|newFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|oldFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"JKS should load from _OLD file !!"
argument_list|)
expr_stmt|;
comment|// Ignore
block|}
finally|finally
block|{
if|if
condition|(
name|newFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|newFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
name|verifyAfterReload
argument_list|(
name|file
argument_list|,
name|provider
argument_list|)
expr_stmt|;
comment|// check permission retention after explicit change
name|fs
operator|.
name|setPermission
argument_list|(
name|path
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"777"
argument_list|)
argument_list|)
expr_stmt|;
name|checkPermissionRetention
argument_list|(
name|conf
argument_list|,
name|ourUrl
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyAfterReload (File file, KeyProvider provider)
specifier|private
name|void
name|verifyAfterReload
parameter_list|(
name|File
name|file
parameter_list|,
name|KeyProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|existingKeys
init|=
name|provider
operator|.
name|getKeys
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|existingKeys
operator|.
name|contains
argument_list|(
literal|"key4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|existingKeys
operator|.
name|contains
argument_list|(
literal|"key3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkPermissionRetention (Configuration conf, String ourUrl, Path path)
specifier|public
name|void
name|checkPermissionRetention
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|ourUrl
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|KeyProvider
name|provider
init|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// let's add a new key and flush and check that permissions are still set to 777
name|byte
index|[]
name|key
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|key
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|key
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
comment|// create a new key
try|try
block|{
name|provider
operator|.
name|createKey
argument_list|(
literal|"key5"
argument_list|,
name|key
argument_list|,
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// get a new instance of the provider to ensure it was saved correctly
name|provider
operator|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|key
argument_list|,
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key5"
argument_list|)
operator|.
name|getMaterial
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileStatus
name|s
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Permissions should have been retained from the preexisting keystore."
argument_list|,
name|s
operator|.
name|getPermission
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"rwxrwxrwx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJksProviderPasswordViaConfig ()
specifier|public
name|void
name|testJksProviderPasswordViaConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|jksPath
init|=
operator|new
name|Path
argument_list|(
name|tmpDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ourUrl
init|=
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
name|jksPath
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
try|try
block|{
name|conf
operator|.
name|set
argument_list|(
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
name|ourUrl
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JavaKeyStoreProvider
operator|.
name|KEYSTORE_PASSWORD_FILE_KEY
argument_list|,
literal|"javakeystoreprovider.password"
argument_list|)
expr_stmt|;
name|KeyProvider
name|provider
init|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|provider
operator|.
name|createKey
argument_list|(
literal|"key3"
argument_list|,
operator|new
name|byte
index|[
literal|16
index|]
argument_list|,
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"could not create keystore with password file"
argument_list|)
expr_stmt|;
block|}
name|KeyProvider
name|provider
init|=
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|provider
operator|.
name|getCurrentKey
argument_list|(
literal|"key3"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|conf
operator|.
name|set
argument_list|(
name|JavaKeyStoreProvider
operator|.
name|KEYSTORE_PASSWORD_FILE_KEY
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"using non existing password file, it should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|//NOP
block|}
try|try
block|{
name|conf
operator|.
name|set
argument_list|(
name|JavaKeyStoreProvider
operator|.
name|KEYSTORE_PASSWORD_FILE_KEY
argument_list|,
literal|"core-site.xml"
argument_list|)
expr_stmt|;
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"using different password file, it should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|//NOP
block|}
try|try
block|{
name|conf
operator|.
name|unset
argument_list|(
name|JavaKeyStoreProvider
operator|.
name|KEYSTORE_PASSWORD_FILE_KEY
argument_list|)
expr_stmt|;
name|KeyProviderFactory
operator|.
name|getProviders
argument_list|(
name|conf
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"No password file property, env not set, it should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|//NOP
block|}
block|}
annotation|@
name|Test
DECL|method|testGetProviderViaURI ()
specifier|public
name|void
name|testGetProviderViaURI
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|jksPath
init|=
operator|new
name|Path
argument_list|(
name|tmpDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test.jks"
argument_list|)
decl_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
name|jksPath
operator|.
name|toUri
argument_list|()
argument_list|)
decl_stmt|;
name|KeyProvider
name|kp
init|=
name|KeyProviderFactory
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|kp
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JavaKeyStoreProvider
operator|.
name|class
argument_list|,
name|kp
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|uri
operator|=
operator|new
name|URI
argument_list|(
literal|"foo://bar"
argument_list|)
expr_stmt|;
name|kp
operator|=
name|KeyProviderFactory
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|kp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

