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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|DFSTestUtil
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
name|Assert
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

begin_comment
comment|/**  * Test {@link FSDirectory}, the in-memory namespace tree.  */
end_comment

begin_class
DECL|class|TestFSDirectory
specifier|public
class|class
name|TestFSDirectory
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
name|TestFSDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|sub1
specifier|private
specifier|final
name|Path
name|sub1
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
DECL|field|file1
specifier|private
specifier|final
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
DECL|field|file2
specifier|private
specifier|final
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"file2"
argument_list|)
decl_stmt|;
DECL|field|sub11
specifier|private
specifier|final
name|Path
name|sub11
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"sub11"
argument_list|)
decl_stmt|;
DECL|field|file3
specifier|private
specifier|final
name|Path
name|file3
init|=
operator|new
name|Path
argument_list|(
name|sub11
argument_list|,
literal|"file3"
argument_list|)
decl_stmt|;
DECL|field|file4
specifier|private
specifier|final
name|Path
name|file4
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"z_file4"
argument_list|)
decl_stmt|;
DECL|field|file5
specifier|private
specifier|final
name|Path
name|file5
init|=
operator|new
name|Path
argument_list|(
name|sub1
argument_list|,
literal|"z_file5"
argument_list|)
decl_stmt|;
DECL|field|sub2
specifier|private
specifier|final
name|Path
name|sub2
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"sub2"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fsn
specifier|private
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|fsdir
specifier|private
name|FSDirectory
name|fsdir
decl_stmt|;
DECL|field|hdfs
specifier|private
name|DistributedFileSystem
name|hdfs
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
name|REPLICATION
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|fsdir
operator|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file1
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file2
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file3
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file5
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
name|sub2
argument_list|)
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
block|}
block|}
comment|/** Dump the tree, make some changes, and then dump the tree again. */
annotation|@
name|Test
DECL|method|testDumpTree ()
specifier|public
name|void
name|testDumpTree
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|INode
name|root
init|=
name|fsdir
operator|.
name|getINode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Original tree"
argument_list|)
expr_stmt|;
specifier|final
name|StringBuffer
name|b1
init|=
name|root
operator|.
name|dumpTreeRecursively
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"b1="
operator|+
name|b1
argument_list|)
expr_stmt|;
specifier|final
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|b1
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|checkClassName
argument_list|(
name|line
argument_list|)
expr_stmt|;
for|for
control|(
init|;
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|line
operator|.
name|startsWith
argument_list|(
name|INodeDirectory
operator|.
name|DUMPTREE_LAST_ITEM
argument_list|)
operator|||
name|line
operator|.
name|startsWith
argument_list|(
name|INodeDirectory
operator|.
name|DUMPTREE_EXCEPT_LAST_ITEM
argument_list|)
argument_list|)
expr_stmt|;
name|checkClassName
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Create a new file "
operator|+
name|file4
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|file4
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|seed
argument_list|)
expr_stmt|;
specifier|final
name|StringBuffer
name|b2
init|=
name|root
operator|.
name|dumpTreeRecursively
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"b2="
operator|+
name|b2
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|j
init|=
name|b1
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
for|for
control|(
init|;
name|b1
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
name|b2
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|;
name|i
operator|++
control|)
empty_stmt|;
name|int
name|k
init|=
name|b2
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
for|for
control|(
init|;
name|b1
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
operator|==
name|b2
operator|.
name|charAt
argument_list|(
name|k
argument_list|)
condition|;
name|j
operator|--
operator|,
name|k
operator|--
control|)
empty_stmt|;
specifier|final
name|String
name|diff
init|=
name|b2
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|k
operator|+
literal|1
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"i="
operator|+
name|i
operator|+
literal|", j="
operator|+
name|j
operator|+
literal|", k="
operator|+
name|k
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"diff="
operator|+
name|diff
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|i
operator|>
name|j
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|diff
operator|.
name|contains
argument_list|(
name|file4
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReset ()
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|fsdir
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fsdir
operator|.
name|isReady
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|INodeDirectory
name|root
init|=
operator|(
name|INodeDirectory
operator|)
name|fsdir
operator|.
name|getINode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|root
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
name|fsdir
operator|.
name|imageLoadComplete
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|fsdir
operator|.
name|isReady
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkClassName (String line)
specifier|static
name|void
name|checkClassName
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|int
name|i
init|=
name|line
operator|.
name|lastIndexOf
argument_list|(
literal|'('
argument_list|)
decl_stmt|;
name|int
name|j
init|=
name|line
operator|.
name|lastIndexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
specifier|final
name|String
name|classname
init|=
name|line
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|j
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|classname
operator|.
name|equals
argument_list|(
name|INodeFile
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|||
name|classname
operator|.
name|equals
argument_list|(
name|INodeDirectory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|||
name|classname
operator|.
name|equals
argument_list|(
name|INodeDirectoryWithQuota
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

