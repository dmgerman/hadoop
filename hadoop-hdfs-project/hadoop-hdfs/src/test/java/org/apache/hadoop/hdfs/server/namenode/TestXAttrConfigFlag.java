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
name|NameNode
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
name|NameNodeAdapter
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
name|IOUtils
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_comment
comment|/**  * Tests that the configuration flag that controls support for XAttrs is off  * and causes all attempted operations related to XAttrs to fail.  The  * NameNode can still load XAttrs from fsimage or edits.  */
end_comment

begin_class
DECL|class|TestXAttrConfigFlag
specifier|public
class|class
name|TestXAttrConfigFlag
block|{
DECL|field|PATH
specifier|private
specifier|static
specifier|final
name|Path
name|PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/path"
argument_list|)
decl_stmt|;
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
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|fs
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
DECL|method|testSetXAttr ()
specifier|public
name|void
name|testSetXAttr
parameter_list|()
throws|throws
name|Exception
block|{
name|initCluster
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|PATH
argument_list|)
expr_stmt|;
name|expectException
argument_list|()
expr_stmt|;
name|fs
operator|.
name|setXAttr
argument_list|(
name|PATH
argument_list|,
literal|"user.foo"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetXAttrs ()
specifier|public
name|void
name|testGetXAttrs
parameter_list|()
throws|throws
name|Exception
block|{
name|initCluster
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|PATH
argument_list|)
expr_stmt|;
name|expectException
argument_list|()
expr_stmt|;
name|fs
operator|.
name|getXAttrs
argument_list|(
name|PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveXAttr ()
specifier|public
name|void
name|testRemoveXAttr
parameter_list|()
throws|throws
name|Exception
block|{
name|initCluster
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|PATH
argument_list|)
expr_stmt|;
name|expectException
argument_list|()
expr_stmt|;
name|fs
operator|.
name|removeXAttr
argument_list|(
name|PATH
argument_list|,
literal|"user.foo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEditLog ()
specifier|public
name|void
name|testEditLog
parameter_list|()
throws|throws
name|Exception
block|{
comment|// With XAttrs enabled, set an XAttr.
name|initCluster
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|PATH
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setXAttr
argument_list|(
name|PATH
argument_list|,
literal|"user.foo"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Restart with XAttrs disabled.  Expect successful restart.
name|restart
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFsImage ()
specifier|public
name|void
name|testFsImage
parameter_list|()
throws|throws
name|Exception
block|{
comment|// With XAttrs enabled, set an XAttr.
name|initCluster
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|PATH
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setXAttr
argument_list|(
name|PATH
argument_list|,
literal|"user.foo"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Save a new checkpoint and restart with XAttrs still enabled.
name|restart
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Restart with XAttrs disabled.  Expect successful restart.
name|restart
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * We expect an IOException, and we want the exception text to state the    * configuration key that controls XAttr support.    */
DECL|method|expectException ()
specifier|private
name|void
name|expectException
parameter_list|()
block|{
name|exception
operator|.
name|expect
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_XATTRS_ENABLED_KEY
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize the cluster, wait for it to become active, and get FileSystem.    *    * @param format if true, format the NameNode and DataNodes before starting up    * @param xattrsEnabled if true, XAttr support is enabled    * @throws Exception if any step fails    */
DECL|method|initCluster (boolean format, boolean xattrsEnabled)
specifier|private
name|void
name|initCluster
parameter_list|(
name|boolean
name|format
parameter_list|,
name|boolean
name|xattrsEnabled
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// not explicitly setting to false, should be false by default
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_XATTRS_ENABLED_KEY
argument_list|,
name|xattrsEnabled
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
name|format
argument_list|(
name|format
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
block|}
comment|/**    * Restart the cluster, optionally saving a new checkpoint.    *    * @param checkpoint boolean true to save a new checkpoint    * @param xattrsEnabled if true, XAttr support is enabled    * @throws Exception if restart fails    */
DECL|method|restart (boolean checkpoint, boolean xattrsEnabled)
specifier|private
name|void
name|restart
parameter_list|(
name|boolean
name|checkpoint
parameter_list|,
name|boolean
name|xattrsEnabled
parameter_list|)
throws|throws
name|Exception
block|{
name|NameNode
name|nameNode
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|checkpoint
condition|)
block|{
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|nameNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|saveNamespace
argument_list|(
name|nameNode
argument_list|)
expr_stmt|;
block|}
name|shutdown
argument_list|()
expr_stmt|;
name|initCluster
argument_list|(
literal|false
argument_list|,
name|xattrsEnabled
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

