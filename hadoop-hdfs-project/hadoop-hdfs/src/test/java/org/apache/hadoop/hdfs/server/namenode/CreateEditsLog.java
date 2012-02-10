begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|fs
operator|.
name|permission
operator|.
name|PermissionStatus
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
name|Block
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
name|blockmanagement
operator|.
name|BlockInfo
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
name|common
operator|.
name|GenerationStamp
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
name|common
operator|.
name|Storage
import|;
end_import

begin_comment
comment|/**  *   * CreateEditsLog  *   Synopsis: CreateEditsLog -f numFiles StartingBlockId numBlocksPerFile  *        [-r replicafactor] [-d editsLogDirectory]  *             Default replication factor is 1  *             Default edits log directory is /tmp/EditsLogOut  *     *   Create a name node's edits log in /tmp/EditsLogOut.  *   The file /tmp/EditsLogOut/current/edits can be copied to a name node's  *   dfs.namenode.name.dir/current direcotry and the name node can be started as usual.  *     *   The files are created in /createdViaInjectingInEditsLog  *   The file names contain the starting and ending blockIds; hence once can   *   create multiple edits logs using this command using non overlapping   *   block ids and feed the files to a single name node.  *     *   See Also @link #DataNodeCluster for injecting a set of matching  *   blocks created with this command into a set of simulated data nodes.  *  */
end_comment

