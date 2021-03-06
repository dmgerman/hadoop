begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|InOrder
import|;
end_import

begin_class
DECL|class|TestContentSummary
specifier|public
class|class
name|TestContentSummary
block|{
comment|// check the empty constructor correctly initialises the object
annotation|@
name|Test
DECL|method|testConstructorEmpty ()
specifier|public
name|void
name|testConstructorEmpty
parameter_list|()
block|{
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getLength"
argument_list|,
literal|0
argument_list|,
name|contentSummary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getFileCount"
argument_list|,
literal|0
argument_list|,
name|contentSummary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getDirectoryCount"
argument_list|,
literal|0
argument_list|,
name|contentSummary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getQuota"
argument_list|,
operator|-
literal|1
argument_list|,
name|contentSummary
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getSpaceConsumed"
argument_list|,
literal|0
argument_list|,
name|contentSummary
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getSpaceQuota"
argument_list|,
operator|-
literal|1
argument_list|,
name|contentSummary
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check the full constructor with quota information
annotation|@
name|Test
DECL|method|testConstructorWithQuota ()
specifier|public
name|void
name|testConstructorWithQuota
parameter_list|()
block|{
name|long
name|length
init|=
literal|11111
decl_stmt|;
name|long
name|fileCount
init|=
literal|22222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|long
name|quota
init|=
literal|44444
decl_stmt|;
name|long
name|spaceConsumed
init|=
literal|55555
decl_stmt|;
name|long
name|spaceQuota
init|=
literal|66666
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getLength"
argument_list|,
name|length
argument_list|,
name|contentSummary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getFileCount"
argument_list|,
name|fileCount
argument_list|,
name|contentSummary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getDirectoryCount"
argument_list|,
name|directoryCount
argument_list|,
name|contentSummary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getQuota"
argument_list|,
name|quota
argument_list|,
name|contentSummary
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getSpaceConsumed"
argument_list|,
name|spaceConsumed
argument_list|,
name|contentSummary
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getSpaceQuota"
argument_list|,
name|spaceQuota
argument_list|,
name|contentSummary
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check the constructor with quota information
annotation|@
name|Test
DECL|method|testConstructorNoQuota ()
specifier|public
name|void
name|testConstructorNoQuota
parameter_list|()
block|{
name|long
name|length
init|=
literal|11111
decl_stmt|;
name|long
name|fileCount
init|=
literal|22222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|length
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getLength"
argument_list|,
name|length
argument_list|,
name|contentSummary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getFileCount"
argument_list|,
name|fileCount
argument_list|,
name|contentSummary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getDirectoryCount"
argument_list|,
name|directoryCount
argument_list|,
name|contentSummary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getQuota"
argument_list|,
operator|-
literal|1
argument_list|,
name|contentSummary
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getSpaceConsumed"
argument_list|,
name|length
argument_list|,
name|contentSummary
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getSpaceQuota"
argument_list|,
operator|-
literal|1
argument_list|,
name|contentSummary
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check the write method
annotation|@
name|Test
DECL|method|testWrite ()
specifier|public
name|void
name|testWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|length
init|=
literal|11111
decl_stmt|;
name|long
name|fileCount
init|=
literal|22222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|long
name|quota
init|=
literal|44444
decl_stmt|;
name|long
name|spaceConsumed
init|=
literal|55555
decl_stmt|;
name|long
name|spaceQuota
init|=
literal|66666
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DataOutput
name|out
init|=
name|mock
argument_list|(
name|DataOutput
operator|.
name|class
argument_list|)
decl_stmt|;
name|InOrder
name|inOrder
init|=
name|inOrder
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|contentSummary
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|inOrder
operator|.
name|verify
argument_list|(
name|out
argument_list|)
operator|.
name|writeLong
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|inOrder
operator|.
name|verify
argument_list|(
name|out
argument_list|)
operator|.
name|writeLong
argument_list|(
name|fileCount
argument_list|)
expr_stmt|;
name|inOrder
operator|.
name|verify
argument_list|(
name|out
argument_list|)
operator|.
name|writeLong
argument_list|(
name|directoryCount
argument_list|)
expr_stmt|;
name|inOrder
operator|.
name|verify
argument_list|(
name|out
argument_list|)
operator|.
name|writeLong
argument_list|(
name|quota
argument_list|)
expr_stmt|;
name|inOrder
operator|.
name|verify
argument_list|(
name|out
argument_list|)
operator|.
name|writeLong
argument_list|(
name|spaceConsumed
argument_list|)
expr_stmt|;
name|inOrder
operator|.
name|verify
argument_list|(
name|out
argument_list|)
operator|.
name|writeLong
argument_list|(
name|spaceQuota
argument_list|)
expr_stmt|;
block|}
comment|// check the readFields method
annotation|@
name|Test
DECL|method|testReadFields ()
specifier|public
name|void
name|testReadFields
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|length
init|=
literal|11111
decl_stmt|;
name|long
name|fileCount
init|=
literal|22222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|long
name|quota
init|=
literal|44444
decl_stmt|;
name|long
name|spaceConsumed
init|=
literal|55555
decl_stmt|;
name|long
name|spaceQuota
init|=
literal|66666
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|DataInput
name|in
init|=
name|mock
argument_list|(
name|DataInput
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|length
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fileCount
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|quota
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|spaceQuota
argument_list|)
expr_stmt|;
name|contentSummary
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getLength"
argument_list|,
name|length
argument_list|,
name|contentSummary
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getFileCount"
argument_list|,
name|fileCount
argument_list|,
name|contentSummary
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getDirectoryCount"
argument_list|,
name|directoryCount
argument_list|,
name|contentSummary
operator|.
name|getDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getQuota"
argument_list|,
name|quota
argument_list|,
name|contentSummary
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getSpaceConsumed"
argument_list|,
name|spaceConsumed
argument_list|,
name|contentSummary
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getSpaceQuota"
argument_list|,
name|spaceQuota
argument_list|,
name|contentSummary
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check the header with quotas
annotation|@
name|Test
DECL|method|testGetHeaderWithQuota ()
specifier|public
name|void
name|testGetHeaderWithQuota
parameter_list|()
block|{
name|String
name|header
init|=
literal|"       QUOTA       REM_QUOTA     SPACE_QUOTA "
operator|+
literal|"REM_SPACE_QUOTA    DIR_COUNT   FILE_COUNT       CONTENT_SIZE "
decl_stmt|;
name|assertEquals
argument_list|(
name|header
argument_list|,
name|ContentSummary
operator|.
name|getHeader
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check the header without quotas
annotation|@
name|Test
DECL|method|testGetHeaderNoQuota ()
specifier|public
name|void
name|testGetHeaderNoQuota
parameter_list|()
block|{
name|String
name|header
init|=
literal|"   DIR_COUNT   FILE_COUNT       CONTENT_SIZE "
decl_stmt|;
name|assertEquals
argument_list|(
name|header
argument_list|,
name|ContentSummary
operator|.
name|getHeader
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check the toString method with quotas
annotation|@
name|Test
DECL|method|testToStringWithQuota ()
specifier|public
name|void
name|testToStringWithQuota
parameter_list|()
block|{
name|long
name|length
init|=
literal|11111
decl_stmt|;
name|long
name|fileCount
init|=
literal|22222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|long
name|quota
init|=
literal|44444
decl_stmt|;
name|long
name|spaceConsumed
init|=
literal|55555
decl_stmt|;
name|long
name|spaceQuota
init|=
literal|66665
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|"       44444          -11111           66665           11110"
operator|+
literal|"        33333        22222              11111 "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|contentSummary
operator|.
name|toString
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check the toString method with quotas
annotation|@
name|Test
DECL|method|testToStringNoQuota ()
specifier|public
name|void
name|testToStringNoQuota
parameter_list|()
block|{
name|long
name|length
init|=
literal|11111
decl_stmt|;
name|long
name|fileCount
init|=
literal|22222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|"        none             inf            none"
operator|+
literal|"             inf        33333        22222              11111 "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|contentSummary
operator|.
name|toString
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check the toString method with quotas
annotation|@
name|Test
DECL|method|testToStringNoShowQuota ()
specifier|public
name|void
name|testToStringNoShowQuota
parameter_list|()
block|{
name|long
name|length
init|=
literal|11111
decl_stmt|;
name|long
name|fileCount
init|=
literal|22222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|long
name|quota
init|=
literal|44444
decl_stmt|;
name|long
name|spaceConsumed
init|=
literal|55555
decl_stmt|;
name|long
name|spaceQuota
init|=
literal|66665
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|"       33333        22222              11111 "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|contentSummary
operator|.
name|toString
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check the toString method (defaults to with quotas)
annotation|@
name|Test
DECL|method|testToString ()
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|long
name|length
init|=
literal|11111
decl_stmt|;
name|long
name|fileCount
init|=
literal|22222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|long
name|quota
init|=
literal|44444
decl_stmt|;
name|long
name|spaceConsumed
init|=
literal|55555
decl_stmt|;
name|long
name|spaceQuota
init|=
literal|66665
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|"       44444          -11111           66665"
operator|+
literal|"           11110        33333        22222              11111 "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|contentSummary
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check the toString method with quotas
annotation|@
name|Test
DECL|method|testToStringHumanWithQuota ()
specifier|public
name|void
name|testToStringHumanWithQuota
parameter_list|()
block|{
name|long
name|length
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|fileCount
init|=
literal|222222222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|long
name|quota
init|=
literal|222256578
decl_stmt|;
name|long
name|spaceConsumed
init|=
literal|1073741825
decl_stmt|;
name|long
name|spaceQuota
init|=
literal|1
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|"     212.0 M            1023               1 "
operator|+
literal|"           -1 G       32.6 K      211.9 M              8.0 E "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|contentSummary
operator|.
name|toString
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check the toString method with quotas
annotation|@
name|Test
DECL|method|testToStringHumanNoShowQuota ()
specifier|public
name|void
name|testToStringHumanNoShowQuota
parameter_list|()
block|{
name|long
name|length
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|fileCount
init|=
literal|222222222
decl_stmt|;
name|long
name|directoryCount
init|=
literal|33333
decl_stmt|;
name|long
name|quota
init|=
literal|222256578
decl_stmt|;
name|long
name|spaceConsumed
init|=
literal|55555
decl_stmt|;
name|long
name|spaceQuota
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|ContentSummary
name|contentSummary
init|=
operator|new
name|ContentSummary
operator|.
name|Builder
argument_list|()
operator|.
name|length
argument_list|(
name|length
argument_list|)
operator|.
name|fileCount
argument_list|(
name|fileCount
argument_list|)
operator|.
name|directoryCount
argument_list|(
name|directoryCount
argument_list|)
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|"      32.6 K      211.9 M              8.0 E "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|contentSummary
operator|.
name|toString
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

