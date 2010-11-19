begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

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
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/** This test makes sure that "DU" does not get to run on each call to getUsed */
end_comment

begin_class
DECL|class|TestDU
specifier|public
class|class
name|TestDU
extends|extends
name|TestCase
block|{
DECL|field|DU_DIR
specifier|final
specifier|static
specifier|private
name|File
name|DU_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"dutmp"
argument_list|)
decl_stmt|;
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|DU_DIR
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DU_DIR
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|DU_DIR
argument_list|)
expr_stmt|;
block|}
DECL|method|createFile (File newFile, int size)
specifier|private
name|void
name|createFile
parameter_list|(
name|File
name|newFile
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write random data so that filesystems with compression enabled (e.g., ZFS)
comment|// can't compress the file
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|newFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|RandomAccessFile
name|file
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|newFile
argument_list|,
literal|"rws"
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|file
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verify that du returns expected used space for a file.    * We assume here that if a file system crates a file of size     * that is a multiple of the block size in this file system,    * then the used size for the file will be exactly that size.    * This is true for most file systems.    *     * @throws IOException    * @throws InterruptedException    */
DECL|method|testDU ()
specifier|public
name|void
name|testDU
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|int
name|writtenSize
init|=
literal|32
operator|*
literal|1024
decl_stmt|;
comment|// writing 32K
comment|// Allow for extra 4K on-disk slack for local file systems
comment|// that may store additional file metadata (eg ext attrs).
specifier|final
name|int
name|slack
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|DU_DIR
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
name|writtenSize
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// let the metadata updater catch up
name|DU
name|du
init|=
operator|new
name|DU
argument_list|(
name|file
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|du
operator|.
name|start
argument_list|()
expr_stmt|;
name|long
name|duSize
init|=
name|du
operator|.
name|getUsed
argument_list|()
decl_stmt|;
name|du
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Invalid on-disk size"
argument_list|,
name|duSize
operator|>=
name|writtenSize
operator|&&
name|writtenSize
operator|<=
operator|(
name|duSize
operator|+
name|slack
operator|)
argument_list|)
expr_stmt|;
comment|//test with 0 interval, will not launch thread
name|du
operator|=
operator|new
name|DU
argument_list|(
name|file
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|du
operator|.
name|start
argument_list|()
expr_stmt|;
name|duSize
operator|=
name|du
operator|.
name|getUsed
argument_list|()
expr_stmt|;
name|du
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Invalid on-disk size"
argument_list|,
name|duSize
operator|>=
name|writtenSize
operator|&&
name|writtenSize
operator|<=
operator|(
name|duSize
operator|+
name|slack
operator|)
argument_list|)
expr_stmt|;
comment|//test without launching thread
name|du
operator|=
operator|new
name|DU
argument_list|(
name|file
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|duSize
operator|=
name|du
operator|.
name|getUsed
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Invalid on-disk size"
argument_list|,
name|duSize
operator|>=
name|writtenSize
operator|&&
name|writtenSize
operator|<=
operator|(
name|duSize
operator|+
name|slack
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

