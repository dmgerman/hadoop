begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStreamReader
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|lang3
operator|.
name|exception
operator|.
name|ExceptionUtils
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
name|Configured
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
name|FSDataInputStream
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|protocol
operator|.
name|HdfsConstants
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
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|DataNodeTestUtils
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
name|SimulatedFSDataset
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Tool
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
name|ToolRunner
import|;
end_import

begin_comment
comment|/**  * Starts up a number of DataNodes within the same JVM. These DataNodes all use  * {@link org.apache.hadoop.hdfs.server.datanode.SimulatedFSDataset}, so they do  * not store any actual data, and do not persist anything to disk; they maintain  * all metadata in memory. This is useful for testing and simulation purposes.  *<p>  * The DataNodes will attempt to connect to a NameNode defined by the default  * FileSystem. There will be one DataNode started for each block list file  * passed as an argument. Each of these files should contain a list of blocks  * that the corresponding DataNode should contain, as specified by a triplet of  * block ID, block size, and generation stamp. Each line of the file is one  * block, in the format:  *<p>  * {@code blockID,blockGenStamp,blockSize}  *<p>  * This class is loosely based off of  * {@link org.apache.hadoop.hdfs.DataNodeCluster}.  */
end_comment

begin_class
DECL|class|SimulatedDataNodes
specifier|public
class|class
name|SimulatedDataNodes
extends|extends
name|Configured
implements|implements
name|Tool
block|{
comment|// Set this arbitrarily large (100TB) since we don't care about storage
comment|// capacity
DECL|field|STORAGE_CAPACITY
specifier|private
specifier|static
specifier|final
name|long
name|STORAGE_CAPACITY
init|=
literal|100
operator|*
literal|2L
operator|<<
literal|40
decl_stmt|;
DECL|field|USAGE
specifier|private
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"Usage: "
operator|+
literal|"org.apache.hadoop.tools.dynamometer.SimulatedDataNodes "
operator|+
literal|"bpid blockListFile1 [ blockListFileN ... ]\n"
operator|+
literal|"   bpid should be the ID of the block pool to which these DataNodes "
operator|+
literal|"belong.\n"
operator|+
literal|"   Each blockListFile specified should contain a list of blocks to "
operator|+
literal|"be served by one DataNode.\n"
operator|+
literal|"   See the Javadoc of this class for more detail."
decl_stmt|;
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|USAGE
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|err
argument_list|)
throw|;
block|}
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
name|Exception
block|{
name|SimulatedDataNodes
name|datanodes
init|=
operator|new
name|SimulatedDataNodes
argument_list|()
decl_stmt|;
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|,
name|datanodes
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Not enough arguments"
argument_list|)
expr_stmt|;
block|}
name|String
name|bpid
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|blockListFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
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
name|blockListFiles
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|URI
name|defaultFS
init|=
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
operator|.
name|equals
argument_list|(
name|defaultFS
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Must specify an HDFS-based default FS! Got<"
operator|+
name|defaultFS
operator|+
literal|">"
argument_list|)
expr_stmt|;
block|}
name|String
name|nameNodeAdr
init|=
name|defaultFS
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
if|if
condition|(
name|nameNodeAdr
operator|==
literal|null
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"No NameNode address and port in config"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DataNodes will connect to NameNode at "
operator|+
name|nameNodeAdr
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|MiniDFSCluster
operator|.
name|PROP_TEST_BUILD_DATA
argument_list|,
name|DataNode
operator|.
name|getStorageLocations
argument_list|(
name|getConf
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|getConf
argument_list|()
operator|.
name|setLong
argument_list|(
name|SimulatedFSDataset
operator|.
name|CONFIG_PROPERTY_CAPACITY
argument_list|,
name|STORAGE_CAPACITY
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|mc
init|=
operator|new
name|MiniDFSCluster
argument_list|()
decl_stmt|;
try|try
block|{
name|mc
operator|.
name|formatDataNodeDirs
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error formatting DataNode dirs: "
operator|+
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error formatting DataNode dirs"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found "
operator|+
name|blockListFiles
operator|.
name|size
argument_list|()
operator|+
literal|" block listing files; launching DataNodes accordingly."
argument_list|)
expr_stmt|;
name|mc
operator|.
name|startDataNodes
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|blockListFiles
operator|.
name|size
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|StartupOption
operator|.
name|REGULAR
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for DataNodes to connect to NameNode and "
operator|+
literal|"init storage directories."
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|DataNode
argument_list|>
name|datanodesWithoutFSDataset
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|mc
operator|.
name|getDataNodes
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|datanodesWithoutFSDataset
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|datanodesWithoutFSDataset
operator|.
name|removeIf
argument_list|(
parameter_list|(
name|dn
parameter_list|)
lambda|->
name|DataNodeTestUtils
operator|.
name|getFSDataset
argument_list|(
name|dn
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waited "
operator|+
operator|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
operator|)
operator|+
literal|" ms for DataNode FSDatasets to be ready"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|dnIndex
init|=
literal|0
init|;
name|dnIndex
operator|<
name|blockListFiles
operator|.
name|size
argument_list|()
condition|;
name|dnIndex
operator|++
control|)
block|{
name|Path
name|blockListFile
init|=
name|blockListFiles
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataInputStream
name|fsdis
init|=
name|blockListFile
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
operator|.
name|open
argument_list|(
name|blockListFile
argument_list|)
init|;
name|BufferedReader
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fsdis
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
init|)
block|{
name|List
argument_list|<
name|Block
argument_list|>
name|blockList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
init|;
name|line
operator|!=
literal|null
condition|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
control|)
block|{
comment|// Format of the listing files is blockID,blockGenStamp,blockSize
name|String
index|[]
name|blockInfo
init|=
name|line
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|blockList
operator|.
name|add
argument_list|(
operator|new
name|Block
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|blockInfo
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|blockInfo
index|[
literal|2
index|]
argument_list|)
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|blockInfo
index|[
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
block|}
try|try
block|{
name|mc
operator|.
name|injectBlocks
argument_list|(
name|dnIndex
argument_list|,
name|blockList
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Error injecting blocks into DataNode %d for "
operator|+
literal|"block pool %s: %s%n"
argument_list|,
name|dnIndex
argument_list|,
name|bpid
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|ioe
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Injected %d blocks into DataNode %d for block pool %s%n"
argument_list|,
name|cnt
argument_list|,
name|dnIndex
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error creating DataNodes: "
operator|+
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

