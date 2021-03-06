begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
package|;
end_package

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
name|FileNotFoundException
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
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
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
name|ImmutableMap
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
name|FileContext
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
name|permission
operator|.
name|FsPermission
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JobHistoryUtils
operator|.
name|getConfiguredHistoryIntermediateUserDoneDirPermissions
import|;
end_import

begin_class
DECL|class|TestJobHistoryUtils
specifier|public
class|class
name|TestJobHistoryUtils
block|{
DECL|field|TEST_DIR
specifier|final
specifier|static
name|String
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testGetHistoryDirsForCleaning ()
specifier|public
name|void
name|testGetHistoryDirsForCleaning
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|pRoot
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
argument_list|,
literal|"org.apache.hadoop.mapreduce.v2.jobhistory."
operator|+
literal|"TestJobHistoryUtils.testGetHistoryDirsForCleaning"
argument_list|)
decl_stmt|;
name|FileContext
name|fc
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|()
decl_stmt|;
name|Calendar
name|cCal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|int
name|year
init|=
literal|2013
decl_stmt|;
name|int
name|month
init|=
literal|7
decl_stmt|;
name|int
name|day
init|=
literal|21
decl_stmt|;
name|cCal
operator|.
name|set
argument_list|(
name|year
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|long
name|cutoff
init|=
name|cCal
operator|.
name|getTimeInMillis
argument_list|()
decl_stmt|;
name|clearDir
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|)
expr_stmt|;
name|Path
name|pId00
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
argument_list|,
name|day
argument_list|,
literal|"000000"
argument_list|)
decl_stmt|;
name|Path
name|pId01
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000001"
argument_list|)
decl_stmt|;
name|Path
name|pId02
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000002"
argument_list|)
decl_stmt|;
name|Path
name|pId03
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
argument_list|,
literal|"000003"
argument_list|)
decl_stmt|;
name|Path
name|pId04
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000004"
argument_list|)
decl_stmt|;
name|Path
name|pId05
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000005"
argument_list|)
decl_stmt|;
name|Path
name|pId06
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
argument_list|,
literal|"000006"
argument_list|)
decl_stmt|;
name|Path
name|pId07
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000007"
argument_list|)
decl_stmt|;
name|Path
name|pId08
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000008"
argument_list|)
decl_stmt|;
name|Path
name|pId09
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
argument_list|,
name|day
argument_list|,
literal|"000009"
argument_list|)
decl_stmt|;
name|Path
name|pId10
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000010"
argument_list|)
decl_stmt|;
name|Path
name|pId11
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000011"
argument_list|)
decl_stmt|;
name|Path
name|pId12
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
argument_list|,
literal|"000012"
argument_list|)
decl_stmt|;
name|Path
name|pId13
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000013"
argument_list|)
decl_stmt|;
name|Path
name|pId14
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000014"
argument_list|)
decl_stmt|;
name|Path
name|pId15
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
argument_list|,
literal|"000015"
argument_list|)
decl_stmt|;
name|Path
name|pId16
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000016"
argument_list|)
decl_stmt|;
name|Path
name|pId17
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|+
literal|1
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000017"
argument_list|)
decl_stmt|;
name|Path
name|pId18
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
argument_list|,
name|day
argument_list|,
literal|"000018"
argument_list|)
decl_stmt|;
name|Path
name|pId19
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000019"
argument_list|)
decl_stmt|;
name|Path
name|pId20
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000020"
argument_list|)
decl_stmt|;
name|Path
name|pId21
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
argument_list|,
literal|"000021"
argument_list|)
decl_stmt|;
name|Path
name|pId22
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000022"
argument_list|)
decl_stmt|;
name|Path
name|pId23
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
operator|+
literal|1
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000023"
argument_list|)
decl_stmt|;
name|Path
name|pId24
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
argument_list|,
literal|"000024"
argument_list|)
decl_stmt|;
name|Path
name|pId25
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
operator|+
literal|1
argument_list|,
literal|"000025"
argument_list|)
decl_stmt|;
name|Path
name|pId26
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|year
operator|-
literal|1
argument_list|,
name|month
operator|-
literal|1
argument_list|,
name|day
operator|-
literal|1
argument_list|,
literal|"000026"
argument_list|)
decl_stmt|;
comment|// non-expected names should be ignored without problems
name|Path
name|pId27
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
literal|"foo"
argument_list|,
literal|""
operator|+
name|month
argument_list|,
literal|""
operator|+
name|day
argument_list|,
literal|"000027"
argument_list|)
decl_stmt|;
name|Path
name|pId28
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
literal|""
operator|+
name|year
argument_list|,
literal|"foo"
argument_list|,
literal|""
operator|+
name|day
argument_list|,
literal|"000028"
argument_list|)
decl_stmt|;
name|Path
name|pId29
init|=
name|createPath
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
literal|""
operator|+
name|year
argument_list|,
literal|""
operator|+
name|month
argument_list|,
literal|"foo"
argument_list|,
literal|"000029"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FileStatus
argument_list|>
name|dirs
init|=
name|JobHistoryUtils
operator|.
name|getHistoryDirsForCleaning
argument_list|(
name|fc
argument_list|,
name|pRoot
argument_list|,
name|cutoff
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|14
argument_list|,
name|dirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId26
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId24
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId25
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId20
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId18
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|4
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId19
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId23
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId21
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId22
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|8
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId08
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId06
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|10
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId07
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|11
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId02
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|12
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pId00
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
literal|13
argument_list|)
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|clearDir (FileContext fc, Path p)
specifier|private
name|void
name|clearDir
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|fc
operator|.
name|delete
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
name|fc
operator|.
name|mkdir
argument_list|(
name|p
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|createPath (FileContext fc, Path root, int year, int month, int day, String id)
specifier|private
name|Path
name|createPath
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|root
parameter_list|,
name|int
name|year
parameter_list|,
name|int
name|month
parameter_list|,
name|int
name|day
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
name|year
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|month
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|day
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|id
argument_list|)
decl_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|path
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
DECL|method|createPath (FileContext fc, Path root, String year, String month, String day, String id)
specifier|private
name|Path
name|createPath
parameter_list|(
name|FileContext
name|fc
parameter_list|,
name|Path
name|root
parameter_list|,
name|String
name|year
parameter_list|,
name|String
name|month
parameter_list|,
name|String
name|day
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
name|year
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|month
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|day
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|id
argument_list|)
decl_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|path
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
annotation|@
name|Test
DECL|method|testGetConfiguredHistoryIntermediateUserDoneDirPermissions ()
specifier|public
name|void
name|testGetConfiguredHistoryIntermediateUserDoneDirPermissions
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FsPermission
argument_list|>
name|parameters
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"775"
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|0775
argument_list|)
argument_list|,
literal|"123"
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|0773
argument_list|)
argument_list|,
literal|"-rwx"
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|0770
argument_list|)
argument_list|,
literal|"+rwx"
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|0777
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FsPermission
argument_list|>
name|entry
range|:
name|parameters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_INTERMEDIATE_USER_DONE_DIR_PERMISSIONS
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|getConfiguredHistoryIntermediateUserDoneDirPermissions
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

