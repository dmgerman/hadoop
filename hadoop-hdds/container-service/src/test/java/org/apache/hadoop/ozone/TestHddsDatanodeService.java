begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED
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
name|assertFalse
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
name|FileUtil
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|test
operator|.
name|GenericTestUtils
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
name|ServicePlugin
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

begin_comment
comment|/**  * Test class for {@link HddsDatanodeService}.  */
end_comment

begin_class
DECL|class|TestHddsDatanodeService
specifier|public
class|class
name|TestHddsDatanodeService
block|{
DECL|field|testDir
specifier|private
name|File
name|testDir
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|service
specifier|private
name|HddsDatanodeService
name|service
decl_stmt|;
DECL|field|args
specifier|private
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{}
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getRandomizedTestDir
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OZONE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|OzoneConfigKeys
operator|.
name|HDDS_DATANODE_PLUGINS_KEY
argument_list|,
name|MockService
operator|.
name|class
argument_list|,
name|ServicePlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|volumeDir
init|=
name|testDir
operator|+
literal|"/disk1"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|volumeDir
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
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStartup ()
specifier|public
name|void
name|testStartup
parameter_list|()
throws|throws
name|IOException
block|{
name|service
operator|=
name|HddsDatanodeService
operator|.
name|createHddsDatanodeService
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|service
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|service
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|service
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|isDaemonStopped
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|join
argument_list|()
expr_stmt|;
name|service
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|MockService
specifier|static
class|class
name|MockService
implements|implements
name|ServicePlugin
block|{
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|start (Object arg0)
specifier|public
name|void
name|start
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// Do nothing
block|}
block|}
block|}
end_class

end_unit

