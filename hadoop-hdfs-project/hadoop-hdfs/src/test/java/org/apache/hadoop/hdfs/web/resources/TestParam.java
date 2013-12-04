begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.resources
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|resources
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|CommonConfigurationKeysPublic
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|util
operator|.
name|StringUtils
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
DECL|class|TestParam
specifier|public
class|class
name|TestParam
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestParam
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testAccessTimeParam ()
specifier|public
name|void
name|testAccessTimeParam
parameter_list|()
block|{
specifier|final
name|AccessTimeParam
name|p
init|=
operator|new
name|AccessTimeParam
argument_list|(
name|AccessTimeParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1L
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|AccessTimeParam
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|AccessTimeParam
argument_list|(
operator|-
literal|2L
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockSizeParam ()
specifier|public
name|void
name|testBlockSizeParam
parameter_list|()
block|{
specifier|final
name|BlockSizeParam
name|p
init|=
operator|new
name|BlockSizeParam
argument_list|(
name|BlockSizeParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getLongBytes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_DEFAULT
argument_list|)
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|BlockSizeParam
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|BlockSizeParam
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBufferSizeParam ()
specifier|public
name|void
name|testBufferSizeParam
parameter_list|()
block|{
specifier|final
name|BufferSizeParam
name|p
init|=
operator|new
name|BufferSizeParam
argument_list|(
name|BufferSizeParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|)
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|BufferSizeParam
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|BufferSizeParam
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDelegationParam ()
specifier|public
name|void
name|testDelegationParam
parameter_list|()
block|{
specifier|final
name|DelegationParam
name|p
init|=
operator|new
name|DelegationParam
argument_list|(
name|DelegationParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDestinationParam ()
specifier|public
name|void
name|testDestinationParam
parameter_list|()
block|{
specifier|final
name|DestinationParam
name|p
init|=
operator|new
name|DestinationParam
argument_list|(
name|DestinationParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|DestinationParam
argument_list|(
literal|"/abc"
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|DestinationParam
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGroupParam ()
specifier|public
name|void
name|testGroupParam
parameter_list|()
block|{
specifier|final
name|GroupParam
name|p
init|=
operator|new
name|GroupParam
argument_list|(
name|GroupParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testModificationTimeParam ()
specifier|public
name|void
name|testModificationTimeParam
parameter_list|()
block|{
specifier|final
name|ModificationTimeParam
name|p
init|=
operator|new
name|ModificationTimeParam
argument_list|(
name|ModificationTimeParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1L
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|ModificationTimeParam
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|ModificationTimeParam
argument_list|(
operator|-
literal|2L
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOverwriteParam ()
specifier|public
name|void
name|testOverwriteParam
parameter_list|()
block|{
specifier|final
name|OverwriteParam
name|p
init|=
operator|new
name|OverwriteParam
argument_list|(
name|OverwriteParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|OverwriteParam
argument_list|(
literal|"trUe"
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|OverwriteParam
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOwnerParam ()
specifier|public
name|void
name|testOwnerParam
parameter_list|()
block|{
specifier|final
name|OwnerParam
name|p
init|=
operator|new
name|OwnerParam
argument_list|(
name|OwnerParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPermissionParam ()
specifier|public
name|void
name|testPermissionParam
parameter_list|()
block|{
specifier|final
name|PermissionParam
name|p
init|=
operator|new
name|PermissionParam
argument_list|(
name|PermissionParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|,
name|p
operator|.
name|getFsPermission
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|PermissionParam
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|PermissionParam
argument_list|(
literal|"-1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
operator|new
name|PermissionParam
argument_list|(
literal|"1777"
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|PermissionParam
argument_list|(
literal|"2000"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|PermissionParam
argument_list|(
literal|"8"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|PermissionParam
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRecursiveParam ()
specifier|public
name|void
name|testRecursiveParam
parameter_list|()
block|{
specifier|final
name|RecursiveParam
name|p
init|=
operator|new
name|RecursiveParam
argument_list|(
name|RecursiveParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|RecursiveParam
argument_list|(
literal|"falSe"
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|RecursiveParam
argument_list|(
literal|"abc"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRenewerParam ()
specifier|public
name|void
name|testRenewerParam
parameter_list|()
block|{
specifier|final
name|RenewerParam
name|p
init|=
operator|new
name|RenewerParam
argument_list|(
name|RenewerParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplicationParam ()
specifier|public
name|void
name|testReplicationParam
parameter_list|()
block|{
specifier|final
name|ReplicationParam
name|p
init|=
operator|new
name|ReplicationParam
argument_list|(
name|ReplicationParam
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|p
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_DEFAULT
argument_list|)
argument_list|,
name|p
operator|.
name|getValue
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|ReplicationParam
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|ReplicationParam
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"EXPECTED: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testToSortedStringEscapesURICharacters ()
specifier|public
name|void
name|testToSortedStringEscapesURICharacters
parameter_list|()
block|{
specifier|final
name|String
name|sep
init|=
literal|"&"
decl_stmt|;
name|Param
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|ampParam
init|=
operator|new
name|TokenArgumentParam
argument_list|(
literal|"token&ampersand"
argument_list|)
decl_stmt|;
name|Param
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|equalParam
init|=
operator|new
name|RenewerParam
argument_list|(
literal|"renewer=equal"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"&renewer=renewer%3Dequal&token=token%26ampersand"
decl_stmt|;
specifier|final
name|String
name|actual
init|=
name|Param
operator|.
name|toSortedString
argument_list|(
name|sep
argument_list|,
name|equalParam
argument_list|,
name|ampParam
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|userNameEmpty ()
specifier|public
name|void
name|userNameEmpty
parameter_list|()
block|{
name|UserParam
name|userParam
init|=
operator|new
name|UserParam
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|userParam
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|userNameInvalidStart ()
specifier|public
name|void
name|userNameInvalidStart
parameter_list|()
block|{
operator|new
name|UserParam
argument_list|(
literal|"1x"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|userNameInvalidDollarSign ()
specifier|public
name|void
name|userNameInvalidDollarSign
parameter_list|()
block|{
operator|new
name|UserParam
argument_list|(
literal|"1$x"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|userNameMinLength ()
specifier|public
name|void
name|userNameMinLength
parameter_list|()
block|{
name|UserParam
name|userParam
init|=
operator|new
name|UserParam
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|userParam
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|userNameValidDollarSign ()
specifier|public
name|void
name|userNameValidDollarSign
parameter_list|()
block|{
name|UserParam
name|userParam
init|=
operator|new
name|UserParam
argument_list|(
literal|"a$"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|userParam
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConcatSourcesParam ()
specifier|public
name|void
name|testConcatSourcesParam
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|strings
init|=
block|{
literal|"/"
block|,
literal|"/foo"
block|,
literal|"/bar"
block|}
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|strings
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
specifier|final
name|String
index|[]
name|sub
init|=
operator|new
name|String
index|[
name|n
index|]
decl_stmt|;
specifier|final
name|Path
index|[]
name|paths
init|=
operator|new
name|Path
index|[
name|n
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
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|sub
index|[
name|i
index|]
operator|=
name|strings
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|expected
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|sub
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ConcatSourcesParam
name|computed
init|=
operator|new
name|ConcatSourcesParam
argument_list|(
name|paths
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|computed
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUserNameOkAfterResettingPattern ()
specifier|public
name|void
name|testUserNameOkAfterResettingPattern
parameter_list|()
block|{
name|String
name|oldPattern
init|=
name|UserParam
operator|.
name|getUserPattern
argument_list|()
decl_stmt|;
name|String
name|newPattern
init|=
literal|"^[A-Za-z0-9_][A-Za-z0-9._-]*[$]?$"
decl_stmt|;
name|UserParam
operator|.
name|setUserPattern
argument_list|(
name|newPattern
argument_list|)
expr_stmt|;
name|UserParam
name|userParam
init|=
operator|new
name|UserParam
argument_list|(
literal|"1x"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|userParam
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|userParam
operator|=
operator|new
name|UserParam
argument_list|(
literal|"123"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|userParam
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|UserParam
operator|.
name|setUserPattern
argument_list|(
name|oldPattern
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

