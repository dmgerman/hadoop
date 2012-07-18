begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.raid
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
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
name|DistributedFileSystem
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
name|RaidDFSUtil
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
name|TestRaidDfs
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
name|LocatedBlocks
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
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
DECL|class|TestBlockFixerDistConcurrency
specifier|public
class|class
name|TestBlockFixerDistConcurrency
extends|extends
name|TestBlockFixer
block|{
comment|/**    * tests that we can have 2 concurrent jobs fixing files     * (dist block fixer)    */
annotation|@
name|Test
DECL|method|testConcurrentJobs ()
specifier|public
name|void
name|testConcurrentJobs
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testConcurrentJobs started."
argument_list|)
expr_stmt|;
name|long
name|blockSize
init|=
literal|8192L
decl_stmt|;
name|int
name|stripeLength
init|=
literal|3
decl_stmt|;
name|mySetup
argument_list|(
name|stripeLength
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// never har
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dhruba/raidtest/file1"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dhruba/raidtest/file2"
argument_list|)
decl_stmt|;
name|Path
name|destPath
init|=
operator|new
name|Path
argument_list|(
literal|"/destraid/user/dhruba/raidtest"
argument_list|)
decl_stmt|;
name|long
name|crc1
init|=
name|TestRaidDfs
operator|.
name|createTestFilePartialLastBlock
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|long
name|crc2
init|=
name|TestRaidDfs
operator|.
name|createTestFilePartialLastBlock
argument_list|(
name|fileSys
argument_list|,
name|file2
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|long
name|file1Len
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file1
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|long
name|file2Len
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file2
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testConcurrentJobs created test files"
argument_list|)
expr_stmt|;
comment|// create an instance of the RaidNode
name|Configuration
name|localConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|localConf
operator|.
name|set
argument_list|(
name|RaidNode
operator|.
name|RAID_LOCATION_KEY
argument_list|,
literal|"/destraid"
argument_list|)
expr_stmt|;
name|localConf
operator|.
name|setInt
argument_list|(
literal|"raid.blockfix.interval"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|localConf
operator|.
name|set
argument_list|(
literal|"raid.blockfix.classname"
argument_list|,
literal|"org.apache.hadoop.raid.DistBlockFixer"
argument_list|)
expr_stmt|;
name|localConf
operator|.
name|setLong
argument_list|(
literal|"raid.blockfix.filespertask"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
try|try
block|{
name|cnode
operator|=
name|RaidNode
operator|.
name|createRaidNode
argument_list|(
literal|null
argument_list|,
name|localConf
argument_list|)
expr_stmt|;
name|TestRaidDfs
operator|.
name|waitForFileRaided
argument_list|(
name|LOG
argument_list|,
name|fileSys
argument_list|,
name|file1
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|TestRaidDfs
operator|.
name|waitForFileRaided
argument_list|(
name|LOG
argument_list|,
name|fileSys
argument_list|,
name|file2
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|cnode
operator|.
name|stop
argument_list|()
expr_stmt|;
name|cnode
operator|.
name|join
argument_list|()
expr_stmt|;
name|FileStatus
name|file1Stat
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|FileStatus
name|file2Stat
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file2
argument_list|)
decl_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|fileSys
decl_stmt|;
name|LocatedBlocks
name|file1Loc
init|=
name|RaidDFSUtil
operator|.
name|getBlockLocations
argument_list|(
name|dfs
argument_list|,
name|file1
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|file1Stat
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
name|LocatedBlocks
name|file2Loc
init|=
name|RaidDFSUtil
operator|.
name|getBlockLocations
argument_list|(
name|dfs
argument_list|,
name|file2
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|file2Stat
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|corruptFiles
init|=
name|RaidDFSUtil
operator|.
name|getCorruptFiles
argument_list|(
name|dfs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"no corrupt files expected"
argument_list|,
literal|0
argument_list|,
name|corruptFiles
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"filesFixed() should return 0 before fixing files"
argument_list|,
literal|0
argument_list|,
name|cnode
operator|.
name|blockFixer
operator|.
name|filesFixed
argument_list|()
argument_list|)
expr_stmt|;
comment|// corrupt file1
name|int
index|[]
name|corruptBlockIdxs
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|,
literal|6
block|}
decl_stmt|;
for|for
control|(
name|int
name|idx
range|:
name|corruptBlockIdxs
control|)
name|corruptBlock
argument_list|(
name|file1Loc
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|reportCorruptBlocks
argument_list|(
name|dfs
argument_list|,
name|file1
argument_list|,
name|corruptBlockIdxs
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|cnode
operator|=
name|RaidNode
operator|.
name|createRaidNode
argument_list|(
literal|null
argument_list|,
name|localConf
argument_list|)
expr_stmt|;
name|DistBlockFixer
name|blockFixer
init|=
operator|(
name|DistBlockFixer
operator|)
name|cnode
operator|.
name|blockFixer
decl_stmt|;
name|long
name|start
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
while|while
condition|(
name|blockFixer
operator|.
name|jobsRunning
argument_list|()
operator|<
literal|1
operator|&&
name|Time
operator|.
name|now
argument_list|()
operator|-
name|start
operator|<
literal|240000
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testBlockFix waiting for fixing job 1 to start"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"job 1 not running"
argument_list|,
literal|1
argument_list|,
name|blockFixer
operator|.
name|jobsRunning
argument_list|()
argument_list|)
expr_stmt|;
comment|// corrupt file2
for|for
control|(
name|int
name|idx
range|:
name|corruptBlockIdxs
control|)
name|corruptBlock
argument_list|(
name|file2Loc
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|reportCorruptBlocks
argument_list|(
name|dfs
argument_list|,
name|file2
argument_list|,
name|corruptBlockIdxs
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
while|while
condition|(
name|blockFixer
operator|.
name|jobsRunning
argument_list|()
operator|<
literal|2
operator|&&
name|Time
operator|.
name|now
argument_list|()
operator|-
name|start
operator|<
literal|240000
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testBlockFix waiting for fixing job 2 to start"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"2 jobs not running"
argument_list|,
literal|2
argument_list|,
name|blockFixer
operator|.
name|jobsRunning
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|blockFixer
operator|.
name|filesFixed
argument_list|()
operator|<
literal|2
operator|&&
name|Time
operator|.
name|now
argument_list|()
operator|-
name|start
operator|<
literal|240000
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testBlockFix waiting for files to be fixed."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"files not fixed"
argument_list|,
literal|2
argument_list|,
name|blockFixer
operator|.
name|filesFixed
argument_list|()
argument_list|)
expr_stmt|;
name|dfs
operator|=
name|getDFS
argument_list|(
name|conf
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{       }
name|assertTrue
argument_list|(
literal|"file not fixed"
argument_list|,
name|TestRaidDfs
operator|.
name|validateFile
argument_list|(
name|dfs
argument_list|,
name|file1
argument_list|,
name|file1Len
argument_list|,
name|crc1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"file not fixed"
argument_list|,
name|TestRaidDfs
operator|.
name|validateFile
argument_list|(
name|dfs
argument_list|,
name|file2
argument_list|,
name|file2Len
argument_list|,
name|crc2
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testConcurrentJobs exception "
operator|+
name|e
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|myTearDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * tests that the distributed block fixer obeys    * the limit on how many files to fix simultaneously    */
annotation|@
name|Test
DECL|method|testMaxPendingFiles ()
specifier|public
name|void
name|testMaxPendingFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testMaxPendingFiles started."
argument_list|)
expr_stmt|;
name|long
name|blockSize
init|=
literal|8192L
decl_stmt|;
name|int
name|stripeLength
init|=
literal|3
decl_stmt|;
name|mySetup
argument_list|(
name|stripeLength
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// never har
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dhruba/raidtest/file1"
argument_list|)
decl_stmt|;
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dhruba/raidtest/file2"
argument_list|)
decl_stmt|;
name|Path
name|destPath
init|=
operator|new
name|Path
argument_list|(
literal|"/destraid/user/dhruba/raidtest"
argument_list|)
decl_stmt|;
name|long
name|crc1
init|=
name|TestRaidDfs
operator|.
name|createTestFilePartialLastBlock
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|long
name|crc2
init|=
name|TestRaidDfs
operator|.
name|createTestFilePartialLastBlock
argument_list|(
name|fileSys
argument_list|,
name|file2
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|long
name|file1Len
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file1
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|long
name|file2Len
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file2
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testMaxPendingFiles created test files"
argument_list|)
expr_stmt|;
comment|// create an instance of the RaidNode
name|Configuration
name|localConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|localConf
operator|.
name|set
argument_list|(
name|RaidNode
operator|.
name|RAID_LOCATION_KEY
argument_list|,
literal|"/destraid"
argument_list|)
expr_stmt|;
name|localConf
operator|.
name|setInt
argument_list|(
literal|"raid.blockfix.interval"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|localConf
operator|.
name|set
argument_list|(
literal|"raid.blockfix.classname"
argument_list|,
literal|"org.apache.hadoop.raid.DistBlockFixer"
argument_list|)
expr_stmt|;
name|localConf
operator|.
name|setLong
argument_list|(
literal|"raid.blockfix.filespertask"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|localConf
operator|.
name|setLong
argument_list|(
literal|"raid.blockfix.maxpendingfiles"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
try|try
block|{
name|cnode
operator|=
name|RaidNode
operator|.
name|createRaidNode
argument_list|(
literal|null
argument_list|,
name|localConf
argument_list|)
expr_stmt|;
name|TestRaidDfs
operator|.
name|waitForFileRaided
argument_list|(
name|LOG
argument_list|,
name|fileSys
argument_list|,
name|file1
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|TestRaidDfs
operator|.
name|waitForFileRaided
argument_list|(
name|LOG
argument_list|,
name|fileSys
argument_list|,
name|file2
argument_list|,
name|destPath
argument_list|)
expr_stmt|;
name|cnode
operator|.
name|stop
argument_list|()
expr_stmt|;
name|cnode
operator|.
name|join
argument_list|()
expr_stmt|;
name|FileStatus
name|file1Stat
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|FileStatus
name|file2Stat
init|=
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file2
argument_list|)
decl_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|fileSys
decl_stmt|;
name|LocatedBlocks
name|file1Loc
init|=
name|RaidDFSUtil
operator|.
name|getBlockLocations
argument_list|(
name|dfs
argument_list|,
name|file1
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|file1Stat
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
name|LocatedBlocks
name|file2Loc
init|=
name|RaidDFSUtil
operator|.
name|getBlockLocations
argument_list|(
name|dfs
argument_list|,
name|file2
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|file2Stat
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|corruptFiles
init|=
name|RaidDFSUtil
operator|.
name|getCorruptFiles
argument_list|(
name|dfs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"no corrupt files expected"
argument_list|,
literal|0
argument_list|,
name|corruptFiles
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"filesFixed() should return 0 before fixing files"
argument_list|,
literal|0
argument_list|,
name|cnode
operator|.
name|blockFixer
operator|.
name|filesFixed
argument_list|()
argument_list|)
expr_stmt|;
comment|// corrupt file1
name|int
index|[]
name|corruptBlockIdxs
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|,
literal|6
block|}
decl_stmt|;
for|for
control|(
name|int
name|idx
range|:
name|corruptBlockIdxs
control|)
name|corruptBlock
argument_list|(
name|file1Loc
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|reportCorruptBlocks
argument_list|(
name|dfs
argument_list|,
name|file1
argument_list|,
name|corruptBlockIdxs
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|corruptFiles
operator|=
name|RaidDFSUtil
operator|.
name|getCorruptFiles
argument_list|(
name|dfs
argument_list|)
expr_stmt|;
name|cnode
operator|=
name|RaidNode
operator|.
name|createRaidNode
argument_list|(
literal|null
argument_list|,
name|localConf
argument_list|)
expr_stmt|;
name|DistBlockFixer
name|blockFixer
init|=
operator|(
name|DistBlockFixer
operator|)
name|cnode
operator|.
name|blockFixer
decl_stmt|;
name|long
name|start
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
while|while
condition|(
name|blockFixer
operator|.
name|jobsRunning
argument_list|()
operator|<
literal|1
operator|&&
name|Time
operator|.
name|now
argument_list|()
operator|-
name|start
operator|<
literal|240000
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testBlockFix waiting for fixing job 1 to start"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"job not running"
argument_list|,
literal|1
argument_list|,
name|blockFixer
operator|.
name|jobsRunning
argument_list|()
argument_list|)
expr_stmt|;
comment|// corrupt file2
for|for
control|(
name|int
name|idx
range|:
name|corruptBlockIdxs
control|)
name|corruptBlock
argument_list|(
name|file2Loc
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|reportCorruptBlocks
argument_list|(
name|dfs
argument_list|,
name|file2
argument_list|,
name|corruptBlockIdxs
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|corruptFiles
operator|=
name|RaidDFSUtil
operator|.
name|getCorruptFiles
argument_list|(
name|dfs
argument_list|)
expr_stmt|;
comment|// wait until both files are fixed
while|while
condition|(
name|blockFixer
operator|.
name|filesFixed
argument_list|()
operator|<
literal|2
operator|&&
name|Time
operator|.
name|now
argument_list|()
operator|-
name|start
operator|<
literal|240000
condition|)
block|{
comment|// make sure the block fixer does not start a second job while
comment|// the first one is still running
name|assertTrue
argument_list|(
literal|"too many jobs running"
argument_list|,
name|blockFixer
operator|.
name|jobsRunning
argument_list|()
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"files not fixed"
argument_list|,
literal|2
argument_list|,
name|blockFixer
operator|.
name|filesFixed
argument_list|()
argument_list|)
expr_stmt|;
name|dfs
operator|=
name|getDFS
argument_list|(
name|conf
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{       }
name|assertTrue
argument_list|(
literal|"file not fixed"
argument_list|,
name|TestRaidDfs
operator|.
name|validateFile
argument_list|(
name|dfs
argument_list|,
name|file1
argument_list|,
name|file1Len
argument_list|,
name|crc1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"file not fixed"
argument_list|,
name|TestRaidDfs
operator|.
name|validateFile
argument_list|(
name|dfs
argument_list|,
name|file2
argument_list|,
name|file2Len
argument_list|,
name|crc2
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testMaxPendingFiles exception "
operator|+
name|e
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|myTearDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

