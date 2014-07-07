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
name|junit
operator|.
name|Test
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
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|assertNull
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

begin_class
DECL|class|TestKeyProvider
specifier|public
class|class
name|TestKeyProvider
block|{
DECL|field|CIPHER
specifier|private
specifier|static
specifier|final
name|String
name|CIPHER
init|=
literal|"AES"
decl_stmt|;
annotation|@
name|Test
DECL|method|testBuildVersionName ()
specifier|public
name|void
name|testBuildVersionName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"/a/b@3"
argument_list|,
name|KeyProvider
operator|.
name|buildVersionName
argument_list|(
literal|"/a/b"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/aaa@12"
argument_list|,
name|KeyProvider
operator|.
name|buildVersionName
argument_list|(
literal|"/aaa"
argument_list|,
literal|12
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseVersionName ()
specifier|public
name|void
name|testParseVersionName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"/a/b"
argument_list|,
name|KeyProvider
operator|.
name|getBaseName
argument_list|(
literal|"/a/b@3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/aaa"
argument_list|,
name|KeyProvider
operator|.
name|getBaseName
argument_list|(
literal|"/aaa@112"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|KeyProvider
operator|.
name|getBaseName
argument_list|(
literal|"no-slashes"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should have thrown"
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
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testKeyMaterial ()
specifier|public
name|void
name|testKeyMaterial
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|key1
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
decl_stmt|;
name|KeyProvider
operator|.
name|KeyVersion
name|obj
init|=
operator|new
name|KeyProvider
operator|.
name|KeyVersion
argument_list|(
literal|"key1"
argument_list|,
literal|"key1@1"
argument_list|,
name|key1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"key1@1"
argument_list|,
name|obj
operator|.
name|getVersionName
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
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|,
name|obj
operator|.
name|getMaterial
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMetadata ()
specifier|public
name|void
name|testMetadata
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Metadata without description
name|DateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"y/m/d"
argument_list|)
decl_stmt|;
name|Date
name|date
init|=
name|format
operator|.
name|parse
argument_list|(
literal|"2013/12/25"
argument_list|)
decl_stmt|;
name|KeyProvider
operator|.
name|Metadata
name|meta
init|=
operator|new
name|KeyProvider
operator|.
name|Metadata
argument_list|(
literal|"myCipher"
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|date
argument_list|,
literal|123
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"myCipher"
argument_list|,
name|meta
operator|.
name|getCipher
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|meta
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|meta
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|date
argument_list|,
name|meta
operator|.
name|getCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
name|KeyProvider
operator|.
name|Metadata
name|second
init|=
operator|new
name|KeyProvider
operator|.
name|Metadata
argument_list|(
name|meta
operator|.
name|serialize
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getCipher
argument_list|()
argument_list|,
name|second
operator|.
name|getCipher
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getBitLength
argument_list|()
argument_list|,
name|second
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|second
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|second
operator|.
name|getAttributes
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getCreated
argument_list|()
argument_list|,
name|second
operator|.
name|getCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|,
name|second
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|newVersion
init|=
name|second
operator|.
name|addVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|newVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|124
argument_list|,
name|second
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
comment|//Metadata with description
name|format
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"y/m/d"
argument_list|)
expr_stmt|;
name|date
operator|=
name|format
operator|.
name|parse
argument_list|(
literal|"2013/12/25"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|meta
operator|=
operator|new
name|KeyProvider
operator|.
name|Metadata
argument_list|(
literal|"myCipher"
argument_list|,
literal|100
argument_list|,
literal|"description"
argument_list|,
name|attributes
argument_list|,
name|date
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"myCipher"
argument_list|,
name|meta
operator|.
name|getCipher
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|meta
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"description"
argument_list|,
name|meta
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attributes
argument_list|,
name|meta
operator|.
name|getAttributes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|date
argument_list|,
name|meta
operator|.
name|getCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
name|second
operator|=
operator|new
name|KeyProvider
operator|.
name|Metadata
argument_list|(
name|meta
operator|.
name|serialize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getCipher
argument_list|()
argument_list|,
name|second
operator|.
name|getCipher
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getBitLength
argument_list|()
argument_list|,
name|second
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getDescription
argument_list|()
argument_list|,
name|second
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getAttributes
argument_list|()
argument_list|,
name|second
operator|.
name|getAttributes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getCreated
argument_list|()
argument_list|,
name|second
operator|.
name|getCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|,
name|second
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
name|newVersion
operator|=
name|second
operator|.
name|addVersion
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|newVersion
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|124
argument_list|,
name|second
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOptions ()
specifier|public
name|void
name|testOptions
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
name|KeyProvider
operator|.
name|DEFAULT_CIPHER_NAME
argument_list|,
literal|"myCipher"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|KeyProvider
operator|.
name|DEFAULT_BITLENGTH_NAME
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|KeyProvider
operator|.
name|Options
name|options
init|=
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"myCipher"
argument_list|,
name|options
operator|.
name|getCipher
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|512
argument_list|,
name|options
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
name|options
operator|.
name|setCipher
argument_list|(
literal|"yourCipher"
argument_list|)
expr_stmt|;
name|options
operator|.
name|setDescription
argument_list|(
literal|"description"
argument_list|)
expr_stmt|;
name|options
operator|.
name|setAttributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|options
operator|.
name|setBitLength
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"yourCipher"
argument_list|,
name|options
operator|.
name|getCipher
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|options
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"description"
argument_list|,
name|options
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attributes
argument_list|,
name|options
operator|.
name|getAttributes
argument_list|()
argument_list|)
expr_stmt|;
name|options
operator|=
name|KeyProvider
operator|.
name|options
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|KeyProvider
operator|.
name|DEFAULT_CIPHER
argument_list|,
name|options
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
name|options
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnnestUri ()
specifier|public
name|void
name|testUnnestUri
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"hdfs://nn.example.com/my/path"
argument_list|)
argument_list|,
name|ProviderUtils
operator|.
name|unnestUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"myscheme://hdfs@nn.example.com/my/path"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"hdfs://nn/my/path?foo=bar&baz=bat#yyy"
argument_list|)
argument_list|,
name|ProviderUtils
operator|.
name|unnestUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"myscheme://hdfs@nn/my/path?foo=bar&baz=bat#yyy"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"inner://hdfs@nn1.example.com/my/path"
argument_list|)
argument_list|,
name|ProviderUtils
operator|.
name|unnestUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"outer://inner@hdfs@nn1.example.com/my/path"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
literal|"user:///"
argument_list|)
argument_list|,
name|ProviderUtils
operator|.
name|unnestUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"outer://user/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|MyKeyProvider
specifier|private
specifier|static
class|class
name|MyKeyProvider
extends|extends
name|KeyProvider
block|{
DECL|field|algorithm
specifier|private
name|String
name|algorithm
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|material
specifier|private
name|byte
index|[]
name|material
decl_stmt|;
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
operator|new
name|Metadata
argument_list|(
name|CIPHER
argument_list|,
literal|128
argument_list|,
literal|"description"
argument_list|,
literal|null
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|,
literal|0
argument_list|)
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
name|this
operator|.
name|material
operator|=
name|material
expr_stmt|;
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
block|{      }
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
name|this
operator|.
name|material
operator|=
name|material
expr_stmt|;
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
block|{      }
annotation|@
name|Override
DECL|method|generateKey (int size, String algorithm)
specifier|protected
name|byte
index|[]
name|generateKey
parameter_list|(
name|int
name|size
parameter_list|,
name|String
name|algorithm
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
return|return
name|super
operator|.
name|generateKey
argument_list|(
name|size
argument_list|,
name|algorithm
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMaterialGeneration ()
specifier|public
name|void
name|testMaterialGeneration
parameter_list|()
throws|throws
name|Exception
block|{
name|MyKeyProvider
name|kp
init|=
operator|new
name|MyKeyProvider
argument_list|()
decl_stmt|;
name|KeyProvider
operator|.
name|Options
name|options
init|=
operator|new
name|KeyProvider
operator|.
name|Options
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|options
operator|.
name|setCipher
argument_list|(
name|CIPHER
argument_list|)
expr_stmt|;
name|options
operator|.
name|setBitLength
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|kp
operator|.
name|createKey
argument_list|(
literal|"hello"
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|kp
operator|.
name|size
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|CIPHER
argument_list|,
name|kp
operator|.
name|algorithm
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|kp
operator|.
name|material
argument_list|)
expr_stmt|;
name|kp
operator|=
operator|new
name|MyKeyProvider
argument_list|()
expr_stmt|;
name|kp
operator|.
name|rollNewVersion
argument_list|(
literal|"hello"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|128
argument_list|,
name|kp
operator|.
name|size
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|CIPHER
argument_list|,
name|kp
operator|.
name|algorithm
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|kp
operator|.
name|material
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

