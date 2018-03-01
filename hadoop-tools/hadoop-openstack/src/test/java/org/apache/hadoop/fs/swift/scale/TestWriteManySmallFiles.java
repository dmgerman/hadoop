begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.scale
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|scale
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|FileStatus
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
name|fs
operator|.
name|swift
operator|.
name|util
operator|.
name|Duration
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
name|swift
operator|.
name|util
operator|.
name|DurationStats
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
name|swift
operator|.
name|util
operator|.
name|SwiftTestUtils
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

begin_class
DECL|class|TestWriteManySmallFiles
specifier|public
class|class
name|TestWriteManySmallFiles
extends|extends
name|SwiftScaleTestBase
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestWriteManySmallFiles
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_BULK_IO_TEST_TIMEOUT
argument_list|)
DECL|method|testScaledWriteThenRead ()
specifier|public
name|void
name|testScaledWriteThenRead
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/test/manysmallfiles"
argument_list|)
decl_stmt|;
name|Duration
name|rm1
init|=
operator|new
name|Duration
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|finished
argument_list|()
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|Duration
name|ls1
init|=
operator|new
name|Duration
argument_list|()
decl_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|ls1
operator|.
name|finished
argument_list|()
expr_stmt|;
name|long
name|count
init|=
name|getOperationCount
argument_list|()
decl_stmt|;
name|SwiftTestUtils
operator|.
name|noteAction
argument_list|(
literal|"Beginning Write of "
operator|+
name|count
operator|+
literal|" files "
argument_list|)
expr_stmt|;
name|DurationStats
name|writeStats
init|=
operator|new
name|DurationStats
argument_list|(
literal|"write"
argument_list|)
decl_stmt|;
name|DurationStats
name|readStats
init|=
operator|new
name|DurationStats
argument_list|(
literal|"read"
argument_list|)
decl_stmt|;
name|String
name|format
init|=
literal|"%08d"
decl_stmt|;
for|for
control|(
name|long
name|l
init|=
literal|0
init|;
name|l
operator|<
name|count
condition|;
name|l
operator|++
control|)
block|{
name|String
name|name
init|=
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|l
argument_list|)
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"part-"
operator|+
name|name
argument_list|)
decl_stmt|;
name|Duration
name|d
init|=
operator|new
name|Duration
argument_list|()
decl_stmt|;
name|SwiftTestUtils
operator|.
name|writeTextFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|d
operator|.
name|finished
argument_list|()
expr_stmt|;
name|writeStats
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|//at this point, the directory is full.
name|SwiftTestUtils
operator|.
name|noteAction
argument_list|(
literal|"Beginning ls"
argument_list|)
expr_stmt|;
name|Duration
name|ls2
init|=
operator|new
name|Duration
argument_list|()
decl_stmt|;
name|FileStatus
index|[]
name|status2
init|=
operator|(
name|FileStatus
index|[]
operator|)
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ls2
operator|.
name|finished
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Not enough entries in the directory"
argument_list|,
name|count
argument_list|,
name|status2
operator|.
name|length
argument_list|)
expr_stmt|;
name|SwiftTestUtils
operator|.
name|noteAction
argument_list|(
literal|"Beginning read"
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|l
init|=
literal|0
init|;
name|l
operator|<
name|count
condition|;
name|l
operator|++
control|)
block|{
name|String
name|name
init|=
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|l
argument_list|)
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"part-"
operator|+
name|name
argument_list|)
decl_stmt|;
name|Duration
name|d
init|=
operator|new
name|Duration
argument_list|()
decl_stmt|;
name|String
name|result
init|=
name|SwiftTestUtils
operator|.
name|readBytesToString
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|d
operator|.
name|finished
argument_list|()
expr_stmt|;
name|readStats
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
comment|//do a recursive delete
name|SwiftTestUtils
operator|.
name|noteAction
argument_list|(
literal|"Beginning delete"
argument_list|)
expr_stmt|;
name|Duration
name|rm2
init|=
operator|new
name|Duration
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rm2
operator|.
name|finished
argument_list|()
expr_stmt|;
comment|//print the stats
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"'filesystem','%s'"
argument_list|,
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|writeStats
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|readStats
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"'rm1',%d,'ls1',%d"
argument_list|,
name|rm1
operator|.
name|value
argument_list|()
argument_list|,
name|ls1
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"'rm2',%d,'ls2',%d"
argument_list|,
name|rm2
operator|.
name|value
argument_list|()
argument_list|,
name|ls2
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

