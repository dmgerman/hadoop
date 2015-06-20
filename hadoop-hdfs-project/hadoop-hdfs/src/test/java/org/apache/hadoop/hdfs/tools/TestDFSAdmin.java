begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|conf
operator|.
name|ReconfigurationUtil
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
name|server
operator|.
name|common
operator|.
name|Storage
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
name|StorageLocation
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
name|ByteArrayOutputStream
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
name|PrintStream
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
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
name|allOf
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
name|anyOf
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
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
name|assertThat
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|mock
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
name|when
import|;
end_import

begin_class
DECL|class|TestDFSAdmin
specifier|public
class|class
name|TestDFSAdmin
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|admin
specifier|private
name|DFSAdmin
name|admin
decl_stmt|;
DECL|field|datanode
specifier|private
name|DataNode
name|datanode
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|restartCluster
argument_list|()
expr_stmt|;
name|admin
operator|=
operator|new
name|DFSAdmin
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|restartCluster ()
specifier|private
name|void
name|restartCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
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
name|datanode
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getReconfigureStatus (String nodeType, String address)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getReconfigureStatus
parameter_list|(
name|String
name|nodeType
parameter_list|,
name|String
name|address
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|bufOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|bufOut
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bufErr
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|err
init|=
operator|new
name|PrintStream
argument_list|(
name|bufErr
argument_list|)
decl_stmt|;
name|admin
operator|.
name|getReconfigurationStatus
argument_list|(
name|nodeType
argument_list|,
name|address
argument_list|,
name|out
argument_list|,
name|err
argument_list|)
expr_stmt|;
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
name|bufOut
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|scanner
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|outputs
operator|.
name|add
argument_list|(
name|scanner
operator|.
name|nextLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|outputs
return|;
block|}
comment|/**    * Test reconfiguration and check the status outputs.    * @param expectedSuccuss set true if the reconfiguration task should success.    * @throws IOException    * @throws InterruptedException    */
DECL|method|testGetReconfigurationStatus (boolean expectedSuccuss)
specifier|private
name|void
name|testGetReconfigurationStatus
parameter_list|(
name|boolean
name|expectedSuccuss
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ReconfigurationUtil
name|ru
init|=
name|mock
argument_list|(
name|ReconfigurationUtil
operator|.
name|class
argument_list|)
decl_stmt|;
name|datanode
operator|.
name|setReconfigurationUtil
argument_list|(
name|ru
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ReconfigurationUtil
operator|.
name|PropertyChange
argument_list|>
name|changes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|File
name|newDir
init|=
operator|new
name|File
argument_list|(
name|cluster
operator|.
name|getDataDirectory
argument_list|()
argument_list|,
literal|"data_new"
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedSuccuss
condition|)
block|{
name|newDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Inject failure.
name|newDir
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
name|changes
operator|.
name|add
argument_list|(
operator|new
name|ReconfigurationUtil
operator|.
name|PropertyChange
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|newDir
operator|.
name|toString
argument_list|()
argument_list|,
name|datanode
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|changes
operator|.
name|add
argument_list|(
operator|new
name|ReconfigurationUtil
operator|.
name|PropertyChange
argument_list|(
literal|"randomKey"
argument_list|,
literal|"new123"
argument_list|,
literal|"old456"
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ru
operator|.
name|parseChangedProperties
argument_list|(
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|changes
argument_list|)
expr_stmt|;
specifier|final
name|int
name|port
init|=
name|datanode
operator|.
name|getIpcPort
argument_list|()
decl_stmt|;
specifier|final
name|String
name|address
init|=
literal|"localhost:"
operator|+
name|port
decl_stmt|;
name|assertThat
argument_list|(
name|admin
operator|.
name|startReconfiguration
argument_list|(
literal|"datanode"
argument_list|,
name|address
argument_list|)
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|100
decl_stmt|;
while|while
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|outputs
operator|=
name|getReconfigureStatus
argument_list|(
literal|"datanode"
argument_list|,
name|address
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|outputs
operator|.
name|isEmpty
argument_list|()
operator|&&
name|outputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
literal|"finished"
argument_list|)
condition|)
block|{
break|break;
block|}
name|count
operator|--
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|count
operator|>
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedSuccuss
condition|)
block|{
name|assertThat
argument_list|(
name|outputs
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|outputs
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
name|DataNode
operator|.
name|getStorageLocations
argument_list|(
name|datanode
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedSuccuss
condition|)
block|{
name|assertThat
argument_list|(
name|locations
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
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
name|getFile
argument_list|()
argument_list|,
name|is
argument_list|(
name|newDir
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify the directory is appropriately formatted.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|newDir
argument_list|,
name|Storage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|locations
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|offset
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|expectedSuccuss
condition|)
block|{
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
name|offset
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"SUCCESS: Changed property "
operator|+
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
name|offset
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"FAILED: Change property "
operator|+
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
name|offset
operator|+
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"From:"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"data1"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"data2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
name|offset
operator|+
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
name|not
argument_list|(
name|anyOf
argument_list|(
name|containsString
argument_list|(
literal|"data1"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"data2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|outputs
operator|.
name|get
argument_list|(
name|offset
operator|+
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"To"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"data_new"
argument_list|)
argument_list|)
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
DECL|method|testGetReconfigurationStatus ()
specifier|public
name|void
name|testGetReconfigurationStatus
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|testGetReconfigurationStatus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|restartCluster
argument_list|()
expr_stmt|;
name|testGetReconfigurationStatus
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getReconfigurationAllowedProperties ( String nodeType, String address)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getReconfigurationAllowedProperties
parameter_list|(
name|String
name|nodeType
parameter_list|,
name|String
name|address
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|bufOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|bufOut
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bufErr
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|err
init|=
operator|new
name|PrintStream
argument_list|(
name|bufErr
argument_list|)
decl_stmt|;
name|admin
operator|.
name|getReconfigurableProperties
argument_list|(
name|nodeType
argument_list|,
name|address
argument_list|,
name|out
argument_list|,
name|err
argument_list|)
expr_stmt|;
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
name|bufOut
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|scanner
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|outputs
operator|.
name|add
argument_list|(
name|scanner
operator|.
name|nextLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|outputs
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testGetReconfigAllowedProperties ()
specifier|public
name|void
name|testGetReconfigAllowedProperties
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|port
init|=
name|datanode
operator|.
name|getIpcPort
argument_list|()
decl_stmt|;
specifier|final
name|String
name|address
init|=
literal|"localhost:"
operator|+
name|port
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|outputs
init|=
name|getReconfigurationAllowedProperties
argument_list|(
literal|"datanode"
argument_list|,
name|address
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|outputs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|outputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

