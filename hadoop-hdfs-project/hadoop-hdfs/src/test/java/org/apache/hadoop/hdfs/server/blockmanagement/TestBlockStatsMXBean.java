begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|fail
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Map
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
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_comment
comment|/**  * Class for testing {@link BlockStatsMXBean} implementation  */
end_comment

begin_class
DECL|class|TestBlockStatsMXBean
specifier|public
class|class
name|TestBlockStatsMXBean
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
name|StorageType
index|[]
index|[]
name|types
init|=
operator|new
name|StorageType
index|[
literal|6
index|]
index|[]
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|3
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|types
index|[
name|i
index|]
operator|=
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
expr_stmt|;
block|}
name|types
index|[
literal|5
index|]
operator|=
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
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
literal|6
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|types
argument_list|)
operator|.
name|storagesPerDatanode
argument_list|(
literal|3
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
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
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
block|}
annotation|@
name|Test
DECL|method|testStorageTypeStats ()
specifier|public
name|void
name|testStorageTypeStats
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|StorageType
argument_list|,
name|StorageTypeStats
argument_list|>
name|storageTypeStatsMap
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getStorageTypeStats
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|storageTypeStatsMap
operator|.
name|containsKey
argument_list|(
name|StorageType
operator|.
name|RAM_DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|storageTypeStatsMap
operator|.
name|containsKey
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|storageTypeStatsMap
operator|.
name|containsKey
argument_list|(
name|StorageType
operator|.
name|ARCHIVE
argument_list|)
argument_list|)
expr_stmt|;
name|StorageTypeStats
name|storageTypeStats
init|=
name|storageTypeStatsMap
operator|.
name|get
argument_list|(
name|StorageType
operator|.
name|RAM_DISK
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|storageTypeStats
operator|.
name|getNodesInService
argument_list|()
argument_list|)
expr_stmt|;
name|storageTypeStats
operator|=
name|storageTypeStatsMap
operator|.
name|get
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|storageTypeStats
operator|.
name|getNodesInService
argument_list|()
argument_list|)
expr_stmt|;
name|storageTypeStats
operator|=
name|storageTypeStatsMap
operator|.
name|get
argument_list|(
name|StorageType
operator|.
name|ARCHIVE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|storageTypeStats
operator|.
name|getNodesInService
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readOutput (URL url)
specifier|protected
specifier|static
name|String
name|readOutput
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
name|url
operator|.
name|openConnection
argument_list|()
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
name|int
name|len
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testStorageTypeStatsJMX ()
specifier|public
name|void
name|testStorageTypeStatsJMX
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|baseUrl
init|=
operator|new
name|URL
argument_list|(
name|cluster
operator|.
name|getHttpUri
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|result
init|=
name|readOutput
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/jmx"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stat
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|Object
index|[]
name|beans
init|=
operator|(
name|Object
index|[]
operator|)
name|stat
operator|.
name|get
argument_list|(
literal|"beans"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|blockStats
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Object
name|bean
range|:
name|beans
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|bean
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"Hadoop:service=NameNode,name=BlockStats"
argument_list|)
condition|)
block|{
name|blockStats
operator|=
name|map
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
name|blockStats
argument_list|)
expr_stmt|;
name|Object
index|[]
name|storageTypeStatsList
init|=
operator|(
name|Object
index|[]
operator|)
name|blockStats
operator|.
name|get
argument_list|(
literal|"StorageTypeStats"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|storageTypeStatsList
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|storageTypeStatsList
operator|.
name|length
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|typesPresent
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|storageTypeStatsList
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|obj
decl_stmt|;
name|String
name|storageType
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|storageTypeStats
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|typesPresent
operator|.
name|add
argument_list|(
name|storageType
argument_list|)
expr_stmt|;
if|if
condition|(
name|storageType
operator|.
name|equals
argument_list|(
literal|"ARCHIVE"
argument_list|)
operator|||
name|storageType
operator|.
name|equals
argument_list|(
literal|"DISK"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|3l
argument_list|,
name|storageTypeStats
operator|.
name|get
argument_list|(
literal|"nodesInService"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|storageType
operator|.
name|equals
argument_list|(
literal|"RAM_DISK"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|6l
argument_list|,
name|storageTypeStats
operator|.
name|get
argument_list|(
literal|"nodesInService"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|typesPresent
operator|.
name|contains
argument_list|(
literal|"ARCHIVE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|typesPresent
operator|.
name|contains
argument_list|(
literal|"DISK"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|typesPresent
operator|.
name|contains
argument_list|(
literal|"RAM_DISK"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