begin_class
DECL|class|CreateEditsLog
specifier|public
class|class
name|CreateEditsLog
block|{
DECL|field|BASE_PATH
specifier|static
specifier|final
name|String
name|BASE_PATH
init|=
literal|"/createdViaInjectingInEditsLog"
decl_stmt|;
DECL|field|EDITS_DIR
specifier|static
specifier|final
name|String
name|EDITS_DIR
init|=
literal|"/tmp/EditsLogOut"
decl_stmt|;
DECL|field|edits_dir
specifier|static
name|String
name|edits_dir
init|=
name|EDITS_DIR
decl_stmt|;
DECL|field|BLOCK_GENERATION_STAMP
specifier|static
specifier|final
specifier|public
name|long
name|BLOCK_GENERATION_STAMP
init|=
name|GenerationStamp
operator|.
name|FIRST_VALID_STAMP
decl_stmt|;
DECL|method|addFiles (FSEditLog editLog, int numFiles, short replication, int blocksPerFile, long startingBlockId, FileNameGenerator nameGenerator)
specifier|static
name|void
name|addFiles
parameter_list|(
name|FSEditLog
name|editLog
parameter_list|,
name|int
name|numFiles
parameter_list|,
name|short
name|replication
parameter_list|,
name|int
name|blocksPerFile
parameter_list|,
name|long
name|startingBlockId
parameter_list|,
name|FileNameGenerator
name|nameGenerator
parameter_list|)
block|{
name|PermissionStatus
name|p
init|=
operator|new
name|PermissionStatus
argument_list|(
literal|"joeDoe"
argument_list|,
literal|"people"
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
decl_stmt|;
name|INodeDirectory
name|dirInode
init|=
operator|new
name|INodeDirectory
argument_list|(
name|p
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|editLog
operator|.
name|logMkDir
argument_list|(
name|BASE_PATH
argument_list|,
name|dirInode
argument_list|)
expr_stmt|;
name|long
name|blockSize
init|=
literal|10
decl_stmt|;
name|BlockInfo
index|[]
name|blocks
init|=
operator|new
name|BlockInfo
index|[
name|blocksPerFile
index|]
decl_stmt|;
for|for
control|(
name|int
name|iB
init|=
literal|0
init|;
name|iB
operator|<
name|blocksPerFile
condition|;
operator|++
name|iB
control|)
block|{
name|blocks
index|[
name|iB
index|]
operator|=
operator|new
name|BlockInfo
argument_list|(
operator|new
name|Block
argument_list|(
literal|0
argument_list|,
name|blockSize
argument_list|,
name|BLOCK_GENERATION_STAMP
argument_list|)
argument_list|,
name|replication
argument_list|)
expr_stmt|;
block|}
name|long
name|currentBlockId
init|=
name|startingBlockId
decl_stmt|;
name|long
name|bidAtSync
init|=
name|startingBlockId
decl_stmt|;
for|for
control|(
name|int
name|iF
init|=
literal|0
init|;
name|iF
operator|<
name|numFiles
condition|;
name|iF
operator|++
control|)
block|{
for|for
control|(
name|int
name|iB
init|=
literal|0
init|;
name|iB
operator|<
name|blocksPerFile
condition|;
operator|++
name|iB
control|)
block|{
name|blocks
index|[
name|iB
index|]
operator|.
name|setBlockId
argument_list|(
name|currentBlockId
operator|++
argument_list|)
expr_stmt|;
block|}
name|INodeFileUnderConstruction
name|inode
init|=
operator|new
name|INodeFileUnderConstruction
argument_list|(
literal|null
argument_list|,
name|replication
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|,
name|blocks
argument_list|,
name|p
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Append path to filename with information about blockIDs
name|String
name|path
init|=
literal|"_"
operator|+
name|iF
operator|+
literal|"_B"
operator|+
name|blocks
index|[
literal|0
index|]
operator|.
name|getBlockId
argument_list|()
operator|+
literal|"_to_B"
operator|+
name|blocks
index|[
name|blocksPerFile
operator|-
literal|1
index|]
operator|.
name|getBlockId
argument_list|()
operator|+
literal|"_"
decl_stmt|;
name|String
name|filePath
init|=
name|nameGenerator
operator|.
name|getNextFileName
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|filePath
operator|=
name|filePath
operator|+
name|path
expr_stmt|;
comment|// Log the new sub directory in edits
if|if
condition|(
operator|(
name|iF
operator|%
name|nameGenerator
operator|.
name|getFilesPerDirectory
argument_list|()
operator|)
operator|==
literal|0
condition|)
block|{
name|String
name|currentDir
init|=
name|nameGenerator
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|dirInode
operator|=
operator|new
name|INodeDirectory
argument_list|(
name|p
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|logMkDir
argument_list|(
name|currentDir
argument_list|,
name|dirInode
argument_list|)
expr_stmt|;
block|}
name|editLog
operator|.
name|logOpenFile
argument_list|(
name|filePath
argument_list|,
operator|new
name|INodeFileUnderConstruction
argument_list|(
name|p
argument_list|,
name|replication
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|logCloseFile
argument_list|(
name|filePath
argument_list|,
name|inode
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentBlockId
operator|-
name|bidAtSync
operator|>=
literal|2000
condition|)
block|{
comment|// sync every 2K blocks
name|editLog
operator|.
name|logSync
argument_list|()
expr_stmt|;
name|bidAtSync
operator|=
name|currentBlockId
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Created edits log in directory "
operator|+
name|edits_dir
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" containing "
operator|+
name|numFiles
operator|+
literal|" File-Creates, each file with "
operator|+
name|blocksPerFile
operator|+
literal|" blocks"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" blocks range: "
operator|+
name|startingBlockId
operator|+
literal|" to "
operator|+
operator|(
name|currentBlockId
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|field|usage
specifier|static
name|String
name|usage
init|=
literal|"Usage: createditlogs "
operator|+
literal|" -f  numFiles startingBlockIds NumBlocksPerFile  [-r replicafactor] "
operator|+
literal|"[-d editsLogDirectory]\n"
operator|+
literal|"      Default replication factor is 1\n"
operator|+
literal|"      Default edits log direcory is "
operator|+
name|EDITS_DIR
operator|+
literal|"\n"
decl_stmt|;
DECL|method|printUsageExit ()
specifier|static
name|void
name|printUsageExit
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|printUsageExit (String err)
specifier|static
name|void
name|printUsageExit
parameter_list|(
name|String
name|err
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|printUsageExit
argument_list|()
expr_stmt|;
block|}
comment|/**    * @param args    * @throws IOException     */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|startingBlockId
init|=
literal|1
decl_stmt|;
name|int
name|numFiles
init|=
literal|0
decl_stmt|;
name|short
name|replication
init|=
literal|1
decl_stmt|;
name|int
name|numBlocksPerFile
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|printUsageExit
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// parse command line
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-h"
argument_list|)
condition|)
name|printUsageExit
argument_list|()
expr_stmt|;
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-f"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|+
literal|3
operator|>=
name|args
operator|.
name|length
operator|||
name|args
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
operator|||
name|args
index|[
name|i
operator|+
literal|2
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
operator|||
name|args
index|[
name|i
operator|+
literal|3
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Missing num files, starting block and/or number of blocks"
argument_list|)
expr_stmt|;
block|}
name|numFiles
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
name|startingBlockId
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
name|numBlocksPerFile
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|numFiles
operator|<=
literal|0
operator|||
name|numBlocksPerFile
operator|<=
literal|0
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"numFiles and numBlocksPerFile most be greater than 0"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-r"
argument_list|)
operator|||
name|args
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|>=
name|args
operator|.
name|length
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Missing num files, starting block and/or number of blocks"
argument_list|)
expr_stmt|;
block|}
name|replication
operator|=
name|Short
operator|.
name|parseShort
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-d"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|>=
name|args
operator|.
name|length
operator|||
name|args
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Missing edits logs directory"
argument_list|)
expr_stmt|;
block|}
name|edits_dir
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|printUsageExit
argument_list|()
expr_stmt|;
block|}
block|}
name|File
name|editsLogDir
init|=
operator|new
name|File
argument_list|(
name|edits_dir
argument_list|)
decl_stmt|;
name|File
name|subStructureDir
init|=
operator|new
name|File
argument_list|(
name|edits_dir
operator|+
literal|"/"
operator|+
name|Storage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|editsLogDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|editsLogDir
operator|.
name|mkdir
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"cannot create "
operator|+
name|edits_dir
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|subStructureDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|subStructureDir
operator|.
name|mkdir
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"cannot create subdirs of "
operator|+
name|edits_dir
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|FileNameGenerator
name|nameGenerator
init|=
operator|new
name|FileNameGenerator
argument_list|(
name|BASE_PATH
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|FSEditLog
name|editLog
init|=
name|FSImageTestUtil
operator|.
name|createStandaloneEditLog
argument_list|(
name|editsLogDir
argument_list|)
decl_stmt|;
name|editLog
operator|.
name|openForWrite
argument_list|()
expr_stmt|;
name|addFiles
argument_list|(
name|editLog
argument_list|,
name|numFiles
argument_list|,
name|replication
argument_list|,
name|numBlocksPerFile
argument_list|,
name|startingBlockId
argument_list|,
name|nameGenerator
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|logSync
argument_list|()
expr_stmt|;
name|editLog
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

