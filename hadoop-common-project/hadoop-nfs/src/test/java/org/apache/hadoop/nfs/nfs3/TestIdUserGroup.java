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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
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
name|nfs
operator|.
name|nfs3
operator|.
name|IdUserGroup
operator|.
name|PassThroughMap
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
name|nfs
operator|.
name|nfs3
operator|.
name|IdUserGroup
operator|.
name|StaticMapping
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
DECL|field|EMPTY_PASS_THROUGH_MAP
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|EMPTY_PASS_THROUGH_MAP
init|=
operator|new
name|PassThroughMap
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testStaticMapParsing ()
specifier|public
name|void
name|testStaticMapParsing
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tempStaticMapFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"nfs-"
argument_list|,
literal|".map"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|staticMapFileContents
init|=
literal|"uid 10 100\n"
operator|+
literal|"gid 10 200\n"
operator|+
literal|"uid 11 201 # comment at the end of a line\n"
operator|+
literal|"uid 12 301\n"
operator|+
literal|"# Comment at the beginning of a line\n"
operator|+
literal|"    # Comment that starts late in the line\n"
operator|+
literal|"uid 10000 10001# line without whitespace before comment\n"
operator|+
literal|"uid 13 302\n"
operator|+
literal|"gid\t11\t201\n"
operator|+
comment|// Tabs instead of spaces.
literal|"\n"
operator|+
comment|// Entirely empty line.
literal|"gid 12 202"
decl_stmt|;
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tempStaticMapFile
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|staticMapFileContents
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|StaticMapping
name|parsedMap
init|=
name|IdUserGroup
operator|.
name|parseStaticMap
argument_list|(
name|tempStaticMapFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|uidMapping
operator|.
name|get
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|uidMapping
operator|.
name|get
argument_list|(
literal|201
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|uidMapping
operator|.
name|get
argument_list|(
literal|301
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|13
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|uidMapping
operator|.
name|get
argument_list|(
literal|302
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|gidMapping
operator|.
name|get
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|gidMapping
operator|.
name|get
argument_list|(
literal|201
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|gidMapping
operator|.
name|get
argument_list|(
literal|202
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10000
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|uidMapping
operator|.
name|get
argument_list|(
literal|10001
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure pass-through of unmapped IDs works.
name|assertEquals
argument_list|(
literal|1000
argument_list|,
operator|(
name|int
operator|)
name|parsedMap
operator|.
name|uidMapping
operator|.
name|get
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStaticMapping ()
specifier|public
name|void
name|testStaticMapping
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|uidStaticMap
init|=
operator|new
name|PassThroughMap
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|gidStaticMap
init|=
operator|new
name|PassThroughMap
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|uidStaticMap
operator|.
name|put
argument_list|(
literal|11501
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|gidStaticMap
operator|.
name|put
argument_list|(
literal|497
argument_list|,
literal|200
argument_list|)
expr_stmt|;
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
name|String
name|GET_ALL_USERS_CMD
init|=
literal|"echo \"atm:x:1000:1000:Aaron T. Myers,,,:/home/atm:/bin/bash\n"
operator|+
literal|"hdfs:x:11501:10787:Grid Distributed File System:/home/hdfs:/bin/bash\""
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
literal|"mapred2:x:498\""
operator|+
literal|" | cut -d: -f1,3"
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
argument_list|,
name|uidStaticMap
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
argument_list|,
name|gidStaticMap
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|int
operator|)
name|uMap
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
literal|"hdfs"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"atm"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
operator|(
name|int
operator|)
name|uMap
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
literal|"atm"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|11501
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11501
argument_list|,
operator|(
name|int
operator|)
name|gMap
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
literal|"hdfs"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapred"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
operator|(
name|int
operator|)
name|gMap
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapred"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapred2"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|498
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|498
argument_list|,
operator|(
name|int
operator|)
name|gMap
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapred2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
argument_list|,
name|EMPTY_PASS_THROUGH_MAP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|uMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|11501
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs2"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|11502
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bin"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"daemon"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|1
argument_list|)
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
argument_list|,
name|EMPTY_PASS_THROUGH_MAP
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
literal|"hdfs"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|11501
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapred"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|497
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapred3"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|498
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIdOutOfIntegerRange ()
specifier|public
name|void
name|testIdOutOfIntegerRange
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|GET_ALL_USERS_CMD
init|=
literal|"echo \""
operator|+
literal|"nfsnobody:x:4294967294:4294967294:Anonymous NFS User:/var/lib/nfs:/sbin/nologin\n"
operator|+
literal|"nfsnobody1:x:4294967295:4294967295:Anonymous NFS User:/var/lib/nfs1:/sbin/nologin\n"
operator|+
literal|"maxint:x:2147483647:2147483647:Grid Distributed File System:/home/maxint:/bin/bash\n"
operator|+
literal|"minint:x:2147483648:2147483648:Grid Distributed File System:/home/minint:/bin/bash\n"
operator|+
literal|"archivebackup:*:1031:4294967294:Archive Backup:/home/users/archivebackup:/bin/sh\n"
operator|+
literal|"hdfs:x:11501:10787:Grid Distributed File System:/home/hdfs:/bin/bash\n"
operator|+
literal|"daemon:x:2:2:daemon:/sbin:/sbin/nologin\""
operator|+
literal|" | cut -d: -f1,3"
decl_stmt|;
name|String
name|GET_ALL_GROUPS_CMD
init|=
literal|"echo \""
operator|+
literal|"hdfs:*:11501:hrt_hdfs\n"
operator|+
literal|"rpcuser:*:29:\n"
operator|+
literal|"nfsnobody:*:4294967294:\n"
operator|+
literal|"nfsnobody1:*:4294967295:\n"
operator|+
literal|"maxint:*:2147483647:\n"
operator|+
literal|"minint:*:2147483648:\n"
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
argument_list|,
name|EMPTY_PASS_THROUGH_MAP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|uMap
operator|.
name|size
argument_list|()
operator|==
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nfsnobody"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nfsnobody1"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"maxint"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|2147483647
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"minint"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
operator|-
literal|2147483648
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"archivebackup"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|1031
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|11501
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"daemon"
argument_list|,
name|uMap
operator|.
name|get
argument_list|(
literal|2
argument_list|)
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
argument_list|,
name|EMPTY_PASS_THROUGH_MAP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gMap
operator|.
name|size
argument_list|()
operator|==
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|11501
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"rpcuser"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|29
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nfsnobody"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nfsnobody1"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"maxint"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|2147483647
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"minint"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
operator|-
literal|2147483648
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapred3"
argument_list|,
name|gMap
operator|.
name|get
argument_list|(
literal|498
argument_list|)
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

