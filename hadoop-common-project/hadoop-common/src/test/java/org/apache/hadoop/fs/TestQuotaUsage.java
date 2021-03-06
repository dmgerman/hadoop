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
name|assertEquals
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
DECL|class|TestQuotaUsage
specifier|public
class|class
name|TestQuotaUsage
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
name|QuotaUsage
name|quotaUsage
init|=
operator|new
name|QuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getQuota"
argument_list|,
operator|-
literal|1
argument_list|,
name|quotaUsage
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
name|quotaUsage
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
name|quotaUsage
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
name|fileAndDirCount
init|=
literal|22222
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
name|QuotaUsage
name|quotaUsage
init|=
operator|new
name|QuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
name|fileAndDirCount
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
literal|"getFileAndDirectoryCount"
argument_list|,
name|fileAndDirCount
argument_list|,
name|quotaUsage
operator|.
name|getFileAndDirectoryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"getQuota"
argument_list|,
name|quota
argument_list|,
name|quotaUsage
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
name|quotaUsage
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
name|quotaUsage
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
name|spaceConsumed
init|=
literal|11111
decl_stmt|;
name|long
name|fileAndDirCount
init|=
literal|22222
decl_stmt|;
name|QuotaUsage
name|quotaUsage
init|=
operator|new
name|QuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
name|fileAndDirCount
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getFileAndDirectoryCount"
argument_list|,
name|fileAndDirCount
argument_list|,
name|quotaUsage
operator|.
name|getFileAndDirectoryCount
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
name|quotaUsage
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
name|quotaUsage
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
name|quotaUsage
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check the header
annotation|@
name|Test
DECL|method|testGetHeader ()
specifier|public
name|void
name|testGetHeader
parameter_list|()
block|{
name|String
name|header
init|=
literal|"       QUOTA       REM_QUOTA     SPACE_QUOTA "
operator|+
literal|"REM_SPACE_QUOTA "
decl_stmt|;
name|assertEquals
argument_list|(
name|header
argument_list|,
name|QuotaUsage
operator|.
name|getHeader
argument_list|()
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
name|fileAndDirCount
init|=
literal|55555
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
name|QuotaUsage
name|quotaUsage
init|=
operator|new
name|QuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
name|fileAndDirCount
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
literal|"           11110 "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|quotaUsage
operator|.
name|toString
argument_list|()
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
name|QuotaUsage
name|quotaUsage
init|=
operator|new
name|QuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
literal|1234
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
literal|"             inf "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|quotaUsage
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
name|fileAndDirCount
init|=
literal|222255555
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
name|QuotaUsage
name|quotaUsage
init|=
operator|new
name|QuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
name|fileAndDirCount
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
literal|"           -1 G "
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|quotaUsage
operator|.
name|toString
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check the equality
annotation|@
name|Test
DECL|method|testCompareQuotaUsage ()
specifier|public
name|void
name|testCompareQuotaUsage
parameter_list|()
block|{
name|long
name|fileAndDirCount
init|=
literal|222255555
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
name|long
name|SSDspaceConsumed
init|=
literal|100000
decl_stmt|;
name|long
name|SSDQuota
init|=
literal|300000
decl_stmt|;
name|QuotaUsage
name|quotaUsage1
init|=
operator|new
name|QuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
name|fileAndDirCount
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
name|typeConsumed
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|,
name|SSDQuota
argument_list|)
operator|.
name|typeQuota
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|,
name|SSDQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|QuotaUsage
name|quotaUsage2
init|=
operator|new
name|QuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
name|fileAndDirCount
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
name|typeConsumed
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|,
name|SSDQuota
argument_list|)
operator|.
name|typeQuota
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|,
name|SSDQuota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|quotaUsage1
argument_list|,
name|quotaUsage2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

