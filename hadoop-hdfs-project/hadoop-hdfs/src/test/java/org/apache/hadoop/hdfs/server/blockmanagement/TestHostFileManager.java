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
name|protocol
operator|.
name|DatanodeID
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
name|test
operator|.
name|Whitebox
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
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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

begin_class
DECL|class|TestHostFileManager
specifier|public
class|class
name|TestHostFileManager
block|{
DECL|method|entry (String e)
specifier|private
specifier|static
name|InetSocketAddress
name|entry
parameter_list|(
name|String
name|e
parameter_list|)
block|{
return|return
name|HostFileManager
operator|.
name|parseEntry
argument_list|(
literal|"dummy"
argument_list|,
literal|"dummy"
argument_list|,
name|e
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testDeduplication ()
specifier|public
name|void
name|testDeduplication
parameter_list|()
block|{
name|HostSet
name|s
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
comment|// These entries will be de-duped, since they refer to the same IP
comment|// address + port combo.
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12345"
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"localhost:12345"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12345"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// The following entries should not be de-duped.
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12346"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.10"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|s
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRelation ()
specifier|public
name|void
name|testRelation
parameter_list|()
block|{
name|HostSet
name|s
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:123"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2:123"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|match
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|matchedBy
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2:123"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testIncludeExcludeLists ()
specifier|public
name|void
name|testIncludeExcludeLists
parameter_list|()
throws|throws
name|IOException
block|{
name|BlockManager
name|bm
init|=
name|mock
argument_list|(
name|BlockManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSNamesystem
name|fsn
init|=
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|HostFileManager
name|hm
init|=
operator|new
name|HostFileManager
argument_list|()
decl_stmt|;
name|HostSet
name|includedNodes
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
name|HostSet
name|excludedNodes
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
name|includedNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12345"
argument_list|)
argument_list|)
expr_stmt|;
name|includedNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"localhost:12345"
argument_list|)
argument_list|)
expr_stmt|;
name|includedNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12345"
argument_list|)
argument_list|)
expr_stmt|;
name|includedNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.2"
argument_list|)
argument_list|)
expr_stmt|;
name|excludedNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12346"
argument_list|)
argument_list|)
expr_stmt|;
name|excludedNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.30.1:12346"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|includedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|excludedNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|hm
operator|.
name|refresh
argument_list|(
name|includedNodes
argument_list|,
name|excludedNodes
argument_list|)
expr_stmt|;
name|DatanodeManager
name|dm
init|=
operator|new
name|DatanodeManager
argument_list|(
name|bm
argument_list|,
name|fsn
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|dm
argument_list|,
literal|"hostConfigManager"
argument_list|,
name|hm
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
argument_list|>
name|dnMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|dm
argument_list|,
literal|"datanodeMap"
argument_list|)
decl_stmt|;
comment|// After the de-duplication, there should be only one DN from the included
comment|// nodes declared as dead.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dnMap
operator|.
name|put
argument_list|(
literal|"uuid-foo"
argument_list|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"localhost"
argument_list|,
literal|"uuid-foo"
argument_list|,
literal|12345
argument_list|,
literal|1020
argument_list|,
literal|1021
argument_list|,
literal|1022
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dnMap
operator|.
name|put
argument_list|(
literal|"uuid-bar"
argument_list|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.0.2"
argument_list|,
literal|"127.0.0.2"
argument_list|,
literal|"uuid-bar"
argument_list|,
literal|12345
argument_list|,
literal|1020
argument_list|,
literal|1021
argument_list|,
literal|1022
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DatanodeDescriptor
name|spam
init|=
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.0"
operator|+
literal|".3"
argument_list|,
literal|"127.0.0.3"
argument_list|,
literal|"uuid-spam"
argument_list|,
literal|12345
argument_list|,
literal|1020
argument_list|,
literal|1021
argument_list|,
literal|1022
argument_list|)
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|setDatanodeDead
argument_list|(
name|spam
argument_list|)
expr_stmt|;
name|includedNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.3:12345"
argument_list|)
argument_list|)
expr_stmt|;
name|dnMap
operator|.
name|put
argument_list|(
literal|"uuid-spam"
argument_list|,
name|spam
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|dnMap
operator|.
name|remove
argument_list|(
literal|"uuid-spam"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|excludedNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.3"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

