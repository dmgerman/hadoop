begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
operator|.
name|nfs3
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
name|junit
operator|.
name|Test
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
name|BiMap
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
name|HashBiMap
import|;
end_import

begin_class
DECL|class|TestIdUserGroup
specifier|public
class|class
name|TestIdUserGroup
block|{
annotation|@
name|Test
DECL|method|testDuplicates ()
specifier|public
name|void
name|testDuplicates
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|GET_ALL_USERS_CMD
init|=
literal|"echo \"root:x:0:0:root:/root:/bin/bash\n"
operator|+
literal|"hdfs:x:11501:10787:Grid Distributed File System:/home/hdfs:/bin/bash\n"
operator|+
literal|"hdfs:x:11502:10788:Grid Distributed File System:/home/hdfs:/bin/bash\n"
operator|+
literal|"hdfs1:x:11501:10787:Grid Distributed File System:/home/hdfs:/bin/bash\n"
operator|+
literal|"hdfs2:x:11502:10787:Grid Distributed File System:/home/hdfs:/bin/bash\n"
operator|+
literal|"bin:x:2:2:bin:/bin:/bin/sh\n"
operator|+
literal|"bin:x:1:1:bin:/bin:/sbin/nologin\n"
operator|+
literal|"daemon:x:1:1:daemon:/usr/sbin:/bin/sh\n"
operator|+
literal|"daemon:x:2:2:daemon:/sbin:/sbin/nologin\""
operator|+
literal|" | cut -d: -f1,3"
decl_stmt|;
name|String
name|GET_ALL_GROUPS_CMD
init|=
literal|"echo \"hdfs:*:11501:hrt_hdfs\n"
operator|+
literal|"mapred:x:497\n"
operator|+
literal|"mapred2:x:497\n"
operator|+
literal|"mapred:x:498\n"
operator|+
literal|"mapred3:x:498\""
operator|+
literal|" | cut -d: -f1,3"
decl_stmt|;
comment|// Maps for id to name map
name|BiMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|uMap
init|=
name|HashBiMap
operator|.
name|create
argument_list|()
decl_stmt|;
name|BiMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|gMap
init|=
name|HashBiMap
operator|.
name|create
argument_list|()
decl_stmt|;
name|IdUserGroup
operator|.
name|updateMapInternal
argument_list|(
name|uMap
argument_list|,
literal|"user"
argument_list|,
name|GET_ALL_USERS_CMD
argument_list|,
literal|":"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|uMap
operator|.
name|size
argument_list|()
operator|==
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uMap
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"root"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uMap
operator|.
name|get
argument_list|(
literal|11501
argument_list|)
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uMap
operator|.
name|get
argument_list|(
literal|11502
argument_list|)
argument_list|,
literal|"hdfs2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uMap
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|"bin"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uMap
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"daemon"
argument_list|)
expr_stmt|;
name|IdUserGroup
operator|.
name|updateMapInternal
argument_list|(
name|gMap
argument_list|,
literal|"group"
argument_list|,
name|GET_ALL_GROUPS_CMD
argument_list|,
literal|":"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gMap
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|gMap
operator|.
name|get
argument_list|(
literal|11501
argument_list|)
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|gMap
operator|.
name|get
argument_list|(
literal|497
argument_list|)
argument_list|,
literal|"mapred"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|gMap
operator|.
name|get
argument_list|(
literal|498
argument_list|)
argument_list|,
literal|"mapred3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUserUpdateSetting ()
specifier|public
name|void
name|testUserUpdateSetting
parameter_list|()
throws|throws
name|IOException
block|{
name|IdUserGroup
name|iug
init|=
operator|new
name|IdUserGroup
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|iug
operator|.
name|getTimeout
argument_list|()
argument_list|,
name|IdUserGroup
operator|.
name|TIMEOUT_DEFAULT
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|IdUserGroup
operator|.
name|NFS_USERUPDATE_MILLY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|iug
operator|=
operator|new
name|IdUserGroup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|iug
operator|.
name|getTimeout
argument_list|()
argument_list|,
name|IdUserGroup
operator|.
name|TIMEOUT_MIN
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|IdUserGroup
operator|.
name|NFS_USERUPDATE_MILLY
argument_list|,
name|IdUserGroup
operator|.
name|TIMEOUT_DEFAULT
operator|*
literal|2
argument_list|)
expr_stmt|;
name|iug
operator|=
operator|new
name|IdUserGroup
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|iug
operator|.
name|getTimeout
argument_list|()
argument_list|,
name|IdUserGroup
operator|.
name|TIMEOUT_DEFAULT
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

