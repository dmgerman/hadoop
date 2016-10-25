begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|StorageType
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
name|LocalFileSystem
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DataNode
operator|.
name|DataNodeDiskChecker
import|;
end_import

begin_class
DECL|class|TestDataDirs
specifier|public
class|class
name|TestDataDirs
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDataDirParsing ()
specifier|public
name|void
name|testDataDirParsing
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
decl_stmt|;
name|File
name|dir0
init|=
operator|new
name|File
argument_list|(
literal|"/dir0"
argument_list|)
decl_stmt|;
name|File
name|dir1
init|=
operator|new
name|File
argument_list|(
literal|"/dir1"
argument_list|)
decl_stmt|;
name|File
name|dir2
init|=
operator|new
name|File
argument_list|(
literal|"/dir2"
argument_list|)
decl_stmt|;
name|File
name|dir3
init|=
operator|new
name|File
argument_list|(
literal|"/dir3"
argument_list|)
decl_stmt|;
name|File
name|dir4
init|=
operator|new
name|File
argument_list|(
literal|"/dir4"
argument_list|)
decl_stmt|;
name|File
name|dir5
init|=
operator|new
name|File
argument_list|(
literal|"/dir5"
argument_list|)
decl_stmt|;
name|File
name|dir6
init|=
operator|new
name|File
argument_list|(
literal|"/dir6"
argument_list|)
decl_stmt|;
comment|// Verify that a valid string is correctly parsed, and that storage
comment|// type is not case-sensitive and we are able to handle white-space between
comment|// storage type and URI.
name|String
name|locations1
init|=
literal|"[disk]/dir0,[DISK]/dir1,[sSd]/dir2,[disK]/dir3,"
operator|+
literal|"[ram_disk]/dir4,[disk]/dir5, [disk] /dir6, [disk] "
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|locations1
argument_list|)
expr_stmt|;
name|locations
operator|=
name|DataNode
operator|.
name|getStorageLocations
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir0
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir1
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir2
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir3
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|RAM_DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir4
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir5
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir6
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// not asserting the 8th URI since it is incomplete and it in the
comment|// test set to make sure that we don't fail if we get URIs like that.
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify that an unrecognized storage type result in an exception.
name|String
name|locations2
init|=
literal|"[BadMediaType]/dir0,[ssd]/dir1,[disk]/dir2"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|locations2
argument_list|)
expr_stmt|;
try|try
block|{
name|locations
operator|=
name|DataNode
operator|.
name|getStorageLocations
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|DataNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"The exception is expected."
argument_list|,
name|iae
argument_list|)
expr_stmt|;
block|}
comment|// Assert that a string with no storage type specified is
comment|// correctly parsed and the default storage type is picked up.
name|String
name|locations3
init|=
literal|"/dir0,/dir1"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|locations3
argument_list|)
expr_stmt|;
name|locations
operator|=
name|DataNode
operator|.
name|getStorageLocations
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir0
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|is
argument_list|(
name|dir1
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDataDirValidation ()
specifier|public
name|void
name|testDataDirValidation
parameter_list|()
throws|throws
name|Throwable
block|{
name|DataNodeDiskChecker
name|diskChecker
init|=
name|mock
argument_list|(
name|DataNodeDiskChecker
operator|.
name|class
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|()
argument_list|)
operator|.
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|()
argument_list|)
operator|.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|diskChecker
argument_list|)
operator|.
name|checkDir
argument_list|(
name|any
argument_list|(
name|LocalFileSystem
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|LocalFileSystem
name|fs
init|=
name|mock
argument_list|(
name|LocalFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|AbstractList
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageLocation
argument_list|>
argument_list|()
decl_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|StorageLocation
operator|.
name|parse
argument_list|(
literal|"file:/p1/"
argument_list|)
argument_list|)
expr_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|StorageLocation
operator|.
name|parse
argument_list|(
literal|"file:/p2/"
argument_list|)
argument_list|)
expr_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|StorageLocation
operator|.
name|parse
argument_list|(
literal|"file:/p3/"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|checkedLocations
init|=
name|DataNode
operator|.
name|checkStorageLocations
argument_list|(
name|locations
argument_list|,
name|fs
argument_list|,
name|diskChecker
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"number of valid data dirs"
argument_list|,
literal|1
argument_list|,
name|checkedLocations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|validDir
init|=
operator|new
name|File
argument_list|(
name|checkedLocations
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"p3 should be valid"
argument_list|,
operator|new
name|File
argument_list|(
literal|"/p3/"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|,
name|is
argument_list|(
name|validDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

