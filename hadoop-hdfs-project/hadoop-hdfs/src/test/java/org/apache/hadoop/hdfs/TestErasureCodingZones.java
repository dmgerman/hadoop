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
name|protocol
operator|.
name|ErasureCodingInfo
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
name|namenode
operator|.
name|ErasureCodingSchemaManager
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
name|namenode
operator|.
name|FSNamesystem
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
name|namenode
operator|.
name|INode
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
name|erasurecode
operator|.
name|ECSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|assertExceptionContains
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

begin_class
DECL|class|TestErasureCodingZones
specifier|public
class|class
name|TestErasureCodingZones
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|namesystem
specifier|private
name|FSNamesystem
name|namesystem
decl_stmt|;
annotation|@
name|Before
DECL|method|setupCluster ()
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|namesystem
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdownCluster ()
specifier|public
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateECZone ()
specifier|public
name|void
name|testCreateECZone
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|Path
name|testDir
init|=
operator|new
name|Path
argument_list|(
literal|"/ec"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|testDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
comment|/* Normal creation of an erasure coding zone */
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|testDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|/* Verify files under the zone are striped */
specifier|final
name|Path
name|ECFilePath
init|=
operator|new
name|Path
argument_list|(
name|testDir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|ECFilePath
argument_list|)
expr_stmt|;
name|INode
name|inode
init|=
name|namesystem
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
name|ECFilePath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|inode
operator|.
name|asFile
argument_list|()
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
comment|/* Verify that EC zone cannot be created on non-empty dir */
specifier|final
name|Path
name|notEmpty
init|=
operator|new
name|Path
argument_list|(
literal|"/nonEmpty"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|notEmpty
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|notEmpty
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|notEmpty
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Erasure coding zone on non-empty dir"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"erasure coding zone for a non-empty directory"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|/* Verify that nested EC zones cannot be created */
specifier|final
name|Path
name|zone1
init|=
operator|new
name|Path
argument_list|(
literal|"/zone1"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|zone2
init|=
operator|new
name|Path
argument_list|(
name|zone1
argument_list|,
literal|"zone2"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|zone1
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|zone1
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|zone2
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|zone2
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Nested erasure coding zones"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"already in an erasure coding zone"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|/* Verify that EC zone cannot be created on a file */
specifier|final
name|Path
name|fPath
init|=
operator|new
name|Path
argument_list|(
literal|"/file"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|fPath
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|fPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Erasure coding zone on file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"erasure coding zone for a file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMoveValidity ()
specifier|public
name|void
name|testMoveValidity
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|Path
name|srcECDir
init|=
operator|new
name|Path
argument_list|(
literal|"/srcEC"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|dstECDir
init|=
operator|new
name|Path
argument_list|(
literal|"/dstEC"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|srcECDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|dstECDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|srcECDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|dstECDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|srcFile
init|=
operator|new
name|Path
argument_list|(
name|srcECDir
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|srcFile
argument_list|)
expr_stmt|;
comment|/* Verify that a file can be moved between 2 EC zones */
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|srcFile
argument_list|,
name|dstECDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"A file should be able to move between 2 EC zones "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Move the file back
name|fs
operator|.
name|rename
argument_list|(
operator|new
name|Path
argument_list|(
name|dstECDir
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
name|srcECDir
argument_list|)
expr_stmt|;
comment|/* Verify that a file cannot be moved from a non-EC dir to an EC zone */
specifier|final
name|Path
name|nonECDir
init|=
operator|new
name|Path
argument_list|(
literal|"/nonEC"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|nonECDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|srcFile
argument_list|,
name|nonECDir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A file shouldn't be able to move from a non-EC dir to an EC zone"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"can't be moved because the source and "
operator|+
literal|"destination have different erasure coding policies"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|/* Verify that a file cannot be moved from an EC zone to a non-EC dir */
specifier|final
name|Path
name|nonECFile
init|=
operator|new
name|Path
argument_list|(
name|nonECDir
argument_list|,
literal|"nonECFile"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|nonECFile
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|rename
argument_list|(
name|nonECFile
argument_list|,
name|dstECDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"can't be moved because the source and "
operator|+
literal|"destination have different erasure coding policies"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetErasureCodingInfoWithSystemDefaultSchema ()
specifier|public
name|void
name|testGetErasureCodingInfoWithSystemDefaultSchema
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|src
init|=
literal|"/ec"
decl_stmt|;
specifier|final
name|Path
name|ecDir
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|ecDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
comment|// dir ECInfo before creating ec zone
name|assertNull
argument_list|(
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getErasureCodingInfo
argument_list|(
name|src
argument_list|)
argument_list|)
expr_stmt|;
comment|// dir ECInfo after creating ec zone
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|src
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//Default one will be used.
name|ECSchema
name|sysDefaultSchema
init|=
name|ErasureCodingSchemaManager
operator|.
name|getSystemDefaultSchema
argument_list|()
decl_stmt|;
name|verifyErasureCodingInfo
argument_list|(
name|src
argument_list|,
name|sysDefaultSchema
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|ecDir
argument_list|,
literal|"/child1"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify for the files in ec zone
name|verifyErasureCodingInfo
argument_list|(
name|src
operator|+
literal|"/child1"
argument_list|,
name|sysDefaultSchema
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetErasureCodingInfo ()
specifier|public
name|void
name|testGetErasureCodingInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|ECSchema
index|[]
name|sysSchemas
init|=
name|ErasureCodingSchemaManager
operator|.
name|getSystemSchemas
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"System schemas should be of only 1 for now"
argument_list|,
name|sysSchemas
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|ECSchema
name|usingSchema
init|=
name|sysSchemas
index|[
literal|0
index|]
decl_stmt|;
name|String
name|src
init|=
literal|"/ec2"
decl_stmt|;
specifier|final
name|Path
name|ecDir
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdir
argument_list|(
name|ecDir
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|)
expr_stmt|;
comment|// dir ECInfo before creating ec zone
name|assertNull
argument_list|(
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getErasureCodingInfo
argument_list|(
name|src
argument_list|)
argument_list|)
expr_stmt|;
comment|// dir ECInfo after creating ec zone
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|src
argument_list|,
name|usingSchema
argument_list|)
expr_stmt|;
name|verifyErasureCodingInfo
argument_list|(
name|src
argument_list|,
name|usingSchema
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|ecDir
argument_list|,
literal|"/child1"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify for the files in ec zone
name|verifyErasureCodingInfo
argument_list|(
name|src
operator|+
literal|"/child1"
argument_list|,
name|usingSchema
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyErasureCodingInfo ( String src, ECSchema usingSchema)
specifier|private
name|void
name|verifyErasureCodingInfo
parameter_list|(
name|String
name|src
parameter_list|,
name|ECSchema
name|usingSchema
parameter_list|)
throws|throws
name|IOException
block|{
name|ErasureCodingInfo
name|ecInfo
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getErasureCodingInfo
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"ECInfo should have been non-null"
argument_list|,
name|ecInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|src
argument_list|,
name|ecInfo
operator|.
name|getSrc
argument_list|()
argument_list|)
expr_stmt|;
name|ECSchema
name|schema
init|=
name|ecInfo
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Actually used schema should be equal with target schema"
argument_list|,
name|usingSchema
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

